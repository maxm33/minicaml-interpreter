import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import constructs.*;
import exceptions.*;
import token.Token;
import types.*;

public class Interpreter {

    @SuppressWarnings("unchecked")
    public static Expression eval(Expression e, List<Binding> env)
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
                                    "unexpected type '" + e1.getClass().getSimpleName() + "' passed to operation '=='");
                        return ret_bool;
                    case "!=":
                        typecheck(e2, e1);
                        if (e1 instanceof Bool b1 && e2 instanceof Bool b2)
                            ret_bool.value = b1.value != b2.value;
                        else if (e1 instanceof Int i1 && e2 instanceof Int i2)
                            ret_bool.value = i1.value != i2.value;
                        else
                            throw new TypeMismatchException(
                                    "unexpected type '" + e1.getClass().getSimpleName() + "' passed to operation '!='");
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
                Expression value = eval(let.value, env);
                Binding bin = new Binding((Iden) let.var, value);
                // creating a new env for the 'in' scope
                if (let.body != null) {
                    List<Binding> newEnv = bind(bin, env);
                    return eval(let.body, newEnv);
                } // extending the global env
                else {
                    env.add(bin);
                    return value;
                }
            }
            case Letrec letr -> {
                typecheck(letr.name, new Iden());
                for (Expression param : letr.params)
                    typecheck(param, new Iden());
                RecClosure closure = new RecClosure((Iden) letr.name, letr.params, letr.fbody, env);
                Binding bin = new Binding((Iden) letr.name, closure);
                // creating a new env for the 'in' scope
                if (letr.letbody != null) {
                    List<Binding> newEnv = bind(bin, env);
                    return eval(letr.letbody, newEnv);
                } // extending the global env
                else {
                    env.add(bin);
                    return closure;
                }
            }
            case Apply app -> {
                int i = 0;
                Expression closure = eval(app.iden, env);
                switch (closure) {
                    case Closure clo -> {
                        if (app.actualParams.size() != clo.params.size())
                            throw new WrongSyntaxException(
                                    "functional application parameters do not match the function signature");
                        List<Binding> extFenv = clone(clo.fenv);
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
                        List<Binding> extFenv = new ArrayList<Binding>();
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
            case ListAdd add -> {
                Expression element = eval(add.element, env);
                Lis list = (Lis) eval(add.list, env);
                if (list.type == null)
                    list.type = element;
                typecheck(element, list.type);
                Lis newList = new Lis();
                newList.type = list.type;
                newList.lis = (LinkedList<Expression>) list.lis.clone();
                newList.lis.addLast(element);
                return newList;
            }
            case ListRemove rem -> {
                Lis list = (Lis) eval(rem.list, env);
                Lis newList = new Lis();
                newList.type = list.type;
                newList.lis = (LinkedList<Expression>) list.lis.clone();
                newList.lis.removeFirst();
                return newList;
            }
            case ListHead head -> {
                Lis list = (Lis) eval(head.list, env);
                Expression h = list.lis.peek();
                return h;
            }
            case ListEmpty isempty -> {
                Lis list = (Lis) eval(isempty.list, env);
                Bool ret = new Bool();
                ret.value = list.lis.isEmpty();
                return ret;
            }
            case ListLength length -> {
                Lis list = (Lis) eval(length.list, env);
                Int ret = new Int();
                ret.value = list.lis.size();
                return ret;
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
            case Lis l -> {
                if (l.lis.size() > 0) {
                    if (l.type == null) {
                        Expression first = eval(l.lis.getFirst(), env);
                        if (first instanceof Int)
                            l.type = new Int();
                        else if (first instanceof Bool)
                            l.type = new Bool();
                        else if (first instanceof Closure)
                            l.type = new Closure();
                        else if (first instanceof RecClosure)
                            l.type = new RecClosure();
                        else if (first instanceof Lis)
                            l.type = new Lis();
                        else
                            throw new TypeMismatchException(
                                    "unexpected type '" + first.getClass().getSimpleName() + "' inside list");
                    }
                    for (int i = 0; i < l.lis.size(); i++) {
                        Expression element = eval(l.lis.get(i), env);
                        typecheck(element, l.type);
                        l.lis.set(i, element);
                    }
                }
                return l;
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
                    "expected '" + expectedType.getClass().getSimpleName() + "' but '"
                            + actualType.getClass().getSimpleName()
                            + "' was found");
    }

    private static List<Binding> bind(Binding bin, List<Binding> env) {
        List<Binding> newEnv = clone(env);
        newEnv.add(bin);
        return newEnv;
    }

    private static Expression lookup(Iden iden, List<Binding> env) throws NoBindingException {
        for (int i = env.size() - 1; i >= 0; i--)
            if (env.get(i).var.value.contentEquals(iden.value))
                return env.get(i).value;
        throw new NoBindingException("variable '" + iden.value + "' is not bound in scope");
    }

    private static List<Binding> clone(List<Binding> oldList) {
        List<Binding> newList = new ArrayList<Binding>();
        for (Binding bin : oldList)
            newList.add(bin);
        return newList;
    }

    private static String printExpression(Expression exp) {
        switch (exp) {
            case Int i:
                return Integer.toString(i.value);
            case Bool b:
                return Boolean.toString(b.value);
            case Iden id:
                return id.value;
            case Closure c:
                return "<fun>";
            case RecClosure r:
                return "<rec fun>";
            case Lis l:
                String out = "[";
                for (Expression e : l.lis)
                    out = out + printExpression(e) + ",";
                int index;
                if ((index = out.lastIndexOf(",")) != -1)
                    out = out.substring(0, index);
                return out + "]";
            default:
                return null;
        }
    }

    public static void main(String[] args)
            throws IllegalTokenException, WrongSyntaxException, ZeroDividerException,
            UnknownCommandException, TypeMismatchException, NoBindingException, IOException {
        if (args.length < 1) {
            System.err.println("\nNo path was provided.\nUsage: java Interpreter <path-to-file>");
            return;
        }
        String program = Files.readString(Paths.get(args[0]));
        List<Binding> env = new ArrayList<Binding>();

        String[] blocks = program.split("(?<=\\s+;;)");
        for (String block : blocks) {
            System.out.println("\n" + block + "\n");
            Queue<Token> tokens = Lexer.tokenize(block);
            Expression exp = Parser.parse(tokens);
            Expression result = eval(exp, env);
            System.out.println("Output: " + printExpression(result));
        }
    }
}