package name.haochenxie.jutilities.turing;

import fj.F2;
import fj.P;
import fj.P2;
import fj.P3;

import java.util.*;
import java.util.function.Supplier;

public class TransitionBuilder {

    private List<F2<State, Symbol, Optional<P3<Symbol, Integer, State>>>> transitions;

    private Map<P2<State, Symbol>, P3<Symbol, Integer, State>> transitionTable;

    public TransitionBuilder() {
        this.transitions = new ArrayList<>();
        this.transitionTable = new Hashtable<>();
    }

    public TransitionBuilder transit(F2<State, Symbol, Optional<P3<Symbol, Integer, State>>> f) {
        transitions.add(f);
        return this;
    }

    public TransitionBuilder transit(State k0, Symbol s0, int mvt, Symbol s1, State k1) {
        transitionTable.put(P.p(k0, s0), P.p(s1, mvt, k1));
        return this;
    }

    public TransitionBuilder transit(Supplier<State> k0, Supplier<Symbol> s0,
                                     int mvt, Supplier<Symbol> s1, Supplier<State> k1) {


        transitionTable.put(P.p(k0.get(), s0.get()), P.p(s1.get(), mvt, k1.get()));
        return this;
    }

    public F2<State, Symbol, Optional<P3<Symbol, Integer, State>>> build() {
        return (k0, s0) -> {
            if (transitionTable.containsKey(P.p(k0, s0))) {
                return Optional.of(transitionTable.get(P.p(k0, s0)));
            } else {
                for (F2<State, Symbol, Optional<P3<Symbol, Integer, State>>> transition : transitions) {
                    Optional<P3<Symbol, Integer, State>> r = transition.f(k0, s0);
                    if (r.isPresent()) {
                        return r;
                    }
                }
                return Optional.empty();
            }
        };
    }

}
