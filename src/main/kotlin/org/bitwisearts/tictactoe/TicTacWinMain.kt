package org.bitwisearts.tictactoe

/**
 * A simple way to interact with the Tic Tac Toe winner determination logic.
 */
fun main()
{
	// prints the WinningBoard template populated with all winning boards to
	// the console
	// println(template())

	// Creates an integer that represents a Tic Tac Toe board (TicTacToeBoard.board)
	val board1 = TicTacToeBoard.board("XO-OX--XO")
	board1?.let {
		println(it.visualBoard)
		println(it.winner)
	}
	println()
	TicTacToeBoard.board("XO-OXO--X")?.let {
		println(it.visualBoard)
		println(it.winner)
	}
	println()
	TicTacToeBoard.board("OOOOOOOOO")?.let {
		println(it.visualBoard)
		println(it.winner)
	} ?: println("Invalid board")
	println()
	TicTacToeBoard.board("XXXXXXXXX")?.let {
		println(it.visualBoard)
		println(it.winner)
	} ?: println("Invalid board")
	println()
	TicTacToeBoard.board("O")?.let {
		println(it.visualBoard)
		println(it.winner)
	} ?: println("Invalid board")
	println()
	TicTacToeBoard.board("This isn't even trying to be a board")?.let {
		println(it.visualBoard)
		println(it.winner)
	} ?: println("Invalid board")
}
