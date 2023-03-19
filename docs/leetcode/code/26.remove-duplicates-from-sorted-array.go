/*
 * @lc app=leetcode id=26 lang=golang
 *
 * [26] Remove Duplicates from Sorted Array
 */
package code
// @lc code=start
func removeDuplicates(nums []int) int {
    i := 0
	j := i + 1
	for j < len(nums) {
		for nums[j] == nums[i] {
			j++
			if j >= len(nums) {
				return i + 1
			}
		}
		nums[i+1] = nums[j]
		i++
	}
	return i + 1
}
// @lc code=end

