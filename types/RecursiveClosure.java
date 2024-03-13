package types;

import java.util.Stack;

public class RecursiveClosure implements Expression {
    public Iden name, param;
    public Expression body;
    public Stack<Binding> fenv;

    public RecursiveClosure(Iden name, Iden param, Expression body, Stack<Binding> fenv) {
        this.name = name;
        this.param = param;
        this.body = body;
        this.fenv = fenv;
    }
}