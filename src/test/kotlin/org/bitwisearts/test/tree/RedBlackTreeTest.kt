package org.bitwisearts.test.tree

import org.bitwisearts.tree.Color
import org.bitwisearts.tree.RedBlackTree
import org.bitwisearts.tree.NodeRB
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RedBlackTreeTest
{
	@Test
	fun insert()
	{
		val tree = RedBlackTree(NodeRB(7, Color.BLACK))
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
		tree.insert(17)
		assertEquals(3, tree.height)
	}

	@Test
	fun search()
	{
		val tree = RedBlackTree(NodeRB(7, Color.BLACK))
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

		var node: NodeRB? = tree.search(10)
		assertEquals(10, node?.key)
		node = tree.search(8)
		assertEquals(8, node?.key)
		node = tree.search(88)
		assertNull(node)
	}
}
