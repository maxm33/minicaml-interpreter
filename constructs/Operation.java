package constructs;

import types.Symbol;
import types.Expression;

public class Operation implements Expression {
    public Symbol op;
    public Expression e1, e2;
}