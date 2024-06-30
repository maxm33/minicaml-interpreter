package constructs;

public class Binding {
    public Identifier var;
    public Expression value;

    public Binding(Identifier var, Expression value) {
        this.var = var;
        this.value = value;
    }
}