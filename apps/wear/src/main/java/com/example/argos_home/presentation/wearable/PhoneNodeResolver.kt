package com.example.argos_home.presentation.wearable

import android.util.Log
import com.example.argos_home.CAPABILITY_MOBILE_APP
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.NodeClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

// Capability is the right way to find the phone, but a node can be connected before its capability item reaches us.
// So discovery prefers the capability and falls back to the connected nodes, which do not depend on that propagation.
/** Finds the node of the phone app the watch has to talk to. */
class PhoneNodeResolver(
    private val capabilityClient: CapabilityClient,
    private val nodeClient: NodeClient
) {

    /** Identifier of the best phone node available, or null when there is none. */
    suspend fun resolvePhoneNodeId(): String? {
        // Preferred: nodes that advertise the phone app and can receive right now.
        pickPreferredNode(capabilityNodes())?.let { nodeId ->
            Log.d(TAG, "Phone node resolved by capability: $nodeId")
            return nodeId
        }

        // Fallback: a connected node whose capability item may not have propagated to the watch yet.
        pickPreferredNode(connectedNodes())?.let { nodeId ->
            Log.d(TAG, "Phone node resolved by connected node: $nodeId")
            return nodeId
        }

        Log.w(TAG, "No node advertises \"$CAPABILITY_MOBILE_APP\" and no node is connected")
        return null
    }

    /** Reachable nodes that advertise the phone capability; empty on any non-cancellation failure. */
    private suspend fun capabilityNodes(): List<NodeChoice> = try {
        capabilityClient
            .getCapability(CAPABILITY_MOBILE_APP, CapabilityClient.FILTER_REACHABLE)
            .await()
            .nodes
            .map { NodeChoice(it.id, it.isNearby) }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Log.w(TAG, "Could not query the phone capability", error)
        emptyList()
    }

    /** Every node connected to the watch, regardless of capability; empty on any non-cancellation failure. */
    private suspend fun connectedNodes(): List<NodeChoice> = try {
        nodeClient.connectedNodes
            .await()
            .map { NodeChoice(it.id, it.isNearby) }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Log.w(TAG, "Could not query the connected nodes", error)
        emptyList()
    }

    private companion object {
        const val TAG = "ArgosPhoneNode"
    }
}

/** A node candidate reduced to what the choice needs, so the rule stays testable without Data Layer types. */
internal data class NodeChoice(val id: String, val isNearby: Boolean)

// A nearby node is reached over the direct link instead of a relayed route, so it is cheaper in battery.
/** Picks the best node id: a nearby one first, then any, then null when the list is empty. */
internal fun pickPreferredNode(candidates: List<NodeChoice>): String? =
    (candidates.firstOrNull { it.isNearby } ?: candidates.firstOrNull())?.id
