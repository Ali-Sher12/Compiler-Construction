import java.util.*;

public class ErrorHandler {

    boolean error_found = false;
    int errorCount  = 0;

    HashMap<String, Set<String>> followSets;
    Set<String> terminals;

    public ErrorHandler(Set<String> terminals) {
        this.followSets = new HashMap<>();
        this.terminals  = terminals;
    }
    public ErrorHandler() {}
    public void setFollowSets(HashMap<String, Set<String>> followSets)
    {
        this.followSets = followSets;
    }

    //lexer errors
    void Iden_Overflow(String lexeme, int line, int column) {
        System.out.println("SyntaxError: Identifier must have char_size <= 31. ["+lexeme+"] | line: "+line+" | Column: "+column);
    }

    void Iden_FirstChar(String lexeme, int line, int column) {
        System.out.println("SyntaxError: Identifier must begin with an uppercase letter. ["+lexeme+"] | line: "+line+" | Column: "+column);
    }

    void Comm_SingleLine(int line) {
        System.out.println("SyntaxError: Single-Line comment not closed properly. line: "+line);
    }

    void Comm_MultiLine(int line) {
        System.out.println("SyntaxError: Multi-Line comment not closed properly. line: "+line);
    }

    void Float_PointError(String lexeme, int line, int column) {
        System.out.println("SyntaxError: Invalid float. ["+lexeme+"] | line: "+line+" | Column: "+column);
    }

    void Char_Error(int line, int column) {
        System.out.println("SyntaxError: Invalid char. line: "+line+" | Column: "+column);
    }

    void String_UnclosedError(int line, int column) {
        System.out.println("SyntaxError: Unclosed String. line: "+line+" | Column: "+column);
    }

    void Exponent_E(int line, int column) {
        System.out.println("SyntaxError: Invalid Exponent Syntax. line: "+line+" | Column: "+column);
    }

    void Invalid_Char(String lexeme, int line, int column) {
        System.out.println("SyntaxError: Unknown Symbol Found. ["+lexeme+"] | line: "+line+" | Column: "+column);
    }

    // Parser Errors
    //Terminal on stack doesn't match current input token
    void Terminal_Mismatch(String expected, String found, String lexeme, int line, int column) {
        System.out.println("ParseError: Expected '"+expected+"' but found '"+found+"' ["+lexeme+"] | line: "+line+" | Column: "+column);
        error_found = true;
        errorCount+=1;
    }

    //Empty table entry
    void No_Production(String nonTerminal, String terminal, String lexeme, int line, int column) {
        System.out.println("ParseError: No rule for <"+nonTerminal+"> on input '"+terminal+"' ["+lexeme+"] | line: "+line+" | Column: "+column);
        error_found = true;
        errorCount+=1;
    }

    //Input is $, but stack top is not $
    void Premature_End(String topOfStack, int line, int column) {
        System.out.println("ParseError: Unexpected end of input. Still expected <"+topOfStack+"> | line: "+line+" | Column: "+column);
        error_found = true;
        errorCount+=1;
    }

    //Stack top is $, but input is not $
    void Extra_Input(String lexeme, int line, int column) {
        System.out.println("ParseError: Unexpected token after end of expression. ["+lexeme+"] | line: "+line+" | Column: "+column);
        error_found = true;
        errorCount+=1;
    }

    // Panic Mode recovery
    boolean panicModeRecovery(Deque<String> stack,ArrayList<Token> tokens,int[] tokenIndex,String nonTerminal) {

        Set<String> follow = followSets.getOrDefault(nonTerminal, new HashSet<>());
        String currentTerminal = mapTypeToTerminal(tokens.get(tokenIndex[0]));

        // We js pop the problematic non-terminal
        if (follow.contains(currentTerminal)) {
            Recovery_Pop(nonTerminal, currentTerminal);
            stack.pop();
            return true;
        }
        // We skip tokens here
        while (tokenIndex[0] < tokens.size() - 1) {
            Token t = tokens.get(tokenIndex[0]);
            Recovery_Skip(t.Lexeme);
            tokenIndex[0]++;
            String term = mapTypeToTerminal(tokens.get(tokenIndex[0]));
            if (follow.contains(term) || term.equals("$")) {
                Recovery_Resume(tokens.get(tokenIndex[0]).Lexeme);
                stack.pop();
                return false;
            }
        }

        Recovery_Failed(nonTerminal);
        stack.pop();
        return false;
    }

    void Recovery_Pop(String nonTerminal, String syncSymbol) {
        System.out.println("  [Recovery] Popped <"+nonTerminal+"> — '"+syncSymbol+"' found in FOLLOW set.");
    }

    void Recovery_Skip(String lexeme) {
        System.out.println("  [Recovery] Skipping token ["+lexeme+"]");
    }

    void Recovery_Resume(String lexeme) {
        System.out.println("  [Recovery] Resuming at ["+lexeme+"]");
    }

    void Recovery_Failed(String nonTerminal) {
        System.out.println("  [Recovery] Could not recover for <"+nonTerminal+">. Popping and continuing.");
    }

    private String mapTypeToTerminal(Token t) {
        switch (t.Type) {
            case "TOK_PLUS": return "+";
            case "TOK_STAR": return "*";
            case "TOK_LPAREN": return "(";
            case "TOK_RPAREN": return ")";
            case "TOK_IDENTIFIER": return "id";
            case "$": return "$";
            default: return t.Type;
        }
    }

    boolean hasErrors() {
        return error_found;
    }

    int getErrorCount() {
        return errorCount;
    }

    void reset() {
        error_found = false;
        errorCount  = 0;
    }
}