package com.example.data.local

import androidx.room.*
import com.example.data.model.CategoryBudget
import com.example.data.model.SavingsGoal
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    // === TRANSACTIONS ===
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)

    // === BUDGETS ===
    @Query("SELECT * FROM category_budgets")
    fun getAllBudgets(): Flow<List<CategoryBudget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: CategoryBudget)

    @Delete
    suspend fun deleteBudget(budget: CategoryBudget)

    // === SAVINGS GOALS ===
    @Query("SELECT * FROM savings_goals")
    fun getAllSavingsGoals(): Flow<List<SavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoal)

    @Delete
    suspend fun deleteSavingsGoal(goal: SavingsGoal)

    @Query("UPDATE savings_goals SET currentAmount = :currentAmount WHERE id = :id")
    suspend fun updateSavingsAmount(id: Int, currentAmount: Double)
}
