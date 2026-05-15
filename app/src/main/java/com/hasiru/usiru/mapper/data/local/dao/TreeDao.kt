package com.hasiru.usiru.mapper.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hasiru.usiru.mapper.data.local.entity.PendingSyncEntity
import com.hasiru.usiru.mapper.data.local.entity.PitEntity
import com.hasiru.usiru.mapper.data.local.entity.SpeciesEntity
import com.hasiru.usiru.mapper.data.local.entity.TreeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TreeDao {
    @Query("SELECT * FROM trees ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TreeEntity>>

    @Query("SELECT * FROM trees WHERE city = :city ORDER BY createdAt DESC")
    fun observeByCity(city: String): Flow<List<TreeEntity>>

    @Query("SELECT * FROM trees WHERE id = :id")
    fun observeById(id: String): Flow<TreeEntity?>

    @Query("SELECT * FROM trees WHERE syncStatus != 'SYNCED'")
    suspend fun getPending(): List<TreeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trees: List<TreeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tree: TreeEntity)

    @Query("DELETE FROM trees WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT COUNT(*) FROM trees")
    suspend fun count(): Int
}

@Dao
interface PitDao {
    @Query("SELECT * FROM empty_pits ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PitEntity>>

    @Query("SELECT * FROM empty_pits WHERE syncStatus != 'SYNCED'")
    suspend fun getPending(): List<PitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pit: PitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pits: List<PitEntity>)
}

@Dao
interface SpeciesDao {
    @Query("SELECT * FROM species ORDER BY nameEn ASC")
    fun observeAll(): Flow<List<SpeciesEntity>>

    @Query("SELECT * FROM species WHERE nameEn LIKE '%' || :q || '%' OR nameKn LIKE '%' || :q || '%'")
    suspend fun search(q: String): List<SpeciesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(species: List<SpeciesEntity>)

    @Query("UPDATE species SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: String, fav: Boolean)
}

@Dao
interface PendingSyncDao {
    @Query("SELECT * FROM pending_sync ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingSyncEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PendingSyncEntity)

    @Query("DELETE FROM pending_sync WHERE id = :id")
    suspend fun delete(id: Long)
}
