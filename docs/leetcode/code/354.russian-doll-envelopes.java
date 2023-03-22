import java.util.Arrays;

/*
 * @lc app=leetcode id=354 lang=java
 *
 * [354] Russian Doll Envelopes
 */

// @lc code=start
class Solution {
    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (int[] a, int[] b) -> {
            return a[0] == b[0] ? a[1] - b[1] : a[0] - b[0];
        });

        int[] dp = new int[envelopes.length];
        for (int i = 0; i < envelopes.length; i++) {
            dp[i] = 1;
        }
        int max = dp[0];
        for (int i = 1; i < envelopes.length; i++) {
            for (int j = 0; j < i; j++) {
                if (fitInto(envelopes[j], envelopes[i])) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            max = Math.max(max, dp[i]);
        }
        return max;
    }

    boolean fitInto(int[] e1, int[] e2) {
        return e1[0] < e2[0] && e1[1] < e2[1];
    }
}
// @lc code=end
