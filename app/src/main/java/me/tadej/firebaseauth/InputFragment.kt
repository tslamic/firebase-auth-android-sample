package me.tadej.firebaseauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_input.*

abstract class InputFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
        inputContent.setOnEditorActionListener { _, _, _ -> inputButton.callOnClick() }
        setup(model, inputContent, inputButton)
    }

    abstract fun setup(model: AuthViewModel, text: EditText, button: Button)
}

class PhoneNumberFragment : InputFragment() {
    override fun setup(model: AuthViewModel, text: EditText, button: Button) {
        text.setHint(R.string.phone_hint)
        button.setText(R.string.request_otp)
        button.setOnClickListener {
            val number = text.text.toString()
            if (number.isNotEmpty()) {
                model.requestOneTimePassword(number)
            }
        }
    }
}

class OtpFragment : InputFragment() {
    override fun setup(model: AuthViewModel, text: EditText, button: Button) {
        text.setHint(R.string.otp)
        button.setText(R.string.login)
        button.setOnClickListener {
            val password = text.text.toString()
            if (password.isNotEmpty()) {
                model.verifyPassword(password)
            }
        }
    }
}
