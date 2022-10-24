package com.mhmdjalal.newsapp.ui.screens.article.adapter

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mhmdjalal.newsapp.R
import com.mhmdjalal.newsapp.data.models.Article
import com.mhmdjalal.newsapp.databinding.ItemArticleBinding
import com.mhmdjalal.newsapp.databinding.ItemLoadingBinding
import com.mhmdjalal.newsapp.utils.CollectionExt.appendWithoutDuplicates
import com.mhmdjalal.newsapp.utils.DateUtils
import com.mhmdjalal.newsapp.utils.requestOptionsCircular
import com.mhmdjalal.newsapp.utils.viewBinding

/**
 * @author Created by Muhamad Jalaludin on 21/10/2022
 */
class ArticleAdapter(private val listener: (Article) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = listOf<Article?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            ArticleViewHolder(parent.viewBinding(ItemArticleBinding::inflate), listener)
        } else {
            LoadingViewHolder(parent.viewBinding(ItemLoadingBinding::inflate))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_ITEM) {
            val item = data[position]
            if (item != null) {
                (holder as ArticleViewHolder).bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount() = data.size

    inner class LoadingViewHolder(binding: ItemLoadingBinding): RecyclerView.ViewHolder(binding.root)

    inner class ArticleViewHolder(private val binding: ItemArticleBinding, private val listener: (Article) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Article) = with(binding) {
            Glide.with(itemView.context)
                .load(item.urlToImage)
                .apply(requestOptionsCircular(itemView.context))
                .error(R.mipmap.no_image)
                .into(imageArticle)
            textArticleTitle.text = item.title
            textArticleDate.text = DateUtils.convertTime(item.publishedAt)
            textArticleDesc.text = HtmlCompat.fromHtml(item.content ?: "", HtmlCompat.FROM_HTML_MODE_COMPACT)
            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }

    fun addLoadingView() {
        // Add loading to the last index
        Handler(Looper.getMainLooper()).post {
            val tempArr = data.toMutableList()
            tempArr.add(null)
            data = tempArr
            notifyItemInserted(data.lastIndex)
        }
    }

    fun removeLoadingView() {
        // Remove last item if the last item is loading view
        val removedIndex = data.indexOf(null)
        if (removedIndex == -1) return
        if (data.isNotEmpty() && getItemViewType(removedIndex) == VIEW_TYPE_LOADING) {
            val tempArr = data.toMutableList()
            tempArr.removeAt(removedIndex)
            data = tempArr
            notifyItemRemoved(removedIndex)
        }
    }

    fun setData(data: List<Article?>) {
        val newData = this.data.appendWithoutDuplicates(
            indexFromZero = false,
            newList = data,
            distinct = { distinct -> distinct?.url }
        )

        this.data = newData

        notifyDataSetChanged()
    }

    fun getData(): List<Article> = data.filterNotNull()

    fun clearData() {
        data = emptyList()
        notifyDataSetChanged()
    }

    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 0
    }
}