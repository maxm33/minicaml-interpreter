package types;

import java.util.List;

public class RecClosure implements Expression {
    public Iden name;
    public List<Expression> params;
    public Expression body;
    public List<Binding> fenv;

    public RecClosure(Iden name, List<Expression> params, Expression body, List<Binding> fenv) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.fenv = fenv;
    }
}