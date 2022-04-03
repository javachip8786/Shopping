package com.example.shopping.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.user
import com.example.shopping.R
import com.example.shopping.utils.constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)

        tv_forgot_password.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPassword::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {

                R.id.tv_forgot_password -> {
                }

                R.id.btn_login -> {

                    LogInRegisteredUser()
                }

                R.id.tv_register -> {
                    // Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
//                    finish()
                }
            }
        }
    }
    
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(let_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(let_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
//                showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }

    private fun LogInRegisteredUser(){
        if(validateLoginDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))
            val email = let_email.text.toString().trim{it <= ' '}
            val password = let_password.text.toString().trim{it <= ' '}
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    task ->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        firestoreclass().getUserDetails(this@LoginActivity)
                    }
                    else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user : user){
        hideProgressDialog()

        if(user.profileCompleted == 0){
            val intent = Intent(this@LoginActivity, UserProfile::class.java)
            intent.putExtra(constants.EXTRA_USER_DETAIL, user)
            startActivity(intent)
        }
        else{
            startActivity(Intent(this@LoginActivity, DashBoardActivity::class.java))
        }
        finish()
    }
}