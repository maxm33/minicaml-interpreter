package constructs;

import java.util.List;
import types.Expression;

public class Apply implements Expression {
    public Expression iden;
    public List<Expression> actualParams;
}