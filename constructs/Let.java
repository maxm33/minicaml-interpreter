package constructs;

import types.Expression;

public class Let implements Expression {
    public Expression name, value, body;

    public void set(Expression name, Expression value, Expression body) {
        this.name = name;
        this.value = value;
        this.body = body;
    }
}