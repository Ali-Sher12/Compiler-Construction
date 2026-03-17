import java.io.*;
import java.util.*;

public class Grammar {
    // Keeps non-terminals in order of appearance
    private LinkedHashMap<String, List<List<String>>> productions;
    private String startSymbol;

    public Grammar() {
        this.productions = new LinkedHashMap<>();
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public LinkedHashMap<String, List<List<String>>> getProductions() {
        return productions;
    }

    // Task 1.1: Input CFG
    public void loadFromFile(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            // Format: NonTerminal -> prod1 | prod2
            String[] parts = line.split("->");
            if (parts.length != 2) continue;

            String nonTerminal = parts[0].trim();
            if (startSymbol == null) {
                startSymbol = nonTerminal;
            }

            String[] rawAlternatives = parts[1].split("\\|");
            List<List<String>> alternatives = productions.getOrDefault(nonTerminal, new ArrayList<>());

            for (String alt : rawAlternatives) {
                alt = alt.trim();
                List<String> symbols = new ArrayList<>(Arrays.asList(alt.split("\\s+")));
                alternatives.add(symbols);
            }
            productions.put(nonTerminal, alternatives);
        }
        reader.close();
    }

    // Task 1.2: Left Factoring
    public void eliminateLeftFactoring() {
        boolean changed = true;
        while (changed) {
            changed = false;
            LinkedHashMap<String, List<List<String>>> newProductions = new LinkedHashMap<>();

            for (Map.Entry<String, List<List<String>>> entry : productions.entrySet()) {
                String nonTerminal = entry.getKey();
                List<List<String>> rules = entry.getValue();

                // Find the longest common prefix among rules
                int maxPrefixLen = 0;
                List<String> bestPrefix = null;
                List<List<String>> bestInvolved = null;

                for (int i = 0; i < rules.size(); i++) {
                    for (int j = i + 1; j < rules.size(); j++) {
                        List<String> r1 = rules.get(i);
                        List<String> r2 = rules.get(j);

                        List<String> prefix = getCommonPrefix(r1, r2);
                        if (!prefix.isEmpty()) {
                            // Find all rules sharing this prefix
                            List<List<String>> involved = new ArrayList<>();
                            for (List<String> r : rules) {
                                if (r.size() >= prefix.size() && r.subList(0, prefix.size()).equals(prefix)) {
                                    involved.add(r);
                                }
                            }
                            if (prefix.size() > maxPrefixLen || (prefix.size() == maxPrefixLen && involved.size() > bestInvolved.size())) {
                                maxPrefixLen = prefix.size();
                                bestPrefix = prefix;
                                bestInvolved = involved;
                            }
                        }
                    }
                }

                if (bestPrefix != null && bestInvolved.size() > 1) {
                    changed = true;
                    String newNonTerminal = generateNewNonTerminal(nonTerminal);
                    
                    List<List<String>> newRules = new ArrayList<>();
                    List<List<String>> newPrimeRules = new ArrayList<>();

                    for (List<String> r : rules) {
                        if (bestInvolved.contains(r)) {
                            List<String> suffix = new ArrayList<>(r.subList(bestPrefix.size(), r.size()));
                            if (suffix.isEmpty()) {
                                suffix.add("epsilon");
                            }
                            newPrimeRules.add(suffix);
                        } else {
                            newRules.add(r);
                        }
                    }
                    
                    List<String> factoredRule = new ArrayList<>(bestPrefix);
                    factoredRule.add(newNonTerminal);
                    newRules.add(factoredRule);

                    newProductions.put(nonTerminal, newRules);
                    newProductions.put(newNonTerminal, newPrimeRules);
                    
                    // Add the remaining undisturbed non-terminals map later down
                } else {
                    newProductions.put(nonTerminal, rules);
                }
            }
            productions = newProductions;
        }
    }

    private List<String> getCommonPrefix(List<String> r1, List<String> r2) {
        List<String> prefix = new ArrayList<>();
        int len = Math.min(r1.size(), r2.size());
        for (int k = 0; k < len; k++) {
            if (r1.get(k).equals(r2.get(k))) {
                prefix.add(r1.get(k));
            } else {
                break;
            }
        }
        return prefix;
    }

