import java.io.*;
import java.util.*;
import java.nio.file.*;

class Main {

    static final String OUTPUT_FILE = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/ParseResults.txt";
    static final String PARSE_TABLE = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/ParseTable.txt";
    static final String INPUT_FILE  = "/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/tests/test1.lang";

    public static void main(String[] args) {

        // ── REDIRECT ALL OUTPUT TO ParseResults.txt ──────────
        try {
            PrintStream fileOut = new PrintStream(OUTPUT_FILE);
            System.setOut(fileOut);
        } catch (FileNotFoundException e) {
            System.err.println("[ERROR] Could not open output file: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("\n\n\t\t <<<<<<<<<<<<<<<<< LL(1) Parser Results >>>>>>>>>>>>>>>>:\n");

        // ── RESOLVE FILES: use args if provided, else use hardcoded defaults ──
        String parseTableFile = (args.length > 0) ? args[0] : PARSE_TABLE;
        String inputFile      = (args.length > 1) ? args[1] : INPUT_FILE;

        // ── STEP 1: Load Parse Table ──────────────────────────
        ParseTableReader tableReader = new ParseTableReader();
        try {
            tableReader.loadTable(parseTableFile);
            System.out.println("[OK] Parse table loaded from: " + parseTableFile);
            tableReader.printTable();
        } catch (IOException e) {
            System.out.println("[ERROR] Could not load parse table: " + e.getMessage());
            System.exit(1);
        }

        // ── STEP 2: Setup Error Handler ───────────────────────
        // FOLLOW sets — Member 2 will eventually write these into ParseTable.txt
        // For now they are hardcoded here
        HashMap<String, Set<String>> followSets = new HashMap<>();
        followSets.put("Expr",       new HashSet<>(Arrays.asList("$", ")")));
        followSets.put("ExprPrime",  new HashSet<>(Arrays.asList("$", ")")));
        followSets.put("Term",       new HashSet<>(Arrays.asList("+", ")", "$")));
        followSets.put("TermPrime",  new HashSet<>(Arrays.asList("+", ")", "$")));
        followSets.put("Factor",     new HashSet<>(Arrays.asList("*", "+", ")", "$")));

        // ── STEP 3: Read and parse each input string ──────────
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line;
            int stringNumber = 1;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                System.out.println("\n" + "=".repeat(60));
                System.out.printf("Input String %d: %s%n", stringNumber++, line);
                System.out.println("=".repeat(60));

                // ── MODE A: Mock tokenizer (no real lexer needed for testing)
                ArrayList<Token> tokens = mockTokenize(line);

                // ── MODE B: Plug your real Lexer in here when ready
                // ManualScanner ms = new ManualScanner(line);
                // ms.tokenise();
                // ArrayList<Token> tokens = ms.getTokenList();

                if (tokens == null || tokens.isEmpty()) {
                    System.out.println("[SKIP] Could not tokenize input.");
                    continue;
                }

                // Fresh error handler + parser per input string
                ErrorHandler eh = new ErrorHandler(tableReader.getTerminals());
                eh.setFollowSets(followSets);
                Parser p = new Parser(tableReader, eh);

                // Parse → get parse tree root
                TreeNode root = p.parse(tokens);

                // Print parse tree only if accepted
                if (root != null) {
                    ParseTree tree = new ParseTree(root);
                    tree.print();
                    tree.printPreorder();
                }
            }

            br.close();

        } catch (IOException e) {
            System.out.println("[ERROR] Could not read input file: " + e.getMessage());
        }

        System.out.println("\n\n\t\t ================ End of Parse Results ================\n");
    }

    // ─── MODE A: Mock tokenizer ───────────────────────────────
    // Splits space-separated input string into Token objects
    // Remove this when plugging in your real Lexer
    private static ArrayList<Token> mockTokenize(String input) {
        ArrayList<Token> tokens = new ArrayList<>();
        String[] parts = input.split("\\s+");
        int col = 1;

        for (String part : parts) {
            if (part.equals("$")) break; // parser adds $ automatically
            tokens.add(new Token(mockGetType(part), part, 1, col));
            col += part.length() + 1;
        }

        return tokens;
    }

    private static String mockGetType(String s) {
        switch (s) {
            case "+":  return "TOK_PLUS";
            case "*":  return "TOK_STAR";
            case "(":  return "TOK_LPAREN";
            case ")":  return "TOK_RPAREN";
            case "-":  return "TOK_MINUS";
            case "/":  return "TOK_DIV";
            case "id": return "TOK_IDENTIFIER";
            default:   return "TOK_IDENTIFIER";
        }
    }
}

