package com.tdxtxt.logger.tree

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.tdxtxt.logger.LogA
import com.tdxtxt.logger.Utils
import java.io.FileWriter

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2024-10-18
 *     desc   : 磁盘日志，缓存文件目录名称规则：rootDir/{yyyy-MM-dd}/{HH}_{count}.log
 *     常见缓存目录选择：
 *     /data/data/<包名>/cache context.getCacheDir()
 *     /data/data/<包名>/files context.getFilesDir()
 *     /storage/emulate/0/Android/data/<包名>/cache context.getExternalCacheDir()
 *     /storage/emulate/0/Android/data/<包名>/files context.getExternalFilesDir()
 * </pre>
 */
class DiskTree(val rootDir: String, val keepDays: Int = 2) : LogA.Tree() {
    private val mHandlerThread = HandlerThread("WriterLogThread")
    private val mWorker: Worker by lazy { Worker(rootDir, keepDays, mHandlerThread) }
    private var isDeleteFile = true
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val currentTime = System.currentTimeMillis()
        val builder = StringBuilder()
        builder.append(logTopBorder())
            .append(System.lineSeparator())
            .append(logHeaderContent(currentTime))
            .append(System.lineSeparator())
            .append(logMessage(message))
            .append(System.lineSeparator())
            .append(logBottomBorder())
        writeLog(tag, builder.toString(), currentTime)
    }

    private fun logHeaderContent(currentTime: Long): String {
        val builder = StringBuilder()
        builder.append(Utils.HORIZONTAL_LINE)
            .append(" ")
            .append(Utils.formatTime(currentTime))
            .append(System.lineSeparator())
            .append(Utils.HORIZONTAL_LINE)
            .append((" "))
            .append("Thread: ")
            .append(Thread.currentThread().name)
            .append(Utils.getTopStackTrace())
        return builder.toString().trim()
    }

    private fun logMessage(message: String): String {
        val builder = StringBuilder()
        val lines = message.split(System.getProperty("line.separator"))
        for (line in lines) {
            builder.append(Utils.HORIZONTAL_LINE)
                .append(" ")
                .append(line)
                .append(System.lineSeparator())
        }
        return builder.toString().trim()
    }

    private fun logTopBorder(): String {
        return Utils.TOP_BORDER
    }

    private fun logBottomBorder(): String {
        return Utils.BOTTOM_BORDER
    }

    private fun writeLog(tag: String?, message: String, currentTime: Long) {
        if (!mHandlerThread.isAlive) mHandlerThread.start() // 启动线程

        mWorker.sendMessage(Message.obtain().apply {
            what = Utils.SENDMSG_SAVE_LOG
            obj = LogInfo(tag, message, currentTime)
        })

        if (isDeleteFile) {
            isDeleteFile = false
            mWorker.sendMessage(Message.obtain().apply {
                what = Utils.SENDMSG_DELETE_FILE
            })
        }

    }

    private class Worker(val rootDir: String, val keepDays: Int, handlerThread: HandlerThread) :
        Handler(handlerThread.looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Utils.SENDMSG_SAVE_LOG -> {
                    val logInfo = msg.obj
                    if (logInfo is LogInfo) {
                        val logFile = Utils.getLogFile(rootDir, logInfo.curTime)
                        var fileWriter: FileWriter? = null
                        try {
                            fileWriter = FileWriter(logFile, true)
                            fileWriter.append(logInfo.message + System.lineSeparator())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            try {
                                fileWriter?.flush()
                                fileWriter?.close()
                            } catch (e: Exception) {
                            }
                        }
                    }
                }

                Utils.SENDMSG_DELETE_FILE -> {
                    Utils.deleteCacheLog(rootDir, keepDays)
                }
            }
        }
    }

    private class LogInfo(val tag: String?, val message: String, val curTime: Long)
}