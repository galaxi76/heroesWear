/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.heroeswear.com.heroesmakers.login.Activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import app.heroeswear.com.common.FBCalbacks
import app.heroeswear.com.heroesfb.FirebaseManager
import app.heroeswear.com.heroesfb.Logger

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import app.heroeswear.com.heroesmakers.R
import app.heroeswear.com.heroesmakers.login.models.User
import common.AppSettingsProfile
import common.BaseActivity
import kotlinx.android.synthetic.main.email_signin_login.*

class LoginSignInActivity : BaseActivity(), View.OnClickListener, FBCalbacks {

    private var mStatusTextView: TextView? = null
    private var mDetailTextView: TextView? = null
    private var mEmailField: EditText? = null
    private var mPasswordField: EditText? = null

    // [START declare_auth]
    private var mAuth: FirebaseAuth? = null
    internal var currentUser: FirebaseUser? = null
    private lateinit var mUser : User

    // [END declare_auth]

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_signin_login)

        // Views
        mStatusTextView = findViewById(R.id.status)
        mDetailTextView = findViewById(R.id.detail)
        mEmailField = findViewById(R.id.field_email)
        mPasswordField = findViewById(R.id.field_password)

        // Buttons
        email_sign_in_button.setOnClickListener(this)
        email_create_account_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)
        verify_email_button.setOnClickListener(this)

        // [START initialize_auth]
        fbManager?.onCreate()
        // [END initialize_auth]

    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = fbManager?.onStart()

//        updateUI(currentUser)
        Logger.d("Push token: ${FirebaseManager.newInstance().getPushToken()}")
    }
    // [END on_start_check_user]

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        // [START create_user_with_email]
        // TODO convert to kotlin and add coroutines for hideProgressDialog();
        currentUser = fbManager?.createAccount(email, password,this)


        hideProgressDialog()

        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        showProgressDialog()
        // TODO convert to kotlin and add coroutines for hideProgressDialog();
        currentUser = fbManager?.signInUser(email, password,this)

    }

    private fun signOut() {
        fbManager?.signOut()
        //updateUI(null)

        mStatusTextView!!.setText(R.string.signed_out)
        mDetailTextView!!.text = null

        email_password_buttons.setVisibility(View.VISIBLE)
        email_password_fields.setVisibility(View.VISIBLE)
        signed_in_buttons.setVisibility(View.GONE)
    }

    private fun sendEmailVerification() {
        // Disable button
        verify_email_button.setEnabled(false)

        // Send verification email
        // [START send_email_verification]
        val user = fbManager?.mCurrentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    // [START_EXCLUDE]
                    // Re-enable button
                    verify_email_button.setEnabled(true)

                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginSignInActivity,
                                "Verification email sent to " + user.email!!,
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        Toast.makeText(this@LoginSignInActivity,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show()
                    }
                    // [END_EXCLUDE]
                }
        // [END send_email_verification]
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = mEmailField!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            mEmailField!!.error = "Required."
            valid = false
        } else {
            mEmailField!!.error = null
        }

        val password = mPasswordField!!.text.toString()
        if (TextUtils.isEmpty(password)) {
            mPasswordField!!.error = "Required."
            valid = false
        } else {
            mPasswordField!!.error = null
        }

        return valid
    }

    /*private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
//            mStatusTextView!!.text = getString(R.string.emailpassword_status_fmt,
//                    user.email, user.isEmailVerified)
//            mDetailTextView!!.text = getString(R.string.firebase_status_fmt, user.uid)
//
//            email_password_buttons.setVisibility(View.GONE)
//            email_password_fields.setVisibility(View.GONE)
//            signed_in_buttons.setVisibility(View.VISIBLE)
//
//            verify_email_button.setEnabled(!user.isEmailVerified)

//
        } else {
            mStatusTextView!!.setText(R.string.signed_out)
            mDetailTextView!!.text = null

            email_password_buttons.setVisibility(View.VISIBLE)
            email_password_fields.setVisibility(View.VISIBLE)
            signed_in_buttons.setVisibility(View.GONE)
        }
    }*/


    override fun onCreateAccountCompleted(user: FirebaseUser?) {
        if (user == null)
        {
            Toast.makeText(this, "Create account failed", Toast.LENGTH_LONG).show()
            return
        }

        user.let {
            mUser = User()
            mUser.userId = user?.uid
            mUser.email = user?.email

            hideProgressDialog()
            AppSettingsProfile.getInstance().setUserID(mUser.userId)
            AppSettingsProfile.getInstance().isSignedIn = true

            initEmpaE4()
            openHomePage(mUser)
            finish()
        }
    }

    override fun onSignInCompleted(user: FirebaseUser?) {

        if (user == null)
        {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_LONG).show()
            return
        }

        user.let {
            mUser = User()
            mUser.userId = user?.uid
            mUser.email = user?.email

            hideProgressDialog()
            initEmpaE4()
            openHomePage(mUser)
            finish()
        }
    }

    override fun onSignInFailed(user: FirebaseUser?) {
        hideProgressDialog()
        if (currentUser == null ){
            Toast.makeText(this,"please insert correct user name/ password ",Toast.LENGTH_LONG).show()
        }

    }

    override fun onSignOutCompleted() {
//        finish()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField!!.text.toString(), mPasswordField!!.text.toString())
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField!!.text.toString(), mPasswordField!!.text.toString())
        } else if (i == R.id.sign_out_button) {
            signOut()
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification()
        }
    }

    companion object {

        private val TAG = "EmailPassword"
    }

}
