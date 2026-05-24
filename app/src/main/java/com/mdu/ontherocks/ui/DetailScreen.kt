package com.mdu.ontherocks.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdu.ontherocks.data.Cocktail
import com.mdu.ontherocks.data.IngredientLine
import com.mdu.ontherocks.ui.components.CocktailGlass
import com.mdu.ontherocks.ui.components.ProportionsBar
import com.mdu.ontherocks.ui.components.SectionLabel
import com.mdu.ontherocks.ui.theme.AppBackground
import com.mdu.ontherocks.ui.theme.AppGold
import com.mdu.ontherocks.ui.theme.AppMuted
import com.mdu.ontherocks.ui.theme.AppSurface
import com.mdu.ontherocks.ui.theme.AppText

@Composable
fun DetailScreen(
    cocktail: Cocktail,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(44.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "‹ Back",
                        color = AppMuted,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable(onClick = onBack)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AppSurface)
                            .border(1.dp, AppMuted.copy(alpha = 0.22f), CircleShape)
                            .clickable(onClick = onFavorite),
                        contentAlignment = Alignment.Center
                    ) {
                        HeartIcon(filled = isFavorite)
                    }
                }
                CocktailGlass(cocktail = cocktail)
                Spacer(modifier = Modifier.height(18.dp))
                SectionLabel(cocktail.detailEyebrow)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = cocktail.name,
                    color = AppText,
                    fontFamily = FontFamily.Serif,
                    fontSize = 56.sp,
                    lineHeight = 60.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = cocktail.flavorProfile,
                    color = AppMuted,
                    fontSize = 18.sp,
                    lineHeight = 27.sp
                )
            }

            item {
                SectionLabel("PROPORTIONS")
                Spacer(modifier = Modifier.height(14.dp))
                ProportionsBar(
                    items = cocktail.proportions,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                )
            }

            item {
                SectionLabel("INGREDIENTS")
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    cocktail.ingredientRows.forEach { row ->
                        IngredientRow(row)
                    }
                }
            }

            item {
                SectionLabel("METHOD")
                Spacer(modifier = Modifier.height(12.dp))
            }

            itemsIndexed(methodSteps(cocktail.method)) { index, step ->
                MethodStep(number = index + 1, text = step)
            }

            item {
                SectionLabel("GARNISH")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = cocktail.garnish,
                    color = AppText,
                    fontSize = 17.sp,
                    lineHeight = 25.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppSurface)
                        .padding(18.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun HeartIcon(filled: Boolean) {
    Canvas(modifier = Modifier.size(26.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.50f, size.height * 0.82f)
            cubicTo(size.width * 0.13f, size.height * 0.58f, size.width * 0.04f, size.height * 0.32f, size.width * 0.22f, size.height * 0.18f)
            cubicTo(size.width * 0.34f, size.height * 0.08f, size.width * 0.47f, size.height * 0.14f, size.width * 0.50f, size.height * 0.26f)
            cubicTo(size.width * 0.53f, size.height * 0.14f, size.width * 0.66f, size.height * 0.08f, size.width * 0.78f, size.height * 0.18f)
            cubicTo(size.width * 0.96f, size.height * 0.32f, size.width * 0.87f, size.height * 0.58f, size.width * 0.50f, size.height * 0.82f)
            close()
        }
        if (filled) {
            drawPath(path = path, color = AppText)
        } else {
            drawPath(path = path, color = AppText, style = Stroke(width = 2.4.dp.toPx()))
        }
    }
}

@Composable
private fun IngredientRow(row: IngredientLine) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppSurface)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = row.name, color = AppText, fontSize = 18.sp, modifier = Modifier.weight(1f))
        if (row.amount.isNotBlank()) {
            Text(text = row.amount, color = AppMuted, fontSize = 18.sp)
        }
    }
}

@Composable
private fun MethodStep(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppSurface)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "%02d".format(number),
            color = AppGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(34.dp)
        )
        Text(text = text, color = AppText, fontSize = 16.sp, lineHeight = 23.sp, modifier = Modifier.weight(1f))
    }
}

private fun methodSteps(method: String): List<String> {
    return method
        .replace("NOTE:", "Note:")
        .split(Regex("(?<=\\.)\\s+"))
        .flatMap { splitCompoundInstructions(it) }
        .map { it.trim().ensurePeriod() }
        .filter { it.isNotBlank() }
}

private fun splitCompoundInstructions(sentence: String): List<String> {
    return sentence
        .replace(
            Regex(""",\s+(?=(?:then\s+)?(?:strain|stir|build|top|pour|discard|fill|garnish)\b)""", RegexOption.IGNORE_CASE),
            ". "
        )
        .replace(
            Regex("""\s+and\s+(?=(?:then\s+)?(?:strain|stir|build|top|pour|discard|fill|garnish)\b)""", RegexOption.IGNORE_CASE),
            ". "
        )
        .split(".")
        .map { it.trim().replaceFirstChar { char -> char.uppercase() } }
        .filter { it.isNotBlank() }
}

private fun String.ensurePeriod(): String = if (endsWith(".")) this else "$this."
