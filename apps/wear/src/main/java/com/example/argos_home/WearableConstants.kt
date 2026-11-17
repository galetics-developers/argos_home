package com.example.argos_home

/** Data Layer contract shared by both modules; replicated by copy, both copies must stay identical. */

// MessageClient paths - Confirm Meal or Walk.
const val PATH_ACTIVITY_UPDATE = "/argos/activity/update" // Wear   -> Mobile
const val PATH_ACTIVITY_RESULT = "/argos/activity/result" // Mobile -> Wear

// "/argos/daily_status" stays reserved for the daily summary synchronization and is unused here.

// CapabilityClient names: must match res/values/wear.xml character by character; the compiler cannot catch it.
const val CAPABILITY_MOBILE_APP = "argos_home_mobile_app"
const val CAPABILITY_WEAR_APP = "argos_home_wear_app"
