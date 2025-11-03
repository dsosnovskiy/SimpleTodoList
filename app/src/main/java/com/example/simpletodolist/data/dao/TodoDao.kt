package com.example.simpletodolist.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.simpletodolist.data.entity.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("""
        SELECT * FROM todo_items 
        ORDER BY 
            is_completed ASC,
            item_order ASC,
            date_of_completion ASC
    """)
    fun getAllItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE is_completed = 0 ORDER BY is_assigned = 0, item_order ASC")
    fun getUncompletedItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE is_completed = 1 ORDER BY date_of_completion ASC")
    fun getCompletedItems(): Flow<List<TodoItem>>

    @Insert
    suspend fun insert(item: TodoItem)

    @Update
    suspend fun update(item: TodoItem)

    @Update
    suspend fun update(items: List<TodoItem>)

    @Query("DELETE FROM todo_items WHERE id = :itemId")
    suspend fun delete(itemId: Int)

    @Query("SELECT * FROM todo_items WHERE id = :itemId")
    suspend fun getItem(itemId: Int): TodoItem?
}
