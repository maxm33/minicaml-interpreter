package constructs;

import types.Expression;

public class Let implements Expression {
    public Expression var, value, body;

    public void set(Expression var, Expression value, Expression body) {
        this.var = var;
        this.value = value;
        this.body = body;
    }
}