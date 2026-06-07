package com.clapped.ontherocks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.clapped.ontherocks.data.BarIngredient
import com.clapped.ontherocks.data.Cocktail
import com.clapped.ontherocks.data.CocktailCatalog
import com.clapped.ontherocks.data.CocktailProportion
import com.clapped.ontherocks.data.DiscoverCategory
import com.clapped.ontherocks.data.DrinkLayer
import com.clapped.ontherocks.data.GlassStyle
import com.clapped.ontherocks.data.normalizedForSearch
import com.clapped.ontherocks.ui.components.HeaderBlock
import com.clapped.ontherocks.ui.components.SearchField
import com.clapped.ontherocks.ui.components.SectionLabel
import com.clapped.ontherocks.ui.theme.AppActiveSurface
import com.clapped.ontherocks.ui.theme.AppGold
import com.clapped.ontherocks.ui.theme.AppMuted
import com.clapped.ontherocks.ui.theme.AppSurface
import com.clapped.ontherocks.ui.theme.AppText
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt

private data class DraftIngredient(
    val id: String = UUID.randomUUID().toString(),
    val ingredient: BarIngredient,
    val amount: String = "",
    val measure: String = "ml"
)

private data class DraftStep(
    val id: String = UUID.randomUUID().toString(),
    val text: String
)

private val measureOptions = listOf(
    "ml",
    "dash",
    "dashes",
    "drop",
    "drops",
    "tsp",
    "teaspoon",
    "teaspoons",
    "bar spoon",
    "bar spoons",
    "pc",
    "pcs",
    "tablespoon",
    "tablespoons"
)

