import java.util.ArrayList;
import java.util.List;
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
            throw new WrongSyntaxException("no tokens found");
        result = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.END_BLOCK, ";;"));
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
                    return parseFunction(tokens);
                case APPLY:
                    return parseApply(tokens);
                case LIST_S:
                    return parseList(tokens);
                case LIST_OP:
                    return parseListOp(tokens);
                case OP:
                    return parseBinaryOp(tokens);
                case NOT:
                    return parseNot(tokens);
                case IDEN:
                    return parseIden(tokens.remove());
                default:
                    throw new WrongSyntaxException("unexpected token " + nextToken.value);
            }
        } else
            throw new WrongSyntaxException("expected expression but found none");
    }

    private static Int parseInt(Token token) {
        Int i = new Int(Integer.parseInt(token.value));
        return i;
    }

    private static Bool parseBool(Token token) {
        Bool b = new Bool(Boolean.parseBoolean(token.value));
        return b;
    }

    private static Expression parseRec(Queue<Token> tokens) throws WrongSyntaxException {
        Letrec rec = new Letrec();
        parseToken(tokens, new Token(TokenType.REC, "letrec"));
        rec.name = parseExpression(tokens);
        rec.params = parseListOfParams(tokens, new Token(TokenType.EQ, "="));
        parseToken(tokens, new Token(TokenType.EQ, "="));
        rec.fbody = parseExpression(tokens);
        if (tokens.peek().type != TokenType.END_BLOCK) {
            parseToken(tokens, new Token(TokenType.IN, "in"));
            rec.letbody = parseExpression(tokens);
        }
        return rec;
    }

    private static Expression parseLet(Queue<Token> tokens) throws WrongSyntaxException {
        Let let = new Let();
        parseToken(tokens, new Token(TokenType.LET, "let"));
        let.var = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.EQ, "="));
        let.value = parseExpression(tokens);
        if (tokens.peek().type != TokenType.END_BLOCK) {
            parseToken(tokens, new Token(TokenType.IN, "in"));
            let.body = parseExpression(tokens);
        }
        return let;
    }

    private static Expression parseIf(Queue<Token> tokens) throws WrongSyntaxException {
        Ifthenelse ifte = new Ifthenelse();
        parseToken(tokens, new Token(TokenType.IF, "if"));
        ifte.guard = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.THEN, "then"));
        ifte.then = parseExpression(tokens);
        parseToken(tokens, new Token(TokenType.ELSE, "else"));
        ifte.els = parseExpression(tokens);
        return ifte;
    }

    private static Expression parseFunction(Queue<Token> tokens) throws WrongSyntaxException {
        Function fun = new Function();
        parseToken(tokens, new Token(TokenType.FUN, "function"));
        fun.formalParams = parseListOfParams(tokens, new Token(TokenType.ARROW, "->"));
        parseToken(tokens, new Token(TokenType.ARROW, "->"));
        fun.body = parseExpression(tokens);
        return fun;
    }

    private static Expression parseApply(Queue<Token> tokens) throws WrongSyntaxException {
        Apply app = new Apply();
        parseToken(tokens, new Token(TokenType.APPLY, "apply"));
        app.iden = parseExpression(tokens);
        app.actualParams = parseListOfParams(tokens, new Token(TokenType.END_PARAM, ";"));
        parseToken(tokens, new Token(TokenType.END_PARAM, ";"));
        return app;
    }

    private static Lis parseList(Queue<Token> tokens) throws WrongSyntaxException {
        Lis l = new Lis();
        parseToken(tokens, new Token(TokenType.LIST_S, "["));
        while (tokens.peek().type != TokenType.LIST_E)
            l.lis.add(parseExpression(tokens));
        parseToken(tokens, new Token(TokenType.LIST_E, "]"));
        return l;
    }

    private static Expression parseListOp(Queue<Token> tokens) throws WrongSyntaxException {
        String[] s = tokens.remove().value.split("\\.");
        ListOp lop = new ListOp();
        lop.operation = s[1];
        switch (lop.operation) {
            case "hd":
            case "tl":
            case "isEmpty":
            case "length":
                lop.list = parseExpression(tokens);
                return lop;
            case "cons":
            case "append":
            case "map":
            case "filter":
            case "exists":
            case "forAll":
                lop.arg = parseExpression(tokens);
                lop.list = parseExpression(tokens);
                return lop;
            default:
                throw new WrongSyntaxException("invalid list operation " + s[0] + "." + lop.operation);
        }
    }

    private static Expression parseBinaryOp(Queue<Token> tokens) throws WrongSyntaxException {
        Operation bop = new Operation();
        parseToken(tokens, new Token(TokenType.OP, "op"));
        bop.e1 = parseExpression(tokens);
        bop.op = parseSymbol(tokens);
        bop.e2 = parseExpression(tokens);
        return bop;
    }

    private static Expression parseNot(Queue<Token> tokens) throws WrongSyntaxException {
        Not not = new Not();
        parseToken(tokens, new Token(TokenType.NOT, "!"));
        not.arg = parseExpression(tokens);
        return not;
    }

    private static Iden parseIden(Token token) {
        Iden id = new Iden(token.value);
        return id;
    }

    private static void parseToken(Queue<Token> tokens, Token expected) throws WrongSyntaxException {
        nextToken = tokens.peek();
        if (nextToken == null)
            throw new WrongSyntaxException("expected " + expected.value + " but found none");
        if (nextToken.type != expected.type)
            throw new WrongSyntaxException("expected " + expected.value + " but found " + nextToken.value);
        tokens.remove();
    }

    private static List<Expression> parseListOfParams(Queue<Token> tokens, Token stopAt)
            throws WrongSyntaxException {
        List<Expression> params = new ArrayList<Expression>();
        while (tokens.peek().type != stopAt.type) {
            if (params.size() >= 16)
                throw new WrongSyntaxException("too many parameters passed to function");
            else {
                try {
                    params.add(parseExpression(tokens));
                } catch (WrongSyntaxException e) {
                    throw new WrongSyntaxException("expected " + stopAt.value + " to delimit list of parameters");
                }
            }
        }
        if (params.size() == 0)
            throw new WrongSyntaxException("no parameters passed to function");
        return params;
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
                case ">=":
                case "<=":
                case "%":
                case "^":
                case "==":
                case "!=":
                    Symbol s = new Symbol(nextToken.value);
                    return s;
                default:
                    throw new WrongSyntaxException("unexpected operation symbol " + nextToken.value);
            }
        } else
            throw new WrongSyntaxException("expected operation symbol but found none");
    }
}