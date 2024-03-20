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
            case Int i -> {
                return i;
            }
            case Bool b -> {
                return b;
            }
            case Iden id -> {
                Expression val = lookup(id, env);
                return val;
            }
            case Function f -> {
                for (Expression param : f.formalParams)
                    typecheck(param, new Iden());
                return new Closure(f.formalParams, f.body, env);
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
                                    "unexpected type " + first.getClass().getSimpleName() + " inside list");
                    }
                    for (int i = 0; i < l.lis.size(); i++) {
                        Expression element = eval(l.lis.get(i), env);
                        typecheck(element, l.type);
                        l.lis.set(i, element);
                    }
                }
                return l;
            }
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
                    case ">=":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        ret_bool.value = ((Int) e1).value >= ((Int) e2).value;
                        return ret_bool;
                    case "<=":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        ret_bool.value = ((Int) e1).value <= ((Int) e2).value;
                        return ret_bool;
                    case "%":
                        typecheck(e1, ret_int);
                        typecheck(e2, ret_int);
                        ret_int.value = ((Int) e1).value % ((Int) e2).value;
                        return ret_int;
                    case "^":
                        typecheck(e2, e1);
                        if (e1 instanceof Bool b1 && e2 instanceof Bool b2) {
                            ret_bool.value = b1.value ^ b2.value;
                            return ret_bool;
                        } else if (e1 instanceof Int i1 && e2 instanceof Int i2) {
                            ret_int.value = i1.value ^ i2.value;
                            return ret_int;
                        } else
                            throw new TypeMismatchException(
                                    "unexpected type " + e1.getClass().getSimpleName() + " passed to operation ^");
                    case "==":
                        typecheck(e2, e1);
                        if (e1 instanceof Bool b1 && e2 instanceof Bool b2)
                            ret_bool.value = b1.value == b2.value;
                        else if (e1 instanceof Int i1 && e2 instanceof Int i2)
                            ret_bool.value = i1.value == i2.value;
                        else
                            throw new TypeMismatchException(
                                    "unexpected type " + e1.getClass().getSimpleName() + " passed to operation ==");
                        return ret_bool;
                    case "!=":
                        typecheck(e2, e1);
                        if (e1 instanceof Bool b1 && e2 instanceof Bool b2)
                            ret_bool.value = b1.value != b2.value;
                        else if (e1 instanceof Int i1 && e2 instanceof Int i2)
                            ret_bool.value = i1.value != i2.value;
                        else
                            throw new TypeMismatchException(
                                    "unexpected type " + e1.getClass().getSimpleName() + " passed to operation !=");
                        return ret_bool;
                    default:
                        throw new UnknownCommandException("unknown operation " + ((Symbol) bop.op).value);
                }
            }
            case Not not -> {
                Expression arg = eval(not.arg, env);
                Bool ret = new Bool();
                typecheck(arg, ret);
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
                    default -> throw new TypeMismatchException("not a functional value passed to apply");
                }
            }
            case ListCons op -> {
                Expression element = eval(op.element, env);
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                if (((Lis) list).type == null)
                    ((Lis) list).type = element;
                typecheck(element, ((Lis) list).type);
                Lis newList = new Lis();
                newList.type = ((Lis) list).type;
                newList.lis = (LinkedList<Expression>) ((Lis) list).lis.clone();
                newList.lis.addFirst(element);
                return newList;
            }
            case ListHead op -> {
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                Expression head = ((Lis) list).lis.peek();
                return head;
            }
            case ListTail op -> {
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                Lis newList = new Lis();
                newList.type = ((Lis) list).type;
                newList.lis = (LinkedList<Expression>) ((Lis) list).lis.clone();
                newList.lis.removeFirst();
                return newList;
            }
            case ListEmpty op -> {
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                Bool empty = new Bool();
                empty.value = ((Lis) list).lis.isEmpty();
                return empty;
            }
            case ListLength op -> {
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                Int length = new Int();
                length.value = ((Lis) list).lis.size();
                return length;
            }
            case ListAppend op -> {
                Expression list1 = eval(op.list1, env);
                Expression list2 = eval(op.list2, env);
                typecheck(list1, new Lis());
                typecheck(list2, new Lis());
                if (!((Lis) list1).lis.isEmpty() && !((Lis) list2).lis.isEmpty())
                    typecheck(((Lis) list2).type, ((Lis) list1).type);
                Lis newList = new Lis();
                newList.type = ((Lis) list1).type;
                newList.lis = (LinkedList<Expression>) ((Lis) list1).lis.clone();
                for (Expression elem : ((Lis) list2).lis)
                    newList.lis.addLast(elem);
                return newList;
            }
            case ListMap op -> {
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                Lis newList = new Lis();
                for (Expression elem : ((Lis) list).lis) {
                    Apply app = new Apply();
                    app.actualParams = new ArrayList<Expression>();
                    app.actualParams.add(elem);
                    app.iden = op.function;
                    Expression newElem = eval(app, env);
                    newList.lis.addLast(newElem);
                }
                return newList;
            }
            case ListFilter op -> {
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                Lis newList = new Lis();
                for (Expression elem : ((Lis) list).lis) {
                    Apply app = new Apply();
                    app.actualParams = new ArrayList<Expression>();
                    app.actualParams.add(elem);
                    app.iden = op.function;
                    Expression result = eval(app, env);
                    typecheck(result, new Bool());
                    if (((Bool) result).value == true)
                        newList.lis.addLast(elem);
                }
                return newList;
            }
            case ListExists op -> {
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                Bool ret = new Bool();
                ret.value = false;
                for (Expression elem : ((Lis) list).lis) {
                    Apply app = new Apply();
                    app.actualParams = new ArrayList<Expression>();
                    app.actualParams.add(elem);
                    app.iden = op.function;
                    Expression result = eval(app, env);
                    typecheck(result, ret);
                    if (((Bool) result).value == true)
                        ret.value = true;
                }
                return ret;
            }
            case ListForAll op -> {
                Expression list = eval(op.list, env);
                typecheck(list, new Lis());
                Bool ret = new Bool();
                ret.value = true;
                for (Expression elem : ((Lis) list).lis) {
                    Apply app = new Apply();
                    app.actualParams = new ArrayList<Expression>();
                    app.actualParams.add(elem);
                    app.iden = op.function;
                    Expression result = eval(app, env);
                    typecheck(result, ret);
                    if (((Bool) result).value == false)
                        ret.value = false;
                }
                return ret;
            }
            default -> throw new UnknownCommandException(null);
        }
    }

    private static void typecheck(Expression actualType, Expression expectedType) throws TypeMismatchException {
        if (!actualType.getClass().equals(expectedType.getClass()))
            throw new TypeMismatchException("expected type " + expectedType.getClass().getSimpleName() + " but found type " + actualType.getClass().getSimpleName());
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
        throw new NoBindingException("variable " + iden.value + " is not bound in scope");
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
                return "<rec>";
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
        System.out.println();
        for (String block : blocks) {
            System.out.println(block + "\n");
            Queue<Token> tokens = Lexer.tokenize(block);
            Expression exp = Parser.parse(tokens);
            Expression result = eval(exp, env);
            System.out.println("-: " + result.getClass().getSimpleName() + " = " + printExpression(result));
        }
    }
}