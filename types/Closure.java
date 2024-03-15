package types;

import java.util.List;
import java.util.Stack;

public class Closure implements Expression {
    public List<Expression> params;
    public Expression body;
    public Stack<Binding> fenv;

    public Closure(List<Expression> params, Expression body, Stack<Binding> fenv) {
        this.params = params;
        this.body = body;
        this.fenv = fenv;
    }
}