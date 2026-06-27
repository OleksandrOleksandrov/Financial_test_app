package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.FinanceApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FinanceViewModel
import com.example.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels {
        FinanceViewModelFactory((application as FinanceApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FinanceApp(viewModel)
            }
        }
    }
}
