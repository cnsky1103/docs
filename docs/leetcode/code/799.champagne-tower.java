/*
 * @lc app=leetcode id=799 lang=java
 *
 * [799] Champagne Tower
 */

// @lc code=start
class Solution {
    double[][] cups = new double[100][101];

    public double champagneTower(int poured, int query_row, int query_glass) {
        cups[0][0] = poured;
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j <= i; j++) {
                if (cups[i][j] > 1 && i != 99) {
                    cups[i + 1][j] += (cups[i][j] - 1) / 2;

                    if (j != 100) {
                        cups[i + 1][j + 1] += (cups[i][j] - 1) / 2;
                    }
                    cups[i][j] = 1;
                }
            }
        }
        return cups[query_row][query_glass];
    }
}
// @lc code=end
