package constructs;

import java.util.List;
import types.Expression;

public class Function implements Expression {
    public List<Expression> formalParams;
    public Expression body;
}