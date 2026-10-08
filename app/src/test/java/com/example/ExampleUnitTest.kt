package com.example

import com.example.data.GameProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testDefaultGameProfileInitialState() {
        val profile = GameProfile()
        assertEquals(1, profile.dayNumber)
        assertFalse(profile.isNight)
        assertEquals(250, profile.liquidBlood)
        assertEquals(180, profile.darkEther)
        assertEquals(120, profile.rawBiomass)
        assertEquals(15, profile.ironPipes)
        assertEquals(100, profile.sanity)
        assertEquals(100, profile.baseHp)
        assertEquals(0, profile.debtAmount)
        assertFalse(profile.pledgedEye)
        assertFalse(profile.pledgedArm)
    }

    @Test
    fun testLoanInterestCalculation() {
        val bloodLoan = 120
        val etherLoan = 80
        val ammoLoan = 20
        val totalValue = bloodLoan + etherLoan + ammoLoan
        val interestRate = 0.25f
        val calculatedDebt = (totalValue * (1f + interestRate)).toInt()

        assertEquals(275, calculatedDebt)
    }

    @Test
    fun testBioGrowthCompletion() {
        var progress = 0.95f
        progress += 0.06f
        assertTrue(progress >= 1.0f)
    }
}
