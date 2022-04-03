package com.example.shopping.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.activities.AddProductActivity
import com.example.shopping.R
import com.example.shopping.adapters.MyProductListAdapter
import com.myshoppal.models.Product
import kotlinx.android.synthetic.main.fragment_product.*

class ProductFragment : BaseFragment() {

    fun deleteProduct(productID: String){
        showProgrssDialog(resources.getString(R.string.please_wait))
//        firestoreclass().deleteProduct(this, productID)
    }

    fun ProductDeleteSuccess(){
        hideProgressdialog()
        getPorductListFromFirestore()
    }

    fun successProductListFromFirestore(productslist: ArrayList<Product>){
        hideProgressdialog()
        if(productslist.size > 0){
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)
            val adapterProduct = MyProductListAdapter(requireActivity(), productslist, this)
            rv_my_product_items.adapter = adapterProduct
        }
        else{
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    private fun getPorductListFromFirestore(){
        showProgrssDialog(resources.getString(R.string.please_wait))
        firestoreclass().getProductList(this)

    }

    override fun onResume() {
        super.onResume()
        getPorductListFromFirestore()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_product, container, false)

        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add_product) {
            startActivity(Intent(activity, AddProductActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}