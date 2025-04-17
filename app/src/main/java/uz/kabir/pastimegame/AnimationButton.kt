package uz.kabir.pastimegame

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object AnimationButton {
    fun View.animateClick(duration: Long = 100) {
        val scaleXDown = ObjectAnimator.ofFloat(this, View.SCALE_X, 1.0f, 0.9f)
        val scaleYDown = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.0f, 0.9f)

        val scaleXUp = ObjectAnimator.ofFloat(this, View.SCALE_X, 0.9f, 1.0f)
        val scaleYUp = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.9f, 1.0f)

        scaleXDown.duration = duration
        scaleYDown.duration = duration
        scaleXUp.duration = duration
        scaleYUp.duration = duration

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleXDown).with(scaleYDown) // Scale down X and Y at the same time
        animatorSet.play(scaleXUp).with(scaleYUp).after(scaleXDown) // Scale up after scale down

        animatorSet.start()
    }
}