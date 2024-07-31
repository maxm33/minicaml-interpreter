import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import constructs.*;
import exceptions.WrongSyntaxException;
import token.*;
import values.*;

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
                    return parseInt(tokens);
                case BOOL:
                    return parseBool(tokens);
                case LET:
                    return parseLet(tokens);
                case IF:
                    return parseIf(tokens);
                case FUN:
                    return parseFunction(tokens);
                case LPAR:
                    return parseParenthesis(tokens);
                case LIST_S:
                    return parseList(tokens);
                case LIST_OP:
                    return parseListOperation(tokens);
                case NOT:
                    return parseUnaryOperation(tokens);
                case IDEN:
                    return parseIdentifier(tokens);
                default:
                    throw new WrongSyntaxException("unexpected token '" + nextToken.value + "'");
            }
        } else
            throw new WrongSyntaxException("expected expression but found none");
    }

    private static Int parseInt(Queue<Token> tokens) {
        return new Int(Integer.parseInt(tokens.remove().value));
    }

    private static Bool parseBool(Queue<Token> tokens) {
        return new Bool(Boolean.parseBoolean(tokens.remove().value));
    }

    private static Identifier parseIdentifier(Queue<Token> tokens) {
        return new Identifier(tokens.remove().value);
    }

    private static Expression parseLet(Queue<Token> tokens) throws WrongSyntaxException {
        parseToken(tokens, new Token(TokenType.LET, "let"));
        if (tokens.peek().type == TokenType.REC)
            return parseRec(tokens);
        Let let = new Let();
        let.var = parseExpression(tokens);
        if (tokens.peek().type != TokenType.EQ)
            let.params = parseListOfParams(tokens, new Token(TokenType.EQ, "="));
        parseToken(tokens, new Token(TokenType.EQ, "="));
        let.value = parseExpression(tokens);
        if (tokens.peek().type != TokenType.END_BLOCK) {
            parseToken(tokens, new Token(TokenType.IN, "in"));
            let.body = parseExpression(tokens);
        }
        return let;
    }

    private static Expression parseRec(Queue<Token> tokens) throws WrongSyntaxException {
        Letrec rec = new Letrec();
        parseToken(tokens, new Token(TokenType.REC, "rec"));
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
        AnonymusFunction fun = new AnonymusFunction();
        parseToken(tokens, new Token(TokenType.FUN, "function"));
        fun.formalParams = parseListOfParams(tokens, new Token(TokenType.ARROW, "->"));
        parseToken(tokens, new Token(TokenType.ARROW, "->"));
        fun.body = parseExpression(tokens);
        return fun;
    }

    private static Expression parseParenthesis(Queue<Token> tokens) throws WrongSyntaxException {
        parseToken(tokens, new Token(TokenType.LPAR, "("));
        Expression firstElem = parseExpression(tokens);
        nextToken = tokens.peek();
        if (nextToken.type == TokenType.SYMB) {
            BinaryOperation bop = new BinaryOperation();
            bop.e1 = firstElem;
            bop.op = parseSymbol(tokens);
            bop.e2 = parseExpression(tokens);
            parseToken(tokens, new Token(TokenType.RPAR, ")"));
            return bop;
        } else if (nextToken.type != TokenType.RPAR) {
            FunctionalApplication app = new FunctionalApplication();
            app.iden = firstElem;
            app.actualParams = parseListOfParams(tokens, new Token(TokenType.RPAR, ")"));
            parseToken(tokens, new Token(TokenType.RPAR, ")"));
            return app;
        } else {
            parseToken(tokens, new Token(TokenType.RPAR, ")"));
            return firstElem;
        }
    }

    private static Lis parseList(Queue<Token> tokens) throws WrongSyntaxException {
        Lis l = new Lis();
        parseToken(tokens, new Token(TokenType.LIST_S, "["));
        while (tokens.peek().type != TokenType.LIST_E)
            l.lis.add(parseExpression(tokens));
        parseToken(tokens, new Token(TokenType.LIST_E, "]"));
        return l;
    }

    private static Expression parseListOperation(Queue<Token> tokens) throws WrongSyntaxException {
        String[] s = tokens.remove().value.split("\\.");
        ListOperation lop = new ListOperation();
        switch (s[1]) {
            case "fold":
                lop.arg_1 = parseExpression(tokens);
            case "cons":
            case "append":
            case "map":
            case "filter":
            case "exists":
            case "forAll":
                lop.arg_2 = parseExpression(tokens);
            case "hd":
            case "tl":
            case "isEmpty":
            case "length":
            case "rev":
                lop.list = parseExpression(tokens);
                lop.op = new Symbol(s[1]);
                return lop;
            default:
                throw new WrongSyntaxException("invalid list operation '" + s[0] + "." + s[1] + "'");
        }
    }

    private static Expression parseUnaryOperation(Queue<Token> tokens) throws WrongSyntaxException {
        UnaryOperation uop = new UnaryOperation();
        parseToken(tokens, new Token(TokenType.NOT, "!"));
        uop.op = new Symbol("!");
        uop.arg = parseExpression(tokens);
        return uop;
    }

    private static void parseToken(Queue<Token> tokens, Token expected) throws WrongSyntaxException {
        nextToken = tokens.peek();
        if (nextToken == null)
            throw new WrongSyntaxException("expected '" + expected.value + "' but found none");
        if (nextToken.type != expected.type)
            throw new WrongSyntaxException("expected '" + expected.value + "' but found '" + nextToken.value + "'");
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
                    throw new WrongSyntaxException("expected '" + stopAt.value + "' to delimit list of parameters");
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
                    return new Symbol(nextToken.value);
                default:
                    throw new WrongSyntaxException("unexpected operation symbol '" + nextToken.value + "'");
            }
        } else
            throw new WrongSyntaxException("expected operation symbol but found none");
    }
}