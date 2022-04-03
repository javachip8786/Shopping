package com.example.shopping.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.size
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.Address
import com.example.shopping.R
import com.example.shopping.adapters.AddressListAdapter
import com.example.shopping.utils.constants
import com.myshoppal.utils.SwipeToDeleteCallback
import com.myshoppal.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*
import kotlinx.android.synthetic.main.activity_settings.*

class AddressListActivity : BaseActivity() {
    private var mSelectedAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setupActionBar()
//        getAddressList()
        tv_add_address.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, constants.ADD_ADDRESS_REQUEST_CODE)
        }
        getAddressList()
        if(intent.hasExtra(constants.EXTRA_SELECT_ADDRESS)){
            mSelectedAddress = intent.getBooleanExtra(constants.EXTRA_SELECT_ADDRESS, false)
        }

        

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            getAddressList()
        }
    }

    override fun onResume() {
        super.onResume()
//        getAddressList()
    }

    fun successAddressListFromFireStore(addressList: ArrayList<Address>){
        hideProgressDialog()
        if(addressList.size > 0){
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE
            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this, addressList, mSelectedAddress)
            rv_address_list.adapter = addressAdapter

            if(!mSelectedAddress){
                val editSwipeHandler = object : SwipeToEditCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)

                val delelteSwipeHandler = object: SwipeToDeleteCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        firestoreclass().deleteAddress(this@AddressListActivity, addressList[viewHolder.adapterPosition].id)
                    }

                }

                val deleteItemTouchHelper = ItemTouchHelper(delelteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }

        }
        else{
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }

    fun deleteAddressSuccess(){
        hideProgressDialog()
        getAddressList()
    }

    private fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        firestoreclass().getAddessesList(this)
    }


    private fun setupActionBar(){
        setSupportActionBar(toolbar_address_list_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }
        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
}