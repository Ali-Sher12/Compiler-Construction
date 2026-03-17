import java.util.ArrayList;

/**
 * TreeNode.java
 * Represents a single node in the parse tree.
 * - Internal nodes = Non-terminals
 * - Leaf nodes     = Terminals (or epsilon)
 */
public class TreeNode {

    public String            symbol;    // grammar symbol at this node
    public ArrayList<TreeNode> children; // ordered list of children (left to right)
    public TreeNode          parent;    // parent reference (useful during construction)

    public TreeNode(String symbol) {
        this.symbol   = symbol;
        this.children = new ArrayList<>();
        this.parent   = null;
    }

    public TreeNode(String symbol, TreeNode parent) {
        this.symbol   = symbol;
        this.children = new ArrayList<>();
        this.parent   = parent;
    }

    // Add a child to this node
    public void addChild(TreeNode child) {
        child.parent = this;
        children.add(child);
    }

    // Leaf = no children
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public String toString() {
        return symbol;
    }
}
