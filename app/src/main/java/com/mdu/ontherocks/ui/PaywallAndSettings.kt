package com.mdu.ontherocks.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.mdu.ontherocks.ui.components.HeaderBlock
import com.mdu.ontherocks.ui.components.SectionLabel
import com.mdu.ontherocks.ui.theme.AppBackground
import com.mdu.ontherocks.ui.theme.AppGold
import com.mdu.ontherocks.ui.theme.AppMuted
import com.mdu.ontherocks.ui.theme.AppSurface
import com.mdu.ontherocks.ui.theme.AppText

@Composable
fun UpgradeSheet(onDismiss: () -> Unit, onPurchase: () -> Unit) {
    val context = LocalContext.current
    val features = listOf(
        "Unlimited custom cocktails" to "Free users can create 2.",
        "Ingredient substitutions" to "Useful swaps for home bartenders.",
        "Add own ingredients" to "Add your bottles, syrups and garnishes.",
        "Shopping list" to "Save missing ingredients.",
        "Custom collections" to "Build Date Night, Summer, Whiskey and more.",
        "Recipe notes and ratings" to "Add notes to custom cocktails.",
        "Custom app icons" to "Choose your home screen look."
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground.copy(alpha = 0.72f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {},
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .background(AppBackground)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("ON THE ROCKS PRO")
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "×",
                    color = AppText,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AppSurface)
                        .clickable(onClick = onDismiss)
                        .padding(top = 1.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            Text("Become a\nMixologist", color = AppText, fontFamily = FontFamily.Serif, fontSize = 42.sp, lineHeight = 46.sp)
            Text("Unlock the tools to build a personal cocktail bar.", color = AppMuted, fontSize = 16.sp, lineHeight = 23.sp)
            features.forEach { feature ->
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AppSurface).padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.padding(top = 6.dp).size(9.dp).clip(CircleShape).background(AppGold))
                    Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text(feature.first, color = AppText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(feature.second, color = AppMuted, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
            }
            Text(
                text = "Upgrade to Pro - 1.99",
                color = AppBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(AppGold)
                    .clickable {
                        launchProPurchase(context)
                        onPurchase()
                    }
                    .padding(top = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun SettingsSheet(
    deletedCocktails: List<String>,
    onPaidFeature: () -> Unit,
    onRestore: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 54.dp, end = 24.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HeaderBlock(eyebrow = "SETTINGS", title = "Settings")
            SettingsRow("Shopping Cart", onPaidFeature)
            SettingsRow("My Pantry", onPaidFeature)
            SettingsRow("Change App Icon", onPaidFeature)
            SectionLabel("RESTORE COCKTAILS")
            if (deletedCocktails.isEmpty()) {
                Text("Deleted original cocktails will appear here.", color = AppMuted, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AppSurface).padding(16.dp))
            } else {
                deletedCocktails.forEach { name ->
                    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AppSurface).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(name, color = AppText, modifier = Modifier.weight(1f))
                        Text("Restore", color = AppGold, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onRestore(name) })
                    }
                }
            }
            Text("Close", color = AppMuted, modifier = Modifier.fillMaxWidth().clickable(onClick = onDismiss).padding(16.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

private fun launchProPurchase(context: Context) {
    val activity = context.findActivity() ?: return
    val listener = PurchasesUpdatedListener { _, _ -> }
    val client = BillingClient.newBuilder(context)
        .enablePendingPurchases()
        .setListener(listener)
        .build()
    client.startConnection(object : BillingClientStateListener {
        override fun onBillingServiceDisconnected() = Unit
        override fun onBillingSetupFinished(result: BillingResult) {
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return
            val product = QueryProductDetailsParams.Product.newBuilder()
                .setProductId("pro_upgrade")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(listOf(product))
                .build()
            client.queryProductDetailsAsync(params) { _, productDetails ->
                val details = productDetails.firstOrNull() ?: return@queryProductDetailsAsync
                val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .build()
                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(productParams))
                    .build()
                client.launchBillingFlow(activity, flowParams)
            }
        }
    })
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun SettingsRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(AppSurface).clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = AppText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("PRO", color = AppGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
