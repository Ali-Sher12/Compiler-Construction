import sys
import token
import lexer
import parser

if __name__ == "__main__":
    if(len(sys.argv)) != 2:
        raise SystemExit('Usage: python pinky.py <.pinky filename>')
    filename = sys.argv[1]
    with open(filename) as file:
        source = file.read()
        tokens = lexer.Lexer(source).tokenise()
        for i in tokens : print(i)
        ast = parser.Parser(tokens).parse()
#        print(ast)
