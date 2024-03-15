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
            throws ZeroDividerException, UnknownCommandException, TypeMismatchException, NoBindingException,
            WrongSyntaxException {
        switch (e) {
            case Operation bop -> {
                Expression e1 = eval(bop.e1, env), e2 = eval(bop.e2, env);
                Int ret_int = new Int();
                Bool ret_bool = new Bool();
                typecheck(bop.op, new Symbol());
                switch (((Symbol) bop.op).value) {
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
                            throw new TypeMismatchException(
                                    "unexpected type '" + e1.getClass().toString() + "' passed to operation '=='");
                        return ret_bool;
                    default:
                        throw new UnknownCommandException("unknown operation '" + ((Symbol) bop.op).value + "'");
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
                for (Expression param : letr.params)
                    typecheck(param, new Iden());
                RecClosure closure = new RecClosure((Iden) letr.name, letr.params, letr.fbody, env);
                Binding bin = new Binding((Iden) letr.name, closure);
                Stack<Binding> newEnv = bind(bin, env);
                return eval(letr.letbody, newEnv);
            }
            case Apply app -> {
                int i = 0;
                Expression closure = eval(app.iden, env);
                switch (closure) {
                    case Closure clo -> {
                        if (app.actualParams.size() != clo.params.size())
                            throw new WrongSyntaxException(
                                    "functional application parameters do not match the function signature");
                        @SuppressWarnings("unchecked")
                        Stack<Binding> extFenv = (Stack<Binding>) clo.fenv.clone();
                        for (Expression param : clo.params) {
                            Expression aVal = eval(app.actualParams.get(i++), env);
                            Binding bin = new Binding((Iden) param, aVal);
                            extFenv = bind(bin, extFenv);
                        }
                        return eval(clo.body, extFenv);
                    }
                    case RecClosure rec -> {
                        if (app.actualParams.size() != rec.params.size())
                            throw new WrongSyntaxException(
                                    "functional application parameters do not match the function signature");
                        Stack<Binding> extFenv = new Stack<Binding>();
                        Binding bin = new Binding(rec.name, rec);
                        extFenv = bind(bin, rec.fenv);
                        for (Expression param : rec.params) {
                            Expression aVal = eval(app.actualParams.get(i++), env);
                            bin = new Binding((Iden) param, aVal);
                            extFenv = bind(bin, extFenv);
                        }
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
                for (Expression param : f.formalParams)
                    typecheck(param, new Iden());
                return new Closure(f.formalParams, f.body, env);
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
            System.err.println("\nNo path was provided.\nUsage: java Interpreter <path-to-file>");
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
            System.out.println("Output: " + e.value);
        if (result instanceof Bool e)
            System.out.println("Output: " + e.value);

        System.out.println("\nExecuted in " + (stop - start) + " ms.");
    }
}