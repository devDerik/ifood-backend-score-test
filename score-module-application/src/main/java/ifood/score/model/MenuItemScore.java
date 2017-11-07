package ifood.score.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Document(collection = "menuItemScore")
public class MenuItemScore {
    @Id
    private final UUID menuId;
    private final BigDecimal score;

    public MenuItemScore(UUID menuId, BigDecimal score) {
        this.menuId = menuId;
        this.score = score;
    }

    public UUID getMenuId() {
        return menuId;
    }

    public BigDecimal getScore() {
        return score;
    }
}
