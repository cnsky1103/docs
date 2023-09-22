/*
 * @lc app=leetcode id=496 lang=java
 *
 * [496] Next Greater Element I
 */

// @lc code=start
class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> map = new HashMap<>();
        Stack<Integer> s = new Stack<>();

        int n = nums2.length;
        for (int i = n - 1; i >= 0; i--) {
            while (!s.isEmpty() && s.peek() <= nums2[i]) {
                s.pop();
            }

            map.put(nums2[i], s.isEmpty() ? -1 : s.peek());
            s.push(nums2[i]);
        }

        int[] ans = new int[nums1.length];
        for (int i = 0; i < nums1.length; ++i) {
            ans[i] = map.get(nums1[i]);
        }

        return ans;
    }
}
// @lc code=end

