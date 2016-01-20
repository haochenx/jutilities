package name.haochenxie.jutilities.turing;

import fj.P3;

import java.util.Set;

public interface Machine {

    public Set<State> getStates();

    public Set<Symbol> getAlphabet();

    public Set<State> getTerminateStates();

    public State getInitialState();

    public P3<Symbol, Integer, State> transition(State state, Symbol sym);

}
