package com.example.shopping.activities

import android.os.Bundle
import android.widget.Toast
import com.example.shopping.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        btn_submit.setOnClickListener {
            val email: String = et_email.text.toString().trim{ it <= ' '}
            if(email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener {task ->
                        if(task.isSuccessful){
                            hideProgressDialog()
                            Toast.makeText(this, "Email send successfully to reset password",Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else{
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }

            }
        }
    }
}