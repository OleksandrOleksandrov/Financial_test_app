package com.example

import android.app.Application
import com.example.data.local.FinanceDatabase
import com.example.data.repository.FinanceRepository

class FinanceApplication : Application() {
    val database by lazy { FinanceDatabase.getDatabase(this) }
    val repository by lazy { FinanceRepository(database.financeDao()) }
}
