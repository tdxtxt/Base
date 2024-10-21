package com.tdxtxt.logger

import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2024-10-18
 *     desc   :
 * </pre>
 */
object Utils {
    const val MAX_LENGTH_LINE = 1 * 1024 * 1024 //每次最多打印1kb
    const val MAX_LENGTH_FILE = 5 * 1024 * 1024 * 1024 //文件最大5M

    const val SENDMSG_SAVE_LOG = 1
    const val SENDMSG_DELETE_FILE = 2

    const val MIN_STACK_OFFSET = 5
    const val TOP_LEFT_CORNER = '┌'
    const val BOTTOM_LEFT_CORNER = '└'
    const val MIDDLE_CORNER = '├'
    const val HORIZONTAL_LINE = '│'
    const val DOUBLE_DIVIDER = "────────────────────────────"
    const val SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    const val TOP_BORDER = TOP_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    const val BOTTOM_BORDER = BOTTOM_LEFT_CORNER.toString() + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    const val MIDDLE_BORDER = MIDDLE_CORNER.toString() + SINGLE_DIVIDER + SINGLE_DIVIDER

    fun getTopStackTrace(): String {
        val trace = Thread.currentThread().stackTrace
        val statckOffset = getStackOffset(trace)
        if (statckOffset < 0 || statckOffset >= trace.size) return ""
        val builder = StringBuilder()
        builder.append(HORIZONTAL_LINE)
            .append(' ')
            .append(getSimpleClassName(trace[statckOffset].className))
            .append(".")
            .append(trace[statckOffset].methodName)
            .append(" ")
            .append(" (")
            .append(trace[statckOffset].fileName)
            .append(":")
            .append(trace[statckOffset].lineNumber)
            .append(")")
        return builder.toString()
    }

    fun getLogFile(rootDir: String, printLogTime: Long): File {
        val rootFolder = File(rootDir)
        if (!rootFolder.exists()) {
            rootFolder.mkdir()
        }
        val logFolder = File(rootFolder, getDirName(printLogTime))
        if (!logFolder.exists()) {
            logFolder.mkdir()
        }
        val fileName = getFileName(printLogTime)
        var newFileCount = 1
        var existingFile: File? = null
        var newFile = File(logFolder, String.format("%s_%s.log", fileName, newFileCount))
        while (existingFile == null) {
            if (newFile.length() < MAX_LENGTH_FILE) {
                existingFile = newFile
            } else {
                newFile = File(logFolder, String.format("%s_%s.log", fileName, newFileCount))
            }
            newFileCount++
        }

        return existingFile
    }

    fun formatTime(time: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(time))
    }

    fun deleteCacheLog(rootDir: String, keepDays: Int) {
        val rootFolder = File(rootDir)
        if (!rootFolder.exists()) return
        val listFile = rootFolder.listFiles()
        listFile.forEach { file ->
            val time = getTimeByDirName(file.name)
            val day = (System.currentTimeMillis() - time) / 86400000
            if (abs(day) > keepDays) {
                deleteFile(file)
            }
        }
    }

    private fun deleteFile(file: File) {
        if (file.exists()) {
            if (file.isDirectory) {
                file.listFiles().forEach {
                    deleteFile(it)
                }
            }
            file.delete()
        }
    }

    private fun getDirName(time: Long): String {
        return SimpleDateFormat("yyyy-MM-dd").format(Date(time))
    }

    private fun getTimeByDirName(dirName: String): Long {
        try {
            return SimpleDateFormat("yyyy-MM-dd").parse(dirName).time
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return -1
    }

    private fun getFileName(time: Long): String {
        return SimpleDateFormat("HH").format(Date(time))
    }

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var index = MIN_STACK_OFFSET
        while (index < trace.size) {
            val e = trace.getOrNull(index)
            val name = e?.className
            if (LogA.Forest::class.java.name.equals(name)) {
                return index + 1
            }
            index++
        }
        return -1
    }

    private fun getSimpleClassName(name: String): String {
        val lastIndex = name.lastIndexOf(".")
        return name.substring(lastIndex + 1)
    }
}