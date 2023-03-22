/*
 * @lc app=leetcode id=698 lang=java
 *
 * [698] Partition to K Equal Sum Subsets
 */

// @lc code=start
class Solution {
    public boolean canPartitionKSubsets(int[] nums, int k) {
        if (k > nums.length) {
            return false;
        }
        int n = nums.length;
        boolean[] used = new boolean[n];
        int target = 0;
        for (int num : nums) {
            target += num;
        }

        if (target % k != 0) {
            return false;
        }

        target /= k;

        return backtrack(k, 0, nums, 0, used, target);
    }

    boolean backtrack(int k, int bucket,
            int[] nums, int start, boolean[] used, int target) {

        if (k == 0) {
            return true;
        }

        if (bucket == target) {
            return backtrack(k - 1, 0, nums, 0, used, target);
        }

        for (int i = start; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }

            if (nums[i] + bucket > target) {
                continue;
            }

            used[i] = true;
            bucket += nums[i];
            if (backtrack(k, bucket, nums, i + 1, used, target)) {
                return true;
            }
            bucket -= nums[i];
            used[i] = false;
        }

        return false;
    }
}
// @lc code=end
