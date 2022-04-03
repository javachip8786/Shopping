package com.example.shopping.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.R
import com.example.shopping.utils.GlideLoader
import com.example.shopping.utils.constants
import com.myshoppal.models.Product
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.iv_user_photo
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {
    private var mlocationOfImage: Uri? =  null
    private var mlocationURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setupActionBar()
        iv_add_update_product.setOnClickListener(this)
        btn_submit_add_product.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_settings_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }
        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                // The permission code is similar to the user profile image selection.
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        constants.showImageChooser(this@AddProductActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit_add_product -> {
                    if (validateProductDetails()) {
                        uploadProductImage()
                    }
                }
            }
        }
    }

    private fun uploadProductImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        firestoreclass().uploadImageToCloudStorage(
            this@AddProductActivity,
            mlocationOfImage,
            constants.PRODUCT_IMAGE
        )
    }

    fun imageUploadSuccess(imageURL: String) {
        mlocationURL = imageURL
        uploadProductDetails()
    }

    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this, "Product Uploaded",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(constants.MYSHOPPAL_PREF, Context.MODE_PRIVATE).getString(constants.LOGGED_IN_USERNAME,"")!!
        val product = Product(
            firestoreclass().getCurrentUserID(),
            username,
            et_product_title.text.toString().trim{it <= ' '},
            et_product_price.text.toString().trim{it <= ' '},
            et_product_description.text.toString().trim{it <= ' '},
            et_product_quantity.text.toString().trim{it <= ' '},
            mlocationURL
        )
        firestoreclass().uploadProductDetail(this, product)
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

                constants.showImageChooser(this@AddProductActivity)
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
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24))
                    try{
                        mlocationOfImage = data.data!!
                        GlideLoader(this).loadUserPicture(mlocationOfImage!!, iv_product_image)
                    }
                    catch(e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {

            mlocationOfImage == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }
}