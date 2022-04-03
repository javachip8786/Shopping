package com.example.shopping.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.cart_item
import com.example.shopping.R
import com.example.shopping.adapters.CartItemListAdapter
import com.example.shopping.utils.constants
import com.myshoppal.models.Product
import kotlinx.android.synthetic.main.activity_cart_list.*

class cartListActivity : BaseActivity() {

    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartListItem: ArrayList<cart_item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()
        btn_checkout.setOnClickListener {
            val intent = Intent(this@cartListActivity, AddressListActivity::class.java)
            intent.putExtra(constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    fun successCartItemsList(cartList : ArrayList<cart_item>){
        hideProgressDialog()
        for(product in mProductList){
            for(cart in cartList){
                if(product.product_id == cart.product_id){
                    cart.stock_quantity = product.stock_quantity
                    if(product.stock_quantity.toInt() == 0){
                        cart.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItem = cartList

        if(mCartListItem.size > 0){
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@cartListActivity)
            rv_cart_items_list.setHasFixedSize(true)
            val cartListAdapter = CartItemListAdapter(this@cartListActivity, mCartListItem, true)
            rv_cart_items_list.adapter = cartListAdapter
            var subTotal: Double= 0.0
            for(item in mCartListItem){
                val avaiableQuantity = item.stock_quantity.toInt()
                if(avaiableQuantity > 0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }

            }
            tv_sub_total.text = "$${subTotal}"
            tv_shipping_charge.text = "$10.0"
            if(subTotal > 0){
                ll_checkout.visibility = View.VISIBLE
                val total = subTotal + 10
                tv_total_amount.text = "$${total}"
            }else{
                ll_checkout.visibility = View.GONE
            }
        }
        else{
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemList()
    }

    fun itemRemovedSuccess(){
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.msg_item_removed_successfully), Toast.LENGTH_SHORT).show()
        getCartItemList()
    }

    fun successProductListFromFirestore(productslist: ArrayList<Product>){
//        hideProgressDialog()
        mProductList = productslist
        getCartItemList()

    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        firestoreclass().getAllProductList(this)
    }

    private fun getCartItemList(){
//        showProgressDialog(resources.getString(R.string.please_wait))
        firestoreclass().getCartList(this@cartListActivity)
    }

    override fun onResume() {
        super.onResume()
//        getCartItemList()
        getProductList()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
}