import java.io.IOException;

public class transform_grammar {

    public static void main(String[] args) {
        
        System.out.println("--- Starting Part 1: CFG Transformation ---");
        Grammar grammar = new Grammar();
        try {
            // Default file paths that map to the tests directory
            String grammarInput = "/home/mustafa/Desktop/CC/Compiler-Construction/Compiler Construction java/tests/test_grammar.txt";
            String grammarOutput = "/home/mustafa/Desktop/CC/Compiler-Construction/Compiler Construction java/tests/grammar_transformed.txt";
            
            grammar.loadFromFile(grammarInput);
            System.out.println("\n[Original Grammar]");
            grammar.printGrammar();
            
            grammar.eliminateLeftFactoring();
            System.out.println("\n[After Left Factoring]");
            grammar.printGrammar();
            
            grammar.eliminateLeftRecursion();
            System.out.println("\n[After Left Recursion Removal]");
            grammar.printGrammar();
            
            grammar.exportToFile(grammarOutput);
            System.out.println("\n--- Saved transformed grammar to " + grammarOutput + " ---");
            
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to process grammar: " + e.getMessage());
        }
    }
}
