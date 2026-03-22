import java.util.*;

/**
 * FirstFollow.java
 * Computes FIRST and FOLLOW sets using Grammar.java directly.
 * No GrammarReader needed.
 */
public class FirstFollow {

    private Grammar grammar;
    private Set<String> nonTerminals;  // keys of productions map
    private Set<String> terminals;     // derived: symbols that are not NTs and not epsilon

    private LinkedHashMap<String, LinkedHashSet<String>> first;
    private LinkedHashMap<String, LinkedHashSet<String>> follow;

    public FirstFollow(Grammar grammar) {
        this.grammar      = grammar;
        this.nonTerminals = grammar.getProductions().keySet();
        this.terminals    = new LinkedHashSet<>();
        this.first        = new LinkedHashMap<>();
        this.follow       = new LinkedHashMap<>();

        // Derive terminals: any symbol that is not a non-terminal and not epsilon
        for (List<List<String>> alts : grammar.getProductions().values()) {
            for (List<String> alt : alts) {
                for (String sym : alt) {
                    if (!isNonTerminal(sym) && !sym.equals("epsilon")) {
                        terminals.add(sym);
                    }
                }
            }
        }

        // Initialize empty FIRST and FOLLOW sets for every non-terminal
        for (String nt : nonTerminals) {
            first.put(nt,  new LinkedHashSet<>());
            follow.put(nt, new LinkedHashSet<>());
        }
    }

    // ── PUBLIC: Compute both sets ────────────────────────────
    public void compute() {
        computeFirst();
        computeFollow();
    }

    // ── FIRST SET COMPUTATION (fixed-point iteration) ────────
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

    /**
     * Adds FIRST of a symbol sequence to targetSet.
     * Returns true if targetSet changed.
     *
     * For Y1 Y2 ... Yn:
     *   - Add FIRST(Yi) - {epsilon}, stop if Yi can't derive epsilon
     *   - If all Yi derive epsilon, add epsilon
     */
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

            // Non-terminal: add FIRST(sym) - {epsilon}
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

    // ── FOLLOW SET COMPUTATION (fixed-point iteration) ───────
    private void computeFollow() {
        // Rule 1: add $ to FOLLOW(start symbol)
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

    // ── PUBLIC: FIRST of an arbitrary string ─────────────────
    public LinkedHashSet<String> firstOfString(List<String> symbols) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        addFirstOfString(symbols, result);
        return result;
    }

    // ── Type helpers (derived from Grammar, no isNT method needed) ──
    public boolean isNonTerminal(String s) { return nonTerminals.contains(s); }
    public boolean isTerminal(String s)    { return terminals.contains(s); }

    // ── Getters ──────────────────────────────────────────────
    public LinkedHashMap<String, LinkedHashSet<String>> getFirst()       { return first; }
    public LinkedHashMap<String, LinkedHashSet<String>> getFollow()      { return follow; }
    public LinkedHashSet<String>                        getFirst(String nt)  { return first.getOrDefault(nt,  new LinkedHashSet<>()); }
    public LinkedHashSet<String>                        getFollow(String nt) { return follow.getOrDefault(nt, new LinkedHashSet<>()); }
    public Set<String>                                  getTerminals()    { return terminals; }
    public Set<String>                                  getNonTerminals() { return nonTerminals; }

    // ── Print sets ───────────────────────────────────────────
    public void printSets() {
        System.out.println("===== FIRST SETS =====");
        for (String nt : nonTerminals) {
            System.out.printf("  FIRST(%-15s) = { %s }%n", nt, String.join(", ", first.get(nt)));
        }
        System.out.println();
        System.out.println("===== FOLLOW SETS =====");
        for (String nt : nonTerminals) {
            System.out.printf("  FOLLOW(%-15s) = { %s }%n", nt, String.join(", ", follow.get(nt)));
        }
        System.out.println();
    }
}
