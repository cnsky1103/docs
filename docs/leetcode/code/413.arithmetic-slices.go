/*
 * @lc app=leetcode id=413 lang=golang
 *
 * [413] Arithmetic Slices
 */
package code

// @lc code=start
func numberOfArithmeticSlices(nums []int) int {
	n := len(nums)
	if n <= 2 {
		return 0
	}

	dp := make([]int, n)

	dp[0] = 0
	dp[1] = 0

	ans := 0
	for i := 2; i < n; i++ {
		if nums[i]+nums[i-2] == 2*nums[i-1] {
			dp[i] = 1 + dp[i-1]
		}
		ans += dp[i]
	}

	return ans
}

// @lc code=end
