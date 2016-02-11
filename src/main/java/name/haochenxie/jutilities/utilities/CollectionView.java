package name.haochenxie.jutilities.utilities;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;

public class CollectionView<E, V> implements Iterable<V> {

    private Iterable<E> backend;

    private Supplier<Function<E, Optional<V>>> filterFactory;

    public CollectionView(Iterable<E> backend, Supplier<Function<E, Optional<V>>> filterFactory) {
        this.backend = backend;
        this.filterFactory = filterFactory;
    }

    public CollectionView(Iterable<E> backend, Function<E, Optional<V>> filter) {
        this(backend, () -> filter);
    }

    public static <E> CollectionView<E, E> viewOf(Iterable<E> collection) {
        return new CollectionView<>(collection, Optional::of);
    }

    public static <E> CollectionView<E, E> viewOf(E[] array) {
        return viewOf(Arrays.asList(array));
    }

    /**
     * @param end      exclusive
     */
    public static CollectionView<Integer, Integer> viewOfRange(int end) {
        return viewOfRange(0, end);
    }

    /**
     * @param start    inclusive
     * @param end      exclusive
     */
    public static CollectionView<Integer, Integer> viewOfRange(int start, int end) {
        return new CollectionView<>(() -> {
            return new Iterator<Integer>() {
                private int n = start;

                @Override
                public boolean hasNext() {
                    return n < end;
                }

                @Override
                public Integer next() {
                    return n++;
                }
            };
        }, Optional::of);
    }

    public <R> CollectionView<V, R> map(Function<V, R> f) {
        return new CollectionView<>(this, e -> Optional.of(f.apply(e)));
    }

    public CollectionView<V, V> filter(Predicate<V> f) {
        return new CollectionView<>(this, e -> f.test(e) ? Optional.of(e) : Optional.empty());
    }

    public <AccType> AccType fold(AccType acc, BiFunction<AccType, V, AccType> f) {
        for (V v : this) {
            acc = f.apply(acc, v);
        }
        return acc;
    }

    public CollectionView<V, V> unique() {
        return new CollectionView<>(this, () -> {
            Set<V> set = new HashSet<>();
            return (V v) -> {
                if (set.contains(v)) {
                    return Optional.empty();
                } else {
                    set.add(v);
                    return Optional.of(v);
                }
            };
        });
    }

    public CollectionView<V, V> sorted() {
        @SuppressWarnings("unchecked")
        Comparator<? super V> comp = (Comparator<? super V>) Comparator.naturalOrder();
        return sorted(comp);
    }

    public CollectionView<V, V> sorted(Comparator<? super V> comp) {
        List<V> list = toList();
        list.sort(comp);
        return viewOf(list);
    }

    public int count() {
        final int[] size = {0};
        this.act($ -> ++size[0]);
        return size[0];
    }

    public void act(Consumer<V> f) {
        for (V v : this) {
            f.accept(v);
        }
    }

    @SuppressWarnings("unchecked")
    public V[] toArray(Class<V[]> clazz) {
        List<V> list = toList();
        V[] arr = (V[]) Array.newInstance(clazz.getComponentType(), list.size());
        return list.toArray(arr);
    }

    public List<V> toList() {
        return Lists.newArrayList(iterator());
    }

    public Set<V> toSet() {
        return Sets.newHashSet(iterator());
    }

    @Override
    public Iterator<V> iterator() {
        Iterator<E> backendIterator = backend.iterator();

        return new Iterator<V>() {
            private Optional<V> buff = Optional.empty();
            private Function<E, Optional<V>> filter = filterFactory.get();

            @Override
            public boolean hasNext() {
                if (buff.isPresent()) {
                    return true;
                } else {
                    fillBuff();
                    return buff.isPresent();
                }
            }

            @Override
            public V next() {
                fillBuff();
                V ret = buff.get();
                buff = Optional.empty();
                return ret;
            }

            private void fillBuff() {
                while (!buff.isPresent() && backendIterator.hasNext()) {
                    buff = filter.apply(backendIterator.next());
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public String toString() {
        return toList().toString();
    }
}
