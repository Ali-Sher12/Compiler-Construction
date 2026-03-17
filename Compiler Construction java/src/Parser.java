import java.util.*;

/**
 * Parser.java
 * Core LL(1) predictive parser.
 *
 * Pipeline:
 *   Token list (from Lexer)
 *       → mapTokenToTerminal()
 *       → stack-based parsing loop
 *       → parse tree construction
 *       → step-by-step trace output
 */
public class Parser {

    private ParseTableReader tableReader;
    private ErrorHandler     errorHandler;

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------
    public Parser(ParseTableReader tableReader, ErrorHandler errorHandler) {
        this.tableReader  = tableReader;
        this.errorHandler = errorHandler;
    }

    // -------------------------------------------------------
    // PUBLIC: Main parse method
    // Takes the full token list from your lexer
    // Returns the root of the parse tree (null if failed)
    // -------------------------------------------------------
    public TreeNode parse(ArrayList<Token> tokens) {

        // Add $ sentinel at the end if not already there
        Token last = tokens.get(tokens.size() - 1);
        if (!last.Type.equals("$")) {
            tokens.add(new Token("$", "$", last.Line, last.Column + 1));
        }

        // ── STACK SETUP ──────────────────────────────────────
        Deque<String> stack = new ArrayDeque<>();
        stack.push("$");
        stack.push(tableReader.getStartSymbol());

        // ── PARSE TREE SETUP ─────────────────────────────────
        // We maintain a parallel stack of TreeNodes
        // Each grammar symbol on the symbol stack has a matching
        // TreeNode on the tree stack
        Deque<TreeNode> treeStack = new ArrayDeque<>();
        TreeNode root        = new TreeNode(tableReader.getStartSymbol());
        TreeNode dollarNode  = new TreeNode("$");   // placeholder for $ on tree stack
        treeStack.push(dollarNode);
        treeStack.push(root);

        // ── TOKEN INDEX ──────────────────────────────────────
        int[] tokenIndex = {0};  // array so ErrorHandler can modify it

        // ── TRACE HEADER ─────────────────────────────────────
        printTraceHeader();

        int step = 1;
        boolean accepted = false;

        // ── MAIN PARSING LOOP ─────────────────────────────────
        while (!stack.isEmpty()) {

            String  X = stack.peek();
            Token   currentToken = tokens.get(tokenIndex[0]);
            String  a = mapTokenToTerminal(currentToken);

            // Print current step
            printStep(step++, stack, tokens, tokenIndex[0]);

            // ── CASE 1: Both stack and input are at $ → ACCEPT ──
            if (X.equals("$") && a.equals("$")) {
                System.out.println("  Action : ACCEPT ✓");
                accepted = true;
                break;
            }

            // ── CASE 2: Top of stack is $ but input isn't ────────
            if (X.equals("$")) {
                errorHandler.Extra_Input(currentToken.Lexeme, currentToken.Line, currentToken.Column);
                break;
            }

            // ── CASE 3: X is a terminal ──────────────────────────
            if (tableReader.isTerminal(X)) {
                if (X.equals(a)) {
                    // MATCH — pop stack, advance input
                    System.out.printf("  Action : Match '%s'%n", X);
                    stack.pop();
                    treeStack.pop();
                    tokenIndex[0]++;
                } else {
                    // MISMATCH
                    errorHandler.Terminal_Mismatch(X, currentToken.Type, currentToken.Lexeme, currentToken.Line, currentToken.Column);
                    // Simple recovery: pop the terminal and continue
                    stack.pop();
                    treeStack.pop();
                }
                continue;
            }

            // ── CASE 4: X is a non-terminal ─────────────────────
            if (tableReader.isNonTerminal(X)) {
                String[] production = tableReader.getProduction(X, a);

                if (production == null) {
                    // ERROR: empty table entry
                    errorHandler.No_Production(X, a, currentToken.Lexeme, currentToken.Line, currentToken.Column);
                    // Panic mode recovery
                    TreeNode errorNode = treeStack.peek();
                    boolean popped = errorHandler.panicModeRecovery(
                        stack, tokens, tokenIndex, X
                    );
                    if (popped) treeStack.pop();
                    // else panicMode already popped from stack
                    continue;
                }

                // Valid production found
                System.out.printf("  Action : Expand %s -> %s%n",
                    X, String.join(" ", production));

                // Pop the non-terminal from both stacks
                stack.pop();
                TreeNode parentNode = treeStack.pop();

                // Handle epsilon production
                if (production.length == 1 && production[0].equals("epsilon")) {
                    if (parentNode != null) {
                        parentNode.addChild(new TreeNode("epsilon"));
                    }
                    continue;
                }

                // Push production symbols in REVERSE order onto symbol stack
                // Push tree nodes in REVERSE order onto tree stack
                // Create child nodes and attach to parent
                TreeNode[] childNodes = new TreeNode[production.length];
                for (int i = 0; i < production.length; i++) {
                    childNodes[i] = new TreeNode(production[i]);
                    if (parentNode != null) {
                        parentNode.addChild(childNodes[i]);
                    }
                }

                // Push to stacks in reverse so leftmost symbol is on top
                for (int i = production.length - 1; i >= 0; i--) {
                    stack.push(production[i]);
                    treeStack.push(childNodes[i]);
                }

                continue;
            }

            // ── FALLTHROUGH: Unknown symbol ──────────────────────
            System.out.printf("  [WARN] Unknown symbol '%s' on stack, popping.%n", X);
            stack.pop();
            treeStack.pop();
        }

        // ── RESULT ────────────────────────────────────────────
        printResult(accepted, errorHandler.getErrorCount());

        return accepted ? root : null;
    }

