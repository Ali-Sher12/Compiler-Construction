import model
import token

class Parser:
  def __init__(self, tokens):
    self.tokens = tokens #list of tokens
    self.curr = 0

  def advance(self):
    if self.curr >= len(self.tokens):
      return token.TOK_FINISH_NON_FUNCTIONAL
    token_ = self.tokens[self.curr]
    self.curr = self.curr + 1
    return token_

  def peek(self):
    if self.curr >= len(self.tokens):
      return token.TOK_FINISH_NON_FUNCTIONAL
    return self.tokens[self.curr]    

  def is_next(self,expected_type):
    if self.curr >= len(self.tokens):
      return False    
    return self.peek().token_type() == expected_type
    
  def expect(self,expected_type):
    if self.curr >= len(self.tokens):
      raise SyntaxError(f'Found {self.previous_token().lexeme!r} at the end of parsing.')
    elif self.tokens[self.curr].token_type() == expected_type:
      token_ = self.advance()
      return token_
    else:
      raise SyntaxError(f'Expected {expected_type!r}, got {self.tekens[self.curr].lexeme!r}.')

  def previous_token(self):
    if self.curr - 1 < 0:
      return token.TOK_FINISH_NON_FUNCTIONAL    
    return self.tokens[self.curr-1]

  def match(self,expected_type):
    if self.curr >= len(self.tokens) or self.peek().token_type != expected_type:
      return False
    self.curr = self.curr + 1
    return True

  # <primary>  ::=  <integer> | <float> | '(' <expr> ')'
  def primary(self):
    if self.match(token.TOK_INTEGER): return model.Integer(int(self.previous_token().lexeme))
    if self.match(token.TOK_FLOAT): return model.Float(float(self.previous_token().lexeme))
    if self.match(token.TOK_LPAREN):
      expr = self.expr()
      if (not self.match(token.TOK_RPAREN)):
        raise SyntaxError(f'Error: ")" expected.')
      else:
        return model.Grouping(expr)

  # <unary>  ::=  ('+'|'-'|'~') <unary>  |  <primary>
  def unary(self):
    if self.match(token.TOK_NOT) or self.match(token.TOK_MINUS) or self.match(token.TOK_PLUS):
      op = self.previous_token()
      operand = self.unary()
      return model.UnOp(op, operand)
    return self.primary()

  # <factor>  ::=  <unary>
  def factor(self):
    return self.unary()

  # <term>  ::=  <factor> ( ('*'|'/') <factor> )*
  def term(self):
    expr = self.factor()
    while self.match(token.TOK_STAR) or self.match(token.TOK_SLASH):
      op = self.previous_token()
      right = self.factor()
      expr = model.BinOp(op, expr, right)
    return expr

  # <expr>  ::=  <term> ( ('+'|'-') <term> )*
  def expr(self):
    expr = self.term()
    while self.match(token.TOK_PLUS) or self.match(token.TOK_MINUS):
      op = self.previous_token()
      right = self.term()
      expr = model.BinOp(op, expr, right)
    return expr

  def parse(self):
    ast = self.expr()
    return ast
  