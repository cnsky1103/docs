import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * @lc app=leetcode id=886 lang=java
 *
 * [886] Possible Bipartition
 */

// @lc code=start
class Solution {
    private boolean ok = true;
    private boolean[] color;
    private boolean[] visited;

    public boolean possibleBipartition(int n, int[][] dislikes) {
        color = new boolean[n + 1];
        visited = new boolean[n + 1];
        List<Integer>[] graph = buildGraph(n, dislikes);

        for (int v = 1; v <= n; v++) {
            if (!visited[v]) {
                traverse(graph, v);
            }
        }

        return ok;
    }

    private List<Integer>[] buildGraph(int n, int[][] dislikes) {
        List<Integer>[] graph = new LinkedList[n + 1];
        for (int i = 1; i <= n; i++) {
            graph[i] = new LinkedList<>();
        }
        for (int[] edge : dislikes) {
            int v = edge[1];
            int w = edge[0];
            graph[v].add(w);
            graph[w].add(v);
        }
        return graph;
    }

    void traverse(List<Integer>[] graph, int v) {
        Queue<Integer> open_list = new LinkedList<>();
        open_list.offer(v);
        visited[v] = true;
        while (!open_list.isEmpty() && ok) {
            int n = open_list.poll();
            visited[n] = true;

            for (int adj : graph[n]) {
                if (!visited[adj]) {
                    color[adj] = !color[n];
                    open_list.offer(adj);
                } else {
                    if (color[adj] == color[n]) {
                        ok = false;
                        return;
                    }
                }
            }
        }
    }
}
// @lc code=end
