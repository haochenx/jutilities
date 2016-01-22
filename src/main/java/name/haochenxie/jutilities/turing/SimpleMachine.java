package name.haochenxie.jutilities.turing;

import fj.F2;
import fj.P;
import fj.P3;
import lombok.Builder;
import lombok.Singular;

import java.util.Optional;
import java.util.Set;

public class SimpleMachine extends StaticMachineBase {

    private F2<State, Symbol, Optional<P3<Symbol, Integer, State>>> transition;

    @Builder
    public SimpleMachine(Set<State> states, Set<Symbol> alphabet, @Singular Set<State> terminateStates,
                         State initialState,
                         F2<State, Symbol, Optional<P3<Symbol, Integer, State>>> transition) {
        super(states, alphabet, terminateStates, initialState);
        this.transition = transition;
    }

    @Override
    public P3<Symbol, Integer, State> transition(State state, Symbol sym) {
        return transition.f(state, sym).orElseGet(() -> {
            System.err.println("Unable to find transition definition: " + P.p(state, sym));
            return P.p(sym, 0, state);
        });
    }

}
