/*
 * @lc app=leetcode id=5 lang=golang
 *
 * [5] Longest Palindromic Substring
 */
package code

// @lc code=start
func longestPalindrome(s string) string {
	res := ""

	for i := 0; i < len(s); i++ {
		s1 := longestPalindromeLR(s, i, i)
		s2 := longestPalindromeLR(s, i, i+1)
		if len(res) <= len(s1) {
			res = s1
		}
		if len(res) <= len(s2) {
			res = s2
		}
	}
	return res
}

func longestPalindromeLR(s string, l, r int) string {
	for l >= 0 && r < len(s) && s[l] == s[r] {
		l--
		r++
	}

	return s[l+1 : r]
}

// @lc code=end
