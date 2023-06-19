import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/*
 * @lc app=leetcode id=347 lang=java
 *
 * [347] Top K Frequent Elements
 */

// @lc code=start
class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> m = new HashMap<>();
        PriorityQueue<Map.Entry<Integer, Integer>> q = new PriorityQueue<>(
                new Comparator<Map.Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        return o1.getValue() - o2.getValue();
                    }
                });

        for (int n : nums) {
            m.put(n, m.getOrDefault(n, 0) + 1);
        }

        Map.Entry<Integer, Integer>[] entries = m.entrySet().toArray(new Map.Entry[0]);

        int n = entries.length;
        for (int i = 0; i < k; i++) {
            q.add(entries[i]);
        }

        for (int i = k; i < n; i++) {
            if (entries[i].getValue() > q.peek().getValue()) {
                q.poll();
                q.add(entries[i]);
            }
        }

        int[] ans = new int[k];
        for (int i = 0; i < k; i++) {
            ans[i] = q.poll().getKey();
        }

        return ans;
    }
}
// @lc code=end
