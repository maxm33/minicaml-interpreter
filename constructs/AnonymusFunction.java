package constructs;

import java.util.List;

public class AnonymusFunction implements Expression {
    public Expression body;
    public List<Expression> formalParams;
}