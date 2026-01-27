class Parser:
  def __init__(self, tokens):
    self.tokens = tokens #list of tokens
    self.curr = 0

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
