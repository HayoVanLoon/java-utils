public class TreeTester {

  public static void main(String[] args) {

    final Tree simpleTree = Tree.node(0, null, Tree.node(2, Tree.leaf(1), Tree.leaf(3))).balance();

    final Tree tree1 = Tree.leaf(-32).add(-16).add(-8).add(-4).add(-2).add(-1).add(0);
    final Tree tree2 = Tree.leaf(0).add(1).add(2).add(4).add(8).add(16).add(32).add(3).add(-3);
    final Tree tree3 = tree1.addAll(tree2);
    final Tree tree4 = tree3.balance();

    System.out.println(simpleTree.print());
    System.out.println();
    System.out.println(tree3.print());
    System.out.println();
    System.out.println(tree4.print());

    System.out.println("dfs (+): " + tree4.dfs(2) + ": " + Tree.counter);
    Tree.counter = 0;
    System.out.println("dfs (-): " + tree4.dfs(320) + ": " + Tree.counter);
    Tree.counter = 0;
    System.out.println("bin (+): " + tree4.bins(2) + ": " + Tree.counter);
    Tree.counter = 0;
    System.out.println("bin (-): " + tree4.bins(320) + ": " + Tree.counter);
    Tree.counter = 0;
    System.out.println("bin (+)(imb): " + tree3.bins(2) + ": " + Tree.counter);
    System.out.println();
    Tree.counter = 0;
    System.out.println("bfs (+): " + tree4.bfs(2) + " " + Tree.counter);
    Tree.counter = 0;
    System.out.println("bfs (+): " + tree4.bfs(320) + " " + Tree.counter);
  }
}
