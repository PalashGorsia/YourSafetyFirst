package com.app.yoursafetyfirst.ui.language

import android.content.res.Resources
import android.os.Build.VERSION.SDK_INT
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.DriverSafetyApp
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivitySplash2Binding
import com.app.yoursafetyfirst.ui.MainActivity
import com.app.yoursafetyfirst.utils.LocalData
import com.app.yoursafetyfirst.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplash2Binding>() {

    private var exoPlayer: ExoPlayer? = null

    @UnstableApi
    override fun onCreate() {

        //initializePlayer()

        val imageLoader = ImageLoader.Builder(this@SplashActivity)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        binding.videoView.load(R.drawable.splash, imageLoader)

        lifecycleScope.launch {
            LocalData(this@SplashActivity).language.first().let {
                DriverSafetyApp.selectedLanguage = it
            }
        }


       /* binding.relative2.apply {
            alpha = 0f

            animate()
                .alpha(1f)
                .setDuration(500L)
                .setListener(null)


            *//*binding.relative1.animate()
                .alpha(0f).duration = 2000L*//*
        }*/




        lifecycleScope.launch {
            delay(2500L)
            LocalData(this@SplashActivity).token.first().let {
                if (it.isNotEmpty()) {
                    MainActivity.start(this@SplashActivity)
                } else {
                    LocaleHelper.setLocale(
                        this@SplashActivity,
                        Resources.getSystem().configuration.locales.get(0).language
                    )
                    DriverSafetyApp.selectedLanguage =
                        Resources.getSystem().configuration.locales.get(0).language
                    LanguageActivity.start(this@SplashActivity)
                }
            }
        }

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                releaseExoPlayer()
                initializePlayer()
            }
        })
    }

    override fun getViewBinding() = ActivitySplash2Binding.inflate(layoutInflater)

    override fun observer() {
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseExoPlayer()
    }

    private fun releaseExoPlayer() {
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }

    @UnstableApi
    private fun initializePlayer() {
        /*val rf = DefaultRenderersFactory(this)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            .setMediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
                var decoderInfos: List<MediaCodecInfo> =
                    MediaCodecSelector.DEFAULT
                        .getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder)
                if (MimeTypes.VIDEO_H264 == mimeType) {
                    // copy the list because MediaCodecSelector.DEFAULT returns an unmodifiable list
                    decoderInfos = ArrayList<MediaCodecInfo>(decoderInfos)
                    Collections.reverse(decoderInfos)
                }
                decoderInfos
            }

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024)
            .build()

        val defaultTrackSelector = DefaultTrackSelector(this).apply {
            buildUponParameters()
                .setMaxVideoSize(360, 360)
                .setMaxVideoFrameRate(24)
                //                .setMaxVideoBitrate(1024)
                .setExceedVideoConstraintsIfNecessary(false)
                .setMaxAudioBitrate(0)
                .setMaxAudioChannelCount(0)
                .setExceedAudioConstraintsIfNecessary(false)
        }*/

        /* val defaultTrackSelector = DefaultTrackSelector(this).apply {
             buildUponParameters()
                 .setMaxVideoSize(360, 360)
                 .setMaxVideoFrameRate(24)
                 //                .setMaxVideoBitrate(1024)
                 .setExceedVideoConstraintsIfNecessary(false)
                 .setMaxAudioBitrate(0)
                 .setMaxAudioChannelCount(0)
                 .setExceedAudioConstraintsIfNecessary(false)
         }*/

        exoPlayer = ExoPlayer.Builder(this)
            //.setTrackSelector(defaultTrackSelector)
            //.setLoadControl(loadControl)
            .build().apply {
                val videoUri = RawResourceDataSource.buildRawResourceUri(R.raw.splash)
                //binding.videoView.player = this
                val mediaItem = MediaItem.fromUri(videoUri)
                setMediaItem(mediaItem)
                volume = 0.0f
                prepare()
                playWhenReady = true
            }
    }


}