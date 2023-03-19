import java.util.LinkedList;
import java.util.Queue;

/*
 * @lc app=leetcode id=111 lang=java
 *
 * [111] Minimum Depth of Binary Tree
 */

/**
 * Definition for a binary tree node.
 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

// @lc code=start
class Solution {
    public int minDepth(TreeNode root) {
        if (root == null) return 0;
        int depth = 1;
        Queue<TreeNode> open_list = new LinkedList<>();
        open_list.offer(root);

        while (!open_list.isEmpty()) {
            int size = open_list.size();

            for (int i = 0; i < size; i++) {
                TreeNode n = open_list.poll();

                if (n.left == null && n.right == null) {
                    return depth;
                }

                if (n.left != null) {
                    open_list.offer(n.left);
                }

                if (n.right != null) {
                    open_list.offer(n.right);
                }
            }

            depth++;
        }

        return depth;
    }
}
// @lc code=end
