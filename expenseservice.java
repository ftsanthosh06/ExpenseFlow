

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.entity.Expense;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for Expense operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
class ExpenseService {

    private final ExpenseRepository expenseRepository;

    /**
     * Get all expenses
     */
    public List<ExpenseDTO> getAllExpenses() {
        log.info("Fetching all expenses");
        return expenseRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ExpenseDTO::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get expense by ID
     */
    public ExpenseDTO getExpenseById(Long id) {
        log.info("Fetching expense with ID: {}", id);
        return expenseRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));
    }

    /**
     * Create new expense
     */
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        log.info("Creating new expense: {}", expenseDTO.getCategory());
        
        Expense expense = Expense.builder()
                .amount(expenseDTO.getAmount())
                .category(convertStringToCategory(expenseDTO.getCategory()))
                .date(expenseDTO.getDate())
                .description(expenseDTO.getDescription())
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        log.info("Expense created successfully with ID: {}", savedExpense.getId());
        return convertToDTO(savedExpense);
    }

    /**
     * Update existing expense
     */
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        log.info("Updating expense with ID: {}", id);
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));

        expense.setAmount(expenseDTO.getAmount());
        expense.setCategory(convertStringToCategory(expenseDTO.getCategory()));
        expense.setDate(expenseDTO.getDate());
        expense.setDescription(expenseDTO.getDescription());

        Expense updatedExpense = expenseRepository.save(expense);
        log.info("Expense updated successfully");
        return convertToDTO(updatedExpense);
    }

    /**
     * Delete expense
     */
    public void deleteExpense(Long id) {
        log.info("Deleting expense with ID: {}", id);
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with ID: " + id);
        }
        expenseRepository.deleteById(id);
        log.info("Expense deleted successfully");
    }

    /**
     * Get expenses by category
     */
    public List<ExpenseDTO> getExpensesByCategory(String category) {
        log.info("Fetching expenses by category: {}", category);
        return expenseRepository.findByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ExpenseDTO::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get expenses for current month
     */
    public List<ExpenseDTO> getCurrentMonthExpenses() {
        log.info("Fetching current month expenses");
        return expenseRepository.findCurrentMonthExpenses()
                .stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ExpenseDTO::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get expenses for last N days
     */
    public List<ExpenseDTO> getLastNDaysExpenses(int days) {
        log.info("Fetching last {} days expenses", days);
        return expenseRepository.findLastNDaysExpenses(days)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expenses within date range
     */
    public List<ExpenseDTO> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching expenses from {} to {}", startDate, endDate);
        return expenseRepository.findByDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ExpenseDTO::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get total spending
     */
    public BigDecimal getTotalSpending() {
        BigDecimal total = expenseRepository.getTotalAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get current month total
     */
    public BigDecimal getCurrentMonthTotal() {
        BigDecimal total = expenseRepository.getCurrentMonthTotal();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get spending summary by category
     */
    public Map<String, BigDecimal> getSpendingByCategory() {
        log.info("Fetching spending by category");
        List<Expense> allExpenses = expenseRepository.findAll();
        return allExpenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().getDisplayName(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add
                        )
                ));
    }

    /**
     * Get monthly spending summary
     */
    public Map<String, BigDecimal> getMonthlySummary() {
        log.info("Fetching monthly spending summary");
        List<Expense> allExpenses = expenseRepository.findAll();
        return allExpenses.stream()
                .collect(Collectors.groupingBy(
                        e -> {
                            LocalDate date = e.getDate();
                            return String.format("%04d-%02d", date.getYear(), date.getMonthValue());
                        },
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add
                        )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toLinkedHashMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    /**
     * Get average daily spending
     */
    public BigDecimal getAverageDailySpending() {
        BigDecimal total = getTotalSpending();
        List<Expense> expenses = expenseRepository.findAll();
        
        if (expenses.isEmpty()) {
            return BigDecimal.ZERO;
        }

        LocalDate minDate = expenses.stream()
                .map(Expense::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
        
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(minDate, LocalDate.now()) + 1;
        
        return daysBetween > 0 ? total.divide(new BigDecimal(daysBetween), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * Convert Expense entity to DTO
     */
    private ExpenseDTO convertToDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .category(expense.getCategory().getDisplayName())
                .date(expense.getDate())
                .description(expense.getDescription())
                .build();
    }

    /**
     * Convert String to ExpenseCategory enum
     */
    private Expense.ExpenseCategory convertStringToCategory(String category) {
        try {
            return Expense.ExpenseCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid category: {}", category);
            return Expense.ExpenseCategory.OTHER;
        }
    }
}