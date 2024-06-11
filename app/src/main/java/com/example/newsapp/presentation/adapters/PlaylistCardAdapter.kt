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
import com.example.newsapp.data.model.YouTubePlaylistVideos
import com.example.newsapp.presentation.ui.components.YouTubeRecyclerViewFragment
import com.squareup.picasso.Picasso
import java.math.BigInteger
import java.text.DecimalFormat

class PlaylistCardAdapter(
    private val youTubePlaylistVideos: YouTubePlaylistVideos,
    private val lastItemReachedListener: YouTubeRecyclerViewFragment.LastItemReachedListener
) : RecyclerView.Adapter<PlaylistCardAdapter.ViewHolder>() {

    companion object {
        private val formatter = DecimalFormat("#,###,###")
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context: Context = view.context
        val titleText: TextView = view.findViewById(R.id.video_title)
        val thumbnailImage: ImageView = view.findViewById(R.id.video_thumbnail)
        val durationText: TextView = view.findViewById(R.id.video_duration)
        val viewCountText: TextView = view.findViewById(R.id.video_view_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_youtube_video_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (youTubePlaylistVideos.isEmpty()) return

        val video = youTubePlaylistVideos[position] ?: return
        val videoSnippet = video.snippet
        val videoContentDetails = video.contentDetails
        val videoStatistics = video.statistics

        holder.titleText.text = videoSnippet?.title.orEmpty()

        videoSnippet?.thumbnails?.high?.url?.let {
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.video_placeholder)
                .into(holder.thumbnailImage)
        }

        holder.thumbnailImage.setOnClickListener {
            holder.context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video.id}"))
            )
        }

        holder.durationText.text = videoContentDetails?.duration?.let { parseDuration(it) }.orEmpty()
        holder.viewCountText.text = formatter.format(videoStatistics?.viewCount ?: BigInteger.ZERO)

        val nextPageToken = youTubePlaylistVideos.nextPageToken
        if (!nextPageToken.isNullOrEmpty() && position == youTubePlaylistVideos.size - 1) {
            holder.itemView.post {
                lastItemReachedListener.onLastItem(position, nextPageToken)
            }
        }
    }

    override fun getItemCount(): Int = youTubePlaylistVideos.size

    private fun parseDuration(duration: String): String {
        val regex = Regex("PT(?:(\\d+)M)?(?:(\\d+)S)?")
        val matchResult = regex.matchEntire(duration)
        if (matchResult != null) {
            val min = matchResult.groups[1]?.value?.toInt() ?: 0
            val sec = matchResult.groups[2]?.value?.toInt() ?: 0
            return String.format("%02d:%02d", min, sec)
        }
        return "00:00"
    }
}
