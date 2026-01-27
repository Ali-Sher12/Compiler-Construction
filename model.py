import token

class Expr:
  '''
  Expressions evaluate to a result, like x + (3 * y) >= 6
  '''
  pass


class Stmt:
  '''
  Statements perform an action
  '''
  pass


class Integer(Expr):
  def __init__(self, value):
    assert isinstance(value, int), value
    self.value = value
  def __repr__(self):
    return f'Integer[{self.value}]'


class Float(Expr):
  def __init__(self, value):
    assert isinstance(value, float), value
    self.value = value
  def __repr__(self):
    return f'Float[{self.value}]'


class UnOp(Expr):
  def __init__(self, op: token.Token, operand: Expr):
    assert isinstance(op, token.Token), op
    assert isinstance(operand, Expr), operand
    self.op = op
    self.operand = operand
  def __repr__(self):
    return f'UnOp({self.op.lexeme!r}, {self.operand})'


class BinOp(Expr):
  def __init__(self, op: token.Token, left: Expr, right: Expr):
    assert isinstance(op, token.Token), op
    assert isinstance(left, Expr), left
    assert isinstance(right, Expr), right
    self.op = op
    self.left = left
    self.right = right
  def __repr__(self):
    return f'BinOp({self.op.lexeme!r}, {self.left}, {self.right})'


class Grouping(Expr):
  def __init__(self, value):
    assert isinstance(value, Expr), value
    self.value = value
  def __repr__(self):
    return f'Grouping({self.value})'


class WhileStmt(Stmt):
  def __init__(self):
    pass
  def __repr__(self):
    pass

class Assignment(Stmt):
  def __init__(self):
    pass
  def __repr__(self):
    pass
