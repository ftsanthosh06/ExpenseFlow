-- ============================================================
-- Expense Tracker - MySQL Database Schema
-- ============================================================

-- Create Database
CREATE DATABASE IF NOT EXISTS expense_tracker;
USE expense_tracker;

-- Create Expenses Table
CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for better query performance
    INDEX idx_category (category),
    INDEX idx_date (date),
    INDEX idx_created_at (created_at),
    INDEX idx_category_date (category, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Sample Data (Optional)
-- ============================================================

INSERT INTO expenses (amount, category, date, description) VALUES
(25.50, 'Food', '2024-01-15', 'Lunch at restaurant'),
(45.00, 'Transport', '2024-01-15', 'Gas'),
(120.00, 'Shopping', '2024-01-16', 'Groceries'),
(15.00, 'Entertainment', '2024-01-17', 'Movie tickets'),
(65.00, 'Utilities', '2024-01-18', 'Electricity bill'),
(50.00, 'Health', '2024-01-19', 'Doctor consultation'),
(30.00, 'Food', '2024-01-20', 'Dinner'),
(200.00, 'Education', '2024-01-21', 'Online course'),
(12.50, 'Other', '2024-01-22', 'Miscellaneous'),
(35.00, 'Transport', '2024-01-23', 'Taxi ride'),
(55.00, 'Shopping', '2024-01-24', 'Clothes'),
(18.00, 'Entertainment', '2024-01-25', 'Concert'),
(28.00, 'Food', '2024-01-26', 'Breakfast'),
(45.00, 'Utilities', '2024-01-27', 'Internet bill'),
(40.00, 'Health', '2024-01-28', 'Medicine'),
(75.00, 'Shopping', '2024-01-29', 'Books'),
(22.00, 'Food', '2024-01-30', 'Lunch'),
(55.00, 'Transport', '2024-01-31', 'Monthly pass');

-- ============================================================
-- Views for Analytics (Optional)
-- ============================================================

-- View for Monthly Summary
CREATE OR REPLACE VIEW monthly_summary AS
SELECT 
    DATE_FORMAT(date, '%Y-%m') AS month,
    SUM(amount) AS total_amount,
    COUNT(*) AS transaction_count,
    AVG(amount) AS average_amount
FROM expenses
GROUP BY DATE_FORMAT(date, '%Y-%m')
ORDER BY month DESC;

-- View for Category Summary
CREATE OR REPLACE VIEW category_summary AS
SELECT 
    category,
    SUM(amount) AS total_amount,
    COUNT(*) AS transaction_count,
    AVG(amount) AS average_amount
FROM expenses
GROUP BY category
ORDER BY total_amount DESC;

-- View for Daily Summary
CREATE OR REPLACE VIEW daily_summary AS
SELECT 
    date,
    SUM(amount) AS total_amount,
    COUNT(*) AS transaction_count
FROM expenses
GROUP BY date
ORDER BY date DESC;

-- ============================================================
-- Useful Queries for Analysis
-- ============================================================

-- Total spending
-- SELECT SUM(amount) AS total_spending FROM expenses;

-- Current month total
-- SELECT SUM(amount) AS current_month_total 
-- FROM expenses 
-- WHERE YEAR(date) = YEAR(CURDATE()) AND MONTH(date) = MONTH(CURDATE());

-- Spending by category
-- SELECT category, SUM(amount) AS total 
-- FROM expenses 
-- GROUP BY category 
-- ORDER BY total DESC;

-- Last 7 days spending
-- SELECT DATE(date) AS date, SUM(amount) AS total 
-- FROM expenses 
-- WHERE date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
-- GROUP BY DATE(date)
-- ORDER BY date DESC;

-- Average daily spending
-- SELECT AVG(daily_total) AS average_daily_spending
-- FROM (
--     SELECT SUM(amount) AS daily_total
--     FROM expenses
--     GROUP BY DATE(date)
-- ) AS daily_spending;