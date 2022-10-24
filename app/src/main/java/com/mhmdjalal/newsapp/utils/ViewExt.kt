package com.mhmdjalal.newsapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.text.LineBreaker
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.TextAppearanceSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textview.MaterialTextView
import splitties.mainthread.mainLooper


object ViewExt {

    /**
     * This view is visible
     */
    fun View.visible() {
        visibility = View.VISIBLE
    }

    /**
     * This view is invisible, but it still takes up space for layout purposes.
     */
    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    /**
     * This view is invisible, and it doesn't take any space for layout purposes.
     */
    fun View.gone() {
        visibility = View.GONE
    }

    /**
     * This view is enabled
     */
    fun View.enabled() {
        isEnabled = true
        alpha = 1f
    }

    /**
     * This view is disabled
     */
    fun View.disabled(alphaParam: Float = 1f) {
        isEnabled = false
        alpha = alphaParam
    }

    /**
     * View transparency reduced to 0.5 (still clickable)
     */
    fun View.disable(alphaParam: Float = 0.5f) {
        alpha = alphaParam
    }

    /**
     * The transparency of the view is fully displayed
     */
    fun View.enable() {
        alpha = 1f
    }

    /**
     * SwipeRefreshLayout -> Stop refreshing
     */
    fun SwipeRefreshLayout.stopRefreshing() {
        isRefreshing = false
    }

    /**
     * SwipeRefreshLayout -> Start refreshing
     */
    fun SwipeRefreshLayout.startRefreshing() {
        isRefreshing = true
    }

    /**
    * ViewPager2
    * @param sensitivity = semakin kecil nilainya semakin mudah digeser
    */
    fun ViewPager2.reduceDragSensitivity(sensitivity: Int = 5) {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * sensitivity)       // "8" was obtained experimentally
    }

    /**
     * TextView -> to justify text alignment
     * (only runs on android version O and above)
     */
    fun TextView.justify() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            this.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    /**
     * EditText -> Enable scrolling when the layout get focus
     */
    @SuppressLint("ClickableViewAccessibility")
    fun View.enableEditTextScroll() {
        this.setOnTouchListener { view, motionEvent ->
            if (this.hasFocus()) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        view.parent.requestDisallowInterceptTouchEvent(false)
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    fun Context.formatStyles(value: String, subValue: String, style: Int): SpannableString {
        var styledText: SpannableString
        try {
            val startIndex = value.indexOf(subValue)

            styledText = SpannableString(value)
            styledText.setSpan(
                TextAppearanceSpan(this, style),
                startIndex,
                startIndex + subValue.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } catch (e: Exception) {
            e.printStackTrace()
            styledText = SpannableString(value)
        }
        return styledText
    }

    fun SpannableString.formatStyles(context: Context, subValue: String, style: Int): SpannableString {
        try {
            val startIndex = this.indexOf(subValue)

            this.setSpan(
                TextAppearanceSpan(context, style),
                startIndex,
                startIndex + subValue.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
        val divider = DividerItemDecoration(
            this.context,
            DividerItemDecoration.VERTICAL
        )
        val drawable = ContextCompat.getDrawable(
            this.context,
            drawableRes
        )
        drawable?.let {
            divider.setDrawable(it)
            addItemDecoration(divider)
        }
    }

    fun MaterialTextView.addEndIcon(value: String?, @DrawableRes icon: Int, listener: ((View) -> Unit)? = null) {
        if (value.isNullOrEmpty()) {
            text = "-"
        } else {
            val string = "$value  "
            val stringBuilder = SpannableStringBuilder(string)
            val imageSpan = ImageSpan(context, icon, DynamicDrawableSpan.ALIGN_BASELINE)
            stringBuilder.setSpan(imageSpan, string.length.dec(), string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text = stringBuilder
            setOnClickListener(listener)
        }
    }

    fun Context.addEndIcon(text: String?, @DrawableRes icon: Int): SpannableStringBuilder {
        val string = "$text  "
        val stringBuilder = SpannableStringBuilder(string)
        val imageSpan = ImageSpan(this, icon, DynamicDrawableSpan.ALIGN_BASELINE)
        stringBuilder.setSpan(imageSpan, string.length.dec(), string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return stringBuilder
    }

    /**
     * Auto scroll view pager
     */
    fun ViewPager2.setAutoScroll(interval: Long) {
        isUserInputEnabled = false

        val handler = Handler(mainLooper)
        var scrollPosition = 0

        val runnable = object : Runnable {

            override fun run() {
                try {

                    /**
                     * Calculate "scroll position" with
                     * adapter pages count and current
                     * value of scrollPosition.
                     */
                    val count = adapter?.itemCount ?: 0
                    setCurrentItem(scrollPosition++ % count, true)

                    handler.postDelayed(this, interval)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                scrollPosition = position + 1
            }
        })

        handler.post(runnable)
    }
}