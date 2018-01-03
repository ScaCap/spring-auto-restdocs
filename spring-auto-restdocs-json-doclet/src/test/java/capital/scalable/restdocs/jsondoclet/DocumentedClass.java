package capital.scalable.restdocs.jsondoclet;

import java.math.BigDecimal;

/**
 * This is a test class. UTF 8 test: 我能吞下玻璃而不伤身体。Árvíztűrő tükörfúrógép
 */
class DocumentedClass {
    /**
     * Location of resource
     *
     * @see path
     */
    public String location;

    /**
     * Path within location
     */
    private BigDecimal path;

    Boolean notDocumented;

    /**
     * Executes request on specified location
     *
     * @title Execute request
     */
    public void execute() {
    }

    /**
     * Initiates request
     *
     * @param when  when to initiate
     * @param force true if force
     * @title Do initiate
     * @deprecated use other method
     */
    private void initiate(String when, boolean force, int notDocumented) {
    }

    void notDocumented() {
    }
}