@Composable
fun CreateScreen(
    selectedTab: AppTab,
    isPro: Boolean,
    customCocktailCount: Int,
    onTabSelected: (AppTab) -> Unit,
    onUpgrade: () -> Unit,
    onRecipeSaved: (Cocktail) -> Unit
) {
    var recipeName by remember { mutableStateOf("") }
    var selectedGlass by remember { mutableStateOf<GlassStyle?>(null) }
    var selectedIngredients by remember { mutableStateOf<List<DraftIngredient>>(emptyList()) }
    var ingredientSearch by remember { mutableStateOf("") }
    var isAddingIngredient by remember { mutableStateOf(false) }
    var draftStep by remember { mutableStateOf("") }
    var isAddingStep by remember { mutableStateOf(false) }
    var methodSteps by remember { mutableStateOf<List<DraftStep>>(emptyList()) }
    val stepFocusRequester = remember { FocusRequester() }

    val matchingIngredients = remember(ingredientSearch, selectedIngredients) {
        val selectedKeys = selectedIngredients.map { it.ingredient.key }.toSet()
        val available = CocktailCatalog.availableIngredients.filter { it.key !in selectedKeys }
        val query = ingredientSearch.normalizedForSearch()
        if (query.isBlank()) {
            available.take(8)
        } else {
            available
                .filter { it.name.normalizedForSearch().contains(query) || it.key.contains(query) }
                .take(8)
        }
    }

    val canSave = recipeName.trim().isNotEmpty() &&
        selectedGlass != null &&
        selectedIngredients.isNotEmpty() &&
        methodSteps.isNotEmpty()

    AppScaffold(selectedTab = selectedTab, onTabSelected = onTabSelected) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HeaderBlock(eyebrow = "CUSTOM RECIPE", title = "Create")
            }

            item {
                RecipeTextField(
                    label = "NAME",
                    value = recipeName,
                    onValueChange = { recipeName = it },
                    placeholder = "Recipe name",
                    capitalization = KeyboardCapitalization.Words
                )
            }

            item {
                GlassPicker(selectedGlass = selectedGlass, onGlassSelected = { selectedGlass = it })
            }

            item {
                SectionLabel("INGREDIENTS")
            }

            if (selectedIngredients.isEmpty()) {
                item { EmptyCreateCopy("No ingredients added yet.") }
            } else {
                items(selectedIngredients, key = { it.id }) { draft ->
                    DraftIngredientRow(
                        draft = draft,
                        onAmountChange = { amount ->
                            selectedIngredients = selectedIngredients.map {
                                if (it.id == draft.id) it.copy(amount = amount.filter(Char::isDigit)) else it
                            }
                        },
                        onMeasureChange = { measure ->
                            selectedIngredients = selectedIngredients.map {
                                if (it.id == draft.id) it.copy(measure = measure) else it
                            }
                        },
                        onDelete = {
                            selectedIngredients = selectedIngredients.filterNot { it.id == draft.id }
                        }
                    )
                }
            }

            item {
                CreateActionButton("Add ingredient") {
                    isAddingIngredient = !isAddingIngredient
                }
            }

            if (isAddingIngredient) {
                item {
                    SearchField(
                        value = ingredientSearch,
                        onValueChange = { ingredientSearch = it },
                        placeholder = "Search ingredients"
                    )
                }
                items(matchingIngredients, key = { it.key }) { ingredient ->
                    IngredientSuggestionRow(ingredient = ingredient) {
                        selectedIngredients = selectedIngredients + DraftIngredient(ingredient = ingredient)
                        ingredientSearch = ""
                        isAddingIngredient = false
                    }
                }
            }

            item {
                SectionLabel("METHOD")
            }

            if (methodSteps.isEmpty()) {
                item { EmptyCreateCopy("No steps added yet.") }
            } else {
                items(methodSteps, key = { it.id }) { step ->
                    DraftStepRow(
                        number = methodSteps.indexOf(step) + 1,
                        step = step,
                        onDelete = { methodSteps = methodSteps.filterNot { it.id == step.id } }
                    )
                }
            }

            item {
                CreateActionButton("Add Step") {
                    isAddingStep = true
                }
            }

            if (isAddingStep) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LaunchedEffect(Unit) {
                            stepFocusRequester.requestFocus()
                        }
                        RecipeTextField(
                            label = "STEP",
                            value = draftStep,
                            onValueChange = { draftStep = it },
                            placeholder = "Describe the next step",
                            minHeight = 92,
                            singleLine = false,
                            capitalization = KeyboardCapitalization.Sentences,
                            focusRequester = stepFocusRequester
                        )
                        SaveSmallButton(
                            label = "Save step",
                            enabled = draftStep.trim().isNotEmpty(),
                            onClick = {
                                val text = draftStep.trim()
                                if (text.isNotEmpty()) {
                                    methodSteps = methodSteps + DraftStep(text = text)
                                    draftStep = ""
                                    isAddingStep = false
                                }
                            }
                        )
                    }
                }
            }

            item {
                SaveRecipeButton(enabled = canSave) {
                    if (!isPro && customCocktailCount >= 2) {
                        onUpgrade()
                        return@SaveRecipeButton
                    }
                    selectedGlass?.let { glass ->
                        val layers = selectedIngredients.map { layerForIngredientKey(it.ingredient.key) }
                        val ingredientLines = selectedIngredients.map { it.ingredientLine() }
                        onRecipeSaved(
                            Cocktail(
                                name = recipeName.trim(),
                                meta = DiscoverCategory.HOUSE.meta,
                                category = DiscoverCategory.HOUSE,
                                glassStyle = glass,
                                layers = layers.ifEmpty { listOf(DrinkLayer.AMBER) },
                                ingredients = ingredientLines,
                                method = methodSteps.joinToString(". ") { it.text.trimEnd('.') },
                                garnish = "No garnish listed.",
                                flavorProfile = "A house recipe built from your selected ingredients.",
                                proportions = selectedIngredients.mapIndexed { index, ingredient ->
                                    CocktailProportion(
                                        name = ingredient.ingredientLine(),
                                        amount = ingredient.amount.toDoubleOrNull() ?: 1.0,
                                        layer = layers.getOrElse(index) { DrinkLayer.AMBER }
                                    )
                                },
                                status = "HOUSE",
                                isUserCreated = true
                            )
                        )

                        recipeName = ""
                        selectedGlass = null
                        selectedIngredients = emptyList()
                        ingredientSearch = ""
                        isAddingIngredient = false
                        draftStep = ""
                        methodSteps = emptyList()
                        isAddingStep = false
                    }
                }
            }
        }
    }
}

