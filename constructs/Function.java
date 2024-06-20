package constructs;

import java.util.List;
import types.Expression;

public class Function implements Expression {
    public Expression body;
    public List<Expression> formalParams;
}