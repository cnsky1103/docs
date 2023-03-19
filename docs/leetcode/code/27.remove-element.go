/*
 * @lc app=leetcode id=27 lang=golang
 *
 * [27] Remove Element
 */
package code
// @lc code=start
func removeElement(nums []int, val int) int {
    slow := 0
	fast := 0

	for fast < len(nums) {
		if nums[fast] != val {
			nums[slow] = nums[fast]
			slow++
		}
		fast++
	}

	return slow
}
// @lc code=end

