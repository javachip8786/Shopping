package com.example.shopping.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.Address
import com.example.shopping.Firestore.models.cart_item
import com.example.shopping.R
import com.example.shopping.adapters.CartItemListAdapter
import com.example.shopping.utils.constants
import com.myshoppal.models.Order
import com.myshoppal.models.Product
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address ?= null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<cart_item>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionBar()

        if(intent.hasExtra(constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(constants.EXTRA_SELECTED_ADDRESS)
        }

        if(mAddressDetails != null){
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

//            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
//                tv_checkout_other_details.text = mAddressDetails?.otherDetails
//            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }
        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }

    fun successProductListFromFirestore(productList : ArrayList<Product>){
        mProductList = productList
        getCartItemList()
    }

    fun orderPlaceSuccess(){
        firestoreclass().updateAllDetail(this, mCartItemsList)
    }

    private fun placeAnOrder(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mAddressDetails!=null){
            val order = Order(
                firestoreclass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "$10.0",
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )
            firestoreclass().placeOrder(this,order)
        }

    }

    private fun getCartItemList(){
        firestoreclass().getCartList(this@CheckoutActivity)
    }

    fun successCartItemsList(cartList : ArrayList<cart_item>){
        hideProgressDialog()
        mCartItemsList = cartList

        for(product in mProductList){
            for(cartitem in mCartItemsList){
                if(product.product_id == cartitem.product_id){
                    cartitem.stock_quantity = product.stock_quantity
                }
            }
        }


        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        var cartListAdapter = CartItemListAdapter(this@CheckoutActivity, mCartItemsList,false)
        rv_cart_list_items.adapter = cartListAdapter

        for(item in mCartItemsList){
            val availableQuantity = item.stock_quantity.toInt()
            if(availableQuantity > 0){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += price * quantity
            }
        }

        tv_checkout_sub_total.text = "$$mSubTotal"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        tv_checkout_shipping_charge.text = "$10.0"

        if(mSubTotal > 0){
            ll_checkout_place_order.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text ="$$mTotalAmount"
        }
        else{
            ll_checkout_place_order.visibility = View.GONE
        }

    }

    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this, "order placed successfully",Toast.LENGTH_SHORT).show()

        val intent = Intent(this@CheckoutActivity, DashBoardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        firestoreclass().getAllProductList(this@CheckoutActivity)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
}