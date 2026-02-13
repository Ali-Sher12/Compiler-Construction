import java.util.HashMap;

public class TokenType
{
    String TOK_LPAREN     = "TOK_LPAREN";     //  (
    String TOK_RPAREN     = "TOK_RPAREN";     //  )
    String TOK_LCURLY     = "TOK_LCURLY";     //  {
    String TOK_RCURLY     = "TOK_RCURLY";     //  }
    String TOK_LSQUAR     = "TOK_LSQUAR";     //  [
    String TOK_RSQUAR     = "TOK_RSQUAR";     //  ]
    String TOK_COMMA      = "TOK_COMMA" ;     //  ,
    String TOK_DOT        = "TOK_DOT"   ;     //  .
    String TOK_PLUS       = "TOK_PLUS"  ;     //  +
    String TOK_MINUS      = "TOK_MINUS" ;     //  -
    String TOK_STAR       = "TOK_STAR"  ;     //  *
    String TOK_SLASH      = "TOK_SLASH" ;     //  /
    String TOK_CARET      = "TOK_CARET" ;     //  ^
    String TOK_MOD        = "TOK_MOD"   ;     //  %
    String TOK_COLON      = "TOK_COLON" ;     //  :
    String TOK_SEMICOLON  = "TOK_SEMICOLON";  //  ;
    String TOK_QUESTION   = "TOK_QUESTION";   //  ?
    String TOK_NOT        = "TOK_NOT";        //  ~
    String TOK_GT         = "TOK_GT" ;        //  >
    String TOK_LT         = "TOK_LT" ;        //  <
    String TOK_EQ         = "TOK_EQ" ;        //  =
    // Two-char tokens
    String TOK_GE         = "TOK_GE"    ;     //  >=
    String TOK_LE         = "TOK_LE"    ;     //  <=
    String TOK_NE         = "TOK_NE"    ;     //  ~=
    String TOK_EQEQ       = "TOK_EQEQ"  ;     //  ==
    String TOK_ASSIGN     = "TOK_ASSIGN";     //  :=
    String TOK_GTGT       = "TOK_GTGT"  ;     //  >>
    String TOK_LTLT       = "TOK_LTLT"  ;     //  <<
    // Literals
    String TOK_IDENTIFIER = "TOK_IDENTIFIER";
    String TOK_STRING     = "TOK_STRING";
    String TOK_INTEGER    = "TOK_INTEGER";
    String TOK_FLOAT      = "TOK_FLOAT";
    // Keywords
    String TOK_IF         = "TOK_IF";
    String TOK_THEN       = "TOK_THEN";
    String TOK_ELSE       = "TOK_ELSE";
    String TOK_TRUE       = "TOK_TRUE";
    String TOK_FALSE      = "TOK_FALSE";
    String TOK_AND        = "TOK_AND";
    String TOK_OR         = "TOK_OR";
    String TOK_WHILE      = "TOK_WHILE";
    String TOK_DO         = "TOK_DO";
    String TOK_FOR        = "TOK_FOR";
    String TOK_FUNC       = "TOK_FUNC";
    String TOK_NULL       = "TOK_NULL";
    String TOK_END        = "TOK_END";
    String TOK_PRINT      = "TOK_PRINT";
    String TOK_PRINTLN    = "TOK_PRINTLN";
    String TOK_RET        = "TOK_RET";

    /////////////////////////////////////////
    HashMap<String, String> token_map;

    public TokenType()
    {
        token_map = new HashMap<String,String>();
        token_map.put("if"     ,TOK_IF);
        token_map.put("else"   ,TOK_ELSE);
        token_map.put("then"   ,TOK_THEN);
        token_map.put("true"   ,TOK_TRUE);
        token_map.put("false"  ,TOK_FALSE);
        token_map.put("and"    ,TOK_AND);
        token_map.put("or"     ,TOK_OR);
        token_map.put("while"  ,TOK_WHILE);
        token_map.put("do"     ,TOK_DO);
        token_map.put("for"    ,TOK_FOR);
        token_map.put("func"   ,TOK_FUNC);
        token_map.put("null"   ,TOK_NULL);
        token_map.put("end"    ,TOK_END);
        token_map.put("print"  ,TOK_PRINT);
        token_map.put("println",TOK_PRINTLN);
        token_map.put("ret"    ,TOK_RET);
        System.out.println(token_map.get("apple")); // 5
    }

}
