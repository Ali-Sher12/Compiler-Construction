import java.io.*;
import java.util.*;

class Main {

    // ── FILE PATHS ────────────────────────────────────────────
    static final String RAW_GRAMMAR      = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/RawGrammar.txt";
    static final String PROCESSED_GRAMMAR= "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/ProcessedGrammar1.txt";
    static final String PARSE_TABLE      = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/ParseTable.txt";
    static final String INPUT_FILE       = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/test4.lang";
    static final String OUTPUT_FILE      = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/ParseResults.txt";

    public static void main(String[] args) {

        // ── REDIRECT ALL OUTPUT TO ParseResults.txt ──────────
        try {
            PrintStream fileOut = new PrintStream(OUTPUT_FILE);
            System.setOut(fileOut);
        } catch (FileNotFoundException e) {
            System.err.println("[ERROR] Could not open output file: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("\n\t\t <<<<<<<<<<<<<<<<<  Full Compiler Pipeline  >>>>>>>>>>>>>>>>>\n");

        // ════════════════════════════════════════════════════════
        // STAGE 1: Grammar Transformation
        //   RawGrammar → Left Factoring → Left Recursion Removal
        //   → ProcessedGrammar1.txt
        // ════════════════════════════════════════════════════════
        System.out.println("=" .repeat(60));
        System.out.println("  STAGE 1: Grammar Transformation");
        System.out.println("=".repeat(60));

        Grammar grammar = new Grammar();
        try {
            grammar.loadFromFile(RAW_GRAMMAR);
            System.out.println("[OK] Raw grammar loaded from: " + RAW_GRAMMAR);

            System.out.println("\n[Original Grammar]");
            grammar.printGrammar();

            grammar.eliminateLeftFactoring();
            System.out.println("\n[After Left Factoring]");
            grammar.printGrammar();

            grammar.eliminateLeftRecursion();
            System.out.println("\n[After Left Recursion Removal]");
            grammar.printGrammar();

            grammar.exportToFile(PROCESSED_GRAMMAR);
            System.out.println("\n[OK] Processed grammar saved to: " + PROCESSED_GRAMMAR);

        } catch (IOException e) {
            System.out.println("[ERROR] Grammar transformation failed: " + e.getMessage());
            System.exit(1);
        }

        // ════════════════════════════════════════════════════════
        // STAGE 2: FIRST / FOLLOW Sets + Parse Table Construction
        //   ProcessedGrammar1.txt → ParseTable.txt
        // ════════════════════════════════════════════════════════
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  STAGE 2: FIRST/FOLLOW + Parse Table");
        System.out.println("=".repeat(60));

        FirstFollow ff = new FirstFollow(grammar);
        ff.compute();
        ff.printSets();

        ParseTableBuilder builder = new ParseTableBuilder(grammar, ff);
        builder.build();
        builder.print();

        try {
            builder.writeToFile(PARSE_TABLE);
        } catch (IOException e) {
            System.out.println("[ERROR] Could not write ParseTable.txt: " + e.getMessage());
            System.exit(1);
        }

        if (!builder.isLL1()) {
            System.out.println("[ERROR] Grammar is NOT LL(1) — cannot parse.");
            for (String c : builder.getConflicts()) System.out.println("  " + c);
            System.exit(1);
        }

        // ════════════════════════════════════════════════════════
        // STAGE 3: Load Parse Table
        // ════════════════════════════════════════════════════════
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  STAGE 3: Load Parse Table");
        System.out.println("=".repeat(60));

        ParseTableReader tableReader = new ParseTableReader();
        try {
            tableReader.loadTable(PARSE_TABLE);
            System.out.println("[OK] Parse table loaded from: " + PARSE_TABLE);
            tableReader.printTable();
        } catch (IOException e) {
            System.out.println("[ERROR] Could not load parse table: " + e.getMessage());
            System.exit(1);
        }

        // ════════════════════════════════════════════════════════
        // STAGE 4: Tokenize using Lexer
        // ════════════════════════════════════════════════════════
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  STAGE 4: Lexer Tokenization");
        System.out.println("=".repeat(60));

        ArrayList<Token> tokens = null;
        try {
            ManualScanner ms = new ManualScanner(INPUT_FILE);
            ms.tokenise();
            tokens = ms.Tokens_List;
            System.out.println("[OK] Lexer tokenized: " + INPUT_FILE);
            System.out.println("[OK] Token count: " + tokens.size());
        } catch (Exception e) {
            System.out.println("[ERROR] Lexer failed: " + e.getMessage());
            System.exit(1);
        }

        if (tokens == null || tokens.isEmpty()) {
            System.out.println("[ERROR] No tokens produced by lexer.");
            System.exit(1);
        }

        // ════════════════════════════════════════════════════════
        // STAGE 5: LL(1) Parsing
        //   Tokens → Stack-based parser → Parse Tree
        // ════════════════════════════════════════════════════════
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  STAGE 5: LL(1) Parsing");
        System.out.println("=".repeat(60));

        // FOLLOW sets loaded from ParseTable.txt — no hardcoding
        ErrorHandler eh = new ErrorHandler(tableReader.getTerminals());
        eh.setFollowSets(tableReader.getFollowSets());

        Parser parser  = new Parser(tableReader, eh);
        TreeNode root  = parser.parse(tokens);

        // Print parse tree if accepted
        if (root != null) {
            ParseTree tree = new ParseTree(root);
            tree.print();
            tree.printPreorder();
        }

        // ── FINAL SUMMARY ─────────────────────────────────────
        System.out.println("\n" + "=".repeat(60));
        if (!eh.hasErrors()) {
            System.out.println("  RESULT: Parsing SUCCESSFUL ✓");
        } else {
            System.out.println("  RESULT: Parsing completed with "
                + eh.getErrorCount() + " error(s).");
        }
        System.out.println("=".repeat(60));
        System.out.println("\n\t\t =================  End of Pipeline  =================\n");
    }
}
