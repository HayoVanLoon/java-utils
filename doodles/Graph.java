import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class Graph {

  private static int timer = 0;
  final boolean directed;
  Map<String, Vertex> vertices = new HashMap<>();

  public Graph(boolean directed) {
    this.directed = directed;
  }

  private static List<String> makeRoute(Map<String, String> route,
                                        Map<String, Integer> cost,
                                        String end) {
    final Stack<String> stack = new Stack<>();
    String last = end;
    String next = route.get(last);
    while (next == null || !next.equals(last)) {
      stack.add(last);
      last = next;
      next = route.get(last);
    }
    stack.add(last);

    List<String> result = new ArrayList<>();
    while (!stack.empty()) result.add(stack.pop());

    result.add("" + cost.get(end));
    return result;
  }

  public static void main(String[] args) {

    Graph g = new Graph(false);

    g.addVertex("A");
    g.addVertex("B");
    g.addVertex("C");
    g.addVertex("D");
    g.addVertex("E");

    g.addEdge("A", "B");
    g.addEdge("A", "C", 12);
    g.addEdge("B", "D");
    g.addEdge("D", "E");
    g.addEdge("C", "E");

    System.out.println(g);

    Map<String, String> bfs = g.bfs("A");
    System.out.println("BFS:\n" + print(bfs, false));

    Map<String, String> dfs = g.dfs("A");
    System.out.println("DFS:\n" + print(dfs, true));

    System.out.println("Dijkstra: " + g.dijkstra("A", "E"));

    System.out.println(pairs("BAAAA".toCharArray()));
  }

  private static int pairs(char[] xs) {
    int acc = 0;
    for (int i = xs.length - 1; i >= 0; i -= 1) {
      if (xs[i] == 'B') {
        for (int j = 0; j < i; j += 1) {
          if (xs[j] == 'A') acc += 1;
        }
      }
    }
    return acc;
  }

  private static String print(Map<String, String> parents, boolean compact) {
    final String root = parents.entrySet().stream()
        .filter(x -> x.getKey().equals(x.getValue()))
        .findFirst().get().getKey();

    final HashMap<String, String> copy = new HashMap<>(parents);
    copy.remove(root);

    SimpleTree<String> t = new SimpleTree<>(root);

    while (!copy.isEmpty()) {
      copy.entrySet()
          .removeIf(entry -> t.appendTo(entry.getValue(), entry.getKey()));
    }

    final String printed = t.toString();
    final String finalised = compact
        ? printed.replaceAll("\n", "").replaceAll("\t", "") : printed;

    return finalised;
  }

  public void addVertex(String label) {
    vertices.put(label, new Vertex(label));
  }

  public void addEdge(String l1, String l2) {
    addEdge(l1, l2, 1, false);
  }

  public void addEdge(String l1, String l2, int weight) {
    addEdge(l1, l2, weight, false);
  }

  public void addEdge(String l1, String l2, boolean directed) {
    addEdge(l1, l2, 1, directed);
  }

  public void addEdge(String l1, String l2, int weight, boolean directed) {
    vertices.get(l1).addEge(vertices.get(l2), weight);
    if (!directed) vertices.get(l2).addEge(vertices.get(l1), weight);
  }

  public boolean cutEdge(String l1, String l2) {
    return cutEdge(l1, l2, false);
  }

  public boolean cutEdge(String l1, String l2, boolean directed) {
    return cutEdge2(l1, l2) && (directed || cutEdge2(l2, l1));
  }

  private boolean cutEdge2(String l1, String l2) {
    return vertices.get(l1).cutEdgeTo(vertices.get(l2));
  }

  public Map<String, String> bfs(String startLabel) {
    final Vertex start = vertices.get(startLabel);

    final HashMap<String, String> parents = new HashMap<>();
    final HashSet<String> discovered = new HashSet<>();
    final HashSet<String> processed = new HashSet<>();

    parents.put(start.label, start.label);
    discovered.add(start.label);

    final Queue<Vertex> queue = new LinkedList<>();
    queue.add(start);

    while (!queue.isEmpty()) {
      Vertex v = queue.poll();

      //process vertex early

      for (Edge edge : v.edges) {
        Vertex other = edge.to;

        if (!processed.contains(other.label) || directed) {
          // process edge
        }
        if (!discovered.contains(other.label)) {
          parents.put(other.label, v.label);
          discovered.add(other.label);
          queue.add(other);
        }
        processed.add(other.label);
      }

      //process vertex late
      processed.add(v.label);
    }

    return parents;
  }

  public Map<String, String> dfs(String startLabel) {
    final Vertex v = vertices.get(startLabel);

    final HashMap<String, String> parents = new HashMap<>();
    final HashMap<String, Integer> entry = new HashMap<>();
    final HashMap<String, Integer> exit = new HashMap<>();
    final HashSet<String> discovered = new HashSet<>();
    final HashSet<String> processed = new HashSet<>();

    timer = 0;
    parents.put(v.label, v.label);

    dfs2(v, parents, discovered, processed, entry, exit);

    return parents;
  }

  private void dfs2(Vertex v,
                    Map<String, String> parents,
                    Set<String> discovered,
                    Set<String> processed,
                    Map<String, Integer> entry,
                    Map<String, Integer> exit) {
    entry.put(v.label, timer);
    timer += 1;

    discovered.add(v.label);

    dfsProcessEarly(v);

    for (Edge e : v.edges) {
      Vertex to = e.to;
      if (!discovered.contains(to.label)) {
        parents.put(to.label, v.label);
        dfsProcessEdge(v, to, discovered, processed, parents);
        dfs2(to, parents, discovered, processed, entry, exit);
      } else if ((!processed.contains(to.label)
          && !to.label.equals(parents.get(v.label))) || directed) {
        dfsProcessEdge(v, to, discovered, processed, parents);
      }
    }

    dsfProcessLate(v);
    processed.add(v.label);

    exit.put(v.label, timer);
  }

  private void dfsProcessEdge(Vertex v,
                              Vertex to,
                              Set<String> discovered,
                              Set<String> processed,
                              Map<String, String> parents) {

    int classification = classifyEdge(v, to, discovered, processed, parents);

    // etc
  }

  private int classifyEdge(Vertex v,
                           Vertex to,
                           Set<String> discovered,
                           Set<String> processed,
                           Map<String, String> parents) {
    if (discovered.contains(to.label)
        && !v.label.equals(parents.get(to.label))) {
      return 1;
    } else {
      return -1;
    }
  }

  private void dfsProcessEarly(Vertex v) {

  }

  private void dsfProcessLate(Vertex v) {

  }

  public List<String> dijkstra(String start, String end) {
    Set<String> visited = new HashSet<>();
    Map<String, String> routes = new HashMap<>();
    Map<String, Integer> distances = new HashMap<>();

    routes.put(start, start);
    distances.put(start, 0);
    Vertex v = vertices.get(start);

    while (!visited.contains(v.label)) {
      int currentCost = distances.get(v.label);
      visited.add(v.label);
      for (Edge edge : v.edges) {
        Vertex to = edge.to;

        int newCost = currentCost + edge.weight;

        if (newCost < distances.getOrDefault(to.label, Integer.MAX_VALUE)) {
          routes.put(to.label, v.label);
          distances.put(to.label, newCost);
        }
      }

      int dist = Integer.MAX_VALUE;
      for (Vertex v2 : vertices.values()) {
        if (!visited.contains(v2.label)
            && dist > distances.getOrDefault(v2.label, Integer.MAX_VALUE)) {
          dist = distances.get(v2.label);
          v = v2;
        }
      }
    }

    System.out.println("distances from " + start + ": " + distances);
    System.out.println("fastest routes from " + start + ": " + routes);
    return makeRoute(routes, distances, end);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (Vertex vertex : vertices.values()) {
      sb.append(vertex.toString()).append("\n");
    }

    return sb.toString();
  }

  static class Vertex {
    final String label;
    final List<Edge> edges = new ArrayList<>();

    public Vertex(String label) {
      this.label = label;
    }

    public void addEge(Vertex to, int weight) {
      edges.add(new Edge(to, weight));
    }

    public boolean cutEdgeTo(Vertex other) {
      return edges.removeIf(edge -> other.label.equals(edge.to.label));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Vertex vertex = (Vertex) o;
      return Objects.equals(label, vertex.label);
    }

    @Override
    public int hashCode() {

      return Objects.hash(label);
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append(label);
      for (Edge edge : edges) {
        sb.append("\n  ").append(edge.toString());
      }
      return sb.toString();
    }
  }

  static class Edge {
    final Vertex to;
    final int weight;

    public Edge(Vertex to, int weight) {
      this.to = to;
      this.weight = weight;
    }

    public String toString() {
      return String.format("-- %s --> %s", weight, to.label);
    }
  }

  public static class SimpleTree<T> {
    final T value;
    final List<SimpleTree<T>> children = new ArrayList<>();

    public SimpleTree(T value) {
      this.value = value;
    }

    public boolean appendTo(T parentValue, T newChild) {
      if (parentValue.equals(value)) {
        children.add(new SimpleTree<>(newChild));
        return true;
      } else {
        for (SimpleTree<T> child : children) {
          if (child.appendTo(parentValue, newChild)) return true;
        }
        return false;
      }
    }

    @Override
    public String toString() {
      return toString("");
    }

    private String toString(String pre) {
      final StringBuilder sb = new StringBuilder();
      sb.append(pre).append(value.toString()).append(" {");

      for (SimpleTree<T> child : children) {
        sb.append("\n").append(child.toString(pre + "\t"));
      }

      if (!children.isEmpty()) sb.append(pre);
      sb.append("}");

      return sb.append("\n").toString();
    }
  }
}
