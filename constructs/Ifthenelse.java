package constructs;

import types.Expression;

public class Ifthenelse implements Expression {
    public Expression guard, then, els;

    public void set(Expression guard, Expression then, Expression els) {
        this.guard = guard;
        this.then = then;
        this.els = els;
    }
}