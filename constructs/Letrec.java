package constructs;

import java.util.List;
import types.Expression;

public class Letrec implements Expression {
    public Expression name, fbody, letbody;
    public List<Expression> params;
}