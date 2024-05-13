package org.bitwisearts.test.tree

import org.bitwisearts.tree.BasicBinarySearchTree
import org.bitwisearts.tree.NodeBST
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

/**
 * Test for [BasicBinarySearchTree].
 */
class BasicBinarySearchTreeTest
{
	@Test
	fun insert()
	{
		val tree = BasicBinarySearchTree(NodeBST(7))
		assertEquals(1, tree.nodeCount)
		tree.insert(3)
		assertEquals(2, tree.nodeCount)
		tree.insert(10)
		assertEquals(3, tree.nodeCount)
		tree.insert(2)
		assertEquals(4, tree.nodeCount)
		tree.insert(5)
		assertEquals(5, tree.nodeCount)
		tree.insert(7)
		assertEquals(5, tree.nodeCount)
		tree.insert(8)
		assertEquals(6, tree.nodeCount)
		tree.insert(3)
		assertEquals(6, tree.nodeCount)
		tree.insert(12)
		assertEquals(7, tree.nodeCount)
	}

	@Test
	fun search()
	{
		val tree = BasicBinarySearchTree(NodeBST(7))
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

		var node: NodeBST? = tree.search(10)
		assertEquals(10, node?.key)
		node = tree.search(8)
		assertEquals(8, node?.key)
		node = tree.search(88)
		assertNull(node)
	}

	@Test
	fun height()
	{
		assertEquals(-1, BasicBinarySearchTree().height)
		val tree = BasicBinarySearchTree(NodeBST(72))
		assertEquals(0, tree.height)
		tree.insert(25)
		assertEquals(1, tree.height)
		tree.insert(100)
		assertEquals(1, tree.height)
		tree.insert(12)
		assertEquals(2, tree.height)
		tree.insert(50)
		assertEquals(2, tree.height)
		tree.insert(200)
		assertEquals(2, tree.height)
		tree.insert(8)
		assertEquals(3, tree.height)
		tree.insert(4)
		assertEquals(4, tree.height)
	}

	@Test
	fun delete() {
		val tree = {
			BasicBinarySearchTree(NodeBST(72)).apply {
				insert(25)
				insert(100)
				insert(12)
				insert(50)
				insert(53)
				insert(200)
				insert(8)
				insert(4)
			}
		}
		val tree1 = tree()
		assertEquals(4, tree1.height)
		tree1.delete(53_000_000)
		assertEquals(tree(), tree1)
		tree1.delete(4)
		assertNull(tree1.search(4))
		tree1.delete(25)
		assertNull(tree1.search(25))

		val tree2 = tree().apply {
			insert(62)
			insert(99)
			insert(87)
			insert(999)
			insert(9999)
			insert(1)
			insert(10)
			insert(11)
			insert(12)
			insert(13)
		}
		tree2.delete(25)
		assertNotNull(tree2.search(1))
		tree2.insert(0)
		tree2.delete(72)
		assertNull(tree2.search(99))
	}
}
