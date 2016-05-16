package capital.scalable.jsondoclet;

import java.math.BigDecimal;

/**
 * This is a test class
 */
public class DocumentedClass {
    /**
     * Location of resource
     */
    public String location;

    /**
     * Path within location
     */
    private BigDecimal path;

    Boolean notDocumented;

    /**
     * Executes request on specified location
     */
    public void execute() {
    }

    /**
     * Initiates request
     *
     * @param when  when to initiate
     * @param force true if force
     */
    private void initiate(String when, boolean force, int notDocumented) {
    }

    void notDocumented() {
    }
}
