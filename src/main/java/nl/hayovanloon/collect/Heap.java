package nl.hayovanloon.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * Basic priority queue returning the biggest item
 */
public class Heap implements Queue<Integer> {

  private static final int DEFAULT_SIZE = 4;

  private int[] data;
  private int next;

  public Heap() {
    data = new int[DEFAULT_SIZE];
    next = 0;
  }

  public boolean add(Integer i) {
    if (next == data.length) {
      enlarge();
    }
    data[next] = i;
    bubbleUp(next);
    next += 1;

    return true;
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException("not supported (yet?)");
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object o : c) {
      if (!contains(o)) return false;
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends Integer> c) {
    for (int i : c) {
      add(i);
    }
    return true;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException("not supported (yet?)");
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException("not supported (yet?)");
  }

  @Override
  public void clear() {
    // TODO: or reinitialise data array as well?
    next = 0;
  }

  public boolean offer(Integer i) {
    return add(i);
  }

  private void enlarge() {
    data = Arrays.copyOf(data, data.length * 2);
  }

  private void bubbleUp(int i) {
    if (i > 0) {
      int parent = parent(i);
      if (data[parent] < data[i]) {
        swap(parent, i);
        bubbleUp(parent);
      }
    }
  }

  public int[] children(int i) {
    if (i * 2 + 1 >= next) return new int[0];
    else if (i * 2 + 2 == next) return new int[]{i * 2 + 1};
    else return new int[]{i * 2 + 1, i * 2 + 2};
  }

  public int parent(int i) {
    return (i + i % 2) / 2 - 1;
  }

  public int depth(int i) {
    if (i < 0 || next <= i) return -1;
    else return (int) Math.floor(Math.log(i + 1) / Math.log(2));
  }

  public boolean hasNext() {
    return next > 0;
  }

  @Override
  public Integer poll() {
    // TODO: return null if empty
    final int popped = data[0];
    next -= 1;
    data[0] = data[next];
    bubbleDown(0);
    return popped;
  }

  @Override
  public Integer remove() {
    // TODO: throw exception if empty
    return poll();
  }

  @Override
  public Integer element() {
    // TODO: throw exception if empty
    return data[0];
  }

  @Override
  public Integer peek() {
    // TODO: return null if empty
    return data[0];
  }

  private void bubbleDown(int i) {
    for (int c : children(i)) {
      if (data[c] > data[i]) {
        swap(c, i);
        bubbleDown(c);
      }
    }
  }

  private void swap(int i, int j) {
    int tmp = data[i];
    data[i] = data[j];
    data[j] = tmp;
  }

  @Override
  public int size() {
    return next;
  }

  @Override
  public boolean isEmpty() {
    return next == 0;
  }

  @Override
  public boolean contains(Object o) {
    if (!(o instanceof Integer) || next == 0) {
      return false;
    } else {
      return contains((Integer)o, 0, next);
    }
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      int i = 0;
      @Override
      public boolean hasNext() {
        return i < next;
      }

      @Override
      public Integer next() {
        return data[i++];
      }
    };
  }

  @Override
  public Object[] toArray() {
    final Integer[] dest = new Integer[next];
    for (int i = 0; i < next; i += 1) {
      dest[i] = data[i];
    }
    return dest;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    if (a == null) {
      throw new NullPointerException("destination cannot be null");
    }

    if (a.getClass().getComponentType() != Integer.class) {
      // TODO fix for primitives
      throw new ArrayStoreException("destination is not a String[]");
    }

    if (a.length >= next) {
      for (int i = 0; i < next; i += 1) {
        a[i] = (T)new Integer(data[i]);
      }
      if (a.length > next) {
        a[next] = null;
      }
      return a;
    } else {
      return (T[])toArray();
    }
  }

  private boolean contains(int i, int lower, int upper) {
    int pivot = lower + (upper - lower) / 2;
    if (i == pivot) {
      return true;
    } else if (i < pivot) {
      return contains(i, lower, pivot);
    } else {
      return contains(i, pivot, upper);
    }
  }

  @Override
  public String toString() {
    return toString(0) + "\n" + Arrays.toString(data) + " (" + next + ")";
  }

  private String toString(int i) {
    final StringBuilder sb = new StringBuilder();
    sb.append("(").append(data[i]);
    for (int j : children(i)) {
      sb.append(toString(j));
    }
    sb.append(")");
    return sb.toString();
  }
}

