import java.io.FileWriter;

public class ErrorHandler {

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
}
