package com.example.childtimer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.childtimer.model.CadranDisplayMode
import com.example.childtimer.model.TimerThemeColor
import com.example.childtimer.model.TimerThemeColors
import kotlinx.coroutines.launch

@Composable
fun ParentSettingsSheet(
    isOpen: Boolean,
    displayMode: CadranDisplayMode,
    themeColor: TimerThemeColor,
    isSoundEnabled: Boolean,
    onDisplayModeChange: (CadranDisplayMode) -> Unit,
    onThemeColorChange: (TimerThemeColor) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .testTag("settings_scrim")
    ) {
        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(320)) + fadeIn(tween(250)),
            exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(280)) + fadeOut(tween(200)),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(360.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
                    .background(Color(0xFF181B1E))
                    .border(1.dp, Color(0xFF2C3238), RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* absorb clicks */ }
                    .testTag("parent_settings_panel")
            ) {
                val scrollState = rememberScrollState()
                val canScrollDown = scrollState.canScrollForward

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 22.dp, vertical = 20.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Réglages & Aide",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF282D33))
                                .testTag("close_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 1. Cadran Style Selector
                    Text(
                        text = "STYLE DU CADRAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E9BA6),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF24282E))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Animals option
                        val isAnimals = displayMode == CadranDisplayMode.ANIMALS
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAnimals) themeColor.primaryColor else Color.Transparent)
                                .clickable { onDisplayModeChange(CadranDisplayMode.ANIMALS) }
                                .padding(vertical = 10.dp)
                                .testTag("mode_animals_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🦁 Animaux",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isAnimals) Color.White else Color(0xFFB0B9C2)
                            )
                        }

                        // Numbers option
                        val isNumbers = displayMode == CadranDisplayMode.NUMBERS
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isNumbers) themeColor.primaryColor else Color.Transparent)
                                .clickable { onDisplayModeChange(CadranDisplayMode.NUMBERS) }
                                .padding(vertical = 10.dp)
                                .testTag("mode_numbers_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔢 Chiffres",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isNumbers) Color.White else Color(0xFFB0B9C2)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Color Palette Selector
                    Text(
                        text = "COULEUR DU DISQUE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E9BA6),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimerThemeColors.all.forEach { colorOption ->
                            val isSelected = colorOption.id == themeColor.id
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(colorOption.primaryColor)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onThemeColorChange(colorOption) }
                                    .testTag("color_choice_${colorOption.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sélectionné",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // 3. Sound Control
                    Text(
                        text = "SON & ALERTES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E9BA6),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF24282E))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = null,
                                tint = if (isSoundEnabled) themeColor.primaryColor else Color(0xFF75818C),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sons & Carillon",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isSoundEnabled) "Activé (priorité alarme)" else "Mode silencieux",
                                    fontSize = 11.sp,
                                    color = Color(0xFF8E9BA6)
                                )
                            }
                        }

                        Switch(
                            checked = isSoundEnabled,
                            onCheckedChange = { onSoundToggle(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = themeColor.primaryColor,
                                uncheckedThumbColor = Color(0xFF9EAAB5),
                                uncheckedTrackColor = Color(0xFF384049)
                            ),
                            modifier = Modifier.testTag("sound_settings_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // 4. Guide / Aide pour les parents
                    Text(
                        text = "AIDE & ASTUCES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E9BA6),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF22272E))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Text("⏱️", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Column {
                                    Text(
                                        text = "Réinitialiser pendant le décompte",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFECEFF1)
                                    )
                                    Text(
                                        text = "Pour stopper et réinitialiser un timer en cours, maintenez appuyé le bouton au centre du cadran pendant 3 secondes.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF9EABB8),
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Text("🔒", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                                Column {
                                    Text(
                                        text = "Sécurité enfant",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFECEFF1)
                                    )
                                    Text(
                                        text = "Ce volet de réglages est automatiquement masqué et inaccessible pendant le compte à rebours.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF9EABB8),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Extra space at bottom to allow comfortable scrolling past the floating chip
                    Spacer(modifier = Modifier.height(52.dp))
                }

                // Subtle bottom gradient fade indicating more content
                if (canScrollDown) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF181B1E))
                                )
                            )
                    )
                }

                // Discreet floating chip hinting that more content / help is available below
                AnimatedVisibility(
                    visible = canScrollDown,
                    enter = fadeIn(tween(250)) + slideInVertically { it / 2 },
                    exit = fadeOut(tween(200)) + slideOutVertically { it / 2 },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF252A30).copy(alpha = 0.95f))
                            .border(1.dp, Color(0xFF3B434C), RoundedCornerShape(20.dp))
                            .clickable {
                                scope.launch {
                                    scrollState.animateScrollTo(scrollState.maxValue)
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("scroll_help_indicator"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "Aide & Astuces",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFCFD8DC)
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Faire défiler vers l'aide",
                                tint = themeColor.primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
