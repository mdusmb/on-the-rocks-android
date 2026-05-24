package com.mdu.ontherocks.data

import androidx.compose.ui.graphics.Color

enum class DrinkLayer {
    VERMOUTH, CAMPARI, GIN, LIME, WHITE, BOURBON, ORANGE, LIME_BRIGHT, CREAM, PALE,
    CLEAR, AMBER, DEEP_AMBER, DARK_AMBER, WHISKEY, BRANDY, DARK_RUM, RUBY_PORT, SHERRY, GRAPPA,
    CITRUS, LEMON, GRAPEFRUIT, PINEAPPLE, PEACH, APRICOT, PASSION, CRANBERRY, RASPBERRY, BERRY, CHERRY,
    COLA, COFFEE, COCOA, TOMATO, GINGER, HONEY, AGAVE, VANILLA, ALMOND, AMARETTO, BITTERS,
    MINT, BASIL, OLIVE, ANISE, CHARTREUSE, SPARKLING, SUGAR, EGG, FOAM, GRENADINE,
    BLUE, VIOLET, CLOUD, CHILI, SPICE, SMOKE, FERNET, AMARO, BLACK, RED_WINE, BRIGHT;

    val color: Color get() = when (this) {
        VERMOUTH -> Color(0.478f, 0.122f, 0.122f)
        CAMPARI -> Color(0.765f, 0.271f, 0.212f)
        GIN -> Color(0.851f, 0.851f, 0.851f)
        LIME -> Color(0.780f, 0.859f, 0.620f)
        WHITE -> Color(0.820f, 0.820f, 0.820f)
        BOURBON -> Color(0.522f, 0.251f, 0.102f)
        ORANGE -> Color(0.859f, 0.561f, 0.239f)
        LIME_BRIGHT -> Color(0.722f, 0.831f, 0.451f)
        CREAM -> Color(0.898f, 0.898f, 0.820f)
        PALE -> Color(0.820f, 0.820f, 0.859f)
        CLEAR -> Color(0.900f, 0.900f, 0.880f)
        AMBER -> Color(0.780f, 0.510f, 0.210f)
        DEEP_AMBER -> Color(0.600f, 0.280f, 0.090f)
        DARK_AMBER -> Color(0.390f, 0.180f, 0.070f)
        WHISKEY -> Color(0.610f, 0.300f, 0.100f)
        BRANDY -> Color(0.620f, 0.330f, 0.150f)
        DARK_RUM -> Color(0.240f, 0.110f, 0.060f)
        RUBY_PORT -> Color(0.420f, 0.070f, 0.120f)
        SHERRY -> Color(0.700f, 0.380f, 0.180f)
        GRAPPA -> Color(0.890f, 0.860f, 0.730f)
        CITRUS -> Color(0.930f, 0.780f, 0.270f)
        LEMON -> Color(0.960f, 0.860f, 0.300f)
        GRAPEFRUIT -> Color(0.930f, 0.450f, 0.380f)
        PINEAPPLE -> Color(0.930f, 0.740f, 0.270f)
        PEACH -> Color(0.930f, 0.550f, 0.360f)
        APRICOT -> Color(0.900f, 0.490f, 0.210f)
        PASSION -> Color(0.950f, 0.620f, 0.120f)
        CRANBERRY -> Color(0.690f, 0.090f, 0.180f)
        RASPBERRY -> Color(0.780f, 0.220f, 0.350f)
        BERRY -> Color(0.520f, 0.080f, 0.260f)
        CHERRY -> Color(0.640f, 0.060f, 0.100f)
        COLA -> Color(0.150f, 0.050f, 0.030f)
        COFFEE -> Color(0.130f, 0.080f, 0.050f)
        COCOA -> Color(0.300f, 0.170f, 0.110f)
        TOMATO -> Color(0.710f, 0.070f, 0.040f)
        GINGER -> Color(0.720f, 0.520f, 0.270f)
        HONEY -> Color(0.900f, 0.620f, 0.180f)
        AGAVE -> Color(0.840f, 0.720f, 0.420f)
        VANILLA -> Color(0.890f, 0.760f, 0.520f)
        ALMOND -> Color(0.720f, 0.600f, 0.430f)
        AMARETTO -> Color(0.520f, 0.260f, 0.090f)
        BITTERS -> Color(0.540f, 0.120f, 0.080f)
        MINT -> Color(0.330f, 0.620f, 0.380f)
        BASIL -> Color(0.150f, 0.500f, 0.240f)
        OLIVE -> Color(0.520f, 0.580f, 0.260f)
        ANISE -> Color(0.850f, 0.880f, 0.740f)
        CHARTREUSE -> Color(0.710f, 0.820f, 0.250f)
        SPARKLING -> Color(0.900f, 0.820f, 0.540f)
        SUGAR -> Color(0.940f, 0.910f, 0.820f)
        EGG -> Color(0.920f, 0.780f, 0.440f)
        FOAM -> Color(0.940f, 0.920f, 0.840f)
        GRENADINE -> Color(0.770f, 0.060f, 0.120f)
        BLUE -> Color(0.400f, 0.780f, 0.830f)
        VIOLET -> Color(0.500f, 0.390f, 0.710f)
        CLOUD -> Color(0.770f, 0.820f, 0.860f)
        CHILI -> Color(0.780f, 0.090f, 0.060f)
        SPICE -> Color(0.470f, 0.120f, 0.060f)
        SMOKE -> Color(0.360f, 0.340f, 0.310f)
        FERNET -> Color(0.100f, 0.060f, 0.040f)
        AMARO -> Color(0.430f, 0.150f, 0.090f)
        BLACK -> Color(0.020f, 0.018f, 0.015f)
        RED_WINE -> Color(0.460f, 0.040f, 0.100f)
        BRIGHT -> Color(0.820f, 0.880f, 0.360f)
    }

