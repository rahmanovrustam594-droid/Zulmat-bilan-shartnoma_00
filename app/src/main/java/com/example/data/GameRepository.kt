package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class GameRepository(
    private val gameDao: GameDao,
    private val occultService: OccultCodexService
) {
    val gameProfileFlow: Flow<GameProfile?> = gameDao.getGameProfileFlow()
    val codexEntriesFlow: Flow<List<CodexEntry>> = gameDao.getAllCodexEntries()

    suspend fun getOrCreateProfile(): GameProfile {
        val existing = gameDao.getGameProfile()
        if (existing != null) return existing
        val defaultProfile = GameProfile()
        gameDao.insertOrUpdateProfile(defaultProfile)
        seedDefaultCodex()
        return defaultProfile
    }

    suspend fun updateProfile(profile: GameProfile) {
        gameDao.insertOrUpdateProfile(profile)
    }

    suspend fun resetGame(language: String = "uz") {
        val reset = GameProfile(language = language)
        gameDao.insertOrUpdateProfile(reset)
    }

    suspend fun borrowFromOliyZom(bloodLoan: Int, etherLoan: Int, ammoLoan: Int, collateralType: String) {
        val current = getOrCreateProfile()
        val totalValue = bloodLoan + etherLoan + ammoLoan
        val newDebt = current.debtAmount + (totalValue * (1f + current.debtInterestRate)).toInt()

        val updated = current.copy(
            liquidBlood = current.liquidBlood + bloodLoan,
            darkEther = current.darkEther + etherLoan,
            ammo = current.ammo + ammoLoan,
            debtAmount = newDebt,
            debtDueNight = current.dayNumber + 1,
            pledgedEye = if (collateralType == "EYE") true else current.pledgedEye,
            pledgedArm = if (collateralType == "ARM") true else current.pledgedArm,
            pledgedSoul = if (collateralType == "SOUL") true else current.pledgedSoul
        )
        gameDao.insertOrUpdateProfile(updated)
    }

    suspend fun repayDebt(amount: Int): Boolean {
        val current = getOrCreateProfile()
        if (current.darkEther < amount && current.liquidBlood < amount) return false
        val paidFromEther = minOf(current.darkEther, amount)
        val remainingToPay = amount - paidFromEther
        val paidFromBlood = minOf(current.liquidBlood, remainingToPay)

        val newDebt = maxOf(0, current.debtAmount - amount)
        val updated = current.copy(
            darkEther = current.darkEther - paidFromEther,
            liquidBlood = current.liquidBlood - paidFromBlood,
            debtAmount = newDebt
        )
        gameDao.insertOrUpdateProfile(updated)
        return true
    }

    suspend fun startBioPodRegeneration(partToRegrow: String): Boolean {
        val current = getOrCreateProfile()
        // Needs 80 Biomass and 100 Blood to initiate growth
        if (current.rawBiomass < 80 || current.liquidBlood < 100) return false

        val updated = current.copy(
            rawBiomass = current.rawBiomass - 80,
            liquidBlood = current.liquidBlood - 100,
            bioPodActive = true,
            bioPodProgress = 0.05f,
            regeneratingPart = partToRegrow
        )
        gameDao.insertOrUpdateProfile(updated)
        return true
    }

    suspend fun advanceBioPodTick(delta: Float) {
        val current = getOrCreateProfile()
        if (!current.bioPodActive) return

        val newProgress = current.bioPodProgress + delta
        if (newProgress >= 1.0f) {
            // Regeneration complete! Remove pledged penalty
            val updated = current.copy(
                bioPodActive = false,
                bioPodProgress = 0f,
                pledgedEye = if (current.regeneratingPart == "EYE") false else current.pledgedEye,
                pledgedArm = if (current.regeneratingPart == "ARM") false else current.pledgedArm,
                pledgedSoul = if (current.regeneratingPart == "SOUL") false else current.pledgedSoul,
                regeneratingPart = "NONE",
                sanity = minOf(100, current.sanity + 25)
            )
            gameDao.insertOrUpdateProfile(updated)
        } else {
            gameDao.insertOrUpdateProfile(current.copy(bioPodProgress = newProgress))
        }
    }

    suspend fun craftUpgrade(type: String): Boolean {
        val current = getOrCreateProfile()
        when (type) {
            "TURRET" -> {
                if (current.ironPipes >= 8 && current.liquidBlood >= 75) {
                    gameDao.insertOrUpdateProfile(
                        current.copy(
                            ironPipes = current.ironPipes - 8,
                            liquidBlood = current.liquidBlood - 75,
                            turretCount = current.turretCount + 1
                        )
                    )
                    return true
                }
            }
            "PIPES" -> {
                if (current.ironPipes >= 5 && current.darkEther >= 60) {
                    gameDao.insertOrUpdateProfile(
                        current.copy(
                            ironPipes = current.ironPipes - 5,
                            darkEther = current.darkEther - 60,
                            pipeNetworkLevel = current.pipeNetworkLevel + 1
                        )
                    )
                    return true
                }
            }
            "AMMO" -> {
                if (current.liquidBlood >= 40 && current.rawBiomass >= 30) {
                    gameDao.insertOrUpdateProfile(
                        current.copy(
                            liquidBlood = current.liquidBlood - 40,
                            rawBiomass = current.rawBiomass - 30,
                            ammo = current.ammo + 20
                        )
                    )
                    return true
                }
            }
            "BARRIER" -> {
                if (current.ironPipes >= 4 && current.darkEther >= 50) {
                    gameDao.insertOrUpdateProfile(
                        current.copy(
                            ironPipes = current.ironPipes - 4,
                            darkEther = current.darkEther - 50,
                            barrierDurability = minOf(100, current.barrierDurability + 40),
                            baseHp = minOf(100, current.baseHp + 25)
                        )
                    )
                    return true
                }
            }
        }
        return false
    }

    suspend fun scavengeExpedition(location: String): String {
        val current = getOrCreateProfile()
        val randomBlood = Random.nextInt(30, 80)
        val randomEther = Random.nextInt(20, 60)
        val randomBiomass = Random.nextInt(20, 50)
        val randomPipes = Random.nextInt(2, 6)

        val updated = current.copy(
            liquidBlood = current.liquidBlood + randomBlood,
            darkEther = current.darkEther + randomEther,
            rawBiomass = current.rawBiomass + randomBiomass,
            ironPipes = current.ironPipes + randomPipes,
            sanity = maxOf(10, current.sanity - 10)
        )
        gameDao.insertOrUpdateProfile(updated)

        return "+$randomBlood Suyuq Qon, +$randomEther Qora Eter, +$randomBiomass Biomassa, +$randomPipes Quvur topildi!"
    }

    suspend fun upgradeBeacon(): Boolean {
        val current = getOrCreateProfile()
        when (current.beaconLevel) {
            1 -> {
                if (current.darkEther >= 70 && current.ironPipes >= 8) {
                    val updated = current.copy(
                        darkEther = current.darkEther - 70,
                        ironPipes = current.ironPipes - 8,
                        beaconLevel = 2,
                        unlockedSectors = "HUB,FOREST,CRAGS,VAULT"
                    )
                    gameDao.insertOrUpdateProfile(updated)
                    return true
                }
            }
            2 -> {
                if (current.darkEther >= 140 && current.ironPipes >= 14) {
                    val updated = current.copy(
                        darkEther = current.darkEther - 140,
                        ironPipes = current.ironPipes - 14,
                        beaconLevel = 3,
                        unlockedSectors = "HUB,FOREST,CRAGS,VAULT,SWAMP"
                    )
                    gameDao.insertOrUpdateProfile(updated)
                    return true
                }
            }
            3 -> {
                if (current.darkEther >= 250 && current.ironPipes >= 22) {
                    val updated = current.copy(
                        darkEther = current.darkEther - 250,
                        ironPipes = current.ironPipes - 22,
                        beaconLevel = 4,
                        unlockedSectors = "HUB,FOREST,CRAGS,VAULT,SWAMP,RIFT"
                    )
                    gameDao.insertOrUpdateProfile(updated)
                    return true
                }
            }
        }
        return false
    }

    suspend fun exploreSpecificSector(sectorId: String): String {
        val current = getOrCreateProfile()
        return when (sectorId) {
            "CRAGS" -> {
                val ether = Random.nextInt(70, 130)
                val pipes = Random.nextInt(4, 9)
                val updated = current.copy(
                    darkEther = current.darkEther + ether,
                    ironPipes = current.ironPipes + pipes,
                    sanity = maxOf(5, current.sanity - 15)
                )
                gameDao.insertOrUpdateProfile(updated)
                "Qora Eter Qoyalari (3.5 km): +$ether Qora Eter, +$pipes Quvurlar topildi!"
            }
            "VAULT" -> {
                val blood = Random.nextInt(90, 160)
                val ammo = Random.nextInt(15, 30)
                val updated = current.copy(
                    liquidBlood = current.liquidBlood + blood,
                    ammo = current.ammo + ammo,
                    sanity = maxOf(5, current.sanity - 18)
                )
                gameDao.insertOrUpdateProfile(updated)
                "Oliy Zom Yertoʻlasi (5.0 km): +$blood Suyuq Qon, +$ammo Oʻq-dori olindi!"
            }
            "SWAMP" -> {
                val biomass = Random.nextInt(80, 150)
                val blood = Random.nextInt(80, 140)
                val updated = current.copy(
                    rawBiomass = current.rawBiomass + biomass,
                    liquidBlood = current.liquidBlood + blood,
                    sanity = maxOf(5, current.sanity - 20)
                )
                gameDao.insertOrUpdateProfile(updated)
                "Qonli Botqoqlik & Plantatsiya (7.5 km): +$biomass Biomassa, +$blood Qon topildi!"
            }
            "RIFT" -> {
                val ether = Random.nextInt(120, 220)
                val biomass = Random.nextInt(100, 180)
                val ammo = Random.nextInt(25, 50)
                val updated = current.copy(
                    darkEther = current.darkEther + ether,
                    rawBiomass = current.rawBiomass + biomass,
                    ammo = current.ammo + ammo,
                    sanity = maxOf(5, current.sanity - 25)
                )
                gameDao.insertOrUpdateProfile(updated)
                "Sado Vodiyi & Nomaʼlum Anomaliya (10.0+ km): +$ether Efir, +$biomass Bio, +$ammo Oʻq!"
            }
            else -> scavengeExpedition("Qora Oʻrmon")
        }
    }

    suspend fun tuneRadio(freq: Float): String? {
        val current = getOrCreateProfile()
        val delta = kotlin.math.abs(freq - 94.2f)
        val deltaSecret = kotlin.math.abs(freq - 103.5f)

        if (delta < 0.3f) {
            val updated = current.copy(
                tunedFrequency = freq,
                interceptedSignalsCount = current.interceptedSignalsCount + 1,
                darkEther = current.darkEther + 40
            )
            gameDao.insertOrUpdateProfile(updated)
            return "CHASTOTA 94.2 MHz: [ZULMAT SADOSI TUTILDI] 'Tunda shimoliy darvozani qon bilan muhrlang... Maridlar suvdan keladi...' (+40 Qora Eter)"
        } else if (deltaSecret < 0.3f) {
            val updated = current.copy(
                tunedFrequency = freq,
                interceptedSignalsCount = current.interceptedSignalsCount + 1,
                rawBiomass = current.rawBiomass + 50
            )
            gameDao.insertOrUpdateProfile(updated)
            return "CHASTOTA 103.5 MHz: [SOYA BANKI SHIFRI] 'Oliy Zom qarzdorlarni qidirmoqda... Kollektorlar 23:00 da yo'lga chiqadi.' (+50 Biomassa)"
        }

        gameDao.insertOrUpdateProfile(current.copy(tunedFrequency = freq))
        return null
    }

    suspend fun completeNightDefense(survived: Boolean, slainJinns: Int, damageTaken: Int): GameProfile {
        val current = getOrCreateProfile()
        if (!survived) {
            // Player fell in battle, penalty but revived
            val updated = current.copy(
                baseHp = 40,
                sanity = 30,
                debtAmount = current.debtAmount + 100, // Bank penalty
                liquidBlood = maxOf(30, current.liquidBlood / 2),
                isNight = false,
                dayNumber = current.dayNumber + 1,
                timeHour = 6.0f
            )
            gameDao.insertOrUpdateProfile(updated)
            return updated
        }

        // Night Survived!
        val rewardBlood = slainJinns * 15
        val rewardBiomass = slainJinns * 10
        val rewardEther = 35 + current.dayNumber * 10

        val newHighest = maxOf(current.highestNightsSurvived, current.dayNumber)
        val updated = current.copy(
            dayNumber = current.dayNumber + 1,
            timeHour = 6.0f,
            isNight = false,
            liquidBlood = current.liquidBlood + rewardBlood,
            rawBiomass = current.rawBiomass + rewardBiomass,
            darkEther = current.darkEther + rewardEther,
            baseHp = maxOf(10, current.baseHp - damageTaken),
            sanity = minOf(100, current.sanity + 20),
            totalJinnSlain = current.totalJinnSlain + slainJinns,
            highestNightsSurvived = newHighest
        )
        gameDao.insertOrUpdateProfile(updated)
        return updated
    }

    suspend fun queryCodex(query: String, category: String): GroundedLoreResult {
        val result = occultService.queryOccultLore(query, category).getOrNull()
            ?: GroundedLoreResult(query, "Tilsim tahlil qilinmoqda...", emptyList(), emptyList())

        // Save to Room for offline quick reference
        gameDao.insertCodexEntry(
            CodexEntry(
                id = "${category}_${System.currentTimeMillis()}",
                title = result.title,
                category = category,
                summary = result.content.take(120),
                detailedLore = result.content,
                searchGroundedAnswer = result.content,
                groundingSource = result.searchSources.joinToString(", ")
            )
        )
        return result
    }

    private suspend fun seedDefaultCodex() {
        val entries = listOf(
            CodexEntry(
                id = "jinn_marid",
                title = "Marid — Efir Alvastisi",
                category = "JINN",
                summary = "Aql-idrokni yo'qotuvchi sovuq jin.",
                detailedLore = "Marid qora tuman ichida paydo bo'ladi. Uning ko'zlari yo'q, lekin o'yinchining aql-idroki (Sanity) 50% dan pastga tushsa, uning illyuziyalari to'g'ridan-to'g'ri jismoniy jarohat yetkazadi. Qora Eter qurollari unga halokatli zarba beradi.",
                groundingSource = "Sharq Mifologiyasi & Okult Ensiklopediya"
            ),
            CodexEntry(
                id = "jinn_ifrit",
                title = "Ifrit — Olovli Qasos Jini",
                category = "JINN",
                summary = "Qon quvurlarini yorib o'tuvchi alangali dev.",
                detailedLore = "Ifrit olovli qanotlari bilan to'siqlarni yondiradi. Qon bilan quvvatlangan Blood Con turretlari uning olovini o'chirishga qodir. Barrikadaga yaqinlashganda safran ritual chizig'i bilan uni to'xtatish shart.",
                groundingSource = "O'zbek Xalq Demonologiyasi"
            ),
            CodexEntry(
                id = "necro_oliy_zom",
                title = "Oliy Zom — Soya Bankiri",
                category = "NECRO_FINANCE",
                summary = "Garovga ko'z va qo'l oluvchi aristokrat vampir-bankir.",
                detailedLore = "Tiriklik bilan o'lim chegarasidagi moliyaviy tizim. Oliy Zomdan olingan kreditning har bir tangasi qon bilan to'lanadi. Agar qarzingizni yopmasangiz, uning Kollektorlari tungi reyd paytida bazangizni yer bilan yakson qiladi.",
                groundingSource = "Zulmat Shartnomasi Kodeksi"
            ),
            CodexEntry(
                id = "blood_con_system",
                title = "Blood Con Muhandisligi",
                category = "BLOOD_CON",
                summary = "Qon va efir oqadigan tirik gidravlika.",
                detailedLore = "Omborsiz arxitektura. Barcha modlar va qurollar bitta tarmoqda ishlaydi. Dushmanlardan olingan qon bevosita quvurlar orqali avtomatik turretlarga boradi va o'qqa aylanadi.",
                groundingSource = "Bio-Gotik Texnologiyalar Qollanmasi"
            )
        )
        entries.forEach { gameDao.insertCodexEntry(it) }
    }
}
