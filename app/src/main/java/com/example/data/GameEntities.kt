package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_profile")
data class GameProfile(
    @PrimaryKey val id: Int = 1,
    val dayNumber: Int = 1,
    val timeHour: Float = 8.0f, // 06:00 - 18:00 Day, 18:00 - 06:00 Night
    val isNight: Boolean = false,
    
    // Resources
    val liquidBlood: Int = 250,
    val darkEther: Int = 180,
    val rawBiomass: Int = 120,
    val ironPipes: Int = 15,
    val ammo: Int = 30,
    
    // Player Vitals
    val sanity: Int = 100, // 0 - 100%
    val baseHp: Int = 100,
    
    // Oliy Zom (Necro-Finance & Debt Horror)
    val debtAmount: Int = 0,
    val debtDueNight: Int = 3,
    val debtInterestRate: Float = 0.25f, // 25% daily interest
    val pledgedEye: Boolean = false, // Loss of depth & narrowed vision debuff
    val pledgedArm: Boolean = false, // Slower reload and gun stabilization debuff
    val pledgedSoul: Boolean = false, // Accelerates sanity drain
    
    // Blood Con & Base Upgrades
    val turretCount: Int = 1,
    val turretLevel: Int = 1,
    val pipeNetworkLevel: Int = 1,
    val barrierDurability: Int = 100,
    
    // Bio-Growth Pod (Organ Regeneration)
    val bioPodActive: Boolean = false,
    val bioPodProgress: Float = 0f, // 0.0 to 1.0
    val regeneratingPart: String = "NONE", // "NONE", "EYE", "ARM", "SOUL"
    
    // Radio Occult
    val tunedFrequency: Float = 94.2f,
    val interceptedSignalsCount: Int = 0,
    
    // Game History
    val highestNightsSurvived: Int = 1,
    val totalJinnSlain: Int = 0,
    val language: String = "uz", // "uz", "en", "ru", "tr"
    
    // World Scale & Signal Beacon
    val beaconLevel: Int = 1, // Level 1 (2.5 km) -> Level 4 (10.0+ km, 100 km²)
    val unlockedSectors: String = "HUB,FOREST"
)

@Entity(tableName = "codex_entries")
data class CodexEntry(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "JINN", "BLOOD_CON", "NECRO_FINANCE", "ANOMALY"
    val summary: String,
    val detailedLore: String,
    val searchGroundedAnswer: String? = null,
    val groundingSource: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
