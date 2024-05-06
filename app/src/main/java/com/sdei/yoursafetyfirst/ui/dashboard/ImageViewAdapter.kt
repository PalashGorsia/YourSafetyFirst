package com.app.yoursafetyfirst.ui.dashboard

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.mediacodec.MediaCodecInfo
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.response.MultiImageModel
import com.app.yoursafetyfirst.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections


class ImageViewAdapter(
    val context: Context,
    private val beanList: ArrayList<MultiImageModel>,
    val activity: Activity?,
    val onPositionClick: (Int) -> Unit
) : RecyclerView.Adapter<ImageViewAdapter.ViewHolder>() {

    private var exoPlayer: Player? = null


    @UnstableApi
    fun initializePlayer(videoView: PlayerView, position: Int) {
        val rf = DefaultRenderersFactory(context.applicationContext)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            .setMediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
                var decoderInfos: List<MediaCodecInfo> =
                    MediaCodecSelector.DEFAULT
                        .getDecoderInfos(
                            mimeType,
                            requiresSecureDecoder,
                            requiresTunnelingDecoder
                        )
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
            .build().apply {
                val mediaItem = MediaItem.fromUri(beanList[position].videoUrl)
                setMediaItem(mediaItem)
                volume = 1.0f
                prepare()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    prepare()
                    playWhenReady = true
                }
            }
        videoView.player = exoPlayer
    }

    fun releaseExoPlayer() {
        //videoView.player = null
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCollection: ImageView = itemView.findViewById(R.id.imageCollectionView)
        val videoView: PlayerView = itemView.findViewById(R.id.video_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val contactView =
            LayoutInflater.from(parent.context).inflate(R.layout.image_adapter, parent, false)
        return ViewHolder(contactView)
    }

    override fun getItemCount(): Int {
        return beanList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (beanList[position].type.equals(Constants.VIDEO, true)) {
            holder.imageCollection.visibility = View.GONE
            holder.videoView.visibility = View.VISIBLE

            if (beanList.size == 1) {
                initializePlayer(holder.videoView, position)
            }

            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e(
                        "error exoplayer",
                        error.errorCode.toString() + "   " + error.errorCodeName
                    )
                    releaseExoPlayer()
                    initializePlayer(holder.videoView, position)
                }
            })
        } else {
            holder.imageCollection.visibility = View.VISIBLE
            holder.videoView.visibility = View.GONE
            holder.imageCollection.load(beanList[position].imageUrl)
        }
    }
}