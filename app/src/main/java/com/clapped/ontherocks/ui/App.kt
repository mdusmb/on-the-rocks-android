package com.clapped.ontherocks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.clapped.ontherocks.data.Cocktail
import com.clapped.ontherocks.data.DiscoverCategory
import com.clapped.ontherocks.ui.theme.AppBackground
import com.clapped.ontherocks.ui.theme.AppLightMode

enum class AppTab(val title: String, val icon: String) {
    DISCOVER("Menu", "◎"),
    BAR("Bar", "◐"),
    CREATE("Create", "+")
}

@Composable
fun OnTheRocksApp() {
    var selectedTab by remember { mutableStateOf(AppTab.DISCOVER) }
    var selectedCocktail by remember { mutableStateOf<Cocktail?>(null) }
    var selectedCategory by remember { mutableStateOf(DiscoverCategory.ALL) }
    var selectedCustomCategory by remember { mutableStateOf<String?>(null) }
    var customCategories by remember { mutableStateOf<List<String>>(emptyList()) }
    var houseCocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }
    var deletedOriginalNames by remember { mutableStateOf<Set<String>>(emptySet()) }
    var pendingDelete by remember { mutableStateOf<Cocktail?>(null) }
    var actionCocktail by remember { mutableStateOf<Cocktail?>(null) }
    var showUpgrade by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showNewCategoryPrompt by remember { mutableStateOf(false) }
    var isLightMode by remember { mutableStateOf(false) }
    val isPro = false
    AppLightMode = isLightMode

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        when (selectedTab) {
            AppTab.DISCOVER -> DiscoverScreen(
                selectedTab = selectedTab,
                selectedCategory = selectedCategory,
                selectedCustomCategory = selectedCustomCategory,
                houseCocktails = houseCocktails,
                customCategories = customCategories,
                deletedOriginalNames = deletedOriginalNames,
                isLightMode = isLightMode,
                onTabSelected = { selectedTab = it },
                onCategorySelected = { selectedCategory = it; selectedCustomCategory = null },
                onCustomCategorySelected = { selectedCustomCategory = it },
                onAddCategory = {
                    if (isPro) {
                        showNewCategoryPrompt = true
                    } else {
                        showUpgrade = true
                    }
                },
                onPaidFeature = { showUpgrade = true },
                onThemeToggle = { isLightMode = !isLightMode },
                onCocktailSelected = { selectedCocktail = it },
                onCocktailLongPressed = { actionCocktail = it }
            )
            AppTab.BAR -> MyBarScreen(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onCocktailSelected = { selectedCocktail = it },
                onSettings = { showSettings = true }
            )
            AppTab.CREATE -> CreateScreen(
                selectedTab = selectedTab,
                isPro = isPro,
                customCocktailCount = houseCocktails.count { it.isUserCreated },
                onTabSelected = { selectedTab = it },
                onUpgrade = { showUpgrade = true },
                onRecipeSaved = { cocktail ->
                    houseCocktails = houseCocktails + cocktail
                    selectedCategory = DiscoverCategory.HOUSE
                    selectedTab = AppTab.DISCOVER
                }
            )
        }

        AnimatedVisibility(
            visible = selectedCocktail != null,
            enter = slideInHorizontally(tween(280)) { it } + fadeIn(tween(180)),
            exit = slideOutHorizontally(tween(240)) { -it } + fadeOut(tween(160))
        ) {
            selectedCocktail?.let { cocktail ->
                DetailScreen(
                    cocktail = cocktail,
                    isFavorite = houseCocktails.any { it.name == cocktail.name },
                    onFavorite = {
                        val existing = houseCocktails.firstOrNull { it.name == cocktail.name }
                        if (existing == null) {
                            houseCocktails = houseCocktails + cocktail.copy(
                                meta = DiscoverCategory.HOUSE.meta,
                                category = DiscoverCategory.HOUSE,
                                status = "HOUSE",
                                isUserCreated = false
                            )
                        } else if (existing.isUserCreated) {
                            pendingDelete = existing
                        } else {
                            houseCocktails = houseCocktails.filterNot { it.name == existing.name }
                            if (selectedCocktail?.name == existing.name) selectedCocktail = null
                        }
                    },
                    onBack = { selectedCocktail = null }
                )
            }
        }

        pendingDelete?.let { cocktail ->
            AlertDialog(
                onDismissRequest = { pendingDelete = null },
                title = { Text("Delete cocktail?") },
                text = { Text("This will delete ${cocktail.name} from the app.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            houseCocktails = houseCocktails.filterNot { it.name == cocktail.name }
                            if (selectedCocktail?.name == cocktail.name) selectedCocktail = null
                            pendingDelete = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        actionCocktail?.let { cocktail ->
            AlertDialog(
                onDismissRequest = { actionCocktail = null },
                title = { Text(cocktail.name) },
                text = { Text("Choose what to do with this cocktail.") },
                confirmButton = {
                    TextButton(onClick = {
                        actionCocktail = null
                        showUpgrade = true
                    }) { Text("Change Category") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        if (!isPro && !cocktail.isUserCreated) {
                            showUpgrade = true
                        } else if (cocktail.isUserCreated) {
                            houseCocktails = houseCocktails.filterNot { it.name == cocktail.name }
                        } else {
                            deletedOriginalNames = deletedOriginalNames + cocktail.name
                        }
                        actionCocktail = null
                    }) { Text("Delete") }
                }
            )
        }

        if (showNewCategoryPrompt) {
            AlertDialog(
                onDismissRequest = { showNewCategoryPrompt = false },
                title = { Text("New category") },
                text = { Text("Name this category from the Menu after upgrading to Pro.") },
                confirmButton = {
                    TextButton(onClick = {
                        val name = "Category ${customCategories.size + 1}"
                        customCategories = customCategories + name
                        selectedCustomCategory = name
                        showNewCategoryPrompt = false
                    }) { Text("Create") }
                },
                dismissButton = {
                    TextButton(onClick = { showNewCategoryPrompt = false }) { Text("Cancel") }
                }
            )
        }

        if (showUpgrade) {
            UpgradeSheet(
                onDismiss = { showUpgrade = false },
                onPurchase = { showUpgrade = false }
            )
        }

        if (showSettings) {
            SettingsSheet(
                deletedCocktails = deletedOriginalNames.sorted(),
                onPaidFeature = {
                    showSettings = false
                    showUpgrade = true
                },
                onRestore = { name -> deletedOriginalNames = deletedOriginalNames - name },
                onDismiss = { showSettings = false }
            )
        }
    }
}
