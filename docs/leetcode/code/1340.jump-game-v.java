import java.util.Arrays;

/*
 * @lc app=leetcode id=1340 lang=java
 *
 * [1340] Jump Game V
 */

// @lc code=start
class Solution {
    int[] dp;

    public int maxJumps(int[] arr, int d) {
        int n = arr.length;
        if (n <= 2) {
            return n;
        }
        dp = new int[n];
        Arrays.fill(dp, -1);
        int max = 0;
        for (int i = 0; i < n; i++) {
            max = Math.max(max, jump(arr, i, d));
        }
        return max;
    }

    int jump(int[] arr, int i, int d) {
        if (dp[i] != -1) {
            return dp[i];
        }

        int n = arr.length;

        if (i == 0 && arr[1] > arr[0]) {
            dp[i] = 1;
            return dp[i];
        }

        if (i == n - 1 && arr[n - 2] > arr[n - 1]) {
            dp[i] = 1;
            return dp[i];
        }

        int left = i - 1, right = i + 1;
        int left_next = -1, right_next = -1;
        while (left >= 0 && left >= i - d) {
            if (arr[left] < arr[i]) {
                if (left_next == -1) {
                    left_next = left;
                } else {
                    if (arr[left] >= arr[left_next]) {
                        left_next = left;
                    }
                }
            } else {
                break;
            }
            left--;
        }

        while (right < n && right <= i + d) {
            if (arr[right] < arr[i]) {
                if (right_next == -1) {
                    right_next = right;
                } else {
                    if (arr[right] >= arr[right_next]) {
                        right_next = right;
                    }
                }
            } else {
                break;
            }
            right++;
        }

        int l = 0;
        if (left_next < i && left_next >= 0) {
            left = left_next;

            while (left < i) {
                if (arr[left] == arr[left_next]) {
                    l = Math.max(l, jump(arr, left, d));
                }
                left++;
            }
        }
        int r = 0;

        if (right_next > i && right_next < n) {
            right = right_next;

            while (right > i) {
                if (arr[right] == arr[right_next]) {
                    r = Math.max(r, jump(arr, right, d));
                }
                right--;
            }
        }

        dp[i] = 1 + Math.max(l, r);
        return dp[i];
    }
}
// @lc code=end
