import token

class Lexer:
    def __init__(self,source):
        self.source = source # bad way, what if there are millions of lines of code
        self.line = 1
        self.start = 0
        self.curr = 0
        self.tokens = []

    def isKEYWORD(self, lex):
        if lex in token.keywords:
            return token.keywords[lex]
        else:
            return token.TOK_IDENTIFIER

    def is_digit(self,num):
        if num >= '0' and num <= '9':
            return True
        return False

    def advance(self):
        if self.curr >= len(self.source):
          return '\0'
        ch = self.source[self.curr]
        self.curr = self.curr + 1
        return ch

    def match(self, expected):
        if self.curr >= len(self.source):
          return False
        if self.source[self.curr] != expected:
          return False
        self.curr = self.curr + 1 # If it is a match, we also consume that char
        return True

    def add_token(self,input_type):
        tempToken = token.Token(input_type,self.source[self.start:self.curr],self.line)
        self.tokens.append(tempToken)

    def peek(self):
        if(self.curr >= len(self.source)):return '\0'
        return self.source[self.curr]

    def lookahead(self):
        if(self.curr+1 >= len(self.source)):return '\0'
        return self.source[self.curr+1]

    def is_alphanum(self,ch):
        return (('a' <= ch <= 'z') or ('A' <= ch <= 'Z') or ('0' <= ch <= '9'))

    def tokenise(self):
        while self.curr < len(self.source):
            self.start = self.curr
            ch = self.advance()
            if ch == '+':self.add_token(token.TOK_PLUS)
            elif ch == '-': self.add_token(token.TOK_MINUS)
            elif ch == '\n': self.line = self.line + 1
            elif ch == '*': self.add_token(token.TOK_STAR)
            elif ch == ' ': pass
            elif ch == '\t': pass
            elif ch == '\r': pass
            elif ch == '#':
                while self.peek() != '\n':
                    if self.peek() == '\0':
                        return self.tokens
                    self.advance()
                self.advance()

            elif ch == '(': self.add_token(token.TOK_LPAREN)
            elif ch == ')': self.add_token(token.TOK_RPAREN)
            elif ch == '{': self.add_token(token.TOK_LCURLY)
            elif ch == '}': self.add_token(token.TOK_RCURLY)
            elif ch == '[': self.add_token(token.TOK_LSQUAR)
            elif ch == ']': self.add_token(token.TOK_RSQUAR)
            elif ch == ',': self.add_token(token.TOK_COMMA)
            elif ch == '+': self.add_token(token.TOK_PLUS)
            elif ch == '*': self.add_token(token.TOK_STAR)
            elif ch == '^': self.add_token(token.TOK_CARET)
            elif ch == '/': self.add_token(token.TOK_SLASH)
            elif ch == ';': self.add_token(token.TOK_SEMICOLON)
            elif ch == '?': self.add_token(token.TOK_QUESTION)
            elif ch == '%': self.add_token(token.TOK_MOD)
            elif ch == '=': 
                if(self.peek() == "="):
                    self.advance()
                    self.add_token(token.TOK_EQEQ)

            elif ch == '~': 
                if(self.peek() == "="):
                    self.advance()
                    self.add_token(token.TOK_NE)
                else:
                    self.add_token(token.TOK_NOT)

            elif ch == '<': 
                if(self.peek() == "="):
                    self.advance()
                    self.add_token(token.TOK_LE)
                else:
                    self.add_token(token.TOK_LT)

            elif ch == '>': 
                if(self.peek() == "="):
                    self.advance()
                    self.add_token(token.TOK_GE)
                else:
                    self.add_token(token.TOK_GT)

            elif ch == ':': 
                if(self.peek() == "="):
                    self.advance()
                    self.add_token(token.TOK_ASSIGN)
                else:
                    self.add_token(token.TOK_COLON)

            elif ch == '.': 
                if self.is_digit(self.peek()) == False:
                    self.add_token(token.TOK_DOT)                    

                else:
                    while True:
                        if self.is_digit(self.peek()):
                            self.advance()
                        else:
                            self.add_token(token.TOK_FLOAT)
                            break

            elif self.is_digit(ch): 
                while self.is_digit(self.peek()):
                    self.advance()
                if self.peek() == '.' and self.is_digit(self.lookahead()):
                    self.advance() #consumes the '.'
                    while self.is_digit(self.peek()):
                        self.advance()
                    self.add_token(token.TOK_FLOAT)
                else:
                    self.add_token(token.TOK_INTEGER)

            elif ch == "'":
                while self.peek() != "'":
                    if self.peek() == '\0':
                        break
                    self.advance()
                if self.peek() == "'":
                    self.advance()
                    self.add_token(token.TOK_STRING)
                else:
                    print("Syntax error: Unclosed string at line :", self.line)                  

            elif ch == '"':
                while self.peek() != '"':
                    if self.peek() == '\0':
                        break
                    self.advance()
                if self.peek() == '"':
                    self.advance()
                    self.add_token(token.TOK_STRING)
                else:
                    print("Syntax error: Unclosed string at line :", self.line)                  

            elif self.is_alphanum(ch):
                while self.is_alphanum(self.peek()) or self.peek() == '_':
                    if self.peek() == '\0':
                        break
                    self.advance()
                token_lexeme = self.source[self.start:self.curr]
                self.add_token(self.isKEYWORD(token_lexeme))

        return self.tokens
