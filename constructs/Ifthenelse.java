package constructs;

import types.Expression;

public class Ifthenelse implements Expression {
    public Expression guard, then, els;
}