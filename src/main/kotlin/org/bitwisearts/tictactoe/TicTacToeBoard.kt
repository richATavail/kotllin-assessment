package org.bitwisearts.tictactoe

import java.util.TreeSet
import kotlin.math.abs

/**
 * Represents the state of a cell in a Tic-Tac-Toe board.
 *
 * @property boardWeight
 *   The weight of the cell state. This is used to determine the value of the
 *   total board state. It is known that in a game of Tic-Tac-Toe, there must
 *   be at least five cells marked before a win state is possible. This means
 *   that the minimum value of a board state is 5 to reach a win condition.
 * @property rowColumnWeight
 *   The weight of the cell state in the context of either a row/column or the
 *   overall board in terms of validity.
 *   1. This can be used to determine the state of any row or column based on
 *   the sum of the cell states in that row or column. If the sum of the cell
 *   states in a row or column is equal to 3 (X) or -3 (O) then we know a win
 *   condition has been reached.
 *   2. This can also be used to determine whether the board is in a valid
 *   state. Per the rules of the game, the difference between the number of "X"
 *   and "O" cells on the board must be 0 or 1. If the absolute value of the
 *   difference is greater than 1, then the board is in an invalid state.
 * @property symbol
 *   The symbol used to represent the cell state in the game board. This is used
 *   to construct boards from string representations. It can also be used to
 *   display a board.
 */
enum class CellState(
	val boardWeight: Int,
	val rowColumnWeight: Int,
	val symbol: Char
)
{
	/**
	 * The cell is empty. The ordinal value of this enum is 0 which is used
	 * to represent an empty cell in the game board as two bit positions:
	 * `00` (`0b00000000`).
	 */
	EMPTY(0, 0, '-'),

	/**
	 * The cell is marked with an "X". The ordinal value of this enum is 1
	 * which is used to represent an "X" cell in the game board as two bit
	 * positions: `01` (`0b00000001`).
	 */
	X(1, 1, 'X'),

	/**
	 * The cell is marked with an "O". The ordinal value of this enum is 2
	 * which is used to represent an "X" cell in the game board as two bit
	 * positions: `10` (`0b00000010`).
	 */
	O(1, -1, 'O'),

	/**
	 * The representation of an invalid cell state. Since the cells are
	 * represented as two bits, the maximum value of the two bits is 3, so
	 * this value is used to represent the invalid cell state of `11` that is
	 * possible in the game board representation of an integer. We use the
	 * value `1_000` to represent this state as there is no calculation that
	 * can be done on a board including this state that would result in a value
	 * of 1_000. We don't use [Integer.MAX_VALUE] or [Int.MIN_VALUE] as the
	 * presence of multiple INVALID `11` cells would result in integer overflow.
	 */
	INVALID(1_000, 1_000, '*')
	{
		override val isValid: Boolean = false
	};

	/**
	 * Determines if the cell state is a valid state. If not, the board is in an
	 * invalid state.
	 */
	open val isValid: Boolean = true

	companion object
	{
		/**
		 * Retrieves a [CellState] value based on the given integer value.
		 */
		operator fun get(value: Int): CellState =
			when (value)
			{
				0 -> EMPTY
				1 -> X
				2 -> O
				else -> INVALID
			}

		/**
		 * Retrieves a [CellState] value based on the given
		 * [character symbol][CellState.symbol].
		 */
		fun from(symbol: Char): CellState =
			when (symbol)
			{
				X.symbol -> X
				O.symbol -> O
				EMPTY.symbol -> EMPTY
				else -> INVALID
			}
	}
}

/**
 * Represents he game board for a game of Tic-Tac-Toe.
 *
 * Each cell on a Tic-Tac-Toe board can be in one of three states:
 *
 * 1. [CellState.EMPTY] - The cell is empty.
 * 2. [CellState.X] - The cell is marked with an "X".
 * 3. [CellState.O] - The cell is marked with an "O".
 *
 * The cell state can be represented in two bits. There are nine cells on a
 * Tic-Tac-Toe board, so the board state can be represented in 18 bits. This
 * enables the board state to be stored in a single 32-bit integer value.
 *
 * The board is represented as a 32-bit integer from the lowest bit, 0th index,
 * to the 17th index. The 18th to 31st bits are unused. In this case we consider
 * the top left cell as the origin of the board, (0,0) (row, column) represented
 * by the 0th and 1st bits. Thus, the entire board is represented as follows:
 *
 * * `(0,0)` - 0th and 1st bits
 * * `(0,1)` - 2nd and 3rd bits
 * * `(0,2)` - 4th and 5th bits
 * * `(1,0)` - 6th and 7th bits
 * * `(1,1)` - 8th and 9th bits
 * * `(1,2)` - 10th and 11th bits
 * * `(2,0)` - 12th and 13th bits
 * * `(2,1)` - 14th and 15th bits
 * * `(2,2)` - 16th and 17th bits
 *
 * This is an extremely efficient way to represent the state of the game board.
 *
 * @property board
 *   The 32-bit integer value representing the state of the game board.
 */
