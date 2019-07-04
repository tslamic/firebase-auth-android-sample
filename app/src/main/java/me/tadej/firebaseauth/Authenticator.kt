package me.tadej.firebaseauth

/**
 * @see FirebaseAuthenticator
 */
interface Authenticator<User> {
    val user: User?

    suspend fun requestOneTimePassword(phoneNumber: String): Status
    suspend fun verifyPassword(password: String): Status
    suspend fun authenticate(): User?
    suspend fun signOut()
}
