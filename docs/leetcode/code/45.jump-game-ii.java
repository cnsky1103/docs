/*
 * @lc app=leetcode id=45 lang=java
 *
 * [45] Jump Game II
 */

// @lc code=start
class Solution {
    public int jump(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[n - 1] = 0;
        for (int i = n - 2; i >= 0; i--) {
            dp[i] = 1145141919;
            for (int j = 1; j <= nums[i] && j + i < n; j++) {
                if (1 + dp[i + j] < dp[i]) {
                    dp[i] = 1 + dp[i + j];
                }
            }
        }
        return dp[0];
    }
}
// @lc code=end
