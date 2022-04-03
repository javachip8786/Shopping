package com.example.shopping.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.user
import com.example.shopping.R
import com.example.shopping.utils.GlideLoader
import com.example.shopping.utils.constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails: user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()

        tv_edit.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
        ll_address.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_settings_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }
        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        firestoreclass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: user){
        mUserDetails = user
        hideProgressDialog()
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfile::class.java)
                    intent.putExtra(constants.EXTRA_USER_DETAIL, mUserDetails)
                    startActivity(intent)

                }

                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}