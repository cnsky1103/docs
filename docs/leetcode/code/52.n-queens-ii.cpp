/*
 * @lc app=leetcode id=52 lang=cpp
 *
 * [52] N-Queens II
 */

// @lc code=start
#include "vector"
#include "string"
using namespace std;
class Solution {
public:
    int res;
    int totalNQueens(int n) {
        res = 0;
        vector<string> board(n, string(n, '.'));
        backtrack(board, 0);
        return res;
    }

    void backtrack(vector<string> &board, int row)
    {
        if (row >= board.size())
        {
            res++;
            return;
        }

        int n = board[row].size();
        for (int col = 0; col < n; ++col)
        {
            if (!isValid(board, row, col))
            {
                continue;
            }

            board[row][col] = 'Q';
            backtrack(board, row + 1);
            board[row][col] = '.';
        }
    }

    bool isValid(vector<string> &board, int row, int col)
    {
        for (int i = row - 1; i >= 0; --i)
        {
            if (board[i][col] == 'Q')
            {
                return false;
            }
        }

        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; --i, --j)
        {
            if (board[i][j] == 'Q')
            {
                return false;
            }
        }

        for (int i = row - 1, j = col + 1; i >= 0 && j <= board[0].size(); --i, ++j)
        {
            if (board[i][j] == 'Q')
            {
                return false;
            }
        }

        return true;
    }
};
// @lc code=end

