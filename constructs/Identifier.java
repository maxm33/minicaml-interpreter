package constructs;

public class Identifier implements Expression {
    public String value;

    public Identifier() {
    }

    public Identifier(String value) {
        this.value = value;
    }
}