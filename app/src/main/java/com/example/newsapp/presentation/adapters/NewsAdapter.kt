package com.example.newsapp.presentation.adapters
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.model.WikiNews
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.Holder>() {
    private var isSortedAscending = true
    var data= ArrayList<WikiNews>()
    private var onClickListener:((WikiNews)->Unit)?=null
    fun onClickItem(block:(WikiNews)->Unit){
        onClickListener=block
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitItems(newsItems: ArrayList<WikiNews?>){
        data.clear()
       // data.addAll()
        Log.d("WikiNewsViewModel",data.toString())
        this.notifyDataSetChanged()

    }
    inner class Holder(view: View): RecyclerView.ViewHolder(view){



        private val image: ImageView =view.findViewById(R.id.itemImage)
        private val textTitle: TextView = view.findViewById(R.id.itemTitle)
        private val textSubtitle: TextView = view.findViewById(R.id.itemSubtitle)




        @SuppressLint("SetTextI18n")
        fun bind(){
            val item=data[adapterPosition]

            textTitle.text=item.title
            textSubtitle.text = item.articleText
            Picasso.get().load(item.imageUrl).into(image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return  Holder(LayoutInflater.from(parent.context).inflate(R.layout.activity_main,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) =holder.bind()

    override fun getItemCount()=data.size




}