class TicTacToeBoard(val board: Int)
{
	/**
	 * The state of the cell at position `(0,0)` on the game board.
	 */
	val cell00State get() = cell00State(board)

	/**
	 * The state of the cell at position `(0,1)` on the game board.
	 */
	val cell01State get() = cell01State(board)

	/**
	 * The state of the cell at position `(0,2)` on the game board.
	 */
	val cell02State get() = cell02State(board)

	/**
	 * The state of the cell at position `(1,0)` on the game board.
	 */
	val cell10State get() = cell10State(board)

	/**
	 * The state of the cell at position `(1,1)` on the game board.
	 */
	val cell11State get() = cell11State(board)

	/**
	 * The state of the cell at position `(1,2)` on the game board.
	 */
	val cell12State get() = cell12State(board)

	/**
	 * The state of the cell at position `(2,0)` on the game board.
	 */
	val cell20State get() = cell20State(board)

	/**
	 * The state of the cell at position `(2,1)` on the game board.
	 */
	val cell21State get() = cell21State(board)

	/**
	 * The state of the cell at position `(2,2)` on the game board.
	 */
	val cell22State get() = cell22State(board)

	/**
	 * The string representation of the game board.
	 */
	val visualBoard: String by lazy {
		val cellStates = cellStates(board)
		val cell00 = cellStates[0].symbol
		val cell01 = cellStates[1].symbol
		val cell02 = cellStates[2].symbol
		val cell10 = cellStates[3].symbol
		val cell11 = cellStates[4].symbol
		val cell12 = cellStates[5].symbol
		val cell20 = cellStates[6].symbol
		val cell21 = cellStates[7].symbol
		val cell22 = cellStates[8].symbol
		"$cell00 $cell01 $cell02\n$cell10 $cell11 $cell12\n$cell20 $cell21 $cell22"
	}

	/**
	 * `true` if the board is in a winning state, `false` otherwise.
	 */
	val isWinner: Boolean get() = checkBoardIsWinningStatic(board)

	/**
	 * The symbol of the winning player if the board is in a winning state.
	 */
	val winner: String get() =
		if (isWinner) {
			checkBoardIsWinningDynamic(board)?.symbol?.toString()?.let { w ->
				"Winner: $w"
			}
				?: "Not a winning board"
		}
		else "Not a winning board"

