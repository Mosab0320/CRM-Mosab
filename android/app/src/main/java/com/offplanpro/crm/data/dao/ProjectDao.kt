package com.offplanpro.crm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.offplanpro.crm.data.entity.Project

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): LiveData<List<Project>>

    @Query("SELECT * FROM projects ORDER BY id DESC")
    suspend fun getAllProjectsList(): List<Project>

    @Query("SELECT name FROM projects ORDER BY name ASC")
    suspend fun getProjectNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: Project): Long

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)

    @Query("DELETE FROM projects")
    suspend fun deleteAll()
}
