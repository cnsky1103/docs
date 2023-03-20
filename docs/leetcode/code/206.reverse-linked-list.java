/*
 * @lc app=leetcode id=206 lang=java
 *
 * [206] Reverse Linked List
 */

/**
 * Definition for singly-linked list.
 * public
 */
class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

// @lc code=start
class Solution {
    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode tail = reverseList(head.next);

        head.next.next = head;
        head.next = null;
        return tail;
    }
}
// @lc code=end
