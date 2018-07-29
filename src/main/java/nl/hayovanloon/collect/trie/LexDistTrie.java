package nl.hayovanloon.collect.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class LexDistTrie implements Trie<String>, Serializable {

  // TODO: store depth and throw exception when maxDist >= maxDepth
  // TODO: optimise: insert most active to start of search

  private final List<LexDistTrie> cons = new ArrayList<>();
  private boolean end;
  private char[] value;

  public LexDistTrie() {
    value = new char[0];
    end = false;
  }

  private LexDistTrie(char[] value) {
    this.value = value;
    this.end = true;
  }

  private LexDistTrie(char[] value, boolean end, List<LexDistTrie> cons) {
    this.value = value;
    this.end = end;
    this.cons.addAll(cons);
  }

  private static int firstDiff(char[] xs, char[] ys, int offsetX, int offsetY) {
    int i = 0;
    while (offsetX + i < xs.length
        && offsetY + i < ys.length
        && xs[offsetX + i] == ys[offsetY + i]) {
      i += 1;
    }
    return i;
  }

  public boolean add(String xs) {
    return add(xs.toCharArray());
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException("not supported (yet");
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    // TODO: optimisation candidate
    for (Object o : c) {
      if (!contains(o)) return false;
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends String> c) {
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return false;
  }

  private boolean add(char[] xs) {
    if (xs.length == 0) {
      return false;
    } else {
      // fast-forward to first difference
      int i = firstDiff(xs, value, 0, 0);
      char[] ys = new char[xs.length - i];

      if (ys.length == 0) {
        end = i == value.length;
        return end;
      } else {
        System.arraycopy(xs, i, ys, 0, xs.length - i);
      }

      if (i == value.length) {
        for (LexDistTrie trie : cons) {
          if (trie.value[0] == ys[0]) {
            return trie.add(ys);
          }
        }

        cons.add(new LexDistTrie(ys));
        return true;
      } else {
        split(ys, i);
        return true;
      }
    }
  }

  private void split(char[] ys, int p) {
    final char[] remainder = new char[value.length - p];
    System.arraycopy(value, p, remainder, 0, value.length - p);
    final LexDistTrie oldNode = new LexDistTrie(remainder, end, cons);
    final LexDistTrie newNode = new LexDistTrie(ys);

    end = false;
    value = Arrays.copyOfRange(value, 0, p);
    cons.clear();
    cons.add(oldNode);
    cons.add(newNode);
  }

  @Override
  public Iterable<String> fuzzyMatches(String xs, int maxDist) {
    return fuzzyMatches(xs.toCharArray(), 0, 0, maxDist);
  }

  private Set<String> fuzzyMatches(char[] xs, int p, int offset, int maxDist) {
    if (maxDist < 0) {
      return new TreeSet<>();
    }

    final Set<String> matches = new TreeSet<>();

    final int i = firstDiff(xs, value, p, offset);

    if (end && p + i == xs.length && value.length - offset - i <= maxDist) {
      matches.add(new String(value));
    }

    if (maxDist > 0) {
      // if something was replaced
      matches.addAll(fuzzyMatches(xs, p + i + 1, offset + i + 1, maxDist - 1));

      // if something was inserted
      matches.addAll(fuzzyMatches(xs, p + i + 1, offset + i, maxDist - 1));

      // if something was deleted
      matches.addAll(fuzzyMatches(xs, p + i, offset + i + 1, maxDist - 1));
    }

    if (value.length - offset - i == 0) {
      for (LexDistTrie child : cons) {
        final Set<String> suffixes =
            child.fuzzyMatches(xs, p + i, 0, maxDist);
        for (String suffix : suffixes) {
          matches.add(new String(value) + suffix);
        }
      }
    }

    return matches;
  }

  public boolean fuzzyContains(String xs, int maxDist) {
    return fuzzyContains(xs.toCharArray(), 0, 0, maxDist);
  }

  private boolean fuzzyContains(char[] xs, int p, int offset, int maxDist) {
    if (maxDist < 0) {
      return false;
    }

    final int i = firstDiff(xs, value, p, offset);

    if (p + i == xs.length) {
      // end of input, remainder must be within allowed range
      return end && value.length - (offset + i) <= maxDist;
    } else if (offset + i != value.length) {
      return maxDist > 0 && (
          fuzzyContains(xs, p + i + 1, offset + i + 1, maxDist - 1)  // replace
              || fuzzyContains(xs, p + i + 1, offset + i, maxDist - 1)  // add
              || fuzzyContains(xs, p + i, offset + i + 1, maxDist - 1));  //delete
    }

    // end of value
    if (end && offset + i == value.length && xs.length - (p + i) <= maxDist) {
      // sufficient credits left
      return true;
    } else {
      // must check children
      for (LexDistTrie next : cons) {
        if (next.fuzzyContains(xs, p + i, 0, maxDist)) {
          return true;
        }
      }
      return false;
    }
  }

  @Override
  public int size() {
    int acc = end ? 1 : 0;
    for (LexDistTrie trie : cons) {
      acc += trie.size();
    }
    return acc;
  }

  @Override
  public boolean isEmpty() {
    return value.length == 0 && !end && cons.isEmpty();
  }

  @Override
  public Iterator<String> iterator() {
    return getAllParts().iterator();
  }

  private List<String> getAllParts() {
    final String base = new String(value);
    if (cons.isEmpty()) {
      return Collections.singletonList(base);
    } else {
      final List<String> xs = new LinkedList<>();
      if (end) {
        xs.add(base);
      }
      for (LexDistTrie trie : cons) {
        for (String s : trie.getAllParts()) {
          xs.add(base + s);
        }
      }
      return xs;
    }
  }

  @Override
  public Object[] toArray() {
    final Object[] array = new Object[size()];
    return toArray(array);
  }

  @Override
  public void clear() {
    value = new char[0];
    end = false;
    cons.clear();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    if (a == null) {
      throw new NullPointerException("destination cannot be null");
    }
    if (a.getClass() != String[].class) {
      throw new ArrayStoreException("destination is not a String[]");
    }

    int i;
    final Iterator<String> iter = iterator();
    for (i = 0; iter.hasNext() && i < a.length; i += 1) {
      a[i] = (T) iter.next();
    }

    if (i < a.length) {
      a[i] = null;
    } else if (iter.hasNext()) {
      final T[] bs = (T[]) new String[size()];
      System.arraycopy(a, 0, bs, 0, i + 1);

      return bs;
    }

    return a;
  }

  public int nodeCount() {
    int acc = 0;
    for (LexDistTrie trie : cons) {
      acc += 1 + trie.size();
    }
    return acc;
  }

  @Override
  public boolean contains(Object xs) {
    return xs instanceof String && fuzzyContains((String) xs, 0);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(value);
    if (end) {
      sb.append('.');
    }
    for (LexDistTrie trie : cons) {
      sb.append('[').append(trie).append(']');
    }
    return sb.toString();
  }
}

