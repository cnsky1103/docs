/*
 * @lc app=leetcode id=344 lang=golang
 *
 * [344] Reverse String
 */
package code

// @lc code=start
func reverseString(s []byte) {
	left := 0
	right := len(s) - 1
	for left < right {
		temp := s[left]
		s[left] = s[right]
		s[right] = temp
		left++
		right--
	}
}

// @lc code=end
