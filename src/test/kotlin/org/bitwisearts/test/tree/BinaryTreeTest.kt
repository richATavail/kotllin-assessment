package org.bitwisearts.test.tree

import kotlinx.coroutines.runBlocking
import org.bitwisearts.tree.BasicBinarySearchTree
import org.bitwisearts.tree.BinaryTree
import org.bitwisearts.tree.Node
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Test for [BasicBinarySearchTree].
 */
class BinaryTreeTest
{
	@Test
	fun insert()
	{
		val tree = BinaryTree(Node(7))
		assertEquals(0, tree.height)
		tree.insert(3)
		tree.insert(10)
		assertEquals(1, tree.height)
		tree.insert(2)
		tree.insert(5)
		tree.insert(7)
		tree.insert(8)
		assertEquals(2, tree.height)
		tree.insert(3)
		tree.insert(12)
		assertEquals(3, tree.height)
	}

	@Test
	fun search()
	{
		val tree = BinaryTree(Node(7))
		tree.insert(3)
		tree.insert(10)
		tree.insert(2)
		tree.insert(5)
		tree.insert(2)
		tree.insert(8)
		tree.insert(12)
		tree.insert(62)
		tree.insert(99)
		tree.insert(87)
		tree.insert(999)
		tree.insert(9999)
		tree.insert(9)
		tree.insert(10)
		tree.insert(11)
		tree.insert(12)
		tree.insert(13)

		runBlocking {
			var node: Node? = tree.parallelSearch(10)
			assertEquals(10, node?.value)
			node = tree.parallelSearch(8)
			assertEquals(8, node?.value)
			node = tree.parallelSearch(88)
			assertNull(node)
		}
	}
}
