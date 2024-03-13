package types;

import java.util.Stack;

public class Closure implements Expression {
    public Iden param;
    public Expression body;
    public Stack<Binding> fenv;

    public Closure(Iden param, Expression body, Stack<Binding> fenv) {
        this.param = param;
        this.body = body;
        this.fenv = fenv;
    }
}