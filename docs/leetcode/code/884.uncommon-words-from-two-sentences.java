import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * @lc app=leetcode id=884 lang=java
 *
 * [884] Uncommon Words from Two Sentences
 */

// @lc code=start
class Solution {
    public String[] uncommonFromSentences(String s1, String s2) {
        Map<String, Integer> m = new HashMap<>();
        List<String> ans = new LinkedList<>();
        for (String s: s1.split(" ")) {
            m.put(s, m.getOrDefault(s, 0) + 1);
        }

        for (String s: s2.split(" ")) {
            m.put(s, m.getOrDefault(s, 0) + 1);
        }

        for (Map.Entry<String, Integer> e: m.entrySet()) {
            if (e.getValue() == 1) {
                ans.add(e.getKey());
            }
        }

        return ans.toArray(new String[0]);
    }
}
// @lc code=end
