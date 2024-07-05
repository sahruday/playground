package com.sahu.playground.data.cache.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addData(table: Table)

    @Query("SELECT * FROM `Table`")
    fun getData(): Flow<List<Table>>

    @Query("DELETE FROM `Table` WHERE 1=1")
    suspend fun delete()
}

@Entity
data class Table(
    @PrimaryKey val id: Int
)