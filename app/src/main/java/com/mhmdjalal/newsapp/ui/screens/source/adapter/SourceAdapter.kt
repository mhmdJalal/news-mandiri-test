package com.mhmdjalal.newsapp.ui.screens.source.adapter

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mhmdjalal.newsapp.data.models.Source
import com.mhmdjalal.newsapp.databinding.ItemLoadingBinding
import com.mhmdjalal.newsapp.databinding.ItemSourceBinding
import com.mhmdjalal.newsapp.ui.screens.article.adapter.ArticleAdapter
import com.mhmdjalal.newsapp.utils.CollectionExt.appendWithoutDuplicates
import com.mhmdjalal.newsapp.utils.viewBinding

/**
 * @author Created by Muhamad Jalaludin on 21/10/2022
 */
class SourceAdapter(private val listener: (Source) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: List<Source?> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ArticleAdapter.VIEW_TYPE_ITEM) {
            SourceViewHolder(parent.viewBinding(ItemSourceBinding::inflate), listener)
        } else {
            LoadingViewHolder(parent.viewBinding(ItemLoadingBinding::inflate))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ArticleAdapter.VIEW_TYPE_ITEM) {
            val item = data[position]
            if (item != null) {
                (holder as SourceViewHolder).bind(item)
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

    inner class SourceViewHolder(private val binding: ItemSourceBinding, private val listener: (Source) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Source) = with(binding) {
            textSourceTitle.text = item.name
            textSourceLink.text = item.url
            textSourceDesc.text = item.description
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

    fun setData(data: List<Source?>) {
        val newData = this.data.appendWithoutDuplicates(
            indexFromZero = false,
            newList = data,
            distinct = { distinct -> distinct?.id }
        )

        this.data = newData

        notifyDataSetChanged()
    }

    fun getData(): List<Source> = data.filterNotNull()

    fun clearData() {
        data = emptyList()
        notifyDataSetChanged()
    }

    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_ITEM = 0
    }
}