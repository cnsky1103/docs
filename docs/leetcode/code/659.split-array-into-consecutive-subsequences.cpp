/*
 * @lc app=leetcode id=659 lang=cpp
 *
 * [659] Split Array into Consecutive Subsequences
 */

#include "vector"
#include "unordered_map"
using namespace std;
// @lc code=start
class Solution
{
public:
    bool isPossible(vector<int> &nums)
    {
        unordered_map<int, int> freq, need;

        for (int v : nums)
        {
            freq[v]++;
        }

        for (int v : nums)
        {
            if (freq[v] == 0)
            {
                continue;
            }

            if (freq[v] > 0 && need[v] > 0)
            {
                freq[v]--;
                need[v]--;
                need[v + 1]++;
            }
            else if (freq[v] > 0 && freq[v + 1] > 0 && freq[v + 2] > 0)
            {
                freq[v]--;
                freq[v + 1]--;
                freq[v + 2]--;
                need[v + 3]++;
            } else {
                return false;
            }
        }

        return true;
    }
};
// @lc code=end
