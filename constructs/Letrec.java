package constructs;

import types.Expression;

public class Letrec implements Expression {
    public Expression name, param, fbody, letbody;

    public void setName(Expression name) {
        this.name = name;
    }

    public void set(Expression param, Expression fbody, Expression letbody) {
        this.param = param;
        this.fbody = fbody;
        this.letbody = letbody;
    }
}