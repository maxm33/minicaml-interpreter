package constructs;

import types.Expression;

public class ListOp implements Expression {
    public String operation;
    public Expression arg, list;
}