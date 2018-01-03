package capital.scalable.restdocs.section;

import org.springframework.restdocs.operation.Operation;

public interface SectionSupport {
    /**
     * File name (= section name).
     */
    String getFileName();

    /**
     * Section header key corresponding to entry in translation file.
     */
    String getHeaderKey();

    /**
     * Flag if section will render non-empty content.
     *
     * @param operation operation
     * @return true if snippet will return non-empty content; false otherwise
     */
    boolean hasContent(Operation operation);
}
