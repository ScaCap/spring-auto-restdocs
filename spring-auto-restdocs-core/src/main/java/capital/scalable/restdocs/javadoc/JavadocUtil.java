package capital.scalable.restdocs.javadoc;

public class JavadocUtil {
    private JavadocUtil() {
        // util
    }

    public static String convertFromJavadoc(String javadoc, String lineBreak) {
        String lineBreaked = javadoc
                .replace("\n", "")
                .replace("<br/>", "\n")
                .replace("<br>", "\n")
                .replace("<p>", "\n\n")
                .replace("</p>", "")
                .trim();

        if (lineBreaked.isEmpty()) {
            return "";
        }

        StringBuilder res = new StringBuilder();
        for (String line : lineBreaked.split("\n")) {
            res.append(line.trim()).append(lineBreak);
        }
        res.delete(res.lastIndexOf(lineBreak), res.length());

        return res.toString();
    }
}
