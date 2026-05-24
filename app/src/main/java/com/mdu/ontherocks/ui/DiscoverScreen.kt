package com.mdu.ontherocks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mdu.ontherocks.data.Cocktail
import com.mdu.ontherocks.data.CocktailCatalog
import com.mdu.ontherocks.data.DiscoverCategory
import com.mdu.ontherocks.ui.components.CategoryChip
import com.mdu.ontherocks.ui.components.CocktailListRow
import com.mdu.ontherocks.ui.components.HeaderBlock
import com.mdu.ontherocks.ui.components.SearchField
import com.mdu.ontherocks.ui.components.SectionLabel
import com.mdu.ontherocks.ui.theme.AppMuted
import com.mdu.ontherocks.ui.theme.AppSurface
import com.mdu.ontherocks.ui.theme.AppGold

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverScreen(
    selectedTab: AppTab,
    selectedCategory: DiscoverCategory,
    selectedCustomCategory: String?,
    houseCocktails: List<Cocktail>,
    customCategories: List<String>,
    deletedOriginalNames: Set<String>,
    isLightMode: Boolean,
    onTabSelected: (AppTab) -> Unit,
    onCategorySelected: (DiscoverCategory) -> Unit,
    onCustomCategorySelected: (String) -> Unit,
    onAddCategory: () -> Unit,
    onPaidFeature: () -> Unit,
    onThemeToggle: () -> Unit,
    onCocktailSelected: (Cocktail) -> Unit,
    onCocktailLongPressed: (Cocktail) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val cocktails = remember(selectedCategory, selectedCustomCategory, searchText, houseCocktails, deletedOriginalNames) {
        val source = if (selectedCustomCategory != null) {
            houseCocktails.filter { it.customCategory == selectedCustomCategory }
        } else when (selectedCategory) {
            DiscoverCategory.ALL -> CocktailCatalog.cocktails(DiscoverCategory.ALL).filterNot { it.name in deletedOriginalNames } + houseCocktails
            DiscoverCategory.HOUSE -> houseCocktails
            else -> CocktailCatalog.cocktails(selectedCategory).filterNot { it.name in deletedOriginalNames }
        }
        source.filter { it.matchesSearch(searchText) }
    }

    AppScaffold(
        selectedTab = selectedTab,
        onTabSelected = onTabSelected
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    HeaderBlock(
                        eyebrow = "COCKTAILS",
                        title = "Menu",
                        modifier = Modifier.weight(1f)
                    )
                    ThemeToggle(isLightMode = isLightMode, onClick = onThemeToggle)
                }
                Spacer(modifier = Modifier.height(20.dp))
                SearchField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = "Search by name or ingredient"
                )
                Spacer(modifier = Modifier.height(18.dp))
                CategoryRows(
                    selectedCategory = selectedCategory,
                    selectedCustomCategory = selectedCustomCategory,
                    customCategories = customCategories,
                    onCategorySelected = onCategorySelected,
                    onCustomCategorySelected = onCustomCategorySelected,
                    onAddCategory = onAddCategory,
                    onPaidFeature = onPaidFeature
                )
                Spacer(modifier = Modifier.height(24.dp))
                SectionLabel(selectedCustomCategory?.uppercase() ?: selectedCategory.sectionTitle)
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (selectedCategory == DiscoverCategory.HOUSE && cocktails.isEmpty()) {
                item {
                    HouseEmptyState()
                }
            } else {
                items(cocktails, key = { it.id }) { cocktail ->
                    CocktailListRow(
                        cocktail = cocktail,
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .combinedClickable(
                                onClick = { onCocktailSelected(cocktail) },
                                onLongClick = { onCocktailLongPressed(cocktail) }
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRows(
    selectedCategory: DiscoverCategory,
    selectedCustomCategory: String?,
    customCategories: List<String>,
    onCategorySelected: (DiscoverCategory) -> Unit,
    onCustomCategorySelected: (String) -> Unit,
    onAddCategory: () -> Unit,
    onPaidFeature: () -> Unit
) {
    val labels = DiscoverCategory.entries.map { it.title } + customCategories + "+"
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    when (label) {
                        "+" -> CategoryChip(label = "+", selected = false, onClick = onAddCategory)
                        else -> {
                            val builtIn = DiscoverCategory.entries.firstOrNull { it.title == label }
                            CategoryChip(
                                label = label,
                                selected = if (builtIn != null) selectedCustomCategory == null && selectedCategory == builtIn else selectedCustomCategory == label,
                                onClick = {
                                    if (builtIn != null) onCategorySelected(builtIn) else onCustomCategorySelected(label)
                                },
                                onLongClick = {
                                    if (builtIn != DiscoverCategory.ALL) onPaidFeature()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeToggle(isLightMode: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .width(42.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(AppSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = if (isLightMode) "☾" else "☀", color = AppGold, fontSize = 18.sp)
    }
}

@Composable
private fun HouseEmptyState() {
    Text(
        text = buildAnnotatedString {
            append("This is a home for all of your favourites, and any of your own creations. Start by favouriting any of the existing cocktails from our menu, or by heading to ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Create")
            }
            append(" in the menu below.")
        },
        color = AppMuted,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppSurface)
            .padding(18.dp)
    )
}

@Composable
fun PlaceholderScreen(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    eyebrow: String,
    title: String,
    body: String
) {
    AppScaffold(selectedTab = selectedTab, onTabSelected = onTabSelected) {
        Column {
            HeaderBlock(eyebrow = eyebrow, title = title)
            Spacer(modifier = Modifier.height(22.dp))
            androidx.compose.material3.Text(
                text = body,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