/*
public class Main {

    public static void main(String[] args) {

        // ── ARGUMENT CHECK ────────────────────────────────────
        if (args.length < 2) {
            System.out.println("Usage: java Main <ParseTable.txt> <input.txt>");
            System.exit(1);
        }

        String parseTableFile = args[0];
        String inputFile      = args[1];

        // ── STEP 1: Load Parse Table ──────────────────────────
        ParseTableReader tableReader = new ParseTableReader();
        try {
            tableReader.loadTable(parseTableFile);
            System.out.println("[OK] Parse table loaded from: " + parseTableFile);
            tableReader.printTable();  // debug print
        } catch (IOException e) {
            System.out.println("[ERROR] Could not load parse table: " + e.getMessage());
            System.exit(1);
        }

        // ── STEP 2: Setup Error Handler ───────────────────────
        ErrorHandler errorHandler = new ErrorHandler(tableReader.getTerminals());

        // Hardcode FOLLOW sets for this grammar
        // (In full implementation, Member 2 writes these to ParseTable.txt too)
        HashMap<String, Set<String>> followSets = new HashMap<>();
        followSets.put("Expr",       new HashSet<>(Arrays.asList("$", ")")));
        followSets.put("ExprPrime",  new HashSet<>(Arrays.asList("$", ")")));
        followSets.put("Term",       new HashSet<>(Arrays.asList("+", ")", "$")));
        followSets.put("TermPrime",  new HashSet<>(Arrays.asList("+", ")", "$")));
        followSets.put("Factor",     new HashSet<>(Arrays.asList("*", "+", ")", "$")));
        errorHandler.setFollowSets(followSets);

        // ── STEP 3: Setup Parser ──────────────────────────────
        Parser parser = new Parser(tableReader, errorHandler);

        // ── STEP 4: Read input strings and parse each one ─────
        // Two modes:
        //   A) Read raw strings from input.txt and mock-tokenize them
        //      (for testing without the full lexer)
        //   B) Plug in your real Lexer tokens
        //
        // Mode A is active here. Swap in Mode B when lexer is ready.

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line;
            int stringNumber = 1;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                System.out.println("=".repeat(60));
                System.out.printf("Input String %d: %s%n", stringNumber++, line);
                System.out.println("=".repeat(60));

                // MODE A: mock tokenize (space-split, no real lexer)
                ArrayList<Token> tokens = mockTokenize(line, parser);

                // MODE B: plug your lexer here instead
                // Lexer lexer = new Lexer(line);
                // ArrayList<Token> tokens = lexer.tokenize();

                if (tokens == null) {
                    System.out.println("[SKIP] Could not tokenize input.\n");
                    continue;
                }

                // Reset error handler for each string
                ErrorHandler eh = new ErrorHandler(tableReader.getTerminals());
                eh.setFollowSets(followSets);
                Parser p = new Parser(tableReader, eh);

                // Parse and get tree
                TreeNode root = p.parse(tokens);

                // Print parse tree if accepted
                if (root != null) {
                    ParseTree tree = new ParseTree(root);
                    tree.print();
                    tree.printPreorder();
                }

                System.out.println();
            }

            br.close();

        } catch (IOException e) {
            System.out.println("[ERROR] Could not read input file: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // MODE A: Mock tokenizer — splits input string by spaces
    // and maps each token to a Token object
    // Replace this with your real Lexer when ready
    // -------------------------------------------------------
    private static ArrayList<Token> mockTokenize(String input, Parser parser) {
        ArrayList<Token> tokens = new ArrayList<>();
        String[] parts = input.split("\\s+");
        int col = 1;

        for (String part : parts) {
            if (part.equals("$")) break; // will be added by parser

            String type = mockGetType(part);
            tokens.add(new Token(type, part, 1, col));
            col += part.length() + 1;
        }

        return tokens;
    }

    // Map raw string to a fake token type (for testing only)
    private static String mockGetType(String s) {
        switch (s) {
            case "+":  return "TOK_PLUS";
            case "*":  return "TOK_STAR";
            case "(":  return "TOK_LPAREN";
            case ")":  return "TOK_RPAREN";
            case "-":  return "TOK_MINUS";
            case "/":  return "TOK_DIV";
            case "id": return "TOK_IDENTIFIER";
            default:   return "TOK_IDENTIFIER"; // treat unknowns as identifiers
        }
    }
}*/