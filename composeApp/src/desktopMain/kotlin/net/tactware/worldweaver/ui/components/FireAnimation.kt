package net.tactware.worldweaver.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

/**
 * A composable that displays a fire animation.
 *
 * @param modifier Modifier to be applied to the animation
 * @param baseColor The base color of the fire (default is orange)
 * @param flameHeight The height of the flames in dp (default is 100dp)
 * @param centerX The horizontal center position of the flame (0.0 to 1.0, default is 0.5)
 * @param spreadFactor The spread factor of the flames (default is 0.4)
 */
@Composable
fun FireAnimation(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFFFF5722), // Orange
    flameHeight: Int = 100,
    centerX: Float = 0.5f,
    spreadFactor: Float = 0.4f
) {
    // Create an infinite transition for continuous animation
    val infiniteTransition = rememberInfiniteTransition()

    // Randomize animation durations for each instance
    val phaseDuration = remember { Random.nextInt(1800, 2200) }
    val intensityDuration = remember { Random.nextInt(1300, 1700) }

    // Animate the phase of the flames with randomized duration
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(phaseDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Animate the intensity of the flames with randomized duration and range
    val minIntensity = remember { 0.7f + Random.nextFloat() * 0.2f } // 0.7 to 0.9
    val maxIntensity = remember { 1.1f + Random.nextFloat() * 0.2f } // 1.1 to 1.3

    val intensity by infiniteTransition.animateFloat(
        initialValue = minIntensity,
        targetValue = maxIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(intensityDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Create a list of flame particles with random properties
    val flameParticles = remember(centerX, spreadFactor) {
        // Randomize the number of particles between 15 and 25
        val particleCount = Random.nextInt(15, 26)

        List(particleCount) {
            // Calculate random offset centered around centerX with spreadFactor
            val randomOffset = (Random.nextFloat() - 0.5f) * spreadFactor
            val offsetX = (centerX + randomOffset).coerceIn(0.05f, 0.95f)

            FlameParticle(
                offsetX = offsetX, // Position centered around centerX
                speed = Random.nextFloat() * 0.7f + 0.3f,   // Random speed (0.3 to 1.0) - wider range
                amplitude = Random.nextFloat() * 0.15f + 0.03f, // Random amplitude (0.03 to 0.18) - wider range
                phase = Random.nextFloat() * 2f * Math.PI.toFloat(), // Random starting phase
                width = Random.nextFloat() * 0.04f + 0.03f, // Random width factor (0.03 to 0.07)
                heightFactor = Random.nextFloat() * 0.3f + 0.85f // Random height factor (0.85 to 1.15)
            )
        }
    }

    // Define the colors for the fire gradient with slight randomization
    val fireColors = remember {
        // Slightly randomize the colors for each instance
        val yellowHue = (Random.nextFloat() * 0.05f) - 0.025f // -0.025 to 0.025
        val redHue = (Random.nextFloat() * 0.1f) - 0.05f // -0.05 to 0.05

        listOf(
            Color.Yellow.copy(red = (1.0f).coerceIn(0f, 1f), green = (1.0f + yellowHue).coerceIn(0f, 1f), blue = yellowHue.coerceAtLeast(0f)),
            baseColor,
            Color.Red.copy(red = (1.0f + redHue).coerceIn(0f, 1f), green = (redHue * 0.5f).coerceAtLeast(0f), alpha = 0.8f),
            Color.Red.copy(alpha = 0.3f),
            Color.Transparent
        )
    }

    Canvas(modifier = modifier.height(flameHeight.dp)) {
        val width = size.width
        val height = size.height

        // Draw each flame particle
        flameParticles.forEach { particle ->
            val x = particle.offsetX * width
            // Apply the particle's height factor to the flame height
            val flameHeight = height * particle.speed * intensity * particle.heightFactor

            // Create a path for the flame
            val flamePath = Path().apply {
                // Start at the bottom center of the flame
                moveTo(x, height)

                // Calculate the control points for the bezier curve with randomized width
                val widthFactor = particle.width
                val controlX1 = x - width * widthFactor
                // Randomize the control point height
                val controlY1 = height - flameHeight * (0.4f + Random.nextFloat() * 0.2f)

                val controlX2 = x + width * widthFactor
                // Randomize the control point height (different from the first one)
                val controlY2 = height - flameHeight * (0.4f + Random.nextFloat() * 0.2f)

                // Calculate the top point of the flame with some horizontal oscillation
                // Add a secondary oscillation with a different frequency
                val oscillation1 = sin(phase + particle.phase) * width * particle.amplitude
                val oscillation2 = sin(phase * 1.5f + particle.phase) * width * particle.amplitude * 0.3f
                val topX = x + oscillation1 + oscillation2
                val topY = height - flameHeight

                // Draw the bezier curve for one side of the flame
                quadraticBezierTo(
                    controlX1, controlY1,
                    topX, topY
                )

                // Draw the bezier curve for the other side of the flame
                quadraticBezierTo(
                    controlX2, controlY2,
                    x, height
                )

                close()
            }

            // Create a vertical gradient for the flame
            val flameGradient = Brush.verticalGradient(
                colors = fireColors,
                startY = height,
                endY = height - flameHeight
            )

            // Draw the flame
            drawPath(
                path = flamePath,
                brush = flameGradient
            )
        }
    }
}

/**
 * Data class representing a flame particle with its properties.
 */
private data class FlameParticle(
    val offsetX: Float,  // Horizontal position (0.0 to 1.0)
    val speed: Float,    // Vertical speed factor (0.0 to 1.0)
    val amplitude: Float, // Horizontal oscillation amplitude
    val phase: Float,    // Phase offset for oscillation
    val width: Float = 0.05f, // Width factor for the flame (0.0 to 0.1)
    val heightFactor: Float = 1.0f // Height factor multiplier (0.8 to 1.2)
)

/**
 * A composable that displays a torch with fire animation.
 *
 * @param modifier Modifier to be applied to the animation
 * @param baseColor The base color of the fire (default is orange)
 * @param flameHeight The height of the flames in dp (default is 100dp)
 */
@Composable
fun TorchAnimation(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFFFF5722),
    flameHeight: Int = 100
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        // Draw the torch handle
        Canvas(
            modifier = Modifier
                .width(30.dp)
                .height((flameHeight * 0.5).dp)
                .align(Alignment.BottomCenter)
        ) {
            val width = size.width
            val height = size.height

            // Draw the handle
            drawLine(
                color = Color(0xFF8B4513), // Brown color for the handle
                start = Offset(width / 2, 0f),
                end = Offset(width / 2, height),
                strokeWidth = width * 0.4f,
                cap = StrokeCap.Round
            )
        }

        // Draw the flame
        FireAnimation(
            modifier = Modifier.width(80.dp),
            baseColor = baseColor,
            flameHeight = flameHeight,
            centerX = 0.5f,
            spreadFactor = 0.3f
        )
    }
}

/**
 * A composable that displays two torches in the corners.
 *
 * @param modifier Modifier to be applied to the layout
 * @param baseColor The base color of the fire (default is orange)
 * @param flameHeight The height of the flames in dp (default is 100dp)
 */
@Composable
fun TorchesLayout(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFFFF5722),
    flameHeight: Int = 100
) {
    Box(modifier = modifier) {
        // Left torch
        TorchAnimation(
            modifier = Modifier
                .width(80.dp)
                .align(Alignment.TopStart)
                .padding(start = 16.dp),
            baseColor = baseColor,
            flameHeight = flameHeight
        )

        // Right torch
        TorchAnimation(
            modifier = Modifier
                .width(80.dp)
                .align(Alignment.TopEnd)
                .padding(end = 16.dp),
            baseColor = baseColor,
            flameHeight = flameHeight
        )
    }
}