	companion object
	{
		/**
		 * Create a [TicTacToeBoard] from a visual representation of the game
		 * with each cell state represented by a single character all in order
		 * with the 0th character as cell `(0,0)` and the 8th character as cell
		 * `(2,2)`. The characters are represented as follows:
		 * * `'-'` - [CellState.EMPTY]
		 * * `'X'` - [CellState.X]
		 * * `'O'` - [CellState.O]
		 *
		 * So a board would potentially be:
		 * ```kotlin
		 * "XO-OX--XO"
		 * ```
		 */
		fun board(visual: String): TicTacToeBoard?
		{
			if (visual.length != 9)
			{
				return null
			}
			visual.map { CellState.from(it) }.let { cellStates ->
				if (cellStates.contains(CellState.INVALID))
				{
					return null
				}
				val boardValue = cellStates.fold(0) { acc, cellState ->
					acc.shl(2) or cellState.ordinal
				}
				if (boardIsValid(boardValue))
				{
					return TicTacToeBoard(boardValue)
				}
				return null
			}
		}

		/**
		 * Create a [TicTacToeBoard.board] from a visual representation of the
		 * game with each cell state represented by a single character all in
		 * order with the 0th character as cell `(0,0)` and the 8th character as
		 * cell `(2,2)`. The characters are represented as follows:
		 * * `'-'` - [CellState.EMPTY]
		 * * `'X'` - [CellState.X]
		 * * `'O'` - [CellState.O]
		 *
		 * So a board would potentially be:
		 * ```kotlin
		 * "XO-OX--XO"
		 * ```
		 */
		fun boardValue(visual: String): Int?
		{
			visual.map { CellState.from(it) }.let { cellStates ->
				if (cellStates.contains(CellState.INVALID))
				{
					return null
				}
				val board = cellStates.fold(0) { acc, cellState ->
					acc.shl(2) or cellState.ordinal
				}
				return board
			}
		}

		/**
		 * The state of the cell at position `(0,0)` on the game board.
		 */
		fun cell00State(board: Int): CellState = CellState[board and 0b00000011]

		/**
		 * The state of the cell at position `(0,1)` on the game board.
		 */
		fun cell01State(board: Int): CellState =
			CellState[board.shr(2) and 0b00000011]

		/**
		 * The state of the cell at position `(0,2)` on the game board.
		 */
		fun cell02State(board: Int): CellState =
			CellState[board.shr(4) and 0b00000011]

		/**
		 * The state of the cell at position `(1,0)` on the game board.
		 */
		fun cell10State(board: Int): CellState =
			CellState[board.shr(6) and 0b00000011]

		/**
		 * The state of the cell at position `(1,1)` on the game board.
		 */
		fun cell11State(board: Int): CellState =
			CellState[board.shr(8) and 0b00000011]

		/**
		 * The state of the cell at position `(1,2)` on the game board.
		 */
		fun cell12State(board: Int): CellState =
			CellState[board.shr(10) and 0b00000011]

		/**
		 * The state of the cell at position `(2,0)` on the game board.
		 */
		fun cell20State(board: Int): CellState =
			CellState[board.shr(12) and 0b00000011]

		/**
		 * The state of the cell at position `(2,1)` on the game board.
		 */
		fun cell21State(board: Int): CellState =
			CellState[board.shr(14) and 0b00000011]

		/**
		 * The state of the cell at position `(2,2)` on the game board.
		 */
		fun cell22State(board: Int): CellState =
			CellState[board.shr(16) and 0b00000011]

		/**
		 * Provides a list of lambdas that calculate the cell combinations total
		 * [CellState.rowColumnWeight] for all of the possible winning
		 * combinations on the game board. This is a list of functions that each
		 * takes the game board as input and returns the sum of the
		 * [CellState.rowColumnWeight] values of the row/column/diagonal.
		 *
		 * Iterating through the board and applying each lambda to the board
		 * until one of them returns 3 or -3 to declare a win state. If there
		 * are multiple winning combinations, the game is in an invalid state.
		 */
		val winningCombinations: List<((Int)->Int)> =
			listOf(
				{ board ->
					cell00State(board).rowColumnWeight +
						cell01State(board).rowColumnWeight +
						cell02State(board).rowColumnWeight
				},
				{ board ->
					cell10State(board).rowColumnWeight +
						cell11State(board).rowColumnWeight +
						cell12State(board).rowColumnWeight
				},
				{ board ->
					cell20State(board).rowColumnWeight +
						cell21State(board).rowColumnWeight +
						cell22State(board).rowColumnWeight
				},
				{ board ->
					cell00State(board).rowColumnWeight +
						cell10State(board).rowColumnWeight +
						cell20State(board).rowColumnWeight
				},
				{ board ->
					cell01State(board).rowColumnWeight +
						cell11State(board).rowColumnWeight +
						cell21State(board).rowColumnWeight
				},
				{ board ->
					cell02State(board).rowColumnWeight +
						cell12State(board).rowColumnWeight +
						cell22State(board).rowColumnWeight
				},
				{ board ->
					cell00State(board).rowColumnWeight +
						cell11State(board).rowColumnWeight +
						cell22State(board).rowColumnWeight
				},
				{ board ->
					cell02State(board).rowColumnWeight +
						cell11State(board).rowColumnWeight +
						cell20State(board).rowColumnWeight
				}
			)

		/**
		 * Retrieves the states of all cells on the game board.
		 *
		 * @param board
		 *   The 32-bit integer value representing the state of the game board.
		 * @return
		 *   A list of [CellState] values representing the state of each cell on
		 *   the game board. The order of the list of (row,column) by index is
		 *   as follows:
		 *   0. `(0,0)`
		 *   1. `(0,1)`
		 *   2. `(0,2)`
		 *   3. `(1,0)`
		 *   4. `(1,1)`
		 *   5. `(1,2)`
		 *   6. `(2,0)`
		 *   7. `(2,1)`
		 *   8. `(2,2)`
		 */
		fun cellStates(board: Int): List<CellState> =
			listOf(
				cell00State(board), cell01State(board), cell02State(board),
				cell10State(board), cell11State(board), cell12State(board),
				cell20State(board), cell21State(board), cell22State(board)
			)

		/**
		 * Calculates the weight of the board based on the sum of the
		 * [CellState.boardWeight] values of each cell on the board.
		 *
		 * @param board
		 *   The 32-bit integer value representing the state of the game board
		 *   to calculate the weight of.
		 * @return
		 *   The weight of the board.
		 */
		fun boardWeight(board: Int): Int =
			cellStates(board).sumOf { it.boardWeight }

		/**
		 * Checks if the provided game board is in a valid state. `true` if it
		 * is valid, `false` otherwise.
		 */
		fun boardIsValid(board: Int): Boolean =
			abs(cellStates(board).sumOf { it.rowColumnWeight }) <= 1

		/**
		 * Checks if the provided game board is in a winning state.
		 *
		 * @param board
		 *   The 32-bit integer value representing the state of the game board
		 *   to check for a win state.
		 * @return
		 *   `true` if the game board is in a winning state, `false` otherwise.
		 */
		fun checkBoardIsWinningDynamic(board: Int): CellState?
		{
			val cellStates = cellStates(board)
			val boardWeight = cellStates.sumOf { it.boardWeight }
			if (boardWeight < 5)
			{
				// Not enough cells marked to reach a win state
				return null
			}
			val rowColumnTotalWeight =
				abs(cellStates.sumOf { it.rowColumnWeight })
			if (rowColumnTotalWeight > 1)
			{
				// The board is in an invalid state as it has more than one
				// difference between the number of "X" and "O" cells or there
				// is at least one CellState.INVALID on the board.
				return null
			}
			val winners = winningCombinations
				.map { it(board) }
				.filter { it == 3 || it == -3 }

			return if (winners.size == 1)
			{
				if (winners[0] == 3)
				{
					CellState.X
				}
				else
				{
					CellState.O
				}
			}
			else
			{
				null
			}
		}

		/**
		 * Checks if the provided game board is in a winning state. This is a
		 * static check against [WinningBoards.winningBoards] which is a
		 * pre-computed list of all possible winning boards originally created
		 * using [preComputeWinningBoards].
		 *
		 * @param board
		 *   The 32-bit integer value representing the state of the game board
		 *   to check for a win state.
		 * @return
		 *   `true` if the game board is in a winning state, `false` otherwise.
		 */
		fun checkBoardIsWinningStatic(board: Int): Boolean =
			WinningBoards.winningBoards.contains(board)
	}
}

