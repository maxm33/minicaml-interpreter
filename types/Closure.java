package types;

import java.util.List;

public class Closure implements Expression {
    public List<Expression> params;
    public Expression body;
    public List<Binding> fenv;

    public Closure() {
    }

    public Closure(List<Expression> params, Expression body, List<Binding> fenv) {
        this.params = params;
        this.body = body;
        this.fenv = fenv;
    }
}