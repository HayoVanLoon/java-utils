import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;


public class Primes {

  public static void main(String[] args) {
    HashTable<String, Integer> table = new HashTable<>(24, String.class, Integer.class);

    for (int i = 0; i < 33; i += 1) {
      table.put("test" + i, i);
    }

    System.out.println(table.get("test0"));
    System.out.println(table.get("test29"));
    System.out.println(table.get("test3"));
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

  private static int getPrimeAfter(int n) {
    int candidate = n + 1;
    while (candidate < Integer.MAX_VALUE) {
      boolean divisible = candidate % 2 == 0;
      for (long i = 3; i <= Math.ceil(Math.sqrt(n)) && !divisible; i += 1) {
        divisible = candidate % i == 0;
      }
      if (!divisible) return candidate;
      else candidate += 1;
    }
    return -1;
  }

  private static long getPrimeAfter(long n) {
    long candidate = n + 1;
    while (candidate < Long.MAX_VALUE) {
      boolean divisible = candidate % 2 == 0;
      for (long i = 3; i <= Math.ceil(Math.sqrt(n)) && !divisible; i += 1) {
        divisible = candidate % i == 0;
      }
      if (!divisible) return candidate;
      else candidate += 1;
    }
    return -1;
  }

  private static class HashTable<K, V> {

    private final Class<K> keyClass;
    private final Class<V> valueClass;

    private final K[][] keys;
    private final V[][] values;

    private HashTable(int size,
                      Class<K> keyClass,
                      Class<V> valueClass) {
      int prime = getPrimeAfter((int) (size * 1.33));
      System.out.println(">>buckets: " + prime);
      this.keyClass = keyClass;
      this.valueClass = valueClass;
      this.keys = (K[][]) Array.newInstance(Array.newInstance(keyClass, 0).getClass(), prime);
      this.values = (V[][]) Array.newInstance(Array.newInstance(valueClass, 0).getClass(), prime);
    }

    @SuppressWarnings("unchecked")
    public void put(K key, V value) {
      int bucket = getBucket(key);
      if (keys[bucket] == null) {
        keys[bucket] = (K[]) Array.newInstance(keyClass, 1);
        values[bucket] = (V[]) Array.newInstance(valueClass, 1);
      }
      if (keys[bucket][0] != null) {
        System.out.println(">>clash at: " + bucket + " for: " + key);
      }
      System.out.println(">>bucket: " + bucket);

      int next = keys[bucket].length;
      keys[bucket] = Arrays.copyOf(keys[bucket], next);
      values[bucket] = Arrays.copyOf(values[bucket], next);
      keys[bucket][next - 1] = key;
      values[bucket][next - 1] = value;
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
      int bucket = getBucket(key);
      K[] ks = keys[bucket];
      if (ks != null) {
        for (int i = 0; i < ks.length; i++) {
          if (key.equals(key)) return values[bucket][i];
        }
      }
      return null;
    }

    private int getBucket(K key) {
      return (keys.length + (key.hashCode() % keys.length)) % keys.length;
    }
  }
}
