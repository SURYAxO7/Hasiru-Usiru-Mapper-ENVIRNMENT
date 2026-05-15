package com.hasiru.usiru.mapper.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hasiru.usiru.mapper.data.remote.firebase.FirestorePaths
import com.hasiru.usiru.mapper.data.remote.firebase.toFirestoreMap
import com.hasiru.usiru.mapper.data.remote.firebase.toUserProfile
import com.hasiru.usiru.mapper.domain.model.UserProfile
import com.hasiru.usiru.mapper.domain.model.UserRole
import com.hasiru.usiru.mapper.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {

    override val currentUser: FirebaseUser? get() = auth.currentUser
    override val isLoggedIn: Boolean get() = auth.currentUser != null

    override fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Result<UserProfile> = try {
        auth.signInWithEmailAndPassword(email, password).await()
        val profile = getProfile() ?: createDefaultProfile()
        Result.success(profile)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        phone: String
    ): Result<UserProfile> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw IllegalStateException("User creation failed")
        val profile = UserProfile(
            uid = uid,
            name = name,
            email = email,
            phone = phone,
            role = UserRole.CITIZEN
        )
        firestore.collection(FirestorePaths.USERS).document(uid)
            .set(profile.toFirestoreMap()).await()
        Result.success(profile)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<UserProfile> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("Google sign-in failed")
        val existing = firestore.collection(FirestorePaths.USERS).document(user.uid).get().await()
        if (!existing.exists()) {
            val profile = UserProfile(
                uid = user.uid,
                name = user.displayName ?: "",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString() ?: ""
            )
            firestore.collection(FirestorePaths.USERS).document(user.uid)
                .set(profile.toFirestoreMap()).await()
            Result.success(profile)
        } else {
            Result.success(existing.toUserProfile(user.uid))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun resetPassword(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun getProfile(): UserProfile? = try {
        val uid = auth.currentUser?.uid ?: return null
        // Wrap in timeout to prevent hang if Firestore API is disabled
        val doc = kotlinx.coroutines.withTimeout(3000) {
            firestore.collection(FirestorePaths.USERS).document(uid).get().await()
        }
        if (doc.exists()) doc.toUserProfile(uid) else createDefaultProfile()
    } catch (_: Exception) {
        // Fallback for verification/demo if Firestore fails or timeouts
        auth.currentUser?.let { user ->
            UserProfile(
                uid = user.uid,
                name = user.displayName ?: "Citizen",
                email = user.email ?: ""
            )
        }
    }

    override suspend fun updateProfile(profile: UserProfile): Result<Unit> = try {
        firestore.collection(FirestorePaths.USERS).document(profile.uid)
            .set(profile.toFirestoreMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun uploadProfilePhoto(uid: String, bytes: ByteArray): String {
        val ref = storage.reference.child("users/$uid/profile.jpg")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    private suspend fun createDefaultProfile(): UserProfile {
        val user = auth.currentUser ?: throw IllegalStateException("Not authenticated")
        val profile = UserProfile(
            uid = user.uid,
            name = user.displayName ?: "",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString() ?: ""
        )
        firestore.collection(FirestorePaths.USERS).document(user.uid)
            .set(profile.toFirestoreMap()).await()
        return profile
    }
}
