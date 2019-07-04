package me.tadej.firebaseauth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        model.observe(this) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content, it)
                .commit()
        }

        model.init()
    }
}
