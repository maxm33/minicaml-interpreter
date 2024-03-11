package types;

public class Binding {
    public Iden name;
    public Expression value;

    public Binding(Iden name, Expression value) {
        this.name = name;
        this.value = value;
    }
}