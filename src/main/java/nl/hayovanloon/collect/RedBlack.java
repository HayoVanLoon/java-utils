package nl.hayovanloon.collect;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

// TODO: move test cases to tests
public class RedBlack<T> implements Collection<T> {

  private Comparator<T> comparator;
  private Inner inner;

  public RedBlack(Comparator<T> comparator) {
    this.comparator = comparator;
    inner = null;
  }

  public RedBlack() {
    this.comparator = null;
    inner = null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean add(T value) {
    if (inner == null) {
      if (comparator == null) {
        if (!Comparable.class.isInstance(value)) {
          throw new IllegalArgumentException(String.format(
              "class %s is not Comparable", value.getClass().getSimpleName()));
        } else {
          comparator = (x, y) -> ((Comparable<T>)x).compareTo(y);
        }
      }
      inner = new Inner(value, true, null, null, null);
    } else {
      inner = inner.add(value);
    }
    return true;
  }

  private Inner newNode(T value, Inner parent) {
    return new Inner(value, false, parent, null, null);
  }

  private boolean isRed(Inner t) {
    return t != null && !t.black;
  }

  private boolean isBlack(Inner t) {
    return t == null || t.black;
  }

  private void paintRed(Inner t) {
    if (t != null) t.black = false;
  }

  private void paintBlack(Inner t) {
    if (t != null) t.black = true;
  }

  private void swapColours(Inner a, Inner b) {
    final boolean tmp = isBlack(a);

    if (isBlack(b)) paintBlack(a);
    else paintRed(a);

    if (tmp) paintBlack(b);
    else paintRed(b);
  }

  @Override
  public int size() {
    return inner == null ? 0 : inner.size();
  }

  @Override
  public boolean isEmpty() {
    return inner == null;
  }

  @Override
  public boolean contains(Object o) {
    return inner != null && inner.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      Inner current;
      Inner candidate;

      @Override
      public boolean hasNext() {
        if (inner == null) {
          return false;
        } else {
          if (candidate == null) {
            candidate = current == null ? inner.lowest() : current.next();
          }
          return candidate != null;
        }
      }

      @Override
      public T next() {
        if (candidate == null) {
          current = current == null ? inner.lowest() : current.next();
        } else {
          current = candidate;
          candidate = null;
        }
        if (current == null) {
          throw new NoSuchElementException();
        } else {
          return current.value;
        }
      }
    };
  }

