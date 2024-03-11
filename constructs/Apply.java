package constructs;

import types.Expression;

public class Apply implements Expression {
    public Expression iden, actualParam;

    public void set(Expression iden, Expression actualParam, Expression e3) {
        this.iden = iden;
        this.actualParam = actualParam;
    }
}