package com.tdxtxt.social.core.lisenter.impl

import android.content.Context
import com.tdxtxt.social.core.lisenter.IRequestAdapter
import java.io.File

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023-08-14
 *     desc   :
 * </pre>
 */
class DefaultRequestAdapter : IRequestAdapter {
    override fun downloadImageSync(context: Context?, url: String?): File? {
        return null
    }

}