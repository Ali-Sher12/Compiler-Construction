import java.io.IOException;

public class MainGrammarTest {
    public static void main(String[] args) {
        Grammar g = new Grammar();
        try {
            System.out.println("--- Original CFG ---");
            g.loadFromFile("/home/mustafa/Desktop/CC/Compiler-Construction/Compiler Construction java/tests/test_grammar.txt");
            g.printGrammar();
            
            System.out.println("\n--- After Left Factoring ---");
            g.eliminateLeftFactoring();
            g.printGrammar();
            
            System.out.println("\n--- After Left Recursion Removal ---");
            g.eliminateLeftRecursion();
            g.printGrammar();
            
            g.exportToFile("/home/mustafa/Desktop/CC/Compiler-Construction/Compiler Construction java/tests/output_grammar.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
