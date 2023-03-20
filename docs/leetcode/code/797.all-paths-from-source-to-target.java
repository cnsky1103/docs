import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
 * @lc app=leetcode id=797 lang=java
 *
 * [797] All Paths From Source to Target
 */

// @lc code=start
class Solution {
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        int n = graph.length;
        List<List<Integer>> ans = new LinkedList<>();
        Queue<List<Integer>> open_list = new LinkedList<>();
        List<Integer> path = new LinkedList<>();
        path.add(0);
        open_list.offer(path);
        while (!open_list.isEmpty()) {
            List<Integer> curPath = open_list.poll();

            int tail = curPath.get(curPath.size() - 1);
            if (tail == n - 1) {
                ans.add(new LinkedList<>(curPath));
            }

            for (int next : graph[tail]) {
                List<Integer> nextPath = new LinkedList<>(curPath);
                nextPath.add(next);
                open_list.offer(nextPath);
            }
        }

        return ans;
    }
}
// @lc code=end
