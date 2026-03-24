import java.io.*;
import java.util.*;

class Main {

    // ── FILE PATHS ────────────────────────────────────────────
    static String RAW_GRAMMAR = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/RawGrammar.txt";
    static String PROCESSED_GRAMMAR= "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/ProcessedGrammar1.txt";
    static String PARSE_TABLE = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/ParseTable.txt";
    static String INPUT_FILE = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/test4.lang";
    static String OUTPUT_FILE = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/ParseResults.txt";

    public static void main(String[] args) {
        try {
            PrintStream fileOut = new PrintStream(OUTPUT_FILE);
            System.setOut(fileOut);
        }
        catch (FileNotFoundException e) {
            System.exit(1);
        }

        // Parsing: Stage 1
        System.out.println("\n----------------------------");
        System.out.println("___Grammar Transformation___");
        System.out.println("----------------------------");
        Grammar grammar = new Grammar();
        try {
            grammar.loadFromFile(RAW_GRAMMAR);
            grammar.eliminateLeftRecursion();
            grammar.eliminateLeftFactoring();
            grammar.exportToFile(PROCESSED_GRAMMAR);
        }
        catch (IOException e) {
            System.out.println("Error: Could not transform Grammar.");
            System.exit(1);
        }

        // Parsing: Stage 2
        System.out.println("\n--------------------------------");
        System.out.println("___First Follow & Parse Table___");
        System.out.println("--------------------------------");
        FirstFollow ff = new FirstFollow(grammar);
        ff.compute();
        ParseTableBuilder builder = new ParseTableBuilder(grammar, ff);
        builder.build();

        try {
            builder.writeToFile(PARSE_TABLE);
        }
        catch (IOException e) {
            System.exit(1);
        }
        if (!builder.isLL1()) {
            System.out.println("Grammar is NOT LL(1)");
            System.exit(1);
        }

        // Lemme just read it
        ParseTableReader tableReader = new ParseTableReader();
        try {
            tableReader.loadTable(PARSE_TABLE);
        }
        catch (IOException e) {
            System.exit(1);
        }

        // Tokenization
        System.out.println("\n----------------");
        System.out.println("___Tokenizing___");
        System.out.println("----------------");

        ArrayList<Token> tokens = null;
        try {
            ManualScanner ms = new ManualScanner(INPUT_FILE);
            ms.tokenise();
            tokens = ms.Tokens_List;
        }
        catch (Exception e) {
            System.exit(1);
        }

        if (tokens == null || tokens.isEmpty()) {
            System.out.println("Empty File");
            System.exit(1);
        }

        // Parsing: Stage 3
        System.out.println("\n------------------------");
        System.out.println("___The Actual Parsing___");
        System.out.println("------------------------");

        ErrorHandler eh = new ErrorHandler(tableReader.getTerminals());
        eh.setFollowSets(tableReader.getFollowSets());

        Parser parser = new Parser(tableReader, eh);
        TreeNode root = parser.parse(tokens);

        /*----------------------------------*/
        ParseTree tree = new ParseTree(root);
        tree.print();
        tree.printPreorder();

        System.out.println("Parsing completed with "+ eh.getErrorCount() + " error(s).");
        System.out.println("\n--------------------");
    }
}
