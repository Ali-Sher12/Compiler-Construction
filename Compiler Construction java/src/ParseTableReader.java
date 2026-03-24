import java.io.*;
import java.util.*;

public class ParseTableReader {

    private HashMap<String, HashMap<String, String[]>> table;
    private Set<String> terminals;
    private Set<String> nonTerminals;
    private String startSymbol;

    private HashMap<String, Set<String>> followSets;
    public ParseTableReader() {
        table = new HashMap<>();
        terminals = new LinkedHashSet<>();
        nonTerminals = new LinkedHashSet<>();
        followSets = new HashMap<>();
        startSymbol = null;
    }

    public void loadTable(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        String section = "";

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            switch (line) {
                case "TABLE_BEGIN": section = "TABLE"; continue;
                case "TABLE_END": section = ""; continue;
                case "TERMINALS_BEGIN": section = "TERMINALS"; continue;
                case "TERMINALS_END": section = ""; continue;
                case "NONTERMINALS_BEGIN": section = "NONTERMINALS"; continue;
                case "NONTERMINALS_END": section = ""; continue;
                case "FOLLOW_BEGIN": section = "FOLLOW"; continue;
                case "FOLLOW_END": section = ""; continue;
            }

            switch (section) {
                case "TABLE": parseTableEntry(line); break;
                case "TERMINALS": terminals.add(line); break;
                case "NONTERMINALS": parseNonTerminal(line); break;
                case "FOLLOW": parseFollowEntry(line); break;
            }
        }
        br.close();
    }

    private void parseTableEntry(String line) {
        int eqIndex = line.indexOf('=');
        if (eqIndex == -1) return;

        String key = line.substring(0, eqIndex).trim();
        String production = line.substring(eqIndex + 1).trim();
        String[] keyParts = key.split(",");
        if (keyParts.length != 2) return;

        String nonTerminal = keyParts[0].trim();
        String terminal = keyParts[1].trim();
        String[] symbols = production.split(" ");

        table.computeIfAbsent(nonTerminal, k -> new HashMap<>()).put(terminal, symbols);
    }

    private void parseNonTerminal(String nt) {
        nonTerminals.add(nt);
        if (startSymbol == null) startSymbol = nt;
    }

    private void parseFollowEntry(String line) {
        int eqIndex = line.indexOf('=');
        if (eqIndex == -1) return;
        String nt      = line.substring(0, eqIndex).trim();
        String symbols = line.substring(eqIndex + 1).trim();
        Set<String> set = new LinkedHashSet<>(Arrays.asList(symbols.split("\\s+")));
        followSets.put(nt, set);
    }

    public String[] getProduction(String nonTerminal, String terminal) {
        HashMap<String, String[]> row = table.get(nonTerminal);
        if (row == null) return null;
        return row.get(terminal);
    }

    public boolean isNonTerminal(String symbol) { return nonTerminals.contains(symbol); }
    public boolean isTerminal(String symbol)    { return terminals.contains(symbol); }
    public String              getStartSymbol() { return startSymbol; }
    public Set<String>         getTerminals()   { return terminals; }
    public Set<String>         getNonTerminals(){ return nonTerminals; }
    public HashMap<String, Set<String>> getFollowSets() { return followSets; }
    public void printTable() {
        System.out.println("\n========== LOADED PARSING TABLE ==========");
        for (String nt : nonTerminals) {
            HashMap<String, String[]> row = table.get(nt);
            if (row == null) continue;
            for (Map.Entry<String, String[]> entry : row.entrySet()) {
                System.out.printf("  M[%-12s, %-4s] = %s%n",
                    nt, entry.getKey(), String.join(" ", entry.getValue()));
            }
        }
        System.out.println("==========================================\n");
    }
}
