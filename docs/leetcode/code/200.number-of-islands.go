/*
 * @lc app=leetcode id=200 lang=golang
 *
 * [200] Number of Islands
 */
package code
// @lc code=start
func numIslands(grid [][]byte) int {
	if len(grid) == 0 {
        return 0
    }
    
    m, n := len(grid), len(grid[0])
    count := 0
    
    for i := 0; i < m; i++ {
        for j := 0; j < n; j++ {
            if grid[i][j] == '1' {
                count++
                dfs(grid, i, j, m, n)
            }
        }
    }
    
    return count
}

func dfs(grid [][]byte, i, j, m, n int) {
	if i < 0 || i >= m || j < 0 || j >= n || grid[i][j] == '0' {
        return
    }
    
    grid[i][j] = '0'
    dfs(grid, i-1, j, m, n)
    dfs(grid, i+1, j, m, n)
    dfs(grid, i, j-1, m, n)
    dfs(grid, i, j+1, m, n)
}
// @lc code=end

