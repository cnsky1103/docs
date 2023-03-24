import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=1218 lang=java
 *
 * [1218] Longest Arithmetic Subsequence of Given Difference
 */

// @lc code=start
class Solution {
    public int longestSubsequence(int[] arr, int difference) {
        Map<Integer, Integer> m = new HashMap<>();

        int res = 1;
        for (int a : arr) {
            int v = m.getOrDefault(a - difference, 0) + 1;
            m.put(a, v);
            res = Math.max(res, v);
        }

        return res;
    }
}
// @lc code=end
