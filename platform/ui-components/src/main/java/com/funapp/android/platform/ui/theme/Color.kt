package com.funapp.android.platform.ui.theme

import androidx.compose.ui.graphics.Color

// Primary blue theme colors
val Blue80 = Color(0xFFA8C8FF)
val BlueGrey80 = Color(0xFFBCC7DC)
val LightBlue80 = Color(0xFFB8D4FF)

val Blue40 = Color(0xFF007AFF)
val BlueGrey40 = Color(0xFF535F70)
val LightBlue40 = Color(0xFF0056B3)

// Item colors matching iOS named colors
val ItemColors = mapOf(
    "green" to Color(0xFF34C759),
    "orange" to Color(0xFFFF9500),
    "blue" to Color(0xFF007AFF),
    "purple" to Color(0xFFAF52DE),
    "indigo" to Color(0xFF5856D6),
    "brown" to Color(0xFFA2845E),
    "teal" to Color(0xFF5AC8FA),
    "mint" to Color(0xFF00C7BE),
    "cyan" to Color(0xFF32ADE6),
    "gray" to Color(0xFF8E8E93),
    "red" to Color(0xFFFF3B30),
    "pink" to Color(0xFFFF2D55),
    "yellow" to Color(0xFFFFCC00)
)

fun itemColor(name: String): Color = ItemColors[name.lowercase()] ?: Color(0xFF007AFF)
