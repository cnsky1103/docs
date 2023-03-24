/*
 * @lc app=leetcode id=309 lang=java
 *
 * [309] Best Time to Buy and Sell Stock with Cooldown
 */

// @lc code=start
class Solution {
    public int maxProfit(int[] prices) {
        int n = prices.length;
        // dp[i][0]=day i, no stock
        // dp[i][1]=day i, have stock
        int[][] dp = new int[n + 1][2];

        for (int i = 1; i <= n; i++) {
            if (i == 1) {
                // base case 1
                dp[i][0] = 0;
                dp[i][1] = -prices[i - 1];
                continue;
            }
            if (i == 2) {
                // base case 2
                dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] + prices[i - 1]);
                dp[i][1] = Math.max(dp[i - 1][1], -prices[i - 1]);
                continue;
            }
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] + prices[i - 1]);
            dp[i][1] = Math.max(dp[i - 1][1], dp[i - 2][0] - prices[i - 1]);
        }

        return dp[n][0];
    }

}
// @lc code=end
