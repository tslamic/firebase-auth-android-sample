package me.tadej.firebaseauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)

        val args = arguments ?: throw IllegalStateException("use newInstance")
        val num = args.getString(KEY_PHONE_NUMBER)
        if (num.isNullOrEmpty()) {
            throw IllegalStateException("use newInstance")
        }

        phoneNumber.text = getString(R.string.auth, num)
        signOut.setOnClickListener {
            model.signOut()
        }
    }

    companion object {
        private const val KEY_PHONE_NUMBER = "HomeFragment.PHONE_NUMBER"

        fun newInstance(user: FirebaseUser): HomeFragment {
            val args = Bundle(1)
            args.putString(KEY_PHONE_NUMBER, user.phoneNumber)

            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
