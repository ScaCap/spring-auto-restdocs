package capital.scalable.restdocs.jsondoclet;

public class DocletUtils {
    private DocletUtils() {
        // utils
    }

    public static String cleanupTagName(String name) {
        return name.startsWith("@") ? name.substring(1) : name;
    }
}
