/*
 * @lc app=leetcode id=494 lang=golang
 *
 * [494] Target Sum
 */
package code

// @lc code=start
func findTargetSumWays(nums []int, target int) int {
	return backtrack(nums, 0, target)
}

func backtrack(nums []int, i int, target int) int {
	if i >= len(nums) {
		if target == 0 {
			return 1
		} else {
			return 0
		}
	}
	return backtrack(nums, i+1, target-nums[i]) + backtrack(nums, i+1, target+nums[i])
}

// @lc code=end
