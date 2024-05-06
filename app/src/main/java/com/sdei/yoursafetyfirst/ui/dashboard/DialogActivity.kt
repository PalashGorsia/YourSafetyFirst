package com.app.yoursafetyfirst.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Patterns
import android.view.View
import android.webkit.URLUtil
import android.widget.AbsListView
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.yoursafetyfirst.BaseActivity
import com.app.yoursafetyfirst.R
import com.app.yoursafetyfirst.databinding.ActivityDialogBinding
import com.app.yoursafetyfirst.response.Description
import com.app.yoursafetyfirst.response.MultiImageModel
import com.app.yoursafetyfirst.response.Title
import com.app.yoursafetyfirst.utils.ShowSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import java.util.regex.Pattern


@AndroidEntryPoint
class DialogActivity : BaseActivity<ActivityDialogBinding>() {

    private var multiImageModels: ArrayList<MultiImageModel>? = null
    private var title: Title? = null
    private var descriptions: Description? = null
    private var language: String? = null
    private var url: String? = null
    var imageViewAdapter: ImageViewAdapter? = null

    companion object {
        @JvmStatic
        fun start(
            context: Context,
            multiImageModels: ArrayList<MultiImageModel>?,
            title: Title?,
            description: Description?,
            language: String, url: String
        ) {
            val starter = Intent(context, DialogActivity::class.java)
            starter.putParcelableArrayListExtra("images", multiImageModels)
            starter.putExtra("title", title)
            starter.putExtra("description", description)
            starter.putExtra("language", language)
            starter.putExtra("url", url)
            context.startActivity(starter)
        }
    }

    override fun onCreate() {
        language = intent.getStringExtra("language")
        url = intent.getStringExtra("url")
        title = intent.getParcelableExtra("title")
        descriptions = intent.getParcelableExtra("description")
        multiImageModels = intent.getParcelableArrayListExtra("images")

        if (url!!.isNotEmpty()) {
            binding.url.visibility = View.VISIBLE
            binding.url.text = url
        } else {
            binding.url.visibility = View.GONE
        }

        binding.url.setOnClickListener {
            if (checkURL(url!!)) {
                val urlIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(urlIntent)
            } else {
                ShowSnackBar.showBarString(
                    binding.frameLayout,
                    resources.getString(R.string.url_not_vaild),
                    this@DialogActivity,
                    language!!
                )
            }
        }

        when (language) {
            "en" -> {
                binding.description.text = descriptions?.en
                binding.title.text = title?.en
            }

            "ja" -> {
                binding.description.text = descriptions?.ja
                binding.title.text = title?.ja
            }

            else -> {
                binding.description.text = descriptions?.en
                binding.title.text = title?.en
            }
        }

        binding.description.movementMethod = ScrollingMovementMethod()

        imageViewAdapter =
            ImageViewAdapter(this@DialogActivity, multiImageModels!!, this@DialogActivity) {}
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = imageViewAdapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)


        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                        if (recyclerView.size > 0) {
                            val view = recyclerView[0]
                            binding.recyclerView.getChildViewHolder(view)
                            val linearLayoutManager =
                                recyclerView.layoutManager as LinearLayoutManager?
                            val holder: ImageViewAdapter.ViewHolder? =
                                recyclerView.findViewHolderForLayoutPosition(linearLayoutManager?.findLastVisibleItemPosition()!!) as ImageViewAdapter.ViewHolder?
                            imageViewAdapter!!.releaseExoPlayer()
                            imageViewAdapter!!.initializePlayer(
                                holder?.videoView!!,
                                linearLayoutManager.findLastVisibleItemPosition()
                            )
                        }

                    }
                }
            }
        })



        binding.close.setOnClickListener {
            onBackPressed()
        }
    }


    override fun getViewBinding(): ActivityDialogBinding =
        ActivityDialogBinding.inflate(layoutInflater)

    override fun observer() {
    }

    override fun onPause() {
        super.onPause()
        imageViewAdapter?.releaseExoPlayer()
    }

    fun checkURL(input: String): Boolean {
        if (TextUtils.isEmpty(input)) {
            return false
        }
        val URL_PATTERN: Pattern = Patterns.WEB_URL
        var isURL: Boolean = URL_PATTERN.matcher(input).matches()
        if (!isURL) {
            val urlString = input + ""
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    URL(urlString)
                    isURL = true
                } catch (e: Exception) {
                }
            }
        }
        return isURL
    }
}