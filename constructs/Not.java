package constructs;

import types.Expression;

public class Not implements Expression {
    public Expression arg;

    public void set(Expression e1, Expression e2, Expression e3) {
        this.arg = e1;
    }
}