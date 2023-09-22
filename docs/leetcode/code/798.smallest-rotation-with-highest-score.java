/*
 * @lc app=leetcode id=798 lang=java
 *
 * [798] Smallest Rotation with Highest Score
 */

import java.util.HashMap;
import java.util.Map;
// @lc code=start

class Solution {
    public static int bestRotation(int[] nums) {
        // suppose nums[i] = x, it scores if x is put at nums[x..]
        // there are two cases,
        // case1: i < x
        // we need to rotate at least (i+1) to put x to the end so it scores
        // and at most (i+1+(n-1-x)) where nums[x]=x
        // case2: i >= x
        // at least rotate 0, and at most rotate i-x
        // but also we can rotate in reverse way, that is,
        // n-1-i to right, to put x to the end
        // we can denote this by negative numbers, that is, 1+i-n
        int n = nums.length;
        int[][] intervals = new int[n][2];
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            int x = nums[i];
            if (i < x) {
                intervals[i][0] = i + 1;
                intervals[i][1] = n + i - x;
            } else {
                intervals[i][0] = 1 + i - n;
                intervals[i][1] = i - x;
            }
            max = Integer.max(max, intervals[i][1]);
            min = Integer.min(min, intervals[i][0]);
        }
        System.out.println(max);
        System.out.println(min);

        // find the most overlapping interval
        /*
         * for (int[] inter : intervals) {
         * inter[0] -= min;
         * inter[1] -= min;
         * }
         * 
         * int[] helper = new int[max - min + 1];
         * for (int[] inter : intervals) {
         * helper[inter[0]]++;
         * if (inter[1] + 1 < helper.length) {
         * helper[inter[1] + 1]--;
         * }
         * }
         * 
         * int max_val = -1;
         * int max_point = -1;
         * 
         * int val = 0;
         * for (int i = 0; i < helper.length; i++) {
         * val += helper[i];
         * if (val > max_val) {
         * max_val = val;
         * max_point = i;
         * }
         * }
         */
        Map<Integer, Integer> m = new HashMap<>();
        for (int[] inter : intervals) {
            for (int j = inter[0]; j <= inter[1]; j++) {
                int actual = j >= 0 ? j : j + n;
                m.put(actual, m.getOrDefault(actual, 0) + 1);
            }
        }

        int max_point = min;
        int max_val = Integer.MIN_VALUE;
        int actual_max = Integer.max(max, min < 0 ? min + n : min);
        for (int i = 0; i <= actual_max; i++) {
            if (m.containsKey(i)) {
                if (m.get(i) > max_val) {
                    max_val = m.get(i);
                    max_point = i;
                }
            }
        }

        return max_point;
    }
}
// @lc code=end
