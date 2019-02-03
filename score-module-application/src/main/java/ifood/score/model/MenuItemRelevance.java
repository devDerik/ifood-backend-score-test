package ifood.score.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Document(collection = "menuItemRelevance")
public class MenuItemRelevance {

    @Id
    private String id;
    private final UUID orderId;
    private final Date confirmedAt;
    @Indexed
    private final UUID menuId;
    private final BigDecimal relevance;
    private RelevanceStatus status;

    public MenuItemRelevance(UUID orderId, Date confirmedAt, UUID menuId, BigDecimal relevance) {
        this.orderId = orderId;
        this.confirmedAt = confirmedAt;
        this.menuId = menuId;
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

    public UUID getMenuId() {
        return menuId;
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
