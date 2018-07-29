package nl.hayovanloon.collect.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
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

  public boolean add(char[] xs) {
    if (xs.length == 0) {
      return false;
    } else {
      // fast-forward to first difference
      int i = firstDiff(xs, value, 0, 0);
      char[] ys = Arrays.copyOfRange(xs, i, xs.length);

      if (ys.length == 0) {
        end = i == value.length;
        return end;
      }

      if (i == value.length) {
        final Iterator<LexDistTrie> iter = cons.listIterator();
        while (iter.hasNext()) {
          final LexDistTrie trie = iter.next();
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
    final char[] remainder = Arrays.copyOfRange(value, p, value.length);
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
    final List<String> matches = new ArrayList<>();
    for (StringBuilder sb : fuzzyMatches(xs.toCharArray(), 0, 0, maxDist)) {
      matches.add(sb.toString());
    }
    return matches;
  }

  private Set<StringBuilder> fuzzyMatches(char[] xs, int p, int offset, int maxDist) {
    if (maxDist < 0) {
      return new TreeSet<>();
    }

    final Set<StringBuilder> matches =
        new TreeSet<>(Comparator.comparing(StringBuilder::toString));

    final int i = firstDiff(xs, value, p, offset);

    if (end && p + i == xs.length && value.length - offset - i <= maxDist) {
      final StringBuilder sb = new StringBuilder().append(value);
      matches.add(sb);
    }

    if (maxDist > 0) {
      matches.addAll(fuzzyMatches(xs, p + i + 1, offset + i + 1, maxDist - 1));  // replaced
      matches.addAll(fuzzyMatches(xs, p + i + 1, offset + i, maxDist - 1));  // inserted
      matches.addAll(fuzzyMatches(xs, p + i, offset + i + 1, maxDist - 1));  // deleted
    }

    if (value.length - offset - i == 0) {
      for (LexDistTrie child : cons) {
        final Set<StringBuilder> suffixes = child.fuzzyMatches(xs, p + i, 0, maxDist);
        for (StringBuilder suffix : suffixes) {
          matches.add(suffix.insert(0, value));
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

  public int size() {
    int acc = end ? 1 : 0;
    for (LexDistTrie trie : cons) {
      acc += trie.size();
    }
    return acc;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return false;
  }

  @Override
  public Iterator iterator() {
    return null;
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public boolean add(Object o) {
    return false;
  }

  @Override
  public boolean remove(Object o) {
    return false;
  }

  @Override
  public boolean addAll(Collection c) {
    return false;
  }

  @Override
  public void clear() {

  }

  @Override
  public boolean retainAll(Collection c) {
    return false;
  }

  @Override
  public boolean removeAll(Collection c) {
    return false;
  }

  @Override
  public boolean containsAll(Collection c) {
    return false;
  }

  @Override
  public Object[] toArray(Object[] a) {
    return new Object[0];
  }

  public int nodeCount() {
    int acc = 0;
    for (LexDistTrie trie : cons) {
      acc += 1 + trie.size();
    }
    return acc;
  }

  public boolean contains(String xs) {
    return fuzzyContains(xs, 0);
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

