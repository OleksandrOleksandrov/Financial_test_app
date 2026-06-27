package com.example.data.repository

import com.example.data.local.FinanceDao
import com.example.data.model.CategoryBudget
import com.example.data.model.SavingsGoal
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val financeDao: FinanceDao) {
    val allTransactions: Flow<List<Transaction>> = financeDao.getAllTransactions()
    val allBudgets: Flow<List<CategoryBudget>> = financeDao.getAllBudgets()
    val allSavingsGoals: Flow<List<SavingsGoal>> = financeDao.getAllSavingsGoals()

    suspend fun insertTransaction(transaction: Transaction) {
        financeDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        financeDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        financeDao.deleteTransactionById(id)
    }

    suspend fun insertBudget(budget: CategoryBudget) {
        financeDao.insertBudget(budget)
    }

    suspend fun deleteBudget(budget: CategoryBudget) {
        financeDao.deleteBudget(budget)
    }

    suspend fun insertSavingsGoal(goal: SavingsGoal) {
        financeDao.insertSavingsGoal(goal)
    }

    suspend fun deleteSavingsGoal(goal: SavingsGoal) {
        financeDao.deleteSavingsGoal(goal)
    }

    suspend fun updateSavingsAmount(id: Int, currentAmount: Double) {
        financeDao.updateSavingsAmount(id, currentAmount)
    }
}
