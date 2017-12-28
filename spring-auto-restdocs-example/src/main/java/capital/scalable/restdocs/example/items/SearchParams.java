package capital.scalable.restdocs.example.items;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchParams
{
	/**
	 * Phrase to filter Items
	 */
	private String descMatch;

	/**
	 * Maybe a constant
	 */
	private Integer hint;

	/**
	 * Just to show example for path parameters.
	 */
	private String subpath;
}
