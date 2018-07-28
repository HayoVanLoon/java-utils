package nl.hayovanloon.collect.trie;

public interface Trie<T> {

  boolean fuzzyContains(T t, int distance);

  Iterable<T> fuzzyMatches(T t, int distance);
}
