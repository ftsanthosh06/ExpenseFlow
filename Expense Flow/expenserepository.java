package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Expense entity
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find expenses by category
     */
    List<Expense> findByCategory(String category);

    /**
     * Find expenses within a date range
     */
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses for a specific month and year
     */
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month")
    List<Expense> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Find expenses for current month
     */
    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = YEAR(CURDATE()) AND MONTH(e.date) = MONTH(CURDATE())")
    List<Expense> findCurrentMonthExpenses();

    /**
     * Find expenses for last N days
     */
    @Query("SELECT e FROM Expense e WHERE e.date >= DATE_SUB(CURDATE(), INTERVAL :days DAY) ORDER BY e.date DESC")
    List<Expense> findLastNDaysExpenses(@Param("days") int days);

    /**
     * Get total spending by category
     */
    @Query("SELECT CONCAT(e.category, ':', SUM(e.amount)) FROM Expense e GROUP BY e.category")
    List<String> getTotalByCategory();

    /**
     * Get total spending
     */
    @Query("SELECT SUM(e.amount) FROM Expense e")
    BigDecimal getTotalAmount();

    /**
     * Get current month total
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE YEAR(e.date) = YEAR(CURDATE()) AND MONTH(e.date) = MONTH(CURDATE())")
    BigDecimal getCurrentMonthTotal();
}
