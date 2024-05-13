package org.bitwisearts.filesystem

import java.util.LinkedList
import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap

/**
 * A directory in the in-memory file system that can contain other directories
 * and files.
 *
 * **Note:** This the basis for an exploration of an in-memory file system. This
 * is not a complete implementation. The goal is to explore the basic concepts
 * of a file system and how it can be implemented in memory without implementing
 * the backing data structures that would be used in a real in-memory file
 * system. This example uses a [TreeMap] to store the file system structure as
 * a simple way to demonstrate the concept. However, a real file system would
 * use a more complex data structure to store the file system structure, such as
 * a [B-Tree](https://en.wikipedia.org/wiki/B-tree) or
 * [Prefix Tree (Trie)](https://en.wikipedia.org/wiki/Trie). These both represent
 * "n-ary" trees, which are trees that can have more than two children per node.
 * This is needed to represent the hierarchical structure of a file system as a
 * tree providing the benefits of the tree structure, such as fast search and
 * retrieval, while allowing for an arbitrary number of children per node.
 *
 * * B-Tries are used in file systems because they are efficient for storing and
 *  retrieving data in a hierarchical structure. The time complexity for
 *  searching a key in a B-Tree is O(log n). Additionally, B-Trees are always
 *  balanced, which means that the height of the tree is always logarithmic.
 *  This decreases the number of hops required to find a key in the tree. It is
 *  also possible to store a large number of keys in a B-Tree node, which makes
 *  it efficient for storing large amounts of data.
 *
 * * Prefix trees are very good at prefix matching which aligns well with the
 * file system directory structure. They also do not have hash collisions as
 * each node has a unique path to the root. The keys are always stored in a
 * sorted order which makes it easy to find the next key in the tree. The other
 * benefit of a trie is that it can quickly autocomplete keys, i.e. the paths in
 * the file system.
 *
 * Considering an in-memory file system in a JVM environment, the file system
 * will utilize references to memory locations stored in the heap. So the tree
 * structure will be a tree of references to the actual data structures that
 * represent the files and directories in the JVM heap. References are much
 * smaller than the data they refer to so it is inexpensive to ensure that each
 * [File] and [Directory] maintains a reference to its parent so we can get any
 * where in the file system "instantly" once we have a reference to a file or
 * directory.
 *
 * Because of the light-weight nature or reference into the heap ,the primary
 * algorithm to focus on is the search algorithm for finding a file or
 * directory. Once located, a [File] or [Directory] object can be used to
 * interact with the file system utilizing their references to their parents and
 * children (if a directory) to quickly navigate and manipulate the file system.
 * The only operations implemented here as examples are [getDirectory] and
 * [getFile]. These are the most important operations in a file system as they
 * represent the search algorithms of the tree.
 *
 * Operations for consideration in a real implementation:
 *
 * 1. **Create File**: This operation is used to create a new file in the file
 * system and save it in the specified directory.
 * 2. **Read File**: This operation is used to read the contents of a file.
 * 3. **Write File**: This operation is used to write data to a file. It could
 * be appending data to the end of the file or overwriting the existing data.
 * 4. **Delete File**: This operation is used to delete a file from the file
 * system.
 * 5. **Create Directory**: This operation is used to create a new directory in
 * the file system.
 * 6. **List Directory**: This operation is used to list the contents of a
 * directory.
 * 7. **Delete Directory**: This operation is used to delete a directory from
 * the file system. Usually, this operation requires the directory to be empty.
 * 8. **Move File/Directory**: This operation is used to move a file or
 * directory from one location to another.
 * 9. **Copy File/Directory**: This operation is used to create a copy of a file
 * or directory at a new location.
 * 10. **Get Metadata**: This operation is used to retrieve metadata about a
 * file or directory, such as its size, creation time, modification time, etc.
 * 11. **Update Metadata**: This operation is used to update metadata of a file
 * or directory, such as changing its permissions, owner, etc.
 * 12. **Symlink**: This operation is used to create a symbolic link to a file
 * or directory.
 *
 * Presumably more work would be done to ensure that the directory structure is
 * thread-safe and that the child directories are stored in a way that is
 * efficient for access.
 *
 * @property name
 *   The name of the directory. This is the final component of the path to the
 *   directory.
 * @property absolutePath
 *   The absolute path of the directory in the file system. This is the full
 *   string path from the root directory to this directory.
 * @property parent
 *   The parent directory of this directory. This is `null` if this directory is
 *   the root directory.
 */
class Directory(
	val name: String,
	val absolutePath: String,
	val parent: Directory?
) {
	/**
	 * The tree map of child directories in this directory. The directories are
	 * keyed by their name, not their full path.
	 */
	val children = TreeMap<String, Directory>()

	/**
	 * The files in this directory.
	 */
	val files = ConcurrentHashMap<String, File>()

	/**
	 * Retrieves a directory by its path.
	 *
	 * Per the explanation in the class comment, this is one of the most
	 * important algorithms in a file system as it is used retrieve directories.
	 *
	 * @param path
	 *   The path to the directory to retrieve. The path is a list of directory
	 *   names that represent the path to the directory to retrieve.
	 * @return
	 *   The directory at the specified path, or `null` if the directory does
	 *   not exist.
	 */
	fun getDirectory(path: LinkedList<String>): Directory?
	{
		if (path.isEmpty())
		{
			return null
		}
		val nextDir = path.removeFirst()
		val directory = children[nextDir] ?: return null
		if (path.isEmpty())
		{
			return directory
		}
		return directory.getDirectory(path)
	}

	/**
	 * Retrieves a file by its path.
	 *
	 * Per the explanation in the class comment, this is one of the most
	 * important algorithms in a file system as it is used retrieve files.
	 *
	 * @param fileName
	 *   The name of the file to retrieve.
	 * @param path
	 *   The path to the file to retrieve. The path is the nested directory
	 *   names. The last element in the list directory the file is in.
	 */
	fun getFile(fileName: String, path: LinkedList<String>): File?
	{
		if (path.isEmpty())
		{
			return files[fileName]
		}
		val nextDir = path.removeFirst()
		val directory = children[nextDir] ?: return null
		return directory.getFile(fileName, path)
	}
}

/**
 * A file in the file system that contains data. With respect to the original
 * question about the file system, this class offers little in the greater scope
 * of the question to describe the data structures and algorithms used in a file
 * system.
 *
 * There is one opportunity for optimization in `File` that could be relevant to
 * the original question of creating an in-memory file system. As the amount of
 * memory available "in memory" is limited, much more so than on disck, it would
 * be beneficial to store the file data in a more efficient way. For example,
 * the file data could be stored in a compressed format and decompressed when
 * read. This would reduce the amount of memory used by the file system.
 *
 * However, mostly it is included here to provide a more complete example of a
 * file system's data structures and how they might be implemented in memory.
 *
 * @property name
 *   The name of the file.
 * @property directory
 *   The directory that contains this [File].
 * @property data
 *   The data stored in the file.
 * @property lastUpdate
 *   The time in milliseconds since the epoch when the file was last updated.
 * @property mimeType
 *   The MIME type of the file if known.
 */
class File(
	val name: String,
	val directory: Directory,
	var data: ByteArray,
	var lastUpdate: Long = 0,
	var mimeType: String?
) {
	/**
	 * The size of the file in bytes.
	 */
	val size: Int get() = data.size

	/**
	 * Saves data to the file. This is a full overwrite of the file's data.
	 */
	fun saveData(data: ByteArray)
	{
		this.data = data
	}
}
