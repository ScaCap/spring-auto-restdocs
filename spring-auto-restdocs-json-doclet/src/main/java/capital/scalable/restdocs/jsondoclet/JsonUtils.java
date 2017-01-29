package capital.scalable.restdocs.jsondoclet;

public final class JsonUtils {

    private JsonUtils() {
        // only static utility functions
    }

    public static String escape(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
