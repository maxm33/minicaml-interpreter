package values;

import constructs.Expression;

public class Bool implements Expression {
    public Boolean value;

    public Bool() {
    }

    public Bool(boolean value) {
        this.value = value;
    }
}