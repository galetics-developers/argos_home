package com.example.argos_home.presentation.wearable

import android.util.Log
import com.example.argos_home.CAPABILITY_MOBILE_APP
import com.google.android.gms.wearable.CapabilityClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

// CapabilityClient, not connectedNodes: a node can be connected without the app that understands this protocol.
// FILTER_REACHABLE then narrows the answer to the nodes that can actually receive right now.
/** Finds the node of the phone app the watch has to talk to. */
class PhoneNodeResolver(private val capabilityClient: CapabilityClient) {

    // A nearby node is preferred: it is reached over the direct link instead of a relayed route, cheaper in battery.
    /** Identifier of the best phone node available, or null when there is none. */
    suspend fun resolvePhoneNodeId(): String? {
        val nodes = try {
            capabilityClient
                .getCapability(CAPABILITY_MOBILE_APP, CapabilityClient.FILTER_REACHABLE)
                .await()
                .nodes
        } catch (error: CancellationException) {
            // The screen went away while the query was in flight: let the cancellation travel.
            throw error
        } catch (error: Exception) {
            Log.w(TAG, "Could not query the phone capability", error)
            return null
        }

        if (nodes.isEmpty()) {
            Log.w(TAG, "No reachable node advertises \"$CAPABILITY_MOBILE_APP\"")
            return null
        }

        val chosen = nodes.firstOrNull { it.isNearby } ?: nodes.first()
        Log.d(TAG, "Phone node resolved: ${chosen.displayName} (nearby=${chosen.isNearby})")
        return chosen.id
    }

    private companion object {
        const val TAG = "ArgosPhoneNode"
    }
}