  @Override
  public Object[] toArray() {
    final Object[] array = new Object[size()];
    final Iterator<T> iter = iterator();
    for (int i = 0; iter.hasNext(); i +=1 ) {
      array[i] = iter.next();
    }
    return array;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> U[] toArray(U[] array) {
    final int size = size();
    if (size == 0) {
      // skips type checking
      return array;
    } else {
      final Class<?> clazz = array.getClass().getComponentType();

      final U[] fitted;
      if (array.length >= size) {
        fitted = array;
      } else {
        fitted = (U[])Array.newInstance(clazz, size);
      }

      final Iterator<T> iter = iterator();
      T first = iter.next();
      if (!clazz.isAssignableFrom(first.getClass())) {
        throw new ArrayStoreException();
      }

      fitted[0] = (U)first;
      for (int i = 1; iter.hasNext(); i +=1 ) {
        fitted[i] = (U)iter.next();
      }
      return fitted;
    }
  }

  @Override
  public boolean remove(Object o) {
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> cs) {
    if (inner == null) return false;
    for (Object c : cs) {
      if (!inner.contains(c)) return false;
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends T> cs) {
    boolean added = !cs.isEmpty();
    for (T c : cs) {
      added = added && add(c);
    }
    return added;
  }

  @Override
  public boolean removeAll(Collection<?> cs) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> cs) {
    return false;
  }

  @Override
  public void clear() {
    inner = null;
  }

  @Override
  public String toString() {
    return inner == null ? "{}" : inner.toString();
  }


  private class Inner {

    private T value;
    private Inner parent;
    private Inner left;
    private Inner right;
    private boolean black;

    private Inner(T value, boolean black, Inner parent, Inner left, Inner right) {
      this.value = value;
      this.parent = parent;
      this.left = left;
      this.right = right;
      this.black = black;
    }

    private boolean isRoot() {
      return parent == null;
    }

    private Inner getRoot() {
      return parent == null ? this : parent.getRoot();
    }

    private Inner add(T value) {
      if (comparator.compare(value, this.value) < 0) {
        if (left == null) {
          left = newNode(value, this);
          left.restore();
        } else {
          left.add(value);
        }
      } else if (comparator.compare(value, this.value) > 0) {
        if (right == null) {
          right = newNode(value, this);
          right.restore();
        } else {
          right.add(value);
        }
      }

      return getRoot();
    }

    private void restore() {
      System.out.println("Restore " + value);
      if (parent == null) {
        // is root: recolour

        System.out.println("root recolour");
        paintBlack(this);
      } else if (isRed(parent)) {
        Inner uncle = getUncle();
        if (isRed(uncle)) {
          // parent & uncle red: recolour
          System.out.println("recolour p & u");

          paintBlack(parent);
          paintBlack(uncle);
          paintRed(parent.parent);
          parent.parent.restore();
        } else {
          // parent red, uncle black

          final Inner p = this.parent;
          final Inner g = p.parent;
          if (isLeftLeft()) {
            System.out.println("LL");
            // zig g
            g.rotateRight();
            // swap colours of gp and p
            swapColours(g, p);
          } else if (isLeftRight()) {
            System.out.println("LR");
            // zag p
            p.rotateLeft();
            // zig g
            g.rotateRight();
            // swap colours of g and p
            swapColours(g, this);
          } else if (isRightRight()) {
            System.out.println("RR");
            // zag g
            g.rotateLeft();
            // swap colours of g and p
            swapColours(g, p);
          } else { // if (isRightLeft()) {
            System.out.println("RL");
            // zig p
            p.rotateLeft();
            // zag g
            g.rotateRight();
            // swap colours of g and p
            swapColours(g, this);
          }
        }
      }
    }

    private boolean isLeft(Inner t) {
      return left == t;
    }

    private boolean isRight(Inner t) {
      return right == t;
    }

    private boolean isLeftLeft() {
      return parent.isLeft(this) && parent.parent.isLeft(parent);
    }

    private boolean isLeftRight() {
      return parent.isRight(this) && parent.parent.isLeft(parent);
    }

    private boolean isRightRight() {
      return parent.isRight(this) && parent.parent.isRight(parent);
    }

    private boolean isRightLeft() {
      return parent.isLeft(this) && parent.parent.isRight(parent);
    }

    @SuppressWarnings("unchecked")
    private boolean contains(Object o) {
      if (!(value.getClass().isInstance(o))) {
        return false;
      } else {
        T value = (T) o;
        if (this.value.equals(o)) {
          return true;
        } else if (comparator.compare(value, this.value) < 0) {
          return left != null && left.contains(value);
        } else {
          return right != null && right.contains(value);
        }
      }
    }

    private Inner get(T value) {
      if (value.equals(this.value)) {
        return this;
      } else if (comparator.compare(value, this.value) < 0  && left != null) {
        return left.get(value);
      } else if (comparator.compare(value, this.value) > 0  && right != null) {
        return right.get(value);
      } else {
        return null;
      }
    }

    private Inner getUncle() {
      return parent.getSibling();
    }

    private Inner getSibling() {
      if (parent == null) return null;
      else return parent.getSibling(this);
    }

    private Inner getSibling(Inner child) {
      return left == child ? right : left;
    }

    private void rotateLeft() {
      final Inner t = right;
      if (parent != null) {
        parent.replace(this, t);
        t.parent = parent;
      } else {
        t.parent = null;
      }
      this.parent = t;

      this.right = t.left;
      if (this.right != null) this.right.parent = this;

      t.left = this;
    }

    private void rotateRight() {
      final Inner t = left;
      if (parent != null) {
        parent.replace(this, t);
        t.parent = parent;
      } else {
        t.parent = null;
      }
      this.parent = t;

      this.left = t.right;
      if (this.left != null) this.left.parent = this;

      t.right = this;
    }

    private void replace(Inner from, Inner to) {
      if (left == from) left = to;
      else right = to;
    }

    private int getDepth() {
      return Math.max(left == null ? 0 : 1 + left.getDepth(),
          right == null ? 0 : 1 + right.getDepth());
    }

    private int size() {
      int sizeLeft = left == null ? 0 : left.size();
      int sizeRight = right == null ? 0 : right.size();
      return 1 + sizeLeft + sizeRight;
    }

    private Inner next() {
      if (right != null) {
        return right.lowest();
      } else {
        return parent == null ? null : parent.after(this);
      }
    }

    private Inner after(Inner child) {
      if (child == left) {
        return this;
      } else {
        return parent == null ? null : parent.after(this);
      }
    }

    private Inner lowest() {
      return left == null ? this : left.lowest();
    }

    @Override
    public String toString() {
      final int size = size();
      final String rootInfo = isRoot()
          ? String.format("s=%s, d=%s maxD=%s ", size, getDepth(), Math.floor(Math.log(size) / Math.log(2))) : "";

      return rootInfo + (black ? "[" : "<") + value
          + " " + (left == null ? "[]" : left.toString())
          + " " + (right == null ? "[]" : right.toString()) + (black ? "]" : ">");
    }
  }

  public static void main(String[] args) {
    RedBlack<Integer> tree = new RedBlack<>();
    tree.add(8);
    System.out.println(tree);
    tree.add(1);
    System.out.println(tree);
    tree.add(3);
    System.out.println(tree);
    tree.add(5);
    System.out.println(tree);
    tree.add(5);
    System.out.println(tree);
    tree.add(4);
    System.out.println(tree);
    tree.add(6);
    System.out.println(tree);
    tree.add(2);
    System.out.println(tree);
    tree.add(7);
    System.out.println(tree);
    tree.add(10);
    System.out.println(tree);

    for (Object aTree : tree.toArray(new Object[0])) {
      System.out.println(aTree);
    }
  }
}
