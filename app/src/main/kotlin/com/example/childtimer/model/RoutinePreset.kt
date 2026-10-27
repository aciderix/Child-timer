package com.example.childtimer.model

data class RoutinePreset(
    val id: String,
    val title: String,
    val frenchTitle: String,
    val durationMinutes: Int,
    val emoji: String,
    val description: String,
    val recommendedAnimalId: String
)

object RoutinePresets {
    val all: List<RoutinePreset> = listOf(
        RoutinePreset(
            id = "teeth",
            title = "Brush Teeth",
            frenchTitle = "Brossage des dents",
            durationMinutes = 2,
            emoji = "🪥",
            description = "Brush front, back, top and bottom!",
            recommendedAnimalId = "croco"
        ),
        RoutinePreset(
            id = "tidy",
            title = "Tidy Up Toys",
            frenchTitle = "Ranger la chambre",
            durationMinutes = 5,
            emoji = "🧸",
            description = "Put building blocks, dolls & cars away!",
            recommendedAnimalId = "ours"
        ),
        RoutinePreset(
            id = "reading",
            title = "Story Time",
            frenchTitle = "Lecture & Histoire",
            durationMinutes = 10,
            emoji = "📖",
            description = "Flip pages and travel through exciting tales!",
            recommendedAnimalId = "hibou"
        ),
        RoutinePreset(
            id = "craft",
            title = "Drawing & Craft",
            frenchTitle = "Dessin & Activité",
            durationMinutes = 15,
            emoji = "🎨",
            description = "Color shapes, fold paper, or make pottery!",
            recommendedAnimalId = "renard"
        ),
        RoutinePreset(
            id = "bath",
            title = "Bath & Bubbles",
            frenchTitle = "L'heure du bain",
            durationMinutes = 20,
            emoji = "🛁",
            description = "Splash gently and enjoy the warm bubbles!",
            recommendedAnimalId = "elephant"
        ),
        RoutinePreset(
            id = "quiet",
            title = "Quiet Nap / Rest",
            frenchTitle = "Temps calme & Sieste",
            durationMinutes = 30,
            emoji = "💤",
            description = "Rest your eyes and let your mind float gently.",
            recommendedAnimalId = "panda"
        )
    )

    val default: RoutinePreset = all[0] // Brush Teeth (2 min)
}
