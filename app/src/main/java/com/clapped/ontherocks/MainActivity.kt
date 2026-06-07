package com.clapped.ontherocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.clapped.ontherocks.ui.OnTheRocksApp
import com.clapped.ontherocks.ui.theme.OnTheRocksTheme

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
