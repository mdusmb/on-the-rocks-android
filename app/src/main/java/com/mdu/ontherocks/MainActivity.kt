package com.mdu.ontherocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mdu.ontherocks.ui.OnTheRocksApp
import com.mdu.ontherocks.ui.theme.OnTheRocksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnTheRocksTheme {
                OnTheRocksApp()
            }
        }
    }
}
