package me.tadej.firebaseauth

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ExperimentalCoroutinesApi
class FirebaseAuthenticator : Authenticator<FirebaseUser> {
    private val executor = Executors.newSingleThreadExecutor()

    private var credential: PhoneAuthCredential? = null
    private var id: String? = null

    override val user: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    override suspend fun requestOneTimePassword(phoneNumber: String): Status =
        suspendCoroutine { c ->
            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    executor,
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(cred: PhoneAuthCredential) {
                            credential = cred
                            c.resume(Status.Completed)
                        }

                        override fun onVerificationFailed(exception: FirebaseException) {
                            c.resume(Status.Failed(exception))
                        }

                        override fun onCodeSent(vid: String, token: PhoneAuthProvider.ForceResendingToken) {
                            id = vid
                            c.resume(Status.Pending)
                        }

                        override fun onCodeAutoRetrievalTimeOut(id: String?) {
                            Log.e("FirebaseAuthenticator", "onCodeAutoRetrievalTimeOut")
                        }
                    })
        }

    override suspend fun verifyPassword(password: String): Status {
        val vid = id
        return if (vid.isNullOrEmpty()) {
            Status.Failed.withMessage("missing verification ID")
        } else {
            credential = PhoneAuthProvider.getCredential(vid, password)
            Status.Completed
        }
    }

    override suspend fun authenticate(): FirebaseUser? {
        val cred = credential ?: return null
        return suspendCoroutine { c ->
            FirebaseAuth.getInstance()
                .signInWithCredential(cred)
                .addOnCompleteListener {
                    var handled = false
                    if (it.isSuccessful) {
                        val result = it.result
                        if (result != null) {
                            c.resume(result.user!!)
                            handled = true
                        }
                    }
                    if (!handled) {
                        c.resume(null)
                    }
                    credential = null
                    id = null
                }
        }
    }

    override suspend fun signOut() = FirebaseAuth.getInstance().signOut()
}
