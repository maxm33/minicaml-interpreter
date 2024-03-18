package types;

import java.util.LinkedList;

public class Lis implements Expression {
    public LinkedList<Expression> lis = new LinkedList<Expression>();
    public Expression type;
}