import sys
import token
import lexer
import parser

def print_pretty_ast(ast_text):
  i = 0
  newline = False
  for ch in str(ast_text):
    if ch == '(':
      if not newline:
        print(end='')
      print(ch)
      i += 2
      newline = True
    elif ch == ')':
      if not newline:
        print()
      i -= 2
      newline = True
      print(' '*i + ch)
    else:
      if newline:
        print(' '*i, end='')
      print(ch, end='')
      newline = False

if __name__ == "__main__":
    if(len(sys.argv)) != 2:
        raise SystemExit('Usage: python pinky.py <.pinky filename>')
    filename = sys.argv[1]
    with open(filename) as file:
        source = file.read()
        tokens = lexer.Lexer(source).tokenise()
        for i in tokens : print(i)
        ast = parser.Parser(tokens).parse()
        print(print_pretty_ast(ast))
