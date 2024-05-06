package com.app.yoursafetyfirst.ui.dashboard

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
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
import com.app.yoursafetyfirst.response.Data
import com.app.yoursafetyfirst.response.MultiImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections


class DashboardAdapter(
    val context: Activity,
    private val dataList: ArrayList<Data>,
    val language: String,
    private val hashmapBeanList: HashMap<Int, ArrayList<MultiImageModel>>,
    private val viewLifecycleOwner: LifecycleOwner,
    val click: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val idlePositionObserver = MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null

        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dashbaord_adapter, parent, false)
                return ViewHolder0(view)
            }

            1 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.staggered_one, parent, false)
                return ViewHolder1(view)
            }

            2 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.staggered_two, parent, false)
                return ViewHolder2(view)
            }

            3 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.staggered_three, parent, false)
                return ViewHolder3(view)
            }

            4 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.staggered_four, parent, false)
                return ViewHolder4(view)
            }

            5 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.staggred_five, parent, false)
                return ViewHolder5(view)
            }

            6 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.staggered_six, parent, false)
                return ViewHolder6(view)
            }

            else -> {
                return null!!
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setDataOnAdapter(position, holder)

    }


    private fun setDataOnAdapter(position: Int, recyclerViewHolder: RecyclerView.ViewHolder) {

        when (hashmapBeanList[position]!!.size) {

            0 -> {
                val holder: DashboardAdapter.ViewHolder0 = recyclerViewHolder as ViewHolder0

                when (language) {
                    "en" -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }

                    "ja" -> {
                        holder.description.text = dataList[position].description.ja
                        holder.heading.text = dataList[position].title.ja
                    }

                    else -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }
                }

            }

            1 -> {
                val holder: DashboardAdapter.ViewHolder1 = recyclerViewHolder as ViewHolder1

                when (language) {
                    "en" -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }

                    "ja" -> {
                        holder.description.text = dataList[position].description.ja
                        holder.heading.text = dataList[position].title.ja
                    }

                    else -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }
                }

                holder.cardView.setOnClickListener {
                    click(position)
                }

                if (hashmapBeanList[position]?.get(0)?.type!!.equals("Video", true)) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView11.visibility = View.VISIBLE
                            holder.imageView11.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(0)?.videoUrl.toString(),
                                holder.videoView11
                            )
                        } else {
                            holder.videoView11.visibility = View.GONE
                            holder.imageView11.visibility = View.VISIBLE
                            holder.imageView11.load(hashmapBeanList[position]?.get(0)?.imageUrl)
                        }
                    }
                } else {
                    holder.videoView11.visibility = View.GONE
                    holder.imageView11.visibility = View.VISIBLE
                    holder.imageView11.load(hashmapBeanList[position]?.get(0)?.imageUrl)

                }
            }

            2 -> {
                val holder: DashboardAdapter.ViewHolder2 = recyclerViewHolder as ViewHolder2

                when (language) {
                    "en" -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }

                    "ja" -> {
                        holder.description.text = dataList[position].description.ja
                        holder.heading.text = dataList[position].title.ja
                    }

                    else -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }
                }

                holder.cardView.setOnClickListener {
                    click(position)
                }

                if (hashmapBeanList[position]?.get(0)?.type.equals("Video", true)) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView21.visibility = View.VISIBLE
                            holder.imageView21.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(0)?.videoUrl.toString(),
                                holder.videoView21
                            )

                        } else {
                            holder.videoView21.visibility = View.GONE
                            holder.imageView21.visibility = View.VISIBLE
                            holder.imageView21.load(hashmapBeanList[position]?.get(0)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView21.visibility = View.GONE
                    holder.imageView21.visibility = View.VISIBLE
                    holder.imageView21.load(hashmapBeanList[position]?.get(0)?.imageUrl)

                }

                if (hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    ) && (!hashmapBeanList[position]?.get(0)?.type.equals("Video", true))
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView22.visibility = View.VISIBLE
                            holder.imageView22.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(1)?.videoUrl.toString(),
                                holder.videoView22
                            )
                        } else {
                            holder.videoView21.visibility = View.GONE
                            holder.imageView22.visibility = View.VISIBLE
                            holder.imageView22.load(hashmapBeanList[position]?.get(1)?.imageUrl)
                        }
                    }
                } else {
                    holder.videoView21.visibility = View.GONE
                    holder.imageView22.visibility = View.VISIBLE
                    holder.imageView22.load(hashmapBeanList[position]?.get(1)?.imageUrl)
                }
            }

            3 -> {
                val holder: DashboardAdapter.ViewHolder3 = recyclerViewHolder as ViewHolder3

                when (language) {
                    "en" -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }

                    "ja" -> {
                        holder.description.text = dataList[position].description.ja
                        holder.heading.text = dataList[position].title.ja
                    }

                    else -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }
                }

                holder.cardView.setOnClickListener {
                    click(position)
                }

                if (hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView31.visibility = View.VISIBLE
                            holder.imageView31.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(0)?.videoUrl.toString(),
                                holder.videoView31
                            )
                        } else {
                            holder.videoView31.visibility = View.GONE
                            holder.imageView31.visibility = View.VISIBLE
                            holder.imageView31.load(hashmapBeanList[position]?.get(0)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView31.visibility = View.GONE
                    holder.imageView31.visibility = View.VISIBLE
                    holder.imageView31.load(hashmapBeanList[position]?.get(0)?.imageUrl)

                }

                if (hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    ) && (!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                            )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView32.visibility = View.VISIBLE
                            holder.imageView32.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(1)?.videoUrl.toString(),
                                holder.videoView32
                            )

                        } else {
                            holder.videoView32.visibility = View.GONE
                            holder.imageView32.visibility = View.VISIBLE
                            holder.imageView32.load(hashmapBeanList[position]?.get(1)?.imageUrl)
                        }
                    }
                } else {
                    holder.videoView32.visibility = View.GONE
                    holder.imageView32.visibility = View.VISIBLE
                    holder.imageView32.load(hashmapBeanList[position]?.get(1)?.imageUrl)
                }


                if (hashmapBeanList[position]?.get(2)?.type.equals(
                        "Video",
                        true
                    ) && ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )) &&
                            (!hashmapBeanList[position]?.get(1)?.type.equals(
                                "Video",
                                true
                            ))
                            )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView33.visibility = View.VISIBLE
                            holder.imageView33.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(2)?.videoUrl.toString(),
                                holder.videoView33
                            )
                        } else {
                            holder.videoView33.visibility = View.GONE
                            holder.imageView33.visibility = View.VISIBLE
                            holder.imageView33.load(hashmapBeanList[position]?.get(2)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView33.visibility = View.GONE
                    holder.imageView33.visibility = View.VISIBLE
                    holder.imageView33.load(hashmapBeanList[position]?.get(2)?.imageUrl)


                }


            }

            4 -> {
                val holder: DashboardAdapter.ViewHolder4 = recyclerViewHolder as ViewHolder4

                holder.cardView.setOnClickListener {
                    click(position)
                }

                when (language) {
                    "en" -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }

                    "ja" -> {
                        holder.description.text = dataList[position].description.ja
                        holder.heading.text = dataList[position].title.ja
                    }

                    else -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }
                }


                if (hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView41.visibility = View.VISIBLE
                            holder.imageView41.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(0)?.videoUrl.toString(),
                                holder.videoView41
                            )
                        } else {
                            holder.videoView41.visibility = View.GONE
                            holder.imageView41.visibility = View.VISIBLE
                            holder.imageView41.load(hashmapBeanList[position]?.get(0)?.imageUrl)

                        }

                    }
                } else {
                    holder.videoView41.visibility = View.GONE
                    holder.imageView41.visibility = View.VISIBLE
                    holder.imageView41.load(hashmapBeanList[position]?.get(0)?.imageUrl)

                }


                if (hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    ) && (!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    ))
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView42.visibility = View.VISIBLE
                            holder.imageView42.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(1)?.videoUrl.toString(),
                                holder.videoView42
                            )

                        } else {
                            holder.videoView42.visibility = View.GONE
                            holder.imageView42.visibility = View.VISIBLE
                            holder.imageView42.load(hashmapBeanList[position]?.get(1)?.imageUrl)
                        }


                    }
                } else {
                    holder.videoView42.visibility = View.GONE
                    holder.imageView42.visibility = View.VISIBLE
                    holder.imageView42.load(hashmapBeanList[position]?.get(1)?.imageUrl)
                }

                if (hashmapBeanList[position]?.get(2)?.type.equals(
                        "Video",
                        true
                    ) && ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                            ) && (!hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )))
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView43.visibility = View.VISIBLE
                            holder.imageView43.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(2)?.videoUrl.toString(),
                                holder.videoView43
                            )
                        } else {
                            holder.videoView43.visibility = View.GONE
                            holder.imageView43.visibility = View.VISIBLE
                            holder.imageView43.load(hashmapBeanList[position]?.get(2)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView43.visibility = View.GONE
                    holder.imageView43.visibility = View.VISIBLE
                    holder.imageView43.load(hashmapBeanList[position]?.get(2)?.imageUrl)

                }


                if (hashmapBeanList[position]?.get(3)?.type.equals(
                        "Video",
                        true
                    )
                    && ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                            ) && (!hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )
                            ) && (!hashmapBeanList[position]?.get(2)?.type.equals(
                        "Video",
                        true
                    )))
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView44.visibility = View.VISIBLE
                            holder.imageView44.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(3)?.videoUrl.toString(),
                                holder.videoView44
                            )
                        } else {
                            holder.videoView44.visibility = View.GONE
                            holder.imageView44.visibility = View.VISIBLE
                            holder.imageView44.load(hashmapBeanList[position]?.get(3)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView44.visibility = View.GONE
                    holder.imageView44.visibility = View.VISIBLE
                    holder.imageView44.load(hashmapBeanList[position]?.get(3)?.imageUrl)

                }


            }

            5 -> {
                val holder: DashboardAdapter.ViewHolder5 = recyclerViewHolder as ViewHolder5

                when (language) {
                    "en" -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }

                    "ja" -> {
                        holder.description.text = dataList[position].description.ja
                        holder.heading.text = dataList[position].title.ja
                    }

                    else -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }
                }

                holder.cardView.setOnClickListener {
                    click(position)
                }

                if (hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView51.visibility = View.VISIBLE
                            holder.imageView51.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(0)?.videoUrl.toString(),
                                holder.videoView51
                            )

                        } else {
                            holder.videoView51.visibility = View.GONE
                            holder.imageView51.visibility = View.VISIBLE
                            holder.imageView51.load(
                                hashmapBeanList[position]?.get(0)?.imageUrl
                            )
                        }
                    }
                } else {
                    holder.videoView51.visibility = View.GONE
                    holder.imageView51.visibility = View.VISIBLE
                    holder.imageView51.load(
                        hashmapBeanList[position]?.get(0)?.imageUrl
                    )
                }


                if (hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    ) && (!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    ))
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView52.visibility = View.VISIBLE
                            holder.imageView52.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(1)?.videoUrl.toString(),
                                holder.videoView52
                            )
                        } else {
                            holder.videoView52.visibility = View.GONE
                            holder.imageView52.visibility = View.VISIBLE
                            holder.imageView52.load(hashmapBeanList[position]?.get(1)?.imageUrl)
                        }
                    }
                } else {
                    holder.videoView52.visibility = View.GONE
                    holder.imageView52.visibility = View.VISIBLE
                    holder.imageView52.load(hashmapBeanList[position]?.get(1)?.imageUrl)
                }


                if (hashmapBeanList[position]?.get(2)?.type.equals("Video", true) &&
                    ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                            ) && (!hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView53.visibility = View.VISIBLE
                            holder.imageView53.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(2)?.videoUrl.toString(),
                                holder.videoView53
                            )
                        } else {
                            holder.videoView53.visibility = View.GONE
                            holder.imageView53.visibility = View.VISIBLE
                            holder.imageView53.load(hashmapBeanList[position]?.get(2)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView53.visibility = View.GONE
                    holder.imageView53.visibility = View.VISIBLE
                    holder.imageView53.load(hashmapBeanList[position]?.get(2)?.imageUrl)

                }



                if (hashmapBeanList[position]?.get(3)?.type.equals(
                        "Video",
                        true
                    )
                    && ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                            ) && (!hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            && (!hashmapBeanList[position]?.get(2)?.type.equals(
                        "Video",
                        true
                    )))
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView54.visibility = View.VISIBLE
                            holder.imageView54.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(3)?.videoUrl.toString(),
                                holder.videoView54
                            )

                        } else {
                            holder.videoView54.visibility = View.GONE
                            holder.imageView54.visibility = View.VISIBLE
                            holder.imageView54.load(hashmapBeanList[position]?.get(3)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView54.visibility = View.GONE
                    holder.imageView54.visibility = View.VISIBLE
                    holder.imageView54.load(hashmapBeanList[position]?.get(3)?.imageUrl)

                }



                if (hashmapBeanList[position]?.get(4)?.type.equals(
                        "Video",
                        true
                    ) && ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                            ) && (!hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            && (!hashmapBeanList[position]?.get(2)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            && (!hashmapBeanList[position]?.get(3)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView55.visibility = View.VISIBLE
                            holder.imageView55.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(4)?.videoUrl.toString(),
                                holder.videoView55
                            )
                        } else {
                            holder.videoView55.visibility = View.GONE
                            holder.imageView55.visibility = View.VISIBLE
                            holder.imageView55.load(hashmapBeanList[position]?.get(4)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView55.visibility = View.GONE
                    holder.imageView55.visibility = View.VISIBLE
                    holder.imageView55.load(hashmapBeanList[position]?.get(4)?.imageUrl)

                }
            }


            else -> {
                val holder: DashboardAdapter.ViewHolder6 = recyclerViewHolder as ViewHolder6

                when (language) {
                    "en" -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }

                    "ja" -> {
                        holder.description.text = dataList[position].description.ja
                        holder.heading.text = dataList[position].title.ja
                    }

                    else -> {
                        holder.description.text = dataList[position].description.en
                        holder.heading.text = dataList[position].title.en
                    }
                }
                holder.cardView.setOnClickListener {
                    click(position)
                }

                holder.remainingText.text = "+${(hashmapBeanList[position]?.size?.minus(5))}"


                if (hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView61.visibility = View.VISIBLE
                            holder.imageView61.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(0)?.videoUrl.toString(),
                                holder.videoView61
                            )

                        } else {
                            holder.videoView61.visibility = View.GONE
                            holder.imageView61.visibility = View.VISIBLE
                            holder.imageView61.load(hashmapBeanList[position]?.get(0)?.imageUrl)
                        }

                    }
                } else {
                    holder.videoView61.visibility = View.GONE
                    holder.imageView61.visibility = View.VISIBLE
                    holder.imageView61.load(hashmapBeanList[position]?.get(0)?.imageUrl)
                }


                if ((hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )) && (!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    ))
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView62.visibility = View.VISIBLE
                            holder.imageView62.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(1)?.videoUrl.toString(),
                                holder.videoView62
                            )

                        } else {
                            holder.videoView62.visibility = View.GONE
                            holder.imageView62.visibility = View.VISIBLE
                            holder.imageView62.load(hashmapBeanList[position]?.get(1)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView62.visibility = View.GONE
                    holder.imageView62.visibility = View.VISIBLE
                    holder.imageView62.load(hashmapBeanList[position]?.get(1)?.imageUrl)

                }



                if ((hashmapBeanList[position]?.get(2)?.type.equals(
                        "Video",
                        true
                    )) && ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    ))) && ((!hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )))
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView63.visibility = View.VISIBLE
                            holder.imageView63.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(2)?.videoUrl.toString(),
                                holder.videoView63
                            )

                        } else {
                            holder.videoView63.visibility = View.GONE
                            holder.imageView63.visibility = View.VISIBLE
                            holder.imageView63.load(hashmapBeanList[position]?.get(2)?.imageUrl)
                        }
                    }

                } else {
                    holder.videoView63.visibility = View.GONE
                    holder.imageView63.visibility = View.VISIBLE
                    holder.imageView63.load(hashmapBeanList[position]?.get(2)?.imageUrl)
                }


                if (hashmapBeanList[position]?.get(3)?.type.equals(
                        "Video",
                        true
                    ) && ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                            ) && (!hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            && (!hashmapBeanList[position]?.get(2)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView64.visibility = View.VISIBLE
                            holder.imageView64.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(4)?.videoUrl.toString(),
                                holder.videoView65
                            )
                        } else {
                            holder.videoView64.visibility = View.GONE
                            holder.imageView64.visibility = View.VISIBLE
                            holder.imageView64.load(hashmapBeanList[position]?.get(3)?.imageUrl)
                        }
                    }
                } else {
                    holder.videoView64.visibility = View.GONE
                    holder.imageView64.visibility = View.VISIBLE
                    holder.imageView64.load(hashmapBeanList[position]?.get(3)?.imageUrl)
                }


                if (hashmapBeanList[position]?.get(4)?.type.equals(
                        "Video",
                        true
                    ) && ((!hashmapBeanList[position]?.get(0)?.type.equals(
                        "Video",
                        true
                    )
                            ) && (!hashmapBeanList[position]?.get(1)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            && (!hashmapBeanList[position]?.get(2)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            && (!hashmapBeanList[position]?.get(3)?.type.equals(
                        "Video",
                        true
                    )
                            )
                            )
                ) {
                    idlePositionObserver.observe(viewLifecycleOwner) {
                        if (it == position) {
                            holder.videoView65.visibility = View.VISIBLE
                            holder.imageView65.visibility = View.GONE
                            setVideo(
                                hashmapBeanList[position]?.get(4)?.videoUrl.toString(),
                                holder.videoView65
                            )


                        } else {
                            holder.videoView65.visibility = View.GONE
                            holder.imageView65.visibility = View.VISIBLE
                            holder.imageView65.load(hashmapBeanList[position]?.get(4)?.imageUrl)

                        }
                    }
                } else {
                    holder.videoView65.visibility = View.GONE
                    holder.imageView65.visibility = View.VISIBLE
                    holder.imageView65.load(hashmapBeanList[position]?.get(4)?.imageUrl)

                }
            }

        }

    }

    @UnstableApi
    private fun setVideo(url: String, videoView: PlayerView) {
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

        val exoPlayer = ExoPlayer.Builder(context, rf)
            .setTrackSelector(defaultTrackSelector)
            .setLoadControl(loadControl)
            .build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                volume = 0.0f
                this.repeatMode = Player.REPEAT_MODE_ONE
                prepare()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    prepare()
                    playWhenReady = true
                }
            }

        videoView.player = exoPlayer


    }


    override fun getItemViewType(position: Int): Int {
        return hashmapBeanList[position]?.size!!
    }


    inner class ViewHolder0(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val heading: TextView = itemView.findViewById(R.id.heading)
        val description: TextView = itemView.findViewById(R.id.description)

    }

    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val heading: TextView = itemView.findViewById(R.id.heading)
        val description: TextView = itemView.findViewById(R.id.description)
        val imageView11: ImageView = itemView.findViewById(R.id.image_view11)
        val videoView11: PlayerView = itemView.findViewById(R.id.play_pause11)

    }

    inner class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val heading: TextView = itemView.findViewById(R.id.heading)
        val description: TextView = itemView.findViewById(R.id.description)
        val imageView21: ImageView = itemView.findViewById(R.id.img_21)
        val imageView22: ImageView = itemView.findViewById(R.id.img_22)
        val videoView21: PlayerView = itemView.findViewById(R.id.play_pause21)
        val videoView22: PlayerView = itemView.findViewById(R.id.play_pause22)

    }

    inner class ViewHolder3(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val heading: TextView = itemView.findViewById(R.id.heading)
        val description: TextView = itemView.findViewById(R.id.description)
        val imageView31: ImageView = itemView.findViewById(R.id.img_31)
        val imageView32: ImageView = itemView.findViewById(R.id.img_32)
        val imageView33: ImageView = itemView.findViewById(R.id.img_three33)
        val videoView31: PlayerView = itemView.findViewById(R.id.play_pause31)
        val videoView32: PlayerView = itemView.findViewById(R.id.play_pause32)
        val videoView33: PlayerView = itemView.findViewById(R.id.play_pause33)

    }

    inner class ViewHolder4(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val heading: TextView = itemView.findViewById(R.id.heading)
        val description: TextView = itemView.findViewById(R.id.description)
        val imageView41: ImageView = itemView.findViewById(R.id.img_one41)
        val imageView42: ImageView = itemView.findViewById(R.id.img_two42)
        val imageView43: ImageView = itemView.findViewById(R.id.img_three43)
        val imageView44: ImageView = itemView.findViewById(R.id.img_four44)
        val videoView41: PlayerView = itemView.findViewById(R.id.play_pause41)
        val videoView42: PlayerView = itemView.findViewById(R.id.play_pause42)
        val videoView43: PlayerView = itemView.findViewById(R.id.play_pause43)
        val videoView44: PlayerView = itemView.findViewById(R.id.play_pause44)

    }

    inner class ViewHolder5(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val heading: TextView = itemView.findViewById(R.id.heading)
        val description: TextView = itemView.findViewById(R.id.description)
        val imageView51: ImageView = itemView.findViewById(R.id.img_one51)
        val imageView52: ImageView = itemView.findViewById(R.id.img_two52)
        val imageView53: ImageView = itemView.findViewById(R.id.img_three53)
        val imageView54: ImageView = itemView.findViewById(R.id.img_four54)
        val imageView55: ImageView = itemView.findViewById(R.id.img_five55)
        val videoView51: PlayerView = itemView.findViewById(R.id.play_pause51)
        val videoView52: PlayerView = itemView.findViewById(R.id.play_pause52)
        val videoView53: PlayerView = itemView.findViewById(R.id.play_pause53)
        val videoView54: PlayerView = itemView.findViewById(R.id.play_pause54)
        val videoView55: PlayerView = itemView.findViewById(R.id.play_pause55)

    }

    inner class ViewHolder6(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val heading: TextView = itemView.findViewById(R.id.heading)
        val description: TextView = itemView.findViewById(R.id.description)
        val remainingText: TextView = itemView.findViewById(R.id.remaining_text)
        val imageView61: ImageView = itemView.findViewById(R.id.img_one61)
        val imageView62: ImageView = itemView.findViewById(R.id.img_two62)
        val imageView63: ImageView = itemView.findViewById(R.id.img_three63)
        val imageView64: ImageView = itemView.findViewById(R.id.img_four64)
        val imageView65: ImageView = itemView.findViewById(R.id.img_five65)
        val videoView61: PlayerView = itemView.findViewById(R.id.play_pause61)
        val videoView62: PlayerView = itemView.findViewById(R.id.play_pause62)
        val videoView63: PlayerView = itemView.findViewById(R.id.play_pause63)
        val videoView64: PlayerView = itemView.findViewById(R.id.play_pause64)
        val videoView65: PlayerView = itemView.findViewById(R.id.play_pause65)

    }


}