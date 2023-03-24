/*
 * @lc app=leetcode id=130 lang=java
 *
 * [130] Surrounded Regions
 */

// @lc code=start
class Solution {
    public void solve(char[][] board) {
        int m = board.length;
        int n = board[0].length;
        for (int i = 0; i < m; i++) {
            if (board[i][0] == 'O') {
                floodfill(board, i, 0, '.');
            }
            if (board[i][n - 1] == 'O') {
                floodfill(board, i, n - 1, '.');
            }
        }

        for (int i = 0; i < n; i++) {
            if (board[0][i] == 'O') {
                floodfill(board, 0, i, '.');
            }
            if (board[m - 1][i] == 'O') {
                floodfill(board, m - 1, i, '.');
            }
        }

        for (int i = 1; i < m - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (board[i][j] == 'O') {
                    floodfill(board, i, j, 'X');
                }
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == '.') {
                    board[i][j] = 'O';
                }
            }
        }
    }

    // flood fill from 'O' to target
    void floodfill(char[][] board, int x, int y, char target) {
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length) {
            return;
        }
        if (board[x][y] != 'O') {
            return;
        }

        board[x][y] = target;

        floodfill(board, x - 1, y, target);
        floodfill(board, x, y - 1, target);
        floodfill(board, x + 1, y, target);
        floodfill(board, x, y + 1, target);
    }
}
// @lc code=end
