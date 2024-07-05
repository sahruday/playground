package com.sahu.playground.data.cache

import com.sahu.playground.data.cache.dao.TableDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalService @Inject constructor(
    private val tableDao: TableDao
) {
    suspend fun getData() = tableDao.getData()
}