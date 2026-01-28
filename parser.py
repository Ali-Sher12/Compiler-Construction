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

  # integer | float | expression
  def primary(self):
    #TODO:
    pass

  def unary(self):
    #TODO:
    pass

  def factor(self):
    #TODO:
    pass

  def term(self):
    #TODO:
    pass

  def expr(self):
    #TODO:
    pass

  def parse(self):
    ast = self.expr()
    return ast
