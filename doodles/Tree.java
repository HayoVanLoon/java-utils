import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

public abstract class Tree {
  private static final int LEAF_PRINT_WIDTH = 5;

  public static int counter = 0;
  public static boolean trace = false;

  final int value;

  Tree(int value) {
    this.value = value;
  }

  static Tree leaf(int value) {
    return new Leaf(value);
  }

  static Tree node(int value, Tree left, Tree right) {
    return new Node(value,
        left == null ? null : left.copy(),
        right == null ? null : right.copy());
  }

  private static String center(int i, int j) {
    return center("" + i, j);
  }

  private static String center(String s, int i) {
    if (s.length() > i) {
      throw new IllegalArgumentException("String too long: " + s.length());
    }

    final int padding = i - s.length();
    int left = (padding / 2) + padding % 2;
    int right = padding / 2;

    final StringBuilder sb = new StringBuilder();

    while (left > 0) {
      sb.append(' ');
      left -= 1;
    }

    sb.append(s);

    while (right > 0) {
      sb.append(' ');
      right -= 1;
    }

    return sb.toString();
  }

  abstract Tree add(int value);

  abstract Tree addAll(Tree tree);

  abstract Tree copy();

  abstract int depth();

  abstract Tree balance();

  public abstract String toString(String offset);

  public abstract String print();

  public abstract boolean dfs(int i);

  public abstract boolean bfs(int i);

  public abstract boolean bins(int i);

  static class Leaf extends Tree {
    Leaf(int value) {
      super(value);
    }

    @Override
    Tree add(int value) {
      if (value <= this.value) {
        return new Node(this.value, new Leaf(value), null);
      } else {
        return new Node(this.value, null, new Leaf(value));
      }
    }

    @Override
    Tree copy() {
      return new Leaf(value);
    }

    @Override
    Tree addAll(Tree tree) {
      return tree == null ? this : tree.add(this.value);
    }

    @Override
    int depth() {
      return 0;
    }

    @Override
    Tree balance() {
      return this;
    }

    @Override
    public String toString(String offset) {
      return offset + "leaf(" + value + ")";
    }

    @Override
    public String print() {
      return Tree.center(value, LEAF_PRINT_WIDTH);
    }

    @Override
    public boolean dfs(int i) {
      if (trace) System.out.println(value);
      counter += 1;

      return value == i;
    }

    @Override
    public boolean bfs(int i) {
      if (trace) System.out.println(value);
      counter += 1;

      return value == i;
    }

    @Override
    public boolean bins(int i) {
      if (trace) System.out.println(value);
      counter += 1;

      return value == i;
    }
  }

  static class Node extends Tree {
    final Tree left;
    final Tree right;

    Node(int value, Tree left, Tree right) {
      super(value);
      if (left == null && right == null) {
        throw new IllegalArgumentException();
      }
      this.left = left;
      this.right = right;
    }

    static Node balance2(Node node) {
      Node newNode = (Node) node(node.value,
          node.left == null ? null : node.left.balance(),
          node.right == null ? null : node.right.balance());

      int dL = newNode.depthLeft();
      int dR = newNode.depthRight();

      if (dL - dR > 1) {
        return shiftLeft(newNode);
      } else if (dL - dR < -1) {
        return shiftRight(newNode);
      } else {
        return newNode;
      }
    }

    static Node shiftLeft(Node tree) {
      if (tree.left instanceof Leaf) {
        return (Node) node(tree.left.value, null, tree.right);
      } else if (tree.left instanceof Node) {
        Node left = (Node) tree.left;

        Tree newR = (left.right == null && tree.right == null)
            ? leaf(tree.value)
            : node(tree.value, left.right, tree.right);

        return (Node) node(left.value, left.left, newR).balance();
      } else {
        throw new IllegalArgumentException();
      }
    }

    static Node shiftRight(Node tree) {
      if (tree.right instanceof Leaf) {
        return (Node) node(tree.right.value, tree.left, null);
      } else if (tree.right instanceof Node) {
        Node right = (Node) tree.right;

        Tree newL = (right.left == null && tree.left == null)
            ? leaf(tree.value)
            : node(tree.value, tree.left, right.left).balance();

        return (Node) node(right.value, newL, right.right);
      } else {
        throw new IllegalArgumentException();
      }
    }

