package com.example.argos_home.wearable

import android.util.Log
import com.example.argos_home.PATH_ACTIVITY_RESULT
import com.example.argos_home.protocol.ActivityResult
import com.example.argos_home.protocol.MessageCodec
import com.google.android.gms.wearable.MessageClient
import kotlinx.coroutines.tasks.await

// The phone replies to messageEvent.sourceNodeId, so it needs no CapabilityClient lookup to find the watch.
/** Sends the decision of the phone back to the watch over the Data Layer. */
class ActivityResultSender(private val messageClient: MessageClient) {

    /** Suspends until the Data Layer takes the answer; cancelled with the lifecycle scope that called it. */
    suspend fun send(nodeId: String, result: ActivityResult) {
        try {
            messageClient
                .sendMessage(nodeId, PATH_ACTIVITY_RESULT, MessageCodec.encode(result))
                .await()
            Log.d(TAG, "Answered ${result.activityId} with accepted=${result.accepted}")
        } catch (error: Exception) {
            // A watch out of range is expected, not a defect: the phone keeps the authoritative state.
            Log.w(TAG, "Could not answer ${result.activityId} to node $nodeId", error)
        }
    }

    private companion object {
        const val TAG = "ArgosResultSender"
    }
}
