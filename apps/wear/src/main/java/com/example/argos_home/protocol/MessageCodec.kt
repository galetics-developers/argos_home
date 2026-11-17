package com.example.argos_home.protocol

import android.util.Log
import com.example.argos_home.domain.ActivityState
import org.json.JSONException
import org.json.JSONObject

/** JSON codec built on org.json, so no serialization dependency is needed; decoding returns null and never throws. */
object MessageCodec {

    private const val TAG = "ArgosMessageCodec"

    private const val KEY_ACTIVITY_ID = "activityId"
    private const val KEY_TARGET_STATE = "targetState"
    private const val KEY_REQUESTED_AT = "requestedAtMillis"
    private const val KEY_MEMBER_ID = "memberId"
    private const val KEY_ACCEPTED = "accepted"
    private const val KEY_RESULTING_STATE = "resultingState"
    private const val KEY_REASON = "reason"
    private const val KEY_ACTOR_NAME = "actorName"
    private const val KEY_RESOLVED_AT = "resolvedAtMillis"

    /** Serializes a watch request as the UTF-8 bytes expected by MessageClient. */
    fun encode(update: ActivityUpdate): ByteArray =
        JSONObject()
            .put(KEY_ACTIVITY_ID, update.activityId)
            .put(KEY_TARGET_STATE, update.targetState.name)
            .put(KEY_REQUESTED_AT, update.requestedAtMillis)
            .put(KEY_MEMBER_ID, update.memberId)
            .toString()
            .toByteArray(Charsets.UTF_8)

    /** Serializes a phone answer as the UTF-8 bytes expected by MessageClient. */
    fun encode(result: ActivityResult): ByteArray =
        JSONObject()
            .put(KEY_ACTIVITY_ID, result.activityId)
            .put(KEY_ACCEPTED, result.accepted)
            .put(KEY_RESULTING_STATE, result.resultingState.name)
            .put(KEY_REASON, result.reason?.name)
            .put(KEY_ACTOR_NAME, result.actorName)
            .put(KEY_RESOLVED_AT, result.resolvedAtMillis)
            .toString()
            .toByteArray(Charsets.UTF_8)

    /** Reads a watch request, or null if the payload cannot be trusted. */
    fun decodeUpdate(payload: ByteArray): ActivityUpdate? {
        val json = parse(payload) ?: return null
        val activityId = requiredString(json, KEY_ACTIVITY_ID) ?: return null
        val stateName = requiredString(json, KEY_TARGET_STATE) ?: return null
        val targetState = ActivityState.fromName(stateName)
        if (targetState == null) {
            Log.w(TAG, "Discarded update: unknown target state \"$stateName\"")
            return null
        }
        val requestedAtMillis = requiredLong(json, KEY_REQUESTED_AT) ?: return null
        val memberId = requiredString(json, KEY_MEMBER_ID) ?: return null
        return ActivityUpdate(activityId, targetState, requestedAtMillis, memberId)
    }

    /** Reads a phone answer, or null if the payload cannot be trusted. */
    fun decodeResult(payload: ByteArray): ActivityResult? {
        val json = parse(payload) ?: return null
        val activityId = requiredString(json, KEY_ACTIVITY_ID) ?: return null
        val accepted = requiredBoolean(json, KEY_ACCEPTED) ?: return null
        val stateName = requiredString(json, KEY_RESULTING_STATE) ?: return null
        val resultingState = ActivityState.fromName(stateName)
        if (resultingState == null) {
            Log.w(TAG, "Discarded result: unknown resulting state \"$stateName\"")
            return null
        }
        val reason = optionalString(json, KEY_REASON)?.let { name ->
            RejectionReason.fromName(name) ?: run {
                Log.w(TAG, "Discarded result: unknown rejection reason \"$name\"")
                return null
            }
        }
        return ActivityResult(
            activityId = activityId,
            accepted = accepted,
            resultingState = resultingState,
            reason = reason,
            actorName = optionalString(json, KEY_ACTOR_NAME),
            resolvedAtMillis = optionalLong(json, KEY_RESOLVED_AT)
        )
    }

    private fun parse(payload: ByteArray): JSONObject? =
        try {
            JSONObject(String(payload, Charsets.UTF_8))
        } catch (error: JSONException) {
            Log.w(TAG, "Discarded message: payload is not a valid JSON object", error)
            null
        } catch (error: Exception) {
            // Last line of defence: the bytes come from another device, so no unexpected failure may escape as a crash.
            Log.w(TAG, "Discarded message: payload could not be read", error)
            null
        }

    private fun requiredString(json: JSONObject, key: String): String? {
        if (isAbsent(json, key)) {
            Log.w(TAG, "Discarded message: missing field \"$key\"")
            return null
        }
        return json.optString(key)
    }

    private fun requiredLong(json: JSONObject, key: String): Long? {
        if (isAbsent(json, key)) {
            Log.w(TAG, "Discarded message: missing field \"$key\"")
            return null
        }
        return readLong(json, key)
    }

    private fun requiredBoolean(json: JSONObject, key: String): Boolean? {
        if (isAbsent(json, key)) {
            Log.w(TAG, "Discarded message: missing field \"$key\"")
            return null
        }
        return try {
            json.getBoolean(key)
        } catch (error: JSONException) {
            Log.w(TAG, "Discarded message: field \"$key\" is not a boolean", error)
            null
        }
    }

    private fun optionalString(json: JSONObject, key: String): String? =
        if (isAbsent(json, key)) null else json.optString(key)

    private fun optionalLong(json: JSONObject, key: String): Long? =
        if (isAbsent(json, key)) null else readLong(json, key)

    private fun readLong(json: JSONObject, key: String): Long? =
        try {
            json.getLong(key)
        } catch (error: JSONException) {
            Log.w(TAG, "Discarded message: field \"$key\" is not a number", error)
            null
        }

    /** True when the key is missing or holds JSONObject.NULL, which would otherwise read back as the text "null". */
    private fun isAbsent(json: JSONObject, key: String): Boolean = !json.has(key) || json.isNull(key)
}
