package com.example.newsapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.databinding.ItemYoutubeVideoCardBinding
import com.example.newsapp.domain.service.PlaylistItem
import com.example.newsapp.domain.service.Thumbnails

import com.example.newsapp.presentation.viewModels.VideoDetails
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class VideoAdapter(private val onVideoClick: (String) -> Unit) :
    ListAdapter<PlaylistItem, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    private val videoDetailsMap = mutableMapOf<String, VideoDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemYoutubeVideoCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position), videoDetailsMap[getItem(position).contentDetails.videoId])
    }

    fun updateVideoStatistics(videoDetailsMap: Map<String, VideoDetails>) {
        this.videoDetailsMap.putAll(videoDetailsMap)
        notifyDataSetChanged()
    }

    inner class VideoViewHolder(private val binding: ItemYoutubeVideoCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlistItem: PlaylistItem, videoDetails: VideoDetails?) {
            binding.videoTitle.text = playlistItem.snippet.title
            binding.videoDate.text = videoDetails?.snippet?.publishedAt?.let { convertToLocalDateFormat(it) }
            binding.videoDuration.text = videoDetails?.contentDetails?.duration?.let { convertToReadableDuration(it) }
            binding.channelName.text = playlistItem.snippet.channelTitle
            binding.videoViewCount.text = videoDetails?.statistics?.viewCount?.let {
                formatViewCount(
                    it.toInt())
            }
//          binding.videoLikes.text = "Likes: ${videoDetails?.statistics?.likeCount}"

            Picasso.get().load(getOptimalThumbnailUrl(playlistItem.snippet.thumbnails)).into(binding.videoThumbnail);
            binding.videoThumbnail.setOnClickListener {
                onVideoClick(playlistItem.contentDetails.videoId)
            }
        }

        private fun convertToLocalDateFormat(dateStr: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            return outputFormat.format(date)
        }

        private fun convertToReadableDuration(duration: String): String {
            val pattern = "PT(\\d+H)?(\\d+M)?(\\d+S)?".toRegex()
            val match = pattern.matchEntire(duration)
            return if (match != null) {
                val hours = match.groupValues[1].removeSuffix("H").takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
                val minutes = match.groupValues[2].removeSuffix("M").takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
                val seconds = match.groupValues[3].removeSuffix("S").takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                duration
            }
        }

        private fun getOptimalThumbnailUrl(thumbnails: Thumbnails): String {
            // You can further improve the logic here to choose a more optimal thumbnail based on device resolution
            return thumbnails.high?.url ?: thumbnails.medium?.url ?: thumbnails.default?.url ?: ""
        }
    }
}
fun formatViewCount(viewCount: Int): String {
    return when {
        viewCount < 1000 -> viewCount.toString()
        viewCount < 1000000 -> "${(viewCount / 1000)}k views"
        else -> "${(viewCount / 1000000)}M views"
    }
}
class VideoDiffCallback : DiffUtil.ItemCallback<PlaylistItem>() {
    override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
        return oldItem == newItem
    }
}
