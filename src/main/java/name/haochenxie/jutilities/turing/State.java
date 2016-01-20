package name.haochenxie.jutilities.turing;

public class State extends Symbolic {

    protected State(String name) {
        super(State.class, name);
    }

    public static State of(String name) {
        return of(State.class, name, State::new);
    }

}
