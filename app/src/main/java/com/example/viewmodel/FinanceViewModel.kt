package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CategoryBudget
import com.example.data.model.SavingsGoal
import com.example.data.model.Transaction
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<CategoryBudget>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savingsGoals: StateFlow<List<SavingsGoal>> = repository.allSavingsGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated fields
    val totalBalance: StateFlow<Double> = transactions.map { list ->
        list.sumOf { if (it.isIncome) it.amount else -it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncome: StateFlow<Double> = transactions.map { list ->
        list.filter { it.isIncome }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = transactions.map { list ->
        list.filter { !it.isIncome }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categorySpendMap: StateFlow<Map<String, Double>> = transactions.map { list ->
        list.filter { !it.isIncome }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    init {
        // Pre-populate realistic sample data if empty on first-launch to avoid empty state
        viewModelScope.launch {
            transactions.first().let { currentList ->
                if (currentList.isEmpty()) {
                    prePopulateSampleData()
                }
            }
        }
    }

    private suspend fun prePopulateSampleData() {
        val sampleTransactions = listOf(
            Transaction(
                title = "Monthly Salary",
                amount = 4500.0,
                category = "Salary",
                notes = "Bank transfer from Tech Corp",
                isIncome = true,
                timestamp = System.currentTimeMillis() - 86400000 * 3 // 3 days ago
            ),
            Transaction(
                title = "Whole Foods Organic Groceries",
                amount = 124.50,
                category = "Food",
                notes = "Weekly groceries purchase",
                isIncome = false,
                timestamp = System.currentTimeMillis() - 86400000 * 2 // 2 days ago
            ),
            Transaction(
                title = "Starbucks Coffee",
                amount = 6.75,
                category = "Food",
                notes = "Caramel Macchiato",
                isIncome = false,
                timestamp = System.currentTimeMillis() - 3600000 * 4 // 4 hours ago
            ),
            Transaction(
                title = "Monthly Rent Payment",
                amount = 1200.0,
                category = "Housing",
                notes = "Apartment 4B rent",
                isIncome = false,
                timestamp = System.currentTimeMillis() - 86400000 * 5 // 5 days ago
            ),
            Transaction(
                title = "Electric Utility Bill",
                amount = 85.20,
                category = "Utilities",
                notes = "Power Grid invoice",
                isIncome = false,
                timestamp = System.currentTimeMillis() - 86400000 * 1 // Yesterday
            ),
            Transaction(
                title = "Freelance UI Design",
                amount = 650.0,
                category = "Salary",
                notes = "Dashboard redesign layout project",
                isIncome = true,
                timestamp = System.currentTimeMillis() - 3600000 * 1 // 1 hour ago
            )
        )

        val sampleBudgets = listOf(
            CategoryBudget("Food", 400.0),
            CategoryBudget("Housing", 1500.0),
            CategoryBudget("Utilities", 150.0),
            CategoryBudget("Entertainment", 200.0)
        )

        val sampleGoals = listOf(
            SavingsGoal(
                name = "Emergency Fund",
                targetAmount = 5000.0,
                currentAmount = 1500.0,
                notes = "6 months of essential living expenses"
            ),
            SavingsGoal(
                name = "New iPad Pro",
                targetAmount = 1100.0,
                currentAmount = 450.0,
                notes = "11-inch M4 model for sketching"
            )
        )

        sampleTransactions.forEach { repository.insertTransaction(it) }
        sampleBudgets.forEach { repository.insertBudget(it) }
        sampleGoals.forEach { repository.insertSavingsGoal(it) }
    }

    fun addTransaction(title: String, amount: Double, category: String, notes: String, isIncome: Boolean, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    title = title,
                    amount = amount,
                    category = category,
                    notes = notes,
                    isIncome = isIncome,
                    timestamp = timestamp
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun deleteTransactionById(id: Int) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    fun setBudget(category: String, limitAmount: Double) {
        viewModelScope.launch {
            repository.insertBudget(CategoryBudget(category, limitAmount))
        }
    }

    fun deleteBudget(budget: CategoryBudget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    fun addSavingsGoal(name: String, targetAmount: Double, currentAmount: Double = 0.0, notes: String = "") {
        viewModelScope.launch {
            repository.insertSavingsGoal(
                SavingsGoal(
                    name = name,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount,
                    notes = notes
                )
            )
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(goal)
        }
    }

    fun updateSavingsAmount(id: Int, currentAmount: Double) {
        viewModelScope.launch {
            repository.updateSavingsAmount(id, currentAmount)
        }
    }
}

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
