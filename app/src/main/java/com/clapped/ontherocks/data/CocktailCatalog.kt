package com.clapped.ontherocks.data

object CocktailCatalog {
    fun cocktails(category: DiscoverCategory): List<Cocktail> = when (category) {
        DiscoverCategory.ALL -> all
        DiscoverCategory.OLD_SCHOOL -> oldSchool
        DiscoverCategory.CLASSICS -> classics
        DiscoverCategory.HOUSE -> emptyList()
    }

    val availableIngredients: List<BarIngredient> by lazy {
        all.flatMap { it.requiredBarIngredients }
            .distinctBy { it.key }
            .sortedBy { it.name }
    }

    private val oldSchoolNames = listOf(
        "Alexander",
        "Americano",
        "Angel Face",
        "Aviation",
        "Between the Sheets",
        "Boulevardier",
        "Brandy Crusta",
        "Casino",
        "Clover Club",
        "Daiquiri",
        "Dry Martini",
        "Gin Fizz",
        "Hanky Panky",
        "John Collins",
        "Last Word",
        "Manhattan",
        "Martinez",
        "Mary Pickford",
        "Monkey Gland",
        "Negroni",
        "Old Fashioned",
        "Paradise",
        "Planters Punch",
        "Porto Flip",
        "Ramos Fizz",
        "Remember the Maine",
        "Rusty Nail",
        "Sazerac",
        "Sidecar",
        "Stinger",
        "Tuxedo",
        "Vieux Carré",
        "Whiskey Sour",
        "White Lady"
    )

    private val classicNames = listOf(
        "Bellini",
        "Black Russian",
        "Bloody Mary",
        "Caipirinha",
        "Cardinale",
        "Champagne Cocktail",
        "Corpse Reviver #2",
        "Cosmopolitan",
        "Cuba Libre",
        "French 75",
        "French Connection",
        "Garibaldi",
        "Grasshopper",
        "Hemingway Special",
        "Horse’s Neck",
        "Irish Coffee",
        "Kir",
        "Lemon Drop Martini",
        "Long Island Iced Tea",
        "Mai-Tai",
        "Margarita",
        "Mimosa",
        "Mint Julep",
        "Mojito",
        "Moscow Mule",
        "Pina Colada",
        "Pisco Sour",
        "Rabo de Galo",
        "Sea Breeze",
        "Sex on the Beach",
        "Singapore Sling",
        "Tequila Sunrise",
        "Vesper",
        "Zombie",
        "Bee’s Knees",
        "Bramble",
        "Canchanchara",
        "Chartreuse Swizzle",
        "Dark ‘N’ Stormy",
        "Don's Special Daiquiri",
        "Espresso Martini",
        "Fernandito",
        "French Martini",
        "Gin Basil Smash",
        "Grand Margarita",
        "IBA Tiki",
        "Illegal",
        "Jungle Bird",
        "Missionary's Downfall",
        "Naked and Famous",
        "New York Sour",
        "Old Cuban",
        "Paloma",
        "Paper Plane",
        "Penicillin",
        "Pisco Punch",
        "Porn Star Martini",
        "Russian Spring Punch",
        "Sherry Cobbler",
        "South Side",
        "Spicy Fifty",
        "Spritz",
        "Suffering Bastard",
        "Three Dots and a Dash",
        "Tipperary",
        "Tommy's Margarita",
        "Trinidad Sour",
        "Ve.N.To"
    )

    private val oldSchool: List<Cocktail> by lazy { rows(oldSchoolNames, DiscoverCategory.OLD_SCHOOL) }
    private val classics: List<Cocktail> by lazy { rows(classicNames, DiscoverCategory.CLASSICS) }
    private val all: List<Cocktail> by lazy { oldSchool + classics }

