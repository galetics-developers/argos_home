package com.example.argos_home.domain

/** Weight unit of a pet; the label is the short text shown next to the number. */
enum class WeightUnit(val label: String) {
    KG("kg"), G("g"), LB("lb")
}

/** Height unit of a pet; the label is the short text shown next to the number. */
enum class HeightUnit(val label: String) {
    CM("cm"), M("m"), IN("in")
}
