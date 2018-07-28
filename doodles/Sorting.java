public class Sorting {

  private static int counter = 0;

  public static void main(String[] args) {

    int[] xs = {3, 4, 5, 6, 7, 8, 9, 10};
    //int[] xs = {10, 9, 8, 7, 6, 5, 4, 3};

    int[] ys = mergeSort(xs);
    System.out.println(counter + ": ");
    for (int y : ys) System.out.print(y + "\t");
    System.out.println();

    ys = bubbleSort(xs);
    System.out.println(counter + ": ");
    for (int y : ys) System.out.print(y + "\t");
    System.out.println();

    ys = quickSort(xs);
    System.out.println(counter + ": ");
    for (int y : ys) System.out.print(y + "\t");
    System.out.println();
  }

  private static int[] quickSort(int[] xs) {
    counter = 0;
    return quickSort2(xs, 0, xs.length);
  }

  private static int[] quickSort2(int[] xs, int l, int r) {
    if (r - l <= 1) {
      return xs;
    } else {
      int pivot = l + (int) (Math.random() * (r - l - 1));
      int hi = l;
      for (int i = l; i < r; i += 1) {
        counter += 1;
        if (xs[i] < pivot) {
          int tmp = xs[hi];
          xs[hi] = xs[i];
          xs[i] = tmp;
          hi += 1;
        }
      }
      xs[hi] = pivot;

      xs = quickSort2(xs, l, hi - 1);
      xs = quickSort2(xs, hi + 1, r);

      return xs;
    }
  }

  private static int[] bubbleSort(int[] xs) {
    counter = 0;
    for (int j = 0; j < xs.length; j += 1) {
      for (int i = 1; i < xs.length; i += 1) {
        counter += 1;
        if (xs[i] < xs[i - 1]) {
          int tmp = xs[i];
          xs[i] = xs[i - 1];
          xs[i - 1] = tmp;
        }
      }
    }

    return xs;
  }

  private static int[] mergeSort(int[] xs) {
    counter = 0;
    return mergeSort(xs, 0, xs.length);
  }

  private static int[] mergeSort(int[] xs, int l, int r) {
    if (r - l == 1) {
      return new int[]{xs[l]};
    } else {
      int mid = l + (r - l) / 2;
      return merge(
          mergeSort(xs, l, mid),
          mergeSort(xs, mid, r));
    }
  }

  private static int[] merge(int[] xs, int[] ys) {
    int[] zs = new int[xs.length + ys.length];

    int x = 0;
    int y = 0;

    for (int i = 0; i < zs.length; i += 1) {
      counter += 1;
      if (y == ys.length || x < xs.length && xs[x] <= ys[y]) {
        zs[i] = xs[x];
        x += 1;
      } else {
        zs[i] = ys[y];
        y += 1;
      }
    }

    return zs;
  }
}

