package name.haochenxie.jutilities.turing;

import fj.P;
import fj.P2;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@EqualsAndHashCode
abstract public class Symbolic {

    protected final Class<?> namespace;

    @Getter
    private final String name;

    private static Map<P2<Class<?>, String>, Symbolic> symbolTable = new ConcurrentHashMap<>();

    protected Symbolic(Class<?> namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    protected Symbolic(P2<Class<?>, String> sym) {
        this(sym._1(), sym._2());
    }

    protected static <T extends Symbolic> T of(Class<T> ns, String name,
                              Function<String, T> cons) {
        //noinspection unchecked
        return (T) symbolTable.computeIfAbsent(P.p(ns, name), p -> cons.apply(p._2()));
    }

}
