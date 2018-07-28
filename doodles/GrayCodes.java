public class GrayCodes {

  public static void main(String[] args) {
    String set = args[0];

    if (args.length > 1) {
      boolean[] bs = encode(args[1], set);
      setPrevGray(bs);
      printBs(bs, set);
    } else {
      for (int i = 0; i < Math.pow(2, set.length()); i += 1) {
        boolean bs[] = grayN(set, i);
        printBs(bs, set);
      }
    }
  }

  private static boolean[] grayN(String elems, int n) {
    boolean[] encoded = encode("", elems);

    for (int i = 0; i < n; i += 1) {
      setNextGray(encoded);
    }

    return encoded;
  }

  private static void setNextGray(boolean[] encoded) {
    int k = 0;
    for (boolean b : encoded) k += b ? 1 : 0;

    if (k % 2 == 0) {
      encoded[0] = !encoded[0];
    } else {
      for (int j = 0; j < encoded.length - 1; j += 1) {
        if (encoded[j]) {
          encoded[j + 1] = !encoded[j + 1];
          break;
        }
      }
    }
  }

  private static void setPrevGray(boolean[] encoded) {
    int k = 0;
    for (boolean b : encoded) k += b ? 1 : 0;

    if (k % 2 == 1) {
      encoded[0] = !encoded[0];
    } else {
      for (int j = 0; j < encoded.length - 1; j += 1) {
        if (encoded[j]) {
          encoded[j + 1] = !encoded[j + 1];
          break;
        }
      }
    }
  }

  private static boolean[] encode(String s, String elems) {
    char[] cs = elems.toCharArray();
    boolean[] bs = new boolean[elems.length()];
    for (int i = 0; i < cs.length; i += 1) {
      bs[i] = s.indexOf(cs[i]) > -1;
    }
    return bs;
  }

  private static void printBs(boolean[] bs) {
    for (boolean b : bs) System.out.print(b ? "1" : "0");
    System.out.println();
  }

  private static void printBs(boolean[] bs, String set) {
    char[] cs = set.toCharArray();
    for (int i = 0; i < bs.length; i += 1) System.out.print(bs[i] ? cs[i] : "");
    System.out.println();
  }
}

