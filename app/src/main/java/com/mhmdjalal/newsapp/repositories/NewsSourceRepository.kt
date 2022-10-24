package com.mhmdjalal.newsapp.repositories

import com.mhmdjalal.newsapp.data.models.Source
import com.mhmdjalal.newsapp.data.models.SourcesPaginate
import com.mhmdjalal.newsapp.network.Resource
import kotlinx.coroutines.flow.Flow

/**
 * @author Created by Muhamad Jalaludin on 24/10/2022
 */
interface NewsSourceRepository {
    suspend fun fetchSources(currentSources: List<Source>, query: HashMap<String, String?>): Flow<Resource<SourcesPaginate>>
}