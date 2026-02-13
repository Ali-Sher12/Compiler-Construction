import java.util.HashMap;

public class TokenType
{
    String NOT_A_TOKEN    = "NOT_A_TOKEN";
    String TOK_LPAREN     = "TOK_LPAREN";     //  (
    String TOK_RPAREN     = "TOK_RPAREN";     //  )
    String TOK_LCURLY     = "TOK_LCURLY";     //  {
    String TOK_RCURLY     = "TOK_RCURLY";     //  }
    String TOK_LSQUAR     = "TOK_LSQUAR";     //  [
    String TOK_RSQUAR     = "TOK_RSQUAR";     //  ]
    String TOK_COMMA      = "TOK_COMMA" ;     //  ,
    String TOK_DOT        = "TOK_DOT"   ;     //  .
    // I'm keeping plus and minus because identifiers can also be added or subtracted.
    String TOK_PLUS       = "TOK_PLUS"  ;     //  +
    String TOK_MINUS      = "TOK_MINUS" ;     //  -
    String TOK_STAR       = "TOK_STAR"  ;     //  *
    String TOK_EXPONENT   = "TOK_EXPONENT";   //  **
    String TOK_DIV        = "TOK_DIV" ;       //  /
    String TOK_MOD        = "TOK_MOD"   ;     //  %
    String TOK_COLON      = "TOK_COLON" ;     //  :
    String TOK_SEMICOLON  = "TOK_SEMICOLON";  //  ;
    String TOK_QUESTION   = "TOK_QUESTION";   //  ?
    String TOK_NOT        = "TOK_NOT";        //  !
    String TOK_GT         = "TOK_GT" ;        //  >
    String TOK_LT         = "TOK_LT" ;        //  <
    String TOK_EQ         = "TOK_EQ" ;        //  =
    String TOK_LOGICALAND = "TOK_LOGICALAND"; //  &
    String TOK_LOGICALOR  = "TOK_LOGICALOR" ; //  |
    String TOK_LOGICALXOR = "TOK_LOGICALXOR"; //  ^
    // Two-char tokens
    String TOK_GE         = "TOK_GE"    ;     //  >=
    String TOK_LE         = "TOK_LE"    ;     //  <=
    String TOK_NE         = "TOK_NE"    ;     //  !=
    String TOK_EQEQ       = "TOK_EQEQ"  ;     //  ==
    String TOK_AND        = "TOK_ASSIGN";     //  &&
    String TOK_OR         = "TOK_ASSIGN";     //  ||
    String TOK_INC        = "TOK_ASSIGN";     //  ++
    String TOK_DEC        = "TOK_ASSIGN";     //  --
    String TOK_PLUSASSIGN   = "TOK_ASSIGN";     //  +=
    String TOK_MINUSASSIGN  = "TOK_ASSIGN";     //  -=
    String TOK_MULASSIGN  = "TOK_ASSIGN";     //  *=
    String TOK_DIVASSIGN  = "TOK_ASSIGN";     //  /=
    // Literals
    String TOK_IDENTIFIER = "TOK_IDENTIFIER";
    String TOK_STRING     = "TOK_STRING";
    String TOK_CHAR     = "TOK_CHAR";
    String TOK_INTEGER    = "TOK_INTEGER";
    String TOK_FLOAT      = "TOK_FLOAT";
    // Keywords
    String TOK_CONDITION         = "TOK_CONDITION";
    String TOK_START       = "TOK_START";
    String TOK_FINISH       = "TOK_FINISH";
    String TOK_TRUE       = "TOK_TRUE";
    String TOK_FALSE      = "TOK_FALSE";
    String TOK_LOOP        = "TOK_LOOP";
    String TOK_DECLARE         = "TOK_DECLARE";
    String TOK_OUTPUT      = "TOK_OUTPUT";
    String TOK_INPUT         = "TOK_INPUT";
    String TOK_FUNCTION        = "TOK_FUNCTION";
    String TOK_RETURN       = "TOK_RETURN";
    String TOK_BREAK       = "TOK_BREAK";
    String TOK_CONTINUE        = "TOK_CONTINUE";

    /////////////////////////////////////////
    HashMap<String, String> token_map;

    public TokenType()
    {
        token_map = new HashMap<String,String>();
        token_map.put("condition"   ,TOK_CONDITION);
        token_map.put("start"  ,TOK_START);
        token_map.put("finish"   ,TOK_FINISH);
        token_map.put("true"   ,TOK_TRUE);
        token_map.put("false"  ,TOK_FALSE);
        token_map.put("loop"   ,TOK_LOOP);
        token_map.put("declare"  ,TOK_DECLARE);
        token_map.put("output"   ,TOK_OUTPUT);
        token_map.put("input"  ,TOK_INPUT);
        token_map.put("function"   ,TOK_FUNCTION);
        token_map.put("return"  ,TOK_RETURN);
        token_map.put("break"   ,TOK_BREAK);
        token_map.put("continue"  ,TOK_CONTINUE);
    }

}
