package nl.hayovanloon.collect;

import java.util.Arrays;

// TODO: move test cases to tests
public class Heap {

  private static final int DEFAULT_SIZE = 4;

  private int[] data;
  private int next;

  public Heap() {
    data = new int[DEFAULT_SIZE];
    next = 0;
  }

  public void add(int i) {
    if (next == data.length) {
      enlarge();
    }
    data[next] = i;
    bubbleUp(next);
    next += 1;
  }

  public void enlarge() {
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

  public int next() {
    final int popped = data[0];
    next -= 1;
    data[0] = data[next];
    bubbleDown(0);
    return popped;
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

  public static void main(String[] args) {
    Heap tree = new Heap();
  
    for (int i = 0; i < 16; i += 1) {
      tree.add((int)(Math.random() * 99));
    }

    System.out.println(tree);

    while(tree.hasNext()) {
      System.out.print(tree.next() + ", ");
      System.out.println(tree);
    }
  }
}

