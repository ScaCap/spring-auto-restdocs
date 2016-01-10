package capital.scalable.example.items;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
class ItemUpdateRequest {
    /**
     * Some information about the item.
     */
    @NotBlank
    private String information;
}