private fun DraftIngredient.ingredientLine(): String {
    val trimmedAmount = amount.trim()
    return if (trimmedAmount.isBlank()) {
        ingredient.name
    } else {
        "$trimmedAmount $measure ${ingredient.name}"
    }
}

@Composable
private fun RecipeTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minHeight: Int = 58,
    singleLine: Boolean = true,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    focusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppSurface)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .height(minHeight.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = label, color = AppGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            if (value.isEmpty()) {
                Text(text = placeholder, color = AppMuted, fontSize = 15.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                cursorBrush = SolidColor(AppGold),
                keyboardOptions = KeyboardOptions(capitalization = capitalization, keyboardType = keyboardType, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                textStyle = TextStyle(color = AppText, fontSize = 15.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            )
        }
    }
}

@Composable
private fun GlassPicker(selectedGlass: GlassStyle?, onGlassSelected: (GlassStyle) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppSurface)
            .clickable { expanded = true }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(text = "GLASS", color = AppGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = selectedGlass?.displayName ?: "Choose a glass", color = if (selectedGlass == null) AppMuted else AppText, fontSize = 15.sp)
            }
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(72.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⌄", color = AppGold, fontSize = 28.sp)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            GlassStyle.entries.forEach { glass ->
                DropdownMenuItem(
                    text = { Text(glass.displayName) },
                    onClick = {
                        onGlassSelected(glass)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DraftIngredientRow(
    draft: DraftIngredient,
    onAmountChange: (String) -> Unit,
    onMeasureChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    SwipeDeleteContainer(key = draft.id, onDelete = onDelete) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppSurface)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IngredientDot(draft.ingredient)
            Text(
                text = draft.ingredient.name,
                color = AppText,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            AmountField(value = draft.amount, onValueChange = onAmountChange)
            Spacer(modifier = Modifier.width(10.dp))
            MeasurePicker(measure = draft.measure, onMeasureChange = onMeasureChange)
        }
    }
}

@Composable
private fun AmountField(value: String, onValueChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .width(66.dp)
            .height(56.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(text = "0", color = AppMuted, fontSize = 18.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.filter(Char::isDigit)) },
            singleLine = true,
            cursorBrush = SolidColor(AppGold),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            textStyle = TextStyle(color = AppText, fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MeasurePicker(measure: String, onMeasureChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .width(112.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppActiveSurface)
            .clickable { expanded = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = measure, color = AppText, fontSize = 16.sp, maxLines = 1)
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            measureOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onMeasureChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DraftStepRow(number: Int, step: DraftStep, onDelete: () -> Unit) {
    SwipeDeleteContainer(key = step.id, onDelete = onDelete) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppSurface)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(AppActiveSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = number.toString(), color = AppGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = step.text, color = AppText, fontSize = 14.sp, lineHeight = 20.sp, modifier = Modifier.weight(1f))
        }
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
private fun IngredientDot(ingredient: BarIngredient) {
    Spacer(
        modifier = Modifier
            .padding(end = 12.dp)
            .size(10.dp)
            .clip(CircleShape)
            .background(ingredient.color)
    )
}

@Composable
private fun EmptyCreateCopy(text: String) {
    Text(
        text = text,
        color = AppMuted,
        fontSize = 13.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppSurface)
            .padding(horizontal = 12.dp, vertical = 14.dp)
    )
}

@Composable
private fun CreateActionButton(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(AppActiveSurface)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "+", color = AppGold, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = AppText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SaveSmallButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = Color(0xFF171614),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(AppGold.copy(alpha = if (enabled) 1f else 0.35f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(top = 11.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Composable
private fun SaveRecipeButton(enabled: Boolean, onClick: () -> Unit) {
    Text(
        text = "Save recipe",
        color = Color(0xFF171614),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(AppGold.copy(alpha = if (enabled) 1f else 0.35f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(top = 16.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Composable
private fun SwipeDeleteContainer(
    key: String,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    var dragOffset by remember(key) { mutableStateOf(0f) }
    val limit = 104f
    val deleteThreshold = 96f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFF4F55))
                .padding(horizontal = 26.dp),
            horizontalArrangement = if (dragOffset < 0f) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrashIcon()
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffset.roundToInt(), 0) }
                .pointerInput(key) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(dragOffset) > deleteThreshold) {
                                onDelete()
                            }
                            dragOffset = 0f
                        },
                        onDragCancel = { dragOffset = 0f }
                    ) { _, dragAmount ->
                        dragOffset = (dragOffset + dragAmount).coerceIn(-limit, limit)
                    }
                }
        ) {
            content()
        }
    }
}

@Composable
private fun TrashIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.4.dp.toPx())
        val color = Color.White
        drawLine(color, Offset(size.width * 0.28f, size.height * 0.28f), Offset(size.width * 0.72f, size.height * 0.28f), strokeWidth = stroke.width)
        drawLine(color, Offset(size.width * 0.40f, size.height * 0.20f), Offset(size.width * 0.60f, size.height * 0.20f), strokeWidth = stroke.width)
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.34f, size.height * 0.34f),
            size = Size(size.width * 0.32f, size.height * 0.46f),
            style = stroke
        )
        drawLine(color, Offset(size.width * 0.43f, size.height * 0.43f), Offset(size.width * 0.43f, size.height * 0.70f), strokeWidth = stroke.width)
        drawLine(color, Offset(size.width * 0.57f, size.height * 0.43f), Offset(size.width * 0.57f, size.height * 0.70f), strokeWidth = stroke.width)
    }
}

