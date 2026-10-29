package com.example.childtimer.ui.cadran

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.childtimer.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.childtimer.model.CadranAnimal
import com.example.childtimer.model.CadranAnimals
import com.example.childtimer.model.CadranDisplayMode
import com.example.childtimer.model.TimerThemeColor
import com.example.childtimer.ui.theme.DialBorderColor
import com.example.childtimer.ui.theme.DialTrackColor
import com.example.childtimer.ui.theme.SoftCharcoal
import com.example.childtimer.ui.theme.WarmWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimalCadran(
    selectedMinutes: Int?,
    remainingRatio: Float, // 1f down to 0f when running, 1f when selected
    isRunning: Boolean,
    isFinished: Boolean,
    displayMode: CadranDisplayMode,
    themeColor: TimerThemeColor,
    onSelectAnimal: (CadranAnimal) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var holdProgress by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = modifier.testTag("cadran_animal_container"),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val diameterDp = minOf(maxWidth, maxHeight)
        val diameterPx = with(density) { diameterDp.toPx() }
        val centerPx = Offset(diameterPx / 2f, diameterPx / 2f)

        // Geometry radii
        val animalOrbitRadiusPx = diameterPx * 0.40f
        val discRadiusPx = diameterPx * 0.285f
        val knobRadiusPx = diameterPx * 0.082f
        val knobDiameterDp = with(density) { (knobRadiusPx * 2.2f).toDp() }
        val animalSizeDp = diameterDp * 0.165f

        // Animated sweep angle for smooth transitions when selecting or running
        val targetSweepAngle = when {
            isFinished -> 0f
            isRunning -> (selectedMinutes ?: 0) * 6f * remainingRatio
            selectedMinutes != null -> selectedMinutes * 6f
            else -> 0f
        }

        val animatedSweepAngle by animateFloatAsState(
            targetValue = targetSweepAngle,
            animationSpec = if (isRunning) tween(durationMillis = 100, easing = LinearEasing)
                           else spring(dampingRatio = 0.75f, stiffness = 400f),
            label = "cadranSweepAngle"
        )

        // Gentle breathing animation when finished
        val infiniteTransition = rememberInfiniteTransition(label = "celebration")
        val celebrationScale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "celebrationScale"
        )

        // Main Drawing Canvas: Dial Background, Clock Ticks, and Colored Sector
        Canvas(modifier = Modifier.size(diameterDp)) {
            // 1. Outer circular clock face plate (Cream background behind animals and dial)
            drawCircle(
                color = Color(0xFFFFFDF8),
                radius = diameterPx * 0.49f,
                center = centerPx
            )
            // Outer circular clock rim
            drawCircle(
                color = DialBorderColor,
                radius = diameterPx * 0.49f,
                center = centerPx,
                style = Stroke(width = with(density) { 3.dp.toPx() })
            )

            // 2. Inner Dial Base (Cream circular face)
            drawCircle(
                color = WarmWhite,
                radius = discRadiusPx,
                center = centerPx
            )

            // Dial border rim
            drawCircle(
                color = DialBorderColor,
                radius = discRadiusPx,
                center = centerPx,
                style = Stroke(width = with(density) { 2.dp.toPx() })
            )

            // 3. Subtle tick dots around the disc circumference (NO NUMBERS on the inner disk)
            for (min in 0 until 60) {
                val isFiveMin = min % 5 == 0
                val angleDeg = min * 6f - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val dotRadius = if (isFiveMin) with(density) { 2.8.dp.toPx() } else with(density) { 1.2.dp.toPx() }
                val dotDist = discRadiusPx - with(density) { 7.dp.toPx() }
                val dotColor = if (isFiveMin) SoftCharcoal.copy(alpha = 0.45f) else SoftCharcoal.copy(alpha = 0.2f)

                val dotX = centerPx.x + (dotDist * cos(angleRad)).toFloat()
                val dotY = centerPx.y + (dotDist * sin(angleRad)).toFloat()

                drawCircle(
                    color = dotColor,
                    radius = dotRadius,
                    center = Offset(dotX, dotY)
                )
            }

            // 4. Colored Sector: Selected theme color from 12 o'clock (-90°) clockwise
            if (animatedSweepAngle > 0.5f) {
                val sectorRadius = discRadiusPx - with(density) { 1.dp.toPx() }
                val sectorTopLeft = Offset(centerPx.x - sectorRadius, centerPx.y - sectorRadius)
                val sectorSize = Size(sectorRadius * 2, sectorRadius * 2)

                // Filled wedge
                drawArc(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            themeColor.softColor,
                            themeColor.primaryColor
                        ),
                        center = centerPx,
                        radius = sectorRadius
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedSweepAngle,
                    useCenter = true,
                    topLeft = sectorTopLeft,
                    size = sectorSize
                )

                // Subtle edge line at the start (12h)
                drawLine(
                    color = themeColor.lightColor.copy(alpha = 0.8f),
                    start = centerPx,
                    end = Offset(centerPx.x, centerPx.y - sectorRadius),
                    strokeWidth = with(density) { 2.dp.toPx() },
                    cap = StrokeCap.Round
                )

                // Subtle edge line at the active sweep boundary
                val sweepRad = Math.toRadians((animatedSweepAngle - 90.0))
                val edgeX = centerPx.x + (sectorRadius * cos(sweepRad)).toFloat()
                val edgeY = centerPx.y + (sectorRadius * sin(sweepRad)).toFloat()
                drawLine(
                    color = themeColor.lightColor.copy(alpha = 0.8f),
                    start = centerPx,
                    end = Offset(edgeX, edgeY),
                    strokeWidth = with(density) { 2.5.dp.toPx() },
                    cap = StrokeCap.Round
                )
            }

            // 5. Center Knob Shading (Tactile circular knob)
            drawCircle(
                color = Color.Black.copy(alpha = 0.06f),
                radius = knobRadiusPx + with(density) { 4.dp.toPx() },
                center = centerPx + Offset(0f, with(density) { 3.dp.toPx() })
            )
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(WarmWhite, DialTrackColor),
                    start = Offset(centerPx.x - knobRadiusPx, centerPx.y - knobRadiusPx),
                    end = Offset(centerPx.x + knobRadiusPx, centerPx.y + knobRadiusPx)
                ),
                radius = knobRadiusPx,
                center = centerPx
            )
            drawCircle(
                color = DialBorderColor.copy(alpha = 0.7f),
                radius = knobRadiusPx,
                center = centerPx,
                style = Stroke(width = with(density) { 1.5.dp.toPx() })
            )
        }

        // Center Knob Interactive Area:
        // - In finished mode: 1 tap restarts immediately!
        // - During running / selecting: 3 seconds hold triggers reset
        Box(
            modifier = Modifier
                .size(if (isFinished) animalSizeDp * 1.45f else knobDiameterDp)
                .pointerInput(isFinished, isRunning, selectedMinutes) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        var holdCompleted = false

                        val holdJob = scope.launch {
                            val totalHoldMs = 3000L
                            val stepMs = 50L
                            var elapsed = 0L
                            while (elapsed < totalHoldMs) {
                                delay(stepMs)
                                elapsed += stepMs
                                holdProgress = (elapsed.toFloat() / totalHoldMs).coerceIn(0f, 1f)
                            }
                            holdCompleted = true
                            holdProgress = 0f
                            onResetClick() // 3 seconds hold resets!
                        }

                        val up = waitForUpOrCancellation()
                        holdJob.cancel()
                        holdProgress = 0f

                        if (up != null && !holdCompleted) {
                            if (isFinished) {
                                onResetClick() // Single tap restarts when finished
                            }
                        }
                    }
                }
                .testTag("center_knob_interaction"),
            contentAlignment = Alignment.Center
        ) {
            // Visual circle ring when holding for 3 seconds to reset
            if (holdProgress > 0.04f) {
                Canvas(modifier = Modifier.fillMaxSize().padding(2.dp)) {
                    drawArc(
                        color = themeColor.primaryColor,
                        startAngle = -90f,
                        sweepAngle = holdProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = with(density) { 3.5.dp.toPx() }, cap = StrokeCap.Round)
                    )
                }
            }

            // Center Knob Content: celebration mascot fox and replay affordance when finished
            if (isFinished) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(celebrationScale)
                        .clip(CircleShape)
                        .background(themeColor.lightColor)
                        .border(2.5.dp, themeColor.primaryColor, CircleShape)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_fox),
                        contentDescription = "Recommencer",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(0.85f)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(themeColor.primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recommencer",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 12 Items placed regularly around the dial (Ours/5 at 1h, ..., Lion/60 at 12h)
        CadranAnimals.list.forEach { animal ->
            val isSelected = (selectedMinutes == animal.minutes)
            val isSelectionInteractive = !isRunning && !isFinished

            // Angle in radians (0° = 12 o'clock, clockwise)
            val angleRad = Math.toRadians(animal.clockAngleDeg.toDouble())
            val animalCenterXPx = centerPx.x + (animalOrbitRadiusPx * sin(angleRad)).toFloat()
            val animalOffsetYPx = centerPx.y - (animalOrbitRadiusPx * cos(angleRad)).toFloat()

            val animalOffsetX = with(density) { (animalCenterXPx - diameterPx / 2f).toDp() }
            val animalOffsetY = with(density) { (animalOffsetYPx - diameterPx / 2f).toDp() }

            val itemScale by animateFloatAsState(
                targetValue = when {
                    isSelected && !isRunning -> 1.18f
                    isSelected && isRunning -> 1.08f
                    else -> 1.0f
                },
                animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
                label = "item_scale_${animal.position}"
            )

            Box(
                modifier = Modifier
                    .offset(x = animalOffsetX, y = animalOffsetY)
                    .size(animalSizeDp)
                    .scale(itemScale)
                    .testTag("item_button_${animal.position}"),
                contentAlignment = Alignment.Center
            ) {
                // If selected: gentle glowing soft circular halo in theme color
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(1.15f)
                            .clip(CircleShape)
                            .background(themeColor.lightColor.copy(alpha = 0.85f))
                            .border(2.dp, themeColor.primaryColor, CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable(
                            enabled = isSelectionInteractive,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onSelectAnimal(animal)
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (displayMode == CadranDisplayMode.ANIMALS) {
                        Image(
                            painter = painterResource(id = animal.drawableRes),
                            contentDescription = animal.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Display numbers: 5, 10, 15, ..., 60 in warm, clear rounded badges
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.92f)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) themeColor.primaryColor
                                    else Color(0xFFF7F2EA)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) themeColor.darkColor else DialBorderColor,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${animal.minutes}",
                                fontSize = if (animal.minutes >= 10) 18.sp else 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else SoftCharcoal
                            )
                        }
                    }
                }
            }
        }
    }
}
