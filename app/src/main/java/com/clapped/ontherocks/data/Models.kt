package com.clapped.ontherocks.data

import java.util.UUID
import androidx.compose.ui.graphics.Color

enum class DiscoverCategory(val title: String, val sectionTitle: String, val meta: String) {
    ALL("All", "ALL COCKTAILS", "iba"),
    OLD_SCHOOL("Old School", "OLD SCHOOL", "old school · iba"),
    CLASSICS("Classics", "CLASSICS", "classic · iba"),
    HOUSE("House", "HOUSE", "house")
}

enum class GlassStyle(val displayName: String) {
    ROCKS("Rocks"), HIGHBALL("Highball"), COLLINS("Collins"), COUPE("Coupe"), MARTINI("Martini"),
    FLUTE("Flute"), MUG("Mug"), WINE("Wine"), TIKI("Tiki")
}

data class CocktailDetails(val ingredients: List<String>, val method: String, val garnish: String)
data class CocktailProportion(val name: String, val amount: Double, val layer: DrinkLayer)
data class BarIngredient(val key: String, val name: String, val color: Color)

data class IngredientLine(val rawValue: String, val id: String = UUID.randomUUID().toString()) {
    val name: String
    val amount: String
    init { val parsed = parse(rawValue.trim()); amount = parsed.first; name = cleanName(parsed.second) }
    private fun parse(value: String): Pair<String, String> {
        Regex("""^(\d+(?:\.\d+)?)\s*ml\s+(.+)$""", RegexOption.IGNORE_CASE).find(value)?.let { return normalizedAmount(it.groupValues[1]) to it.groupValues[2] }
        Regex("""^(\d+(?:\.\d+)?/\d+(?:\.\d+)?|\d+/\d+)\s+(?:bar spoon|bar spoons)\s+(.+)$""", RegexOption.IGNORE_CASE).find(value)?.let { return "${it.groupValues[1]} Bar Spoon" to it.groupValues[2] }
        Regex("""^(\d+(?:\.\d+)?/\d+(?:\.\d+)?|\d+/\d+)\s+(.+)$""", RegexOption.IGNORE_CASE).find(value)?.let { return it.groupValues[1] to it.groupValues[2] }
        Regex("""^(\d+(?:\.\d+)?)\s+(tsp|teaspoon|teaspoons|bar spoon|bar spoons|dash|dashes|drop|drops|pc|pcs|tablespoon|tablespoons)\s+(.+)$""", RegexOption.IGNORE_CASE).find(value)?.let { return "${it.groupValues[1]} ${it.groupValues[2]}" to it.groupValues[3] }
        Regex("""^(\d+(?:\.\d+)?)\s+(raw whole)\s+(.+)$""", RegexOption.IGNORE_CASE).find(value)?.let { return "${it.groupValues[1]} ${it.groupValues[2]}" to it.groupValues[3] }
        Regex("""^(Few drops|Few Drops|A dash|Dash|Few dashes|Few Dashes|2 dashes|2 Dashes|3 Dashes|A splash|Splash|Top with|Top up with|Fill up with|A pinch of|A pinch Of)\s+(.+)$""", RegexOption.IGNORE_CASE).find(value)?.let { return it.groupValues[1] to it.groupValues[2] }
        return "" to value
    }
    private fun normalizedAmount(value: String): String { val number = value.toDoubleOrNull(); return if (number != null && number % 1.0 == 0.0) "${number.toInt()}ml" else "${value}ml" }
    private fun cleanName(value: String): String = value.replace(Regex("""^\s*of\s+""", RegexOption.IGNORE_CASE), "").replace(Regex("""\s+\(optional\)$""", RegexOption.IGNORE_CASE), "").trim()
}

