public class Token
{
    int Line = 0;
    int Column = 0;
    String Lexeme = "";
    String Type = "";

    Token(String type,String lexeme,int line, int column)
    {
        Type = type; Lexeme = lexeme;
        Line = line; Column = column;
    }
    void set(String type,String lexeme,int line, int column)
    {
        Type = type; Lexeme = lexeme;
        Line = line; Column = column;
    }
    void print()
    {
        System.out.println("Token type: "+Type+" | Lexeme: "+Lexeme+" | Line: "+Line+" | Column: "+Column+"\n");
    }
}
