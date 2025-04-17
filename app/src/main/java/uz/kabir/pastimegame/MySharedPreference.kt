package uz.kabir.pastimegame

import android.content.Context

object MySharedPreference {
    private const val PREF_NAME = "audio_pref"

    fun saveStateAudio(context: Context, state: Boolean = false) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("audio_state", state)
        editor.apply()
    }

    fun getStateAudio(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("audio_state", false)
    }
}