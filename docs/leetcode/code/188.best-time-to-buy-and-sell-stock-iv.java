/*
 * @lc app=leetcode id=188 lang=java
 *
 * [188] Best Time to Buy and Sell Stock IV
 */

// @lc code=start
class Solution {
    public int maxProfit(int k, int[] prices) {
        int days = prices.length;
        int[][][] dp = new int[days + 1][k + 1][2];
        for (int y = 0; y <= k; y++) {
            dp[0][y][0] = 0;
            dp[0][y][1] = Integer.MIN_VALUE;
        }

        for (int x = 0; x <= days; x++) {
            dp[x][0][0] = 0;
            dp[x][0][1] = Integer.MIN_VALUE;
        }

        for (int x = 1; x <= days; x++) {
            for (int y = 1; y <= k; y++) {
                dp[x][y][0] = Integer.max(dp[x - 1][y][0], dp[x - 1][y][1] + prices[x - 1]);

                dp[x][y][1] = Integer.max(dp[x - 1][y][1], dp[x - 1][y - 1][0] - prices[x - 1]);
            }
        }

        return dp[days][k][0];
    }
}
// @lc code=end
