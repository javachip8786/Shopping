package com.example.shopping.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.Address
import com.example.shopping.R
import com.example.shopping.utils.constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_address_list.*

class AddEditAddressActivity : BaseActivity() {

    private var mAddressDetail: Address?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)
        setupActionBar()

        if(intent.hasExtra(constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetail= intent.getParcelableExtra(constants.EXTRA_ADDRESS_DETAILS)

        }

        if(mAddressDetail != null){
            if(mAddressDetail!!.id.isNotEmpty()){
//                tv_title.text = resources.getString(R.string.title_edit_address)
                btn_submit_address.text = resources.getString(R.string.btn_lbl_update)

                et_full_name.setText(mAddressDetail?.name)
                et_phone_number.setText(mAddressDetail?.mobileNumber)
                et_address.setText(mAddressDetail?.address)
                et_zip_code.setText(mAddressDetail?.zipCode)
                et_additional_note.setText(mAddressDetail?.additionalNote)

                when (mAddressDetail?.type) {
                    constants.HOME -> {
                        rb_home.isChecked = true
                    }
                    constants.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_other.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        et_other_details.setText(mAddressDetail?.otherDetails)
                    }
                }
            }
        }

        btn_submit_address.setOnClickListener {
            saveAddressToFirestore()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_edit_address_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }
        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun addUpdateAddressSuccess(){
        hideProgressDialog()
        setResult(RESULT_OK)
        finish()

    }


    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            rb_other.isChecked && TextUtils.isEmpty(
                et_zip_code.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFirestore() {

        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
        val address: String = et_address.text.toString().trim { it <= ' ' }
        val zipCode: String = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_details.text.toString().trim { it <= ' ' }

        if (validateData()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                rb_home.isChecked -> {
                    constants.HOME
                }
                rb_office.isChecked -> {
                    constants.OFFICE
                }
                else -> {
                    constants.OTHER
                }
            }
            val addressModel = Address(
                firestoreclass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if(mAddressDetail!=null && mAddressDetail!!.id.isNotEmpty()){
                firestoreclass().updateAddress(this, addressModel,mAddressDetail!!.id)
            }
            else{
                firestoreclass().addAdress(this,addressModel)
            }
        }
    }
}