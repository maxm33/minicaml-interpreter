package constructs;

import java.util.List;

public class Letrec implements Expression {
    public Expression name, fbody, letbody;
    public List<Expression> params;
}