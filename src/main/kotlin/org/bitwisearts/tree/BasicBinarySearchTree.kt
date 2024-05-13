package org.bitwisearts.tree

import java.util.Stack
import kotlin.math.max

/**
 * A basic binary tree node that can be used to store an Int a
 * [BasicBinarySearchTree].
 *
 * This basic binary tree is unbalanced and is only added to ensure that the
 * exact instructions for this assessment are followed. Under normal
 * circumstances, a balanced tree would be used.
 *
 * Some of the characteristics of an this tree is that it can become unbalanced
 * with the addition of new nodes or the deletion of existing nodes. At a worse
 * case scenario, depending on the insertion implementation, a tree can become a
 * linked list. A linked list has a time complexity of O(n) for search, insert,
 * and delete operations.
 *
 * This implementation is limited to basic operations, not necessarily cleverly
 * implemented, and is not intended to demonstrate the best practices for binary
 * search tree implementations.
 *
 * @property key
 *   The value of the node.
 * @property left
 *   The left child [NodeBST] of this node.
 * @property right
 *   The right child [NodeBST] of this node.
 */
data class NodeBST(
	var key: Int,
	var left: NodeBST? = null,
	var right: NodeBST? = null
) {
	override fun toString(): String = key.toString()

	/**
	 * Inserts a new node into the tree.
	 *
	 * @param target
	 *   The target Int to insert.
	 * @return
	 *   `true` if the target was inserted, `false` if the target is a
	 *   duplicate resulting in no insertion.
	 */
	fun insert(
		target: Int
	): Boolean =
		when {
			target == key -> false
			target < key ->
				left?.insert(target) ?: run {
					left = NodeBST(target)
					true
				}
			else ->
			{
				right?.insert(target) ?: run {
					right = NodeBST(target)
					true
				}
			}
		}

	/**
	 * Searches for a node in the tree with the `target` [key], answering the
	 * node that matches the `target` or `null` if the target is not found.
	 */
	fun search(target: Int): NodeBST?=
		when
		{
			key == target -> this
			key < target -> right?.search(target)
			else -> left?.search(target)
		}

	/**
	 * Answers the height of the tree treating this [NodeBST] as the
	 * root. The height of a tree is the number of edges on the longest path
	 * between the root node and a leaf node. The height of a tree with a single
	 * node is 0.
	 */
	val height: Int get() {
		var maxDepth = 0
		// Calculate the height using depth-first traversal of the tree.
		Stack<Pair<NodeBST, Int>>().apply {
			push(Pair(this@NodeBST, 0))
			while (isNotEmpty())
			{
				val (node, depth) = pop()
				maxDepth = max(maxDepth, depth)
				node.left?.let { push(Pair(it, depth + 1)) }
				node.right?.let { push(Pair(it, depth + 1)) }
			}
		}
		return maxDepth
	}
}

/**
 * A basic binary tree that is unbalanced that contains a [NodeBST]
 * root node. The [NodeBST] itself is all that is needed to
 * represent the tree, however, this class is added to ensure that the exact
 * root node is preserved.
 *
 * @property root
 *   The root [NodeBST] of the tree or `null` if it is an empty
 *   tree.
 */
data class BasicBinarySearchTree(
	var root: NodeBST? = null
) {
	/**
	 * The number of nodes in the tree. This is used for testing purposes.
	 */
	var nodeCount = if (root == null) 0 else 1

	/**
	 * Answers the height of the tree. The height of a tree is the number of
	 * edges on the longest path between the root node and a leaf node. The
	 * height of a tree with a single node is 0.
	 */
	val height: Int get() = root?.height ?: -1

	/**
	 * Inserts a new node into the tree.
	 *
	 * @param value
	 *   The [NodeBST] to insert.
	 */
	fun insert(value: Int) {
		if (root?.insert(value) ?: run {
			root = NodeBST(value)
			true
		}) {
			nodeCount++
		}
	}

	/**
	 * Searches for a [NodeBST] in the tree with the `target`
	 * [NodeBST.key] navigating the tree in parallel. Answers the
	 * first node that matches the `target` or `null` if the target is not
	 * found.
	 */
	fun search(target: Int): NodeBST? =
		root?.search(target)

	/**
	 * Deletes a node from the tree with the `target` key if it exists.
	 */
	fun delete(target: Int) {
		root?.let {
			deleteRecursive(it, target) { replacement ->
				root = replacement
			}
		}
	}

	/**
	 * The recursive function that deletes a node from the tree with the `target`
	 * as its [NodeBST.key].
	 *
	 * @param current
	 *   The current [NodeBST] being evaluated.
	 * @param target
	 *   The [NodeBST.key] to delete from the tree if it exists.
	 * @param updater
	 *   The function that accepts a [NodeBST] that will replace the
	 *   one of the current node's children if the target for deletion is found
	 *   to be one of the children.
	 */
	private tailrec fun deleteRecursive(
		current: NodeBST,
		target: Int,
		updater: (NodeBST?) -> Unit
	) {
		when {
			// Case 1: the target is found so we need to delete the current node
			target == current.key ->
				when
				{
					// Case 1.1: no children so we just remove the leaf
					current.left == null && current.right == null ->
						updater(null)

					// Case 1.2: only 1 child so we elevate the child only child
					current.right == null -> updater(current.left)

					current.left == null -> updater(current.right)

					// Case 1.3: 2 children so we must decide which child to
					// elevate
					else ->
					{
						if (current.right!!.height > current.left!!.height)
						{
							updater(current.right)
						}
						else
						{
							updater(current.left)
						}
					}
				}

			// Case 4: the current node is a leaf so the target is not in the
			// tree
			current.left == null && current.right == null -> {}

			// Case 5: the only place left to search is the right child's
			// subtree which
			current.right != null && target > current.key ->
				deleteRecursive(current.right!!, target) { current.right = it }

			// Case 6: the only place left to search is the left child's
			// subtree
			current.left != null && target < current.key ->
				deleteRecursive(current.left!!, target) { current.left = it }

			// Case 7: There is no subtree left that could contain the target
			else -> {}
		}
	}
}
