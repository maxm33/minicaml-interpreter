package types;

import java.util.List;
import java.util.Stack;

public class RecClosure implements Expression {
    public Iden name;
    public List<Expression> params;
    public Expression body;
    public Stack<Binding> fenv;

    public RecClosure(Iden name, List<Expression> params, Expression body, Stack<Binding> fenv) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.fenv = fenv;
    }
}