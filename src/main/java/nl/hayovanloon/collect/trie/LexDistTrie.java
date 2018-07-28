package nl.hayovanloon.collect.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class LexDistTrie implements Trie<String>, Serializable {

  // TODO: store depth and throw exception when maxDist >= maxDepth
  // TODO: optimise: insert most active to start of search

  private boolean end;
  private char[] value;
  private final List<LexDistTrie> cons = new ArrayList<>();

  public LexDistTrie() {
    value = new char[0];
    end = false;
  }

  private LexDistTrie(char[] value, boolean end) {
    this.value = value;
    this.end = true;
  }
  
  private LexDistTrie(char[] value, boolean end, List<LexDistTrie> cons) {
    this.value = value;
    this.end = end;
    this.cons.addAll(cons);
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
          if(trie.value[0] == ys[0]) {
            return trie.add(ys);
          }
        }

        cons.add(new LexDistTrie(ys, true));
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
    final LexDistTrie newNode = new LexDistTrie(ys, true);
    
    end = false;
    value = Arrays.copyOfRange(value, 0, p);
    cons.clear();
    cons.add(oldNode);
    cons.add(newNode);
  }

  private static int firstDiff(char[] xs, char[] ys, int offsetX, int offsetY) {
    int i = 0;
    while (offsetX + i < xs.length 
        && offsetY + i < ys.length 
        && xs[offsetX+i] == ys[offsetY+i]) {
      i += 1;
    }
    return i;
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
      System.out.println(sb);
      System.out.println(value.length - offset - i);
      System.out.println(maxDist);
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

  public static void main(String[] args) {
    // test construction
    final LexDistTrie trie = new LexDistTrie();
    System.out.println(trie.size() + ", " + trie);  // 0, empty
    
    System.out.println("\n  add");

    // test add: first addition
    trie.add("kettingkast");
    System.out.println(trie.size() + ", " + trie);  // 1, [kettingkast.]
    
    // test add: split by addition
    trie.add("kettingslot");
    System.out.println(trie.size() + ", " + trie);  // 2, [ketting[kast.][slot.]
    
    // test add: concat
    trie.add("kettingslotkraker");
    System.out.println(trie.size() + ", " + trie);  // 3, [ketting[kast.][slot.[kraker.]]
    
    // test add: new unknown start
    trie.add("fietsketting");
    System.out.println(trie.size() + ", " + trie);  // 4, [ketting[kast.][slot.[kraker.]][fietsketting.]

    // test add: split on element with child nodes
    trie.add("ketel");
    System.out.println(trie.size() + ", " + trie);  // 5, [ket[ting[kast.][slot.[kraker.]]][el.][fietsketting.]

    // test add: unknown subrange
    trie.add("ketting");
    System.out.println(trie.size() + ", " + trie);  // 6, [ket[ting.[kast.][slot.[kraker.]]][el.][fietsketting.]
    
    // test add: duplicate
    trie.add("kettingkast");
    System.out.println(trie.size() + ", " + trie);  // 6, [ket[ting.[kast.][slot.[kraker.]]][el.][fietsketting.]

    System.out.println("\n  contains");

    // test contains: non-final
    System.out.println("+ketting: " + trie.contains("ketting"));  // true

    // test contains: final
    System.out.println("+kettingkast: " + trie.contains("kettingkast"));  // true

    // test contains: all
    final String[] entered = {"ketel", "ketting", "kettingkast", "kettingslot", 
        "kettingslotkraker", "fietsketting"};
    boolean ok = true;
    for (String s : entered) {
      ok = ok && trie.contains(s);
    }
    System.out.println("all: " + ok);  // true

    // test contains: not present
    System.out.println("-kettingpoets: " + trie.contains("kettingpoets"));  // false

    // test contains: longer than any known value
    System.out.println("-kettingkastkleur: " + trie.contains("kettingkastkleur"));  // false

    System.out.println("\n  fuzzyContains");

    // test fuzzyContains: exact match
    System.out.println("+ketting: " + trie.fuzzyContains("ketting", 0));  // true
    
    // test fuzzyContains: not exact match
    System.out.println("-ketting: " + trie.fuzzyContains("kettink", 0));  // false 
    
    // test fuzzyContains: 1 replaced
    System.out.println("+kettinX: " + trie.fuzzyContains("kettinX", 1));  // true
    System.out.println("+ketXing: " + trie.fuzzyContains("ketXing", 1));  // true
    System.out.println("+Xetting: " + trie.fuzzyContains("Xetting", 1));  // true
    
    // test fuzzyContains: 1 replaced fail
    System.out.println("-kettiXX: " + trie.fuzzyContains("kettiXX", 1));  // false 
    System.out.println("-keXXing: " + trie.fuzzyContains("keXXing", 1));  // false 
    System.out.println("-XXtting: " + trie.fuzzyContains("XXtting", 1));  // false 
    
    // test fuzzyContains: 1 added
    System.out.println("+kettingX: " + trie.fuzzyContains("kettingX", 1));  // true
    System.out.println("+keXtting: " + trie.fuzzyContains("keXtting", 1));  // true
    System.out.println("+Xketting: " + trie.fuzzyContains("Xketting", 1));  // true
    
    // test fuzzyContains: 1 added fail
    System.out.println("-kettingXX: " + trie.fuzzyContains("kettingXX", 1));  // false 
    System.out.println("-keXttXing: " + trie.fuzzyContains("keXttXing", 1));  // false 
    System.out.println("-XXketting: " + trie.fuzzyContains("XXketting", 1));  // false 
    
    // test fuzzyContains: 1 removed
    System.out.println("+kettin: " + trie.fuzzyContains("kettin", 1));  // true
    System.out.println("+keting: " + trie.fuzzyContains("keting", 1));  // true
    System.out.println("+etting: " + trie.fuzzyContains("etting", 1));  // true
    
    // test fuzzyContains: 1 removed fail
    System.out.println("-ketti: " + trie.fuzzyContains("ketti", 1));  // false 
    System.out.println("-keing: " + trie.fuzzyContains("keing", 1));  // false 
    System.out.println("-tting: " + trie.fuzzyContains("tting", 1));  // false 
    
    // test fuzzyContains: combos
    System.out.println("+XkettinX: " + trie.fuzzyContains("XkettinX", 2));  // true
    System.out.println("+keingX: " + trie.fuzzyContains("keingX", 3));  // true
    System.out.println("+XetingX: " + trie.fuzzyContains("XetingX", 3));  // true
    System.out.println("+XttngX: " + trie.fuzzyContains("XttngX", 4));  // false 
    
    // test fuzzyContains: combos fail
    System.out.println("-XkttingX: " + trie.fuzzyContains("XkttingX", 2));  // false 
    System.out.println("-ttingXX: " + trie.fuzzyContains("ttingXX", 3));  // false 
    System.out.println("-XttngX: " + trie.fuzzyContains("XttngX", 3));  // false 

    System.out.println("\n  fuzzyMatches");

    System.out.println("+kettings/3: " + trie.fuzzyMatches("kettings", 3));  // ketting, kettingslot
    System.out.println("+ketting/5: " + trie.fuzzyMatches("ketting", 5));  // ketting, kettingslot, ketel, fietsketting, kettingkast
    System.out.println("+ketting/5: " + trie.fuzzyMatches("kXtting", 5));  // ketting, kettingslot, ketel, kettingkast
    System.out.println("-XttngX/3: " + trie.fuzzyMatches("XttngX", 3));  // []
  }
}

