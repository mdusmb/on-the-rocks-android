package com.mdu.ontherocks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdu.ontherocks.data.BarIngredient
import com.mdu.ontherocks.data.Cocktail
import com.mdu.ontherocks.data.CocktailCatalog
import com.mdu.ontherocks.data.DiscoverCategory
import com.mdu.ontherocks.data.normalizedForSearch
import com.mdu.ontherocks.ui.components.CocktailListRow
import com.mdu.ontherocks.ui.components.HeaderBlock
import com.mdu.ontherocks.ui.components.SearchField
import com.mdu.ontherocks.ui.components.SectionLabel
import com.mdu.ontherocks.ui.theme.AppActiveSurface
import com.mdu.ontherocks.ui.theme.AppGold
import com.mdu.ontherocks.ui.theme.AppMuted
import com.mdu.ontherocks.ui.theme.AppSurface
import com.mdu.ontherocks.ui.theme.AppText

@Composable
fun MyBarScreen(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onCocktailSelected: (Cocktail) -> Unit,
    onSettings: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var onHand by remember { mutableStateOf<List<BarIngredient>>(emptyList()) }
    val onHandKeys = remember(onHand) { onHand.map { it.key }.toSet() }
    val matchingIngredients = remember(searchText, onHand) {
        val query = searchText.normalizedForSearch()
        if (query.isBlank()) {
            emptyList()
        } else {
            CocktailCatalog.availableIngredients
                .filter { it.key !in onHandKeys }
                .filter { it.name.normalizedForSearch().contains(query) || it.key.contains(query) }
                .take(8)
        }
    }
    val makeableCocktails = remember(onHandKeys) {
        CocktailCatalog.cocktails(DiscoverCategory.ALL).filter { it.missingIngredients(onHandKeys).isEmpty() }
    }
    val bestUnlock = remember(onHandKeys) {
        CocktailCatalog.cocktails(DiscoverCategory.ALL)
            .map { it.missingIngredients(onHandKeys) }
            .filter { it.size == 1 }
            .flatten()
            .filter { it.key !in onHandKeys }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
    }

    AppScaffold(selectedTab = selectedTab, onTabSelected = onTabSelected) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    HeaderBlock(
                        eyebrow = "YOUR INGREDIENTS",
                        title = "My Bar",
                        modifier = Modifier.weight(1f)
                    )
                    SettingsButton(onSettings)
                }
                Spacer(modifier = Modifier.height(20.dp))
                SearchField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = "Find or add ingredient"
                )
            }

            items(matchingIngredients, key = { it.key }) { ingredient ->
                IngredientSuggestionRow(
                    ingredient = ingredient,
                    onClick = {
                        onHand = (onHand + ingredient).distinctBy { it.key }.sortedBy { it.name }
                        searchText = ""
                    }
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppActiveSurface)
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    SectionLabel("READY TONIGHT")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${makeableCocktails.size} ${if (makeableCocktails.size == 1) "cocktail" else "cocktails"}",
                        color = AppText,
                        fontFamily = FontFamily.Serif,
                        fontSize = 36.sp,
                        lineHeight = 40.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bestUnlock?.let { "Add ${it.key.name.lowercase()} to unlock ${it.value} more recipes." }
                            ?: "Add ingredients to see what opens up.",
                        color = AppMuted,
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            item {
                SectionLabel("ON HAND")
                if (onHand.isEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Pick ingredients from search to start building your bar.",
                        color = AppMuted,
                        fontSize = 15.sp
                    )
                }
            }

            items(onHand, key = { it.key }) { ingredient ->
                IngredientChipRow(ingredient = ingredient, onRemove = {
                    onHand = onHand.filterNot { it.key == ingredient.key }
                })
            }

            item {
                SectionLabel("SUGGESTED")
                Spacer(modifier = Modifier.height(2.dp))
            }

            items(makeableCocktails, key = { it.id }) { cocktail ->
                CocktailListRow(
                    cocktail = cocktail.copy(meta = "all ingredients ready", status = "ready"),
                    modifier = Modifier.clickable { onCocktailSelected(cocktail) }
                )
            }
        }
    }
}

@Composable
private fun SettingsButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(AppSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "⚙", color = AppGold, fontSize = 17.sp)
    }
}

@Composable
private fun IngredientSuggestionRow(ingredient: BarIngredient, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IngredientDot(ingredient)
        Text(text = ingredient.name, color = AppText, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(text = "+", color = AppGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun IngredientChipRow(ingredient: BarIngredient, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AppSurface)
            .clickable(onClick = onRemove)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IngredientDot(ingredient)
        Text(text = ingredient.name, color = AppMuted, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(text = "Remove", color = AppGold, fontSize = 12.sp)
    }
}

@Composable
private fun IngredientDot(ingredient: BarIngredient) {
    Spacer(
        modifier = Modifier
            .padding(end = 12.dp)
            .size(10.dp)
            .clip(CircleShape)
            .background(ingredient.color)
    )
}
