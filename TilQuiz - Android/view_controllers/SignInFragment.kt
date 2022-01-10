package com.madortilofficialapps.tilquiz.view_controllers


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.madortilofficialapps.tilquiz.R
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.jetbrains.anko.toast

/**
 * A simple [Fragment] subclass.
 *
 */
class SignInFragment : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerButton.setOnClickListener { findNavController().navigate(
                SignInFragmentDirections.actionSignInFragmentToRegisterFragment()) }

        signInButton.setOnClickListener { emailSignIn() }
        google_sign_in_button.setOnClickListener { googleSignIn() }
        guest_sign_in_button.setOnClickListener { signedIn() }
    }

    private var isSigningIn = false
        set(value) {
            field = value
            progressBar?.isInvisible = !isSigningIn
        }

    private fun emailSignIn() {
        if (!isSigningIn && !emailEditText.text.isNullOrBlank() && !passwordEditText.text.isNullOrBlank()) {
            isSigningIn = true
            mAuth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener { task ->
                        isSigningIn = false
                        if (task.isSuccessful) {
                            // Sign in success
                            Log.d("wabalabadubdub", "signInWithEmail: success")
                            signedIn()
                        } else {
                            // If sign in fails
                            Log.w("wabalabadubdub", "signInWithEmail: failure", task.exception)
                            activity?.toast("signInWithEmail: failure " + task.exception)
                        }
                    }
        }
    }

    private val rcSignIn = 0
    private fun googleSignIn() {
        if (!isSigningIn) {
            isSigningIn = true
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, rcSignIn)
        }
    }

    private fun signedIn() {
        findNavController().popBackStack()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        isSigningIn = false
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("wabalabadubdub", "Google Sign In failed " + e.localizedMessage)
                activity?.toast("Google Sign In failed " + e.localizedMessage)
            }
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            // Sign out of Google to enable relogging with a different account
            mGoogleSignInClient.signOut()
            mGoogleSignInClient.revokeAccess()
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = mAuth.currentUser
                Log.d("wabalabadubdub", user!!.displayName)
                signedIn()
            } else {
                // If sign in fails, display a message to the user.
                Log.w("wabalabadubdub", "Firebase sign in failed")
            }
        }
    }
}