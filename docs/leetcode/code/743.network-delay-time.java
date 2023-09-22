/*
 * @lc app=leetcode id=743 lang=java
 *
 * [743] Network Delay Time
 */

import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

// @lc code=start
class Solution {
    public int networkDelayTime(int[][] times, int n, int k) {
        List<int[]>[] graph = new ArrayList[n + 1];

        for (int i = 1; i <= n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int[] edge : times) {
            graph[edge[0]].add(new int[] { edge[1], edge[2] });
        }

        int[] distTo = dijkstra(graph, k);

        int res = 0;
        for (int i = 1; i < distTo.length; i++) {
            if (distTo[i] == Integer.MAX_VALUE) {
                return -1;
            }
            res = Math.max(res, distTo[i]);
        }
        return res;
    }

    int[] dijkstra(List<int[]>[] graph, int start) {
        int[] distTo = new int[graph.length];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        distTo[start] = 0;
        State starState = new State(start, 0);
        Queue<State> q = new PriorityQueue<>((a, b) -> {
            return a.dist - b.dist;
        });
        q.offer(starState);

        while (!q.isEmpty()) {
            State s = q.poll();

            if (s.dist > distTo[s.id]) {
                continue;
            }

            for (int[] edge : graph[s.id]) {
                if (s.dist + edge[1] < distTo[edge[0]]) {
                    q.offer(new State(edge[0], s.dist + edge[1]));
                    distTo[edge[0]] = s.dist + edge[1];
                }
            }
        }

        return distTo;
    }

    class State {
        int dist;
        int id;

        State(int id, int dist) {
            this.id = id;
            this.dist = dist;
        }
    }
}
// @lc code=end
