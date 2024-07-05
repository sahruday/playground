package com.sahu.playground.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahu.playground.data.cache.dao.TableDao

@Database(
    entities = [TableDao::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun tableDao() : TableDao

}