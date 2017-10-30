package capital.scalable.restdocs.util;

public class TemplateFormatting {
    private static final String LINE_BREAK_ASCIIDOC = " +\n";
    private static final String LINE_BREAK_MARKDOWN = "<br>";
    private static final String BOLD_ASCIIDOC = "**";
    private static final String BOLD_MARKDOWN = "**";
    private static final String ITALICS_ASCIIDOC = "__";
    private static final String ITALICS_MARKDOWN = "*";

    public static TemplateFormatting ASCIIDOC =
            new TemplateFormatting(LINE_BREAK_ASCIIDOC, BOLD_ASCIIDOC, ITALICS_ASCIIDOC);

    public static TemplateFormatting MARKDOWN =
            new TemplateFormatting(LINE_BREAK_MARKDOWN, BOLD_MARKDOWN, ITALICS_MARKDOWN);

    private final String lineBreak;
    private final String bold;
    private final String italics;

    private TemplateFormatting(String lineBreak, String bold, String italics) {
        this.lineBreak = lineBreak;
        this.bold = bold;
        this.italics = italics;
    }

    public String getLineBreak() {
        return lineBreak;
    }

    public String getBold() {
        return bold;
    }

    public String getItalics() {
        return italics;
    }
}
