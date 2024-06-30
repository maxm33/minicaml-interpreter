package values;

import java.util.List;

import constructs.Binding;
import constructs.Expression;
import constructs.Identifier;

public class RecursiveClosure implements Expression {
    public Identifier name;
    public List<Expression> params;
    public Expression body;
    public List<Binding> fenv;

    public RecursiveClosure() {
    }

    public RecursiveClosure(Identifier name, List<Expression> params, Expression body, List<Binding> fenv) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.fenv = fenv;
    }
}