package com.example.simpletodolist.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.simpletodolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("""
        SELECT * FROM todo_items 
        ORDER BY 
            is_completed ASC,
            date_of_completion DESC,
            item_order ASC
    """)
    fun getAllItems(): Flow<List<TodoItem>>

    @Query("""
        SELECT * FROM todo_items 
        WHERE is_completed = 0 
        ORDER BY 
            is_assigned DESC,
            item_order IS NULL DESC,
            item_order DESC,
            last_action_timestamp DESC,
            id DESC
    """)
    fun getUncompletedItems(): Flow<List<TodoItem>>

    @Query("""
        SELECT * FROM todo_items 
        WHERE is_completed = 0 AND is_assigned = 1 
        ORDER BY 
            item_order IS NULL DESC, 
            item_order DESC, 
            last_action_timestamp DESC, 
            id DESC
    """)
    fun getAssignedItems(): Flow<List<TodoItem>>

    @Query("""
        SELECT * FROM todo_items 
        WHERE is_completed = 0 AND is_assigned = 0 
        ORDER BY 
            item_order IS NULL DESC, 
            item_order DESC, 
            last_action_timestamp DESC, 
            id DESC
    """)
    fun getUnassignedItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE is_completed = 1 ORDER BY date_of_completion DESC")
    fun getCompletedItems(): Flow<List<TodoItem>>

    @Insert
    suspend fun insert(item: TodoItem): Long

    @Update
    suspend fun update(item: TodoItem)

    @Update
    suspend fun update(items: List<TodoItem>)

    @Query("DELETE FROM todo_items WHERE id = :itemId")
    suspend fun delete(itemId: Int)

    @Query("SELECT * FROM todo_items WHERE id = :itemId")
    suspend fun getItem(itemId: Int): TodoItem?
}