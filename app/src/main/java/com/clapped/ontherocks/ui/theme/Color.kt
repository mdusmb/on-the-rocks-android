package com.clapped.ontherocks.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

var AppLightMode by mutableStateOf(false)

val AppBackground: Color get() = if (AppLightMode) Color(0xFFF4F0E8) else Color(0xFF101012)
val AppSurface: Color get() = if (AppLightMode) Color(0xFFFFFFFF) else Color(0xFF1B1B21)
val AppActiveSurface: Color get() = if (AppLightMode) Color(0xFFECE4D7) else Color(0xFF25211B)
val AppText: Color get() = if (AppLightMode) Color(0xFF171614) else Color(0xFFF6F1EA)
val AppMuted: Color get() = if (AppLightMode) Color(0xFF6E6862) else Color(0xFFA8A3AA)
val AppGold = Color(0xFFD7B56D)
val AppReady = Color(0xFF62B977)
