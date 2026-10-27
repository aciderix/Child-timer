package com.example.childtimer.model

import androidx.annotation.DrawableRes
import com.example.childtimer.R

data class CadranAnimal(
    val position: Int, // 1 to 12 (corresponding to clock hours)
    val minutes: Int, // 5 to 60 minutes
    val name: String,
    @DrawableRes val drawableRes: Int
) {
    // Angle in degrees from the top (12 o'clock, 0° = 12h, 30° = 1h, 90° = 3h, etc.)
    val clockAngleDeg: Float
        get() = (position % 12) * 30f

    // Total sweep angle of the turquoise sector starting from 12h clockwise
    val sweepAngleDeg: Float
        get() = position * 30f
}

object CadranAnimals {
    val list: List<CadranAnimal> = listOf(
        CadranAnimal(position = 1, minutes = 5, name = "Ours", drawableRes = R.drawable.ours),
        CadranAnimal(position = 2, minutes = 10, name = "Hibou", drawableRes = R.drawable.hibou),
        CadranAnimal(position = 3, minutes = 15, name = "Lapin", drawableRes = R.drawable.lapin),
        CadranAnimal(position = 4, minutes = 20, name = "Renard", drawableRes = R.drawable.renard),
        CadranAnimal(position = 5, minutes = 25, name = "Croco", drawableRes = R.drawable.croco),
        CadranAnimal(position = 6, minutes = 30, name = "Zèbre", drawableRes = R.drawable.zebre),
        CadranAnimal(position = 7, minutes = 35, name = "Singe", drawableRes = R.drawable.singe),
        CadranAnimal(position = 8, minutes = 40, name = "Éléphant", drawableRes = R.drawable.elephant),
        CadranAnimal(position = 9, minutes = 45, name = "Girafe", drawableRes = R.drawable.girafe),
        CadranAnimal(position = 10, minutes = 50, name = "Écureuil", drawableRes = R.drawable.ecureuil),
        CadranAnimal(position = 11, minutes = 55, name = "Panda", drawableRes = R.drawable.panda),
        CadranAnimal(position = 12, minutes = 60, name = "Lion", drawableRes = R.drawable.lion)
    )

    fun forMinutes(minutes: Int): CadranAnimal? = list.find { it.minutes == minutes }
    fun forPosition(position: Int): CadranAnimal? = list.find { it.position == position }
}
