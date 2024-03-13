package constructs;

import types.Expression;

public class Letrec implements Expression {
    public Expression name, param, fbody, letbody;
}