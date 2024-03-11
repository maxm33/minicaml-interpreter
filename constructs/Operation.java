package constructs;

import types.Expression;

public class Operation implements Expression {
    public Expression e1, op, e2;

    public void set(Expression e1, Expression op, Expression e2) {
        this.e1 = e1;
        this.op = op;
        this.e2 = e2;
    }
}