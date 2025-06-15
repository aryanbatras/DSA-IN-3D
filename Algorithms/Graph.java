package Algorithms;

import Collections.JGraph;
import Collections.JStack;
import Shapes.JBox;

import java.util.*;
import java.util.Queue;

public enum Graph {

    DEPTH_FIRST_TRAVERSAL {
        public <T extends Comparable<T>> void run(JGraph<T> g) {
            System.out.print("DFS: ");
            Set<T> visited = new HashSet<>();
            for (T start : g.getVertices()) {
                if (!visited.contains(start)) {
                    dfs(g, start, visited);
                }
            }
        }
    },

    BREADTH_FIRST_TRAVERSAL {
        public <T extends Comparable<T>> void run(JGraph<T> g) {
            System.out.print("BFS: ");
            Set<T> visited = new HashSet<>();
            Queue<T> queue = new java.util.LinkedList<>();

            for (T start : g.getVertices()) {
                if (!visited.contains(start)) {
                    queue.add(start);
                    visited.add(start);
                    while (!queue.isEmpty()) {
                        T current = queue.poll();
                        g.highlightVertex(current);
                        System.out.print(current + " ");

                        for (T neighbor : g.getAdjacencyList().getOrDefault(current, new ArrayList<>())) {
                            if (!visited.contains(neighbor)) {
                                g.highlightEdge(current, neighbor);
                                queue.add(neighbor);
                                visited.add(neighbor);
                            }
                        }
                    }
                }
            }
            System.out.println();
        }

    },

    CONNECTED_COMPONENTS {
        public <T extends Comparable<T>> void run(JGraph<T> g) {
            System.out.println("Connected Components:");
            Set<T> visited = new HashSet<>();
            int component = 1;

            for (T start : g.getVertices()) {
                if (!visited.contains(start)) {
                    System.out.print("Component " + component++ + ": ");
                    dfs(g, start, visited);
                    System.out.println();
                }
            }
        }
    },

    REVERSE_GRAPH {
        public <T extends Comparable<T>> void run(JGraph<T> g) {
            System.out.println("Reversing Graph:");
            List<T[]> edgesToReverse = new ArrayList<>();

            for (T u : g.getVertices()) {
                for (T v : g.getAdjacencyList().getOrDefault(u, new ArrayList<>())) {
                    edgesToReverse.add((T[]) new Comparable[]{u, v});
                }
            }

            for (T[] edge : edgesToReverse) {
                T u = edge[0], v = edge[1];
                g.highlightEdge(u, v);
                g.removeEdge(u, v);
                g.addEdge(v, u);
                g.highlightEdge(v, u);
            }

            System.out.println("Graph reversed!");
        }
    },

    CYCLE_DETECTION {
        public <T extends Comparable<T>> void run(JGraph<T> g) {
            Set<T> visited = new HashSet<>( );
            Set<T> stack = new HashSet<>( );

            for (T node : g.getVertices( )) {
                if (dfsCycle(g, node, visited, stack, null)) {
                    System.out.println("Cycle Found!");
                    return;
                }
            }
            System.out.println("No Cycle Detected.");
        }
    },

    TOPOLOGICAL_SORT {
        public <T extends Comparable<T>> void run(JGraph<T> g) {
            System.out.print("Topological Order: ");
            Set<T> visited = new HashSet<T>();
            JStack<T> stack = new JStack<T>();

            for (T node : g.getVertices()) {
                if (!visited.contains(node)) {
                    dfsTopo(g, node, visited, stack);
                }
            }

            while (!stack.isEmpty()) {
                T v = stack.pop();
                g.highlightVertex(v);
                System.out.print(v + " ");
            }

            System.out.println();
        }

    };

    public abstract <T extends Comparable<T>> void run(JGraph<T> g);

    private static <T extends Comparable<T>> void dfs(JGraph<T> g, T current, Set<T> visited) {
        visited.add(current);
        g.highlightVertex(current);
        System.out.print(current + " ");
        for (T neighbor : g.getAdjacencyList().getOrDefault(current, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                g.highlightEdge(current, neighbor);
                dfs(g, neighbor, visited);
            }
        }
    }

    private static  <T extends Comparable<T>> boolean dfsCycle(JGraph<T> g, T node, Set<T> visited, Set<T> stack, T parent) {
        if (stack.contains(node)) {
            g.highlightVertex(node);
            return true;
        }

        if (visited.contains(node)) return false;

        visited.add(node);
        stack.add(node);
        g.highlightVertex(node);

        for (T neighbor : g.getAdjacencyList().getOrDefault(node, new ArrayList<>())) {
            g.highlightEdge(node, neighbor);
            if (!neighbor.equals(parent) && dfsCycle(g, neighbor, visited, stack, node)) {
                g.highlightVertex(neighbor);
                return true;
            }
        }

        stack.remove(node);
        return false;
    }

    private static <T extends Comparable<T>> void dfsTopo(JGraph<T> g, T node, Set<T> visited, JStack<T> stack) {
        visited.add(node);
        for (T neighbor : g.getAdjacencyList().getOrDefault(node, new ArrayList<>())) {
            g.highlightEdge(node, neighbor);
            if (!visited.contains(neighbor)) {
                dfsTopo(g, neighbor, visited, stack);
            }
        }
        stack.push(node);
    }
}

