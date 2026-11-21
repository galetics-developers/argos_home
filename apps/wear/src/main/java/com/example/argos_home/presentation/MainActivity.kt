package com.example.argos_home.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.argos_home.PATH_ACTIVITY_RESULT
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.presentation.home.HomeStore
import com.example.argos_home.presentation.theme.Argos_homeTheme
import com.example.argos_home.presentation.wearable.ActivityUpdateSender
import com.example.argos_home.presentation.wearable.PhoneNodeResolver
import com.example.argos_home.protocol.MessageCodec
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch

// No WearableListenerService on purpose: a service wakes for every message, and nothing is shown while unwatched.
/** Watch host and Data Layer listener; a tap only asks, and nothing changes here until the phone answers. */
class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    private lateinit var messageClient: MessageClient
    private lateinit var updateSender: ActivityUpdateSender

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        messageClient = Wearable.getMessageClient(this)
        updateSender = ActivityUpdateSender(
            messageClient = messageClient,
            nodeResolver = PhoneNodeResolver(
                capabilityClient = Wearable.getCapabilityClient(this),
                nodeClient = Wearable.getNodeClient(this)
            )
        )

        setContent {
            Argos_homeTheme {
                WearApp(onRequestTransition = ::requestTransition)
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

    /** Entry point for phone answers: the path is matched before decoding and any unrecognised one is discarded. */
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path != PATH_ACTIVITY_RESULT) {
            Log.w(TAG, "Ignored message on unknown path \"${messageEvent.path}\"")
            return
        }

        val result = MessageCodec.decodeResult(messageEvent.data)
        if (result == null) {
            // Bytes from another device are untrusted: the codec returns null and already logged the reason.
            return
        }

        // The callback arrives on a Data Layer background thread, so state is mutated inside lifecycleScope.
        lifecycleScope.launch {
            HomeStore.applyResult(result)
        }
    }

    /** Asks the phone for the tapped transition; beginSend opens the guard the send callback checks afterwards. */
    private fun requestTransition(activityId: String, targetState: ActivityState) {
        if (!HomeStore.beginSend(activityId, targetState)) {
            return
        }

        lifecycleScope.launch {
            val failure = updateSender.send(activityId, targetState)
            if (failure == null) {
                HomeStore.markSendDelivered(activityId)
            } else {
                // Nothing was decided: the card keeps its state and the failure is reported in words with a retry.
                HomeStore.reportFailure(failure)
            }
        }
    }

    private companion object {
        const val TAG = "ArgosWearMainActivity"
    }
}
