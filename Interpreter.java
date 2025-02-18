import constructs.*;
import exceptions.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import values.*;

public class Interpreter {

    @SuppressWarnings("unchecked")
    public Expression eval(Expression e, List<Binding> env)
            throws ZeroDividerException, UnknownCommandException, TypeMismatchException, NoBindingException,
            WrongSyntaxException {
        Int ret_int = new Int();
        Bool ret_bool = new Bool();
        switch (e) {
            case Int i -> {
                return i;
            }
            case Bool b -> {
                return b;
            }
            case Identifier id -> {
                Expression val = lookup(id, env);
                return val;
            }
            case AnonymusFunction f -> {
                for (Expression param : f.formalParams)
                    typecheck(param, new Identifier());
                return new Closure(f.formalParams, f.body, env);
            }
            case Lis l -> {
                if (!l.lis.isEmpty()) {
                    if (l.type == null) {
                        Expression first = eval(l.lis.getFirst(), env);
                        switch (first) {
                            case Int i -> l.type = i;
                            case Bool b -> l.type = b;
                            case Closure c -> l.type = c;
                            case RecursiveClosure rc -> l.type = rc;
                            case Lis li -> l.type = li;
                            default -> throw new TypeMismatchException(
                                    "unexpected type '" + first.getClass().getSimpleName() + "' inside list");
                        }
                    }
                    for (int i = 0; i < l.lis.size(); i++) {
                        Expression element = eval(l.lis.get(i), env);
                        typecheck(element, l.type);
                        l.lis.set(i, element);
                    }
                }
                return l;
            }
            case BinaryOperation bop -> {
                Expression e1 = eval(bop.e1, env), e2 = eval(bop.e2, env);
                switch (bop.op.value) {
                    case "+" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        ret_int.value = ((Int) e1).value + ((Int) e2).value;
                        return ret_int;
                    }
                    case "-" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        ret_int.value = ((Int) e1).value - ((Int) e2).value;
                        return ret_int;
                    }
                    case "*" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        ret_int.value = ((Int) e1).value * ((Int) e2).value;
                        return ret_int;
                    }
                    case "/" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        if (((Int) e2).value == 0)
                            throw new ZeroDividerException("cannot divide by zero");
                        ret_int.value = ((Int) e1).value / ((Int) e2).value;
                        return ret_int;
                    }
                    case "&" -> {
                        typecheck(e1, new Bool());
                        typecheck(e2, new Bool());
                        ret_bool.value = ((Bool) e1).value && ((Bool) e2).value;
                        return ret_bool;
                    }
                    case "|" -> {
                        typecheck(e1, new Bool());
                        typecheck(e2, new Bool());
                        ret_bool.value = ((Bool) e1).value || ((Bool) e2).value;
                        return ret_bool;
                    }
                    case ">" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        ret_bool.value = ((Int) e1).value > ((Int) e2).value;
                        return ret_bool;
                    }
                    case "<" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        ret_bool.value = ((Int) e1).value < ((Int) e2).value;
                        return ret_bool;
                    }
                    case ">=" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        ret_bool.value = ((Int) e1).value >= ((Int) e2).value;
                        return ret_bool;
                    }
                    case "<=" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        ret_bool.value = ((Int) e1).value <= ((Int) e2).value;
                        return ret_bool;
                    }
                    case "%" -> {
                        typecheck(e1, new Int());
                        typecheck(e2, new Int());
                        ret_int.value = ((Int) e1).value % ((Int) e2).value;
                        return ret_int;
                    }
                    case "^" -> {
                        typecheck(e2, e1);
                        if (e1 instanceof Bool b1 && e2 instanceof Bool b2) {
                            ret_bool.value = b1.value ^ b2.value;
                            return ret_bool;
                        } else if (e1 instanceof Int i1 && e2 instanceof Int i2) {
                            ret_int.value = i1.value ^ i2.value;
                            return ret_int;
                        } else
                            throw new TypeMismatchException(
                                    "unexpected type '" + e1.getClass().getSimpleName() + "' passed to operation ^");
                    }
                    case "==" -> {
                        typecheck(e2, e1);
                        if (e1 instanceof Bool b1 && e2 instanceof Bool b2)
                            ret_bool.value = Objects.equals(b1.value, b2.value);
                        else if (e1 instanceof Int i1 && e2 instanceof Int i2)
                            ret_bool.value = Objects.equals(i1.value, i2.value);
                        else
                            throw new TypeMismatchException(
                                    "unexpected type '" + e1.getClass().getSimpleName() + "' passed to operation ==");
                        return ret_bool;
                    }
                    case "!=" -> {
                        typecheck(e2, e1);
                        if (e1 instanceof Bool b1 && e2 instanceof Bool b2)
                            ret_bool.value = !Objects.equals(b1.value, b2.value);
                        else if (e1 instanceof Int i1 && e2 instanceof Int i2)
                            ret_bool.value = !Objects.equals(i1.value, i2.value);
                        else
                            throw new TypeMismatchException(
                                    "unexpected type '" + e1.getClass().getSimpleName() + "' passed to operation !=");
                        return ret_bool;
                    }
                    default -> throw new UnknownCommandException("unknown operation '" + bop.op.value + "'");
                }
            }
            case UnaryOperation uop -> {
                Expression arg = eval(uop.arg, env);
                switch (uop.op.value) {
                    case "!" -> {
                        typecheck(arg, new Bool());
                        ret_bool.value = !((Bool) arg).value;
                        return ret_bool;
                    }
                    default -> throw new UnknownCommandException("unknown operation '" + uop.op.value + "'");
                }
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
                typecheck(let.var, new Identifier());
                if (let.params != null) {
                    AnonymusFunction fun = new AnonymusFunction();
                    fun.formalParams = let.params;
                    fun.body = let.value;
                    let.value = fun;
                }
                Expression value = eval(let.value, env);
                Binding bin = new Binding((Identifier) let.var, value);
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
                typecheck(letr.name, new Identifier());
                for (Expression param : letr.params)
                    typecheck(param, new Identifier());
                RecursiveClosure closure = new RecursiveClosure((Identifier) letr.name, letr.params, letr.fbody, env);
                Binding bin = new Binding((Identifier) letr.name, closure);
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
            case FunctionalApplication app -> {
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
                            Binding bin = new Binding((Identifier) param, aVal);
                            extFenv = bind(bin, extFenv);
                        }
                        return eval(clo.body, extFenv);
                    }
                    case RecursiveClosure rec -> {
                        if (app.actualParams.size() != rec.params.size())
                            throw new WrongSyntaxException(
                                    "functional application parameters do not match the function signature");
                        Binding bin = new Binding(rec.name, rec);
                        List<Binding> extFenv = bind(bin, rec.fenv);
                        for (Expression param : rec.params) {
                            Expression aVal = eval(app.actualParams.get(i++), env);
                            bin = new Binding((Identifier) param, aVal);
                            extFenv = bind(bin, extFenv);
                        }
                        return eval(rec.body, extFenv);
                    }
                    default -> throw new TypeMismatchException("not a functional value passed");
                }
            }
            case ListOperation lop -> {
                Lis newList = new Lis();
                Expression list = eval(lop.list, env);
                typecheck(list, new Lis());
                Lis oplis = (Lis) list;
                switch (lop.op.value) {
                    case "cons" -> {
                        Expression element = eval(lop.arg2, env);
                        if (oplis.type == null)
                            oplis.type = element;
                        else
                            typecheck(element, oplis.type);
                        newList.type = oplis.type;
                        newList.lis = (LinkedList<Expression>) oplis.lis.clone();
                        newList.lis.addFirst(element);
                        return newList;
                    }
                    case "hd" -> {
                        return oplis.lis.peek();
                    }
                    case "tl" -> {
                        if (!oplis.lis.isEmpty()) {
                            newList.type = oplis.type;
                            newList.lis = (LinkedList<Expression>) oplis.lis.clone();
                            newList.lis.removeFirst();
                            return newList;
                        } else
                            return null;
                    }
                    case "isEmpty" -> {
                        return new Bool(oplis.lis.isEmpty());
                    }
                    case "length" -> {
                        return new Int(oplis.lis.size());
                    }
                    case "append" -> {
                        Expression list1 = eval(lop.arg2, env);
                        typecheck(list1, new Lis());
                        Lis arglis = (Lis) list1;
                        if (!oplis.lis.isEmpty() && !arglis.lis.isEmpty())
                            typecheck(oplis.type, arglis.type);
                        newList.type = arglis.type;
                        newList.lis = (LinkedList<Expression>) arglis.lis.clone();
                        for (Expression elem : oplis.lis)
                            newList.lis.addLast(elem);
                        return newList;
                    }
                    case "map" -> {
                        for (Expression elem : oplis.lis) {
                            FunctionalApplication app = new FunctionalApplication();
                            app.actualParams = new ArrayList<>();
                            app.actualParams.add(elem);
                            app.iden = lop.arg2;
                            Expression newElem = eval(app, env);
                            newList.lis.addLast(newElem);
                        }
                        return newList;
                    }
                    case "filter" -> {
                        for (Expression elem : oplis.lis) {
                            FunctionalApplication app = new FunctionalApplication();
                            app.actualParams = new ArrayList<>();
                            app.actualParams.add(elem);
                            app.iden = lop.arg2;
                            Expression result = eval(app, env);
                            typecheck(result, new Bool());
                            if (((Bool) result).value == true)
                                newList.lis.addLast(elem);
                        }
                        return newList;
                    }
                    case "exists" -> {
                        Bool ret = new Bool(false);
                        for (Expression elem : oplis.lis) {
                            FunctionalApplication app = new FunctionalApplication();
                            app.actualParams = new ArrayList<>();
                            app.actualParams.add(elem);
                            app.iden = lop.arg2;
                            Expression result = eval(app, env);
                            typecheck(result, new Bool());
                            if (((Bool) result).value == true)
                                ret.value = true;
                        }
                        return ret;
                    }
                    case "forAll" -> {
                        Bool _ret = new Bool(true);
                        for (Expression elem : oplis.lis) {
                            FunctionalApplication app = new FunctionalApplication();
                            app.actualParams = new ArrayList<>();
                            app.actualParams.add(elem);
                            app.iden = lop.arg2;
                            Expression result = eval(app, env);
                            typecheck(result, new Bool());
                            if (((Bool) result).value == false)
                                _ret.value = false;
                        }
                        return _ret;
                    }
                    case "fold" -> {
                        Expression firstAcc = eval(lop.arg2, env);
                        Expression newAcc = null;
                        for (Expression elem : oplis.lis) {
                            FunctionalApplication app = new FunctionalApplication();
                            app.actualParams = new ArrayList<>();
                            app.actualParams.add(elem);
                            if (newAcc == null)
                                app.actualParams.add(firstAcc);
                            else
                                app.actualParams.add(newAcc);
                            app.iden = lop.arg1;
                            newAcc = eval(app, env);
                            typecheck(newAcc, firstAcc);
                        }
                        return newAcc;
                    }
                    case "rev" -> {
                        for (Expression elem : oplis.lis)
                            newList.lis.addFirst(elem);
                        return newList;
                    }
                    default -> throw new UnknownCommandException("unknown list operation '" + lop.op.value + "'");
                }
            }
            default -> throw new UnknownCommandException(null);
        }
    }

    private void typecheck(Expression actualType, Expression expectedType) throws TypeMismatchException {
        if (!actualType.getClass().equals(expectedType.getClass()))
            throw new TypeMismatchException("expected type '" + expectedType.getClass().getSimpleName()
                    + "' but found type '" + actualType.getClass().getSimpleName() + "'");
    }

    private List<Binding> bind(Binding bin, List<Binding> oldEnv) {
        List<Binding> newEnv = clone(oldEnv);
        newEnv.add(bin);
        return newEnv;
    }

    private Expression lookup(Identifier iden, List<Binding> env) throws NoBindingException {
        for (int i = env.size() - 1; i >= 0; i--)
            if (env.get(i).var.value.contentEquals(iden.value))
                return env.get(i).value;
        throw new NoBindingException("variable '" + iden.value + "' is not bound in scope");
    }

    private List<Binding> clone(List<Binding> oldList) {
        List<Binding> newList = new ArrayList<>();
        for (Binding bin : oldList)
            newList.add(bin);
        return newList;
    }

    public String printValue(Expression e) {
        switch (e) {
            case Int i -> {
                return Integer.toString(i.value);
            }
            case Bool b -> {
                return Boolean.toString(b.value);
            }
            case Identifier id -> {
                return id.value;
            }
            case Closure _ -> {
                return "<fun>";
            }
            case RecursiveClosure _ -> {
                return "<rec>";
            }
            case Lis l -> {
                String out = "[";
                for (Expression element : l.lis)
                    out = out + printValue(element) + ",";
                int index;
                if ((index = out.lastIndexOf(",")) != -1)
                    out = out.substring(0, index);
                return out + "]";
            }
            default -> {
                return null;
            }
        }
    }
}