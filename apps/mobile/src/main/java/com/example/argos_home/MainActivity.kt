package com.example.argos_home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.argos_home.data.ActivityRepository
import com.example.argos_home.protocol.MessageCodec
import com.example.argos_home.ui.calendar.CalendarScreen
import com.example.argos_home.ui.theme.Argos_homeTheme
import com.example.argos_home.wearable.ActivityResultSender
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch

// No WearableListenerService on purpose: a service wakes for every message, and only a visible day has an answer.
/** Phone host and Data Layer listener; the phone is the authority that decides every transition. */
class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    private lateinit var messageClient: MessageClient
    private lateinit var resultSender: ActivityResultSender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        messageClient = Wearable.getMessageClient(this)
        resultSender = ActivityResultSender(messageClient)
        setContent {
            Argos_homeTheme {
                CalendarScreen()
            }
        }
    }

    /** Registered in onResume and removed in onPause: the practice recommended for battery in interactive apps. */
    override fun onResume() {
        super.onResume()
        messageClient.addListener(this)
    }

    /** Unregistered with the screen, so the callback never outlives the state it mutates. */
    override fun onPause() {
        super.onPause()
        messageClient.removeListener(this)
    }

    /** Entry point for watch messages: the path is matched before decoding and any unrecognised one is discarded. */
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path != PATH_ACTIVITY_UPDATE) {
            Log.w(TAG, "Ignored message on unknown path \"${messageEvent.path}\"")
            return
        }

        val update = MessageCodec.decodeUpdate(messageEvent.data)
        if (update == null) {
            // Bytes from another device are untrusted: the codec returns null and already logged the reason.
            return
        }

        // The asking node travels in the message, so the phone never has to look the watch up to answer it.
        val sourceNodeId = messageEvent.sourceNodeId

        // The callback arrives on a Data Layer background thread, so state is mutated inside lifecycleScope.
        lifecycleScope.launch {
            val result = ActivityRepository.applyTransition(
                id = update.activityId,
                target = update.targetState,
                resolvedAtMillis = System.currentTimeMillis()
            )
            resultSender.send(sourceNodeId, result)
        }
    }

    private companion object {
        const val TAG = "ArgosMainActivity"
    }
}
