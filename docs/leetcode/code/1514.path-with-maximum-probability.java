/*
 * @lc app=leetcode id=1514 lang=java
 *
 * [1514] Path with Maximum Probability
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.PriorityQueue;

// @lc code=start
class Solution {
    public double maxProbability(int n, int[][] edges, double[] succProb, int start_node, int end_node) {
        List<double[]>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int i = 0; i < edges.length; i++) {
            int from = edges[i][0];
            int to = edges[i][1];
            double weight = succProb[i];
            graph[from].add(new double[] { (double) to, weight });
            graph[to].add(new double[] { (double) from, weight });
        }

        double[] prob = new double[n];

        Arrays.fill(prob, -1);

        prob[start_node] = 1;
        Queue<State> q = new PriorityQueue<>((a, b) -> {
            return Double.compare(b.p, a.p);
        });

        q.offer(new State(start_node, 1));
        while (!q.isEmpty()) {
            State s = q.poll();
            if (s.p < prob[s.id]) {
                continue;
            }

            for (double[] e: graph[s.id]) {
                if (e[1] * s.p > prob[(int)e[0]]) {
                    prob[(int)e[0]] = e[1] * s.p;
                    q.offer(new State((int)e[0], e[1] * s.p));
                }
            }
        }

        return prob[end_node] >= 0 ? prob[end_node] : 0;
    }

    class State {
        int id;
        double p;

        State(int id, double p) {
            this.id = id;
            this.p = p;
        }
    }
}
// @lc code=end
