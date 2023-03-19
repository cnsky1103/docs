import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=337 lang=java
 *
 * [337] House Robber III
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
    Map<TreeNode, Integer> doRob = new HashMap<>();
    Map<TreeNode, Integer> notRob = new HashMap<>();

    public int rob(TreeNode root) {
        return robTree(root);
    }

    int robTree(TreeNode root) {
        if (root == null) {
            return 0;
        }

        int l = robTree(root.left);
        int r = robTree(root.right);

        doRob.put(root, notRob.getOrDefault(root.left, 0) + notRob.getOrDefault(root.right, 0) + root.val);

        notRob.put(root, l + r);

        return Math.max(doRob.get(root), notRob.get(root));
    }
}
// @lc code=end
