package name.haochenxie.jutilities.utilities;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static name.haochenxie.jutilities.utilities.CollectionView.viewOf;
import static name.haochenxie.jutilities.utilities.CollectionView.viewOfRange;
import static org.junit.Assert.*;

public class CollectionViewTest {

    @Test
    public void test() {
        List<Integer> sample = Arrays.asList(1, 3, 1, 9, 7);
        Integer[] sampleArray = sample.toArray(new Integer[sample.size()]);

        assertArrayEquals(sampleArray,
                viewOf(sample).toArray(Integer[].class));

        assertEquals(sample,
                viewOf(sampleArray).toList());

        assertEquals(Arrays.asList(1, 3, 1),
                viewOf(sample).filter(i -> i < 7).toList());

        assertEquals(Arrays.asList("3", "9", "3", "27", "21"),
                viewOf(sample).map(i -> String.format("%d", i * 3)).toList());

        assertEquals(Arrays.asList(0, 1, 2, 3, 4),
                viewOfRange(0, 5).toList());

        assertEquals(5, viewOf(sample).count());

        assertEquals(Arrays.asList(1, 1, 3, 7, 9),
                viewOf(sample).sorted().toList());

        assertEquals(Arrays.asList(1, 3, 9, 7),
                viewOf(sample).unique().toList());

        assertEquals((Integer) 21,
                viewOf(sample).fold(0, (acc, x) -> acc + x));

        assertEquals((Integer) 189,
                viewOf(sample).fold(1, (acc, x) -> acc * x));

        viewOf(sample)
                .unique()
                .filter(i -> i < 7)
                .act(System.out::println);

        Random rng = new Random();
        viewOfRange(100)
                .map($->rng.nextDouble())
                .act(System.out::println);
    }

}