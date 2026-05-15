package com.hasiru.usiru.mapper.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hasiru.usiru.mapper.data.local.dao.PendingSyncDao
import com.hasiru.usiru.mapper.data.local.dao.PitDao
import com.hasiru.usiru.mapper.data.local.dao.SpeciesDao
import com.hasiru.usiru.mapper.data.local.dao.TreeDao
import com.hasiru.usiru.mapper.data.local.entity.PendingSyncEntity
import com.hasiru.usiru.mapper.data.local.entity.PitEntity
import com.hasiru.usiru.mapper.data.local.entity.SpeciesEntity
import com.hasiru.usiru.mapper.data.local.entity.TreeEntity

@Database(
    entities = [
        TreeEntity::class,
        PitEntity::class,
        SpeciesEntity::class,
        PendingSyncEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HasiruDatabase : RoomDatabase() {
    abstract fun treeDao(): TreeDao
    abstract fun pitDao(): PitDao
    abstract fun speciesDao(): SpeciesDao
    abstract fun pendingSyncDao(): PendingSyncDao
}
