/*
 * @lc app=leetcode id=55 lang=golang
 *
 * [55] Jump Game
 */
package code
// @lc code=start
func canJump(nums []int) bool {
    farthest := 0
	for i := 0; i < len(nums) - 1; i++ {
		if i + nums[i] > farthest {
			farthest = i + nums[i]
		}

		if farthest <= i {
			return false
		}
	}

	return farthest >= len(nums) - 1
}
// @lc code=end

