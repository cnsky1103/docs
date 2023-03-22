import java.util.Arrays;

/*
 * @lc app=leetcode id=1024 lang=java
 *
 * [1024] Video Stitching
 */

// @lc code=start
class Solution {
    public int videoStitching(int[][] clips, int time) {
        if (time == 0) {
            return 0;
        }
        Arrays.sort(clips, (a, b) -> {
            if (a[0] == b[0]) {
                return b[1] - a[1];
            }
            return a[0] - b[0];
        });

        if (clips[0][0] != 0) {
            return -1;
        }
        int ans = 1;
        int curClip = 0;
        while (curClip < clips.length && clips[curClip][1] < time) {
            int nextClip = curClip + 1;
            if (nextClip >= clips.length) {
                return -1;
            }
            int nextLongest = nextClip;
            while (nextClip < clips.length) {
                if (clips[nextClip][0] <= clips[curClip][1]) {
                    if (clips[nextClip][1] >= clips[nextLongest][1]) {
                        nextLongest = nextClip;
                    }
                }
                nextClip++;
            }

            if (clips[nextLongest][0] > clips[curClip][1]) {
                return -1;
            }

            ans++;
            if (clips[nextLongest][1] >= time) {
                return ans;
            }
            curClip = nextLongest;
        }

        if (clips[curClip][1] >= time) {
            return ans;
        }
        return -1;
    }
}
// @lc code=end
