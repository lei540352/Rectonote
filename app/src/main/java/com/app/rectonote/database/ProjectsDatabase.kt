package com.app.rectonote.database

import android.content.Context
import androidx.room.*
import com.app.rectonote.Key
import java.util.*



@Database(entities = arrayOf(ProjectEntity::class, DraftTrackEntity::class), version = 1)
@TypeConverters(DateConverters::class, KeyConverters::class)
abstract class ProjectsDatabase : RoomDatabase(){
    abstract fun projectDAO() : ProjectDao
    abstract fun drafttracksDAO() : DraftTrackDao

    companion object{
        private var INSTANCE: ProjectsDatabase?= null
        fun getInstance(context: Context): ProjectsDatabase {
            synchronized(this) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(
                        context,
                        ProjectsDatabase::class.java,
                        "projectsDatabase.db"
                    ).build()
                return INSTANCE!!
            }
        }
    }
}


