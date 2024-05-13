package org.bitwisearts.tree

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.LinkedList
import java.util.Queue
import kotlin.coroutines.cancellation.CancellationException

/**
 * Represents a [Node] in the [BinaryTree]. It matches the most basic structure
 * of a binary tree, having at most two children, left and right.
 *
 * This basic binary tree is unordered but unbalanced, growing by filling each
 * level before it creates a new level to the tree from an insert operation. It
 * is only added to ensure that the exact instructions for this assessment are
 * followed. Under normal circumstances, a binary search tree would be used.
 *
 * This implementation is limited to basic, naive operations and is not intended
 * to demonstrate the best practices for binary tree implementations.
 *
 * **NOTE** The assignment instructions specifically state that the tree is to
 * store integers, so the value is an integer and no generics are used to store
 * any additional type of value at each node.
 *
 * @property value
 *   The value stored in the node.
 * @property left
 *   The left child of the node.
 * @property right
 *   The right child of the node.
 */
class Node(
	var value: Int,
	var left: Node? = null,
	var right: Node? = null,
) {
	/**
	 * Searches for a node in the tree with the `target` [value] navigating the
	 * tree in parallel. Returns the first node that matches the `target` or
	 * null if the target is not found.
	 *
	 * This is done as the tree is unordered and unbalanced, so the search is
	 * done in parallel to find the target value more quickly.
	 */
	suspend fun parallelSearch(
		target: Int
	): Node? = coroutineScope {
		println("2 Searching for $target in $value")
		if (value == target)
		{
			return@coroutineScope this@Node
		}

		val leftJob = async { left?.parallelSearch(target) }
		val rightJob = async { right?.parallelSearch(target) }

		val leftResult = try
		{
			leftJob.await()?.also {
				rightJob.cancel()
				println("Found $target in left $value")
			}
		}
		catch (e: CancellationException)
		{
			null
		}
		val rightResult = try
		{
			rightJob.await()?.also {
				leftJob.cancel()
				println("Found $target in right $value")
			}
		}
		catch (e: CancellationException)
		{
			null
		}

		leftResult ?: rightResult
	}

	/**
	 * The height of the tree from this node. The height of a tree is the number
	 * of edges on the longest path between the root node and a leaf node.
	 *
	 * Because the tree is built level by level, the height is determined by
	 * counting the number of levels from the root to the deepest leaf node
	 * along the left side of the tree.
	 */
	val height: Int
		get()
		{
			var depth = 0
			var current: Node? = this
			while (current != null)
			{
				if(current.left != null) depth++
				current = current.left
			}
			return depth
		}
}

/**
 * The [BinaryTree] class represents a basic, unordered, unbalanced binary tree.
 *
 * @property root
 *   The root node of the binary tree.
 */
class BinaryTree(
	var root: Node? = null
) {
	/**
	 * The height of the tree.
	 */
	val height: Int get() = root?.height ?: -1
	/**
	 * Inserts a new node with the given value into the binary tree.
	 * Nodes are inserted level by level from left to right.
	 *
	 * @param value The value to be inserted into the binary tree.
	 */
	fun insert(value: Int)
	{
		val newNode = Node(value)
		if (root == null)
		{
			root = newNode
			return
		}

		val queue: Queue<Node> = LinkedList()
		queue.add(root)

		while (queue.isNotEmpty())
		{
			val node = queue.poll()

			if (node.left == null)
			{
				node.left = newNode
				break
			}
			else
			{
				queue.add(node.left)
			}

			if (node.right == null)
			{
				node.right = newNode
				break
			}
			else
			{
				queue.add(node.right)
			}
		}
	}

	/**
	 * Searches for a node in the tree with the `target` [Node.value] navigating
	 * the tree in parallel. Returns the first node that matches the `target` or
	 * null if the target is not found.
	 *
	 * @param target
	 *   The value to search for in the binary tree.
	 * @return
	 *   The [Node] with the target value or null if the target is not found.
	 */
	suspend fun parallelSearch(target: Int): Node? =
		root?.parallelSearch(target)
}
