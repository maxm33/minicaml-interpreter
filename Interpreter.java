import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.Stack;
import constructs.*;
import exceptions.*;
import token.Token;
import types.*;

public class Interpreter {

    private static Expression eval(Expression e, Stack<Binding> env)
            throws ZeroDividerException, UnknownCommandException, TypeMismatchException, NoBindingException {
        switch (e) {
            case Operation op -> {
                Expression e1 = eval(op.e1, env), e2 = eval(op.e2, env);
                Int ret_int = new Int();
                Bool ret_bool = new Bool();
                typecheck(op.op, new Symbol());
                switch (((Symbol) op.op).value) {
                    case "+":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        ret_int.value = ((Int) e1).value + ((Int) e2).value;
                        return ret_int;
                    case "-":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        ret_int.value = ((Int) e1).value - ((Int) e2).value;
                        return ret_int;
                    case "*":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        ret_int.value = ((Int) e1).value * ((Int) e2).value;
                        return ret_int;
                    case "/":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        if (((Int) e2).value == 0)
                            throw new ZeroDividerException("cannot divide by zero");
                        ret_int.value = ((Int) e1).value / ((Int) e2).value;
                        return ret_int;
                    case "&":
                        typecheck(e1, ret_bool);
                        typecheck(e2, ret_bool);
                        ret_bool.value = ((Bool) e1).value && ((Bool) e2).value;
                        return ret_bool;
                    case "|":
                        typecheck(e1, ret_bool);
                        typecheck(e2, ret_bool);
                        ret_bool.value = ((Bool) e1).value || ((Bool) e2).value;
                        return ret_bool;
                    case ">":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        ret_bool.value = ((Int) e1).value > ((Int) e2).value;
                        return ret_bool;
                    case "<":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        ret_bool.value = ((Int) e1).value < ((Int) e2).value;
                        return ret_bool;
                    case "==":
                        typecheck(e2, e1);
                        if (e1 instanceof Bool b1 && e2 instanceof Bool b2)
                            ret_bool.value = b1.value == b2.value;
                        else if (e1 instanceof Int i1 && e2 instanceof Int i2)
                            ret_bool.value = i1.value == i2.value;
                        else
                            throw new TypeMismatchException("unexpected type passed to operation");
                        return ret_bool;
                    default:
                        throw new UnknownCommandException("Unknown operation '" + ((Symbol) op.op).value + "'");
                }
            }
            case Not not -> {
                Expression arg = eval(not.arg, env);
                typecheck(arg, new Bool());
                Bool ret = new Bool();
                ret.value = !((Bool) arg).value;
                return ret;
            }
            case Ifthenelse ifte -> {
                Expression guard = eval(ifte.guard, env);
                typecheck(guard, new Bool());
                if (((Bool) guard).value == true)
                    return eval(ifte.then, env);
                else
                    return eval(ifte.els, env);
            }
            case Let let -> {
                typecheck(let.var, new Iden());
                Binding bin = new Binding((Iden) let.var, eval(let.value, env));
                Stack<Binding> newEnv = bind(bin, env);
                return eval(let.body, newEnv);
            }
            case Letrec letr -> {
                typecheck(letr.name, new Iden());
                typecheck(letr.param, new Iden());
                RecursiveClosure closure = new RecursiveClosure((Iden) letr.name, (Iden) letr.param, letr.fbody, env);
                Binding bin = new Binding((Iden) letr.name, closure);
                Stack<Binding> newEnv = bind(bin, env);
                return eval(letr.letbody, newEnv);
            }
            case Apply app -> {
                typecheck(app.iden, new Iden());
                Expression closure = lookup((Iden) app.iden, env);
                switch (closure) {
                    case Closure c -> {
                        Expression aVal = eval(app.actualParam, env);
                        Binding bin = new Binding(c.param, aVal);
                        Stack<Binding> extFenv = bind(bin, c.fenv);
                        return eval(c.body, extFenv);
                    }
                    case RecursiveClosure rec -> {
                        Expression aVal = eval(app.actualParam, env);
                        Binding bin = new Binding(rec.name, rec);
                        Stack<Binding> extFenv = bind(bin, rec.fenv);
                        bin = new Binding(rec.param, aVal);
                        extFenv = bind(bin, extFenv);
                        return eval(rec.body, extFenv);
                    }
                    default -> throw new TypeMismatchException("not a functional value");
                }
            }
            case Iden iden -> {
                Expression val = lookup(iden, env);
                return val;
            }
            case Int i -> {
                return i;
            }
            case Bool b -> {
                return b;
            }
            case Function f -> {
                typecheck(f.formalParam, new Iden());
                return new Closure((Iden) f.formalParam, f.body, env);
            }
            default -> throw new UnknownCommandException(null);
        }
    }

    private static void typecheck(Expression actualType, Expression expectedType) throws TypeMismatchException {
        if (!actualType.getClass().equals(expectedType.getClass()))
            throw new TypeMismatchException(
                    "expected '" + expectedType.getClass().toString() + "' but '" + actualType.getClass().toString()
                            + "' was found");
    }

    @SuppressWarnings("unchecked")
    private static Stack<Binding> bind(Binding bin, Stack<Binding> env) {
        Stack<Binding> newEnv = (Stack<Binding>) env.clone();
        newEnv.push(bin);
        return newEnv;
    }

    private static Expression lookup(Iden iden, Stack<Binding> env) throws NoBindingException {
        for (Binding bin : env)
            if ((bin.var.value).contentEquals(iden.value))
                return bin.value;
        throw new NoBindingException("variable '" + iden.value + "' is not bound in scope");
    }

    public static void main(String[] args)
            throws IllegalTokenException, WrongSyntaxException, ZeroDividerException,
            UnknownCommandException, TypeMismatchException, NoBindingException, IOException {
        if (args.length < 1) {
            System.err.println("\nno path was provided.\nUsage: java Interpreter <path-to-file>");
            return;
        }
        String program = Files.readString(Paths.get(args[0]));
        System.out.println("\n" + program);

        long start = System.currentTimeMillis();
        Queue<Token> tokens = Lexer.tokenize(program);
        System.out.println("\nLexic analyzed.");

        Expression exp = Parser.parse(tokens);
        System.out.println("Parsing done.");

        Stack<Binding> emptyEnv = new Stack<Binding>();
        Expression result = eval(exp, emptyEnv);
        System.out.println("Expression evaluated.\n");
        long stop = System.currentTimeMillis();

        if (result instanceof Int e)
            System.out.println("output: " + e.value);
        if (result instanceof Bool e)
            System.out.println("output: " + e.value);

        System.out.println("\nExecuted in " + (stop - start) + " ms.");
    }
}