package com.tdxtxt.social.core.lisenter

import android.content.Context
import java.io.File

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
interface IRequestAdapter {
    fun downloadImageSync(context: Context?, url: String?): File?
}