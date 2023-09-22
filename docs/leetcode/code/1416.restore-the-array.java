/*
 * @lc app=leetcode id=1416 lang=java
 *
 * [1416] Restore The Array
 */

// @lc code=start
class Solution {
    public int numberOfArrays(String s, int k) {
        char[] sc = s.toCharArray();
        int n = sc.length;
        int[] nums = new int[n];
        int consecutive_zeroes = 0;
        int max_cons_zeroes = 0;
        for (int i = 0; i < n; i++) {
            nums[i] = (int) (sc[i] - '0');
            if (nums[i] == 0) {
                consecutive_zeroes++;
                max_cons_zeroes = Math.max(max_cons_zeroes, consecutive_zeroes);
            } else {
                consecutive_zeroes = 0;
            }
        }
        if (Math.pow(10, consecutive_zeroes) > k) {
            return 0;
        }
        if (nums[0] > k) {
            return 0;
        }
        // dp[i] = numberOfArrays(s[..=i], k)
        long[] dp = new long[n];
        dp[0] = 1;

        for (int i = 1; i < n; i++) {
            int p = i;
            long num = nums[i];
            long base = 1;
            while (p >= 0 && num <= k) {
                if (p == 0 && num <= k) {
                    dp[i] += 1;
                    dp[i] %= 1e9 + 7;
                    break;
                }
                if (nums[p] != 0) {
                    dp[i] += dp[p - 1];
                    dp[i] %= 1e9 + 7;
                }

                p--;
                base *= 10;
                num += base * nums[p];
            }
        }

        return (int) dp[n - 1];
    }
}
// @lc code=end
