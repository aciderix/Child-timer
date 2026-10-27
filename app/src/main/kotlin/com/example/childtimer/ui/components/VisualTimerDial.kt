package com.example.childtimer.ui.components

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.childtimer.model.AnimalCompanion
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VisualTimerDial(
    remainingSeconds: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    isPaused: Boolean,
    animal: AnimalCompanion,
    modifier: Modifier = Modifier,
    onDialTap: (() -> Unit)? = null
) {
    val progress = if (totalSeconds > 0) {
        (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Breathing pulse for the animal buddy when timer is actively counting
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by if (isRunning && !isPaused) {
        infiniteTransition.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "animalScale"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "staticScale"
        )
    }

    val arcColor = animal.primaryColor
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .testTag("timer_dial_box"),
            contentAlignment = Alignment.Center
        ) {
            // Background & Progress Dial Canvas
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                val strokeWidth = 22.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                // 1. Background ring track
                drawCircle(
                    color = trackColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // 2. Subtle clock ticks (12 hour / 5-min intervals)
                val tickCount = 12
                val tickLength = 10.dp.toPx()
                for (i in 0 until tickCount) {
                    val angleDeg = (i * 360f / tickCount) - 90f
                    val angleRad = Math.toRadians(angleDeg.toDouble())
                    val innerR = radius - (strokeWidth / 2) - 4.dp.toPx()
                    val outerR = innerR - tickLength

                    val startX = center.x + (innerR * cos(angleRad)).toFloat()
                    val startY = center.y + (innerR * sin(angleRad)).toFloat()
                    val endX = center.x + (outerR * cos(angleRad)).toFloat()
                    val endY = center.y + (outerR * sin(angleRad)).toFloat()

                    drawLine(
                        color = arcColor.copy(alpha = 0.35f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // 3. Colored Sweep Pie Sector (Time Timer visual sector fill)
                val sweepAngle = progress * 360f
                if (sweepAngle > 0f) {
                    drawArc(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                arcColor.copy(alpha = 0.22f),
                                arcColor.copy(alpha = 0.38f)
                            ),
                            center = center,
                            radius = radius
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // 4. Outer Progress Arc
                    drawArc(
                        color = arcColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // Central Animal Avatar Companion
            Box(
                modifier = Modifier
                    .fillMaxSize(0.55f)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(4.dp, animal.accentColor.copy(alpha = 0.4f), CircleShape)
                    .padding(8.dp)
                    .testTag("animal_avatar_center"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = animal.drawableRes),
                    contentDescription = animal.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Time Remaining Display
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)

        Text(
            text = timeFormatted,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPaused) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            letterSpacing = 1.sp,
            modifier = Modifier.testTag("timer_countdown_text")
        )

        // Status or animal cheering motto
        val mottoText = when {
            isPaused -> "Paused • Tap Resume to continue!"
            isRunning -> "${animal.name}: \"${animal.soundMotto}\""
            else -> "Ready! Tap Start with ${animal.name}"
        }

        Text(
            text = mottoText,
            style = MaterialTheme.typography.titleMedium,
            color = animal.accentColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}
