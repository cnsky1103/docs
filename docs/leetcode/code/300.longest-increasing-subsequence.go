/*
 * @lc app=leetcode id=300 lang=golang
 *
 * [300] Longest Increasing Subsequence
 */
package code

// @lc code=start
func lengthOfLIS(nums []int) int {
	dp := make([]int, len(nums))
	for i := 0; i < len(nums); i++ {
		dp[i] = 1
	}
	res := dp[0]
	for i := 1; i < len(nums); i++ {
		for j := 0; j < i; j++ {
			if nums[j] < nums[i] {
				dp[i] = max(dp[i], dp[j] + 1)
			}
		}
		res = max(res, dp[i])
	}
	return res
}

func max(a, b int) int {
	if a > b {
		return a
	} else {
		return b
	}
}

// @lc code=end
