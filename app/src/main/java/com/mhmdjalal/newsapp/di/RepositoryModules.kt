package com.mhmdjalal.newsapp.di

import com.mhmdjalal.newsapp.repositories.MainRepository
import com.mhmdjalal.newsapp.repositories.MainRepositoryImpl
import com.mhmdjalal.newsapp.repositories.NewsSourceRepository
import com.mhmdjalal.newsapp.repositories.NewsSourceRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * @author Created by Muhamad Jalaludin on 20/10/2022
 */
val repositoryModules = module {
    singleOf(::MainRepositoryImpl) { bind<MainRepository>() }
    singleOf(::NewsSourceRepositoryImpl) { bind<NewsSourceRepository>() }
}