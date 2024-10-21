package com.tdxtxt.logger
import android.util.Log
import com.tdxtxt.logger.tree.DebugTree
import org.jetbrains.annotations.NonNls
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.Collections.unmodifiableList

/**
 * <pre>
 *     author : ton
 *     time   : 2023/2/13
 *     desc   : https://github.com/JakeWharton/timber/blob/trunk/timber/src/main/java/timber/log/Timber.kt
 *    ┌──────────────────────────
 *    │ Method stack history
 *    ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 *    │ Thread information
 *    ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 *    │ Log message
 *    └──────────────────────────
 * </pre>
 */
class LogA {
    init {
        throw AssertionError()
    }

    /** A facade for handling logging calls. Install instances via [`Timber.plant()`][.plant]. */
    abstract class Tree {
        /** Log a verbose message with optional format args. */
        open fun v(tag: String, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.VERBOSE, null, message, *args)
        }

        /** Log a verbose exception and a message with optional format args. */
        open fun v(tag: String, t: Throwable?, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.VERBOSE, t, message, *args)
        }

        /** Log a verbose exception. */
        open fun v(tag: String, t: Throwable?) {
            prepareLog(tag, Log.VERBOSE, t, null)
        }

        /** Log a debug message with optional format args. */
        open fun d(tag: String, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.DEBUG, null, message, *args)
        }

        /** Log a debug exception and a message with optional format args. */
        open fun d(tag: String, t: Throwable?, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.DEBUG, t, message, *args)
        }

        /** Log a debug exception. */
        open fun d(tag: String, t: Throwable?) {
            prepareLog(tag, Log.DEBUG, t, null)
        }

        /** Log an info message with optional format args. */
        open fun i(@NonNls tag: String, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.INFO, null, message, *args)
        }



        /** Log an info exception and a message with optional format args. */
        open fun i(tag: String, t: Throwable?, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.INFO, t, message, *args)
        }

        /** Log an info exception. */
        open fun i(tag: String, t: Throwable?) {
            prepareLog(tag, Log.INFO, t, null)
        }

        /** Log a warning message with optional format args. */
        open fun w(tag: String, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.WARN, null, message, *args)
        }

        /** Log a warning exception and a message with optional format args. */
        open fun w(tag: String, t: Throwable?, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.WARN, t, message, *args)
        }

        /** Log a warning exception. */
        open fun w(tag: String, t: Throwable?) {
            prepareLog(tag, Log.WARN, t, null)
        }

        /** Log an error message with optional format args. */
        open fun e(tag: String, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.ERROR, null, message, *args)
        }

        /** Log an error exception and a message with optional format args. */
        open fun e(tag: String, t: Throwable?, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.ERROR, t, message, *args)
        }

        /** Log an error exception. */
        open fun e(tag: String, t: Throwable?) {
            prepareLog(tag, Log.ERROR, t, null)
        }

        /** Log an assert message with optional format args. */
        open fun wtf(tag: String, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.ASSERT, null, message, *args)
        }

        /** Log an assert exception and a message with optional format args. */
        open fun wtf(tag: String, t: Throwable?, message: String?, vararg args: Any?) {
            prepareLog(tag, Log.ASSERT, t, message, *args)
        }

        /** Log an assert exception. */
        open fun wtf(tag: String, t: Throwable?) {
            prepareLog(tag, Log.ASSERT, t, null)
        }

        /** Log at `priority` a message with optional format args. */
        open fun log(tag: String, priority: Int, message: String?, vararg args: Any?) {
            prepareLog(tag, priority, null, message, *args)
        }

        /** Log at `priority` an exception and a message with optional format args. */
        open fun log(tag: String, priority: Int, t: Throwable?, message: String?, vararg args: Any?) {
            prepareLog(tag, priority, t, message, *args)
        }

        /** Log at `priority` an exception. */
        open fun log(tag: String, priority: Int, t: Throwable?) {
            prepareLog(tag, priority, t, null)
        }

        /** Return whether a message at `priority` should be logged. */
        @Deprecated("Use isLoggable(String, int)", ReplaceWith("this.isLoggable(null, priority)"))
        protected open fun isLoggable(priority: Int) = true

        /** Return whether a message at `priority` or `tag` should be logged. */
        protected open fun isLoggable(tag: String?, priority: Int) = isLoggable(priority)

        private fun prepareLog(tag: String? = null, priority: Int, t: Throwable?, message: String?, vararg args: Any?) {
            // Consume tag even when message is not loggable so that next message is correctly tagged.
            val tag = tag
            if (!isLoggable(tag, priority)) {
                return
            }

            var message = message
            if (message.isNullOrEmpty()) {
                if (t == null) {
                    return  // Swallow message if it's null and there's no throwable.
                }
                message = getStackTraceString(t)
            } else {
                if (args.isNotEmpty()) {
                    message = formatMessage(message, args)
                }
                if (t != null) {
                    message += "\n" + getStackTraceString(t)
                }
            }
            log(priority, tag, message.trim(), t)
        }

        /** Formats a log message with optional arguments. */
        protected open fun formatMessage(message: String, args: Array<out Any?>) = message.format(*args)

        private fun getStackTraceString(t: Throwable): String {
            // Don't replace this with Log.getStackTraceString() - it hides
            // UnknownHostException, which is not what we want.
            val sw = StringWriter(256)
            val pw = PrintWriter(sw, false)
            t.printStackTrace(pw)
            pw.flush()
            return sw.toString()
        }

        /**
         * Write a log message to its destination. Called for all level-specific methods by default.
         *
         * @param priority Log level. See [Log] for constants.
         * @param tag Explicit or inferred tag. May be `null`.
         * @param message Formatted log message.
         * @param t Accompanying exceptions. May be `null`.
         */
        protected abstract fun log(priority: Int, tag: String?, message: String, t: Throwable?)
    }
    companion object Forest : Tree() {
        /** Log a verbose message with optional format args. */
        @JvmStatic override fun v(tag: String, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.v(tag, message, *args) }
        }

        /** Log a verbose exception and a message with optional format args. */
        @JvmStatic override fun v(tag: String, t: Throwable?, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.v(tag, t, message, *args) }
        }

        /** Log a verbose exception. */
        @JvmStatic override fun v(tag: String, t: Throwable?) {
            treeArray.forEach { it.v(tag, t) }
        }

        /** Log a debug message with optional format args. */
        @JvmStatic override fun d(tag: String, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.d(tag, message, *args) }
        }

        /** Log a debug exception and a message with optional format args. */
        @JvmStatic override fun d(tag: String, t: Throwable?, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.d(tag, t, message, *args) }
        }

        /** Log a debug exception. */
        @JvmStatic override fun d(tag: String, t: Throwable?) {
            treeArray.forEach { it.d(tag, t) }
        }

        /** Log an info message with optional format args. */
        @JvmStatic override fun i(@NonNls tag: String, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.i(tag, message, *args) }
        }

        /** Log an info exception and a message with optional format args. */
        @JvmStatic override fun i(tag: String, t: Throwable?, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.i(tag, t, message, *args) }
        }

        /** Log an info exception. */
        @JvmStatic override fun i(tag: String, t: Throwable?) {
            treeArray.forEach { it.i(tag, t) }
        }

        /** Log a warning message with optional format args. */
        @JvmStatic override fun w(tag: String, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.w(tag, message, *args) }
        }

        /** Log a warning exception and a message with optional format args. */
        @JvmStatic override fun w(tag: String, t: Throwable?, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.w(tag, t, message, *args) }
        }

        /** Log a warning exception. */
        @JvmStatic override fun w(tag: String, t: Throwable?) {
            treeArray.forEach { it.w(tag, t) }
        }

        /** Log an error message with optional format args. */
        @JvmStatic override fun e(tag: String, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.e(tag, message, *args) }
        }

        /** Log an error exception and a message with optional format args. */
        @JvmStatic override fun e(tag: String, t: Throwable?, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.e(tag, t, message, *args) }
        }

        /** Log an error exception. */
        @JvmStatic override fun e(tag: String, t: Throwable?) {
            treeArray.forEach { it.e(tag, t) }
        }

        /** Log an assert message with optional format args. */
        @JvmStatic override fun wtf(tag: String, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.wtf(tag, message, *args) }
        }

        /** Log an assert exception and a message with optional format args. */
        @JvmStatic override fun wtf(tag: String, t: Throwable?, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.wtf(tag, t, message, *args) }
        }

        /** Log an assert exception. */
        @JvmStatic override fun wtf(tag: String, t: Throwable?) {
            treeArray.forEach { it.wtf(tag, t) }
        }

        /** Log at `priority` a message with optional format args. */
        @JvmStatic override fun log(tag: String, priority: Int, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.log(tag, priority, message, *args) }
        }

        /** Log at `priority` an exception and a message with optional format args. */
        @JvmStatic
        override fun log(tag: String, priority: Int, t: Throwable?, @NonNls message: String?, vararg args: Any?) {
            treeArray.forEach { it.log(tag, priority, t, message, *args) }
        }

        /** Log at `priority` an exception. */
        @JvmStatic override fun log(tag: String, priority: Int, t: Throwable?) {
            treeArray.forEach { it.log(tag, priority, t) }
        }

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            throw AssertionError() // Missing override for log method.
        }

        /**
         * A view into Timber's planted trees as a tree itself. This can be used for injecting a logger
         * instance rather than using static methods or to facilitate testing.
         */
        @Suppress(
            "NOTHING_TO_INLINE", // Kotlin users should reference `Tree.Forest` directly.
            "NON_FINAL_MEMBER_IN_OBJECT" // For japicmp check.
        )
        @JvmStatic
        open inline fun asTree(): Tree = this

        /** Add a new logging tree. */
        @JvmStatic fun plant(tree: Tree) {
            require(tree !== this) { "Cannot plant Timber into itself." }
            synchronized(trees) {
                trees.add(tree)
                treeArray = trees.toTypedArray()
            }
        }

        /** Adds new logging trees. */
        @JvmStatic fun plant(vararg trees: Tree) {
            for (tree in trees) {
                requireNotNull(tree) { "trees contained null" }
                require(tree !== this) { "Cannot plant Timber into itself." }
            }
            synchronized(this.trees) {
                Collections.addAll(this.trees, *trees)
                treeArray = this.trees.toTypedArray()
            }
        }

        /** Remove a planted tree. */
        @JvmStatic fun uproot(tree: Tree) {
            synchronized(trees) {
                require(trees.remove(tree)) { "Cannot uproot tree which is not planted: $tree" }
                treeArray = trees.toTypedArray()
            }
        }

        /** Remove all planted trees. */
        @JvmStatic fun uprootAll() {
            synchronized(trees) {
                trees.clear()
                treeArray = emptyArray()
            }
        }

        /** Return a copy of all planted [trees][Tree]. */
        @JvmStatic fun forest(): List<Tree> {
            synchronized(trees) {
                return unmodifiableList(trees.toList())
            }
        }

        @get:[JvmStatic JvmName("treeCount")]
        val treeCount get() = treeArray.size

        // Both fields guarded by 'trees'.
        private val trees = ArrayList<Tree>(listOf(DebugTree()))
        @Volatile private var treeArray = trees.toTypedArray()
    }
}