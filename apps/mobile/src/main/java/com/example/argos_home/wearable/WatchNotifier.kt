package com.example.argos_home.wearable

import android.content.Context
import com.example.argos_home.protocol.ActivityResult
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// The phone owns the state, so a change made on its own screen must be pushed to the watch, mirroring the reply it
// already sends for watch-initiated changes; without this the watch keeps a stale card and its action buttons.
/** Pushes phone-originated state changes to the watch over the Data Layer, so neither device drifts. */
object WatchNotifier {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var resolver: WatchNodeResolver? = null
    private var sender: ActivityResultSender? = null

    /** Wires the notifier to the Data Layer; called once with the application context. */
    fun attach(context: Context) {
        val app = context.applicationContext
        sender = ActivityResultSender(Wearable.getMessageClient(app))
        resolver = WatchNodeResolver(
            capabilityClient = Wearable.getCapabilityClient(app),
            nodeClient = Wearable.getNodeClient(app)
        )
    }

    // A rejection changed nothing on the phone, so only an accepted transition is worth pushing; delivery is best effort.
    /** Sends the authoritative state to the watch after a change made on the phone. */
    fun notifyStateChanged(result: ActivityResult) {
        if (!result.accepted) return
        val watchResolver = resolver ?: return
        val resultSender = sender ?: return
        scope.launch {
            watchResolver.resolveWatchNodeIds().forEach { nodeId ->
                resultSender.send(nodeId, result)
            }
        }
    }
}
