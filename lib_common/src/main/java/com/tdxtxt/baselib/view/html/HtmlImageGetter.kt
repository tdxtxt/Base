package com.tdxtxt.baselib.view.html

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html.ImageGetter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.ref.WeakReference

/**
 * <pre>
 *     author : tangdexiang
 *     time   : 2023/5/26
 *     desc   : 自定义ImageGetter，实现HTML显示各种类型的图片。
 * </pre>
 */
class HtmlImageGetter(val textViewRef: WeakReference<TextView>?, val matchParentWidth: Boolean) : ImageGetter {

    override fun getDrawable(source: String?): Drawable? {
        val image = source?.replace("\\\"", "")
        if(image?.startsWith("http") == true || image?.startsWith("data:image") == true){
            return loadImageFromSource(image)
        }
        return null
    }
    private fun getTextView() = textViewRef?.get()
    private fun loadImageFromSource(image: String): Drawable? {
        val context = getTextView()?.context?: return null
        val listDrawable = LevelListDrawable()
        Glide.with(context).asBitmap().load(image)
            .into(object : SimpleTarget<Bitmap>(){
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    getTextView()?.post {
                        val bitmapWidth = bitmap.width
                        val bitmapHeight = bitmap.height
                        if(bitmapWidth == 0 || bitmapHeight == 0) return@post
                        val textView = getTextView()?: return@post

                        val scale = calculateScale(textView, bitmapWidth)
                        val bitmapDrawable = BitmapDrawable(context.resources, bitmap)
                        listDrawable.addLevel(1, 1, bitmapDrawable)
                        listDrawable.setBounds(0, 0,
                            (bitmap.width * scale).toInt(),
                            (bitmap.height * scale).toInt()
                        )
                        listDrawable.setLevel(1)
//                        textView.invalidate()
                        textView.text = textView.text
                    }
                }
            })
        return listDrawable
    }

    private fun scaleAndFillBitmap(textView: TextView?, originalBitmap: Bitmap?, scale: Float): BitmapDrawable? {
        if(textView == null || originalBitmap == null) return null
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height

        val scaledWidth = Math.round(originalWidth * scale)
        val scaledHeight = Math.round(originalHeight * scale)

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)
        return BitmapDrawable(textView.resources, scaledBitmap)
    }

    private fun calculateScale(textView: TextView?, originalBitmapWidth: Int): Float {
        if(textView == null) return 1f
        val drawables = textView.compoundDrawables //Left、Top、right、bottom
        val drawableWidth = (drawables.getOrNull(0)?.intrinsicWidth?: 0) + (drawables.getOrNull(2)?.intrinsicWidth?: 0)
        val maxWidth = textView.measuredWidth - textView.paddingStart - textView.paddingEnd - textView.compoundDrawablePadding - drawableWidth
        if(maxWidth <= 0) return 1f
        if(originalBitmapWidth <= 0) return 1f
        if(matchParentWidth || originalBitmapWidth > maxWidth) {
            return maxWidth.toFloat() / originalBitmapWidth.toFloat()
        }
        return 1f
    }
}