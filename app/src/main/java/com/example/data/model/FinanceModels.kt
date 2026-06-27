package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isIncome: Boolean
)

@Entity(tableName = "category_budgets")
data class CategoryBudget(
    @PrimaryKey val category: String,
    val limitAmount: Double
)

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val notes: String = ""
)
