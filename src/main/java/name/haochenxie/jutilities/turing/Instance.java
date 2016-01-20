package name.haochenxie.jutilities.turing;

import com.google.common.base.Joiner;
import fj.P;
import fj.P3;
import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
public class Instance {

    @Getter
    private Machine machine;

    @Getter
    private State state;

    private int head;

    @Getter
    private Tape tape;

    @Getter
    private boolean terminated;

    public void step() {
        P3<Symbol, Integer, State> next = machine.transition(state, getHead());
        Symbol newSym = next._1();
        Integer movement = next._2();
        State newState = next._3();

        if (machine.getTerminateStates().contains(newState)) {
            terminated = true;
        }

        tape.set(head, newSym);
        head += movement;
        state = newState;
    }

    public Symbol getHead() {
        return tape.get(head);
    }

    public void run() {
        while (!machine.getTerminateStates().contains(state)) {
            step();
        }
    }

    public void printStatus() {
        int min = tape.getEffectiveMin();
        int max = tape.getEffectiveMax();

        min = Math.min(min, head);
        max = Math.max(max, head);

        int hptr = 0;
        boolean hptrFlag = false;

        System.out.print("| "); hptr += 2;
        for (int i = min; i <= max; ++i) {
            String name = Optional.ofNullable(tape.get(i)).map(Symbol::getName).orElse("ε");

            System.out.print(name);
            System.out.print(" | ");

            hptrFlag |= i == head;

            if (!hptrFlag) hptr += name.length() + 3;
            if (i == head) hptr += name.length() / 2;
        }
        System.out.println();

        System.out.print(Joiner.on("").join(Collections.nCopies(hptr, ' ')));
        System.out.print('^');
        System.out.println();

        System.out.printf("State: %s", state.getName());
        System.out.println(isTerminated() ? " (Terminated)" : "");
    }

    public static void main(String[] args) throws IOException {
        StaticMachineBase machine = new StaticMachineBase() {

            {
                setStates(Arrays.asList("T", "i").stream().map(State::of).collect(Collectors.toSet()));
                setAlphabet(Arrays.asList("0", "1").stream().map(Symbol::of).collect(Collectors.toSet()));
                setInitialState(State.of("i"));
                setTerminateStates(Collections.singleton(State.of("T")));
            }

            int k = 5;
            int t = 6;

            @Override
            public P3<Symbol, Integer, State> transition(State state, Symbol sym) {
                if (--t < 0) {
                    return P.p(null, 0, State.of("T"));
                }

                if (k-- > 0) {
                    return P.p(Symbol.of("0"), +1, State.of("i"));
                } else {
                    return P.p(Symbol.of("1"), +1, State.of("i"));
                }
            }
        };

        Instance instance = Instance.builder()
                .machine(machine)
                .head(0)
                .state(machine.getInitialState())
                .tape(Tape.emptyTape())
                .build();

        instance.printStatus();

        while (!instance.isTerminated()) {
            instance.step();
            instance.printStatus();
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
            System.out.println("=>");
        }
    }

}
