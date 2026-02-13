import java.util.HashMap;

public class SymbolTable {
    HashMap<String, Integer> symbol_map;

    SymbolTable(){
        symbol_map = new HashMap<String,Integer>();

    }
    void add(String str){
        if(symbol_map.containsKey(str)){
            symbol_map.put(str,symbol_map.get(str)+1);
        }
        else symbol_map.put(str,1);
    }
}
