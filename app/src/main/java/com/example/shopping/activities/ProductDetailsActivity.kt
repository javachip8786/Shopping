package com.example.shopping.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.cart_item
import com.example.shopping.R
import com.example.shopping.utils.GlideLoader
import com.example.shopping.utils.constants
import com.myshoppal.models.Product
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()
        if(intent.hasExtra(constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(constants.EXTRA_PRODUCT_ID)!!

        }
        var productOwnerId: String = ""
        if(intent.hasExtra(constants.EXTRA_PRODUCT_OWNER_ID)){
            productOwnerId = intent.getStringExtra(constants.EXTRA_PRODUCT_OWNER_ID)!!
        }
        setupActionBar()
        if(firestoreclass().getCurrentUserID() == productOwnerId){
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        }
        else{
            btn_add_to_cart.visibility = View.VISIBLE
        }
        getProductDetails()
        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }

    private fun getProductDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        firestoreclass().getProductDetails(this, mProductId)
    }

    fun productExistInCart(){
        hideProgressDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun productDetailsSuccess(product: Product){
        mProductDetails = product
//        hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadUserPicture(
            product.image,
            iv_product_detail_image
        )
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_available_quantity.text = product.stock_quantity

        if(product.stock_quantity.toInt() == 0){
            hideProgressDialog()
            btn_add_to_cart.visibility = View.GONE
            tv_product_details_available_quantity.text = resources.getString(R.string.lbl_out_of_stock)
        }
        else{
            if(firestoreclass().getCurrentUserID() == product.user_id){
                hideProgressDialog()
            }
            else{
                firestoreclass().checkIfItemExistInCart(this, mProductId)
            }
        }


    }


    private fun setupActionBar(){
        setSupportActionBar(toolbar_product_details_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)

        }
        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun addToCart(){
        val addToCart = cart_item(
            firestoreclass().getCurrentUserID(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        firestoreclass().addCartItem(this, addToCart)


    }

    fun addToCartSuccess(){
        hideProgressDialog()

        btn_go_to_cart.visibility = View.VISIBLE
        btn_add_to_cart.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.btn_add_to_cart -> {
                    startActivity(Intent(this@ProductDetailsActivity, cartListActivity::class.java))
                }
            }

        }
    }
}