import java.util.*;

public class Parser {

    private ParseTableReader tableReader;
    private ErrorHandler errorHandler;

    public Parser(ParseTableReader tableReader, ErrorHandler errorHandler) {
        this.tableReader  = tableReader;
        this.errorHandler = errorHandler;
    }

    public TreeNode parse(ArrayList<Token> tokens) {
        Token last = tokens.get(tokens.size() - 1);
        if (!last.Type.equals("$")) {
            tokens.add(new Token("$", "$", last.Line, last.Column + 1));
        }
        Deque<String> stack = new ArrayDeque<>();
        stack.push("$");
        stack.push(tableReader.getStartSymbol());

        Deque<TreeNode> treeStack = new ArrayDeque<>();
        TreeNode root        = new TreeNode(tableReader.getStartSymbol());
        TreeNode dollarNode  = new TreeNode("$");   // placeholder for $ on tree stack
        treeStack.push(dollarNode);
        treeStack.push(root);
        int[] tokenIndex = {0};  // array so ErrorHandler can modify it
        printTraceHeader();
        int step = 1;
        boolean accepted = false;
        while (!stack.isEmpty()) {
            String  X = stack.peek();
            Token   currentToken = tokens.get(tokenIndex[0]);
            String  a = mapTokenToTerminal(currentToken);
            printStep(step++, stack, tokens, tokenIndex[0]);
            if (X.equals("$") && a.equals("$")) {
                System.out.println("  Action : ACCEPT ✓");
                accepted = true;
                break;
            }
            if (X.equals("$")) {
                errorHandler.Extra_Input(currentToken.Lexeme, currentToken.Line, currentToken.Column);
                break;
            }
            if (tableReader.isTerminal(X)) {
                if (X.equals(a)) {
                    System.out.printf("  Action : Match '%s'%n", X);
                    stack.pop();
                    treeStack.pop();
                    tokenIndex[0]++;
                }
                else {
                    errorHandler.Terminal_Mismatch(X, currentToken.Type, currentToken.Lexeme, currentToken.Line, currentToken.Column);
                    stack.pop();
                    treeStack.pop();
                }
                continue;
            }

            if (tableReader.isNonTerminal(X)) {
                String[] production = tableReader.getProduction(X, a);
                if (production == null) {
                    errorHandler.No_Production(X, a, currentToken.Lexeme, currentToken.Line, currentToken.Column);
                    TreeNode errorNode = treeStack.peek();
                    boolean popped = errorHandler.panicModeRecovery(stack, tokens, tokenIndex, X);
                    if (popped) treeStack.pop();
                    continue;
                }
                System.out.printf("  Action : Expand %s -> %s%n",X, String.join(" ", production));
                stack.pop();
                TreeNode parentNode = treeStack.pop();
                if (production.length == 1 && production[0].equals("epsilon")) {
                    if (parentNode != null) {
                        parentNode.addChild(new TreeNode("epsilon"));
                    }
                    continue;
                }

                TreeNode[] childNodes = new TreeNode[production.length];
                for (int i = 0; i < production.length; i++) {
                    childNodes[i] = new TreeNode(production[i]);
                    if (parentNode != null) {
                        parentNode.addChild(childNodes[i]);
                    }
                }
                for (int i = production.length - 1; i >= 0; i--) {
                    stack.push(production[i]);
                    treeStack.push(childNodes[i]);
                }
                continue;
            }

            System.out.printf("  [WARN] Unknown symbol '%s' on stack, popping.%n", X);
            stack.pop();
            treeStack.pop();
        }
        printResult(accepted, errorHandler.getErrorCount());
        return accepted ? root : null;
    }

    public String mapTokenToTerminal(Token t) {
        switch (t.Type) {
            case "TOK_PLUS": return "+";
            case "TOK_MINUS": return "-";
            case "TOK_STAR": return "*";
            case "TOK_DIV": return "/";
            case "TOK_MOD": return "%";
            case "TOK_LPAREN": return "(";
            case "TOK_RPAREN": return ")";
            case "TOK_LCURLY": return "{";
            case "TOK_RCURLY": return "}";
            case "TOK_LSQUAR": return "[";
            case "TOK_RSQUAR": return "]";
            case "TOK_SEMICOLON": return ";";
            case "TOK_COMMA": return ",";
            case "TOK_EQ": return "=";
            case "TOK_EQEQ": return "==";
            case "TOK_NE": return "!=";
            case "TOK_LT": return "<";
            case "TOK_GT": return ">";
            case "TOK_LE": return "<=";
            case "TOK_GE": return ">=";
            case "TOK_AND": return "&&";
            case "TOK_OR": return "||";
            case "TOK_NOT": return "!";
            case "TOK_IDENTIFIER": return "id";
            case "TOK_INTEGER": return "int_lit";
            case "TOK_FLOAT": return "float_lit";
            case "TOK_STRING": return "string_lit";
            case "TOK_CONDITION": return "condition";
            case "TOK_LOOP": return "loop";
            case "TOK_ELSE": return "else";
            case "TOK_RETURN": return "return";
            case "TOK_DECLARE": return "declare";
            case "TOK_OUTPUT": return "output";
            case "TOK_INPUT": return "input";
            case "TOK_FUNCTION": return "function";
            case "TOK_BREAK": return "break";
            case "TOK_CONTINUE": return "continue";
            case "TOK_TRUE": return "true";
            case "TOK_FALSE": return "false";
            case "TOK_START": return "start";
            case "TOK_FINISH": return "finish";
            case "$": return "$";
            default: return t.Type; // fallback: use type as-is
        }
    }

    private void printTraceHeader() {
        System.out.println();
        System.out.println("========== PARSING TRACE ==========");
        System.out.printf("%-6s | %-35s | %-25s | %s%n", "Step", "Stack (bottom -> top)", "Remaining Input", "Action");
        System.out.println("-".repeat(100));
    }

    private void printStep(int step, Deque<String> stack, ArrayList<Token> tokens, int tokenIndex) {
        List<String> stackList = new ArrayList<>(stack);
        Collections.reverse(stackList);                    // bottom → top
        String stackStr = String.join(" ", stackList);
        if (stackStr.length() > 33) {
            stackStr = "..." + stackStr.substring(stackStr.length() - 30);
        }
        StringBuilder input = new StringBuilder();
        for (int i = tokenIndex; i < tokens.size() && i < tokenIndex + 6; i++) {
            input.append(tokens.get(i).Lexeme).append(" ");
        }
        if (tokens.size() - tokenIndex > 6) input.append("...");
        String inputStr = input.toString().trim();
        System.out.printf("%-6d | %-35s | %-25s | ",
            step, stackStr, inputStr);
    }

    private void printResult(boolean accepted, int errorCount) {
        System.out.println("-".repeat(100));
        if (accepted && errorCount == 0) {
            System.out.println("Result: String ACCEPTED ✓");
        }
        else if (accepted && errorCount > 0) {
            System.out.printf("Result: Parsing completed with %d error(s).%n", errorCount);
        }
        else {
            System.out.printf("Result: String REJECTED ✗ (%d error(s))%n", errorCount);
        }
        System.out.println("====================================\n");
    }
}
