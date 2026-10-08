package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CodexEntry
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
fun CodexOracleScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.gameProfile.collectAsState()
    val codexEntries by viewModel.codexEntries.collectAsState()
    val isLoading by viewModel.codexLoading.collectAsState()
    val lastResult by viewModel.lastCodexResult.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("JINN") }

    val sampleQueries = listOf(
        "Ifrit zaifligi nima va qanday toʻxtatiladi?",
        "Oliy Zom qarz shartnomasi va Kollektorlar reydi",
        "Marid qanday qilib Sanityni yemirishi mumkin?",
        "Alvasti va jinlar oʻzbek mifologiyasida qanday tasvirlangan?",
        "Blood Con gidravlik quvurlarining eng samarali zanjiri"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidObsidian)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VaultSurface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(GameScreen.MAIN_MENU) },
                modifier = Modifier.testTag("codex_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrassGold
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = DarkEther,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OKULT KODEKS & AI ORACLE",
                        color = BrassGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
                Text(
                    text = "Gemini 3.5 Flash & Google Search Grounding",
                    color = DarkEther,
                    fontSize = 11.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "JINN" to "Jinlar Mifologiyasi",
                        "NECRO_FINANCE" to "Oliy Zom Qonunlari",
                        "BLOOD_CON" to "Blood Con Alkimyosi"
                    ).forEach { (cat, label) ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) BloodCrimson else VaultSurfaceCard)
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Search / Query Input
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkEther, VaultBorder)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "GRIMOIREGA SAVOL BERISH:",
                            color = DarkEther,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Masalan: Ifrit qanday zaiflikka ega?", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("codex_search_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkEther,
                                unfocusedBorderColor = VaultBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    viewModel.queryCodexAI(searchQuery, selectedCategory)
                                }
                            },
                            enabled = !isLoading && searchQuery.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().testTag("codex_ask_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BloodDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = BloodGlow, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Qadimiy tilsimlar skanerlanmoqda...", color = BloodGlow, fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null, tint = BloodGlow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("KODEKSDAN SOʻRASH (Google Search Grounding)", color = BloodGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Quick Prompt Chips
            item {
                Text("TAVSIYA ETILGAN SAVOLLAR:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sampleQueries) { q ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(VaultSurfaceCard)
                                .border(1.dp, VaultBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    searchQuery = q
                                    viewModel.queryCodexAI(q, selectedCategory)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(q, color = BrassGold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Live AI Grounded Result Card
            if (lastResult != null) {
                item {
                    val result = lastResult!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BloodCrimson, DarkEther))),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DarkEther, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI JAVOBI & GROUNDING TAHLILI",
                                    color = DarkEther,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = result.content,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            if (result.searchSources.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("🌐 Qidiruv Manbalari (Google Search Grounding):", color = BrassGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                result.searchSources.forEach { source ->
                                    Text("• $source", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Database Stored Codex Entries
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text("KODEKS ENS IKLOPEDIYASI (Room Database):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            items(codexEntries) { entry ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = VaultSurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(VaultBorder, VaultBorder))),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(entry.title, color = BrassGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(entry.category, color = DarkEther, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(entry.detailedLore, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)

                        if (!entry.groundingSource.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Manba: ${entry.groundingSource}", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
