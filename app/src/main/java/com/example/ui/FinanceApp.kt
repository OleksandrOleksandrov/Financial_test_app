package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryBudget
import com.example.data.model.SavingsGoal
import com.example.data.model.Transaction
import com.example.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

enum class FinanceTab {
    DASHBOARD, TRANSACTIONS, BUDGETS, SAVINGS
}

val DefaultCategories = listOf("Food", "Housing", "Utilities", "Entertainment", "Salary", "Savings", "Other")

// Mapping of categories to standard colors and icons
fun getCategoryColor(category: String): Color {
    return when (category) {
        "Food" -> Color(0xFFF59E0B) // Amber
        "Housing" -> Color(0xFF3B82F6) // Blue
        "Utilities" -> Color(0xFF10B981) // Emerald
        "Entertainment" -> Color(0xFFEC4899) // Pink
        "Salary" -> Color(0xFF8B5CF6) // Purple
        "Savings" -> Color(0xFF06B6D4) // Cyan
        else -> Color(0xFF64748B) // Slate/Other
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Food" -> Icons.Default.Restaurant
        "Housing" -> Icons.Default.Home
        "Utilities" -> Icons.Default.ElectricBolt
        "Entertainment" -> Icons.Default.SportsEsports
        "Salary" -> Icons.Default.Payments
        "Savings" -> Icons.Default.Savings
        else -> Icons.Default.Category
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    var currentTab by rememberSaveable { mutableStateOf(FinanceTab.DASHBOARD) }
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddSavingsDialog by remember { mutableStateOf(false) }

    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()

    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val totalIncome by viewModel.totalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val categorySpendMap by viewModel.categorySpendMap.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Welcome back,",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Alex Rivers",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                FinanceTab.values().forEach { tab ->
                    val (icon, label) = when (tab) {
                        FinanceTab.DASHBOARD -> Icons.Default.Dashboard to "Dashboard"
                        FinanceTab.TRANSACTIONS -> Icons.Default.ReceiptLong to "Transactions"
                        FinanceTab.BUDGETS -> Icons.Default.AccountBalance to "Budgets"
                        FinanceTab.SAVINGS -> Icons.Default.Savings to "Savings"
                    }
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(text = label, fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            when (currentTab) {
                FinanceTab.TRANSACTIONS -> {
                    ExtendedFloatingActionButton(
                        onClick = { showAddTransactionDialog = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Transaction") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("add_transaction_fab")
                    )
                }
                FinanceTab.BUDGETS -> {
                    ExtendedFloatingActionButton(
                        onClick = { showAddBudgetDialog = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Set Budget") },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.testTag("add_budget_fab")
                    )
                }
                FinanceTab.SAVINGS -> {
                    ExtendedFloatingActionButton(
                        onClick = { showAddSavingsDialog = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("New Goal") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("add_savings_fab")
                    )
                }
                else -> { /* No FAB for Dashboard */ }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "tab_animation"
            ) { targetTab ->
                when (targetTab) {
                    FinanceTab.DASHBOARD -> DashboardScreen(
                        totalBalance = totalBalance,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        categorySpendMap = categorySpendMap,
                        budgets = budgets,
                        transactions = transactions,
                        onAddTransactionClick = { showAddTransactionDialog = true }
                    )
                    FinanceTab.TRANSACTIONS -> TransactionsScreen(
                        transactions = transactions,
                        onDeleteClick = { viewModel.deleteTransaction(it) }
                    )
                    FinanceTab.BUDGETS -> BudgetsScreen(
                        budgets = budgets,
                        categorySpendMap = categorySpendMap,
                        onDeleteClick = { viewModel.deleteBudget(it) }
                    )
                    FinanceTab.SAVINGS -> SavingsScreen(
                        savingsGoals = savingsGoals,
                        onAddMoney = { goal, amt -> viewModel.updateSavingsAmount(goal.id, goal.currentAmount + amt) },
                        onDeleteGoal = { viewModel.deleteSavingsGoal(it) }
                    )
                }
            }

            // --- DIALOGS ---
            if (showAddTransactionDialog) {
                AddTransactionDialog(
                    onDismiss = { showAddTransactionDialog = false },
                    onConfirm = { title, amt, category, notes, isIncome ->
                        viewModel.addTransaction(title, amt, category, notes, isIncome)
                        showAddTransactionDialog = false
                    }
                )
            }

            if (showAddBudgetDialog) {
                AddBudgetDialog(
                    onDismiss = { showAddBudgetDialog = false },
                    onConfirm = { category, limit ->
                        viewModel.setBudget(category, limit)
                        showAddBudgetDialog = false
                    }
                )
            }

            if (showAddSavingsDialog) {
                AddSavingsDialog(
                    onDismiss = { showAddSavingsDialog = false },
                    onConfirm = { name, target, current, notes ->
                        viewModel.addSavingsGoal(name, target, current, notes)
                        showAddSavingsDialog = false
                    }
                )
            }
        }
    }
}

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(
    totalBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    categorySpendMap: Map<String, Double>,
    budgets: List<CategoryBudget>,
    transactions: List<Transaction>,
    onAddTransactionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Board Card (Professional Polish Style)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Balance",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = formatCurrency(totalBalance),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag("dashboard_balance_text")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFF3E7FF))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+2.4%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6750A4)
                        )
                    }
                    Text(
                        text = "vs last month",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Income Stat
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD7E3FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF001B3D),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Incomes",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = formatCurrency(totalIncome),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D6F2C)
                            )
                        }
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                    )

                    // Expense Stat
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFDAD6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFF410002),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Expenses",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = formatCurrency(totalExpense),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB3261E)
                            )
                        }
                    }
                }
            }
        }

        // Spend Breakdown & Donut Chart
        if (categorySpendMap.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Expense Allocation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut Chart Canvas
                        SpendDonutChart(
                            categorySpendMap = categorySpendMap,
                            modifier = Modifier
                                .size(130.dp)
                                .weight(1f)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Spend Legend List
                        Column(
                            modifier = Modifier.weight(1.2f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val sortedExpenses = categorySpendMap.entries.sortedByDescending { it.value }.take(4)
                            sortedExpenses.forEach { entry ->
                                val color = getCategoryColor(entry.key)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = entry.key,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatCurrency(entry.value),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Budget Warning Alerts Card
        val overBudgetItems = budgets.mapNotNull { budget ->
            val spent = categorySpendMap[budget.category] ?: 0.0
            if (spent > budget.limitAmount) {
                budget.category to (spent - budget.limitAmount)
            } else null
        }

        if (overBudgetItems.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alert",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Budget Limits Exceeded!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        overBudgetItems.forEach { (cat, diff) ->
                            Text(
                                text = "• Overdraft of ${formatCurrency(diff)} inside \"$cat\"",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Quick Overview: Today's Actions / Recent Feed Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (transactions.isEmpty()) {
            EmptyStateView(
                prompt = "No transactions tracked yet. Get started!",
                icon = Icons.Default.MonetizationOn,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                transactions.take(4).forEach { tx ->
                    TransactionRow(transaction = tx, onDelete = {})
                }
            }
        }
    }
}

// Donut Chart Canvas Composer
@Composable
fun SpendDonutChart(
    categorySpendMap: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val totalExpense = categorySpendMap.values.sum()
    Canvas(modifier = modifier) {
        val strokeWidth = 32f
        val diameter = size.minDimension - strokeWidth
        val chartSize = Size(diameter, diameter)
        val offset = strokeWidth / 2
        val boundingBoxOffset = Offset(offset, offset)

        var startAngle = -90f

        if (totalExpense == 0.0) {
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = boundingBoxOffset,
                size = chartSize,
                style = Stroke(width = strokeWidth)
            )
        } else {
            categorySpendMap.forEach { (category, value) ->
                val sweepAngle = ((value / totalExpense) * 360f).toFloat()
                drawArc(
                    color = getCategoryColor(category),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = boundingBoxOffset,
                    size = chartSize,
                    style = Stroke(width = strokeWidth)
                )
                startAngle += sweepAngle
            }
        }
    }
}

// Empty State View Component (highly functional!)
@Composable
fun EmptyStateView(
    prompt: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = prompt,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// 2. TRANSACTIONS SCREEN
// ==========================================
@Composable
fun TransactionsScreen(
    transactions: List<Transaction>,
    onDeleteClick: (Transaction) -> Unit
) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Filtering Section
        OutlinedTextField(
            value = searchKeyword,
            onValueChange = { searchKeyword = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("transaction_search_input"),
            placeholder = { Text("Search transactions...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Category Quick Filters Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterOptions = listOf("All") + DefaultCategories
            filterOptions.forEach { opt ->
                val isSelected = selectedCategoryFilter == opt
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryFilter = opt },
                    label = { Text(opt) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("filter_chip_$opt")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtering list implementation
        val filteredTransactions = transactions.filter { tx ->
            val matchSearch = tx.title.contains(searchKeyword, ignoreCase = true) || 
                              tx.notes.contains(searchKeyword, ignoreCase = true)
            val matchCategory = selectedCategoryFilter == "All" || tx.category == selectedCategoryFilter
            matchSearch && matchCategory
        }

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    prompt = "No matching transactions found.",
                    icon = Icons.Default.Receipt,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTransactions, key = { it.id }) { tx ->
                    TransactionRow(
                        transaction = tx,
                        onDelete = { onDeleteClick(tx) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionRow(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category Styled Icon Bubble (Professional Polish red/blue style)
                val bubbleBg = if (transaction.isIncome) Color(0xFFD7E3FF) else Color(0xFFFFDAD6)
                val iconTint = if (transaction.isIncome) Color(0xFF001B3D) else Color(0xFF410002)

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(bubbleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(transaction.category),
                        contentDescription = transaction.category,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Detail Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${transaction.category} • ${formatDate(transaction.timestamp)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Amount Text
                val amountColor = if (transaction.isIncome) Color(0xFF1D6F2C) else Color(0xFFB3261E)
                val prefix = if (transaction.isIncome) "+" else "-"
                Text(
                    text = "$prefix${formatCurrency(transaction.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }

            // Explicable Expanded Notes Pane
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    if (transaction.notes.isNotEmpty()) {
                        Text(
                            text = "Comment / Notes:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = transaction.notes,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        Text(
                            text = "No additional comment provided.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Delete Row Button Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Tracker", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. BUDGETS SCREEN
// ==========================================
@Composable
fun BudgetsScreen(
    budgets: List<CategoryBudget>,
    categorySpendMap: Map<String, Double>,
    onDeleteClick: (CategoryBudget) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Category Budgets",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Maintain control by imposing custom limits on secondary expenses.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (budgets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    prompt = "No budgets initialized. Add one using the button below!",
                    icon = Icons.Default.AccountBalanceWallet,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(budgets, key = { it.category }) { budget ->
                    BudgetProgressRow(
                        budget = budget,
                        spentAmount = categorySpendMap[budget.category] ?: 0.0,
                        onDeleteClick = { onDeleteClick(budget) }
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetProgressRow(
    budget: CategoryBudget,
    spentAmount: Double,
    onDeleteClick: () -> Unit
) {
    val progress = if (budget.limitAmount > 0) (spentAmount / budget.limitAmount).toFloat() else 0f
    val progressClamped = progress.coerceIn(0f, 1f)
    val color = getCategoryColor(budget.category)
    val isViolated = spentAmount > budget.limitAmount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(budget.category),
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = budget.category,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progressClamped },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isViolated) MaterialTheme.colorScheme.error else color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Budget info feet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent ${formatCurrency(spentAmount)}",
                    fontSize = 12.sp,
                    color = if (isViolated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isViolated) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "Limit ${formatCurrency(budget.limitAmount)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================
// 4. SAVINGS SCREEN
// ==========================================
@Composable
fun SavingsScreen(
    savingsGoals: List<SavingsGoal>,
    onAddMoney: (SavingsGoal, Double) -> Unit,
    onDeleteGoal: (SavingsGoal) -> Unit
) {
    var topUpGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var topUpAmountStr by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Savings Goals",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Define targets, organize funds, and watch your capital generate.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (savingsGoals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    prompt = "No saving targets defined yet.",
                    icon = Icons.Default.Savings,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(savingsGoals, key = { it.id }) { goal ->
                    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f
                    val progressPercent = (progress * 100).toInt().coerceIn(0, 100)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = goal.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (goal.notes.isNotEmpty()) {
                                            Text(
                                                text = goal.notes,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }

                                IconButton(onClick = { onDeleteGoal(goal) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Goal",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Target Amount & Progress percent
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "${formatCurrency(goal.currentAmount)} / ${formatCurrency(goal.targetAmount)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$progressPercent%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Linear indicator
                            LinearProgressIndicator(
                                progress = { progress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { topUpGoal = goal },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Funds", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Top up money quick dialog
    if (topUpGoal != null) {
        AlertDialog(
            onDismissRequest = {
                topUpGoal = null
                topUpAmountStr = ""
                inputError = null
            },
            title = { Text("Top Up Funds") },
            text = {
                Column {
                    Text(
                        text = "Add money to \"${topUpGoal?.name}\":",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = topUpAmountStr,
                        onValueChange = {
                            topUpAmountStr = it
                            inputError = null
                        },
                        label = { Text("Contribution Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("savings_topup_input"),
                        isError = inputError != null
                    )
                    if (inputError != null) {
                        Text(
                            text = inputError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = topUpAmountStr.toDoubleOrNull()
                        if (amt == null || amt <= 0) {
                            inputError = "Please write a positive value"
                        } else {
                            onAddMoney(topUpGoal!!, amt)
                            topUpGoal = null
                            topUpAmountStr = ""
                            inputError = null
                        }
                    },
                    modifier = Modifier.testTag("savings_topup_confirm")
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    topUpGoal = null
                    topUpAmountStr = ""
                    inputError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ==========================================
// 5. HELPER DIALOGS
// ==========================================

// Add Transaction Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }
    var notes by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    var expandedCatDropdown by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add Transaction",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Selector: Income vs Expense Segment
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Expense Selector Chip
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (!isIncome) MaterialTheme.colorScheme.tertiary else Color.Transparent)
                            .clickable { isIncome = false }
                            .testTag("type_expense_chip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Expense",
                            fontWeight = FontWeight.Bold,
                            color = if (!isIncome) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Income Selector Chip
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(if (isIncome) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { isIncome = true }
                            .testTag("type_income_chip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Income",
                            fontWeight = FontWeight.Bold,
                            color = if (isIncome) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Transaction Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_transaction_title"),
                    label = { Text("Title (e.g. Groceries)") },
                    isError = titleError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Transaction Amount Input
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = {
                        amountStr = it
                        amountError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_transaction_amount"),
                    label = { Text("Amount ($ / €)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Category Dropdown Selection Box
                ExposedDropdownMenuBox(
                    expanded = expandedCatDropdown,
                    onExpandedChange = { expandedCatDropdown = !expandedCatDropdown }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCatDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("add_transaction_category"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCatDropdown,
                        onDismissRequest = { expandedCatDropdown = false }
                    ) {
                        DefaultCategories.forEach { selectedOption ->
                            DropdownMenuItem(
                                text = { Text(selectedOption) },
                                onClick = {
                                    category = selectedOption
                                    expandedCatDropdown = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                // Optional Notes Input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_transaction_notes"),
                    label = { Text("Add Comments/Notes (Optional)") },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Actions buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull()
                            if (title.isBlank()) {
                                titleError = true
                            }
                            if (amt == null || amt <= 0) {
                                amountError = true
                            }
                            if (title.isNotBlank() && amt != null && amt > 0) {
                                onConfirm(title.trim(), amt, category, notes.trim(), isIncome)
                            }
                        },
                        modifier = Modifier.testTag("add_transaction_submit")
                    ) {
                        Text("Save Transaction")
                    }
                }
            }
        }
    }
}

// Add Budget Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var category by remember { mutableStateOf("Food") }
    var limitStr by remember { mutableStateOf("") }
    var expandedCatDropdown by remember { mutableStateOf(false) }
    var limitError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Configure Budget",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Category dropdown selection
                ExposedDropdownMenuBox(
                    expanded = expandedCatDropdown,
                    onExpandedChange = { expandedCatDropdown = !expandedCatDropdown }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCatDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("add_budget_category"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCatDropdown,
                        onDismissRequest = { expandedCatDropdown = false }
                    ) {
                        DefaultCategories.filter { it != "Salary" && it != "Savings" }.forEach { selectedOption ->
                            DropdownMenuItem(
                                text = { Text(selectedOption) },
                                onClick = {
                                    category = selectedOption
                                    expandedCatDropdown = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                // Limit Str Input
                OutlinedTextField(
                    value = limitStr,
                    onValueChange = {
                        limitStr = it
                        limitError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_budget_limit"),
                    label = { Text("Monthly Spend Cap ($ / €)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = limitError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val limitAmt = limitStr.toDoubleOrNull()
                            if (limitAmt == null || limitAmt <= 0) {
                                limitError = true
                            } else {
                                onConfirm(category, limitAmt)
                            }
                        },
                        modifier = Modifier.testTag("add_budget_submit")
                    ) {
                        Text("Define Limit")
                    }
                }
            }
        }
    }
}

// Add Savings Target Dialog
@Composable
fun AddSavingsDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("") }
    var currentStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var targetError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "New Savings Goal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Goal Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_savings_name"),
                    label = { Text("Goal Name (e.g. Vacation)") },
                    isError = nameError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Target Amount
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = {
                        targetStr = it
                        targetError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_savings_target"),
                    label = { Text("Target Capital Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = targetError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Initial funds
                OutlinedTextField(
                    value = currentStr,
                    onValueChange = { currentStr = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_savings_initial"),
                    label = { Text("Initial Saved Amount (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_savings_notes"),
                    label = { Text("Notes (e.g. Target date)") },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val target = targetStr.toDoubleOrNull()
                            val initial = currentStr.toDoubleOrNull() ?: 0.0
                            if (name.isBlank()) {
                                nameError = true
                            }
                            if (target == null || target <= 0) {
                                targetError = true
                            }
                            if (name.isNotBlank() && target != null && target > 0) {
                                onConfirm(name.trim(), target, initial, notes.trim())
                            }
                        },
                        modifier = Modifier.testTag("add_savings_submit")
                    ) {
                        Text("Create Goal")
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. GENERAL UTILITY FUNCTIONS
// ==========================================
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(amount)
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
