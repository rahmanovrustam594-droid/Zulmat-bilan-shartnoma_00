package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameProfile
import com.example.ui.BattleResult
import com.example.ui.GameStrings
import com.example.ui.GameViewModel
import com.example.ui.theme.BloodCrimson
import com.example.ui.theme.BloodDark
import com.example.ui.theme.BloodGlow
import com.example.ui.theme.BrassAged
import com.example.ui.theme.BrassGold
import com.example.ui.theme.DarkEther
import com.example.ui.theme.SanityPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceCard
import com.example.ui.theme.VoidObsidian

@Composable
fun NightDefenseScreen(
    viewModel: GameViewModel,
    profile: GameProfile,
    modifier: Modifier = Modifier
) {
    val lang = profile.language
    val battleState by viewModel.nightBattle.collectAsState()
    val systemMsg by viewModel.systemMessage.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidObsidian)
    ) {
        // Base Defense Background Image
        Image(
            painter = painterResource(id = R.drawable.night_defense_bg),
            contentDescription = "Night Defense Battle",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Night Filter Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            VoidObsidian.copy(alpha = 0.55f),
                            VoidObsidian.copy(alpha = 0.40f),
                            VoidObsidian.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // PLEDGED EYE VIGNETTE EFFECT (Vision Impairment)
        if (profile.pledgedEye) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                VoidObsidian.copy(alpha = 0.88f),
                                VoidObsidian
                            ),
                            radius = 600f
                        )
                    )
            )
        }

        // INTERACTIVE 3D FIRST-PERSON CANVAS
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("battle_canvas")
                .pointerInput(battleState.isRunning) {
                    detectTapGestures(
                        onTap = { offset ->
                            val normX = offset.x / size.width
                            val normY = offset.y / size.height
                            viewModel.shootAt(normX, normY)
                        }
                    )
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val yawRad = Math.toRadians(battleState.cameraYaw.toDouble()).toFloat()
            val pitchOffset = (battleState.cameraPitch / 25f) * (canvasH * 0.18f)

            // 3D HORIZON & DISTANT ANOMALY FOG
            val horizonY = (canvasH * 0.45f) + pitchOffset
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xFF140D20).copy(alpha = 0.6f), Color(0xFF09060E)),
                    startY = 0f,
                    endY = horizonY
                ),
                topLeft = Offset(0f, 0f),
                size = Size(canvasW, horizonY)
            )

            // 3D PERSPECTIVE GROUND GRID (Distant gothic floor converging to horizon)
            val groundStartY = horizonY.coerceAtLeast(0f)
            val groundHeight = (canvasH - groundStartY).coerceAtLeast(1f)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D0B14), Color(0xFF191224), VoidObsidian),
                    startY = groundStartY,
                    endY = canvasH
                ),
                topLeft = Offset(0f, groundStartY),
                size = Size(canvasW, groundHeight)
            )

            // 3D Perspective Lines converging to vanishing point
            val vanishingX = canvasW * 0.5f - (yawRad * canvasW * 0.8f)
            val gridLines = 14
            for (i in -gridLines..gridLines) {
                val bottomX = canvasW * 0.5f + (i * canvasW * 0.12f) - (yawRad * canvasW * 1.4f)
                drawLine(
                    color = DarkEther.copy(alpha = 0.18f),
                    start = Offset(vanishingX, horizonY),
                    end = Offset(bottomX, canvasH),
                    strokeWidth = 1.2f
                )
            }

            // 3D Depth Rings / Distance Markers (10m, 20m, 30m)
            val zDistances = listOf(30f, 20f, 10f, 4f)
            for (z in zDistances) {
                val zScreenY = horizonY + (groundHeight * (4f / z).coerceIn(0.05f, 0.95f))
                drawLine(
                    color = BloodDark.copy(alpha = 0.35f),
                    start = Offset(0f, zScreenY),
                    end = Offset(canvasW, zScreenY),
                    strokeWidth = 1.5f
                )
            }

            // 3D Concrete Fortress Barricade at bottom foreground (Z = 2m)
            val barrierTopY = canvasH * 0.78f + (pitchOffset * 0.4f)
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(VaultSurfaceCard, VaultSurface, Color.Black)
                ),
                topLeft = Offset(0f, barrierTopY),
                size = Size(canvasW, canvasH - barrierTopY)
            )
            // Barricade iron rim
            drawLine(
                brush = Brush.horizontalGradient(listOf(BloodDark, BloodCrimson, BrassGold, BloodDark)),
                start = Offset(0f, barrierTopY),
                end = Offset(canvasW, barrierTopY),
                strokeWidth = 5f
            )

            // 3D Blood Con Turret Tracers firing towards closest demon
            if (battleState.bloodTurretActive && battleState.demons.isNotEmpty()) {
                val turretPos = Offset(canvasW * 0.5f, barrierTopY + 15f)
                val targetDemon = battleState.demons.minByOrNull { it.worldZ } ?: battleState.demons.first()
                val tDx = targetDemon.worldX
                val tDz = targetDemon.worldZ
                val tXCam = tDx * kotlin.math.cos(yawRad) - tDz * kotlin.math.sin(yawRad)
                val tZCam = tDx * kotlin.math.sin(yawRad) + tDz * kotlin.math.cos(yawRad)

                if (tZCam > 1f) {
                    val scaleT = (canvasH * 0.65f) / tZCam
                    val targetScreenX = canvasW * 0.5f + tXCam * scaleT
                    val targetScreenY = horizonY + (1.2f * scaleT)

                    drawLine(
                        color = BloodGlow.copy(alpha = 0.85f),
                        start = turretPos,
                        end = Offset(targetScreenX, targetScreenY),
                        strokeWidth = 3f * pulse
                    )
                    drawCircle(
                        color = BloodGlow,
                        radius = 8f * pulse,
                        center = turretPos
                    )
                }
            }

            // DRAW 3D DEMONS (Sorted by depth: furthest first, nearest last for correct Z-ordering)
            val sortedDemons = battleState.demons.sortedByDescending { it.worldZ }
            for (demon in sortedDemons) {
                val dx = demon.worldX
                val dz = demon.worldZ
                val xCam = dx * kotlin.math.cos(yawRad) - dz * kotlin.math.sin(yawRad)
                val zCam = dx * kotlin.math.sin(yawRad) + dz * kotlin.math.cos(yawRad)

                // Only render if demon is in front of the camera (zCam > 0.8m)
                if (zCam > 0.8f) {
                    val scale = (canvasH * 0.62f) / zCam
                    val screenX = canvasW * 0.5f + xCam * scale
                    val screenY = horizonY + (1.0f * scale)

                    // 3D Perspective Radius: scales up as demon gets closer!
                    val baseRadius = when (demon.type) {
                        "COLLECTOR" -> 1.4f
                        "IFRIT" -> 1.2f
                        else -> 1.0f
                    }
                    val demonRadius = (baseRadius * scale).coerceIn(12f, 90f)

                    val demonColor = when (demon.type) {
                        "COLLECTOR" -> BrassGold
                        "IFRIT" -> BloodGlow
                        else -> Color(0xFFBA68C8)
                    }

                    val demonPos = Offset(screenX, screenY)

                    // Saffron Freeze Aura in 3D
                    if (battleState.freezeTimerSeconds > 0) {
                        drawCircle(
                            color = DarkEther,
                            radius = demonRadius + 8f,
                            center = demonPos,
                            style = Stroke(width = 3.5f)
                        )
                    }

                    // Shadow Silhouette & Volumetric Glow
                    val depthAlpha = (1f - (zCam / 45f)).coerceIn(0.35f, 1f)
                    drawCircle(
                        color = Color.Black.copy(alpha = depthAlpha * 0.88f),
                        radius = demonRadius,
                        center = demonPos
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                demonColor.copy(alpha = depthAlpha),
                                demonColor.copy(alpha = depthAlpha * 0.35f),
                                Color.Transparent
                            ),
                            center = demonPos,
                            radius = demonRadius
                        ),
                        radius = demonRadius * 0.85f,
                        center = demonPos
                    )

                    // Glowing Demonic Eyes (perspective spaced)
                    val eyeSpacing = demonRadius * 0.28f
                    val eyeSize = (demonRadius * 0.14f).coerceIn(3f, 8f)
                    drawCircle(
                        color = if (demon.type == "IFRIT") Color.Yellow else Color.Red,
                        radius = eyeSize,
                        center = Offset(demonPos.x - eyeSpacing, demonPos.y - demonRadius * 0.15f)
                    )
                    drawCircle(
                        color = if (demon.type == "IFRIT") Color.Yellow else Color.Red,
                        radius = eyeSize,
                        center = Offset(demonPos.x + eyeSpacing, demonPos.y - demonRadius * 0.15f)
                    )

                    // 3D Distance Indicator Label & Health Bar
                    val barW = (demonRadius * 2.2f).coerceIn(40f, 110f)
                    val barH = (demonRadius * 0.25f).coerceIn(4f, 8f)
                    val barX = demonPos.x - barW / 2
                    val barY = demonPos.y - demonRadius - 16f

                    drawRect(
                        color = Color.Black.copy(alpha = 0.75f),
                        topLeft = Offset(barX, barY),
                        size = Size(barW, barH)
                    )
                    val hpPct = demon.hp.toFloat() / demon.maxHp.toFloat()
                    drawRect(
                        color = if (demon.type == "COLLECTOR") BrassGold else BloodCrimson,
                        topLeft = Offset(barX, barY),
                        size = Size(barW * hpPct, barH)
                    )
                }
            }

            // 3D HIT IMPACT BURST
            battleState.lastHitPosition?.let { (hx, hy) ->
                val hitOffset = Offset(hx * canvasW, hy * canvasH)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.White, Color.Yellow, BloodGlow, Color.Transparent),
                        center = hitOffset,
                        radius = 34f
                    ),
                    radius = 34f,
                    center = hitOffset
                )
            }

            // 3D FIRST-PERSON BLOOD SHOTGUN MODEL (Rendered at bottom right)
            val recoilOffset = if (battleState.muzzleFlash) -24f else 0f
            val gunBaseX = canvasW * 0.72f
            val gunBaseY = canvasH * 0.85f + recoilOffset

            // Gun Barrel (Cast iron)
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF212121), Color(0xFF424242), Color(0xFF1B1B1B))
                ),
                topLeft = Offset(gunBaseX - 30f, gunBaseY - 140f),
                size = Size(60f, 150f)
            )

            // Brass Receiver & Stock
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(BrassAged, BrassGold, BrassAged)
                ),
                topLeft = Offset(gunBaseX - 40f, gunBaseY + 10f),
                size = Size(80f, 90f)
            )

            // Glowing Blood Conduit Tube attached to shotgun
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(BloodDark, BloodGlow, BloodDark)
                ),
                topLeft = Offset(gunBaseX - 10f, gunBaseY - 110f),
                size = Size(20f, 110f)
            )

            // 3D Muzzle Flash Burst when Fired
            if (battleState.muzzleFlash) {
                val muzzlePos = Offset(gunBaseX, gunBaseY - 150f)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.White, Color.Yellow, BloodGlow, Color.Transparent),
                        center = muzzlePos,
                        radius = 90f
                    ),
                    radius = 90f,
                    center = muzzlePos
                )
            }

            // 3D CENTER CROSSHAIR
            val crosshairX = canvasW * 0.5f
            val crosshairY = canvasH * 0.5f
            val chSize = 14f

            drawLine(
                color = BloodGlow.copy(alpha = 0.85f),
                start = Offset(crosshairX - chSize, crosshairY),
                end = Offset(crosshairX + chSize, crosshairY),
                strokeWidth = 2.5f
            )
            drawLine(
                color = BloodGlow.copy(alpha = 0.85f),
                start = Offset(crosshairX, crosshairY - chSize),
                end = Offset(crosshairX, crosshairY + chSize),
                strokeWidth = 2.5f
            )
            drawCircle(
                color = BloodGlow.copy(alpha = 0.5f),
                radius = 6f,
                center = Offset(crosshairX, crosshairY),
                style = Stroke(width = 1.5f)
            )
        }

        // HUD TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VaultSurface.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                    .border(1.dp, VaultBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Countdown Timer to Dawn (06:00)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Nightlight,
                            contentDescription = null,
                            tint = DarkEther,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tonggacha: ${battleState.timeLeftSeconds}s",
                            color = DarkEther,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = "Yoʻq qilingan: ${battleState.slainCount} ta",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Base Health
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = BloodCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Baza: ${profile.baseHp}%",
                            color = BloodCrimson,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    LinearProgressIndicator(
                        progress = { profile.baseHp / 100f },
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BloodCrimson,
                        trackColor = VaultBorder
                    )
                }

                // Sanity
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Psychology,
                            contentDescription = null,
                            tint = SanityPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile.sanity}%",
                            color = SanityPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "Aql-idrok",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Debuff Notice
            if (profile.pledgedEye) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BloodDark.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = BloodGlow, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = GameStrings.get("eye_pledged_warning", lang),
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // BOTTOM CONTROLS & WEAPON ACTIONS
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, VaultSurface.copy(alpha = 0.95f), VoidObsidian)
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Ammo & Reload Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SHOTGUN: ",
                        color = BrassGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (battleState.isReloading) "OʻQLANMOQDA..." else "${battleState.magazineAmmo} / ${battleState.maxMagazine}",
                        color = if (battleState.magazineAmmo == 0) BloodCrimson else TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(Zaxira: ${profile.ammo})",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Reload button
                Button(
                    onClick = { viewModel.reloadGun() },
                    enabled = !battleState.isReloading && profile.ammo > 0 && battleState.magazineAmmo < battleState.maxMagazine,
                    colors = ButtonDefaults.buttonColors(containerColor = VaultSurfaceCard),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("reload_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = BrassGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("OʻQLASH", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Action Spells / Abilities Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Safran Ritual Circle (Freeze Demons)
                Button(
                    onClick = { viewModel.castRitualCircle() },
                    modifier = Modifier.weight(1f).testTag("ritual_circle_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrassAged),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrassGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Safran Tilsimi (25 Efir)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Ether Flare (Sanity Restore)
                Button(
                    onClick = { viewModel.igniteEtherFlare() },
                    modifier = Modifier.weight(1f).testTag("ether_flare_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkEther),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = VoidObsidian, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Efir Mashʻalasi (15 Efir)", color = VoidObsidian, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 3D Camera Pan & Fire Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.rotateCamera(-14f, 0f) },
                    modifier = Modifier.weight(1f).height(44.dp).testTag("look_left_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = VaultSurfaceCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("⮜ 3D Chapga", color = BrassGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.shootAt(0.5f, 0.5f) },
                    modifier = Modifier.weight(1.3f).height(48.dp).testTag("fire_shotgun_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BloodCrimson),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("OTISH", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                }

                Button(
                    onClick = { viewModel.rotateCamera(14f, 0f) },
                    modifier = Modifier.weight(1f).height(44.dp).testTag("look_right_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = VaultSurfaceCard),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("3D Oʻngga ⮞", color = BrassGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Big Tap to Shoot instruction
            Text(
                text = "🎯 Ekranga bosib toʻgʻridan-toʻgʻri nishonga oling yoki tugmalar orqali 3D atrofga qarang!",
                color = TextSecondary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // BATTLE END SUMMARY DIALOG
    if (battleState.battleResult != null) {
        val isVictory = battleState.battleResult == BattleResult.VICTORY
        AlertDialog(
            onDismissRequest = {},
            containerColor = VaultSurface,
            title = {
                Text(
                    text = if (isVictory) "TONG OTDI! OMON QOLDINGIZ!" else "BAZA VAYRON BOʻLDI!",
                    color = if (isVictory) BrassGold else BloodCrimson,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isVictory)
                            "Siz #${profile.dayNumber}-kechani muvaffaqiyatli himoya qildingiz. Yangi kun boshlandi!"
                        else
                            "Jinlar toʻsigʻingizni yorib oʻtdi. Oliy Zom sizni jazolab qutqardi (+100 Eter qarz jarimasi).",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Yoʻq qilingan jinlar: ${battleState.slainCount} ta", color = BloodGlow, fontSize = 12.sp)
                    Text("• Qabul qilingan zarar: ${battleState.damageTaken} HP", color = TextSecondary, fontSize = 12.sp)
                    if (isVictory) {
                        Text("• Mukofot: +${battleState.slainCount * 15} Qon, +${battleState.slainCount * 10} Biomassa", color = DarkEther, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.returnToCamp() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isVictory) BloodDark else VaultBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("dawn_continue_button")
                ) {
                    Text("KUNDUZGI BAZAGA QAYTISH ➔", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
