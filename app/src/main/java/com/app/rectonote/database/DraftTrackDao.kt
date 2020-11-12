package com.app.rectonote.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DraftTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newDraftTrack(track: DraftTrackEntity)

    @Query("SELECT * FROM draft_tracks WHERE project_id = :projectId  ORDER BY date_modified DESC")
    suspend fun loadTracksFromProject(projectId: Int): LiveData<MutableList<DraftTrackEntity>>

    @Query("SELECT tracks_name FROM draft_tracks ORDER BY date_modified DESC")
    suspend fun loadTrackNames(): LiveData<List<String>>

    @Update
    suspend fun changeData(track: DraftTrackEntity)

    @Delete
    suspend fun deleteTrack(track: DraftTrackEntity)
}