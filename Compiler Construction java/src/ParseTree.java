import java.util.ArrayList;

public class ParseTree {
    private TreeNode root;
    public ParseTree(TreeNode root) {
        this.root = root;
    }
    public void print() {
        System.out.println("\n========== PARSE TREE ==========");
        printNode(root, "", true);
        System.out.println("=================================\n");
    }
    private void printNode(TreeNode node, String prefix, boolean isLast) {
        String connector = isLast ? "+-- " : "|-- ";
        if (prefix.isEmpty()) {
            System.out.println(node.symbol);
        }
        else {
            System.out.println(prefix + connector + node.symbol);
        }
        String childPrefix = prefix + (isLast ? "    " : "|   ");

        ArrayList<TreeNode> children = node.children;
        for (int i = 0; i < children.size(); i++) {
            boolean lastChild = (i == children.size() - 1);
            printNode(children.get(i), childPrefix, lastChild);
        }
    }
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

    public TreeNode getRoot() {
        return root;
    }
}
