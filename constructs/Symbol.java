package constructs;

public class Symbol implements Expression {
    public String value;

    public Symbol() {
    }

    public Symbol(String value) {
        this.value = value;
    }
}