/*
 * @lc app=leetcode id=48 lang=golang
 *
 * [48] Rotate Image
 */
package code

// @lc code=start
func rotate(matrix [][]int) {
	transform(matrix)
	for _, row := range matrix {
		reverseRow(row)
	}
}

func transform(matrix [][]int) {
	n := len(matrix)
	for i := 0; i < n; i++ {
		for j := i; j < n; j++ {
			//swap (i,j) and (j,i)
			temp := matrix[i][j]
			matrix[i][j] = matrix[j][i]
			matrix[j][i] = temp
		}
	}
}

func reverseRow(row []int) {
	n := len(row)
	i := 0
	j := n - 1
	for (i <= j) {
		temp := row[i]
		row[i] = row[j]
		row[j] = temp
		i++
		j--
	}
}

// @lc code=end
