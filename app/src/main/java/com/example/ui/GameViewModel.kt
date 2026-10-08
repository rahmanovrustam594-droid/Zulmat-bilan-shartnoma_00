package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CodexEntry
import com.example.data.GameProfile
import com.example.data.GameRepository
import com.example.data.GroundedLoreResult
import com.example.data.OccultCodexService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameScreen {
    MAIN_MENU,
    DAY_CAMP,
    NIGHT_DEFENSE,
    CODEX_ORACLE,
    SETTINGS
}

data class DemonTarget(
    val id: Long,
    val type: String, // "SHADOW_JINN", "IFRIT", "COLLECTOR"
    val x: Float = 0.5f,
    val y: Float = 0.5f,
    val worldX: Float = 0f, // -14f to +14f in 3D space
    val worldZ: Float = 35f, // 35 meters down to 2 meters at barricade
    var hp: Int,
    val maxHp: Int,
    val speed: Float = 1.6f, // meters per second
    var isFrozen: Boolean = false
)

data class NightBattleState(
    val timeLeftSeconds: Int = 40,
    val isRunning: Boolean = false,
    val demons: List<DemonTarget> = emptyList(),
    val magazineAmmo: Int = 6,
    val maxMagazine: Int = 6,
    val isReloading: Boolean = false,
    val cameraYaw: Float = 0f, // 3D Camera Look Yaw (-60° to +60°)
    val cameraPitch: Float = 0f, // 3D Camera Look Pitch (-25° to +25°)
    val slainCount: Int = 0,
    val damageTaken: Int = 0,
    val freezeTimerSeconds: Int = 0,
    val muzzleFlash: Boolean = false,
    val lastHitPosition: Pair<Float, Float>? = null,
    val bloodTurretActive: Boolean = true,
    val battleResult: BattleResult? = null
)

enum class BattleResult {
    VICTORY,
    DEFEAT
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val occultService = OccultCodexService()
    val repository = GameRepository(database.gameDao(), occultService)
    val audioHaptics = AudioHapticHelper(application)

    val gameProfile: StateFlow<GameProfile?> = repository.gameProfileFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val codexEntries: StateFlow<List<CodexEntry>> = repository.codexEntriesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _currentScreen = MutableStateFlow(GameScreen.MAIN_MENU)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

    private val _activeTab = MutableStateFlow("SCAVENGE")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    private val _nightBattle = MutableStateFlow(NightBattleState())
    val nightBattle: StateFlow<NightBattleState> = _nightBattle.asStateFlow()

    private val _systemMessage = MutableStateFlow<String?>(null)
    val systemMessage: StateFlow<String?> = _systemMessage.asStateFlow()

    private val _codexLoading = MutableStateFlow(false)
    val codexLoading: StateFlow<Boolean> = _codexLoading.asStateFlow()

    private val _lastCodexResult = MutableStateFlow<GroundedLoreResult?>(null)
    val lastCodexResult: StateFlow<GroundedLoreResult?> = _lastCodexResult.asStateFlow()

    private var battleJob: Job? = null

    init {
        viewModelScope.launch {
            repository.getOrCreateProfile()
        }
    }

