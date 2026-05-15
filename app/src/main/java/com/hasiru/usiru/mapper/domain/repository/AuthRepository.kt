package com.hasiru.usiru.mapper.domain.repository

import com.hasiru.usiru.mapper.domain.model.UserProfile
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    val isLoggedIn: Boolean
    fun observeAuthState(): Flow<FirebaseUser?>
    suspend fun signIn(email: String, password: String): Result<UserProfile>
    suspend fun signUp(name: String, email: String, password: String, phone: String): Result<UserProfile>
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut()
    suspend fun getProfile(): UserProfile?
    suspend fun updateProfile(profile: UserProfile): Result<Unit>
}
