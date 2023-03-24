import java.util.HashSet;
import java.util.Set;

/*
 * @lc app=leetcode id=128 lang=java
 *
 * [128] Longest Consecutive Sequence
 */

// @lc code=start
class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> s = new HashSet<>();
        for (int n : nums) {
            s.add(n);
        }

        int ans = 0;
        for (int n : nums) {
            if (s.contains(n - 1)) {
                continue;
            }
            int len = 0;
            int cur = n;
            while (s.contains(cur)) {
                cur++;
                len++;
            }
            ans = Math.max(ans, len);
        }

        return ans;
    }
}
// @lc code=end