private fun layerForIngredientKey(key: String): DrinkLayer = when {
    key.contains("gin") -> DrinkLayer.GIN
    key.contains("vodka") || key.contains("white rum") -> DrinkLayer.CLEAR
    key.contains("rum") -> if (key.contains("dark")) DrinkLayer.DARK_RUM else DrinkLayer.AMBER
    key.contains("whiskey") || key.contains("whisky") || key.contains("bourbon") || key.contains("rye") || key.contains("scotch") -> DrinkLayer.WHISKEY
    key.contains("cognac") || key.contains("brandy") || key.contains("calvados") -> DrinkLayer.BRANDY
    key.contains("tequila") || key.contains("mezcal") -> DrinkLayer.AMBER
    key.contains("vermouth") -> if (key.contains("dry")) DrinkLayer.CLEAR else DrinkLayer.VERMOUTH
    key.contains("campari") || key.contains("aperol") -> DrinkLayer.CAMPARI
    key.contains("lime") -> DrinkLayer.LIME_BRIGHT
    key.contains("lemon") -> DrinkLayer.LEMON
    key.contains("orange") || key.contains("triple sec") || key.contains("cointreau") -> DrinkLayer.ORANGE
    key.contains("grapefruit") -> DrinkLayer.GRAPEFRUIT
    key.contains("pineapple") -> DrinkLayer.PINEAPPLE
    key.contains("cranberry") -> DrinkLayer.CRANBERRY
    key.contains("grenadine") -> DrinkLayer.GRENADINE
    key.contains("coffee") -> DrinkLayer.COFFEE
    key.contains("cola") -> DrinkLayer.COLA
    key.contains("cream") || key.contains("egg") -> DrinkLayer.CREAM
    key.contains("ginger") -> DrinkLayer.GINGER
    key.contains("honey") -> DrinkLayer.HONEY
    key.contains("agave") -> DrinkLayer.AGAVE
    key.contains("sugar") -> DrinkLayer.SUGAR
    key.contains("basil") -> DrinkLayer.BASIL
    key.contains("mint") -> DrinkLayer.MINT
    key.contains("absinthe") || key.contains("pernod") -> DrinkLayer.ANISE
    key.contains("salt") -> DrinkLayer.WHITE
    else -> DrinkLayer.AMBER
}