    val title: String get() = when (this) {
        VERMOUTH -> "Vermouth"
        CAMPARI -> "Campari"
        GIN -> "Gin"
        LIME, LIME_BRIGHT -> "Lime"
        WHITE -> "White spirit"
        BOURBON -> "Bourbon"
        ORANGE -> "Orange"
        CREAM -> "Cream"
        PALE -> "Pale mixer"
        CLEAR -> "Clear spirit"
        AMBER, DEEP_AMBER, DARK_AMBER -> "Amber spirit"
        WHISKEY -> "Whiskey"
        BRANDY -> "Brandy"
        DARK_RUM -> "Dark rum"
        RUBY_PORT -> "Ruby port"
        SHERRY -> "Sherry"
        GRAPPA -> "Grappa"
        CITRUS -> "Citrus"
        LEMON -> "Lemon"
        GRAPEFRUIT -> "Grapefruit"
        PINEAPPLE -> "Pineapple"
        PEACH -> "Peach"
        APRICOT -> "Apricot"
        PASSION -> "Passion fruit"
        CRANBERRY -> "Cranberry"
        RASPBERRY -> "Raspberry"
        BERRY -> "Berry"
        CHERRY -> "Cherry"
        COLA -> "Cola"
        COFFEE -> "Coffee"
        COCOA -> "Cocoa"
        TOMATO -> "Tomato"
        GINGER -> "Ginger"
        HONEY -> "Honey"
        AGAVE -> "Agave"
        VANILLA -> "Vanilla"
        ALMOND -> "Almond"
        AMARETTO -> "Amaretto"
        BITTERS -> "Bitters"
        MINT -> "Mint"
        BASIL -> "Basil"
        OLIVE -> "Olive"
        ANISE -> "Anise"
        CHARTREUSE -> "Chartreuse"
        SPARKLING -> "Sparkling wine"
        SUGAR -> "Sugar"
        EGG -> "Egg"
        FOAM -> "Foam"
        GRENADINE -> "Grenadine"
        BLUE -> "Blue curacao"
        VIOLET -> "Violet"
        CLOUD -> "Cloudy citrus"
        CHILI -> "Chili"
        SPICE -> "Spice"
        SMOKE -> "Smoke"
        FERNET -> "Fernet"
        AMARO -> "Amaro"
        BLACK -> "Dark liqueur"
        RED_WINE -> "Red wine"
        BRIGHT -> "Bright citrus"
    }

    val flavorNote: String get() = when (this) {
        VERMOUTH, RUBY_PORT, SHERRY, RED_WINE -> "winey"
        CAMPARI, BITTERS, AMARO, FERNET -> "bitter"
        GIN, ANISE, OLIVE -> "botanical"
        LIME, LIME_BRIGHT, LEMON, CITRUS, GRAPEFRUIT -> "bright"
        WHITE, CLEAR, SPARKLING -> "crisp"
        BOURBON, WHISKEY, BRANDY, DARK_RUM, AMBER, DEEP_AMBER, DARK_AMBER, GRAPPA -> "spirit-forward"
        ORANGE, PEACH, APRICOT, PASSION, PINEAPPLE -> "fruity"
        CREAM, FOAM, EGG, VANILLA -> "silky"
        COLA, COFFEE, COCOA, BLACK -> "dark"
        TOMATO, SPICE, CHILI, SMOKE -> "savory"
        GINGER, HONEY, AGAVE, SUGAR, ALMOND, AMARETTO -> "rounded"
        MINT, BASIL, CHARTREUSE -> "herbal"
        CRANBERRY, RASPBERRY, BERRY, CHERRY, GRENADINE -> "tart"
        BLUE, VIOLET, CLOUD, BRIGHT, PALE -> "delicate"
    }
}
