package ifood.score.model;

import ifood.score.menu.Category;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Document(collection = "categoryRelevance")
public class CategoryRelevance {

    @Id
    private String id;
    @Indexed
    private final UUID orderId;
    private final Category category;
    private final BigDecimal relevance;
    private RelevanceStatus status;
    private final Date confirmedAt;

    public CategoryRelevance(UUID orderId, Date confirmedAt, Category category, BigDecimal relevance) {
        this.orderId = orderId;
        this.confirmedAt = confirmedAt;
        this.category = category;
        this.relevance = relevance;
        this.status = RelevanceStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getRelevance() {
        return relevance;
    }

    public RelevanceStatus getStatus() {
        return status;
    }

    public void setStatus(RelevanceStatus status) {
        this.status = status;
    }
}
