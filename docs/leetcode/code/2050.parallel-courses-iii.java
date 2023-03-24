import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/*
 * @lc app=leetcode id=2050 lang=java
 *
 * [2050] Parallel Courses III
 */

// @lc code=start
class Node {
    List<Node> in;
    List<Node> out;
    int duration;
    int minTime; // min time to finish all the prereqs of this class

    Node(int d) {
        in = new LinkedList<>();
        out = new LinkedList<>();
        duration = d;
        minTime = 0;
    }
}

class Solution {
    public int minimumTime(int n, int[][] relations, int[] time) {
        Node[] classes = new Node[n];
        for (int i = 0; i < n; i++) {
            classes[i] = new Node(time[i]);
        }

        for (int[] relation : relations) {
            classes[relation[0] - 1].out.add(classes[relation[1] - 1]);

            classes[relation[1] - 1].in.add(classes[relation[0] - 1]);
        }

        Queue<Node> q = new LinkedList<>();
        for (Node c : classes) {
            if (c.in.size() == 0) {
                q.offer(c);
            }
        }

        int ans = 0;
        while (!q.isEmpty()) {
            Node c = q.poll();
            if (c.out.size() == 0) {
                ans = Math.max(ans, c.duration + c.minTime);
                continue;
            }

            for (Node o : c.out) {
                o.minTime = Math.max(o.minTime, c.duration + c.minTime);

                o.in.remove(c);

                if (o.in.size() == 0) {
                    q.offer(o);
                }
            }
        }

        return ans;
    }
}
// @lc code=end
