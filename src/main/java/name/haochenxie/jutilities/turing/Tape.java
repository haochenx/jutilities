package name.haochenxie.jutilities.turing;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tape {

    private ArrayList<Symbol> positive;
    private ArrayList<Symbol> negative;

    @Getter
    private int min = 0;

    @Getter
    private int max = 0;

    protected Tape() {
        this.positive = new ArrayList<>();
        this.negative = new ArrayList<>();
    }

    public static Tape emptyTape() {
        return new Tape();
    }

    public Symbol get(int idx) {
        updateMinMax(idx);
        return (idx < 0)
                ? get(negative, 1 - idx)
                : get(positive, idx);
    }

    public Symbol set(int idx, Symbol s) {
        return (idx < 0)
                ? set(negative, 1 - idx, s)
                : set(positive, idx, s);
    }

    // TODO optimization
    public int getEffectiveMax() {
        int m = max;
        while (get(m) == null && m > min) --m;
        return m > min ? m : 0;
    }

    // TODO optimization
    public int getEffectiveMin() {
        int m = min;
        while (get(m) == null && m < max) ++m;
        return m < max ? m : 0;
    }

    public void updateMinMax(int idx) {
        min = Math.min(idx, min);
        max = Math.max(idx, max);
    }

    private static Symbol get(ArrayList<Symbol> list, int idx) {
        return ensureSize(list, idx + 1).get(idx);
    }

    private static Symbol set(ArrayList<Symbol> list, int idx, Symbol s) {
         return ensureSize(list, idx + 1).set(idx, s);
    }

    private static <T> List<T> ensureSize(ArrayList<T> list, int size) {
        if (list.size() < size) {
            list.addAll(Collections.<T>nCopies(size - list.size(), null));
        }
        return list;
    }

}
