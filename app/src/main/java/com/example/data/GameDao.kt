package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_profile WHERE id = 1 LIMIT 1")
    fun getGameProfileFlow(): Flow<GameProfile?>

    @Query("SELECT * FROM game_profile WHERE id = 1 LIMIT 1")
    suspend fun getGameProfile(): GameProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: GameProfile)

    @Update
    suspend fun updateProfile(profile: GameProfile)

    @Query("SELECT * FROM codex_entries ORDER BY timestamp DESC")
    fun getAllCodexEntries(): Flow<List<CodexEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCodexEntry(entry: CodexEntry)

    @Query("SELECT * FROM codex_entries WHERE id = :id LIMIT 1")
    suspend fun getCodexEntryById(id: String): CodexEntry?
}