/**
 * Pre-computes all possible winning boards for a game of Tic-Tac-Toe. We only
 * need to check boards where there are at least five cells marked as this is
 * the minimum number of cells required to reach a win state. So the minimum
 * sum of all [CellState.boardWeight] values on the board is 5. The lowest
 * possible value for a winning board is 362 (`0b101101010`):
 * ```
 * OOO
 * XX-
 * ---
 * ```
 * The highest possible value for a winning board is 174,425
 * (`0b101010100101011001`):
 * ```
 * XOX
 * XXO
 * OOO
 * ```
 * So we don't need to check  all the boards up to 2^18-1 (262,143) as there
 * are no winning boards above 174,425. We can stop checking boards at 174,425.
 */
fun preComputeWinningBoards(): List<Int>
{
	val winningBoards = mutableListOf<Int>()
	for (board in 362 until 174_425)
	{ // Iterate over the range [362, 2^18-1]
		try
		{
			val boardCheck = TicTacToeBoard.checkBoardIsWinningDynamic(board)
			if (boardCheck != null)
			{
				winningBoards.add(board)
			}
		}
		catch (e: Exception)
		{
			println("Error checking board: $board")
			e.printStackTrace()
		}
	}
	return winningBoards
}

/**
 * Formats a list of integers into a string with a maximum line length of 60
 * characters. The list is formatted as a comma-separated list with a newline
 * character separating each line. This list is used to code generate all of
 * the winning boards for a game of Tic-Tac-Toe in [WinningBoards].
 */
fun formatList(inputList: List<Int>): String
{
	return buildString {
		var lineLength = 0
		for ((index, value) in inputList.withIndex())
		{
			val stringValue = value.toString()
			val potentialLength =
				lineLength + stringValue.length +
					if (index != 0) 1 else 0 // +1 for comma

			if (potentialLength > 60)
			{
				append(",\n\t\t\t")
				lineLength = 0
			}
			else if (index != 0)
			{
				append(",")
				lineLength++
			}

			append(stringValue)
			lineLength += stringValue.length
		}
		if (lineLength > 0)
		{
			append(",")
		}
	}
}

