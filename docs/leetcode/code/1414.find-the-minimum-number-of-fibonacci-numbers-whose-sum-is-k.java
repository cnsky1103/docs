/*
 * @lc app=leetcode id=1414 lang=java
 *
 * [1414] Find the Minimum Number of Fibonacci Numbers Whose Sum Is K
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// @lc code=start
class Solution {
    List<Integer> list;

    public int findMinFibonacciNumbers(int k) {
        list = new ArrayList<>();
        list.add(1);
        list.add(1);
        int pre1 = 1;
        int pre2 = 1;
        int n;
        while ((n = pre1 + pre2) <= k) {
            list.add(n);
            pre2 = pre1;
            pre1 = n;
        }
        return find(k);
    }

    int find(int k) {
        if (k <= 3) {
            return 1;
        }
        // the index of the search key, if it is contained in the list; otherwise,
        // (-(insertion point) - 1). The insertion point is defined as the point at
        // which the key would be inserted into the list: the index of the first element
        // greater than the key, or list.size() if all elements in the list are less
        // than the specified key.
        int i = Collections.binarySearch(list, k);
        if (i >= 0) {
            return 1;
        }

        int insertion_point = -(1 + i);
        int next_smaller = list.get(insertion_point - 1);
        int n = 0;
        while (k >= next_smaller) {
            k -= next_smaller;
            n++;
        }
        return n + find(k);
    }
}
// @lc code=end