    private fun rows(names: List<String>, category: DiscoverCategory): List<Cocktail> = names.map { name ->
        val details = detailsFor(name, category)
        val layers = layersFor(name, category)
        Cocktail(name = name, meta = category.meta, category = category, glassStyle = glassStyleFor(name), layers = layers, ingredients = details.ingredients, method = methodWithoutNote(details.method), garnish = details.garnish, flavorProfile = flavorProfileFor(name, category, noteIn(details.method)), proportions = proportionsFor(name, category, details))
    }
    private fun detailsFor(name: String, category: DiscoverCategory): CocktailDetails = drinkDetails[name] ?: CocktailDetails(layersFor(name, category).map { it.title }, "Prepare and serve according to the IBA specification.", "No garnish listed.")
    private fun flavorProfileFor(name: String, category: DiscoverCategory, note: String?): String { val notes = layersFor(name, category).map { it.flavorNote }.distinct().take(3).joinToString(", "); val profile = "A $notes cocktail with a balanced finish."; return if (note.isNullOrBlank()) profile else "$profile Note: $note" }
    private fun methodWithoutNote(method: String): String = method.replace(Regex("""\s+NOTE:\s+.*$""", RegexOption.IGNORE_CASE), "").replace(Regex("""\s+Note:\s+.*$""", RegexOption.IGNORE_CASE), "").trim()
    private fun noteIn(method: String): String? = Regex("""\bNOTE:\s+(.+)$""", RegexOption.IGNORE_CASE).find(method)?.groupValues?.getOrNull(1)?.trim()
    private fun proportionsFor(name: String, category: DiscoverCategory, details: CocktailDetails): List<CocktailProportion> { val measured = details.ingredients.mapNotNull { ingredient -> measuredMilliliters(ingredient)?.let { CocktailProportion(ingredient, it, layerForIngredient(ingredient, name)) } }; return measured.ifEmpty { layersFor(name, category).map { CocktailProportion(it.title, 1.0, it) } } }
    private fun measuredMilliliters(ingredient: String): Double? { val lower = ingredient.lowercase(); if (listOf("dash", "drop", "bar spoon", "teaspoon", "tsp", "pcs", "slice", "sprig", "pinch", "splash", "top", "fill up", "soda water").any { lower.contains(it) }) return null; Regex("""(\d+(?:\.\d+)?)\s*ml""", RegexOption.IGNORE_CASE).find(ingredient)?.let { return it.groupValues[1].toDoubleOrNull() }; Regex("""^\s*(\d+(?:\.\d+)?)\s+""").find(ingredient)?.let { return it.groupValues[1].toDoubleOrNull() }; return null }
    private fun layerForIngredient(ingredient: String, cocktailName: String): DrinkLayer { val text = ingredient.lowercase(); return when {
        text.contains("grand marnier") || text.contains("orange curacao") || text.contains("curacao") || text.contains("cointreau") || text.contains("triple sec") -> DrinkLayer.ORANGE
        text.contains("tequila") || text.contains("mezcal") -> DrinkLayer.AMBER
        text.contains("gin") -> DrinkLayer.GIN
        text.contains("vodka") || text.contains("white rum") || text.contains("cuban ron") || text.contains("aguardiente") -> DrinkLayer.CLEAR
        text.contains("rum") || text.contains("rhum") -> if (text.contains("dark") || text.contains("blackstrap") || text.contains("aged")) DrinkLayer.DARK_RUM else DrinkLayer.AMBER
        text.contains("whiskey") || text.contains("whisky") || text.contains("bourbon") || text.contains("rye") || text.contains("scotch") -> DrinkLayer.WHISKEY
        text.contains("cognac") || text.contains("brandy") || text.contains("calvados") -> DrinkLayer.BRANDY
        text.contains("vermouth") -> if (text.contains("dry")) DrinkLayer.CLEAR else DrinkLayer.VERMOUTH
        text.contains("campari") || text.contains("aperol") -> DrinkLayer.CAMPARI
        text.contains("lime") -> DrinkLayer.LIME
        text.contains("lemon") -> DrinkLayer.LEMON
        text.contains("orange") -> DrinkLayer.ORANGE
        text.contains("grapefruit") -> DrinkLayer.GRAPEFRUIT
        text.contains("pineapple") -> DrinkLayer.PINEAPPLE
        text.contains("cranberry") -> DrinkLayer.CRANBERRY
        text.contains("passion") -> DrinkLayer.PASSION
        text.contains("peach") -> DrinkLayer.PEACH
        text.contains("raspberry") -> DrinkLayer.RASPBERRY
        text.contains("cassis") || text.contains("mûre") || text.contains("mure") -> DrinkLayer.BERRY
        text.contains("cherry") || text.contains("maraschino") -> DrinkLayer.CHERRY
        text.contains("coffee") || text.contains("kahl") || text.contains("espresso") -> DrinkLayer.COFFEE
        text.contains("cream") || text.contains("coconut") -> DrinkLayer.CREAM
        text.contains("honey") -> DrinkLayer.HONEY
        text.contains("agave") -> DrinkLayer.AGAVE
        text.contains("mint") || text.contains("menthe") -> DrinkLayer.MINT
        text.contains("chartreuse") -> DrinkLayer.CHARTREUSE
        text.contains("cola") -> DrinkLayer.COLA
        text.contains("ginger") -> DrinkLayer.GINGER
        text.contains("tomato") -> DrinkLayer.TOMATO
        text.contains("grenadine") -> DrinkLayer.GRENADINE
        text.contains("prosecco") || text.contains("champagne") || text.contains("sparkling") -> DrinkLayer.SPARKLING
        text.contains("wine") || text.contains("port") -> if (text.contains("red") || text.contains("tawny")) DrinkLayer.RUBY_PORT else DrinkLayer.WHITE
        text.contains("amaro") || text.contains("cynar") || text.contains("fernet") -> DrinkLayer.AMARO
        text.contains("almond") || text.contains("orgeat") || text.contains("amaretto") -> DrinkLayer.ALMOND
        else -> layersFor(cocktailName, DiscoverCategory.ALL).firstOrNull() ?: DrinkLayer.AMBER
    } }
    private val rocksGlass = setOf("Americano", "Black Russian", "Boulevardier", "Caipirinha", "French Connection", "Negroni", "New York Sour", "Naked and Famous", "Old Fashioned", "Penicillin", "Rabo de Galo", "Remember the Maine", "Rusty Nail", "Sazerac", "Spritz", "Stinger", "Trinidad Sour", "Vieux Carré")
    private val highballGlass = setOf("Cuba Libre", "Dark ‘N’ Stormy", "Fernandito", "Garibaldi", "Horse’s Neck", "Long Island Iced Tea", "Mojito", "Moscow Mule", "Paloma", "Planters Punch", "Suffering Bastard", "Tequila Sunrise")
    private val collinsGlass = setOf("Bloody Mary", "Canchanchara", "Chartreuse Swizzle", "French 75", "Gin Fizz", "John Collins", "Ramos Fizz", "Sea Breeze", "Singapore Sling", "Tommy's Margarita")
    private val fluteGlass = setOf("Bellini", "Champagne Cocktail", "Mimosa", "Russian Spring Punch")
    private val mugGlass = setOf("Irish Coffee", "Mint Julep")
    private val wineGlass = setOf("Aperol Spritz", "Kir", "Sherry Cobbler", "Ve.N.To")
    private val tikiGlass = setOf("IBA Tiki", "Jungle Bird", "Mai-Tai", "Pina Colada", "Three Dots and a Dash", "Zombie")
    private val martiniGlass = setOf("Aviation", "Casino", "Cosmopolitan", "Dry Martini", "Espresso Martini", "French Martini", "Grand Margarita", "Grasshopper", "Lemon Drop Martini", "Margarita", "Porn Star Martini", "Vesper")
    private fun glassStyleFor(name: String): GlassStyle = when {
        rocksGlass.contains(name) -> GlassStyle.ROCKS
        highballGlass.contains(name) -> GlassStyle.HIGHBALL
        collinsGlass.contains(name) -> GlassStyle.COLLINS
        fluteGlass.contains(name) -> GlassStyle.FLUTE
        mugGlass.contains(name) -> GlassStyle.MUG
        wineGlass.contains(name) -> GlassStyle.WINE
        tikiGlass.contains(name) -> GlassStyle.TIKI
        martiniGlass.contains(name) -> GlassStyle.MARTINI
        else -> GlassStyle.COUPE
    }
    private fun layersFor(name: String, category: DiscoverCategory): List<DrinkLayer> = drinkLayers[name] ?: when (category) { DiscoverCategory.ALL -> listOf(DrinkLayer.AMBER, DrinkLayer.CITRUS, DrinkLayer.CLEAR); DiscoverCategory.OLD_SCHOOL -> listOf(DrinkLayer.DEEP_AMBER, DrinkLayer.AMBER); DiscoverCategory.CLASSICS -> listOf(DrinkLayer.CITRUS, DrinkLayer.CREAM, DrinkLayer.CLEAR); DiscoverCategory.HOUSE -> listOf(DrinkLayer.AMBER, DrinkLayer.ORANGE) }
    private val drinkDetails = mapOf(
        "Alexander" to CocktailDetails(listOf("30 ml Cognac", "30 ml Crème de Cacao (Brown)", "30 ml Fresh Cream"), "Pour all ingredients into cocktail shaker filled with ice cubes. Shake and strain into a chilled cocktail glass.", "Sprinkle fresh ground nutmeg on top."),
        "Americano" to CocktailDetails(listOf("30 ml Bitter Campari", "30 ml Sweet Red Vermouth", "A splash of Soda Water"), "Mix the ingredients directly in an old fashioned glass filled with ice cubes. Add a splash of Soda Water. Stir gently.", "Garnish with half orange slice and a lemon zest."),
        "Angel Face" to CocktailDetails(listOf("30 ml Gin", "30 ml Apricot Brandy", "30 ml Calvados"), "Pour all ingredients into cocktail shaker filled with ice cubes. Shake and strain into a chilled cocktail glass.", "No garnish listed."),
        "Aviation" to CocktailDetails(listOf("45 ml Gin", "15 ml Maraschino", "Luxardo", "15 ml Fresh Lemon Juice", "1 Bar Spoon Crème de Violette"), "Add all ingredients into a cocktail shaker. Shake with cracked ice and strain into a chilled cocktail glass.", "Optional Maraschino Cherry."),
        "Bee’s Knees" to CocktailDetails(listOf("52.5 ml Dry Gin", "2 teaspoons Honey Syrup", "22.5 ml Fresh Lemon Juice", "22.5 ml Fresh Orange Juice"), "Stir honey with lemon and orange juices until it dissolves, add gin and shake with ice. Strain into a chilled cocktail glass.", "Optionally garnish with a lemon or orange zest."),
        "Bellini" to CocktailDetails(listOf("100 ml Prosecco", "50 ml White Peach Puree"), "Pour peach puree into the mixing glass with ice, add the Prosecco wine. Stir gently and pour in a chilled flute glass. NOTE: Puccini – Fresh Mandarin Orange Juice; Rossini – Fresh Strawberry Puree; Tintoretto – Fresh Pomegranate Juice.", "No garnish listed."),
        "Between the Sheets" to CocktailDetails(listOf("30 ml White Rum", "30 ml Cognac", "30 ml Triple Sec", "20 ml Fresh Lemon Juice"), "Add all ingredients into a cocktail shaker. Shake with ice and strain into a chilled cocktail glass.", "No garnish listed."),
        "Black Russian" to CocktailDetails(listOf("50 ml Vodka", "20 ml Coffee Liqueur"), "Pour the ingredients into the old fashioned glass filled with ice cubes. Stir gently. strain ingredients into old fashioned glass filled with ice. NOTE: WHITE RUSSIAN – Float fresh cream on the top and stir in slowly.", "No garnish listed."),
        "Bloody Mary" to CocktailDetails(listOf("45 ml Vodka", "90 ml Tomato Juice", "15 ml Fresh Lemon Juice", "2 dashes Worcestershire Sauce", "Tabasco, Celery Salt, Pepper (Up to taste)"), "Stir gently all the ingredients in a mixing glass with ice, pour into rocks glass. NOTE: If requested served with ice, pour into highball glass.", "Celery, lemon wedge (Optional)."),
        "Boulevardier" to CocktailDetails(listOf("45 ml Bourbon or Rye Whiskey", "30 ml Bitter Campari", "30 ml Sweet Red Vermouth"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled cocktail glass.", "Garnish with a orange zest, optionally a lemon zest."),
        "Bramble" to CocktailDetails(listOf("50 ml Gin", "25 ml Fresh Lemon Juice", "12.5 ml Sugar Syrup", "15 ml Crème de Mûre"), "Pour all ingredients into cocktail shaker except the Crème de Mûre, shake well with ice, strain into chilled old fashioned glass filled with crushed ice, then pour the blackberry liqueur (Crème de Mûre) over the top of the drink, in a circular motion.", "Garnish optionally with a lemon slice and blackberries."),
        "Brandy Crusta" to CocktailDetails(listOf("52.5 ml Brandy", "7.5 ml Maraschino", "Luxardo", "1 Bar Spoon Curacao", "15 ml Fresh Lemon Juice", "1 Bar Spoon Simple Syrup", "2 Dashes Aromatic Bitters"), "Mix together all ingredients with ice cubes in a mixing glass and strain into a prepared slim cocktail glass.", "Rub a slice of orange (or lemon) around the rim of the glass and dip it in pulverized white sugar, so that the sugar will adhere to the edge of the glass. Carefully curling place the orange/lemon peel around the inside of the glass."),
        "Caipirinha" to CocktailDetails(listOf("60 ml Cachaça", "1 Lime cut into small wedges", "4 Teaspoons White Cane Sugar"), "Place lime and sugar into a double old fashioned glass and muddle gently. Fill the glass with cracked ice and add Cachaça. Stir gently to involve ingredients. Note: Caipiroska – Instead of Cachaça use Vodka; Caipirissima – Instead of Cachaça use Rum. Caipirão – Instead of Cachaça use Licor Beirão.", "No garnish listed."),
        "Canchanchara" to CocktailDetails(listOf("60 ml Cuban Aguardiente", "15 ml Fresh Lime Juice", "15 ml Raw Honey", "50 ml Water"), "Mix honey with water and lime juice and spread the mixture on the bottom and sides of the glass. Add cracked ice, and then the rum. End by energetically stirring from bottom to top.", "Garnish with a lime wedge."),
        "Cardinale" to CocktailDetails(listOf("40 ml Gin", "20 ml Dry Vermouth", "10 ml Bitter Campari"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled cocktail glass.", "Garnish with a lemon zest."),
        "Casino" to CocktailDetails(listOf("40 ml Old Tom Gin", "10 ml Maraschino", "Luxardo", "10 ml Fresh Lemon Juice", "2 Dashes Orange Bitters"), "Pour all ingredients into cocktails shaker, shake well with ice, strain into chilled rocks glass with ice.", "Garnish with a lemon zest and a maraschino cherry."),
        "Champagne Cocktail" to CocktailDetails(listOf("90 ml Chilled Champagne", "10 ml Cognac", "2 dashes Angostura bitters", "Few drops of Grand Marnier (optional)", "1 sugar cube"), "Place the sugar cube with 2 dashes of bitters in a large Champagne glass, add the cognac. Pour gently chilled Champagne.", "Garnish with orange zest and maraschino cherry."),
        "Chartreuse Swizzle" to CocktailDetails(listOf("45 ml Green Chartreuse", "30 ml Fresh Pineapple Juice", "22.5 ml Fresh Lime Juice", "15 ml Falernum"), "Pour all ingredients into a tall glass, add pebble ice. With the help of a swizzle stick (or cocktail spoon) mix vigorously, complete by filling the glass with more pebble ice.", "Garnish with mint leaves and grated nutmeg."),
        "Clover Club" to CocktailDetails(listOf("45 ml Gin", "15 ml Raspberry Syrup", "15 ml Fresh Lemon Juice", "Few Drops of Egg White"), "Pour all ingredients into cocktails shaker, shake well with ice, strain into chilled cocktail glass.", "Fresh raspberries."),
        "Corpse Reviver #2" to CocktailDetails(listOf("30 ml Gin", "30 ml Cointreau", "30 ml Lillet Blanc", "30 ml Fresh Lemon Juice", "1 dash Absinthe"), "Pour all ingredients into shaker with ice. Shake well and strain in chilled cocktail glass.", "Garnish with an orange zest."),
        "Cosmopolitan" to CocktailDetails(listOf("40 ml Vodka Citron", "15 ml Cointreau", "15 ml Fresh Lime Juice", "30 ml Cranberry Juice"), "Add all ingredients into cocktail shaker filled with ice. Shake well and strain into large cocktail glass.", "Garnish with lemon twist."),
        "Cuba Libre" to CocktailDetails(listOf("50 ml White Rum", "120 ml Cola", "10 ml Fresh Lime Juice"), "Build all ingredients in a highball glass filled with ice.", "Garnish with lime wedge."),
        "Daiquiri" to CocktailDetails(listOf("60 ml White Cuban Ron", "20 ml Fresh Lime Juice", "2 Bar Spoons Superfine Sugar"), "In a cocktail shaker add all ingredients. Stir well to dissolve the sugar. Add ice and shake. Strain into chilled cocktail glass.", "No garnish listed."),
        "Dark ‘N’ Stormy" to CocktailDetails(listOf("60 ml Goslings Rum", "100 ml Ginger Beer"), "In a highball glass filled with ice pour the ginger beer and top floating with the Rum.", "Garnish with a lime wedge or slice."),
        "Don's Special Daiquiri" to CocktailDetails(listOf("30 ml Gold Jamaican Rum", "15 ml Cuban Rum", "15 ml Passion Fruit Syrup", "15 ml Fresh lime juice", "15 ml Honey Syrup"), "Blend for a few seconds in a milkshake mixer with crushed ice and pour into a footed copo glass. Fill the glass with more crushed ice.", "Garnish with 1/2 passion fruit"),
        "Dry Martini" to CocktailDetails(listOf("60 ml Gin", "10 ml Dry Vermouth"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled martini cocktail glass.", "Squeeze oil from lemon peel onto the drink, or garnish with a green olives if requested."),
        "Espresso Martini" to CocktailDetails(listOf("50 ml Vodka", "30 ml Kahlúa", "10 ml Sugar Syrup", "1 strong Espresso"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "3 coffee beans"),
        "Fernandito" to CocktailDetails(listOf("50 ml Fernet Branca", "Fill up with Cola"), "Pour the Fernet Branca into a double old fashioned glass with ice, fill the glass up with Cola. Gently stir.", "No garnish listed."),
        "French 75" to CocktailDetails(listOf("30 ml Gin", "15 ml Fresh Lemon Juice", "15 ml Sugar Syrup", "60 ml Champagne"), "Pour all the ingredients, except Champagne, into a shaker. Shake well and strain into a Champagne flute. Top up with Champagne. Stir gently.", "No garnish listed."),
        "French Connection" to CocktailDetails(listOf("35 ml Cognac", "35 ml Amaretto"), "Pour all ingredients directly into old fashioned glass filled with ice cubes. Stir gently.", "No garnish listed."),
        "French Martini" to CocktailDetails(listOf("45 ml Vodka", "15 ml Raspberry Liqueur", "15 ml Fresh Pineapple Juice"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "Squeeze oil from lemon peel onto the drink."),
        "Garibaldi" to CocktailDetails(listOf("45 ml Bitter Campari", "120 ml Freshly Squeezed Orange Juice"), "Build all ingredients in a highball glass filled with ice.", "Garnish with an orange wedge."),
        "Gin Basil Smash" to CocktailDetails(listOf("60ml Gin", "22.5 ml Freshly Squeezed Lemon Juice", "22.5 ml Sugar Syrup", "10 pcs Italian Basil leaves"), "Add all ingredients into shaker with ice. Shake vigorously and pour into chilled cocktail glass.", "No garnish listed."),
        "Gin Fizz" to CocktailDetails(listOf("45 ml Gin", "30 ml Fresh Lemon Juice", "10 ml Simple Syrup", "Splash of Soda Water"), "Shake all ingredients with ice except soda water. Pour into thin tall Tumbler glass , top with a splash soda water. NOTE: Serve without ice.", "Garnish with lemon slice, optional lemon zest."),
        "Grand Margarita" to CocktailDetails(listOf("45 ml Tequila 100% agave", "30 ml Grand Marnier", "15 ml Fresh Lime Juice"), "Rim the rock glass with good quality sea salt. Pour the ingredients into the shaker. Add ice to both glass and shaker. Shake hard for 10 seconds. Strain the drink into the glass.", "Garnish with a lime slice."),
        "Grasshopper" to CocktailDetails(listOf("20 ml Crème de Cacao (White)", "20 ml Crème de Menthe (Green)", "20 ml Fresh Cream"), "Pour all ingredients into shaker filled with ice. Shake briskly for few seconds. Strain into chilled cocktail glass.", "N/A, optional mint leave."),
        "Hanky Panky" to CocktailDetails(listOf("45 ml London Dry Gin", "45 ml Sweet Red Vermouth", "7.5 ml Fernet"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled cocktail glass.", "Orange zest."),
        "Hemingway Special" to CocktailDetails(listOf("60 ml Rum", "40 ml Grapefruit Juice", "15 ml Maraschino Luxardo", "15 ml Fresh Lime"), "Pour all ingredients into a shaker with ice. Shake well and strain into a large cocktail glass.", "No garnish listed."),
        "Horse’s Neck" to CocktailDetails(listOf("40 ml Cognac", "120 ml Ginger Ale", "Dash of Angostura Bitters (optional)"), "Pour Cognac and ginger ale directly into highball glass with ice cubes. Stir gently. If preferred, add dashes of Angostura Bitter.", "Garnish with rind of one lemon spiral."),
        "IBA Tiki" to CocktailDetails(listOf("30 ml Ron Profundo Havana Club", "30 ml Ron Smoky Havana Club", "15 ml Licor Amaretto", "5 ml Licor Frangelico", "5 drops Maraschino Luxardo", "30 ml Passion Fruit Puree", "90 Fresh Pineapple Juice", "30 Fresh Lime Juice", "1 pc Ginger Slice"), "In a cocktail shaker muddle a thin slice of Ginger, Pour all other ingredients. Shake vigorously with ice. Strain into a chilled Tiki glass filled with pebbled ice.", "Garnish with citruses and dehydrated pineapple slice."),
        "Illegal" to CocktailDetails(listOf("30 ml Espadin Mezcal", "15 ml Jamaica Overproof White Rum", "15 ml Falernum", "1 Bar Spoon Maraschino Luxardo", "22.5 ml Fresh Lime Juice", "15 ml Simple Syrup", "Few Drops of Egg White (Optional)"), "Pour all ingredients into the shaker. Shake vigorously with ice. Strain into a chilled cocktail glass, or “on the rocks” in a traditional clay or terracotta mug.", "No garnish listed."),
        "Irish Coffee" to CocktailDetails(listOf("50 ml Irish Whiskey", "120 ml Hot coffee", "50 ml Fresh cream (Chilled)", "1 teaspoon Sugar"), "Warm black coffee is poured into a preheated Irish coffee glass. Whiskey and at least one teaspoon of sugar is added and stirred until dissolved. Fresh thick chilled cream is carefully poured over the back of a spoon held just above the surface of the coffee. The layer of cream will float on the coffee without mixing. Plain sugar can be replaced with sugar syrup", "No garnish listed."),
        "John Collins" to CocktailDetails(listOf("45 ml Gin", "30 ml Fresh Lemon Juice", "15 ml Simple Syrup", "60 ml Soda Water"), "Pour all ingredients directly into highball filled with ice. Stir gently. NOTE: Use ‘Old Tom’ Gin for Tom Collins.", "Garnish with lemon slice and maraschino cherry."),
        "Jungle Bird" to CocktailDetails(listOf("45 ml Blackstrap rum", "22.5 ml Campari", "45 ml Pineapple juice", "15 ml Freshly Squeezed Lime juice", "15 ml Demerara sugar syrup"), "Pour all ingredients into a shaker with ice and shake. Strain into a rocks glass filled with ice.", "Garnish with a pineapple wedge."),
        "Kir" to CocktailDetails(listOf("90 ml Dry White Wine", "10 ml Crème de Cassis"), "Pour Crème de Cassis into glass, top up with white wine. NOTE: Kir Royal – Use Champagne instead of white wine", "No garnish listed."),
        "Last Word" to CocktailDetails(listOf("22.5 ml Gin", "22.5 ml Green Chartreuse", "22.5 ml Maraschino", "Luxardo", "22.5 ml Fresh Lime Juice"), "Add all ingredients into a cocktail shaker. Shake with ice and strain into a chilled cocktail glass.", "No garnish listed."),
        "Lemon Drop Martini" to CocktailDetails(listOf("30 ml Vodka", "20 ml Triple Sec", "15 ml Fresh Squeezed Lemon Juice"), "Pour all ingredients into a shaker with ice. Shake well and strain into a chilled cocktail glass.", "No garnish listed."),
        "Long Island Iced Tea" to CocktailDetails(listOf("15 ml Vodka", "15 ml Tequila", "15 ml White rum", "15 ml Gin", "15 ml Cointreau", "25 ml Lemon juice", "30 ml Simple syrup", "Top with Cola"), "Add all ingredients into highball glass filled with ice. Stir gently.", "Garnish with lemon slice (Optional)."),
        "Mai-Tai" to CocktailDetails(listOf("30 ml Amber Jamaican Rum", "30 ml Martinique Molasses Rhum*", "15 ml Orange Curacao", "15 ml Orgeat Syrup (Almond)", "30 ml Fresh Squeezed Lime Juice", "7.5 ml Simple Syrup"), "Add all ingredients into a shaker with ice. Shake and pour into a double rocks glass or an highball glass. * The Martinique molasses rum used by Trader Vic was not an Agricole Rhum but a type of “rummy” from molasses.", "Garnish with pineapple spear, mint leaves and lime peel."),
        "Manhattan" to CocktailDetails(listOf("50 ml Rye Whiskey", "20 ml Sweet Red Vermouth", "1 dash Angostura Bitters"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled cocktail glass.", "Garnish with cocktail cherry."),
        "Margarita" to CocktailDetails(listOf("50 ml Tequila 100% Agave", "20 ml Triple Sec", "15 ml Freshly Squeezed Lime Juice"), "Add all ingredients into a shaker with ice. Shake and strain into a chilled cocktail glass.", "Half salt rim (Optional)."),
        "Martinez" to CocktailDetails(listOf("45 ml London Dry Gin", "45 ml Sweet Red Vermouth", "1 Bar Spoon Maraschino", "Luxardo", "2 Dashes Orange Bitters"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled cocktail glass.", "Lemon zest."),
        "Mary Pickford" to CocktailDetails(listOf("45 ml White Rum", "45 ml Fresh Pineapple Juice", "7.5 ml Maraschino Luxardo", "5 ml Grenadine Syrup"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "No garnish listed."),
        "Mimosa" to CocktailDetails(listOf("75 ml Freshly Squeezed Orange Juice", "75 ml Prosecco"), "Pour orange juice into flute glass and gently pour the sparkling wine. Stir gently. NOTE: Also known as Buck’s Fizz.", "Garnish with orange twist (optional)."),
        "Mint Julep" to CocktailDetails(listOf("60 ml Bourbon Whiskey", "4 fresh Mint sprigs", "1 tsp Powdered Sugar", "2 tsp Water"), "In Julep Stainless Steel Cup gently muddle the mint with sugar and water. Fill the glass with cracked ice, add the Bourbon and stir well until the cup frosts.", "Garnish with a mint sprig."),
        "Missionary's Downfall" to CocktailDetails(listOf("30 ml White rum", "15 ml Peach Brandy", "15 ml Fresh lime juice", "30 ml Honey Mix", "10 pcs Mint Leaves", "3 to 4 pcs Pineapple Chunks"), "Blend all the ingredients with half cup of crushed ice. Serve it in a Coppa grande.", "Garnish with mint sprig and a slice of pineapple."),
        "Mojito" to CocktailDetails(listOf("45 ml White Cuban Ron", "20 ml Fresh Lime Juice", "6 pcs Mint Sprigs", "2 tsp White Cane Sugar", "Soda Water"), "Mix mint springs with sugar and lime juice. Add splash of soda water and fill the glass with ice. Pour the rum and top with soda water. Light stir to involve all ingredients.", "Garnish with sprigs of mint and slice of lime."),
        "Monkey Gland" to CocktailDetails(listOf("45 ml Dry Gin", "45 ml Fresh Orange Juice", "1 Tablespoon Absinthe", "1 Tablespoon Grenadine Syrup"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "No garnish listed."),
        "Moscow Mule" to CocktailDetails(listOf("45 ml Smirnoff Vodka", "120 ml Ginger Beer", "10 ml Fresh lime juice"), "In an Mule Cup or rocks glass, combine the vodka and ginger beer. Add lime juice and gently stir to involve all ingredients.", "Garnish with a lime slice."),
        "Naked and Famous" to CocktailDetails(listOf("22.5 ml Mezcal", "22.5 ml Yellow Chartreuse", "22.5 ml Aperol", "22.5 ml Fresh Lime Juice"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "No garnish listed."),
        "Negroni" to CocktailDetails(listOf("30 ml Gin", "30 ml Bitter Campari", "30 ml Sweet Red Vermouth"), "Pour all ingredients directly into chilled old fashioned glass filled with ice. Stir gently.", "Garnish with half orange slice."),
        "New York Sour" to CocktailDetails(listOf("60 ml Rye Whiskey or Bourbon", "22.5 ml Simple syrup", "30 ml Fresh lemon juice", "Few Drops of Egg White", "15 ml Red wine (Shiraz or Malbech)"), "Pour all ingredients into the shaker. Shake vigorously with ice. Strain into a chilled rocks glass filled with ice. Float the wine on top.", "Garnish with lemon or orange zest with cherry."),
        "Old Cuban" to CocktailDetails(listOf("6/8 pcs Mint Leaves", "45 ml Aged Rum", "22.5 ml Fresh Lime Juice", "30 ml Simple Syrup", "2 Dashes Angostura Bitters", "60 ml Brut Champagne or Prosecco"), "Pour all ingredients into cocktail shaker except the wine, shake well with ice, strain into chilled elegant cocktail glass. Top up with the sparkling wine.", "Garnish with mint springs."),
        "Old Fashioned" to CocktailDetails(listOf("45 ml Bourbon or Rye Whiskey", "1 Sugar Cube", "Few Dashes Angostura Bitters", "Few Dashes Plain Water"), "Place sugar cube in old fashioned glass and saturate with bitter, add few dashes of plain water. Muddle until dissolved. Fill the glass with ice cubes and add whiskey. Stir gently.", "Garnish with orange slice or zest, and a cocktail cherry."),
        "Paloma" to CocktailDetails(listOf("50 ml 100% Agave Tequila", "5 ml Fresh lime", "A pinch of Salt", "100 ml Pink Grapefruit Soda"), "Poor the tequila into a highball glass, squeeze the lime juice. Add ice and salt, fill up pink grapefruit soda. Stir gently.", "Garnish with a slice of lime."),
        "Paper Plane" to CocktailDetails(listOf("30 ml Bourbon Whiskey", "30 ml Amaro Nonino", "30 ml Aperol", "30 ml Fresh Lemon Juice"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "No garnish listed."),
        "Paradise" to CocktailDetails(listOf("30 ml Gin", "20 ml Apricot Brandy", "15 ml Fresh Orange Juice"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "No garnish listed."),
        "Penicillin" to CocktailDetails(listOf("60 ml Blended scotch whisky", "7.5 ml Lagavulin 16y", "22.5 ml Fresh lemon juice", "22.5 ml Honey syrup", "2-3 quarter size sliced fresh ginger"), "Muddle fresh ginger in a shaker and add the remaining ingredients except for the Islay single malt whisky. Fill the shaker with ice and shake. Fine train into a chilled Old Fashioned glass with ice. Float the single malt whisky on top.", "Garnish with candied ginger slices."),
        "Pina Colada" to CocktailDetails(listOf("50 ml White Rum", "30 ml Coconut Cream", "50 ml Fresh Pineapple Juice"), "Blend all the ingredients with ice in a electric blender, pour into a large glass and serve with straws. Note: Historically a few drops of fresh lime juice was added to taste. 4 slices of fresh pineapple can be used instead of juice", "Garnish with a slice of pineapple with a cocktail cherry."),
        "Pisco Punch" to CocktailDetails(listOf("60 ml Pisco", "22.5 ml Fresh Pineapple Juice", "15 ml Simple Syrup", "15 ml Fresh Lemon Juice", "30 ml Dry White Wine", "3 pcs Cloves"), "Gentle mash the simple syrup with the cloves, add the remaining ingredients except the wine. Shake vigorously and double strain into a large goblet. Add the wine on top and gently stir.", "No garnish listed."),
        "Pisco Sour" to CocktailDetails(listOf("60 ml Pisco", "30 ml Fresh Lemon Juice", "20 ml Simple Syrup", "1 Raw whole Egg White"), "Add all ingredients into a shaker with ice. Shake and strain into a chilled goblet glass.", "Few dashes of Amargo bitters on top as an aromatic garnish."),
        "Planters Punch" to CocktailDetails(listOf("45 ml Jamaican Rum", "15 ml Lime Juice", "30 ml Sugar Cane Juice"), "Pour all ingredients directly in a small tumbler or a typical terracotta glass. NOTE: Add dilution up to taste, it can be given by water, ice or fresh juices.", "Garnish with orange zest."),
        "Porn Star Martini" to CocktailDetails(listOf("50 ml Vanilla Vodka", "20 ml Passion Fruit Liqueur", "50 ml Passion Fruit Puree", "2 Bar Spoons Vanilla Sugar", "50 ml Champagne to serve on the side"), "Pour all ingredients into cocktail shaker, shake well with ice, double strain into a large chilled cocktail glass. Accompany with a shot of champagne.", "Garnish with passion fruit cup and sugar."),
        "Porto Flip" to CocktailDetails(listOf("15 ml Brandy", "45 ml Red Tawny Port Wine", "10 ml Egg Yolk"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "Sprinkle with fresh ground nutmeg."),
        "Rabo de Galo" to CocktailDetails(listOf("60 ml Cachaca", "20 ml Sweet Vermouth Cinzano Rosso", "15 ml Cynar", "2 Drops Angostura (Optional)"), "Combine all the ingredients into a rocks glass, add ice and stir briefly.", "Garnish with a orange twist."),
        "Ramos Fizz" to CocktailDetails(listOf("45 ml Gin", "15 ml Fresh Lime Juice", "15 ml Fresh Lemon Juice", "30 ml Sugar Syrup", "60 ml Cream", "30ml Egg white", "3 Dashes Orange Flower Water", "2 Drops Vanilla Extract", "Soda Water"), "Pour all ingredients except soda water in a cocktail shaker with ice. Shake for two minutes, double strain in a glass, pour the drink back in the shaker and hard shake without ice for one minute. Strain into a highball glass, top up with soda. NOTE: The drink was invented by Henry Ramos in 1888, at his bar Meyer’s Table d’Hôtel Internationale in New Orleans. The Ramos Fizz was originally shaken for 12 minutes by a crew of 30 bartenders who passed the shaker from one to another.", "No garnish listed."),
        "Remember the Maine" to CocktailDetails(listOf("60 ml Rye Whiskey", "22.5 ml Sweet Vermouth", "15 ml Cherry Brandy Luxardo", "7.5 ml Absinthe"), "Pour the absinthe into a coupe glass and swirl to completely coat the inside. Discard the absinthe and set the glass aside. Add the other ingredients to a mixing glass and fill it 3/4 full with ice. Stir until chilled, then strain into the glass rinsed with the absinthe.", "Garnish with lemon zest."),
        "Russian Spring Punch" to CocktailDetails(listOf("25 ml Vodka", "25 ml Fresh Lemon Juice", "15 ml Creme de Cassis", "10 ml Sugar Syrup", "Top up with Sparkling Wine"), "Pour all ingredients into cocktail shaker except the sparkling wine, shake well with ice, strain into chilled tall tumbler glass filled with ice and top up with sparkling wine.", "Garnish with blackberries and optionally a lemon slice as well."),
        "Rusty Nail" to CocktailDetails(listOf("45 ml Scotch Whisky", "25 ml Drambuie"), "Pour all ingredients directly into an old fashioned glass filled with ice. Stir gently.", "Garnish with lemon zest."),
        "Sazerac" to CocktailDetails(listOf("50 ml Cognac", "10 ml Absinthe", "1 Sugar Cube", "2 Dashes Peychaud’s Bitters"), "Rinse a chilled old-fashioned glass with the absinthe, add crushed ice and set it aside. Stir the remaining ingredients over ice in a mixing glass. Discard the ice and any excess absinthe from the prepared glass, strain the mixed drink into the glass. NOTE: The original recipe changed after the American Civil War, Rye Whiskey substituted Cognac as it became hard to obtain.", "Garnish with lemon zest."),
        "Sea Breeze" to CocktailDetails(listOf("40 ml Vodka", "120 ml Cranberry Juice", "30 ml Grapefruit Juice"), "Build all ingredients in a highball glass filled with ice.", "Garnish with an orange zest and cherry."),
        "Sex on the Beach" to CocktailDetails(listOf("40 ml Vodka", "20 ml Peach Schnapps", "40 ml Fresh Orange Juice", "40 ml Cranberry Juice"), "Build all ingredients in a highball glass filled with ice.", "Garnish with half orange slice."),
        "Sherry Cobbler" to CocktailDetails(listOf("45 ml Amontillado sherry", "45 ml Palo Cortado", "1 tsp Superfine Sugar (or granulated)", "1/2 Orange Wheel", "1/2 Lemon Wheel"), "Combine sherry, sugar and 2 quarter wheels each of orange and lemon in a shaker with ice, shake briskly, strain into a Julep cocktail cup filled with crushed ice.", "Garnish with fresh berries, ¼ wheel each orange and lemon. Serve with straws."),
        "Sidecar" to CocktailDetails(listOf("50 ml Cognac", "20 ml Triple Sec", "20 ml Fresh Lemon Juice"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "No garnish listed."),
        "Singapore Sling" to CocktailDetails(listOf("30 ml Gin", "15 ml Cherry Sangue Morlacco", "7.5 ml Cointreau", "7.5 ml DOM Bénédictine", "120 ml Fresh Pineapple Juice", "15 ml Fresh Lime Juice", "10 ml Grenadine Syrup", "A dash of Angostura bitters"), "Pour all ingredients into cocktail shaker filled with ice cubes. Shake well. Strain into Hurricane glass.", "Garnish with pineapple and maraschino cherry."),
        "South Side" to CocktailDetails(listOf("60 ml London dry Gin", "30 ml Fresh Lemon Juice", "15 ml Simple syrup", "5/6 Mint leaves", "Few drops Egg white (Optional)"), "Pour all ingredients into a cocktail shaker, shake well with ice, double-strain into chilled cocktail glass. Note: If egg white is used shake vigorously.", "Garnish with mint springs."),
        "Spicy Fifty" to CocktailDetails(listOf("50 ml Vodka Vanilla", "15 ml Elderflower Cordial", "15 ml Fresh Lime Juice", "10 ml Monin Honey Syrup", "2 thin Slices Red Chili Pepper"), "Pour all ingredients into a cocktail shaker, shake well with ice, double-strain into chilled cocktail glass.", "Garnish with a red chili pepper."),
        "Spritz" to CocktailDetails(listOf("90 ml Prosecco", "60 ml Aperol", "Splash of Soda water"), "Build all ingredients into a wine glass filled with ice. Stir gently. NOTE: There are other versions of the Spritz that use Campari, Cynar or Select instead of Aperol.", "Garnish with a slice of orange."),
        "Stinger" to CocktailDetails(listOf("50 ml Cognac", "20 ml White Crème de Menthe"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled martini cocktail glass.", "Optional mint leave."),
        "Suffering Bastard" to CocktailDetails(listOf("30 ml Cognac or Brandy", "30 ml Gin", "15 ml Fresh Lime Juice", "2 Dashes Angostura Bitters", "Top up Ginger beer"), "Pour all ingredients into cocktail shaker except the ginger beer, shake well with ice. Pour unstrained into a Collins glass or in the original. S. Bastard mug and top up with ginger beer.", "Garnish with mint spring and optionally an orange slice as well."),
        "Tequila Sunrise" to CocktailDetails(listOf("45 ml Tequila", "90 ml Fresh Orange Juice", "15 ml Grenadine Syrup"), "Pour tequila and orange juice directly into highball glass filled with ice cubes. Add the grenadine syrup to create chromatic effect (sunrise), do not stir.", "Garnish with half orange slice or an orange zest."),
        "Three Dots and a Dash" to CocktailDetails(listOf("45 ml Rhum Martinique Agricole", "15 ml Blended Aged Rum", "7.5 ml Falernum", "7.5 ml Allspice Saint Elizabeth15 ml Fresh Lime Juice", "15 ml Fresh Orange juice", "15 ml Honey Syrup", "2 Dashes Angostura Bitters"), "Pour all ingredients in a Blender with 12 ounces of crushed ice, flash blend, pour the drink into a footed copo glass. Fill the glass with more crushed ice.", "Garnish with three cherries and a rectangular chunk of pineapple."),
        "Tipperary" to CocktailDetails(listOf("50 ml Irish Whiskey", "25 ml Sweet Red Vermouth", "15 ml Green Chartreuse", "2 Dashes Angostura Bitters"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled martini cocktail glass.", "Garnish with a slice of orange."),
        "Tommy's Margarita" to CocktailDetails(listOf("60 ml Tequila 100% agave", "30 ml Fresh Lime Juice", "30 ml Agave Nectar"), "Pour all ingredients into a cocktail shaker, shake well with ice, strain into chilled rocks glass filled with ice.", "Garnish with a lime slice."),
        "Trinidad Sour" to CocktailDetails(listOf("45 ml Angostura Bitters", "30 ml Orgeat Syrup", "22.5 ml Fresh Lemon Juice", "15 ml Rye Whiskey"), "Pour all ingredients into a cocktail shaker, shake well with ice. Strain into chilled cocktail glass.", "No garnish listed."),
        "Tuxedo" to CocktailDetails(listOf("30 ml Old Tom Gin", "30 ml Dry Vermouth", "1/2 Bar Spoon Maraschino Luxardo", "1/4 Bar Spoon of Absinthe", "3 Dashes Orange Bitters"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled martini cocktail glass.", "Garnish with cherry and lemon zest."),
        "Ve.N.To" to CocktailDetails(listOf("45 ml White Smooth Grappa", "22.5 ml Fresh lemon Juice", "15 ml Honey mix (replace water with chamomile)*", "15 ml Chamomile cordial", "Few Drops of Egg White (Optional)"), "Pour all ingredients into the shaker. Shake vigorously with ice. Strain into a chilled small tumbler glass filled with ice. NOTE: *If desired water can be replaced by chamomile infusion in the honey mix.", "Garnish with lemon zest and white grapes."),
        "Vesper" to CocktailDetails(listOf("45 ml Gin", "15 ml Vodka", "7.5 ml Lillet Blanc"), "Pour all ingredients into cocktail shaker filled with ice cubes. Shake and strain into a chilled cocktail glass.", "Garnish with lemon zest."),
        "Vieux Carré" to CocktailDetails(listOf("30 ml Rye Whiskey", "30 ml Cognac", "30 ml Sweet Vermouth", "1 Bar Spoon Bénédictine", "2 Dashes Peychaud’s Bitters"), "Pour all ingredients into mixing glass with ice cubes. Stir well. Strain into chilled cocktail glass.", "Garnish with orange zest and maraschino cherry."),
        "Whiskey Sour" to CocktailDetails(listOf("45 ml Bourbon Whiskey", "25 ml Fresh Lemon Juice", "20 ml Sugar Syrup", "Few Drops of Egg White (Optional)"), "Pour all ingredients into cocktail shaker filled with ice. Shake well. Strain into cobbler glass. If served “On the rocks”, strain ingredients into old fashioned glass filled with ice. NOTE: If egg white is used shake little harder to release and incorporate the foam from the egg white.", "Garnish with half orange slice and maraschino cherry, optionally use orange zest."),
        "White Lady" to CocktailDetails(listOf("40 ml Gin", "30 ml Triple Sec", "20 ml Fresh Lemon Juice"), "Pour all ingredients into cocktail shaker, shake well with ice, strain into chilled cocktail glass.", "No garnish listed."),
        "Zombie" to CocktailDetails(listOf("45 ml Jamaican dark rum", "45 ml Gold Puerto Rican rum", "30 ml Demerara Rum", "20 ml Fresh lime juice", "15 ml Falernum", "15 ml Donn’s Mix*", "1 tsp Grenadine syrup", "1 dash Angostura bitters", "6 drops Pernod"), "Add all ingredients into an electric blender with 170 grams of cracked ice. With pulse bottom blend for a few seconds. Serve in a tall tumbler glass. Note: *Donn’s Mix: 2 parts of fresh yellow grapefruit and 1 part of cinnamon syrup", "Garnish with mint leaves.")
    )
    private val drinkLayers = mapOf(
        "Alexander" to listOf(DrinkLayer.CREAM, DrinkLayer.COCOA, DrinkLayer.BRANDY),
        "Americano" to listOf(DrinkLayer.COLA, DrinkLayer.CAMPARI, DrinkLayer.VERMOUTH),
        "Angel Face" to listOf(DrinkLayer.APRICOT, DrinkLayer.BRANDY, DrinkLayer.CLEAR),
        "Aviation" to listOf(DrinkLayer.VIOLET, DrinkLayer.CLOUD, DrinkLayer.CLEAR),
        "Between the Sheets" to listOf(DrinkLayer.BRANDY, DrinkLayer.CITRUS, DrinkLayer.CLEAR),
        "Boulevardier" to listOf(DrinkLayer.VERMOUTH, DrinkLayer.CAMPARI, DrinkLayer.BOURBON),
        "Brandy Crusta" to listOf(DrinkLayer.AMBER, DrinkLayer.CITRUS, DrinkLayer.SUGAR),
        "Casino" to listOf(DrinkLayer.CHERRY, DrinkLayer.CITRUS, DrinkLayer.CLEAR),
        "Clover Club" to listOf(DrinkLayer.RASPBERRY, DrinkLayer.FOAM),
        "Daiquiri" to listOf(DrinkLayer.LIME, DrinkLayer.WHITE),
        "Dry Martini" to listOf(DrinkLayer.OLIVE, DrinkLayer.CLEAR),
        "Gin Fizz" to listOf(DrinkLayer.FOAM, DrinkLayer.CITRUS, DrinkLayer.CLEAR),
        "Hanky Panky" to listOf(DrinkLayer.VERMOUTH, DrinkLayer.AMBER, DrinkLayer.CLEAR),
        "John Collins" to listOf(DrinkLayer.CITRUS, DrinkLayer.CLEAR),
        "Last Word" to listOf(DrinkLayer.CHARTREUSE, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "Manhattan" to listOf(DrinkLayer.VERMOUTH, DrinkLayer.CHERRY, DrinkLayer.BOURBON),
        "Martinez" to listOf(DrinkLayer.VERMOUTH, DrinkLayer.AMBER, DrinkLayer.CLEAR),
        "Mary Pickford" to listOf(DrinkLayer.PINEAPPLE, DrinkLayer.CHERRY, DrinkLayer.CLEAR),
        "Monkey Gland" to listOf(DrinkLayer.ORANGE, DrinkLayer.GRENADINE, DrinkLayer.CLEAR),
        "Negroni" to listOf(DrinkLayer.VERMOUTH, DrinkLayer.CAMPARI, DrinkLayer.GIN),
        "Old Fashioned" to listOf(DrinkLayer.BOURBON, DrinkLayer.ORANGE),
        "Paradise" to listOf(DrinkLayer.APRICOT, DrinkLayer.ORANGE, DrinkLayer.CLEAR),
        "Planters Punch" to listOf(DrinkLayer.DARK_RUM, DrinkLayer.GRENADINE, DrinkLayer.CITRUS),
        "Porto Flip" to listOf(DrinkLayer.RUBY_PORT, DrinkLayer.EGG, DrinkLayer.BRANDY),
        "Ramos Fizz" to listOf(DrinkLayer.FOAM, DrinkLayer.CREAM, DrinkLayer.CLEAR),
        "Remember the Maine" to listOf(DrinkLayer.VERMOUTH, DrinkLayer.CHERRY, DrinkLayer.WHISKEY),
        "Rusty Nail" to listOf(DrinkLayer.HONEY, DrinkLayer.WHISKEY),
        "Sazerac" to listOf(DrinkLayer.AMBER, DrinkLayer.ANISE, DrinkLayer.WHISKEY),
        "Sidecar" to listOf(DrinkLayer.BRANDY, DrinkLayer.CITRUS, DrinkLayer.SUGAR),
        "Stinger" to listOf(DrinkLayer.CREAM, DrinkLayer.MINT, DrinkLayer.BRANDY),
        "Tuxedo" to listOf(DrinkLayer.OLIVE, DrinkLayer.ANISE, DrinkLayer.CLEAR),
        "Vieux Carré" to listOf(DrinkLayer.VERMOUTH, DrinkLayer.AMBER, DrinkLayer.WHISKEY),
        "Whiskey Sour" to listOf(DrinkLayer.FOAM, DrinkLayer.LEMON, DrinkLayer.WHISKEY),
        "White Lady" to listOf(DrinkLayer.FOAM, DrinkLayer.LEMON, DrinkLayer.CLEAR),
        "Bellini" to listOf(DrinkLayer.PEACH, DrinkLayer.SPARKLING),
        "Black Russian" to listOf(DrinkLayer.COFFEE, DrinkLayer.CLEAR),
        "Bloody Mary" to listOf(DrinkLayer.TOMATO, DrinkLayer.SPICE),
        "Caipirinha" to listOf(DrinkLayer.LIME_BRIGHT, DrinkLayer.CLEAR),
        "Cardinale" to listOf(DrinkLayer.CAMPARI, DrinkLayer.VERMOUTH, DrinkLayer.CLEAR),
        "Champagne Cocktail" to listOf(DrinkLayer.SPARKLING, DrinkLayer.BITTERS, DrinkLayer.SUGAR),
        "Corpse Reviver #2" to listOf(DrinkLayer.ORANGE, DrinkLayer.ANISE, DrinkLayer.CLEAR),
        "Cosmopolitan" to listOf(DrinkLayer.CRANBERRY, DrinkLayer.CITRUS, DrinkLayer.CLEAR),
        "Cuba Libre" to listOf(DrinkLayer.COLA, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "French 75" to listOf(DrinkLayer.SPARKLING, DrinkLayer.LEMON, DrinkLayer.CLEAR),
        "French Connection" to listOf(DrinkLayer.AMARETTO, DrinkLayer.BRANDY),
        "Garibaldi" to listOf(DrinkLayer.ORANGE, DrinkLayer.CAMPARI),
        "Grasshopper" to listOf(DrinkLayer.MINT, DrinkLayer.CREAM, DrinkLayer.COCOA),
        "Hemingway Special" to listOf(DrinkLayer.GRAPEFRUIT, DrinkLayer.CHERRY, DrinkLayer.CLEAR),
        "Horse’s Neck" to listOf(DrinkLayer.GINGER, DrinkLayer.BRANDY),
        "Irish Coffee" to listOf(DrinkLayer.CREAM, DrinkLayer.COFFEE, DrinkLayer.WHISKEY),
        "Kir" to listOf(DrinkLayer.BERRY, DrinkLayer.WHITE),
        "Lemon Drop Martini" to listOf(DrinkLayer.LEMON, DrinkLayer.SUGAR, DrinkLayer.CLEAR),
        "Long Island Iced Tea" to listOf(DrinkLayer.ORANGE, DrinkLayer.BLUE, DrinkLayer.LIME, DrinkLayer.COLA),
        "Mai-Tai" to listOf(DrinkLayer.DARK_RUM, DrinkLayer.ALMOND, DrinkLayer.LIME),
        "Margarita" to listOf(DrinkLayer.LIME_BRIGHT, DrinkLayer.CREAM),
        "Mimosa" to listOf(DrinkLayer.ORANGE, DrinkLayer.SPARKLING),
        "Mint Julep" to listOf(DrinkLayer.MINT, DrinkLayer.WHISKEY),
        "Mojito" to listOf(DrinkLayer.MINT, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "Moscow Mule" to listOf(DrinkLayer.GINGER, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "Pina Colada" to listOf(DrinkLayer.CREAM, DrinkLayer.PINEAPPLE, DrinkLayer.CLEAR),
        "Pisco Sour" to listOf(DrinkLayer.FOAM, DrinkLayer.LEMON, DrinkLayer.CLEAR),
        "Rabo de Galo" to listOf(DrinkLayer.VERMOUTH, DrinkLayer.DARK_AMBER),
        "Sea Breeze" to listOf(DrinkLayer.CRANBERRY, DrinkLayer.GRAPEFRUIT, DrinkLayer.CLEAR),
        "Sex on the Beach" to listOf(DrinkLayer.CRANBERRY, DrinkLayer.ORANGE, DrinkLayer.PEACH),
        "Singapore Sling" to listOf(DrinkLayer.GRENADINE, DrinkLayer.PINEAPPLE, DrinkLayer.CLEAR),
        "Tequila Sunrise" to listOf(DrinkLayer.ORANGE, DrinkLayer.GRENADINE),
        "Vesper" to listOf(DrinkLayer.LEMON, DrinkLayer.CLEAR),
        "Zombie" to listOf(DrinkLayer.DARK_RUM, DrinkLayer.ORANGE, DrinkLayer.GRENADINE),
        "Bee’s Knees" to listOf(DrinkLayer.HONEY, DrinkLayer.LEMON, DrinkLayer.CLEAR),
        "Bramble" to listOf(DrinkLayer.BERRY, DrinkLayer.LEMON, DrinkLayer.CLEAR),
        "Canchanchara" to listOf(DrinkLayer.HONEY, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "Chartreuse Swizzle" to listOf(DrinkLayer.CHARTREUSE, DrinkLayer.PINEAPPLE, DrinkLayer.LIME),
        "Dark ‘N’ Stormy" to listOf(DrinkLayer.COLA, DrinkLayer.GINGER, DrinkLayer.DARK_RUM),
        "Don's Special Daiquiri" to listOf(DrinkLayer.PASSION, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "Espresso Martini" to listOf(DrinkLayer.FOAM, DrinkLayer.COFFEE, DrinkLayer.BLACK),
        "Fernandito" to listOf(DrinkLayer.COLA, DrinkLayer.FERNET),
        "French Martini" to listOf(DrinkLayer.RASPBERRY, DrinkLayer.PINEAPPLE, DrinkLayer.CLEAR),
        "Gin Basil Smash" to listOf(DrinkLayer.BASIL, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "Grand Margarita" to listOf(DrinkLayer.ORANGE, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "IBA Tiki" to listOf(DrinkLayer.DARK_RUM, DrinkLayer.PINEAPPLE, DrinkLayer.GRENADINE),
        "Illegal" to listOf(DrinkLayer.SMOKE, DrinkLayer.LIME, DrinkLayer.AMBER),
        "Jungle Bird" to listOf(DrinkLayer.PINEAPPLE, DrinkLayer.CAMPARI, DrinkLayer.DARK_RUM),
        "Missionary's Downfall" to listOf(DrinkLayer.MINT, DrinkLayer.PINEAPPLE, DrinkLayer.CLEAR),
        "Naked and Famous" to listOf(DrinkLayer.ORANGE, DrinkLayer.CHARTREUSE, DrinkLayer.AMBER),
        "New York Sour" to listOf(DrinkLayer.RED_WINE, DrinkLayer.FOAM, DrinkLayer.WHISKEY),
        "Old Cuban" to listOf(DrinkLayer.SPARKLING, DrinkLayer.MINT, DrinkLayer.DARK_RUM),
        "Paloma" to listOf(DrinkLayer.GRAPEFRUIT, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "Paper Plane" to listOf(DrinkLayer.ORANGE, DrinkLayer.AMARO, DrinkLayer.BOURBON),
        "Penicillin" to listOf(DrinkLayer.HONEY, DrinkLayer.GINGER, DrinkLayer.WHISKEY),
        "Pisco Punch" to listOf(DrinkLayer.PINEAPPLE, DrinkLayer.LEMON, DrinkLayer.CLEAR),
        "Porn Star Martini" to listOf(DrinkLayer.PASSION, DrinkLayer.VANILLA, DrinkLayer.SPARKLING),
        "Russian Spring Punch" to listOf(DrinkLayer.BERRY, DrinkLayer.SPARKLING, DrinkLayer.CLEAR),
        "Sherry Cobbler" to listOf(DrinkLayer.ORANGE, DrinkLayer.SHERRY, DrinkLayer.BERRY),
        "South Side" to listOf(DrinkLayer.MINT, DrinkLayer.LIME, DrinkLayer.CLEAR),
        "Spicy Fifty" to listOf(DrinkLayer.CHILI, DrinkLayer.HONEY, DrinkLayer.CLEAR),
        "Spritz" to listOf(DrinkLayer.ORANGE, DrinkLayer.SPARKLING),
        "Suffering Bastard" to listOf(DrinkLayer.GINGER, DrinkLayer.LIME, DrinkLayer.BRANDY),
        "Three Dots and a Dash" to listOf(DrinkLayer.DARK_RUM, DrinkLayer.HONEY, DrinkLayer.ORANGE),
        "Tipperary" to listOf(DrinkLayer.CHARTREUSE, DrinkLayer.VERMOUTH, DrinkLayer.WHISKEY),
        "Tommy's Margarita" to listOf(DrinkLayer.LIME_BRIGHT, DrinkLayer.AGAVE, DrinkLayer.CLEAR),
        "Trinidad Sour" to listOf(DrinkLayer.BITTERS, DrinkLayer.ALMOND, DrinkLayer.LEMON),
        "Ve.N.To" to listOf(DrinkLayer.HONEY, DrinkLayer.LEMON, DrinkLayer.GRAPPA)
    )
}
