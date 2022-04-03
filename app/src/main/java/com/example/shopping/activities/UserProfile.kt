package com.example.shopping.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.user
import com.example.shopping.R
import com.example.shopping.utils.GlideLoader
import com.example.shopping.utils.constants
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.et_email
import kotlinx.android.synthetic.main.activity_register.et_first_name
import kotlinx.android.synthetic.main.activity_register.et_last_name
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfile : BaseActivity(), View.OnClickListener {
    private lateinit var userDetail: user
    private var mselectedImageFileUri: Uri? = null
    private var mselectedImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        if(intent.hasExtra(constants.EXTRA_USER_DETAIL)){
            userDetail = intent.getParcelableExtra(constants.EXTRA_USER_DETAIL)!!
        }

        et_first_name.setText(userDetail.firstName)
        et_last_name.setText(userDetail.lastName)
        et_email.isEnabled = false
        et_email.setText(userDetail.email)

        if(userDetail.profileCompleted == 0){
//            tv_title.text = resources.getString(R.string.title_complete_profile)
            et_first_name.isEnabled = false
            et_last_name.isEnabled = false

        }
        else{
            setupActionBar()
            GlideLoader(this).loadUserPicture(userDetail.image,iv_user_photo)
            if(userDetail.mobile != 0L){
                et_mobile_number.setText(userDetail.mobile.toString())
            }
            if(userDetail.gender == constants.MALE){
                rb_male.isChecked = true
            }
            else{
                rb_female.isChecked = true
            }
        }


        iv_user_photo.setOnClickListener(this@UserProfile)

        btn_submit.setOnClickListener(this@UserProfile)



    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)
        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener{onBackPressed()}
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        constants.showImageChooser(this@UserProfile)
                        // END
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }


                R.id.btn_submit ->{

                    if(ValidateUserProfileDetail()){
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if(mselectedImageFileUri != null){
                            firestoreclass().uploadImageToCloudStorage(this, mselectedImageFileUri, constants.USER_PROFILE_IMAGE)

                        }
                        else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails(){
        val userHashMap= HashMap<String,Any>()
        val firstName = et_first_name.text.toString().trim{it <= ' '}
        if(firstName != userDetail.firstName){
            userHashMap[constants.FIRST_NAME] = firstName
        }
        val lastName = et_last_name.text.toString().trim{it <= ' '}
        if(lastName != userDetail.lastName){
            userHashMap[constants.LAST_NAME] = lastName
        }
        val phone = et_mobile_number.text.toString().trim{it <= ' '}
        val gender = if(rb_male.isChecked){
            constants.MALE
        }
        else{
            constants.FEMALE
        }
        if(phone.isNotEmpty() && phone != userDetail.mobile.toString()){
            userHashMap[constants.MOBILE] = phone.toLong()
        }
        if(gender.isNotEmpty() && gender != userDetail.gender){
            userHashMap[constants.GENDER] = gender
        }

        if(mselectedImageURL.isNotEmpty()){
            userHashMap[constants.IMAGE] = mselectedImageURL
        }
        userHashMap[constants.COMPLETE_PROFILE] = 1
//        showProgressDialog(resources.getString(R.string.please_wait))

        firestoreclass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this,"Updated successfully",Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@UserProfile, DashBoardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                constants.showImageChooser(this@UserProfile)
                // END
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mselectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mselectedImageFileUri!!,iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfile,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun ValidateUserProfileDetail(): Boolean{
        return when{
            TextUtils.isEmpty(et_mobile_number.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String){
//        hideProgressDialog()
//        Toast.makeText(this, "Your is uploaded successfully ${imageURL}",Toast.LENGTH_SHORT).show()
        mselectedImageURL = imageURL
        updateUserProfileDetails()
    }
}