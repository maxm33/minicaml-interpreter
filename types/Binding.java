package types;

public class Binding {
    public Iden var;
    public Expression value;

    public Binding(Iden var, Expression value) {
        this.var = var;
        this.value = value;
    }
}