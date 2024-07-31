package values;

import constructs.Expression;

public class Int implements Expression {
    public Integer value;

    public Int() {
    }

    public Int(int value) {
        this.value = value;
    }
}