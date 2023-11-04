package com.mhmdjalal.newsapp.ui.screens.category.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mhmdjalal.newsapp.data.models.Category
import com.mhmdjalal.newsapp.databinding.ItemCategoryBinding
import com.mhmdjalal.newsapp.utils.viewBinding

/**
 * @author Created by Muhamad Jalaludin on 21/10/2022
 */
class CategoryAdapter(private val listener: (Category) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var data: List<Category> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = parent.viewBinding(ItemCategoryBinding::inflate)
        return CategoryViewHolder(binding, listener)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        private val listener: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category) = with(binding) {
            textCategoryName.text = item.name
            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }

    fun setData(data: List<Category>) {
        this.data = data
        notifyDataSetChanged()
    }
}