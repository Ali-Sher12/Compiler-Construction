import java.util.ArrayList;

public class TreeNode {

    public String symbol;
    public ArrayList<TreeNode> children;
    public TreeNode parent;

    public TreeNode(String symbol) {
        this.symbol = symbol;
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public TreeNode(String symbol, TreeNode parent) {
        this.symbol = symbol;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    public void addChild(TreeNode child) {
        child.parent = this;
        children.add(child);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public String toString() {
        return symbol;
    }
}
