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
 *     desc   : 磁盘日志，缓存文件目录名称规则：rootDir/log/{yyyy-MM-dd}/{HH}_{count}.{suffix}
 *     rootDir: 缓存目录，常见缓存目录如下
 *              context.getCacheDir()：/data/data/<包名>/cache
 *              context.getFilesDir()：/data/data/<包名>/files
 *              context.getExternalCacheDir()：/storage/emulate/0/Android/data/<包名>/cache
 *              context.getExternalFilesDir()：/storage/emulate/0/Android/data/<包名>/files
 *     keepDays：缓存事件，默认缓存一天
 * </pre>
 */
class DiskTree(val rootDir: String, val keepDays: Int = 1) : LogA.Tree() {
    private val mHandlerThread = HandlerThread("WriterLogThread")
    private val mWorker: Worker by lazy { Worker(rootDir, keepDays, mHandlerThread) }
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
        writeLog(tag, builder.toString(), currentTime, "log")
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

    override fun write(suffix: String, message: String?) {
        if (message.isNullOrEmpty()) return
        writeLog(null, message, System.currentTimeMillis(), suffix)
    }

    private fun writeLog(tag: String?, message: String, currentTime: Long, suffix: String) {
        if (!mHandlerThread.isAlive) { //启动线程
            mHandlerThread.start()
            //删除过期文件
            mWorker.sendMessage(Message.obtain().apply {
                what = Utils.SENDMSG_DELETE_FILE
            })
        }

        //写日志
        mWorker.sendMessage(Message.obtain().apply {
            what = Utils.SENDMSG_SAVE_LOG
            obj = LogInfo(tag, message, currentTime, suffix)
        })
    }

    private class Worker(val rootDir: String, val keepDays: Int, handlerThread: HandlerThread) :
        Handler(handlerThread.looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Utils.SENDMSG_SAVE_LOG -> {
                    val logInfo = msg.obj
                    if (logInfo is LogInfo) {
                        val logFile = Utils.getLogFile(rootDir, logInfo.suffix, logInfo.curTime)
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
                    Utils.deleteCacheLog(rootDir, Utils.DIR_LOG, keepDays)
                }
            }
        }
    }

    private class LogInfo(
        val tag: String?,
        val message: String,
        val curTime: Long,
        val suffix: String
    )
}