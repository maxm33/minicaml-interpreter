import constructs.*;
import exceptions.WrongSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import token.*;
import values.*;

public class Parser {
    private Queue<Token> tokens;
    private Token nextToken;
    private Expression result;

    public void setInput(Queue<Token> queue) {
        tokens = queue;
    }

    public Expression getResult() {
        return result;
    }

    public void parse() throws WrongSyntaxException {
        if ((nextToken = tokens.peek()) == null)
            throw new WrongSyntaxException("no tokens found");
        result = parseExpression();
        parseToken(new Token(TokenType.END_BLOCK, ";;"));
        if (!tokens.isEmpty()) {
            String s = new String();
            for (Token t : tokens)
                s = s + t.value + " ";
            throw new WrongSyntaxException("unexpected tokens out of scope: " + s);
        }
    }

    private Expression parseExpression() throws WrongSyntaxException {
        nextToken = tokens.peek();
        if (nextToken != null) {
            switch (nextToken.type) {
                case INT -> {
                    return parseInt();
                }
                case BOOL -> {
                    return parseBool();
                }
                case LET -> {
                    return parseLet();
                }
                case IF -> {
                    return parseIf();
                }
                case FUN -> {
                    return parseFunction();
                }
                case LPAR -> {
                    return parseParenthesis();
                }
                case LIST_S -> {
                    return parseList();
                }
                case LIST_OP -> {
                    return parseListOperation();
                }
                case NOT -> {
                    return parseUnaryOperation();
                }
                case IDEN -> {
                    return parseIdentifier();
                }
                default -> throw new WrongSyntaxException("unexpected token '" + nextToken.value + "'");
            }
        } else
            throw new WrongSyntaxException("expected expression but found none");
    }

    private Int parseInt() {
        return new Int(Integer.parseInt(tokens.remove().value));
    }

    private Bool parseBool() {
        return new Bool(Boolean.parseBoolean(tokens.remove().value));
    }

    private Identifier parseIdentifier() {
        return new Identifier(tokens.remove().value);
    }

    private Expression parseLet() throws WrongSyntaxException {
        parseToken(new Token(TokenType.LET, "let"));
        if (tokens.peek().type == TokenType.REC)
            return parseRec();
        Let let = new Let();
        let.var = parseExpression();
        if (tokens.peek().type != TokenType.EQ)
            let.params = parseListOfParams(new Token(TokenType.EQ, "="));
        parseToken(new Token(TokenType.EQ, "="));
        let.value = parseExpression();
        if (tokens.peek().type != TokenType.END_BLOCK) {
            parseToken(new Token(TokenType.IN, "in"));
            let.body = parseExpression();
        }
        return let;
    }

    private Expression parseRec() throws WrongSyntaxException {
        Letrec rec = new Letrec();
        parseToken(new Token(TokenType.REC, "rec"));
        rec.name = parseExpression();
        rec.params = parseListOfParams(new Token(TokenType.EQ, "="));
        parseToken(new Token(TokenType.EQ, "="));
        rec.fbody = parseExpression();
        if (tokens.peek().type != TokenType.END_BLOCK) {
            parseToken(new Token(TokenType.IN, "in"));
            rec.letbody = parseExpression();
        }
        return rec;
    }

    private Expression parseIf() throws WrongSyntaxException {
        Ifthenelse ifte = new Ifthenelse();
        parseToken(new Token(TokenType.IF, "if"));
        ifte.guard = parseExpression();
        parseToken(new Token(TokenType.THEN, "then"));
        ifte.then = parseExpression();
        parseToken(new Token(TokenType.ELSE, "else"));
        ifte.els = parseExpression();
        return ifte;
    }

    private Expression parseFunction() throws WrongSyntaxException {
        AnonymusFunction fun = new AnonymusFunction();
        parseToken(new Token(TokenType.FUN, "function"));
        fun.formalParams = parseListOfParams(new Token(TokenType.ARROW, "->"));
        parseToken(new Token(TokenType.ARROW, "->"));
        fun.body = parseExpression();
        return fun;
    }

