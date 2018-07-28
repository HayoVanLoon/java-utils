import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class Coins {

  private static int loops = 0;

  public static void main(String[] args) throws IOException {
    System.out.println("enter number of coins:");
    int num = readInteger();

    Set<Integer> coins = new TreeSet<>();
    for (int i = 0; i < num; i += 1) {
      System.out.println("enter coin " + (i + 1));
      coins.add(readInteger());
    }
    System.out.println("coins: " + coins);

    int target = -1;
    while (target != 0) {
      System.out.println("target (0 to quit):");
      target = readInteger();

      System.out.println("-------------");

      Stack<Integer> bag = findMinimalBag(coins, target);
      if (bag != null) {
        System.out.println(String.format(
            "(1) [%s] best:\t%s (%s)", loops, bag, bag.size()));
      } else {
        System.out.println(String.format(
            "(1) no solution after %s loops", loops));
      }

      Stack<Integer> bag2 = memoisedSearch(coins, target);
      if (bag2 != null) {
        System.out.println(String.format(
            "(2) [%s] best:\t%s (%s)", loops, bag2, bag2.size()));
      } else {
        System.out.println(String.format(
            "(2) no solution after %s loops", loops));
      }

      Stack<Integer> bag3 = dynamicSearch(coins, target);
      if (bag3 != null) {
        System.out.println(String.format(
            "(3) [%s] best:\t%s (%s)", loops, bag3, bag3.size()));
      } else {
        System.out.println(String.format(
            "(3) no solution after %s loops", loops));
      }

      Stack<Integer> bag4 = greedySearch(coins, target);
      if (bag4 != null) {
        System.out.println(String.format(
            "(4) [%s] best: %s (%s)", loops, bag4, bag4.size()));
      } else {
        System.out.println(String.format(
            "(4) no solution after %s loops", loops));
      }
    }
  }

  private static int readInteger() throws IOException {
    int acc = 0;
    int read = 0;
    while (read != 10) {
      read = System.in.read();
      int i = read - 48;
      if (0 <= i && i <= 9) acc = acc * 10 + i;
    }

    return acc;
  }

  private static Stack<Integer> findMinimalBag(Set<Integer> coins, int target) {
    TreeSet<Integer> desc = new TreeSet<>((x, y) -> y - x);
    desc.addAll(coins);

    boolean[] bits = new boolean[target + 1];

    LinkedList<Stack<Integer>> queue = new LinkedList<>();
    queue.add(new Stack<>());

    loops = 0;
    while (!queue.isEmpty()) {
      loops += 1;

      Stack<Integer> s = queue.poll();
      int acc = countStack(s);

      if (acc == target) {
        return s;
      } else {
        for (Integer i : desc) {
          if (acc + i <= target && !bits[acc + i]) {
            Stack<Integer> s2 = new Stack<>();
            s2.addAll(s);
            s2.add(i);
            queue.add(s2);
            bits[acc + i] = true;
          }
        }
      }
    }

    return null;
  }

  private static int countStack(Stack<Integer> s) {
    int acc = 0;
    int i;
    Stack<Integer> tmp = new Stack<>();
    while (!s.empty()) {
      i = s.pop();
      acc += i;
      tmp.push(i);
    }
    s.addAll(tmp);

    return acc;
  }

  @SuppressWarnings("unchecked")
  private static Stack<Integer> memoisedSearch(Set<Integer> coins, int target) {
    TreeSet<Integer> desc = new TreeSet<>((x, y) -> y - x);
    desc.addAll(coins);

    int[] parents = new int[target + 1];
    int[] amounts = new int[target + 1];
    for (int i = 0; i < parents.length; i += 1) {
      parents[i] = -1;
      amounts[i] = -1;
    }

    LinkedList<Integer> queue = new LinkedList<>();
    queue.add(0);
    amounts[0] = 0;

    loops = 0;
    while (!queue.isEmpty()) {
      loops += 1;

      int p = queue.poll();

      if (amounts[p] == target) {
        return constructFromParents(parents, target);
      } else {
        if (parents[target - amounts[p]] != -1) {
          Stack<Integer> s = constructFromParents(parents, target - amounts[p]);
          Stack<Integer> s2 = constructFromParents(parents, p);
          return combine(s, s2);
        } else {
          for (Integer coin : desc) {
            int w = amounts[p] + coin;
            if (w <= target && parents[p + coin] == -1) {
              parents[p + coin] = p;
              amounts[p + coin] = w;
              queue.add(p + coin);
            }
          }
        }
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  private static Stack<Integer> dynamicSearch(Set<Integer> coins, int target) {
    TreeSet<Integer> desc = new TreeSet<>((x, y) -> y - x);
    desc.addAll(coins);

    int[] parents = new int[target + 1];
    for (int i = 0; i < parents.length; i += 1) parents[i] = -1;

    loops = 0;
    for (int i = 0; i < parents.length; i += 1) {
      loops += 1;

      if (i == target && (parents[i] != -1 || target == 0)) {
        return constructFromParents(parents, target);
      } else if (i == 0 || parents[i] != -1) {
        for (Integer coin : desc) {
          if (i + coin <= target && parents[i + coin] == -1) {
            parents[i + coin] = i;
          }
        }
      }
    }

    return null;
  }

  private static Stack<Integer> constructFromParents(int[] parents,
                                                     int target) {
    Stack<Integer> s = new Stack<>();
    int p = target;
    while (p > 0) {
      s.push(p - parents[p]);
      p = parents[p];
    }
    return s;
  }

  private static Stack<Integer> greedySearch(Set<Integer> coins,
                                             int target) {
    TreeSet<Integer> desc = new TreeSet<>((x, y) -> y - x);
    desc.addAll(coins);
    loops = 0;
    return greedySearch2(desc, target, new Stack<>());
  }

  private static Stack<Integer> greedySearch2(Set<Integer> coins,
                                              int target,
                                              Stack<Integer> acc) {
    loops += 1;
    if (target == 0) {
      return acc;
    } else if (target > 0) {
      for (Integer coin : coins) {
        Stack<Integer> s = greedySearch2(coins, target - coin, pushCopy(acc, coin));
        if (s != null) {
          return s;
        }
      }
    }

    return null;
  }

  private static Stack<Integer> pushCopy(Stack<Integer> s, Integer i) {
    Stack<Integer> comb = new Stack<>();
    comb.addAll(s);
    comb.add(i);
    return comb;
  }

  private static Stack<Integer> combine(Stack<Integer> s1, Stack<Integer> s2) {
    Stack<Integer> comb = new Stack<>();
    comb.addAll(s1);
    comb.addAll(s2);
    return comb;
  }
}
