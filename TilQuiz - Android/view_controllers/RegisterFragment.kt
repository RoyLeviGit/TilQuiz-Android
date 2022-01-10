package com.madortilofficialapps.tilquiz.view_controllers


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.madortilofficialapps.tilquiz.R
import com.madortilofficialapps.tilquiz.extensions.isValidEmail
import com.madortilofficialapps.tilquiz.extensions.isValidNickname
import com.madortilofficialapps.tilquiz.extensions.isValidPassword
import com.madortilofficialapps.tilquiz.extensions.shake
import kotlinx.android.synthetic.main.fragment_register.*
import org.jetbrains.anko.toast

/**
 * A simple [Fragment] subclass.
 *
 */
class RegisterFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerButton.setOnClickListener { emailRegister() }
    }

    private fun emailRegister() {
        var readyToCreateUser = true
        if (!nicknameEditText.text.toString().isValidNickname()) {
            nicknameEditText.shake()
            readyToCreateUser = false
        }
        if (!emailEditText.text.toString().isValidEmail()) {
            emailEditText.shake()
            readyToCreateUser = false
        }
        if (!passwordEditText.text.toString().isValidPassword()) {
            passwordEditText.shake()
            readyToCreateUser = false
        }
        if (passwordEditText.text.toString() != verifyPasswordEditText.text.toString() || verifyPasswordEditText.text.toString().isBlank()) {
            verifyPasswordEditText.shake()
            readyToCreateUser = false
        }
        if (readyToCreateUser) {
            progressBar.isInvisible = false
            mAuth.createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Sign in success.
                            Log.d("wabalabadubdub", "createUserWithEmail: success")
                            val user = mAuth.currentUser
                            val changeRequest = UserProfileChangeRequest.Builder()
                                    .setDisplayName(nicknameEditText.text.toString())
                                    .build()
                            user?.updateProfile(changeRequest)?.addOnCompleteListener {changeRequestTask ->
                                if (changeRequestTask.isSuccessful) {
                                    Log.d("wabalabadubdub", "changeDisplayNameRequest: success")
                                } else {
                                    // If sign in fails, Sign out and display a message to the user.
                                    mAuth.signOut()
                                    Log.w("wabalabadubdub", "changeDisplayNameRequest: failure", it.exception)
                                    activity?.toast("changeDisplayNameRequest: failure " + it.exception)
                                }
                            }
                            findNavController().popBackStack()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("wabalabadubdub", "createUserWithEmail: failure", it.exception)
                            activity?.toast("createUserWithEmail: failure " + it.exception)
                        }
                    }
        }
    }
}
