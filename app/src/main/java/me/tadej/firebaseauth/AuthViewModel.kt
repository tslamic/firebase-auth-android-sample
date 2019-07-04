package me.tadej.firebaseauth

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class AuthViewModel : ViewModel() {
    private val authenticator = FirebaseAuthenticator() // Normally provided via a Factory/DI.

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val data = MutableLiveData<Fragment>()

    fun observe(owner: LifecycleOwner, observer: (Fragment) -> Unit) = data.observe(owner, Observer { observer(it) })

    fun init() {
        val user = authenticator.user
        data.value = if (user == null) {
            PhoneNumberFragment()
        } else {
            HomeFragment.newInstance(user)
        }
    }

    fun requestOneTimePassword(phoneNumber: String) = scope.launch {
        val status = authenticator.requestOneTimePassword(phoneNumber)
        handleStatus(status)
    }

    fun verifyPassword(password: String) = scope.launch {
        val status = authenticator.verifyPassword(password)
        handleStatus(status)
    }

    fun signOut() = scope.launch {
        authenticator.signOut()
        data.postValue(PhoneNumberFragment())
    }

    private suspend fun handleStatus(status: Status) {
        val fragment = when (status) {
            is Status.Completed -> {
                val user = authenticator.authenticate()
                if (user == null) {
                    PhoneNumberFragment()
                } else {
                    HomeFragment.newInstance(user)
                }
            }
            is Status.Pending -> OtpFragment()
            is Status.Failed -> PhoneNumberFragment()
        }
        data.postValue(fragment)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
