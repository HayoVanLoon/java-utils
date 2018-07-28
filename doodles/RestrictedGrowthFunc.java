public class RestrictedGrowthFunc {

  private static final int[] BELL = {1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147,
      115975, 678570};

  public static void main(String[] args) {
    String set = args[0];
    int[] ss = encode(new String[]{set}, set);

    // 1, 1, 2, 5, 15, 52, 203, 877, 4140, 21147, 115975, 678570
    for (int i = 0; i < BELL[set.length()]; i += 1) {
      print(ss);
      print(ss, set);
      setNext(ss);
    }
  }

  private static int[] encode(String[] xs, String set) {
    int[] ss = new int[set.length()];

    for (int i = 0; i < xs.length; i += 1) {
      char[] cs = xs[i].toCharArray();
      for (int j = 0; j < cs.length; j += 1) {
        ss[set.indexOf(cs[j])] = i;
      }
    }

    return ss;
  }

  private static void setNext(int[] ss) {
    int[] maxRadix = maxRadixArr(ss);
    boolean done = false;
    for (int i = ss.length - 1; !done && i >= 0; i -= 1) {
      if (ss[i] < maxRadix[i]) {
        ss[i] = ss[i] + 1;
        for (int j = i + 1; j < ss.length; j += 1) {
          ss[j] = 0;
        }
        done = true;
      }
    }
  }

  private static int[] maxRadixArr(int ss[]) {
    int[] xs = new int[ss.length];
    xs[0] = 0;
    for (int i = 1; i < ss.length; i += 1) {
      xs[i] = Math.min(xs[i - 1], ss[i - 1]) + 1;
    }
    return xs;
  }

  private static void print(int[] ss) {
    for (int s : ss) System.out.print(s);
    System.out.println();
  }

  private static void print(int[] ss, String set) {
    char[] cs = set.toCharArray();
    for (int i = 0; i < ss.length; i += 1) {
      boolean any = false;
      for (int j = 0; j < ss.length; j += 1) {
        if (ss[j] == i) {
          System.out.print(cs[j]);
          any = true;
        }
      }
      if (any) System.out.print(",");
    }
    System.out.println();
  }
}
