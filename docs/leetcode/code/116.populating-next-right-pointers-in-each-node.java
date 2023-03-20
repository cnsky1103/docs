import java.util.LinkedList;
import java.util.Queue;

/*
 * @lc app=leetcode id=116 lang=java
 *
 * [116] Populating Next Right Pointers in Each Node
 */
/*
// Definition for a Node.
*/
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node next;

    public Node() {
    }

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, Node _left, Node _right, Node _next) {
        val = _val;
        left = _left;
        right = _right;
        next = _next;
    }
};

// @lc code=start
class Solution {
    public Node connect(Node root) {
        if (root == null) {
            return root;
        }
        Queue<Node> q = new LinkedList<>();
        q.offer(root);
        while (!q.isEmpty()) {
            int size = q.size();
            Node pre = q.poll();
            if (pre.left != null) {
                q.offer(pre.left);
            }
            if (pre.right != null) {
                q.offer(pre.right);
            }
            Node nxt;
            for (int i = 1; i < size; i++) {
                nxt = q.poll();
                if (nxt.left != null) {
                    q.offer(nxt.left);
                }
                if (nxt.right != null) {
                    q.offer(nxt.right);
                }
                pre.next = nxt;
                pre = nxt;
            }
            pre.next = null;
        }

        return root;
    }
}
// @lc code=end
