import java.util.*;

public class FirstFollow {

    Grammar grammar;
    Set<String> nonTerminals;  // keys of productions map
    Set<String> terminals;     // derived: symbols that are not NTs and not epsilon
    LinkedHashMap<String, LinkedHashSet<String>> first;
    LinkedHashMap<String, LinkedHashSet<String>> follow;

    FirstFollow(Grammar grammar) {
        this.grammar = grammar;
        this.nonTerminals = grammar.getProductions().keySet();
        this.terminals = new LinkedHashSet<>();
        this.first = new LinkedHashMap<>();
        this.follow = new LinkedHashMap<>();

        // Getting terminals
        for (List<List<String>> alts : grammar.getProductions().values()) {
            for (List<String> alt : alts) {
                for (String sym : alt) {
                    if (!isNonTerminal(sym) && !sym.equals("epsilon")) {
                        terminals.add(sym);
                    }
                }
            }
        }
        for (String nt : nonTerminals) {
            first.put(nt,  new LinkedHashSet<>());
            follow.put(nt, new LinkedHashSet<>());
        }
    }

    void compute() {
        computeFirst();
        computeFollow();
    }

    private void computeFirst() {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nt : nonTerminals) {
                for (List<String> production : grammar.getProductions().get(nt)) {
                    changed |= addFirstOfString(production, first.get(nt));
                }
            }
        }
    }

    private boolean addFirstOfString(List<String> symbols, LinkedHashSet<String> targetSet) {
        boolean changed = false;
        boolean allDeriveEpsilon = true;

        for (String sym : symbols) {
            if (sym.equals("epsilon")) {
                changed |= targetSet.add("epsilon");
                allDeriveEpsilon = true;
                break;
            }

            if (isTerminal(sym)) {
                changed |= targetSet.add(sym);
                allDeriveEpsilon = false;
                break;
            }

            LinkedHashSet<String> firstOfSym = first.get(sym);
            if (firstOfSym != null) {
                for (String s : firstOfSym) {
                    if (!s.equals("epsilon")) changed |= targetSet.add(s);
                }
                if (!firstOfSym.contains("epsilon")) {
                    allDeriveEpsilon = false;
                    break;
                }
            } else {
                allDeriveEpsilon = false;
                break;
            }
        }

        if (allDeriveEpsilon && !symbols.isEmpty()) {
            changed |= targetSet.add("epsilon");
        }

        return changed;
    }

    private void computeFollow() {
        follow.get(grammar.getStartSymbol()).add("$");

        boolean changed = true;
        while (changed) {
            changed = false;

            for (String nt : nonTerminals) {
                for (List<String> production : grammar.getProductions().get(nt)) {
                    for (int i = 0; i < production.size(); i++) {
                        String sym = production.get(i);
                        if (!isNonTerminal(sym)) continue;

                        LinkedHashSet<String> followOfSym = follow.get(sym);
                        List<String> beta = production.subList(i + 1, production.size());

                        // Rule 2: add FIRST(beta) - {epsilon} to FOLLOW(sym)
                        LinkedHashSet<String> firstOfBeta = new LinkedHashSet<>();
                        addFirstOfString(new ArrayList<>(beta), firstOfBeta);

                        for (String s : firstOfBeta) {
                            if (!s.equals("epsilon")) changed |= followOfSym.add(s);
                        }

                        // Rule 3: if beta empty or epsilon in FIRST(beta), add FOLLOW(nt)
                        if (beta.isEmpty() || firstOfBeta.contains("epsilon")) {
                            for (String s : follow.get(nt)) {
                                changed |= followOfSym.add(s);
                            }
                        }
                    }
                }
            }
        }
    }
    LinkedHashSet<String> firstOfString(List<String> symbols) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        addFirstOfString(symbols, result);
        return result;
    }
    boolean isNonTerminal(String s) {
        return nonTerminals.contains(s);
    }
    boolean isTerminal(String s) {
        return terminals.contains(s);
    }

    LinkedHashMap<String, LinkedHashSet<String>> getFirst() { return first; }
    LinkedHashMap<String, LinkedHashSet<String>> getFollow() { return follow; }
    LinkedHashSet<String> getFirst(String nt) {
        return first.getOrDefault(nt,  new LinkedHashSet<>());
    }
    LinkedHashSet<String> getFollow(String nt) {
        return follow.getOrDefault(nt, new LinkedHashSet<>());
    }
    Set<String> getTerminals() { return terminals; }
    Set<String> getNonTerminals() { return nonTerminals; }

    void printSets() {
        System.out.println("First:");
        for (String nt : nonTerminals) {
            System.out.printf("  FIRST(%-15s) = { %s }%n", nt, String.join(", ", first.get(nt)));
        }
        System.out.println("Follow:");
        for (String nt : nonTerminals) {
            System.out.printf("  FOLLOW(%-15s) = { %s }%n", nt, String.join(", ", follow.get(nt)));
        }
    }
}
