import java.util.Queue;
import constructs.*;
import exceptions.WrongSyntaxException;
import token.Token;
import token.TokenType;
import types.*;

public class Parser {
    private static Expression result;
    private static Token nextToken;

    public static Expression parse(Queue<Token> tokens) throws WrongSyntaxException {
        if ((nextToken = tokens.peek()) == null)
            throw new WrongSyntaxException("no tokens were found");
        result = parseExpression(tokens);
        if (tokens.size() > 0) {
            String s = new String();
            for (Token t : tokens)
                s = s + t.value + " ";
            throw new WrongSyntaxException("unexpected tokens out of scope: " + s);
        }
        return result;
    }

    private static Expression parseExpression(Queue<Token> tokens) throws WrongSyntaxException {
        nextToken = tokens.peek();
        if (nextToken != null) {
            switch (nextToken.type) {
                case INT:
                    return parseInt(tokens.remove());
                case BOOL:
                    return parseBool(tokens.remove());
                case REC:
                    return parseRec(tokens);
                case LET:
                    return parseLet(tokens);
                case IF:
                    return parseIf(tokens);
                case FUN:
                    return parseFun(tokens);
                case APPLY:
                    return parseApply(tokens);
                case OP:
                    return parseBinaryOP(tokens);
                case NOT:
                    return parseNot(tokens);
                case IDEN:
                    return parseIden(tokens.remove());
                default:
                    throw new WrongSyntaxException("unexpected token passed as argument");
            }
        } else
            throw new WrongSyntaxException("expected argument but none was found");
    }

    private static Int parseInt(Token token) {
        Int i = new Int();
        i.value = (Integer.parseInt(token.value));
        return i;
    }

    private static Bool parseBool(Token token) {
        Bool b = new Bool();
        b.value = (Boolean.parseBoolean(token.value));
        return b;
    }

    private static Expression parseRec(Queue<Token> tokens) throws WrongSyntaxException {
        Letrec rec = new Letrec();
        parseToken(tokens, new Token(TokenType.REC, "Letrec"));
        Expression name = parseExpression(tokens);
        Expression param = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.EQ, "="));
        Expression fbody = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.IN, "in"));
        Expression letbody = parseExpression(tokens);
        rec.setName(name);
        rec.set(param, fbody, letbody);
        return rec;
    }

    private static Expression parseLet(Queue<Token> tokens) throws WrongSyntaxException {
        Let let = new Let();
        parseToken(tokens, new Token(TokenType.LET, "Let"));
        Expression ide = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.EQ, "="));
        Expression value = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.IN, "in"));
        Expression body = parseExpression(tokens);
        let.set(ide, value, body);
        return let;
    }

    private static Expression parseIf(Queue<Token> tokens) throws WrongSyntaxException {
        Ifthenelse ifte = new Ifthenelse();
        parseToken(tokens, new Token(TokenType.IF, "If"));
        Expression guard = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.THEN, "then"));
        Expression then = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.ELSE, "else"));
        Expression els = parseExpression(tokens);
        ifte.set(guard, then, els);
        return ifte;
    }

    private static Expression parseFun(Queue<Token> tokens) throws WrongSyntaxException {
        Function fun = new Function();
        parseToken(tokens, new Token(TokenType.FUN, "Fun"));
        Expression param = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.ARROW, "->"));
        Expression body = parseExpression(tokens);
        fun.set(param, body, null);
        return fun;
    }

    private static Expression parseApply(Queue<Token> tokens) throws WrongSyntaxException {
        Apply app = new Apply();
        parseToken(tokens, new Token(TokenType.APPLY, "Apply"));
        Expression iden = parseExpression(tokens);
        Expression param = parseExpression(tokens);
        app.set(iden, param, null);
        return app;
    }

    private static Expression parseBinaryOP(Queue<Token> tokens) throws WrongSyntaxException {
        Operation bop = new Operation();
        parseToken(tokens, new Token(TokenType.OP, "Op"));
        Expression e1 = parseExpression(tokens);
        Expression op = parseSymbol(tokens);
        Expression e2 = parseExpression(tokens);
        bop.set(e1, op, e2);
        return bop;
    }

    private static Expression parseNot(Queue<Token> tokens) throws WrongSyntaxException {
        Not not = new Not();
        parseToken(tokens, new Token(TokenType.NOT, "Not"));
        Expression arg = parseExpression(tokens);
        not.set(arg, null, null);
        return not;
    }

    private static Iden parseIden(Token token) {
        Iden id = new Iden();
        id.value = nextToken.value;
        return id;
    }

    private static void parseToken(Queue<Token> tokens, Token expected) throws WrongSyntaxException {
        if ((nextToken = tokens.peek()) == null || nextToken.type != expected.type)
            throw new WrongSyntaxException("expected '" + expected.value + "'");
        else
            tokens.remove();
    }

    private static Symbol parseSymbol(Queue<Token> tokens) throws WrongSyntaxException {
        nextToken = tokens.peek();
        if (nextToken != null) {
            tokens.remove();
            switch (nextToken.value) {
                case "+":
                case "-":
                case "*":
                case "/":
                case "&":
                case "|":
                case ">":
                case "<":
                case "==":
                    Symbol s = new Symbol();
                    s.value = nextToken.value;
                    return s;
                default:
                    throw new WrongSyntaxException("expected operation symbol");
            }
        } else
            throw new WrongSyntaxException("expected operation symbol but none was found");
    }
}