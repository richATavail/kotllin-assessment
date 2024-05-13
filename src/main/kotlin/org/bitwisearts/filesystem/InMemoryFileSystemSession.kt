package org.bitwisearts.filesystem

import java.util.LinkedList

/**
 * Manages a session interacting with a virtual file system in memory. It
 * provides session-specific state and operations for interacting with the file
 * system. This could be used to simulate a user session in a terminal or
 * command-line interface.
 *
 * Another way this may be used is via a remote file system API where the
 * session represents a user's connection to the file system, either locally or
 * remotely. The session would maintain the user's state and provide operations
 * for interacting with the file system while the accessing application would
 * run in a separate process. Doing this would allow the application to manage
 * the file system state and operations in a way that is isolated from the
 * user's session and allow for multiple users to interact with the file system
 * at the same time. Multiple user sessions would require a more complex
 * implementation of the file system supporting users and permissions.
 *
 * **NOTE** The backing tree structure used to store the file system is not this
 * class's concern. This class is only responsible for managing the session. The
 * file system structure is managed by the [Directory] and [File] classes. The
 * [Directory] is the backing tree data structure that represents the file and
 * all tree-like operations should be handled by the [Directory] class.
 *
 * @property root
 *   The root directory of the file system.
 */
class InMemoryFileSystemSession(val root: Directory)
{
	/**
	 * The current directory in the file system that the session is working in.
	 */
	var currentDirectory = root

	/**
	 * A utility method to parse unix-style paths into a list of path components.
	 */
	private fun parsePath(path: String): LinkedList<String> =
		LinkedList<String>().apply {
			addAll(path.split("/"))
		}

	/**
	 * Retrieves a file by its path relative to the [currentDirectory].
	 */
	fun getFilePathRelative(path: String): File?
	{
		val parsedPath = parsePath(path)
		val fileName = parsedPath.removeLast()
		return currentDirectory.getFile(fileName, parsedPath)
	}

	/**
	 * Retrieves a file by its absolute path from the [root] in the file system.
	 */
	fun getFileAbsolutePath(path: String): File?
	{
		val parsedPath = parsePath(path)
		val fileName = parsedPath.removeLast()
		return root.getFile(fileName, parsedPath)
	}

	/**
	 * Retrieves a directory by its path relative to the [currentDirectory].
	 */
	fun getDirectoryPathRelative(path: String): Directory?
	{
		val parsedPath = LinkedList<String>().apply {
			addAll(path.split("/"))
		}
		return currentDirectory.getDirectory(parsedPath)
	}

	/**
	 * Retrieves a directory by its absolute path from the [root] in the file
	 * system.
	 */
	fun getDirectoryAbsolutePath(path: String): Directory?
	{
		val parsedPath = LinkedList<String>().apply {
			addAll(path.split("/"))
		}
		return root.getDirectory(parsedPath)
	}
}
