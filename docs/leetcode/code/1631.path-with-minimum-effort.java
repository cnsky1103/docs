/*
 * @lc app=leetcode id=1631 lang=java
 *
 * [1631] Path With Minimum Effort
 */

import java.util.Queue;
import java.util.Arrays;
import java.util.PriorityQueue;
// @lc code=start

class Solution {
    public int minimumEffortPath(int[][] heights) {
        int row = heights.length;
        int col = heights[0].length;

        int[][] ans = new int[row][col];

        Queue<State> q = new PriorityQueue<>((a, b) -> {
            return a.dist - b.dist;
        });

        for (int i = 0; i < row; i++) {
            Arrays.fill(ans[i], Integer.MAX_VALUE);
        }

        ans[0][0] = 0;
        q.offer(new State(0, 0, 0));

        while (!q.isEmpty()) {
            State s = q.poll();
            if (s.dist < ans[s.r][s.c]) {
                ans[s.r][s.c] = s.dist;
            }

            if (s.r != 0) {
                // go up
                int diff = Math.max(ans[s.r][s.c], Math.abs(heights[s.r - 1][s.c] - heights[s.r][s.c]));
                if (diff < ans[s.r - 1][s.c]) {
                    ans[s.r - 1][s.c] = diff;
                    q.offer(new State(s.r - 1, s.c, diff));
                }
            }
            if (s.r != row - 1) {
                // go down
                int diff = Math.max(ans[s.r][s.c], Math.abs(heights[s.r + 1][s.c] - heights[s.r][s.c]));
                if (diff < ans[s.r + 1][s.c]) {
                    ans[s.r + 1][s.c] = diff;
                    q.offer(new State(s.r + 1, s.c, diff));
                }
            }
            if (s.c != 0) {
                // go left
                int diff = Math.max(ans[s.r][s.c], Math.abs(heights[s.r][s.c - 1] - heights[s.r][s.c]));
                if (diff < ans[s.r][s.c - 1]) {
                    ans[s.r][s.c - 1] = diff;
                    q.offer(new State(s.r, s.c - 1, diff));
                }
            }
            if (s.c != col - 1) {
                // go right
                int diff = Math.max(ans[s.r][s.c], Math.abs(heights[s.r][s.c + 1] - heights[s.r][s.c]));
                if (diff < ans[s.r][s.c + 1]) {
                    ans[s.r][s.c + 1] = diff;
                    q.offer(new State(s.r, s.c + 1, diff));
                }
            }
        }

        return ans[row - 1][col - 1];
    }

    class State {
        int r;
        int c;
        int dist;

        State(int r, int c, int dist) {
            this.r = r;
            this.c = c;
            this.dist = dist;
        }
    }
}
// @lc code=end