    private Expression parseParenthesis() throws WrongSyntaxException {
        parseToken(new Token(TokenType.LPAR, "("));
        Expression firstElem = parseExpression();
        nextToken = tokens.peek();
        if (nextToken.type == TokenType.SYMB) {
            BinaryOperation bop = new BinaryOperation();
            bop.e1 = firstElem;
            bop.op = parseSymbol();
            bop.e2 = parseExpression();
            parseToken(new Token(TokenType.RPAR, ")"));
            return bop;
        } else if (nextToken.type != TokenType.RPAR) {
            FunctionalApplication app = new FunctionalApplication();
            app.iden = firstElem;
            app.actualParams = parseListOfParams(new Token(TokenType.RPAR, ")"));
            parseToken(new Token(TokenType.RPAR, ")"));
            return app;
        } else {
            parseToken(new Token(TokenType.RPAR, ")"));
            return firstElem;
        }
    }

    private Lis parseList() throws WrongSyntaxException {
        Lis l = new Lis();
        parseToken(new Token(TokenType.LIST_S, "["));
        while (tokens.peek().type != TokenType.LIST_E)
            l.lis.add(parseExpression());
        parseToken(new Token(TokenType.LIST_E, "]"));
        return l;
    }

    private Expression parseListOperation() throws WrongSyntaxException {
        String[] s = tokens.remove().value.split("\\.");
        ListOperation lop = new ListOperation();
        switch (s[1]) {
            case "fold":
                lop.arg1 = parseExpression();
            case "cons":
            case "append":
            case "map":
            case "filter":
            case "exists":
            case "forAll":
                lop.arg2 = parseExpression();
            case "hd":
            case "tl":
            case "isEmpty":
            case "length":
            case "rev":
                lop.list = parseExpression();
                lop.op = new Symbol(s[1]);
                return lop;
            default:
                throw new WrongSyntaxException("invalid list operation '" + s[0] + "." + s[1] + "'");
        }
    }

    private Expression parseUnaryOperation() throws WrongSyntaxException {
        UnaryOperation uop = new UnaryOperation();
        parseToken(new Token(TokenType.NOT, "!"));
        uop.op = new Symbol("!");
        uop.arg = parseExpression();
        return uop;
    }

    private void parseToken(Token expected) throws WrongSyntaxException {
        nextToken = tokens.peek();
        if (nextToken == null)
            throw new WrongSyntaxException("expected '" + expected.value + "' but found none");
        if (nextToken.type != expected.type)
            throw new WrongSyntaxException("expected '" + expected.value + "' but found '" + nextToken.value + "'");
        tokens.remove();
    }

    private List<Expression> parseListOfParams(Token stopAt)
            throws WrongSyntaxException {
        List<Expression> params = new ArrayList<>();
        while (tokens.peek().type != stopAt.type) {
            if (params.size() > 16)
                throw new WrongSyntaxException("too many parameters passed to function");
            else {
                try {
                    params.add(parseExpression());
                } catch (WrongSyntaxException e) {
                    throw new WrongSyntaxException("expected '" + stopAt.value + "' to delimit list of parameters");
                }
            }
        }
        if (params.isEmpty())
            throw new WrongSyntaxException("no parameters passed to function");
        return params;
    }

    private Symbol parseSymbol() throws WrongSyntaxException {
        nextToken = tokens.peek();
        if (nextToken != null) {
            tokens.remove();
            switch (nextToken.value) {
                case "+", "-", "*", "/", "&", "|", ">", "<", ">=", "<=", "%", "^", "==", "!=" -> {
                    return new Symbol(nextToken.value);
                }
                default -> throw new WrongSyntaxException("unexpected operation symbol '" + nextToken.value + "'");
            }
        } else
            throw new WrongSyntaxException("expected operation symbol but found none");
    }
}