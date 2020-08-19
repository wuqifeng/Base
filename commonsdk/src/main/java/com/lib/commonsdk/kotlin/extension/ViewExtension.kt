package com.lib.commonsdk.kotlin.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.lib.commonsdk.constants.longAnimTime
import org.simple.eventbus.EventBus


/**
 * 对 Android Framework 的 View 做的一些扩展。
 */
// ------------------------------

/**
 * 把一个View设置成VISIBLE
 */
fun <T : View> T.visible() = apply {
    visibility = View.VISIBLE
}

/**
 * 把一个View设置成INVISIBLE
 */
fun <T : View> T.invisible() = apply {
    visibility = View.INVISIBLE
}

/**
 * 把一个View设置成GONE
 */
fun <T : View> T.gone() = apply {
    visibility = View.GONE
}

/**
 * 切换一个View的可见状态
 * 如果当前可见, 把它设成不可见, 反之设成可见.
 */
fun <T : View> T.toggleVisibility() {
    if (visibility == View.VISIBLE) {
        invisible()
    } else {
        visible()
    }
}

/**
 * 渐渐的显示出这个View,而不是突然出现
 */
fun <T : View> T.showSmoothly() = apply {
    val alphaAnimator = ObjectAnimator.ofFloat(this, View.ALPHA, 1.0F)
    alphaAnimator.addUpdateListener {
        this.alpha = it.animatedValue as Float
    }

    alphaAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            this@apply.visible()
        }
    })
    alphaAnimator.duration = longAnimTime
    alphaAnimator.start()
}

/**
 * 渐渐的隐藏这个View,而不是突然消失
 */
fun <T : View> T.hideSmoothly() = apply {
    val alphaAnimator = ObjectAnimator.ofFloat(this, View.ALPHA, 0.0F)
    alphaAnimator.addUpdateListener {
        this.alpha = it.animatedValue as Float
    }

    alphaAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            this@apply.gone()
        }
    })
    alphaAnimator.duration = longAnimTime
    alphaAnimator.start()
}

/**
 * View 被 attached 到 window 的时候做一些事情
 */
fun <V : View> V.doWhenAttachedToWindow(detached: ((View) -> Unit)? = null, attached: (View) -> Unit) {
    if (isAttachedToWindow) {
        attached(this)
    }

    if (!isAttachedToWindow || detached != null) {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                attached(view)
            }

            override fun onViewDetachedFromWindow(view: View) {
                detached?.let { it(view) }
            }
        })
    }
}

/**
 * 设置让一个View可以监听EventBus事件
 */
var <V : View> V.eventAware: Boolean
    get() = throw UnsupportedOperationException("get for eventAware is not supported.")
    set(v) {
        require(v)
        doWhenAttachedToWindow(
                attached = { EventBus.getDefault().register(it) },
                detached = { EventBus.getDefault().unregister(it) }
        )
    }

var <T : Drawable> T.tint: Int
    get() = throw java.lang.UnsupportedOperationException("cannot get tint for Drawable")
    set(v) = DrawableCompat.setTint(this, v)

/**
 * 以Pixel为单位设置TextView的字号。
 */
var <V : TextView> V.textSizePx: Float
    get() = throw UnsupportedOperationException("cannot get textSizePx for TextView")
    set(v) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, v)
    }

