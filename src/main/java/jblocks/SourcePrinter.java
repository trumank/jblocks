package jblocks;

public class SourcePrinter {
    static final String INDENT_TYPE = "    ";
    static final String NEWLINE = "\n";
    StringBuilder source;
    int indent;

    public SourcePrinter() {
        source = new StringBuilder();
        indent = 0;
    }
    public void indent() {
        indent++;
    }
    public void outdent() {
        if (indent <= 0) {
            throw new RuntimeException("Cannot have indentation < 0");
        }
        indent--;
    }
    public void print(String string) {
        source.append(string);
    }
    public void printIndented(String string) {
        if (string.length() > 0) {
            printIndentation();
            print(string);
        }
        printNewline();
    }
    public void printNewline() {
        source.append(NEWLINE);
    }
    public void printIndentation() {
        for (int i = 0; i < indent; i++) {
            print(INDENT_TYPE);
        }
    }
    public String getSource() {
        return source.toString();
    }
}
