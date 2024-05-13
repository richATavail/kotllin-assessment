package org.bitwisearts.tree

import org.bitwisearts.tree.Color.BLACK
import org.bitwisearts.tree.Color.RED
import java.util.Stack
import java.util.TreeMap
import kotlin.math.max

/**
 * The [Color] of a [NodeRB] in a Red-Black Tree.
 */
enum class Color
{
	RED,
	BLACK
}

/**
 * A data class representing a node in a [RedBlackTree].
 *
 * @property key
 *   The key of the node, represented as an integer.
 * @property color
 *   The [Color] of the node.
 * @property left
 *   The left child [NodeRB] representing the left subtree or null if the node
 *   has no left child.
 * @property right
 *   The right child [NodeRB] representing the right subtree or null if the node
 *   has no right child.
 * @property parent
 *   The parent [NodeRB] of this [NodeRB] or null if the node is the root of the
 *   tree.
 */
data class NodeRB(
	val key: Int,
	var color: Color?,
	var left: NodeRB? = null,
	var right: NodeRB? = null,
	var parent: NodeRB? = null
)
{
	override fun toString(): String =
		"$key : $color (${left?.color} - ${right?.color})"

	/**
	 * Searches for a node in the tree with the `target` [key], answering the
	 * node that matches the `target` or `null` if the target is not found.
	 */
	fun search(target: Int): NodeRB? =
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
	val height: Int
		get()
		{
			var maxDepth = 0
			// Calculate the height using depth-first traversal of the tree.
			Stack<Pair<NodeRB, Int>>().apply {
				push(Pair(this@NodeRB, 0))
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
 * A Red-Black Tree is a type of self-balancing Binary Search Tree where each
 * node has an extra attribute: the [Color], which can be either [Color.RED] or
 * [Color.BLACK]. This type of tree only guarantees that it will be
 * approximately balanced during insertions and deletions to guarantee O(log n)
 * time. Some characteristics are:
 *
 * 1. Every node is either red or black.
 * 2. The root is black.
 * 3. All leaves (NIL nodes) are black.
 * 4. If a node is red, then both its children are black.
 * 5. Every path from a node to its descendant NIL nodes goes through the same
 * number of black nodes.
 *
 * **NOTE** Due to the level of complexity in maintaining the Red-Black Tree,
 * specific methods for deletion are not implemented in this class as doing so
 * would require a significant amount of additional code and time. This class is
 * intended to be used as a reference for understanding the Red-Black Tree
 * structure and properties, not as a full implementation.
 *
 * It should also be noted that the Java Standard Library provides [TreeMap]
 * which is a Red-Black Tree implementation that can be used, so it is not
 * necessary to implement a Red-Black Tree from scratch in JVM-based languages
 * that have access to the Java Standard Library.
 *
 * [See Wikipedia](https://en.wikipedia.org/wiki/Red%E2%80%93black_tree)
 *
 * @property root
 *   The root [NodeRB] of the tree or null if the tree is empty.
 */
data class RedBlackTree(
	var root: NodeRB? = null
) {
	override fun toString(): String =
		root?.let { "${it.key} : ${it.color}" } ?: "EMPTY"

	/**
	 * Answers the height of the tree. The height of a tree is the number of
	 * edges on the longest path between the root node and a leaf node. The
	 * height of a tree with a single node is 0.
	 */
	val height: Int get() = root?.height ?: -1

	/**
	 * Searches for a node in the tree with the `target` [NodeRB.key], answering
	 * the node that matches the `target` or `null` if the target is not found.
	 */
	fun search(target: Int): NodeRB? = root?.search(target)

	/**
	 * Rotates the given node to the left.
	 *
	 * @param target
	 *   The targeted node to rotate left.
	 */
	private fun leftRotate(target: NodeRB?)
	{
		// 1. Right child of target becomes the new parent of target, newParent,
		// making target the left child of the newParent.
		// 2. The left child of newParent becomes the right child of target.
		val newParent = target?.right
			?: return // Can't rotate if there is no right child.

		val oldParent = target.parent
		val newRight = newParent.left
		target.right = newRight?.apply {
			parent = target
		}
		newParent.parent = oldParent
		target.parent = newParent
		newParent.left = target
		when
		{
			oldParent == null -> root = newParent
			target == oldParent.left -> oldParent.left = newParent
			else -> oldParent.right = newParent
		}
	}

	/**
	 * Rotates the given node to the right.
	 *
	 * @param target
	 *   The targeted node to rotate right.
	 */
	private fun rightRotate(target: NodeRB?)
	{
		// 1. Left child of target becomes the new parent of target, newParent,
		// making target the right child of the newParent.
		// 2. The right child of newParent becomes the left child of target.
		val newParent = target?.left
			?: return // Can't rotate if there is no left child.

		val oldParent = target.parent
		val newLeft = newParent.right
		target.left = newLeft?.apply {
			parent = target
		}
		newParent.parent = oldParent
		target.parent = newParent
		newParent.right = target
		when
		{
			oldParent == null -> root = newParent
			target == oldParent.right -> oldParent.right = newParent
			else -> oldParent.left = newParent
		}
	}

	/**
	 * Inserts a node into the tree.
	 *
	 * @param target
	 *   The target key value to insert into the tree as a new node.
	 */
	fun insert(target: Int)
	{
		if (root == null)
		{
			root = NodeRB(target, BLACK)
			return
		}
		val targetNode = NodeRB(target, RED)
		var targetParent: NodeRB? = null
		var currentNode = root
		while (currentNode != null)
		{
			targetParent = currentNode
			currentNode = when
			{
				// NodeRB already exists in the tree
				targetNode.key == currentNode.key ->
					return

				targetNode.key < currentNode.key ->
					currentNode.left

				else ->
					currentNode.right
			}
		}
		targetNode.parent = targetParent
		when
		{
			targetParent == null -> root = targetNode
			targetNode.key < targetParent.key -> targetParent.left = targetNode
			else -> targetParent.right = targetNode
		}
		targetNode.parent?.parent?.let { fixupInsert(targetNode) }
	}

	/**
	 * Fixes the tree after insertion to maintain the Red-Black Tree properties.
	 * We start with the newly inserted node and work our way up the tree fixing
	 * violations as we go.
	 *
	 * There are four cases to consider for a currently violating [NodeRB]:
	 * 1. Violating node is the root: Change to [Color.BLACK] -> Done
	 * 2. Violating node's uncle is [Color.RED]: Recolor parent, uncle, and
	 * grandparent -> Move up to grandparent and recheck
	 * 3. Violating node's uncle node is [Color.BLACK]: Rotate parent in
	 * opposite direction of violating node -> Recolor parent and grandparent
	 * -> Recheck
	 * 4. Violating node's uncle is [Color.BLACK] and parent is [Color.RED]:
	 * Rotate grandparent in the opposite direction of the violating node and
	 * Recolor -> Recheck
	 *
	 * @param inserted
	 *   The node that was inserted.
	 */
	private fun fixupInsert(inserted: NodeRB)
	{
		var current: NodeRB? = inserted
		while (current?.parent?.color == RED)
		{
			if (current.parent == current.parent?.parent?.left)
			{
				val uncle = current.parent?.parent?.right
				if (uncle?.color == RED)
				{
					// Case 1: Uncle Red
					current.parent?.color = BLACK
					uncle.color = BLACK
					current.parent?.parent?.color = RED
					current = current.parent?.parent
				}
				else
				{
					// Case 2: Uncle Black rotate left
					if (current == current.parent?.right)
					{
						current = current.parent
						leftRotate(current)
					}

					current?.parent?.color = BLACK
					current?.parent?.parent?.color = RED
					rightRotate(current?.parent?.parent)
				}
			}
			else
			{
				val uncle = current.parent?.parent?.left
				if (uncle?.color == RED)
				{
					// Case 1: Uncle Red
					current.parent?.color = BLACK
					uncle.color = BLACK
					current.parent?.parent?.color = RED
					current = current.parent?.parent
				}
				else
				{
					// Case 2: Uncle Black rotate right
					if (current == current.parent?.left)
					{
						current = current.parent
						rightRotate(current)
					}
					current?.parent?.color = BLACK
					current?.parent?.parent?.color = RED
					leftRotate(current?.parent?.parent)
				}
			}
		}
		root?.color = BLACK
	}

	/**
	 * Checks if the tree is valid according to the Red-Black Tree properties:
	 *
	 * 1. A node is either red or black.
	 * 2. The root and leaves are black including NIL (`null`) children.
	 * 3. If a node is red, then its children are black.
	 * 4. All paths from a node to its NIL descendants contain the same number
	 * of black nodes.
	 *
	 * @return
	 *   `true` if the tree is valid, `false` otherwise.
	 */
	fun isValid(): Boolean
	{
		if (root == null) return true // An empty tree is valid
		if (root?.color != BLACK) return false // #2 The root must be black

		return checkProperties(root) != -1
	}

	/**
	 * Recursively checks the Red-Black Tree properties for each node in the
	 * tree.
	 *
	 * @param node
	 *   The current node to check the properties for.
	 * @return
	 *   `-1` if the tree is invalid, otherwise the number of black nodes in the
	 *   path from the current node to its NIL descendants.
	 */
	private fun checkProperties(node: NodeRB?): Int
	{
		if (node == null) return 1 // null nodes are black

		val left = checkProperties(node.left)
		val right = checkProperties(node.right)

		// Check #3
		when
		{
			node.color == RED ->
			{
				if (node.left?.color == RED || node.right?.color == RED)
				{
					return -1
				}
			}

			// Check #4
			left == -1 || right == -1 || left != right ->
			{
				return -1
			}

			// Count black nodes
			node.color == BLACK ->
			{
				return left + 1
			}
		}
		// Return the number of black nodes in the path, either left or right,
		// will be the same.
		return left
	}
}
