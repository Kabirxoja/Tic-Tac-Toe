package uz.kabir.pastimegame

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object AnimationButton {
    fun View.animateClick(duration: Long = 100) {
        // Scale X and Y down to 0.9
        val scaleXDown = ObjectAnimator.ofFloat(this, View.SCALE_X, 1.0f, 0.9f)
        val scaleYDown = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.0f, 0.9f)

        // Scale X and Y back to 1.0 (original size)
        val scaleXUp = ObjectAnimator.ofFloat(this, View.SCALE_X, 0.9f, 1.0f)
        val scaleYUp = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.9f, 1.0f)

        // Set duration for both animations
        scaleXDown.duration = duration
        scaleYDown.duration = duration
        scaleXUp.duration = duration
        scaleYUp.duration = duration

        // Create an AnimatorSet to play the scale down and then scale up animations in sequence
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleXDown).with(scaleYDown) // Scale down X and Y at the same time
        animatorSet.play(scaleXUp).with(scaleYUp).after(scaleXDown) // Scale up after scale down

        // Start the animation set
        animatorSet.start()
    }
}