package com.mhmdjalal.newsapp.di

import com.mhmdjalal.newsapp.ui.screens.article.ArticleViewModel
import com.mhmdjalal.newsapp.ui.screens.category.CategoryViewModel
import com.mhmdjalal.newsapp.ui.screens.source.SourceViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
 * @author Created by Muhamad Jalaludin on 20/10/2022
 */
val appModules = module {

}

val viewModelModules = module {
    viewModelOf(::CategoryViewModel)
    viewModelOf(::SourceViewModel)
    viewModelOf(::ArticleViewModel)
}