package com.example.newsapp.presentation.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.model.PlaylistVideos
import com.example.newsapp.presentation.screen.YouTubeRecyclerViewFragment
import com.squareup.picasso.Picasso
import java.math.BigInteger
import java.text.DecimalFormat

class PlaylistCardAdapter(
    private val playlistVideos: PlaylistVideos,
    private val lastItemReachedListener: YouTubeRecyclerViewFragment.LastItemReachedListener
) : RecyclerView.Adapter<PlaylistCardAdapter.ViewHolder>() {

    companion object {
        private val formatter = DecimalFormat("#,###,###")
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context: Context = view.context
        val titleText: TextView = view.findViewById(R.id.video_title)
//        val channelNameText: TextView = view.findViewById(R.id.channel_name)
        val thumbnailImage: ImageView = view.findViewById(R.id.video_thumbnail)
        val durationText: TextView = view.findViewById(R.id.video_duration)
        val viewCountText: TextView = view.findViewById(R.id.video_view_count)
        val videoDateText: TextView = view.findViewById(R.id.video_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.youtube_video_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (playlistVideos.isEmpty()) return

        val video = playlistVideos[position]
        val videoSnippet = video?.snippet
        val videoContentDetails = video?.contentDetails
        val videoStatistics = video?.statistics

        holder.titleText.text = videoSnippet?.title
//        holder.channelNameText.text = videoSnippet?.channelTitle

        if (videoSnippet != null) {
            Picasso.get()
                .load(videoSnippet.thumbnails.high.url)
                .placeholder(R.drawable.video_placeholder)
                .into(holder.thumbnailImage)
        }

        holder.thumbnailImage.setOnClickListener {
            holder.context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video?.id}"))
            )
        }

        if (videoContentDetails != null) {
            holder.durationText.text = parseDuration(videoContentDetails.duration)
        }

        holder.viewCountText.text = formatter.format(videoStatistics?.viewCount ?: BigInteger.ZERO)
//        holder.videoDateText.text = videoSnippet?.publishedAt?.toString()?.let { parseDate(it) }

        if (lastItemReachedListener != null) {
            val nextPageToken = playlistVideos.nextPageToken
            if (!nextPageToken.isNullOrEmpty() && position == playlistVideos.size - 1) {
                holder.itemView.post {
                    lastItemReachedListener.onLastItem(position, nextPageToken)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return playlistVideos.size
    }

    private fun parseDuration(duration: String): String {
        val regex = Regex("PT(?:(\\d+)M)?(?:(\\d+)S)?")
        val matchResult = regex.matchEntire(duration)
        val (minutes, seconds) = if (matchResult != null) {
            val min = matchResult.groups[1]?.value?.toInt() ?: 0
            val sec = matchResult.groups[2]?.value?.toInt() ?: 0
            Pair(min, sec)
        } else {
            Pair(0, 0)
        }
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun parseDate(date: String): String {
        // Implement a method to parse and format the date string
        return date // Placeholder
    }
}
