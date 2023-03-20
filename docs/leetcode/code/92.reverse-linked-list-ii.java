/*
 * @lc app=leetcode id=92 lang=java
 *
 * [92] Reverse Linked List II
 */

/**
 * Definition for singly-linked list.
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
    public ListNode reverseBetween(ListNode head, int left, int right) {
        if (left == 1) {
            return reverseN(head, right);
        } else {
            head.next = reverseBetween(head.next, left - 1, right - 1);

            return head;
        }
    }

    ListNode succeessor;
    ListNode reverseN(ListNode head, int n) {
        if (n == 1) {
            succeessor = head.next;
            return head;
        }

        ListNode tail = reverseN(head.next, n - 1);
        head.next.next = head;
        head.next = succeessor;

        return tail;
    }
}
// @lc code=end
