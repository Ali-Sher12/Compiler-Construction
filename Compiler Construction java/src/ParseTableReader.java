import java.io.*;
import java.util.*;

/**
 * ParseTableReader.java
 * Reads the ParseTable.txt file and loads:
 *   - The LL(1) parsing table into a nested HashMap
 *   - The set of terminals
 *   - The set of non-terminals
 *   - The start symbol (first non-terminal listed)
 */
public class ParseTableReader {

    // table.get("Expr").get("id") -> ["Term", "ExprPrime"]
    private HashMap<String, HashMap<String, String[]>> table;

    private Set<String> terminals;
    private Set<String> nonTerminals;
    private String startSymbol;

    public ParseTableReader() {
        table        = new HashMap<>();
        terminals    = new LinkedHashSet<>();   // LinkedHashSet preserves insertion order
        nonTerminals = new LinkedHashSet<>();
        startSymbol  = null;
    }

    // -------------------------------------------------------
    // PUBLIC: Load the file
    // -------------------------------------------------------
    public void loadTable(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        String section = ""; // tracks which block we're in

        while ((line = br.readLine()) != null) {
            line = line.trim();

            // Skip comments and empty lines
            if (line.isEmpty() || line.startsWith("#")) continue;

            // Detect section markers
            switch (line) {
                case "TABLE_BEGIN":        section = "TABLE";         continue;
                case "TABLE_END":          section = "";              continue;
                case "TERMINALS_BEGIN":    section = "TERMINALS";     continue;
                case "TERMINALS_END":      section = "";              continue;
                case "NONTERMINALS_BEGIN": section = "NONTERMINALS";  continue;
                case "NONTERMINALS_END":   section = "";              continue;
            }

            // Parse based on current section
            switch (section) {
                case "TABLE":        parseTableEntry(line);        break;
                case "TERMINALS":    terminals.add(line);          break;
                case "NONTERMINALS": parseNonTerminal(line);       break;
            }
        }

        br.close();
    }

    // -------------------------------------------------------
    // PRIVATE: Parse one table entry line
    // Format: NonTerminal,Terminal=Symbol1 Symbol2 ...
    // Example: Expr,id=Term ExprPrime
    // Example: ExprPrime,)=epsilon
    // -------------------------------------------------------
    private void parseTableEntry(String line) {
        // Split on '=' to get key and production
        int eqIndex = line.indexOf('=');
        if (eqIndex == -1) return; // malformed line, skip

        String key        = line.substring(0, eqIndex).trim();
        String production = line.substring(eqIndex + 1).trim();

        // Split key on ',' to get NonTerminal and Terminal
        String[] keyParts = key.split(",");
        if (keyParts.length != 2) return;

        String nonTerminal = keyParts[0].trim();
        String terminal    = keyParts[1].trim();

        // Split production into individual symbols
        String[] symbols = production.split(" ");

        // Store in table
        table.computeIfAbsent(nonTerminal, k -> new HashMap<>())
             .put(terminal, symbols);
    }

    // -------------------------------------------------------
    // PRIVATE: Track non-terminals; first one = start symbol
    // -------------------------------------------------------
    private void parseNonTerminal(String nt) {
        nonTerminals.add(nt);
        if (startSymbol == null) startSymbol = nt; // first = start symbol
    }

    // -------------------------------------------------------
    // PUBLIC: Get production for M[nonTerminal, terminal]
    // Returns null if entry is empty (ERROR)
    // -------------------------------------------------------
    public String[] getProduction(String nonTerminal, String terminal) {
        HashMap<String, String[]> row = table.get(nonTerminal);
        if (row == null) return null;
        return row.get(terminal);
    }

    // -------------------------------------------------------
    // PUBLIC: Symbol type checks
    // -------------------------------------------------------
    public boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    public boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }

    // -------------------------------------------------------
    // PUBLIC: Getters
    // -------------------------------------------------------
    public String getStartSymbol()             { return startSymbol; }
    public Set<String> getTerminals()          { return terminals; }
    public Set<String> getNonTerminals()       { return nonTerminals; }

    // -------------------------------------------------------
    // DEBUG: Print the loaded table to console
    // -------------------------------------------------------
    public void printTable() {
        System.out.println("\n========== LOADED PARSING TABLE ==========");
        for (String nt : nonTerminals) {
            HashMap<String, String[]> row = table.get(nt);
            if (row == null) continue;
            for (Map.Entry<String, String[]> entry : row.entrySet()) {
                System.out.printf("  M[%-12s, %-4s] = %s%n",
                    nt,
                    entry.getKey(),
                    String.join(" ", entry.getValue()));
            }
        }
        System.out.println("==========================================\n");
    }
}
