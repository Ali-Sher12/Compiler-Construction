import java.io.*;
import java.util.*;
import java.nio.file.*;

public class ManualScanner
{
    TokenType Tokens_Dict;
    int line = 1;
    int start = 0;
    int curr = 0;
    int column = 1;
    ArrayList<Token> Tokens_List;
    String source;
    boolean error_found = false;
    ErrorHandler err;
    SymbolTable symbols;

    ArrayList<Integer> SingleComm_List;
    ArrayList<ArrayList<Integer>> MultiComm_List;

    ManualScanner(String input_path)
    {
        err = new ErrorHandler();
        symbols = new SymbolTable();
        Tokens_Dict = new TokenType();
        Tokens_List = new ArrayList<Token>();
        SingleComm_List = new ArrayList<Integer>();
        MultiComm_List = new ArrayList<ArrayList<Integer>>();

        try
        {
            Path path = Path.of(input_path);
            source = Files.readString(path);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    boolean Identifier_Eligibility(){
        if(is_lowercase(source.charAt(start))){
            err.Iden_FirstChar(source.substring(start,curr),line,column-(curr-start));
            error_found = true;
            return false;
        }
        if((curr-start) > 30)
        {
            err.Iden_Overflow(source.substring(start,curr),line,column-(curr-start));
            error_found = true;
            return false;
        }
        return true;
    }

    String isKEYWORD(String lexeme) {
        if(Tokens_Dict.token_map.containsKey(lexeme.toLowerCase()))
            return Tokens_Dict.token_map.get(lexeme.toLowerCase());
        if(Identifier_Eligibility())
            return Tokens_Dict.TOK_IDENTIFIER;
        return Tokens_Dict.NOT_A_TOKEN;
    }

    boolean is_digit(char num){
        return num >= '0' && num <= '9';
    }

    char advance(){
        if(curr >= source.length())
            return '\0';
        column+=1;
        char ch = source.charAt(curr);
        curr += 1;
        return ch;
    }

    void add_token(String input_type){
        // add validations using input_type
        if(Objects.equals(input_type, Tokens_Dict.TOK_IDENTIFIER) && !Identifier_Eligibility()) return;
        else if(Objects.equals(input_type, Tokens_Dict.NOT_A_TOKEN)) return;

        Token tempToken = new Token(input_type, source.substring(start, curr),line,column-(curr-start));
        Tokens_List.add(tempToken);

        if(Tokens_Dict.token_map.containsKey(source.substring(start, curr).toLowerCase()))
            symbols.add(tempToken.Lexeme.toLowerCase());
        else symbols.add(tempToken.Lexeme);
    }

    char peek(){
        if(curr >= source.length())
            return '\0';
        return source.charAt(curr);
    }

    char lookahead(){
        if(curr+1 >= source.length())
            return '\0';
        return source.charAt(curr+1);
    }

    boolean is_uppercase(char ch){
        return ('A' <= ch && ch <= 'Z');
    }

    boolean is_lowercase(char ch){
        return ('a' <= ch && ch <= 'z');
    }

    boolean is_alphabet(char ch){
        return is_uppercase(ch) || is_lowercase(ch);
    }

    void tokenise(){
        while (curr < source.length()) {
            error_found = false;
            start = curr;
            char ch = advance();
            if(ch =='\0') return;
            if(ch =='\n'){
                line+=1;
                column = 1;
            }
            else if (ch == '\t') {
                column+=3;
                continue;
            }
            else if (ch == '\r') continue;
            else if (ch ==' ') continue;
            else if (ch == '#'){
                if( peek() == '#' ){
                    SingleComm_List.add(line);
                    while (peek() != '\n'){
                        if (peek() == '\0')
                            return;
                        advance();
                    }
                    if(peek() == '\n'){
                        line+=1;
                        column = 1;
                        advance();
                    }
                }
                else if( peek() == '*' ){
                    advance();
                    ArrayList<Integer> temp_storage = new ArrayList<>();
                    temp_storage.add(line);
                    temp_storage.add(1);
                    while (!(peek() == '*' && lookahead() == '#')){
                        if (peek() == '\0') {
                            err.Comm_MultiLine(line);
                            return;
                        }
                        else if (peek() == '\n') {
                            temp_storage.set(1, temp_storage.get(1) + 1);
                            column = 1;
                            line+=1;
                        }
                        advance();
                    }
                    MultiComm_List.add(temp_storage);
                    advance(); advance();
                }
                else err.Comm_SingleLine(line);

            }
            else if (ch =='(') add_token(Tokens_Dict.TOK_LPAREN);
            else if (ch =='$' || ch =='@') {
                err.Invalid_Char(source.substring(start,curr),line,column-(curr-start));
                continue;
            }
            else if (ch ==')') add_token(Tokens_Dict.TOK_RPAREN);
            else if (ch =='{') add_token(Tokens_Dict.TOK_LCURLY);
            else if (ch =='}') add_token(Tokens_Dict.TOK_RCURLY);
            else if (ch =='[') add_token(Tokens_Dict.TOK_LSQUAR);
            else if (ch ==']') add_token(Tokens_Dict.TOK_RSQUAR);
            else if (ch ==',') add_token(Tokens_Dict.TOK_COMMA);
            else if (ch ==';') add_token(Tokens_Dict.TOK_SEMICOLON);
            else if (ch =='?') add_token(Tokens_Dict.TOK_QUESTION);
            else if (ch =='%') add_token(Tokens_Dict.TOK_MOD);
            else if (ch ==':') add_token(Tokens_Dict.TOK_COLON);
            else if (ch =='.') add_token(Tokens_Dict.TOK_DOT);
            else if (ch =='^') add_token(Tokens_Dict.TOK_LOGICALXOR);
            else if (ch =='=') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_EQEQ);
                }
                else add_token(Tokens_Dict.TOK_EQ);
            }
            else if (ch =='&') {
                if (peek() == '&') {
                    advance();
                    add_token(Tokens_Dict.TOK_LOGICALAND);
                }
                else add_token(Tokens_Dict.TOK_AND);
            }
            else if (ch =='|') {
                if (peek() == '|') {
                    advance();
                    add_token(Tokens_Dict.TOK_LOGICALOR);
                }
                else add_token(Tokens_Dict.TOK_OR);
            }
            else if (ch =='*') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_MULASSIGN);
                }
                else if (peek() == '*') {
                    advance();
                    add_token(Tokens_Dict.TOK_EXPONENT);
                }
                else add_token(Tokens_Dict.TOK_STAR);
            }

            else if (ch =='/') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_DIVASSIGN);
                }
                else add_token(Tokens_Dict.TOK_DIV);
            }
            else if (ch =='-') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_MINUSASSIGN);
                }
                else if (peek() == '-') {
                    advance();
                    add_token(Tokens_Dict.TOK_DEC);
                }
                else if (is_digit(peek())) {
                    while (is_digit(peek()))
                        advance();
                    if (peek() == '.' && is_digit (lookahead())) {
                        advance(); //consumes the '.'
                        while (is_digit(peek())) {
                            advance();
                        }
                        if(!exponent_condition_helper()){
                            add_token(Tokens_Dict.TOK_FLOAT);
                        }
                    }
                    else if (peek() == '.' && !is_digit (lookahead())) {
                        error_found = true;
                        err.Float_PointError(source.substring(start,curr),line,column-(curr-start));
                    }
                    else if(!exponent_condition_helper())
                    {
                        add_token(Tokens_Dict.TOK_INTEGER);
                    }
                }
                else add_token(Tokens_Dict.TOK_MINUS);
            }

            else if (ch =='+') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_PLUSASSIGN);
                }
                else if (peek() == '+') {
                    advance();
                    add_token(Tokens_Dict.TOK_INC);
                }
                else if (is_digit(peek())) {
                    while (is_digit(peek()))
                        advance();
                    if (peek() == '.' && is_digit (lookahead())) {
                        advance(); //consumes the '.'
                        while (is_digit(peek())) {
                            advance();
                        }
                        if(!exponent_condition_helper()){
                            add_token(Tokens_Dict.TOK_FLOAT);
                        }
                    }
                    else if (peek() == '.' && !is_digit (lookahead())) {
                        err.Float_PointError(source.substring(start,curr),line,column-(curr-start));
                        error_found = true;
                    }
                    else if(!exponent_condition_helper())
                    {
                        add_token(Tokens_Dict.TOK_INTEGER);
                    }
                }
                else add_token(Tokens_Dict.TOK_PLUS);
            }
            else if (ch =='!') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_NE);
                }
                else add_token(Tokens_Dict.TOK_NOT);
            }

            else if (ch =='<'){
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_LE);
                }
                else add_token(Tokens_Dict.TOK_LT);
            }

            else if(ch =='>') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_GE);
                }
                else add_token(Tokens_Dict.TOK_GT);
            }

            else if (ch =='\'') {
                ch = advance();
                if (ch == '\\'){
                    advance();
                    if(peek() != '\''){
                        err.Char_Error(line,column-(curr-start));
                        error_found = true;
                    }
                    else {
                        advance();
                        add_token(Tokens_Dict.TOK_CHAR);
                    }
                }
                else if(peek() != '\''){
                    err.Char_Error(line,column-(curr-start));
                    error_found = true;
                }
                else {
                    advance();
                    add_token(Tokens_Dict.TOK_CHAR);
                }
            }

            else if (ch =='\"') {
                while (peek() != '\"') {
                    if (peek() == '\0')
                        break;
                    advance();
                }
                if (peek() == '\"') {
                    advance();
                    add_token(Tokens_Dict.TOK_STRING);
                }
                else{
                    err.String_UnclosedError(line,column-(curr-start));
                    error_found = true;
                }
            }

            else if (is_digit(ch)) {
                while (is_digit(peek()))
                    advance();
                if (peek() == '.' && is_digit (lookahead())) {
                    advance(); //consumes the '.'
                    while (is_digit(peek())) {
                        advance();
                    }
                    if(!exponent_condition_helper()){
                        add_token(Tokens_Dict.TOK_FLOAT);
                    }
                }
                else if (peek() == '.' && !is_digit (lookahead())) {
                    err.Float_PointError(source.substring(start,curr),line,column-(curr-start));
                    error_found = true;
                }
                else if(!exponent_condition_helper()) {
                    add_token(Tokens_Dict.TOK_INTEGER);
                }
            }
            else if (is_alphabet (ch)) {
                while (is_alphabet(peek()) || peek() == '_' || is_digit(peek())) {
                    if (peek() == '\0')
                        break;
                    advance ();
                }
                String token_lexeme = source.substring(start, curr);
                add_token(isKEYWORD(token_lexeme));
            }
        }
    }

    private boolean exponent_condition_helper()
    {
        if (peek() == 'e' || peek() == 'E'){
            advance();
            if (peek() == '+' || peek() == '-') {
                advance();
                if (!is_digit(peek())) {
                    err.Exponent_E(line,column-(curr-start));
                    error_found = true;
                }
                while (is_digit(peek()))
                    advance();
                if(!error_found) add_token(Tokens_Dict.TOK_FLOAT);
            }
            else if (is_digit (peek())){
                while (is_digit(peek()))
                    advance();
                add_token(Tokens_Dict.TOK_FLOAT);                        }

            else{
                err.Exponent_E(line,column-(curr-start));
                error_found = true;
            }
            return true;
        }
        return false;
    }

    void printList()
    {
        System.out.println("\n-----------------------------------------\n");

        for (int i=0;i<Tokens_List.size();i++)
        {
            Tokens_List.get(i).print();
        }
        System.out.println("\n-----------------------------------------\nStats: ");

        System.out.println("Total Tokens: "+Tokens_List.size()+" | Total Lines: "+line);

        System.out.println("\n-----------------------------------------\n");

        System.out.println("Singleline comments: "+SingleComm_List.size()+ " lines.");
        for (int i=0;i<SingleComm_List.size();i++)
        {
            System.out.println("Comment "+(i+1)+": | Line# "+SingleComm_List.get(i));
        }

        System.out.println("\n-----------------------------------------\n");

        System.out.println("Multiline comments: "+MultiComm_List.size()+ " blocks.");
        for (int i=0;i<MultiComm_List.size();i++)
        {
           System.out.println("Block "+(i+1)+": | Line# "+MultiComm_List.get(i).get(0)+" | Block Size: "+MultiComm_List.get(i).get(1));
        }

        System.out.println("\n-----------------------------------------\n");

        System.out.println(symbols.symbol_map.toString());
        System.out.println("\n-----------------------------------------\n");
    }

}
