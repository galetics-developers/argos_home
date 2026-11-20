package com.example.argos_home.domain

import java.time.LocalTime

/** A time range is valid when the end is strictly after the start on the same day; an open end (null) is allowed. */
fun isValidTimeRange(start: LocalTime?, end: LocalTime?): Boolean =
    start == null || end == null || end.isAfter(start)
