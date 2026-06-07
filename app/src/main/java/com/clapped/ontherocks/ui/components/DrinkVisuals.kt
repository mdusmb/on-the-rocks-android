package com.clapped.ontherocks.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.clapped.ontherocks.data.Cocktail
import com.clapped.ontherocks.data.CocktailProportion
import com.clapped.ontherocks.data.GlassStyle
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun ProportionsBar(items: List<CocktailProportion>, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
    ) {
        val total = max(items.sumOf { it.amount }, 1.0)
        var left = 0f
        items.forEach { item ->
            val width = (size.width * item.amount / total).toFloat()
            drawRect(item.layer.color, topLeft = Offset(left, 0f), size = Size(width + 1f, size.height))
            left += width
        }
    }
}

@Composable
fun CocktailGlass(cocktail: Cocktail, modifier: Modifier = Modifier) {
    val progress = remember(cocktail.id) { Animatable(0f) }

    LaunchedEffect(cocktail.id) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(durationMillis = 3100))
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        val stroke = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val centerX = size.width / 2f
        val top = size.height * 0.13f
        val bowlWidth = size.width * when (cocktail.glassStyle) {
            GlassStyle.HIGHBALL, GlassStyle.COLLINS, GlassStyle.TIKI -> 0.34f
            GlassStyle.MARTINI -> 0.58f
            else -> 0.46f
        }
        val bowlHeight = size.height * when (cocktail.glassStyle) {
            GlassStyle.HIGHBALL, GlassStyle.COLLINS, GlassStyle.TIKI -> 0.58f
            GlassStyle.ROCKS -> 0.43f
            GlassStyle.MARTINI -> 0.42f
            else -> 0.48f
        }
        val left = centerX - bowlWidth / 2f
        val right = centerX + bowlWidth / 2f
        val bottom = top + bowlHeight
        val fillTop = top + bowlHeight * 0.30f
        val fillBottom = bottom - bowlHeight * 0.06f
        val damping = (1f - progress.value).coerceIn(0f, 1f).toDouble().pow(0.58).toFloat()
        val sway = sin(((progress.value * 8.4f + 0.2f) * PI)).toFloat()
        val wave = size.height * 0.14f * damping * sway
        val sidePull = bowlWidth * 0.12f * damping * sway

        val glassPath = Path()
        when (cocktail.glassStyle) {
            GlassStyle.MARTINI -> {
                glassPath.moveTo(left, top)
                glassPath.lineTo(right, top)
                glassPath.lineTo(centerX + bowlWidth * 0.07f, bottom)
                glassPath.lineTo(centerX - bowlWidth * 0.07f, bottom)
                glassPath.close()
            }
            GlassStyle.HIGHBALL, GlassStyle.COLLINS, GlassStyle.TIKI -> {
                glassPath.moveTo(left, top)
                glassPath.lineTo(right, top)
                glassPath.lineTo(right - bowlWidth * 0.07f, bottom)
                glassPath.quadraticTo(centerX, bottom + 10f, left + bowlWidth * 0.07f, bottom)
                glassPath.close()
            }
            GlassStyle.ROCKS -> {
                glassPath.moveTo(left, top)
                glassPath.lineTo(right, top)
                glassPath.lineTo(right - bowlWidth * 0.07f, bottom)
                glassPath.quadraticTo(centerX, bottom + 12f, left + bowlWidth * 0.07f, bottom)
                glassPath.close()
            }
            else -> {
                glassPath.moveTo(left, top)
                glassPath.cubicTo(left - 8f, top + bowlHeight * 0.52f, centerX - bowlWidth * 0.32f, bottom, centerX, bottom)
                glassPath.cubicTo(centerX + bowlWidth * 0.32f, bottom, right + 8f, top + bowlHeight * 0.52f, right, top)
                glassPath.close()
            }
        }

        clipPath(glassPath) {
            val liquidHeight = fillBottom - fillTop
            val total = max(cocktail.proportions.sumOf { it.amount }, 1.0)
            var y = fillTop
            cocktail.proportions.forEach { item ->
                val height = (liquidHeight * item.amount / total).toFloat()
                drawRect(item.layer.color, topLeft = Offset(0f, y), size = Size(size.width, height + 1f))
                y += height
            }
            val surface = Path().apply {
                moveTo(left - 18f + sidePull, fillTop + wave)
                cubicTo(
                    centerX - bowlWidth * 0.30f + sidePull,
                    fillTop - wave * 1.18f,
                    centerX + bowlWidth * 0.30f + sidePull,
                    fillTop + wave * 1.08f,
                    right + 18f + sidePull,
                    fillTop - wave * 0.70f
                )
                lineTo(right + 10f, fillBottom)
                lineTo(left - 10f, fillBottom)
                close()
            }
            drawPath(surface, Color.Black.copy(alpha = 0.12f))
        }

        drawPath(glassPath, Color.Black, style = stroke)

        if (cocktail.glassStyle != GlassStyle.HIGHBALL && cocktail.glassStyle != GlassStyle.COLLINS && cocktail.glassStyle != GlassStyle.ROCKS && cocktail.glassStyle != GlassStyle.TIKI) {
            drawLine(Color.Black, Offset(centerX, bottom), Offset(centerX, size.height * 0.88f), strokeWidth = 14f, cap = StrokeCap.Round)
            drawLine(Color.Black, Offset(centerX - bowlWidth * 0.22f, size.height * 0.91f), Offset(centerX + bowlWidth * 0.22f, size.height * 0.91f), strokeWidth = 12f, cap = StrokeCap.Round)
        }
    }
}
