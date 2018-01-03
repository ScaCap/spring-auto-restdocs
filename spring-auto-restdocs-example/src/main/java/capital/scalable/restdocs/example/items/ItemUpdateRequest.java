package capital.scalable.restdocs.example.items;

import javax.validation.constraints.Size;

import capital.scalable.restdocs.example.constraints.OneOf;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Java object for the JSON request.
 */
@Data
class ItemUpdateRequest {
    /**
     * Some information about the item.
     */
    @NotBlank(groups = English.class)
    @Length(max = 20)
    @Size.List({
            @Size(min = 2, max = 10, groups = German.class),
            @Size(min = 4, max = 12, groups = English.class)
    })
    private String description;

    /**
     * Country dependent type of the item.
     */
    @Size(max = 1000)
    @OneOf.List({
            @OneOf(value = {"klein", "groß"}, groups = German.class),
            @OneOf(value = {"small", "big"}, groups = English.class)
    })
    private String type;
}
