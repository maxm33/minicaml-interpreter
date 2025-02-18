package values;

import constructs.Expression;
import java.util.LinkedList;

public class Lis implements Expression {
    public LinkedList<Expression> lis = new LinkedList<>();
    public Expression type;
}