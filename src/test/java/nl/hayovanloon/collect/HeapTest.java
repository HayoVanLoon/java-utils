package nl.hayovanloon.collect;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HeapTest {

  @Test
  public void simple() {
    final Heap heap = new Heap();

    for (int i = 0; i < 16; i += 1) {
      heap.add((int)(Math.random() * 99));
    }

    int i = heap.poll();
    while(heap.hasNext()) {
      int j = heap.poll();
      Assert.assertTrue(j <= i);
      i = j;
    }
  }

  @Test
  public void toArray() {
    Assert.assertArrayEquals(new Object[0], new Heap().toArray());

    final Heap heap = new Heap();
    heap.add(9);
    heap.add(10);

    final Object[] os = heap.toArray();
    Assert.assertArrayEquals(new Object[]{10, 9}, os);
  }

  @Test
  public void toArray2() {
    Assert.assertArrayEquals(new Object[0], new Heap().toArray(new Integer[0]));

    final Heap heap = new Heap();
    heap.add(9);
    heap.add(10);

    final Object[] os = heap.toArray();
    Assert.assertArrayEquals(new Object[]{10, 9}, os);
  }
}
