package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameProfile
import com.example.ui.GameScreen
import com.example.ui.GameStrings
import com.example.ui.GameViewModel
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.BloodDark
import com.example.ui.theme.BloodGlow
import com.example.ui.theme.BrassGold
import com.example.ui.theme.DarkEther
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceCard
import com.example.ui.theme.VoidObsidian

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    profile: GameProfile?,
    modifier: Modifier = Modifier
) {
    val lang = profile?.language ?: "uz"
    var showLangDialog by remember { mutableStateOf(false) }
    var showLoreDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidObsidian)
    ) {
        // Hero Background Image
        Image(
            painter = painterResource(id = R.drawable.hero_menu_bg),
            contentDescription = "Zulmat Shartnomasi Victorian Laboratory",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Atmospheric Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            VoidObsidian.copy(alpha = 0.85f),
                            VoidObsidian.copy(alpha = 0.65f),
                            VoidObsidian.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Main Menu Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Language and Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // High Score badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(VaultSurfaceCard.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                        .border(1.dp, VaultBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Days Survived",
                        tint = BrassGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Omon Qolingan Tunlar: ${profile?.highestNightsSurvived ?: 1}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Language Selector Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .testTag("language_button")
                        .clickable { showLangDialog = true }
                        .background(VaultSurfaceCard.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                        .border(1.dp, DarkEther.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Change Language",
                        tint = DarkEther,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (lang) {
                            "en" -> "🇬🇧 EN"
                            "ru" -> "🇷🇺 RU"
                            "tr" -> "🇹🇷 TR"
                            else -> "🇺🇿 UZ"
                        },
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Center: Title & Atmospheric Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = GameStrings.get("app_title", lang).uppercase(),
                    color = BloodGlow,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "BIO-GOTIK SURVIVAL HORROR & NEKRO-MOLIYA",
                    color = BrassGold.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Qon, Eter va Oliy Zom qarz shartnomalari girdobidagi omon qolish kurashi.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // START GAME (Grimoire Initiation)
                Button(
                    onClick = {
                        viewModel.audioHaptics.playRitualCast()
                        viewModel.navigateTo(GameScreen.DAY_CAMP)
                    },
                    modifier = Modifier
                        .testTag("start_game_button")
                        .fillMaxWidth()
                        .height(60.dp)
                        .border(1.5.dp, BloodCrimson.copy(alpha = glowAlpha), RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BloodDark
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Game",
                            tint = BloodGlow,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = GameStrings.get("start_game", lang),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // OCCULT CODEX & AI ORACLE
                OutlinedButton(
                    onClick = {
                        viewModel.audioHaptics.playRadioStatic()
                        viewModel.navigateTo(GameScreen.CODEX_ORACLE)
                    },
                    modifier = Modifier
                        .testTag("codex_button")
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = VaultSurfaceCard.copy(alpha = 0.8f)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            listOf(DarkEther.copy(alpha = 0.7f), BrassGold.copy(alpha = 0.7f))
                        )
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Codex AI",
                            tint = DarkEther,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = GameStrings.get("codex_lore", lang),
                            color = DarkEther,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Secondary buttons: Lore and Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { showLoreDialog = true },
                        modifier = Modifier.testTag("lore_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Game Lore GDD",
                            tint = BrassGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GDD & Tarix",
                            color = BrassGold,
                            fontSize = 13.sp
                        )
                    }

                    TextButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("reset_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Progress",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Qayta Boshlash",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // Language Dialog
    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            containerColor = VaultSurface,
            title = {
                Text("Tilni Tanlang / Select Language", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "uz" to "Oʻzbekcha 🇺🇿",
                        "en" to "English 🇬🇧",
                        "ru" to "Русский 🇷🇺",
                        "tr" to "Türkçe 🇹🇷"
                    ).forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (lang == code) BloodDark else VaultSurfaceCard)
                                .clickable {
                                    viewModel.setLanguage(code)
                                    showLangDialog = false
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                color = if (lang == code) BloodGlow else TextPrimary,
                                fontWeight = if (lang == code) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLangDialog = false }) {
                    Text("OK", color = BloodGlow)
                }
            }
        )
    }

    // Lore & GDD Dialog
    if (showLoreDialog) {
        AlertDialog(
            onDismissRequest = { showLoreDialog = false },
            containerColor = VaultSurface,
            title = {
                Text(
                    text = "Zulmat Shartnomasi — Master GDD",
                    color = BloodGlow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. KUNDUZGI FAZA (06:00 - 18:00):",
                        color = BrassGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Vayrona xaritalarni skanerlash, Suyuq Qon va Qora Eter yigʻish. Blood Con quvurlarini bazaga payvandlash va turretlarni tayyorlash.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "2. OLIY ZOM: SOYA BANKI & QARZ HORRORI:",
                        color = BrassGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Oʻlmas aristokrat bankirdan resurs va oʻq-dori qarzga olinadi. Agar muddati oʻtsa, oʻz koʻzingiz yoki qoʻlingiz garovga olinadi (koʻrish va otish qobiliyati buziladi).",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "3. BIO-GROWTH POD (ORGAN OʻSTIRISH):",
                        color = BrassGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Biomassa va qon evaziga garovga ketgan aʻzolarni oʻstirib, oʻz tanangizni qayta tiklaysiz.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "4. TUNGI BOSQIN (18:00 - 06:00):",
                        color = BloodCrimson,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Birinchi shaxs koʻrinishida qonli miltiqdan otish, Blood Con turretlarini boshqarish, safran tilsimlari bilan jinlarni muzlatish va aql-idrokni saqlash.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLoreDialog = false }) {
                    Text("TUSHUNDIM", color = BloodGlow)
                }
            }
        )
    }

    // Reset Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = VaultSurface,
            title = {
                Text("Oʻyinni Tozalash", color = BloodCrimson, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Hamma resurslar, qarzlar va saqlangan natijalar oʻchirilib, oʻyin 1-kundan boshlanadi. Davom etasizmi?",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetGame()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BloodCrimson)
                ) {
                    Text("TOZALASH", color = TextPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("BEKOR QILISH", color = TextSecondary)
                }
            }
        )
    }
}