/**
 * Generated code which is an object containing a [TreeSet] of all possible
 * winning boards for a game of Tic-Tac-Toe. The winning boards are calculated
 * using [preComputeWinningBoards] and code generated using [template].
 */
object WinningBoards
{
	/**
	 * A [TreeSet] of all possible winning [TicTacToeBoard.board] for a game of
	 * Tic-Tac-Toe.
	 */
	val winningBoards: TreeSet<Int> = TreeSet(
		listOf(
			362,661,1130,1322,1354,1378,1384,2197,2581,2693,2705,2708,
			4201,4202,4378,4394,4458,4498,4504,4506,4681,4705,4713,5162,
			5226,5418,5530,5546,5737,5738,6217,6241,6249,6418,6424,6426,
			6490,6505,6506,6544,6546,6550,6552,6553,6554,6721,6729,6745,
			6753,6757,6761,6785,6788,6789,6800,6801,6804,6806,6809,6821,
			8341,8342,8582,8594,8598,8725,8741,8801,8804,8805,8853,9350,
			9362,9366,9538,9544,9546,9562,9568,9570,9574,9576,9577,9602,
			9606,9618,9622,9626,9638,9761,9764,9765,9824,9825,9828,9829,
			9830,9833,9877,9878,9893,10261,10389,10645,10646,10773,10837,
			10853,16490,16678,16682,16746,16774,16804,16806,17450,17514,
			17706,17830,17834,18026,18694,18724,18726,18790,18794,18820,
			18822,18838,18852,18853,18854,19073,19076,19077,19088,19089,
			19092,19094,19097,19109,20522,20586,20778,20842,20890,20902,
			20906,21097,21098,21546,21610,21674,21802,21930,22058,22122,
			22633,22634,22810,22822,22826,22890,22918,22930,22936,22938,
			22948,22950,23113,23137,23145,23168,23169,23172,23173,23174,
			23177,23184,23185,23186,23188,23190,23192,23193,23201,23204,
			23205,24710,24722,24726,24838,24868,24870,24934,24938,24962,
			24964,24978,24986,24996,24997,25121,25124,25125,25184,25185,
			25188,25189,25190,25193,25237,25238,25253,25706,25730,25734,
			25746,25750,25754,25766,25894,25898,25930,25954,25960,25986,
			25994,26002,26010,26018,26020,26144,26145,26148,26149,26150,
			26153,26208,26209,26210,26212,26214,26216,26217,26246,26258,
			26262,26273,26276,26277,26773,26774,26884,26886,26902,26916,
			26917,26918,26950,26980,26982,27012,27013,27026,27028,27044,
			27045,27157,27173,27233,27236,27237,27269,27281,27284,32917,
			33301,33305,33353,33368,33369,33429,34114,34120,34122,34138,
			34144,34146,34150,34152,34153,34313,34328,34329,34376,34377,
			34392,34393,34394,34409,34453,34457,34837,34965,35221,35349,
			35413,35417,36937,36961,36969,37138,37144,37146,37210,37225,
			37226,37264,37266,37270,37272,37273,37274,37385,37400,37401,
			37441,37448,37464,37466,37473,37477,37525,37529,37993,37994,
			38170,38186,38218,38242,38248,38290,38296,38298,38408,38409,
			38424,38425,38426,38441,38472,38474,38488,38490,38497,38504,
			38537,38552,38553,38977,38985,39001,39009,39013,39017,39061,
			39184,39186,39190,39192,39193,39194,39241,39250,39256,39258,
			39265,39273,39312,39313,39314,39316,39318,39320,39321,39445,
			39449,39489,39493,39505,39512,39521,39525,39557,39569,39572,
			40981,41109,41365,41366,41493,41557,41561,41573,41621,42133,
			42134,42304,42306,42310,42312,42313,42314,42322,42328,42330,
			42336,42337,42338,42340,42342,42344,42345,42374,42386,42390,
			42517,42521,42533,42569,42584,42585,42593,42596,42597,42645,
			43029,43093,43157,43285,43413,43541,43605,65642,65833,65834,
			65898,65929,65953,65961,66586,66602,66666,66706,66712,66714,
			66858,66970,66985,66986,67090,67096,67098,67162,67178,67216,
			67218,67222,67224,67225,67226,67849,67873,67881,67945,67946,
			67969,67977,67993,68001,68005,68009,68225,68228,68229,68240,
			68241,68244,68246,68249,68261,69674,69738,69930,69994,70042,
			70057,70058,70249,70250,70698,70762,70810,70826,70954,71082,
			71194,71210,71274,71314,71320,71322,71785,71786,71962,71977,
			71978,72042,72073,72082,72088,72090,72097,72105,72265,72289,
			72297,72320,72321,72324,72325,72326,72329,72336,72337,72338,
			72340,72342,72344,72345,72353,72356,72357,73862,73874,73878,
			73993,74017,74025,74089,74090,74113,74114,74118,74121,74130,
			74134,74137,74138,74145,74149,74150,74153,74273,74276,74277,
			74336,74337,74340,74341,74342,74345,74389,74390,74405,74770,
			74776,74778,74842,74858,74882,74886,74896,74904,74905,74918,
			75034,75049,75050,75082,75106,75112,75138,75142,75145,75146,
			75160,75169,75170,75174,75177,75280,75282,75286,75288,75289,
			75290,75296,75297,75300,75301,75302,75305,75346,75352,75354,
			75360,75361,75362,75364,75366,75368,75369,75398,75408,75409,
			75412,75416,75417,75425,75428,75429,75925,75926,76033,76041,
			76057,76065,76069,76073,76105,76129,76137,76161,76165,76166,
			76169,76177,76178,76182,76185,76193,76197,76309,76325,76385,
			76388,76389,76421,76433,76436,81962,82026,82218,82282,82342,
			82345,82346,82538,82986,83050,83098,83114,83242,83370,83482,
			83498,83562,83602,83608,83610,84074,84262,84265,84266,84330,
			84358,84361,84385,84388,84390,84393,84608,84609,84612,84613,
			84614,84617,84624,84625,84626,84628,84630,84632,84633,84641,
			84644,84645,86026,86050,86056,86146,86152,86154,86170,86176,
			86178,86182,86184,86185,86410,86434,86440,86530,86536,86538,
			86554,86560,86562,86566,86568,86569,86602,86626,86632,86656,
			86658,86662,86664,86665,86666,86674,86680,86682,86688,86689,
			86690,86692,86694,86696,86697,87178,87202,87208,87562,87586,
			87592,87682,87688,87690,87712,87714,87718,87720,87721,88066,
			88072,88074,88090,88096,88098,88102,88104,88105,88138,88162,
			88168,88192,88194,88198,88200,88201,88202,88210,88216,88218,
			88224,88225,88226,88228,88230,88232,88233,88330,88354,88360,
			88450,88456,88458,88480,88482,88488,88576,88578,88582,88584,
			88585,88586,88594,88600,88602,88608,88609,88610,88612,88614,
			88616,88617,88642,88648,88650,88666,88672,88674,88678,88680,
			90218,90242,90246,90258,90262,90266,90278,90406,90409,90410,
			90474,90498,90505,90506,90514,90522,90529,90530,90532,90537,
			90656,90657,90660,90661,90662,90665,90720,90721,90722,90724,
			90726,90728,90729,90758,90770,90774,90785,90788,90789,91162,
			91178,91242,91266,91270,91274,91288,91298,91302,91434,91522,
			91530,91554,91561,91666,91672,91674,91680,91681,91682,91684,
			91686,91688,91689,91738,91744,91746,91750,91752,91753,91778,
			91782,91792,91800,91801,91808,91809,91812,91813,91817,92294,
			92306,92310,92422,92425,92449,92452,92454,92457,92518,92521,
			92522,92545,92546,92548,92553,92562,92569,92570,92577,92580,
			92585,92705,92708,92709,92768,92769,92772,92773,92774,92777,
			92801,92804,92805,92816,92817,92820,92825,98569,98593,98601,
			98665,98666,98689,98697,98713,98721,98725,98729,98825,98840,
			98841,98888,98889,98904,98905,98906,98921,98965,98969,99346,
			99352,99354,99418,99434,99472,99474,99478,99480,99481,99482,
			99610,99625,99626,99658,99682,99688,99721,99730,99736,99738,
			99745,99753,99848,99849,99856,99858,99862,99881,99912,99913,
			99914,99922,99944,99945,99977,99984,99985,99986,99988,99990,
			100501,100609,100617,100633,100641,100645,100649,100681,
			100705,100713,100737,100741,100745,100753,100761,100769,
			100773,100885,100889,100937,100952,100953,100997,101009,
			101012,102505,102506,102682,102697,102698,102762,102793,
			102802,102808,102810,102817,102825,102920,102921,102936,
			102937,102938,102953,102984,102986,103000,103002,103009,
			103016,103049,103064,103065,103450,103466,103530,103570,
			103576,103578,103722,103849,103850,103944,103945,103946,
			103954,103976,103977,104008,104010,104040,104072,104073,
			104080,104082,104086,104105,104521,104545,104553,104713,
			104722,104728,104730,104737,104745,104794,104810,104833,
			104841,104848,104850,104854,104856,104858,104865,104869,
			104873,104969,104984,104985,105025,105032,105048,105050,
			105057,105061,105089,105092,105093,105104,105105,105108,
			105110,105125,106645,106646,106753,106761,106777,106785,
			106789,106793,106825,106849,106857,106881,106885,106886,
			106889,106897,106898,106902,106905,106913,106917,107029,
			107033,107045,107081,107096,107097,107105,107108,107109,
			107157,107536,107538,107542,107544,107545,107546,107602,
			107608,107610,107654,107664,107665,107668,107672,107673,
			107785,107794,107800,107802,107809,107817,107842,107848,
			107850,107872,107874,107878,107880,107905,107906,107910,
			107913,107920,107928,107937,107941,107942,107945,108041,
			108048,108049,108050,108052,108054,108065,108068,108069,
			108104,108105,108112,108114,108118,108128,108129,108132,
			108133,108134,108176,108177,108180,108197,108565,108693,
			108801,108805,108809,108817,108825,108833,108837,108865,
			108873,108889,108897,108901,108905,108929,108933,108945,
			108950,108953,108965,109077,109141,109145,109157,131221,
			131605,131606,131654,131666,131670,131733,132418,132424,
			132426,132442,132448,132450,132454,132456,132457,132614,
			132626,132630,132674,132678,132690,132694,132698,132710,
			132757,132758,133141,133157,133217,133220,133221,133269,
			133409,133412,133413,133472,133473,133476,133477,133478,
			133481,133525,133541,133653,133717,133718,133733,135241,
			135265,135273,135442,135448,135450,135514,135529,135530,
			135568,135570,135574,135576,135577,135578,135686,135698,
			135702,135745,135746,135750,135753,135762,135766,135769,
			135770,135777,135781,135782,135785,135829,135830,136297,
			136298,136474,136490,136522,136546,136552,136594,136600,
			136602,136706,136710,136722,136726,136730,136742,136770,
			136774,136777,136778,136786,136794,136801,136802,136806,
			136809,136838,136850,136854,137249,137252,137253,137281,
			137289,137305,137312,137316,137318,137365,137381,137488,
			137490,137494,137496,137497,137498,137504,137505,137508,
			137509,137510,137513,137545,137554,137560,137562,137568,
			137570,137572,137574,137576,137616,137617,137618,137620,
			137622,137624,137625,137633,137636,137637,137749,137750,
			137765,137793,137797,137798,137801,137809,137810,137814,
			137817,137828,137861,137873,137876,139285,139413,139669,
			139670,139797,139861,139862,139877,139925,140437,140438,
			140608,140610,140614,140616,140617,140618,140626,140632,
			140634,140640,140641,140642,140644,140646,140648,140649,
			140678,140690,140694,140821,140822,140837,140870,140882,
			140886,140897,140900,140901,140949,141333,141397,141413,
			141461,141589,141605,141665,141668,141669,141717,141845,
			141909,147718,147748,147750,147814,147818,147844,147846,
			147862,147876,147877,147878,147974,147986,147990,148034,
			148038,148050,148054,148058,148070,148117,148118,148586,
			148774,148778,148810,148834,148840,148870,148900,148902,
			148994,148998,149010,149014,149018,149030,149058,149062,
			149066,149074,149082,149090,149094,149126,149138,149142,
			149537,149540,149541,149600,149601,149604,149605,149606,
			149609,149653,149669,149764,149766,149782,149792,149793,
			149801,149830,149856,149857,149858,149864,149865,149892,
			149893,149894,149908,149910,149921,150037,150038,150053,
			150086,150098,150102,150113,150116,150117,150149,150161,
			150164,151657,151658,151834,151846,151850,151914,151942,
			151954,151960,151962,151972,151974,152066,152070,152082,
			152086,152090,152102,152130,152134,152137,152138,152146,
			152154,152161,152162,152166,152169,152198,152210,152214,
			152618,152682,152874,152986,152998,153002,153090,153094,
			153098,153106,153114,153122,153126,153154,153162,153178,
			153186,153190,153193,153218,153222,153234,153238,153242,
			153254,153632,153633,153636,153637,153638,153641,153673,
			153696,153698,153700,153702,153704,153761,153764,153765,
			153862,153874,153880,153882,153888,153889,153890,153896,
			153897,153946,153952,153954,153960,153988,153990,154000,
			154002,154008,154009,154010,154016,154017,154025,154118,
			154130,154134,154145,154148,154149,154177,154178,154182,
			154185,154194,154198,154201,154202,154208,154212,154241,
			154244,154245,154256,154257,154260,154265,155797,155798,
			155908,155910,155926,155940,155941,155942,155974,156004,
			156006,156036,156037,156050,156052,156068,156069,156181,
			156182,156197,156230,156242,156246,156257,156260,156261,
			156309,156806,156818,156822,156934,156964,156966,156994,
			157000,157002,157018,157024,157026,157032,157033,157058,
			157060,157074,157082,157092,157093,157190,157202,157206,
			157217,157220,157221,157250,157254,157266,157270,157274,
			157280,157281,157284,157285,157289,157333,157349,157717,
			157733,157793,157796,157797,157845,157956,157957,157958,
			157972,157974,157985,158020,158022,158038,158048,158049,
			158057,158084,158085,158100,158229,158293,158294,163861,
			163989,164245,164373,164437,164438,164441,164501,165013,
			165184,165186,165190,165192,165193,165194,165202,165208,
			165210,165216,165217,165218,165220,165222,165224,165225,
			165397,165398,165401,165446,165449,165458,165462,165464,
			165465,165525,165909,165973,165989,166037,166165,166181,
			166241,166244,166245,166293,166421,166485,168001,168009,
			168025,168033,168037,168041,168085,168208,168210,168214,
			168216,168217,168218,168265,168274,168280,168282,168289,
			168297,168336,168337,168338,168340,168342,168344,168345,
			168469,168470,168473,168513,168517,168518,168529,168530,
			168534,168536,168545,168549,168597,169033,169057,169065,
			169234,169240,169242,169282,169288,169290,169312,169314,
			169318,169320,169360,169362,169366,169368,169369,169370,
			169478,169481,169490,169494,169496,169497,169537,169538,
			169542,169544,169554,169558,169560,169569,169573,169574,
			169621,169622,169625,170005,170021,170049,170053,170057,
			170065,170073,170084,170133,170256,170257,170258,170260,
			170262,170264,170265,170273,170276,170277,170305,170313,
			170320,170322,170326,170328,170330,170336,170340,170342,
			170384,170385,170388,170390,170393,170405,170517,170561,
			170565,170577,170582,172037,172049,172052,172097,172100,
			172101,172112,172113,172116,172118,172121,172133,172289,
			172292,172293,172304,172305,172308,172310,172313,172325,
			172352,172353,172356,172357,172358,172361,172368,172369,
			172370,172372,172374,172376,172377,172385,172388,172389,
			172421,172433,172436,172613,172625,172628,173057,173060,
			173061,173072,173073,173076,173078,173081,173093,173120,
			173121,173124,173125,173126,173129,173136,173137,173138,
			173140,173142,173144,173145,173153,173156,173157,173189,
			173201,173204,173312,173313,173316,173317,173318,173321,
			173328,173329,173330,173332,173334,173336,173337,173345,
			173348,173349,173441,173444,173445,173456,173457,173460,
			173465,173477,173573,173585,173588,173633,173636,173637,
			173648,173649,173652,174149,174161,174164,174341,174353,
			174356,174401,174404,174405,174416,174417,174420,174422,
		)
	)
}

val template = {
"""
/**
 * Generated code which is an object containing a [TreeSet] of all possible
 * winning boards for a game of Tic-Tac-Toe. The winning boards are calculated
 * using [preComputeWinningBoards].
 */
object WinningBoards
{
	/**
	 * A [TreeSet] of all possible winning [TicTacToeBoard.board] for a game of
	 * Tic-Tac-Toe.
	 */
	val winningBoards: TreeSet<Int> = TreeSet(
		listOf(
			${formatList(preComputeWinningBoards())}
		)
	)
}
"""
}
