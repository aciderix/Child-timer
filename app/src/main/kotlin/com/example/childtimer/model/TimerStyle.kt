package com.example.childtimer.model

import androidx.compose.ui.graphics.Color

enum class CadranDisplayMode {
    ANIMALS,
    NUMBERS
}

data class TimerThemeColor(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val softColor: Color,
    val lightColor: Color,
    val darkColor: Color
)

object TimerThemeColors {
    val all = listOf(
        TimerThemeColor(
            id = "turquoise",
            name = "Turquoise",
            primaryColor = Color(0xFF4AC4B5),
            softColor = Color(0xFF72D7CB),
            lightColor = Color(0xFFE4F7F5),
            darkColor = Color(0xFF38A89A)
        ),
        TimerThemeColor(
            id = "corail",
            name = "Corail Time Timer",
            primaryColor = Color(0xFFE85D56),
            softColor = Color(0xFFF28B82),
            lightColor = Color(0xFFFCE8E6),
            darkColor = Color(0xFFC53929)
        ),
        TimerThemeColor(
            id = "bleu",
            name = "Bleu Ciel",
            primaryColor = Color(0xFF4EA8DE),
            softColor = Color(0xFF90E0EF),
            lightColor = Color(0xFFE8F4F8),
            darkColor = Color(0xFF0077B6)
        ),
        TimerThemeColor(
            id = "vert",
            name = "Vert Menthe",
            primaryColor = Color(0xFF57CC99),
            softColor = Color(0xFF80ED99),
            lightColor = Color(0xFFEAF8F0),
            darkColor = Color(0xFF38A3A5)
        ),
        TimerThemeColor(
            id = "lavande",
            name = "Lavande",
            primaryColor = Color(0xFF9D80CB),
            softColor = Color(0xFFC7B8EA),
            lightColor = Color(0xFFF3EEFA),
            darkColor = Color(0xFF7451B0)
        ),
        TimerThemeColor(
            id = "peche",
            name = "Pêche Dorée",
            primaryColor = Color(0xFFF4A261),
            softColor = Color(0xFFF8C291),
            lightColor = Color(0xFFFDF0E5),
            darkColor = Color(0xFFE76F51)
        )
    )

    val default = all[0]
    fun forId(id: String): TimerThemeColor = all.find { it.id == id } ?: default
}
