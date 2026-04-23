package com.dragonest.artifacts.goo.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.dragonest.artifacts.goo.R
import com.dragonest.artifacts.goo.data.PreferencesManager

/**
 * Abstraction over the audio manager so previews and tests can run without a real
 * MediaPlayer / SoundPool. All methods have no-op defaults; the real implementation
 * ([GameAudioManager]) overrides them.
 */
interface GameAudioController {
    fun onActivityStart() {}
    fun onActivityStop() {}
    fun setMusicEnabled(enabled: Boolean) {}
    fun setSoundEnabled(enabled: Boolean) {}
    fun playSpin() {}
    fun playWin() {}
    fun playLose() {}
    fun release() {}
}

/** Shared no-op controller used as the default for [LocalAudioManager] (e.g. in Compose previews). */
object NoopAudioController : GameAudioController

/**
 * Lightweight audio manager. One instance is owned by the activity.
 * - [music] (game_music.mp3) is a looped MediaPlayer; only played while the hosting activity is
 *   at least STARTED and music is enabled in settings.
 * - Short sfx go through SoundPool so they can overlap with music.
 */
class GameAudioManager(context: Context) : GameAudioController {

    private val appContext = context.applicationContext
    private val prefs = PreferencesManager.get(appContext)

    private var musicPlayer: MediaPlayer? = null
    private var musicPrepared = false
    private var musicShouldPlay = false // set from lifecycle ("we're in foreground")

    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(4).build()
    private val sfxIds = mutableMapOf<Int, Int>() // rawResId -> soundId
    private var sfxLoaded = false

    init {
        preloadSfx()
    }

    private fun preloadSfx() {
        val res = listOf(R.raw.slot_rounded, R.raw.level_win, R.raw.level_lose)
        res.forEach { raw ->
            val id = soundPool.load(appContext, raw, 1)
            sfxIds[raw] = id
        }
        soundPool.setOnLoadCompleteListener { _, _, _ -> sfxLoaded = true }
    }

    // ---------- Music ----------

    /** Called by the activity when it becomes visible (ON_START). */
    override fun onActivityStart() {
        musicShouldPlay = true
        if (prefs.musicEnabled) startMusic()
    }

    /** Called by the activity when it leaves the foreground (ON_STOP). */
    override fun onActivityStop() {
        musicShouldPlay = false
        pauseMusic()
    }

    /** Called from Settings when the music toggle changes. */
    override fun setMusicEnabled(enabled: Boolean) {
        prefs.musicEnabled = enabled
        if (enabled && musicShouldPlay) startMusic() else pauseMusic()
    }

    private fun startMusic() {
        val player = musicPlayer ?: createMusicPlayer().also { musicPlayer = it }
        if (!musicPrepared) return
        if (!player.isPlaying) {
            runCatching { player.start() }
        }
    }

    private fun pauseMusic() {
        musicPlayer?.let { p ->
            if (p.isPlaying) runCatching { p.pause() }
        }
    }

    private fun createMusicPlayer(): MediaPlayer {
        return MediaPlayer.create(appContext, R.raw.game_music).apply {
            isLooping = true
            setVolume(0.55f, 0.55f)
            musicPrepared = true
        }
    }

    // ---------- SFX ----------

    override fun setSoundEnabled(enabled: Boolean) {
        prefs.soundEnabled = enabled
    }

    override fun playSpin() = playSfx(R.raw.slot_rounded, volume = 0.85f)
    override fun playWin() = playSfx(R.raw.level_win, volume = 1f)
    override fun playLose() = playSfx(R.raw.level_lose, volume = 1f)

    private fun playSfx(@RawRes raw: Int, volume: Float = 1f) {
        if (!prefs.soundEnabled) return
        val id = sfxIds[raw] ?: return
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    // ---------- Lifecycle ----------

    override fun release() {
        musicPlayer?.let {
            runCatching { if (it.isPlaying) it.stop() }
            runCatching { it.release() }
        }
        musicPlayer = null
        musicPrepared = false
        runCatching { soundPool.release() }
    }
}

val LocalAudioManager = staticCompositionLocalOf<GameAudioController> { NoopAudioController }

/**
 * Binds the audio manager to the current lifecycle so music follows ON_START / ON_STOP.
 * Use from MainActivity's composable tree.
 */
@Composable
fun BindAudioToLifecycle(audio: GameAudioController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> audio.onActivityStart()
                Lifecycle.Event.ON_STOP -> audio.onActivityStop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
