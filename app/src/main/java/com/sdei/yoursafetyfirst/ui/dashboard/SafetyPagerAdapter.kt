package com.app.yoursafetyfirst.ui.dashboard

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.mediacodec.MediaCodecInfo
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import androidx.viewpager.widget.PagerAdapter
import coil.load
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.response.MultiImageModel
import com.app.yoursafetyfirst.utils.Constants
import java.util.Collections
import java.util.Objects


class SafetyPagerAdapter(
    val context: Context,
    private val viewPagerResponseArrayList: ArrayList<MultiImageModel>,
    val click: (Int) -> Unit
) : PagerAdapter() {

    private var imageView: ImageView? = null
    private var videoView1: PlayerView? = null
    private var currentPosition: Int = 0
    private var relativeLayout: LinearLayout? = null
    private var exoPlayer: ExoPlayer? = null

    override fun getCount(): Int {
        return viewPagerResponseArrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View =
            mLayoutInflater.inflate(R.layout.safety_video_adapter, container, false)


        imageView = itemView.findViewById(R.id.image)
        videoView1 = itemView.findViewById(R.id.video_view)
        relativeLayout = itemView.findViewById(R.id.parentRelative)

        currentPosition = position
        relativeLayout?.setOnClickListener {
            click(position)
        }

        if (viewPagerResponseArrayList[position].type == Constants.IMAGE) {
            videoView1?.visibility = View.GONE
            imageView?.visibility = View.VISIBLE
            imageView?.load(viewPagerResponseArrayList[position].imageUrl)
        } else {
            videoView1?.visibility = View.VISIBLE
            imageView?.visibility = View.GONE
            initPlayer(videoView1, viewPagerResponseArrayList[position].videoUrl)

            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e("aaa", error.errorCode.toString() + "   " + error.errorCodeName)
                    releaseExoPlayer()
                    initPlayer(videoView1, viewPagerResponseArrayList[position].videoUrl)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_READY -> {
                            Log.e("aaa", "STATE_READY")
                        }

                        Player.STATE_ENDED -> {
                            Log.e("aaa", "STATE_ENDED")

                        }

                        Player.STATE_BUFFERING, Player.STATE_IDLE -> {
                            Log.e("aaa", "STATE_BUFFERING")


                        }
                    }
                }
            })

        }



        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // on below line we are removing view
        container.removeView(`object` as LinearLayout)
        //releaseExoPlayer()

    }


    @UnstableApi
    private fun initPlayer(videoView1: PlayerView?, url: String) {
        val rf = DefaultRenderersFactory(context.applicationContext)
            .setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)
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

        val defaultTrackSelector = DefaultTrackSelector(context).apply {
            buildUponParameters()
                .setMaxVideoSize(360, 360)
                .setMaxVideoFrameRate(24)
                //                .setMaxVideoBitrate(1024)
                .setExceedVideoConstraintsIfNecessary(false)
                .setMaxAudioBitrate(0)
                .setMaxAudioChannelCount(0)
                .setExceedAudioConstraintsIfNecessary(false)
        }

        exoPlayer = ExoPlayer.Builder(context, rf)
            .setTrackSelector(defaultTrackSelector)
            .setLoadControl(loadControl)
            .build()
            .apply {
                val mediaItem =
                    MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                volume = 0.0f
                repeatMode = Player.REPEAT_MODE_OFF
                prepare()
                playWhenReady = true
            }

        videoView1?.player = exoPlayer
    }

    private fun releaseExoPlayer() {
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }


}