import java.io.*;
import java.util.*;
import java.nio.file.*;

public class TransitionTable {
    // 57rows and 15 cols
    ArrayList<String> ColumnIdentifiers;
    ArrayList<ArrayList<Integer>> TransitionTable_;
    TransitionTable()
    {
        TransitionTable_ = new ArrayList<ArrayList<Integer>>();
        ColumnIdentifiers = new ArrayList<String>();
        ColumnIdentifiers.add("[0-9]");
        ColumnIdentifiers.add("[Z-Z]");
        ColumnIdentifiers.add("[a-z]");
        ColumnIdentifiers.add("e");
        ColumnIdentifiers.add("E");
        ColumnIdentifiers.add("+");
        ColumnIdentifiers.add("-");
        ColumnIdentifiers.add(".");
        ColumnIdentifiers.add("_");
        ColumnIdentifiers.add("#");
        ColumnIdentifiers.add("&");
        ColumnIdentifiers.add("|");
        ColumnIdentifiers.add("!");
        ColumnIdentifiers.add("True");
        ColumnIdentifiers.add("False");

        // will set it when I make LL1 parser
    }
}