    private static List<String> getSub(Tree t) {
      if (t instanceof Leaf) {
        return Collections.singletonList(t.print());
      } else if (t instanceof Node) {
        return ((Node) t).print2();
      } else {
        return Collections.singletonList(center("nil", LEAF_PRINT_WIDTH));
      }
    }

    @Override
    Tree add(int value) {
      if (value == this.value) {
        return this;
      } else {
        if (value < this.value) {
          final Tree newLeft = left == null ? new Leaf(value) : left.add(value);
          return new Node(this.value, newLeft, right);
        } else {
          final Tree newRight = right == null ? new Leaf(value) : right.add(value);
          return new Node(this.value, left, newRight);
        }
      }
    }

    @Override
    Tree copy() {
      return new Node(value,
          left == null ? null : left.copy(),
          right == null ? null : right.copy());
    }

    @Override
    Tree addAll(Tree tree) {
      if (tree instanceof Leaf) {
        return this.add(tree.value);
      } else if (tree instanceof Node) {
        final Node node = (Node) tree;
        if (value < node.value) {
          return new Node(value,
              left,
              right == null ? node.right : right.addAll(node)
          );
        } else if (value > node.value) {
          return new Node(value,
              left == null ? node.left : left.addAll(node),
              right
          );
        } else {
          return new Node(value,
              left == null ? node.left : left.addAll(node.left),
              right == null ? node.right : right.addAll(node.right)
          );
        }
      } else {
        return this;
      }
    }

    @Override
    int depth() {
      if (left == null) {
        return 1 + right.depth();
      } else if (right == null) {
        return 1 + left.depth();
      } else {
        return 1 + Math.max(left.depth(), right.depth());
      }
    }

    @Override
    Tree balance() {
      int diff;
      Node acc = (Node) copy();
      do {
        acc = balance2(acc);
        diff = acc.depthLeft() - acc.depthRight();
      } while (Math.abs(diff) > 2);
      return acc;
    }

    int depthLeft() {
      return left == null ? 0 : 1 + left.depth();
    }

    int depthRight() {
      return right == null ? 0 : 1 + right.depth();
    }

    @Override
    public String toString() {
      return toString("");
    }

    public String toString(String offset) {
      String sb = offset +
          "node(" +
          value +
          ",\n" +
          (left == null
              ? offset + "\t" + "null" : left.toString(offset + "\t")) +
          ",\n" +
          (right == null
              ? offset + "\t" + "null" : right.toString(offset + "\t")) +
          ")";

      return sb;
    }

    public String print() {
      final StringBuilder sb = new StringBuilder();
      for (String s : print2()) {
        sb.append(s).append('\n');
      }
      return sb.toString();
    }

    @Override
    public boolean dfs(int i) {
      counter += 1;
      return value == i
          || (left != null && left.dfs(i)) || (right != null && right.dfs(i));
    }

    @Override
    public boolean bfs(int i) {
      if (trace) System.out.println(value);
      counter += 1;

      Queue<Tree> queue = new LinkedList<>();
      Tree t = this;
      while (t != null) {
        if (t.value == i) return true;
        else if (t instanceof Leaf && t.bfs(i)) return true;
        else if (t instanceof Node) {
          Node n = (Node) t;

          if (trace) System.out.println(n.value);
          counter += 1;

          if (n.left != null) queue.add(n.left);
          if (n.right != null) queue.add(n.right);
        }

        t = queue.poll();
      }

      return false;
    }

    @Override
    public boolean bins(int i) {
      if (trace) System.out.println(value);
      counter += 1;

      if (value == i) return true;
      else if (value > i) return left.bins(i);
      else return right.bins(i);
    }

    private List<String> print2() {
      final ListIterator<String> liter = getSub(left).listIterator();
      final ListIterator<String> riter = getSub(right).listIterator();

      final List<String> acc = new ArrayList<>();
      int widthL = -1;
      int widthR = -1;
      while (liter.hasNext() || riter.hasNext()) {
        String acc2 = "";
        final String l;
        if (liter.hasNext()) {
          l = liter.next();
          widthL = l.length();
          acc2 += l + "|";
        } else {
          acc2 += center("", widthL) + " ";
        }


        final String r;
        if (riter.hasNext()) {
          r = riter.next();
          widthR = r.length();
          acc2 += r;
        } else {
          acc2 += center("", widthR);
        }

        acc.add(acc2);
      }

      acc.add(0, Tree.center("", 1 + widthL - ("" + value).length()) + value + Tree.center("", widthR));
      return acc;
    }
  }
}
