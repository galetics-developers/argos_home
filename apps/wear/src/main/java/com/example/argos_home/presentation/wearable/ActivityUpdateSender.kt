package com.example.argos_home.presentation.wearable

import android.util.Log
import com.example.argos_home.PATH_ACTIVITY_UPDATE
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.SeedData
import com.example.argos_home.protocol.ActivityUpdate
import com.example.argos_home.protocol.MessageCodec
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.WearableStatusCodes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/** Why a request could not leave the watch. Every value has to be readable as plain words. */
enum class SendFailure {

    /** No reachable node advertises the phone app. */
    NO_PHONE_NODE,

    /** A node was found but the link dropped before the request could be handed over. */
    PHONE_NOT_CONNECTED,

    /** The send task failed for any other reason. */
    SEND_FAILED
}

/** Sends the tapped transition to the phone; the request only states a target, the answer arrives separately. */
class ActivityUpdateSender(
    private val messageClient: MessageClient,
    private val nodeResolver: PhoneNodeResolver
) {

    // sendMessage is best effort with no built-in retry, so TARGET_NODE_NOT_CONNECTED is told apart from the rest.
    // It means the phone moved out of range, not that something is broken: the user taps again back in range.
    /** Suspends until the Data Layer takes the request; answers null on success or the failure, never throwing. */
    suspend fun send(activityId: String, targetState: ActivityState): SendFailure? {
        val nodeId = nodeResolver.resolvePhoneNodeId() ?: return SendFailure.NO_PHONE_NODE

        val update = ActivityUpdate(
            activityId = activityId,
            targetState = targetState,
            requestedAtMillis = System.currentTimeMillis(),
            // With no login, every action is attributed to the local member.
            memberId = SeedData.localMember.id
        )

        return try {
            messageClient
                .sendMessage(nodeId, PATH_ACTIVITY_UPDATE, MessageCodec.encode(update))
                .await()
            Log.d(TAG, "Requested $targetState for $activityId on node $nodeId")
            null
        } catch (error: CancellationException) {
            // The screen went away while the send was in flight: let the cancellation travel.
            throw error
        } catch (error: ApiException) {
            if (error.statusCode == WearableStatusCodes.TARGET_NODE_NOT_CONNECTED) {
                Log.w(TAG, "Node $nodeId is no longer connected", error)
                SendFailure.PHONE_NOT_CONNECTED
            } else {
                Log.w(TAG, "Could not send $targetState for $activityId", error)
                SendFailure.SEND_FAILED
            }
        } catch (error: Exception) {
            Log.w(TAG, "Could not send $targetState for $activityId", error)
            SendFailure.SEND_FAILED
        }
    }

    private companion object {
        const val TAG = "ArgosUpdateSender"
    }
}
