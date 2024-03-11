package constructs;

import types.Expression;

public class Function implements Expression {
    public Expression formalParam, body;

    public void set(Expression formalParam, Expression body, Expression e3) {
        this.formalParam = formalParam;
        this.body = body;
    }
}