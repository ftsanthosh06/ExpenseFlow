package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Expense operations
 */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Get all expenses
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
        log.info("GET /api/expenses - Fetching all expenses");
        List<ExpenseDTO> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get expense by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long id) {
        log.info("GET /api/expenses/{} - Fetching expense by ID", id);
        ExpenseDTO expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }

    /**
     * Create new expense
     */
    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        log.info("POST /api/expenses - Creating new expense");
        ExpenseDTO created = expenseService.createExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update expense
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseDTO expenseDTO) {
        log.info("PUT /api/expenses/{} - Updating expense", id);
        ExpenseDTO updated = expenseService.updateExpense(id, expenseDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete expense
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        log.info("DELETE /api/expenses/{} - Deleting expense", id);
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get expenses by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByCategory(@PathVariable String category) {
        log.info("GET /api/expenses/category/{} - Fetching expenses by category", category);
        List<ExpenseDTO> expenses = expenseService.getExpensesByCategory(category);
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get current month expenses
     */
    @GetMapping("/month/current")
    public ResponseEntity<List<ExpenseDTO>> getCurrentMonthExpenses() {
        log.info("GET /api/expenses/month/current - Fetching current month expenses");
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpenses();
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get last N days expenses
     */
    @GetMapping("/days/{days}")
    public ResponseEntity<List<ExpenseDTO>> getLastNDaysExpenses(@PathVariable int days) {
        log.info("GET /api/expenses/days/{} - Fetching last {} days expenses", days, days);
        List<ExpenseDTO> expenses = expenseService.getLastNDaysExpenses(days);
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get expenses by date range
     */
    @GetMapping("/range")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.info("GET /api/expenses/range - Fetching expenses from {} to {}", startDate, endDate);
        List<ExpenseDTO> expenses = expenseService.getExpensesByDateRange(startDate, endDate);
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get analytics summary
     */
    @GetMapping("/analytics/summary")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        log.info("GET /api/expenses/analytics/summary - Fetching analytics summary");
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSpending", expenseService.getTotalSpending());
        summary.put("currentMonthTotal", expenseService.getCurrentMonthTotal());
        summary.put("averageDailySpending", expenseService.getAverageDailySpending());
        summary.put("spendingByCategory", expenseService.getSpendingByCategory());
        summary.put("monthlySummary", expenseService.getMonthlySummary());
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Get spending by category
     */
    @GetMapping("/analytics/category")
    public ResponseEntity<Map<String, BigDecimal>> getSpendingByCategory() {
        log.info("GET /api/expenses/analytics/category - Fetching spending by category");
        Map<String, BigDecimal> spending = expenseService.getSpendingByCategory();
        return ResponseEntity.ok(spending);
    }

    /**
     * Get monthly summary
     */
    @GetMapping("/analytics/monthly")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlySummary() {
        log.info("GET /api/expenses/analytics/monthly - Fetching monthly summary");
        Map<String, BigDecimal> monthly = expenseService.getMonthlySummary();
        return ResponseEntity.ok(monthly);
    }

    /**
     * Get average daily spending
     */
    @GetMapping("/analytics/daily-average")
    public ResponseEntity<BigDecimal> getAverageDailySpending() {
        log.info("GET /api/expenses/analytics/daily-average - Fetching average daily spending");
        BigDecimal average = expenseService.getAverageDailySpending();
        return ResponseEntity.ok(average);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Expense Tracker API");
        return ResponseEntity.ok(response);
    }
}