    fun navigateTo(screen: GameScreen) {
        _currentScreen.value = screen
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            val current = repository.getOrCreateProfile()
            repository.updateProfile(current.copy(language = lang))
        }
    }

    fun showMessage(msg: String) {
        _systemMessage.value = msg
        viewModelScope.launch {
            delay(3500)
            if (_systemMessage.value == msg) {
                _systemMessage.value = null
            }
        }
    }

    // --- DAY PHASE ACTIONS ---

    fun scavenge() {
        viewModelScope.launch {
            audioHaptics.playRadioStatic()
            val msg = repository.scavengeExpedition("Vayrona Skanerlash")
            showMessage(msg)
        }
    }

    fun exploreSector(sectorId: String) {
        viewModelScope.launch {
            audioHaptics.playRadioStatic()
            val msg = repository.exploreSpecificSector(sectorId)
            showMessage(msg)
        }
    }

    fun upgradeBeacon() {
        viewModelScope.launch {
            audioHaptics.playAlarm()
            val success = repository.upgradeBeacon()
            if (success) {
                showMessage("Signal Minorasi quvvatlandi! 'Sferik Nurlanish' taraldi — yangi xarita zonalari ochildi!")
            } else {
                showMessage("Minorani kuchaytirish uchun Qora Eter va Quvurlar yetarli emas!")
            }
        }
    }

    fun borrowLoan(type: String, collateral: String) {
        viewModelScope.launch {
            audioHaptics.playCoinStamp()
            when (type) {
                "SMALL" -> repository.borrowFromOliyZom(120, 80, 20, collateral)
                "MEDIUM" -> repository.borrowFromOliyZom(300, 200, 45, collateral)
                "MASSIVE" -> repository.borrowFromOliyZom(700, 450, 90, collateral)
            }
            val collMsg = if (collateral != "NONE") " (Garov: $collateral berildi)" else ""
            showMessage("Oliy Zom bilan qonli shartnoma imzolandi!$collMsg")
        }
    }

    fun repayDebt(amount: Int) {
        viewModelScope.launch {
            audioHaptics.playCoinStamp()
            val success = repository.repayDebt(amount)
            if (success) {
                showMessage("Oliy Zom qarzi yopildi! ($amount Eter/Qon)")
            } else {
                showMessage("Qarz toʻlash uchun resurs yetarli emas!")
            }
        }
    }

    fun startBioPod(part: String) {
        viewModelScope.launch {
            audioHaptics.playRitualCast()
            val success = repository.startBioPodRegeneration(part)
            if (success) {
                showMessage("Bio-Growth Pod ishga tushdi: $part toʻqimalari tiklanmoqda...")
            } else {
                showMessage("Organ oʻstirish uchun kamida 80 Biomassa va 100 Qon kerak!")
            }
        }
    }

    fun craftUpgrade(type: String) {
        viewModelScope.launch {
            audioHaptics.playGunshot()
            val success = repository.craftUpgrade(type)
            if (success) {
                showMessage("Blood Con muhandisligi: $type muvaffaqiyatli qurildi/kuchaytirildi!")
            } else {
                showMessage("Resurslar yetarli emas!")
            }
        }
    }

    fun tuneRadioFrequency(freq: Float) {
        viewModelScope.launch {
            val intercepted = repository.tuneRadio(freq)
            if (intercepted != null) {
                audioHaptics.playAlarm()
                showMessage(intercepted)
            } else {
                audioHaptics.playRadioStatic()
            }
        }
    }

    fun queryCodexAI(query: String, category: String) {
        viewModelScope.launch {
            _codexLoading.value = true
            audioHaptics.playRitualCast()
            try {
                val res = repository.queryCodex(query, category)
                _lastCodexResult.value = res
            } finally {
                _codexLoading.value = false
            }
        }
    }

    // --- NIGHT BATTLE & DEFENSE LOOP ---

    fun rotateCamera(deltaYaw: Float, deltaPitch: Float) {
        val current = _nightBattle.value
        val newYaw = (current.cameraYaw + deltaYaw).coerceIn(-60f, 60f)
        val newPitch = (current.cameraPitch + deltaPitch).coerceIn(-25f, 25f)
        _nightBattle.value = current.copy(cameraYaw = newYaw, cameraPitch = newPitch)
    }

    fun startNightDefense() {
        val profile = gameProfile.value ?: return
        _currentScreen.value = GameScreen.NIGHT_DEFENSE
        _nightBattle.value = NightBattleState(
            timeLeftSeconds = 35 + (profile.dayNumber * 5),
            isRunning = true,
            magazineAmmo = 6,
            demons = generateInitialDemons(profile.dayNumber, profile.debtAmount > 0)
        )
        audioHaptics.playAlarm()

        battleJob?.cancel()
        battleJob = viewModelScope.launch {
            while (_nightBattle.value.isRunning && _nightBattle.value.timeLeftSeconds > 0) {
                delay(1000)
                val current = _nightBattle.value
                val profileNow = gameProfile.value ?: break

                // Check Bio-Pod progress in the background during battle
                repository.advanceBioPodTick(0.04f)

                // Turret auto-fire (consumes liquid blood)
                var newDemons = current.demons.map { it.copy() }.toMutableList()
                if (current.bloodTurretActive && profileNow.liquidBlood >= 4 && newDemons.isNotEmpty()) {
                    val target = newDemons.minByOrNull { it.worldZ } ?: newDemons.first()
                    target.hp -= (15 * profileNow.turretLevel)
                    if (target.hp <= 0) {
                        newDemons.remove(target)
                        audioHaptics.playGunshot()
                    }
                }

                // Spawn new demons in 3D distance (worldZ: 35m to 42m)
                if (newDemons.size < 7 && Random.nextFloat() < 0.65f) {
                    val isCollector = profileNow.debtAmount > 0 && Random.nextFloat() < 0.4f
                    val demonType = if (isCollector) "COLLECTOR" else if (Random.nextBoolean()) "IFRIT" else "SHADOW_JINN"
                    val hp = when (demonType) {
                        "COLLECTOR" -> 60 + profileNow.dayNumber * 10
                        "IFRIT" -> 45 + profileNow.dayNumber * 8
                        else -> 30 + profileNow.dayNumber * 5
                    }
                    newDemons.add(
                        DemonTarget(
                            id = System.nanoTime(),
                            type = demonType,
                            worldX = Random.nextFloat() * 20f - 10f, // -10m to +10m
                            worldZ = Random.nextFloat() * 8f + 32f, // 32m to 40m away
                            hp = hp,
                            maxHp = hp,
                            speed = if (demonType == "COLLECTOR") 2.4f else 1.8f
                        )
                    )
                }

                // Move demons in 3D toward barricade (worldZ decreasing from 35m -> 2m)
                var damageThisTick = 0
                val survivingDemons = mutableListOf<DemonTarget>()
                for (demon in newDemons) {
                    if (current.freezeTimerSeconds <= 0) {
                        val newZ = demon.worldZ - demon.speed
                        if (newZ <= 2.2f) {
                            // Hit barrier/base!
                            damageThisTick += when (demon.type) {
                                "COLLECTOR" -> 14
                                "IFRIT" -> 10
                                else -> 6
                            }
                            audioHaptics.playDemonRoar()
                        } else {
                            survivingDemons.add(demon.copy(worldZ = newZ))
                        }
                    } else {
                        survivingDemons.add(demon)
                    }
                }

                val newHp = maxOf(0, profileNow.baseHp - damageThisTick)
                if (damageThisTick > 0) {
                    repository.updateProfile(profileNow.copy(baseHp = newHp))
                }

                // Sanity drain in the night
                val sanityDrain = if (profileNow.pledgedSoul) 2 else 1
                val newSanity = maxOf(0, profileNow.sanity - sanityDrain)
                repository.updateProfile(profileNow.copy(sanity = newSanity))

                val newTime = current.timeLeftSeconds - 1
                val newFreeze = maxOf(0, current.freezeTimerSeconds - 1)

                if (newHp <= 0) {
                    // Defeat!
                    _nightBattle.value = current.copy(
                        isRunning = false,
                        battleResult = BattleResult.DEFEAT
                    )
                    repository.completeNightDefense(false, current.slainCount, current.damageTaken)
                    break
                } else if (newTime <= 0) {
                    // Victory! Dawn arrives!
                    _nightBattle.value = current.copy(
                        timeLeftSeconds = 0,
                        isRunning = false,
                        battleResult = BattleResult.VICTORY
                    )
                    repository.completeNightDefense(true, current.slainCount, current.damageTaken)
                    break
                } else {
                    _nightBattle.value = current.copy(
                        timeLeftSeconds = newTime,
                        freezeTimerSeconds = newFreeze,
                        demons = survivingDemons,
                        damageTaken = current.damageTaken + damageThisTick
                    )
                }
            }
        }
    }

    private fun generateInitialDemons(day: Int, hasDebt: Boolean): List<DemonTarget> {
        val list = mutableListOf<DemonTarget>()
        val count = 2 + minOf(5, day)
        for (i in 0 until count) {
            val type = if (hasDebt && i == 0) "COLLECTOR" else if (i % 2 == 0) "SHADOW_JINN" else "IFRIT"
            val hp = when (type) {
                "COLLECTOR" -> 60 + day * 10
                "IFRIT" -> 40 + day * 8
                else -> 25 + day * 5
            }
            list.add(
                DemonTarget(
                    id = System.nanoTime() + i,
                    type = type,
                    worldX = (i - count / 2f) * 4.5f,
                    worldZ = 28f + (i * 3.5f),
                    hp = hp,
                    maxHp = hp,
                    speed = 1.6f + (day * 0.15f)
                )
            )
        }
        return list
    }

    fun shootAt(normalizedX: Float, normalizedY: Float) {
        val current = _nightBattle.value
        val profile = gameProfile.value ?: return

        if (!current.isRunning || current.isReloading || current.magazineAmmo <= 0) {
            if (current.magazineAmmo <= 0) {
                reloadGun()
            }
            return
        }

        audioHaptics.playGunshot()

        // Pledged arm debuff reduces shotgun precision & damage
        val damage = if (profile.pledgedArm) 28 else 45
        var hitAny = false
        var slainNew = 0

        // In 3D: Convert look angle or tap coordinates to hit detection
        val yawRad = Math.toRadians(current.cameraYaw.toDouble()).toFloat()
        val pitchRad = Math.toRadians(current.cameraPitch.toDouble()).toFloat()

        val updatedDemons = current.demons.map { demon ->
            val dx = demon.worldX
            val dz = demon.worldZ
            val xCam = dx * kotlin.math.cos(yawRad) - dz * kotlin.math.sin(yawRad)
            val zCam = dx * kotlin.math.sin(yawRad) + dz * kotlin.math.cos(yawRad)

            if (zCam > 0.5f) {
                val projX = 0.5f + (xCam / zCam) * 0.8f
                val projY = 0.5f - (pitchRad * 0.8f) - (0.5f / zCam)
                val dist = kotlin.math.hypot(projX - normalizedX, projY - normalizedY)
                val hitRadius = (2.4f / zCam).coerceIn(0.09f, 0.35f)

                if (dist < hitRadius) {
                    hitAny = true
                    val newHp = demon.hp - damage
                    if (newHp <= 0) slainNew++
                    demon.copy(hp = newHp)
                } else {
                    demon
                }
            } else {
                demon
            }
        }.filter { it.hp > 0 }

        _nightBattle.value = current.copy(
            magazineAmmo = current.magazineAmmo - 1,
            demons = updatedDemons,
            slainCount = current.slainCount + slainNew,
            muzzleFlash = true,
            lastHitPosition = if (hitAny) Pair(normalizedX, normalizedY) else null
        )

        viewModelScope.launch {
            delay(120)
            _nightBattle.value = _nightBattle.value.copy(muzzleFlash = false)
        }
    }

    fun reloadGun() {
        val current = _nightBattle.value
        val profile = gameProfile.value ?: return
        if (current.isReloading || profile.ammo <= 0) return

        val reloadTimeMs = if (profile.pledgedArm) 2000L else 1200L
        _nightBattle.value = current.copy(isReloading = true)

        viewModelScope.launch {
            delay(reloadTimeMs)
            val needed = current.maxMagazine - current.magazineAmmo
            val loaded = minOf(needed, profile.ammo)
            repository.updateProfile(profile.copy(ammo = profile.ammo - loaded))
            _nightBattle.value = _nightBattle.value.copy(
                magazineAmmo = current.magazineAmmo + loaded,
                isReloading = false
            )
            audioHaptics.playCoinStamp()
        }
    }

    fun castRitualCircle() {
        val profile = gameProfile.value ?: return
        if (profile.darkEther < 25) {
            showMessage("Safran tilsimi uchun 25 Qora Eter kerak!")
            return
        }
        viewModelScope.launch {
            audioHaptics.playRitualCast()
            repository.updateProfile(profile.copy(darkEther = profile.darkEther - 25))
            _nightBattle.value = _nightBattle.value.copy(freezeTimerSeconds = 5)
            showMessage("Safran Tilsimi chizildi! Jinlar 5 soniyaga muzlatildi!")
        }
    }

    fun igniteEtherFlare() {
        val profile = gameProfile.value ?: return
        if (profile.darkEther < 15) {
            showMessage("Efir mashʻalasi uchun 15 Qora Eter kerak!")
            return
        }
        viewModelScope.launch {
            audioHaptics.playRitualCast()
            val newSanity = minOf(100, profile.sanity + 30)
            repository.updateProfile(profile.copy(darkEther = profile.darkEther - 15, sanity = newSanity))
            showMessage("Efir Mashʻalasi yoqildi! Aql-idrok (Sanity) tiklandi (+30%)")
        }
    }

    fun returnToCamp() {
        battleJob?.cancel()
        _currentScreen.value = GameScreen.DAY_CAMP
    }

    fun resetGame() {
        viewModelScope.launch {
            val lang = gameProfile.value?.language ?: "uz"
            repository.resetGame(lang)
            _currentScreen.value = GameScreen.MAIN_MENU
            showMessage("Oʻyin yangitdan boshlandi!")
        }
    }
}
