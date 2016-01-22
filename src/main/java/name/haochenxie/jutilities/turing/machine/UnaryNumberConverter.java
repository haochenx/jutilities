package name.haochenxie.jutilities.turing.machine;

import fj.F2;
import fj.P3;
import name.haochenxie.jutilities.turing.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class UnaryNumberConverter {

    public enum S implements Supplier<Symbol> {

        B, c0, c1, D, E;

        public Symbol get() {
            return Symbol.of(name());
        }

        public static Set<Symbol> toSet() {
            return Arrays.stream(S.values())
                    .map(S::get)
                    .collect(Collectors.toSet());
        }

    }

    public enum K implements Supplier<State> {

        RD, A, RT, HT;

        public State get() {
            return State.of(name());
        }

        public static Set<State> toSet() {
            return Arrays.stream(K.values())
                    .map(K::get)
                    .collect(Collectors.toSet());
        }

    }

    public static Machine createMachine() {
        F2<State, Symbol, Optional<P3<Symbol, Integer, State>>> transition = new TransitionBuilder()
                .transit(K.RD, S.B, +1, S.B, K.HT)
                .transit(K.RD, S.D, +1, S.D, K.RD)
                .transit(K.RD, S.E, +1, S.E, K.RD)
                .transit(K.RD, S.c1, -1, S.E, K.A)

                .transit(K.A, S.B, +1, S.c1, K.RT)
                .transit(K.A, S.D, -1, S.D, K.A)
                .transit(K.A, S.E, -1, S.E, K.A)
                .transit(K.A, S.c0, +1, S.c1, K.RT)
                .transit(K.A, S.c1, -1, S.c0, K.A)

                .transit(K.RT, S.D, +1, S.D, K.RD)
                .transit(K.RT, S.c0, +1, S.c0, K.RT)
                .transit(K.RT, S.c1, +1, S.c1, K.RT)
                .build();

        SimpleMachine machine = SimpleMachine.builder()
                .alphabet(S.toSet())
                .states(K.toSet())
                .terminateState(K.HT.get())
                .initialState(K.RD.get())
                .transition(transition)
                .build();

        return machine;
    }

    public static Tape createTape(int x) {
        Tape tape = new Tape();
        for (int i = 0; i < x; ++i) {
            tape.set(i, S.c1.get());
        }
        tape.set(-2, S.B.get());
        tape.set(-1, S.D.get());
        tape.set(x, S.B.get());

        return tape;
    }

    public static void main(String[] args) throws IOException {
        Tape tape = createTape(3);
        Machine machine = createMachine();

        Instance instance = Instance.builder()
                .machine(machine)
                .tape(tape)
                .head(0)
                .state(machine.getInitialState())
                .build();

        while (!instance.isTerminated()) {
            instance.step();
            instance.printStatus();
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
            System.out.println("=>");
        }
    }

}
