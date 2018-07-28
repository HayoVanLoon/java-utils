import java.io.IOException;

public class EditDistance {

  public static void main(String[] args) throws IOException {

    char[] left = (" " + readLine()).toCharArray();
    char[] right = (" " + readLine()).toCharArray();

    int[][] costs = new int[left.length][];
    int[][] parents = new int[left.length][];
    for (int i = 0; i < left.length; i += 1) {
      costs[i] = new int[right.length];
      parents[i] = new int[right.length];
      costs[i][0] = i;
      parents[i][0] = 2;
    }
    for (int i = 0; i < right.length; i += 1) {
      costs[0][i] = i;
      parents[0][i] = 1;
    }
    parents[0][0] = -1;


    for (int i = 1; i < left.length; i += 1) {
      for (int j = 1; j < right.length; j += 1) {
        int[] ops = new int[3];
        ops[0] = costs[i - 1][j - 1] + matSub(left[i], right[j]);
        ops[1] = costs[i][j - 1] + insDel(right[j]);
        ops[2] = costs[i - 1][j] + insDel(left[i]);

        costs[i][j] = ops[0];
        parents[i][j] = 0;
        for (int k = 1; k < ops.length; k += 1) {
          if (ops[k] < costs[i][j]) {
            parents[i][j] = k;
            costs[i][j] = ops[k];
          }
        }
      }
    }

    System.out.println("cost matrix");
    System.out.print("\t");
    for (int i = 0; i < right.length; i += 1) {
      System.out.print(right[i] + "\t");
    }
    System.out.println();
    for (int i = 0; i < left.length; i += 1) {
      System.out.print(left[i] + "\t");
      for (int j = 0; j < right.length; j += 1) {
        System.out.print(costs[i][j] + "\t");
      }
      System.out.println();
    }

    System.out.println();

    System.out.println("parent matrix");
    for (int i = 0; i < left.length; i += 1) {
      for (int j = 0; j < right.length; j += 1) {
        System.out.print(parents[i][j] + "\t");
      }
      System.out.println();
    }

    System.out.println();

    String t = trace(parents, left, right, left.length - 1, right.length - 1,
        "");
    System.out.println(t);
  }

  private static int matSub(char l, char r) {
    return l == r ? 0 : 1;
  }

  private static int insDel(char c) {
    return 1;
  }

  private static String trace(int[][] parents,
                              char[] left,
                              char[] right,
                              int x,
                              int y,
                              String acc) {
    switch (parents[x][y]) {
      case -1:
        return acc;
      case 0:
        return trace(parents, left, right, x - 1, y - 1,
            (left[x] == right[y] ? 'm' : 'S') + acc);
      case 1:
        return trace(parents, left, right, x, y - 1, "I" + acc);
      case 2:
        return trace(parents, left, right, x - 1, y, "D" + acc);
      default:
        throw new IllegalArgumentException("unknown op: " + parents[x][y]);
    }
  }

  private static String readLine() throws IOException {
    StringBuilder sb = new StringBuilder();

    int read;
    do {
      read = System.in.read();
      if (read != '\n') sb.append((char) (read));
    } while (read != '\n');

    return sb.toString();
  }
}
