package name.haochenxie.jutilities.turing;

import fj.P3;
import lombok.Data;

import java.util.Set;

@Data
abstract public class StaticMachineBase implements Machine {

    private Set<State> states;

    private Set<Symbol> alphabet;

    private Set<State> terminateStates;

    private State initialState;

    @Override
    abstract public P3<Symbol, Integer, State> transition(State state, Symbol sym);

}
