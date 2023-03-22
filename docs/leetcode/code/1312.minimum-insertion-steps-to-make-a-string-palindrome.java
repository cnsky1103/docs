import java.util.Arrays;

/*
 * @lc app=leetcode id=1312 lang=java
 *
 * [1312] Minimum Insertion Steps to Make a String Palindrome
 */

// @lc code=start
class Solution {
    int[][] memo;

    public int minInsertions(String s) {
        int n = s.length();
        // dp[i][j] represents that, how many steps we need to make s[:i] and s[j+1:]
        // palindrome
        memo = new int[n][n];
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            min = Math.min(min, dp(s, i, i));
        }

        for (int i = 1; i < n; i++) {
            if (s.charAt(i - 1) == s.charAt(i)) {
                min = Math.min(min, dp(s, i - 1, i));
            }
        }

        return min;
    }

    int dp(String s, int i, int j) {
        int n = s.length();
        if (i <= 0) {
            return n - j - 1;
        }
        if (j >= n - 1) {
            return i;
        }

        if (memo[i][j] != -1) {
            return memo[i][j];
        }

        if (s.charAt(i - 1) == s.charAt(j + 1)) {
            memo[i][j] = dp(s, i - 1, j + 1);
        } else {
            memo[i][j] = 1 + Math.min(dp(s, i - 1, j), dp(s, i, j + 1));
        }

        return memo[i][j];
    }
}
// @lc code=end

/*
 * mbadm
 * dp(s,0,0) => 4 => mdabmdadm
 * dp(s,1,1) => dp(s,0,1) => 3 => mb(m)adm => mdamb(m)adm
 *           => dp(s,1,2) => m(a)badm
 */