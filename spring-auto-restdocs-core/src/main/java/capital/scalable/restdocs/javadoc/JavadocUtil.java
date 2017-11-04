package capital.scalable.restdocs.javadoc;

import capital.scalable.restdocs.util.TemplateFormatting;

public class JavadocUtil {
    private static String FORCED_LB = "__FLB__";
    private static String LB = "__LB__";

    private JavadocUtil() {
        // util
    }

    public static String convertFromJavadoc(String javadoc, TemplateFormatting templateFormatting) {
        String converted = javadoc
                // line breaks in javadoc are ignored
                .replace("\n", "")
                // will be replaced with forced line break
                .replace("<br/>", FORCED_LB)
                .replace("<br>", FORCED_LB)
                // will be replaced with normal line break
                .replace("<p>", LB + LB)
                .replace("</p>", LB + LB)
                .replace("<ul>", LB)
                .replace("</ul>", LB + LB)
                .replace("<li>", LB + "- ")
                .replace("</li>", "")
                .replace("<b>", templateFormatting.getBold())
                .replace("</b>", templateFormatting.getBold())
                .replace("<i>", templateFormatting.getItalics())
                .replace("</i>", templateFormatting.getItalics());

        if (converted.isEmpty()) {
            return "";
        }

        String res = trimAndFixLineBreak(converted, FORCED_LB, templateFormatting.getLineBreak());
        res = trimAndFixLineBreak(res, LB, "\n");
        return res;
    }

    private static String trimAndFixLineBreak(String text, String separator, String newSeparator) {
        StringBuilder res = new StringBuilder();
        for (String line : text.split(separator)) {
            res.append(line.trim()).append(newSeparator);
        }
        res.delete(res.lastIndexOf(newSeparator), res.length());
        return res.toString();
    }
}
