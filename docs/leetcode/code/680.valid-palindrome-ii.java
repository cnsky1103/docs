/*
 * @lc app=leetcode id=680 lang=java
 *
 * [680] Valid Palindrome II
 */

// @lc code=start
class Solution {
    public boolean validPalindrome(String s) {
        return validPalindrome(s, 0, s.length() - 1, false);
    }

    boolean validPalindrome(String s, int l, int r, boolean delete) {
        if (l >= r) {
            return true;
        }

        if (s.charAt(l) == s.charAt(r)) {
            return validPalindrome(s, l + 1, r - 1, delete);
        }

        if (delete) {
            return false;
        }

        return validPalindrome(s, l + 1, r, true) || validPalindrome(s, l, r - 1, true);
    }
}
// @lc code=end
