package capital.scalable.example.items;

import java.util.List;

import lombok.Value;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Data for a single item.
 */
@Value
class ItemResponse {
    /**
     * Unique ID. This text comes directly from JavaDoc.
     */
    @NotBlank
    private String id;
    /**
     * Some information about the item.
     */
    @NotBlank
    private String information;

    private NestedInformation nestedInformation;

    private List<NestedInformation> nestedList;

    @Value
    static class NestedInformation {
        private String optionalText;
        private Integer someNumber;
        private Boolean someBoolean;
    }
}
