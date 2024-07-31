package values;

import java.util.LinkedList;

import constructs.Expression;

public class Lis implements Expression {
    public LinkedList<Expression> lis = new LinkedList<Expression>();
    public Expression type;
}