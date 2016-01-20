package name.haochenxie.jutilities.turing;

public class Symbol extends Symbolic {

    public Symbol(String name) {
        super(Symbol.class, name);
    }

    public static Symbol of(String name) {
        return of(Symbol.class, name, Symbol::new);
    }

}
