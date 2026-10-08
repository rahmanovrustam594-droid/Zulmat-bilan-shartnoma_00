package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameProfile
import com.example.ui.GameScreen
import com.example.ui.GameStrings
import com.example.ui.GameViewModel
import com.example.ui.theme.BiomassGreen
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.BloodDark
import com.example.ui.theme.BloodGlow
import com.example.ui.theme.BrassAged
import com.example.ui.theme.BrassGold
import com.example.ui.theme.DarkEther
import com.example.ui.theme.EtherDeep
import com.example.ui.theme.SanityPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceCard
import com.example.ui.theme.VoidObsidian

@Composable
fun DayCampScreen(
    viewModel: GameViewModel,
    profile: GameProfile,
    modifier: Modifier = Modifier
) {
    val lang = profile.language
    val activeTab by viewModel.activeTab.collectAsState()
    val systemMsg by viewModel.systemMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidObsidian)
    ) {
        // TOP HUD: Status & Resources
        CampTopHud(
            profile = profile,
            onBackToMenu = { viewModel.navigateTo(GameScreen.MAIN_MENU) }
        )

        // System notification banner if any
        if (systemMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BloodDark)
                    .padding(vertical = 6.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = systemMsg ?: "",
                    color = BloodGlow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // TABS: Navigation between World Map, Scavenge, Bank, Engineering, Bio-Pod, Radio
        CampTabRow(
            activeTab = activeTab,
            onTabSelected = { viewModel.setActiveTab(it) }
        )

        // TAB CONTENT (Scrollable)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                "MAP" -> WorldMapTab(viewModel, profile, lang)
                "SCAVENGE" -> ScavengeTab(viewModel, profile, lang)
                "BANK" -> OliyZomBankTab(viewModel, profile, lang)
                "ENGINEERING" -> BloodConEngineeringTab(viewModel, profile, lang)
                "BIOPOD" -> BioPodTab(viewModel, profile, lang)
                "RADIO" -> OccultRadioTab(viewModel, profile, lang)
                else -> WorldMapTab(viewModel, profile, lang)
            }
        }

        // BOTTOM BAR: Enter Nightfall Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VaultSurfaceCard)
                .border(1.dp, VaultBorder)
                .padding(16.dp)
        ) {
            Button(
                onClick = { viewModel.startNightDefense() },
                modifier = Modifier
                    .testTag("start_night_button")
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BloodCrimson),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Nightlight,
                        contentDescription = "Nightfall",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = GameStrings.get("start_night", lang).uppercase(),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CampTopHud(
    profile: GameProfile,
    onBackToMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VaultSurface)
            .padding(top = 8.dp, bottom = 10.dp, start = 12.dp, end = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToMenu,
                    modifier = Modifier.testTag("camp_back_button").size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BrassGold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "KUN #${profile.dayNumber}",
                        color = BrassGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Kunduzgi Faza (06:00 — 18:00)",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Health & Sanity
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Health
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = "HP", tint = BloodCrimson, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${profile.baseHp}%", color = BloodCrimson, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                // Sanity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = "Sanity", tint = SanityPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${profile.sanity}%", color = SanityPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Resource Gauges Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ResourceBadge("🩸 Qon", "${profile.liquidBlood}", BloodCrimson)
            ResourceBadge("⚡ Eter", "${profile.darkEther}", DarkEther)
            ResourceBadge("🥩 Bio", "${profile.rawBiomass}", BiomassGreen)
            ResourceBadge("🔧 Quvur", "${profile.ironPipes}", BrassGold)
            ResourceBadge("🎯 Oʻq", "${profile.ammo}", TextPrimary)
        }

        // Debt & Collateral Warning Banner if any
        if (profile.debtAmount > 0 || profile.pledgedEye || profile.pledgedArm || profile.pledgedSoul) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BloodDark.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = "Warning", tint = BloodGlow, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (profile.debtAmount > 0) "Oliy Zom Qarzi: ${profile.debtAmount} Eter" else "Qarzsiz",
                        color = BloodGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (profile.pledgedEye) {
                        Text("👁️ Koʻz Garovda", color = BrassGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (profile.pledgedArm) {
                        Text("🦾 Qoʻl Garovda", color = BrassGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (profile.pledgedSoul) {
                        Text("👻 Ruh Garovda", color = SanityPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceBadge(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(VaultSurfaceCard, RoundedCornerShape(6.dp))
            .border(0.5.dp, VaultBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 13.sp)
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}

@Composable
fun CampTabRow(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        "MAP" to "🗺️ Dunyo Xaritasi",
        "SCAVENGE" to "Ekspeditsiya",
        "BANK" to "Oliy Zom",
        "ENGINEERING" to "Blood Con",
        "BIOPOD" to "Bio-Pod",
        "RADIO" to "Radio"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(VaultSurface)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(tabs) { (key, label) ->
            val isSelected = activeTab == key
            Box(
                modifier = Modifier
                    .testTag("tab_$key")
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) BloodCrimson else VaultSurfaceCard)
                    .clickable { onTabSelected(key) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

// --- SUB-TABS ---

@Composable
fun ScavengeTab(viewModel: GameViewModel, profile: GameProfile, lang: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BloodCrimson, BrassGold))),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "QORA OʻRMON VA ANOMALIYA SKANERI",
                    color = BrassGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tashqarida qalin tuman va anomaliyalar hukmron. Kunduzi bazadan chiqib, vayrona qon quvurlari va efir kukunini yigʻib olishingiz mumkin. Har bir chiqish aql-idrokni (Sanity) -10% ga tushiradi.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.scavenge() },
                    modifier = Modifier.testTag("scavenge_action_button").fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrassAged)
                ) {
                    Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EKSPEDITSIYA YUBORISH (Resurs Yigʻish)", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Anomaly Sites
        Text("ANIQLANGAN ANOMALIYA HUDUDLARI", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        
        listOf(
            Triple("Qonli Drenaj Quvurlari", "Suyuq Qon va Zanglagan Quvurlar manbai", BloodCrimson),
            Triple("Eter Kristal Anomaliyasi", "Sehrli toʻsiqlar uchun Qora Eter koni", DarkEther),
            Triple("Goʻshtxoʻr Plantatsiya", "Bio-Growth pod uchun xom Biomassa", BiomassGreen)
        ).forEach { (title, desc, color) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(color.copy(alpha = 0.5f), VaultBorder))),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(desc, color = TextMuted, fontSize = 11.sp)
                    }
                    Button(
                        onClick = { viewModel.scavenge() },
                        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Kovlash", fontSize = 11.sp, color = VoidObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OliyZomBankTab(viewModel: GameViewModel, profile: GameProfile, lang: String) {
    var selectedCollateral by remember { mutableStateOf("NONE") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banker Vault Visual
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            modifier = Modifier.fillMaxWidth().height(180.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.oliy_zom_vault),
                    contentDescription = "Oliy Zom Vault",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, VoidObsidian.copy(alpha = 0.85f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "OLIY ZOM — SOYA BANKI",
                        color = BrassGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "\"Resurs kerakmi, tirik vujud? Qonli shartnomaga imzo chek...\"",
                        color = BloodGlow,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        // Debt Ledger Status
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BloodCrimson, VaultBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Joriy Qarz Balansi:", color = TextPrimary, fontSize = 13.sp)
                    Text("${profile.debtAmount} Eter", color = BloodCrimson, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Foiz stavkasi: Kuniga 25%. Muddat: #${profile.debtDueNight}-kunga qadar. Toʻlanmasa, Kollektorlar reydi boshlanadi!",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                if (profile.debtAmount > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.repayDebt(profile.debtAmount) },
                        modifier = Modifier.testTag("repay_all_debt_button").fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkEther)
                    ) {
                        Text("QARZNI TOʻLIQ YOPISH", color = VoidObsidian, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Collateral Selection (Garov)
        Text("GAROV TANLASH (Flesh Collateral):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(
            "Garov qoʻyish foizni kamaytiradi, lekin jismoniy qobiliyatni cheklaydi:",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "NONE" to "Garovsiz",
                "EYE" to "👁️ Chap Koʻz",
                "ARM" to "🦾 Oʻng Qoʻl",
                "SOUL" to "👻 Ruh"
            ).forEach { (code, label) ->
                val isSelected = selectedCollateral == code
                Box(
                    modifier = Modifier
                        .testTag("collateral_$code")
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BloodCrimson else VaultSurfaceCard)
                        .border(1.dp, if (isSelected) BloodGlow else VaultBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedCollateral = code }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Loan Offers
        Text("KREDIT PAKETLARI:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        
        LoanCard(
            title = "Kichik Kredit Paketi",
            resources = "+120 Qon, +80 Eter, +20 Oʻq-dori",
            onClick = { viewModel.borrowLoan("SMALL", selectedCollateral) }
        )
        LoanCard(
            title = "Oʻrta Mudofaa Krediti",
            resources = "+300 Qon, +200 Eter, +45 Oʻq-dori",
            onClick = { viewModel.borrowLoan("MEDIUM", selectedCollateral) }
        )
        LoanCard(
            title = "Katta Bio-Gothic Krediti",
            resources = "+700 Qon, +450 Eter, +90 Oʻq-dori",
            onClick = { viewModel.borrowLoan("MASSIVE", selectedCollateral) }
        )
    }
}

@Composable
fun LoanCard(title: String, resources: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BrassGold.copy(alpha = 0.5f), VaultBorder))),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = BrassGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(resources, color = TextSecondary, fontSize = 11.sp)
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = BloodDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Olish", color = BloodGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun BloodConEngineeringTab(viewModel: GameViewModel, profile: GameProfile, lang: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BloodCrimson, DarkEther)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "BLOOD CON: GIBRID QUVURLAR VA TURRETLAR",
                    color = BloodGlow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Bazangiz omborsiz gidravlika asosida ishlaydi. Dushmanlardan olingan qon toʻgʻridan-toʻgʻri quvurlar orqali avtomatik turretlarga boradi va oʻt ochishni taʼminlaydi.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Oʻrnatilgan Turretlar: ${profile.turretCount} ta", color = BrassGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Quvurlar Tarmogʻi: Daraja ${profile.pipeNetworkLevel}", color = DarkEther, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Text("MUHANDISLIK LOYIHALARI (CRAFT):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        CraftUpgradeItem(
            name = "Yangi Blood Turret Qurish",
            cost = "8 Quvur + 75 Suyuq Qon",
            desc = "Tunda dushmanlarga avtomatik qonli snaryadlar yogʻdiradi.",
            canAfford = profile.ironPipes >= 8 && profile.liquidBlood >= 75,
            onClick = { viewModel.craftUpgrade("TURRET") }
        )

        CraftUpgradeItem(
            name = "Quvur Bosimini Kuchaytirish",
            cost = "5 Quvur + 60 Qora Eter",
            desc = "Turretlar otish tezligi va mudofaa sinergiyasini oshiradi.",
            canAfford = profile.ironPipes >= 5 && profile.darkEther >= 60,
            onClick = { viewModel.craftUpgrade("PIPES") }
        )

        CraftUpgradeItem(
            name = "Shotgun Oʻq-dorisi Tayyorlash (+20)",
            cost = "40 Suyuq Qon + 30 Biomassa",
            desc = "Birinchi shaxs mudofaasi uchun qonli gilzalar quyish.",
            canAfford = profile.liquidBlood >= 40 && profile.rawBiomass >= 30,
            onClick = { viewModel.craftUpgrade("AMMO") }
        )

        CraftUpgradeItem(
            name = "Barrikadani Qayta Tiklash (+40%)",
            cost = "4 Quvur + 50 Qora Eter",
            desc = "Jinlar bosqiniga qarshi mustahkam temir va efir toʻsigʻi.",
            canAfford = profile.ironPipes >= 4 && profile.darkEther >= 50,
            onClick = { viewModel.craftUpgrade("BARRIER") }
        )
    }
}

@Composable
fun CraftUpgradeItem(
    name: String,
    cost: String,
    desc: String,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VaultBorder, VaultBorder))),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(cost, color = if (canAfford) BiomassGreen else BloodCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                enabled = canAfford,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) BloodCrimson else VaultBorder,
                    disabledContainerColor = VaultBorder
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (canAfford) "QURISH / YARATISH" else "RESURS YETARLI EMAS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BioPodTab(viewModel: GameViewModel, profile: GameProfile, lang: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visual of the Bio-Pod
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            modifier = Modifier.fillMaxWidth().height(180.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.bio_growth_pod),
                    contentDescription = "Bio Growth Pod",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, VoidObsidian.copy(alpha = 0.85f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "TIRIK ORGAN OʻSTIRISH KAPSULASI (BIO-POD)",
                        color = BiomassGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Biomassa va suyuq plazma yordamida yoʻqotilgan aʻzolarni qayta tiklash.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Active Regeneration Status
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BiomassGreen, VaultBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kapsula Holati:", color = TextPrimary, fontSize = 13.sp)
                    Text(
                        text = if (profile.bioPodActive) "Oʻstirmoqda (${profile.regeneratingPart})" else "Kutish Rejimida",
                        color = if (profile.bioPodActive) BiomassGreen else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (profile.bioPodActive) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { profile.bioPodProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = BiomassGreen,
                        trackColor = VaultBorder
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Jarayon: ${(profile.bioPodProgress * 100).toInt()}% (Tungi jang davomida rivojlanadi)",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Available Body Parts to Regrow
        Text("TIKLASH MUMKIN BOʻLGAN AʻZOLAR:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        RegrowPartCard(
            partName = "Chap Koʻz (Vision Restoration)",
            isPledged = profile.pledgedEye,
            desc = "Tungi jangdagi qorongʻu periferik koʻrish cheklovini yoʻqotadi.",
            canStart = !profile.bioPodActive && profile.pledgedEye && profile.rawBiomass >= 80 && profile.liquidBlood >= 100,
            onClick = { viewModel.startBioPod("EYE") }
        )

        RegrowPartCard(
            partName = "Oʻng Qoʻl (Handling & Reload)",
            isPledged = profile.pledgedArm,
            desc = "Shotgun oʻqlash tezligi va nishonga olish barqarorligini toʻliq tiklaydi.",
            canStart = !profile.bioPodActive && profile.pledgedArm && profile.rawBiomass >= 80 && profile.liquidBlood >= 100,
            onClick = { viewModel.startBioPod("ARM") }
        )

        RegrowPartCard(
            partName = "Ruh Poklash (Sanity Stabilizer)",
            isPledged = profile.pledgedSoul,
            desc = "Oliy Zomga berilgan ruh bogʻliqligini uzib, aql-idrok oqishini toʻxtatadi.",
            canStart = !profile.bioPodActive && profile.pledgedSoul && profile.rawBiomass >= 80 && profile.liquidBlood >= 100,
            onClick = { viewModel.startBioPod("SOUL") }
        )
    }
}

@Composable
fun RegrowPartCard(
    partName: String,
    isPledged: Boolean,
    desc: String,
    canStart: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(partName, color = if (isPledged) BloodGlow else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = if (isPledged) "⚠️ Garovda Ketgan" else "✅ Tana butun",
                    color = if (isPledged) BloodCrimson else BiomassGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (isPledged) {
                Button(
                    onClick = onClick,
                    enabled = canStart,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BiomassGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (canStart) "OʻSTIRISHNI BOSHLASH (80 Bio + 100 Qon)" else "80 Bio + 100 Qon Kerak",
                        color = VoidObsidian,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OccultRadioTab(viewModel: GameViewModel, profile: GameProfile, lang: String) {
    var frequency by remember { mutableFloatStateOf(profile.tunedFrequency) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkEther, BrassGold)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RADIO-OKULT TERMINALI & CHASTOTA TUTGICH",
                    color = DarkEther,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Qora efir toʻlqinlarini tutib olish uchun antennani sozlang. Sirli chastotalarda (masalan, 94.2 MHz va 103.5 MHz) yashirin okult xabarlari, resurs koordinatalari va dushman ogohlantirishlari eshitiladi.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Frequency Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VoidObsidian, RoundedCornerShape(8.dp))
                        .border(1.dp, DarkEther.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${String.format("%.1f", frequency)} MHz",
                        color = DarkEther,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slider
                Slider(
                    value = frequency,
                    onValueChange = { frequency = it },
                    valueRange = 88.0f..108.0f,
                    steps = 200,
                    colors = SliderDefaults.colors(
                        thumbColor = DarkEther,
                        activeTrackColor = DarkEther,
                        inactiveTrackColor = VaultBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.tuneRadioFrequency(frequency) },
                    modifier = Modifier.testTag("tune_radio_button").fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkEther)
                ) {
                    Icon(Icons.Default.Radio, contentDescription = null, tint = VoidObsidian, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CHASTOTANI TEKSHIRISH (Signal Qidirish)", color = VoidObsidian, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("TUTIB OLINGAN SIGNALLAR: ${profile.interceptedSignalsCount} TA", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("• 94.2 MHz — 'Zulmat Sadosi' (Maridlar va shimoliy xandaq)", color = BrassGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• 103.5 MHz — 'Soya Banki Shifri' (Kollektorlar reydi soati)", color = BloodGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• Qolgan toʻlqinlar — Efir shovqini va jinlar pichirlashi", color = TextMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun WorldMapTab(viewModel: GameViewModel, profile: GameProfile, lang: String) {
    val unlocked = profile.unlockedSectors.split(",")
    val bLevel = profile.beaconLevel

    val totalAreaKm2 = when (bLevel) {
        1 -> 20
        2 -> 45
        3 -> 75
        else -> 100
    }

    val exploredRadiusKm = when (bLevel) {
        1 -> "2.5 km"
        2 -> "5.0 km"
        3 -> "7.5 km"
        else -> "10.0+ km"
    }

    val nextCost = when (bLevel) {
        1 -> "70 Eter + 8 Quvur"
        2 -> "140 Eter + 14 Quvur"
        3 -> "250 Eter + 22 Quvur"
        else -> "Maksimal"
    }

    val canUpgrade = when (bLevel) {
        1 -> profile.darkEther >= 70 && profile.ironPipes >= 8
        2 -> profile.darkEther >= 140 && profile.ironPipes >= 14
        3 -> profile.darkEther >= 250 && profile.ironPipes >= 22
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tactical Map Radar Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            modifier = Modifier.fillMaxWidth().height(190.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.world_map_radar),
                    contentDescription = "Tactical World Map Radar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, VoidObsidian.copy(alpha = 0.88f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "ISLA TENEBRAE — DUNYO XARITASI (100 KM²)",
                        color = DarkEther,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Ochilishi: $totalAreaKm2 km² | Nurlanish Radiusi: $exploredRadiusKm",
                        color = BrassGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Signal Tower Beacon Upgrade Card
        Card(
            colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkEther, BrassGold)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Radar, contentDescription = null, tint = DarkEther, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SIGNAL MINORASI (Daraja $bLevel / 4)",
                            color = DarkEther,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "Radius: $exploredRadiusKm",
                        color = BrassGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Signal Minorasi 100% quvvatlanganda 'Sferik Nurlanish' taratadi va tuman ichidagi yangi hududlarni, anomaliya konlari va dushman inlarini ochib beradi.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (bLevel < 4) {
                    Button(
                        onClick = { viewModel.upgradeBeacon() },
                        enabled = canUpgrade,
                        modifier = Modifier.testTag("upgrade_beacon_button").fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkEther,
                            disabledContainerColor = VaultBorder
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (canUpgrade) "SFERIK NURLANISH TARATISH ($nextCost)" else "KERAK: $nextCost",
                            color = if (canUpgrade) VoidObsidian else TextMuted,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Text(
                        text = "✅ Maksimal daraja! Barcha 100 km² orol zonalari toʻliq ochilgan.",
                        color = BiomassGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sectors of the World
        Text("DUNYO HUDUDLARI VA SEKTORLAR:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

        // Sector List
        SectorCard(
            sectorId = "HUB",
            name = "Sektor Alpha: Markaziy Baza & Signal Minorasi",
            distance = "0.0 km (Markaz)",
            dangerLevel = "Xavfsiz (0/10)",
            resources = "Baza, Bio-Pod, Quvurlar va Turretlar",
            isUnlocked = true,
            color = BrassGold,
            onExplore = null
        )

        SectorCard(
            sectorId = "FOREST",
            name = "Sektor 1: Qora Oʻrmon & Vayrona Drenaj",
            distance = "1.5 km Radius",
            dangerLevel = "Past (2/10)",
            resources = "Suyuq Qon, Yogʻoch va Zanglagan Quvurlar",
            isUnlocked = true,
            color = BiomassGreen,
            onExplore = { viewModel.exploreSector("FOREST") }
        )

        SectorCard(
            sectorId = "CRAGS",
            name = "Sektor 2: Qora Eter Qoyalari & Anomaliya Koni",
            distance = "3.5 km Radius",
            dangerLevel = "Oʻrta (4/10) — Maridlar Makoni",
            resources = "Yuqori zichlikdagi Qora Eter, Temir rudasi",
            isUnlocked = unlocked.contains("CRAGS"),
            color = DarkEther,
            requiredLevel = 2,
            onExplore = { viewModel.exploreSector("CRAGS") }
        )

        SectorCard(
            sectorId = "VAULT",
            name = "Sektor 3: Oliy Zom Yertoʻlasi & Soya Banki",
            distance = "5.0 km Radius",
            dangerLevel = "Yuqori (6/10) — Kollektorlar Qasri",
            resources = "Qonli shartnomalar, Oʻq-dori, Oltin tangalar",
            isUnlocked = unlocked.contains("VAULT"),
            color = BloodCrimson,
            requiredLevel = 2,
            onExplore = { viewModel.exploreSector("VAULT") }
        )

        SectorCard(
            sectorId = "SWAMP",
            name = "Sektor 4: Qonli Botqoqlik & Goʻshtxoʻr Plantatsiya",
            distance = "7.5 km Radius",
            dangerLevel = "Oʻta Xavfli (8/10) — Ifrit Inlari",
            resources = "Xom Biomassa, Tirik toʻqimalar, Suyuq Plazma",
            isUnlocked = unlocked.contains("SWAMP"),
            color = Color(0xFFFF5722),
            requiredLevel = 3,
            onExplore = { viewModel.exploreSector("SWAMP") }
        )

        SectorCard(
            sectorId = "RIFT",
            name = "Sektor 5: Sado Vodiyi & Nomaʼlum Anomaliya Rifi",
            distance = "10.0+ km (Orol Chegarasi)",
            dangerLevel = "Ekstremal (10/10) — Qora Yomgʻir & Portal",
            resources = "Giper-Eter, Afsonaviy Artefaktlar, Qutqaruv Portali",
            isUnlocked = unlocked.contains("RIFT"),
            color = Color(0xFFE040FB),
            requiredLevel = 4,
            onExplore = { viewModel.exploreSector("RIFT") }
        )
    }
}

@Composable
fun SectorCard(
    sectorId: String,
    name: String,
    distance: String,
    dangerLevel: String,
    resources: String,
    isUnlocked: Boolean,
    color: Color,
    requiredLevel: Int = 1,
    onExplore: (() -> Unit)?
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) VaultSurfaceCard else VaultSurfaceCard.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(if (isUnlocked) color.copy(alpha = 0.6f) else VaultBorder, VaultBorder)
            )
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    color = if (isUnlocked) color else TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = distance,
                    color = BrassGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Xavf: $dangerLevel", color = if (isUnlocked) TextSecondary else TextMuted, fontSize = 11.sp)
                if (!isUnlocked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = BloodCrimson, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Minora Lv.$requiredLevel", color = BloodCrimson, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Resurslar: $resources", color = TextMuted, fontSize = 10.sp)

            if (isUnlocked && onExplore != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onExplore,
                    modifier = Modifier.fillMaxWidth().testTag("explore_sector_$sectorId"),
                    colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("EKSPEDITSIYA YUBORISH (Kashf Qilish)", color = VoidObsidian, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        }
    }
}

