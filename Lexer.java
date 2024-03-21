import java.util.LinkedList;
import java.util.Queue;
import exceptions.IllegalTokenException;
import token.Token;
import token.TokenType;

public class Lexer {
    private static Queue<Token> tokens = new LinkedList<Token>();

    public static Queue<Token> tokenize(String input) throws IllegalTokenException {
        String[] words = input.split("\\s+|\\(|\\)|\\{|\\}|,");
        for (String word : words) {
            if (word.isBlank())
                continue;
            else if (word.matches("-[0-9]+|[0-9]+"))
                tokens.add(new Token(TokenType.INT, word));
            else if (word.matches("true|false"))
                tokens.add(new Token(TokenType.BOOL, word));
            else if (word.contentEquals("letrec"))
                tokens.add(new Token(TokenType.REC, word));
            else if (word.contentEquals("let"))
                tokens.add(new Token(TokenType.LET, word));
            else if (word.contentEquals("="))
                tokens.add(new Token(TokenType.EQ, word));
            else if (word.contentEquals("in"))
                tokens.add(new Token(TokenType.IN, word));
            else if (word.contentEquals("if"))
                tokens.add(new Token(TokenType.IF, word));
            else if (word.contentEquals("then"))
                tokens.add(new Token(TokenType.THEN, word));
            else if (word.contentEquals("else"))
                tokens.add(new Token(TokenType.ELSE, word));
            else if (word.contentEquals("function"))
                tokens.add(new Token(TokenType.FUN, word));
            else if (word.contentEquals("->"))
                tokens.add(new Token(TokenType.ARROW, word));
            else if (word.contentEquals("apply"))
                tokens.add(new Token(TokenType.APPLY, word));
            else if (word.contentEquals(";"))
                tokens.add(new Token(TokenType.END_PARAM, word));
            else if (word.contentEquals(";;"))
                tokens.add(new Token(TokenType.END_BLOCK, word));
            else if (word.matches("\\["))
                tokens.add(new Token(TokenType.LIST_S, word));
            else if (word.matches("\\]"))
                tokens.add(new Token(TokenType.LIST_E, word));
            else if (word.matches("List.[a-z][a-zA-Z]*"))
                tokens.add(new Token(TokenType.LIST_OP, word));
            else if (word.contentEquals("op"))
                tokens.add(new Token(TokenType.OP, word));
            else if (word.contentEquals("!"))
                tokens.add(new Token(TokenType.NOT, word));
            else if (word.matches("\\+|-|\\*|/|&|\\||>|<|>=|<=|%|\\^|==|!="))
                tokens.add(new Token(TokenType.SYMB, word));
            else if (word.matches("[a-z]\\w*"))
                tokens.add(new Token(TokenType.IDEN, word));
            else
                throw new IllegalTokenException(
                        "illegal token " + word + " detected. Try spacing between characters.");
        }
        return tokens;
    }
}