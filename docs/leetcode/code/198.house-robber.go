/*
 * @lc app=leetcode id=198 lang=golang
 *
 * [198] House Robber
 */
package code

// @lc code=start
func rob(nums []int) int {
	dp := make([]int, len(nums))
	dp[0] = nums[0]

	if len(nums) == 1{
		return dp[0]
	}
	if nums[1] > nums[0] {
		dp[1] = nums[1]
	} else {
		dp[1] = nums[0]
	}

	for i := 2; i < len(nums); i++ {
		if dp[i-1] > dp[i-2]+nums[i] {
			dp[i] = dp[i-1]
		} else {
			dp[i] = dp[i-2] + nums[i]
		}
	}

	return dp[len(nums)-1]
}

// @lc code=end
