package com.example.childtimer.ui.main

import com.example.childtimer.model.CadranAnimals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CadranAnimalsTest {

    @Test
    fun cadranAnimals_hasExactlyTwelveAnimals() {
        assertEquals(12, CadranAnimals.list.size)
    }

    @Test
    fun cadranAnimals_durationsSpanFrom5To60Minutes() {
        CadranAnimals.list.forEachIndexed { index, animal ->
            val expectedPosition = index + 1
            val expectedMinutes = expectedPosition * 5
            val expectedSweep = expectedPosition * 30f

            assertEquals(expectedPosition, animal.position)
            assertEquals(expectedMinutes, animal.minutes)
            assertEquals(expectedSweep, animal.sweepAngleDeg, 0.01f)
        }
    }

    @Test
    fun cadranAnimals_firstIsOursAndTwelfthIsLion() {
        val first = CadranAnimals.forMinutes(5)
        val twelfth = CadranAnimals.forMinutes(60)

        assertNotNull(first)
        assertEquals("Ours", first?.name)
        assertEquals(30f, first?.sweepAngleDeg)

        assertNotNull(twelfth)
        assertEquals("Lion", twelfth?.name)
        assertEquals(360f, twelfth?.sweepAngleDeg)
    }

    @Test
    fun cadranAnimals_lookupByMinutesWorks() {
        val quarter = CadranAnimals.forMinutes(15)
        assertNotNull(quarter)
        assertEquals("Lapin", quarter?.name)
        assertEquals(90f, quarter?.sweepAngleDeg)

        val half = CadranAnimals.forMinutes(30)
        assertNotNull(half)
        assertEquals("Zèbre", half?.name)
        assertEquals(180f, half?.sweepAngleDeg)
    }
}
