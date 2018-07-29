package nl.hayovanloon.collect.trie;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class LexDistTrieTest {

  private static LexDistTrie create() {
    final LexDistTrie trie = new LexDistTrie();
    trie.add("kettingkast");
    trie.add("kettingslot");
    trie.add("kettingslotkraker");
    trie.add("fietsketting");
    trie.add("ketel");
    trie.add("ketting");
    trie.add("kettingkast");
    return trie;
  }

  @Test
  public void add() {
    // test construction
    final LexDistTrie trie = new LexDistTrie();
    Assert.assertEquals(0, trie.size());

    // test add: first addition
    trie.add("kettingkast");
    Assert.assertEquals(1, trie.size());

    // test add: split by addition
    trie.add("kettingslot");
    Assert.assertEquals(2, trie.size());

    // test add: concat
    trie.add("kettingslotkraker");
    Assert.assertEquals(3, trie.size());

    // test add: new unknown start
    trie.add("fietsketting");
    Assert.assertEquals(4, trie.size());

    // test add: split on element with child nodes
    trie.add("ketel");
    Assert.assertEquals(5, trie.size());

    // test add: unknown subrange
    trie.add("ketting");
    Assert.assertEquals(6, trie.size());

    // test add: duplicate
    trie.add("kettingkast");
    Assert.assertEquals(6, trie.size());
  }

  @Test
  public void contains() {
    final LexDistTrie trie = create();

    // test contains: non-final
    Assert.assertTrue(trie.contains("ketting"));

    // test contains: final
    Assert.assertTrue(trie.contains("kettingkast"));

    // test contains: all
    final String[] entered = {"ketel", "ketting", "kettingkast", "kettingslot",
        "kettingslotkraker", "fietsketting"};
    for (String s : entered) {
      Assert.assertTrue(trie.contains(s));
    }

    // test contains: not present
    Assert.assertTrue(!trie.contains("kettingpoets"));

    // test contains: longer than any known value
    Assert.assertTrue(!trie.contains("kettingkastkleur"));
  }

  @Test
  public void fuzzyContains() {
    final Trie<String> trie = create();

    // test fuzzyContains: exact match
    Assert.assertTrue(trie.fuzzyContains("ketting", 0));

    // test fuzzyContains: not exact match
    Assert.assertTrue(!trie.fuzzyContains("kettink", 0));

    // test fuzzyContains: 1 replaced
    Assert.assertTrue(trie.fuzzyContains("kettinX", 1));
    Assert.assertTrue(trie.fuzzyContains("ketXing", 1));
    Assert.assertTrue(trie.fuzzyContains("Xetting", 1));

    // test fuzzyContains: 1 replaced fail
    Assert.assertTrue(!trie.fuzzyContains("kettiXX", 1));
    Assert.assertTrue(!trie.fuzzyContains("keXXing", 1));
    Assert.assertTrue(!trie.fuzzyContains("XXtting", 1));

    // test fuzzyContains: 1 added
    Assert.assertTrue(trie.fuzzyContains("kettingX", 1));
    Assert.assertTrue(trie.fuzzyContains("keXtting", 1));
    Assert.assertTrue(trie.fuzzyContains("Xketting", 1));

    // test fuzzyContains: 1 added fail
    Assert.assertTrue(!trie.fuzzyContains("kettingXX", 1));
    Assert.assertTrue(!trie.fuzzyContains("keXttXing", 1));
    Assert.assertTrue(!trie.fuzzyContains("XXketting", 1));

    // test fuzzyContains: 1 removed
    Assert.assertTrue(trie.fuzzyContains("kettin", 1));
    Assert.assertTrue(trie.fuzzyContains("keting", 1));
    Assert.assertTrue(trie.fuzzyContains("etting", 1));

    // test fuzzyContains: 1 removed fail
    Assert.assertTrue(!trie.fuzzyContains("ketti", 1));
    Assert.assertTrue(!trie.fuzzyContains("keing", 1));
    Assert.assertTrue(!trie.fuzzyContains("tting", 1));

    // test fuzzyContains: combos
    Assert.assertTrue(trie.fuzzyContains("XkettinX", 2));
    Assert.assertTrue(trie.fuzzyContains("keingX", 3));
    Assert.assertTrue(trie.fuzzyContains("XetingX", 3));
    Assert.assertTrue(trie.fuzzyContains("XttngX", 4));

    // test fuzzyContains: combos fail
    Assert.assertTrue(!trie.fuzzyContains("XkttingX", 2));
    Assert.assertTrue(!trie.fuzzyContains("ttingXX", 3));
    Assert.assertTrue(!trie.fuzzyContains("XttngX", 3));
  }

  @Test
  public void fuzzyMatches() {
    final Trie<String> trie = create();

    Assert.assertEquals(
        toSet(new String[]{"ketting", "kettingkast", "kettingslot"}),
        toSet(trie.fuzzyMatches("kettings", 3)));

    Assert.assertEquals(
        toSet(new String[]{"ketting", "kettingslot", "ketel", "fietsketting",
            "kettingkast"}),
        toSet(trie.fuzzyMatches("ketting", 5)));

    Assert.assertEquals(
        toSet(new String[]{"ketting", "kettingslot", "ketel", "kettingkast"}),
        toSet(trie.fuzzyMatches("kXtting", 5)));

    Assert.assertEquals(
        toSet(new String[]{}),
        toSet(trie.fuzzyMatches("XttngX", 3)));
  }

  @Test
  public void iterator() {
    final LexDistTrie trie = new LexDistTrie();
    trie.add("ketting");
    trie.add("ketel");

    final Iterator<String> iter = trie.iterator();
    for (int i = 0; iter.hasNext(); i += 1) {
      Assert.assertEquals(new String[]{"ketting", "ketel"}[i], iter.next());
    }

    final Set a = toSet(create());
    final Set expected = toSet(new String[]{"kettingkast", "kettingslot",
        "kettingslotkraker", "fietsketting", "ketel", "ketting",
        "kettingkast"});

    Assert.assertEquals(expected, a);
  }

  private static <T> Set<T> toSet(T[] array) {
    return Arrays.stream(array).collect(Collectors.toSet());
  }

  private static <T> Set<T> toSet(Iterable<T> iterable) {
    return toSet(iterable.iterator());
  }

  private static <T> Set<T> toSet(Iterator<T> iterator) {
    final Set<T> set = new TreeSet<>();
    iterator.forEachRemaining(set::add);
    return set;
  }
}

