package constructs;

import java.util.List;

public class Let implements Expression {
    public Expression var, value, body;
    public List<Expression> params;
}