data class Cocktail(
    val name: String,
    val meta: String,
    val category: DiscoverCategory,
    val glassStyle: GlassStyle,
    val layers: List<DrinkLayer>,
    val ingredients: List<String>,
    val method: String,
    val garnish: String,
    val flavorProfile: String,
    val proportions: List<CocktailProportion>,
    val status: String = "IBA",
    val isUserCreated: Boolean = false,
    val customCategory: String? = null
) {
    val id: String = name
    val ingredientRows: List<IngredientLine> get() = ingredients.map { IngredientLine(it) }
    val detailEyebrow: String get() { val label = when (category) { DiscoverCategory.ALL -> "IBA COCKTAIL"; DiscoverCategory.OLD_SCHOOL -> "OLD SCHOOL"; DiscoverCategory.CLASSICS -> "CLASSIC"; DiscoverCategory.HOUSE -> "HOUSE" }; return "$label • ${glassStyle.displayName}" }
    val requiredBarIngredients: List<BarIngredient> get() {
        val ingredientsByKey = linkedMapOf<String, BarIngredient>()
        ingredientRows.forEach { ingredient ->
            val key = ingredient.name.barIngredientKey()
            if (key.isNotBlank() && ingredientsByKey[key] == null) {
                val color = proportions.firstOrNull { IngredientLine(it.name).name.barIngredientKey() == key }?.layer?.color
                    ?: layers.firstOrNull()?.color
                    ?: Color.LightGray
                ingredientsByKey[key] = BarIngredient(
                    key = key,
                    name = displayBarIngredientName(key, ingredient.name),
                    color = color
                )
            }
        }
        return ingredientsByKey.values.sortedBy { it.name }
    }
    fun matchesSearch(query: String): Boolean { val normalized = query.normalizedForSearch(); if (normalized.isBlank()) return true; if (name.normalizedForSearch().contains(normalized)) return true; return ingredientRows.any { it.name.normalizedForSearch().contains(normalized) || it.amount.normalizedForSearch().contains(normalized) } || ingredients.any { it.normalizedForSearch().contains(normalized) } }
    fun missingIngredients(onHand: Set<String>): List<BarIngredient> = requiredBarIngredients.filter { it.key !in onHand }
}
fun String.normalizedForSearch(): String = lowercase().trim()

fun String.barIngredientKey(): String {
    var text = normalizedForSearch()
        .replace(Regex("""\([^)]*\)"""), " ")
        .replace(Regex("""[^a-z0-9# ]"""), " ")
        .replace(Regex("""\s+"""), " ")
        .trim()

    text = text
        .replace("freshly squeezed ", "")
        .replace("fresh squeezed ", "")
        .replace("fresh ", "")
        .replace("chilled ", "")
        .replace("smooth ", "")
        .trim()

    return when {
        text.contains("absinthe") || text.contains("absinth") -> "absinthe"
        text.contains("egg white") -> "egg white"
        text == "salt" || text.contains(" salt") || text.contains("salt ") -> "salt"
        text.contains("sugar cube") -> "sugar cube"
        text.contains("italian basil") || text.contains("basil leaves") || text.contains("basil") -> "basil leaves"
        text.contains("vanilla extract") -> "vanilla extract"
        text.contains("red chili pepper") || text.contains("chili pepper") -> "red chili pepper"
        text.contains("cloves") || text == "clove" -> "cloves"
        text.contains("pernod") -> "pernod"
        text.contains("ginger slice") || text.contains("gengibre") -> "ginger slice"
        text.contains("angostura") -> "angostura bitters"
        text.contains("peychaud") -> "peychaud bitters"
        text.contains("orange bitters") -> "orange bitters"
        text.contains("bitters") -> "bitters"
        text.contains("soda water") || text == "soda" || text.contains("soda ") -> "soda water"
        text.contains("ginger beer") -> "ginger beer"
        text.contains("ginger ale") -> "ginger ale"
        text.contains("cola") -> "cola"
        text.contains("lime") -> "lime juice"
        text.contains("lemon") -> "lemon juice"
        text.contains("grapefruit") -> "grapefruit juice"
        text.contains("pineapple") -> "pineapple juice"
        text.contains("cranberry") -> "cranberry juice"
        text.contains("orange juice") || text.contains("mandarin") -> "orange juice"
        text.contains("campari") -> "campari"
        text.contains("aperol") -> "aperol"
        text.contains("cointreau") || text.contains("triple sec") || text.contains("curacao") -> "triple sec"
        text.contains("grand marnier") -> "grand marnier"
        text.contains("dry vermouth") -> "dry vermouth"
        (text.contains("sweet") && text.contains("vermouth")) || text.contains("red vermouth") -> "sweet vermouth"
        text.contains("vermouth") -> "vermouth"
        text.contains("gin") -> "gin"
        text.contains("vodka") -> "vodka"
        text.contains("tequila") -> "tequila"
        text.contains("mezcal") -> "mezcal"
        text.contains("white rum") || text.contains("white cuban ron") || text.contains("cuban ron") -> "white rum"
        text.contains("dark rum") || text.contains("blackstrap") || text.contains("aged rum") || text.contains("demerara rum") -> "dark rum"
        text.contains("rum") || text.contains("ron ") || text.contains("rhum") -> "rum"
        text.contains("whiskey") || text.contains("whisky") || text.contains("bourbon") || text.contains("rye") || text.contains("scotch") -> "whiskey"
        text.contains("cognac") -> "cognac"
        text.contains("brandy") || text.contains("calvados") -> "brandy"
        text.contains("prosecco") -> "prosecco"
        text.contains("champagne") || text.contains("sparkling wine") -> "champagne"
        text.contains("wine") -> if (text.contains("red") || text.contains("port") || text.contains("tawny")) "red wine" else "white wine"
        text.contains("simple syrup") || text.contains("sugar syrup") -> "sugar syrup"
        text.contains("honey") -> "honey syrup"
        text.contains("agave") -> "agave nectar"
        text.contains("orgeat") || text.contains("almond") -> "orgeat syrup"
        text.contains("cream") || text.contains("coconut") -> if (text.contains("coconut")) "coconut cream" else "cream"
        text.contains("kahl") -> "coffee liqueur"
        text.contains("coffee") || text.contains("espresso") -> "coffee"
        text.contains("grenadine") -> "grenadine"
        text.contains("maraschino") -> "maraschino"
        text.contains("chartreuse") -> if (text.contains("yellow")) "yellow chartreuse" else "green chartreuse"
        text.contains("mint") || text.contains("menthe") -> "mint"
        else -> text
    }
}

