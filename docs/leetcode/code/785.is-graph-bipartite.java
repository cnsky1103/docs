/*
 * @lc app=leetcode id=785 lang=java
 *
 * [785] Is Graph Bipartite?
 */

// @lc code=start
class Solution {
    boolean[] color;
    boolean ok = true;
    boolean[] visited;

    public boolean isBipartite(int[][] graph) {
        int n = graph.length;
        color = new boolean[n];
        visited = new boolean[n];

        for (int i = 0; i < graph.length; i++) {
            //if (!visited[i])
            traverse(graph, i);
        }

        return ok;
    }

    void traverse(int[][] graph, int n) {
        if (!ok) {
            return;
        }

        visited[n] = true;
        for (int adj : graph[n]) {
            if (!visited[adj]) {
                color[adj] = !color[n];
                traverse(graph, adj);
            } else {
                if (color[adj] == color[n]) {
                    ok = false;
                    return;
                }
            }
        }
    }
}
// @lc code=end
