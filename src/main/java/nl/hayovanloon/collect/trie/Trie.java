package nl.hayovanloon.collect.trie;

import java.util.Collection;

public interface Trie<T> extends Collection<T> {

  boolean fuzzyContains(T t, int distance);

  Iterable<T> fuzzyMatches(T t, int distance);
}
