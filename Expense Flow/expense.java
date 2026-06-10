

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing an Expense record
 */
@Entity
@Table(name = "expenses", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_date", columnList = "date"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Enum for expense categories
     */
    public enum ExpenseCategory {
        FOOD("Food", "🍔"),
        TRANSPORT("Transport", "🚗"),
        ENTERTAINMENT("Entertainment", "🎬"),
        SHOPPING("Shopping", "🛍️"),
        UTILITIES("Utilities", "💡"),
        HEALTH("Health", "🏥"),
        EDUCATION("Education", "📚"),
        OTHER("Other", "📌");

        private final String displayName;
        private final String emoji;

        ExpenseCategory(String displayName, String emoji) {
            this.displayName = displayName;
            this.emoji = emoji;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getEmoji() {
            return emoji;
        }
    }
}
    