fun displayBarIngredientName(key: String, fallback: String): String {
    val knownNames = mapOf(
        "absinthe" to "Absinthe", "agave nectar" to "Agave Nectar", "amaretto" to "Amaretto",
        "angostura bitters" to "Angostura Bitters", "aperol" to "Aperol", "basil leaves" to "Italian Basil Leaves",
        "brandy" to "Brandy", "campari" to "Campari", "champagne" to "Champagne", "cloves" to "Cloves",
        "cognac" to "Cognac", "cola" to "Cola", "coffee" to "Coffee", "coffee liqueur" to "Coffee Liqueur",
        "cranberry juice" to "Cranberry Juice", "dark rum" to "Dark Rum", "dry vermouth" to "Dry Vermouth",
        "egg white" to "Egg White", "gin" to "Gin", "ginger ale" to "Ginger Ale", "ginger beer" to "Ginger Beer",
        "ginger slice" to "Ginger Slice", "grapefruit juice" to "Grapefruit Juice", "grenadine" to "Grenadine",
        "honey syrup" to "Honey Syrup", "lemon juice" to "Lemon Juice", "lime juice" to "Lime Juice",
        "orange juice" to "Orange Juice", "orgeat syrup" to "Orgeat Syrup", "pernod" to "Pernod",
        "pineapple juice" to "Pineapple Juice", "red chili pepper" to "Red Chili Pepper", "rum" to "Rum",
        "salt" to "Salt", "soda water" to "Soda Water", "sugar cube" to "Sugar Cube", "sugar syrup" to "Sugar Syrup",
        "sweet vermouth" to "Sweet Vermouth", "tequila" to "Tequila", "triple sec" to "Triple Sec",
        "vanilla extract" to "Vanilla Extract", "vodka" to "Vodka", "whiskey" to "Whiskey", "white rum" to "White Rum"
    )
    return knownNames[key] ?: fallback.replace(Regex("""\s*\([^)]*\)"""), "").trim()
}
