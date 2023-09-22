/*
 * @lc app=leetcode id=2225 lang=java
 *
 * [2225] Find Players With Zero or One Losses
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
// @lc code=start

class Solution {
    public List<List<Integer>> findWinners(int[][] matches) {
        Set<Integer> lose0 = new HashSet<>();
        Set<Integer> lose1 = new HashSet<>();
        Set<Integer> lose_many = new HashSet<>();

        for (int[] match : matches) {
            int w = match[0];
            int l = match[1];
            if (lose0.contains(l)) {
                lose0.remove(l);
                lose1.add(l);
            } else if (lose1.contains(l)) {
                lose1.remove(l);
                lose_many.add(l);
            } else if (!lose_many.contains(l)) {
                lose1.add(l);
            }
            if (!lose1.contains(w) && !lose_many.contains(w)) {
                lose0.add(w);
            }
        }

        return new ArrayList<List<Integer>>() {
            {
                add(lose0.stream().sorted().collect(Collectors.toList()));
                add(lose1.stream().sorted().collect(Collectors.toList()));
            }
        };

    }
}
// @lc code=end
