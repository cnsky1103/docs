/*
 * @lc app=leetcode id=807 lang=java
 *
 * [807] Max Increase to Keep City Skyline
 */

// @lc code=start
class Solution {
    public int maxIncreaseKeepingSkyline(int[][] grid) {
        int n = grid.length;
        int[] skyline_r = new int[n];
        int[] skyline_c = new int[n];

        for (int i = 0; i < n; i++) {
            int r_max = -1;
            for (int j = 0; j < n; j++) {
                r_max = Math.max(r_max, grid[i][j]);
            }
            skyline_r[i] = r_max;

            int c_max = -1;
            for (int j = 0; j < n; j++) {
                c_max = Math.max(c_max, grid[j][i]);
            }
            skyline_c[i] = c_max;
        }

        int total = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                total += Math.min(skyline_r[i], skyline_c[j]) - grid[i][j];
            }
        }

        return total;
    }
}
// @lc code=end
