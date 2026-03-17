import java.util.ArrayList;

/**
 * ParseTree.java
 * Handles parse tree display in a readable indented format.
 *
 * Example output:
 *   Expr
 *   ├── Term
 *   │   ├── Factor
 *   │   │   └── id
 *   │   └── TermPrime
 *   │       └── epsilon
 *   └── ExprPrime
 *       └── epsilon
 */
public class ParseTree {

    private TreeNode root;

    public ParseTree(TreeNode root) {
        this.root = root;
    }

    // -------------------------------------------------------
    // PUBLIC: Print the full tree from root
    // -------------------------------------------------------
    public void print() {
        System.out.println("\n========== PARSE TREE ==========");
        printNode(root, "", true);
        System.out.println("=================================\n");
    }

    // -------------------------------------------------------
    // PRIVATE: Recursive pretty-print with box-drawing chars
    // prefix  = indentation string built up recursively
    // isLast  = whether this node is the last child of its parent
    // -------------------------------------------------------
    private void printNode(TreeNode node, String prefix, boolean isLast) {
        // Choose connector: last child gets +-- , others get |--
        String connector = isLast ? "+-- " : "|-- ";

        // Print current node (root has no connector)
        if (prefix.isEmpty()) {
            System.out.println(node.symbol);
        } else {
            System.out.println(prefix + connector + node.symbol);
        }

        // Build prefix for children
        // If current node is last, next level uses spaces; otherwise uses |
        String childPrefix = prefix + (isLast ? "    " : "|   ");

        ArrayList<TreeNode> children = node.children;
        for (int i = 0; i < children.size(); i++) {
            boolean lastChild = (i == children.size() - 1);
            printNode(children.get(i), childPrefix, lastChild);
        }
    }

    // -------------------------------------------------------
    // PUBLIC: Print preorder traversal (symbol by symbol)
    // -------------------------------------------------------
    public void printPreorder() {
        System.out.print("Preorder: ");
        preorder(root);
        System.out.println();
    }

    private void preorder(TreeNode node) {
        System.out.print(node.symbol + " ");
        for (TreeNode child : node.children) {
            preorder(child);
        }
    }

    // -------------------------------------------------------
    // PUBLIC: Get root
    // -------------------------------------------------------
    public TreeNode getRoot() {
        return root;
    }
}
