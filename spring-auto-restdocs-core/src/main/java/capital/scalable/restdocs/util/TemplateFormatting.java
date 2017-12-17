package capital.scalable.restdocs.util;

public class TemplateFormatting {
    private static final String LINE_BREAK_ADOC = " +\n";
    private static final String LINE_BREAK_MD = "<br>";
    private static final String BOLD_ADOD = "**";
    private static final String BOLD_MD = "**";
    private static final String ITALICS_ADOC = "__";
    private static final String ITALICS_MD = "*";
    private static final String LINK_ADOC = "link:$1[$2]";
    private static final String LINK_MD = "[$2]($1)";

    public static TemplateFormatting ASCIIDOC =
            new TemplateFormatting(LINE_BREAK_ADOC, BOLD_ADOD, ITALICS_ADOC, LINK_ADOC);

    public static TemplateFormatting MARKDOWN =
            new TemplateFormatting(LINE_BREAK_MD, BOLD_MD, ITALICS_MD, LINK_MD);

    private final String lineBreak;
    private final String bold;
    private final String italics;
    private final String link;

    private TemplateFormatting(String lineBreak, String bold, String italics, String link) {
        this.lineBreak = lineBreak;
        this.bold = bold;
        this.italics = italics;
        this.link = link;
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

    public String link() {
        return link;
    }
}
