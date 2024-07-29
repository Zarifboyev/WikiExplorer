package com.z99a.zarifboyevjavohir.vikipediya.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.z99a.zarifboyevjavohir.R
import com.z99a.zarifboyevjavohir.databinding.ItemYoutubeVideoCardBinding
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.PlaylistItem
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.Thumbnails
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.VideoDetails
import com.z99a.zarifboyevjavohir.vikipediya.utils.DateFormatter
import com.z99a.zarifboyevjavohir.vikipediya.utils.NumberFormatter
import timber.log.Timber

class VideoAdapter(
    private val onVideoClick: (String) -> Unit,
    private val onChannelClick: (String) -> Unit
) : ListAdapter<PlaylistItem, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    private val videoDetailsMap = mutableMapOf<String, VideoDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        Timber.d("onCreateViewHolder called for viewType: $viewType")
        return VideoViewHolder(
            ItemYoutubeVideoCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = getItem(position)
        Timber.d("onBindViewHolder called for position: $position, videoId: ${item?.contentDetails?.videoId}")
        item?.let {
            holder.bind(it, videoDetailsMap[it.contentDetails.videoId])
        }
    }

    fun updateVideoStatistics(videoDetailsMap: Map<String, VideoDetails>) {
        Timber.d("updateVideoStatistics called with map size: ${videoDetailsMap.size}")
        this.videoDetailsMap.putAll(videoDetailsMap)
        notifyDataSetChanged()
    }

    inner class VideoViewHolder(
        private val binding: ItemYoutubeVideoCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlistItem: PlaylistItem, videoDetails: VideoDetails?) {
            Timber.d("Binding videoId: ${playlistItem.contentDetails.videoId}, channelId: ${playlistItem.snippet.channelId}")

            with(binding) {
                videoTitle.text = playlistItem.snippet.title
                channelName.text = playlistItem.snippet.channelTitle

                val viewCountText = videoDetails?.statistics?.viewCount?.let {
                    NumberFormatter.formatViewCount(it.toInt())
                } ?: ""
                val publishDateText = videoDetails?.snippet?.publishedAt?.let {
                    DateFormatter.formatPublishDate(it)
                } ?: ""
                videoInfo.text = root.context.getString(
                    R.string.video_info_format,
                    viewCountText,
                    publishDateText
                )

                videoDuration.text = videoDetails?.contentDetails?.duration?.let {
                    DateFormatter.formatDuration(it)
                }

                loadThumbnail(playlistItem.snippet.thumbnails)
                loadChannelPhoto(videoDetails?.snippet?.thumbnails?.high?.url)

                root.setOnClickListener {
                    Timber.d("Video clicked: ${playlistItem.contentDetails.videoId}")
                    onVideoClick(playlistItem.contentDetails.videoId)
                }
                channelPhoto.setOnClickListener {
                    Timber.d("Channel clicked: ${playlistItem.snippet.channelId}")
                    onChannelClick(playlistItem.snippet.channelId)
                }
            }
        }

        private fun loadThumbnail(thumbnails: Thumbnails) {
            val url = thumbnails.high?.url ?: thumbnails.medium?.url ?: thumbnails.default?.url ?: ""
            Timber.d("Loading thumbnail from URL: $url")
            if (url.isNotEmpty()) {
                Picasso.get().load(url).into(binding.videoThumbnail)
            } else {
                binding.videoThumbnail.setImageResource(R.drawable.video_placeholder2)
            }
        }

        private fun loadChannelPhoto(url: String?) {
            binding.channelPhoto.setImageResource(R.drawable.ic_wikipedia)
        }
    }
}

class VideoDiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {
    override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        Timber.d("Checking if items are the same: ${oldItem.id} == ${newItem.id}")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        Timber.d("Checking if contents are the same: ${oldItem.id} == ${newItem.id}")
        return oldItem == newItem
    }
}
