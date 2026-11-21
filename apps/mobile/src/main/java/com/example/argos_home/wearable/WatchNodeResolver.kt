package com.example.argos_home.wearable

import android.util.Log
import com.example.argos_home.CAPABILITY_WEAR_APP
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.NodeClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

// Capability is the right way to find the watch, but a node can be connected before its capability item reaches us.
// So discovery prefers the capability and falls back to the connected nodes, which do not depend on that propagation.
/** Finds the watch nodes the phone has to push a state change to. */
class WatchNodeResolver(
    private val capabilityClient: CapabilityClient,
    private val nodeClient: NodeClient
) {

    /** Ids of the watch nodes to notify: those advertising the watch app, or every connected node as a fallback. */
    suspend fun resolveWatchNodeIds(): List<String> {
        val byCapability = capabilityNodeIds()
        if (byCapability.isNotEmpty()) return byCapability
        return connectedNodeIds()
    }

    /** Reachable nodes that advertise the watch capability; empty on any non-cancellation failure. */
    private suspend fun capabilityNodeIds(): List<String> = try {
        capabilityClient
            .getCapability(CAPABILITY_WEAR_APP, CapabilityClient.FILTER_REACHABLE)
            .await()
            .nodes
            .map { it.id }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Log.w(TAG, "Could not query the watch capability", error)
        emptyList()
    }

    /** Every node connected to the phone, regardless of capability; empty on any non-cancellation failure. */
    private suspend fun connectedNodeIds(): List<String> = try {
        nodeClient.connectedNodes
            .await()
            .map { it.id }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Log.w(TAG, "Could not query the connected nodes", error)
        emptyList()
    }

    private companion object {
        const val TAG = "ArgosWatchNode"
    }
}