    // Task 1.3: Left Recursion Removal
    public void eliminateLeftRecursion() {
        List<String> nonTerms = new ArrayList<>(productions.keySet());

        for (int i = 0; i < nonTerms.size(); i++) {
            String Ai = nonTerms.get(i);

            for (int j = 0; j < i; j++) {
                String Aj = nonTerms.get(j);
                
                // Replace Ai -> Aj gamma with Ai -> delta1 gamma | delta2 gamma ...
                List<List<String>> aiRules = productions.get(Ai);
                List<List<String>> ajRules = productions.get(Aj);
                List<List<String>> newAiRules = new ArrayList<>();

                boolean indirectFound = false;
                for (List<String> rule : aiRules) {
                    if (!rule.isEmpty() && rule.get(0).equals(Aj)) {
                        indirectFound = true;
                        List<String> gamma = rule.subList(1, rule.size());
                        for (List<String> ajRule : ajRules) {
                            List<String> newRule = new ArrayList<>();
                            if (!(ajRule.size() == 1 && ajRule.get(0).equals("epsilon"))) {
                                newRule.addAll(ajRule);
                            }
                            newRule.addAll(gamma);
                            if (newRule.isEmpty()) {
                                newRule.add("epsilon");
                            }
                            newAiRules.add(newRule);
                        }
                    } else {
                        newAiRules.add(rule);
                    }
                }
                if (indirectFound) {
                    productions.put(Ai, newAiRules);
                }
            }
            eliminateDirectLeftRecursion(Ai);
        }
    }

    private void eliminateDirectLeftRecursion(String A) {
        List<List<String>> rules = productions.get(A);
        List<List<String>> alphas = new ArrayList<>();
        List<List<String>> betas = new ArrayList<>();

        for (List<String> rule : rules) {
            if (!rule.isEmpty() && rule.get(0).equals(A)) {
                alphas.add(new ArrayList<>(rule.subList(1, rule.size())));
            } else {
                betas.add(rule);
            }
        }

        if (alphas.isEmpty()) return; // No direct left recursion

        String A_prime = generateNewNonTerminal(A);
        List<List<String>> newARules = new ArrayList<>();
        List<List<String>> newAPrimeRules = new ArrayList<>();

        if (betas.isEmpty()) {
            newARules.add(new ArrayList<>(Arrays.asList(A_prime)));
        } else {
            for (List<String> beta : betas) {
                List<String> newRule = new ArrayList<>();
                if (!(beta.size() == 1 && beta.get(0).equals("epsilon"))) {
                    newRule.addAll(beta);
                }
                newRule.add(A_prime);
                newARules.add(newRule);
            }
        }

        for (List<String> alpha : alphas) {
            List<String> newRule = new ArrayList<>();
            if (!(alpha.size() == 1 && alpha.get(0).equals("epsilon"))) {
                newRule.addAll(alpha);
            }
            newRule.add(A_prime);
            newAPrimeRules.add(newRule);
        }
        newAPrimeRules.add(new ArrayList<>(Arrays.asList("epsilon")));

        // Preserve order by creating a new map or modifying in place
        // Modifying in place works since LinkedHashMap maintains insertion order for existing keys,
        // and A_prime is appended.
        LinkedHashMap<String, List<List<String>>> updatedProductions = new LinkedHashMap<>();
        for (Map.Entry<String, List<List<String>>> entry : productions.entrySet()) {
            if (entry.getKey().equals(A)) {
                updatedProductions.put(A, newARules);
                updatedProductions.put(A_prime, newAPrimeRules);
            } else {
                updatedProductions.put(entry.getKey(), entry.getValue());
            }
        }
        productions = updatedProductions;
    }

    private String generateNewNonTerminal(String base) {
        String newName = base + "Prime";
        int count = 1;
        while (productions.containsKey(newName)) {
            newName = base + "Prime" + count++;
        }
        return newName;
    }

    public void exportToFile(String filepath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
        for (Map.Entry<String, List<List<String>>> entry : productions.entrySet()) {
            String nt = entry.getKey();
            writer.write(nt + " -> ");
            List<List<String>> rules = entry.getValue();
            for (int i = 0; i < rules.size(); i++) {
                writer.write(String.join(" ", rules.get(i)));
                if (i < rules.size() - 1) {
                    writer.write(" | ");
                }
            }
            writer.newLine();
        }
        writer.close();
    }
    
    // Debug method
    public void printGrammar() {
        for (Map.Entry<String, List<List<String>>> entry : productions.entrySet()) {
            String nt = entry.getKey();
            System.out.print(nt + " -> ");
            List<List<String>> rules = entry.getValue();
            for (int i = 0; i < rules.size(); i++) {
                System.out.print(String.join(" ", rules.get(i)));
                if (i < rules.size() - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }
    }
}
