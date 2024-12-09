package com.tdxtxt.logger.tree

import android.util.Log
import com.tdxtxt.logger.LogA
import com.tdxtxt.logger.Utils
import kotlin.math.min

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2024-10-18
 *     desc   : 日志输出树
 *    ┌──────────────────────────
 *    │ Thread information
 *    │ Method stack history
 *    ├┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
 *    │ Log message
 *    └──────────────────────────
 * </pre>
 */
class DebugTree : LogA.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        logTopBorder(priority, tag)
        logHeader(priority, tag)
        logDivider(priority, tag)
        logMessage(priority, tag, message, t)
        logBottomBorder(priority, tag)
    }

    private fun logHeader(priority: Int, tag: String?) {
        logcatLog(priority, tag, Utils.HORIZONTAL_LINE + " Thread: " + Thread.currentThread().getName())
        logcatLog(priority, tag, Utils.getTopStackTrace())
    }

    private fun logMessage(priority: Int, tag: String?, message: String, t: Throwable?) {
        val bytes = message.toByteArray()
        val length = bytes.size
        if (length <= Utils.MAX_LENGTH_LINE) {
            formatPrintLine(priority, tag, message)
        } else {
            for (i in 0 until length step Utils.MAX_LENGTH_LINE) {
                val count = min(length - i, Utils.MAX_LENGTH_LINE)
                formatPrintLine(priority, tag, String(bytes, i, count))
            }
        }
    }

    private fun logDivider(priority: Int, tag: String?) {
        logcatLog(priority, tag, Utils.MIDDLE_BORDER)
    }
    private fun logTopBorder(priority: Int, tag: String?) {
        logcatLog(priority, tag, Utils.TOP_BORDER)
    }
    private fun logBottomBorder(priority: Int, tag: String?) {
        logcatLog(priority, tag, Utils.BOTTOM_BORDER)
    }

    private fun logcatLog(priority: Int, tag: String?, message: String) {
        Log.println(priority, tag, message)
    }

    private fun formatPrintLine(priority: Int, tag: String?, message: String) {
        val lines = message.split(System.getProperty("line.separator"))
        for (line in lines) {
            logcatLog(priority, tag, "│ $line")
        }
    }
}