    // -------------------------------------------------------
    // Map token type → grammar terminal string
    // This is the bridge between your Lexer and the grammar
    // -------------------------------------------------------
    public String mapTokenToTerminal(Token t) {
        switch (t.Type) {
            case "TOK_PLUS":        return "+";
            case "TOK_MINUS":       return "-";
            case "TOK_STAR":        return "*";
            case "TOK_DIV":         return "/";
            case "TOK_MOD":         return "%";
            case "TOK_LPAREN":      return "(";
            case "TOK_RPAREN":      return ")";
            case "TOK_LCURLY":      return "{";
            case "TOK_RCURLY":      return "}";
            case "TOK_LSQUAR":      return "[";
            case "TOK_RSQUAR":      return "]";
            case "TOK_SEMICOLON":   return ";";
            case "TOK_COMMA":       return ",";
            case "TOK_EQ":          return "=";
            case "TOK_EQEQ":        return "==";
            case "TOK_NE":          return "!=";
            case "TOK_LT":          return "<";
            case "TOK_GT":          return ">";
            case "TOK_LE":          return "<=";
            case "TOK_GE":          return ">=";
            case "TOK_AND":         return "&&";
            case "TOK_OR":          return "||";
            case "TOK_NOT":         return "!";
            case "TOK_IDENTIFIER":  return "id";
            case "TOK_INTEGER":     return "int_lit";
            case "TOK_FLOAT":       return "float_lit";
            case "TOK_STRING":      return "string_lit";
            case "TOK_CONDITION":   return "condition";
            case "TOK_LOOP":        return "loop";
            case "TOK_ELSE":        return "else";
            case "TOK_RETURN":      return "return";
            case "TOK_DECLARE":     return "declare";
            case "TOK_OUTPUT":      return "output";
            case "TOK_INPUT":       return "input";
            case "TOK_FUNCTION":    return "function";
            case "TOK_BREAK":       return "break";
            case "TOK_CONTINUE":    return "continue";
            case "TOK_TRUE":        return "true";
            case "TOK_FALSE":       return "false";
            case "TOK_START":       return "start";
            case "TOK_FINISH":      return "finish";
            case "$":               return "$";
            default:                return t.Type; // fallback: use type as-is
        }
    }

    // -------------------------------------------------------
    // Print the trace table header
    // -------------------------------------------------------
    private void printTraceHeader() {
        System.out.println();
        System.out.println("========== PARSING TRACE ==========");
        System.out.printf("%-6s | %-35s | %-25s | %s%n",
            "Step", "Stack (bottom -> top)", "Remaining Input", "Action");
        System.out.println("-".repeat(100));
    }

    // -------------------------------------------------------
    // Print one step of the trace
    // -------------------------------------------------------
    private void printStep(int step, Deque<String> stack,
                           ArrayList<Token> tokens, int tokenIndex) {
        // Build stack string (bottom to top)
        List<String> stackList = new ArrayList<>(stack);
        Collections.reverse(stackList);                    // bottom → top
        String stackStr = String.join(" ", stackList);
        if (stackStr.length() > 33) {
            stackStr = "..." + stackStr.substring(stackStr.length() - 30);
        }

        // Build remaining input string
        StringBuilder input = new StringBuilder();
        for (int i = tokenIndex; i < tokens.size() && i < tokenIndex + 6; i++) {
            input.append(tokens.get(i).Lexeme).append(" ");
        }
        if (tokens.size() - tokenIndex > 6) input.append("...");
        String inputStr = input.toString().trim();

        System.out.printf("%-6d | %-35s | %-25s | ",
            step, stackStr, inputStr);
        // Action will be printed by the caller right after this
    }

    // -------------------------------------------------------
    // Print final result summary
    // -------------------------------------------------------
    private void printResult(boolean accepted, int errorCount) {
        System.out.println("-".repeat(100));
        if (accepted && errorCount == 0) {
            System.out.println("Result: String ACCEPTED ✓");
        } else if (accepted && errorCount > 0) {
            System.out.printf("Result: Parsing completed with %d error(s).%n", errorCount);
        } else {
            System.out.printf("Result: String REJECTED ✗ (%d error(s))%n", errorCount);
        }
        System.out.println("====================================\n");
    }
}
