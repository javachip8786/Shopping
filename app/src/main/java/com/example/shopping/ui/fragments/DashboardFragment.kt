package com.example.shopping.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.R
import com.example.shopping.activities.ProductDetailsActivity
import com.example.shopping.activities.SettingsActivity
import com.example.shopping.activities.cartListActivity
import com.example.shopping.adapters.DashboardItemsListAdapter
import com.example.shopping.adapters.MyProductListAdapter
import com.example.shopping.databinding.FragmentDashboardBinding
import com.example.shopping.utils.constants
import com.myshoppal.models.Product
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_product.*
import kotlinx.android.synthetic.main.item_dashboard_layout.*

class DashboardFragment : BaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, cartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemList(dashboardItemList: ArrayList<Product>){
        hideProgressdialog()
        if(dashboardItemList.size > 0){
            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager = LinearLayoutManager(activity)
            rv_dashboard_items.setHasFixedSize(true)
            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemList)
            rv_dashboard_items.adapter = adapter

            adapter.setOnClickListener(object: DashboardItemsListAdapter.OnClickListener{
                override fun onClick(position: Int, product: Product){
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(constants.EXTRA_PRODUCT_ID, product.product_id)
                    startActivity(intent)
                }
            })
        }
        else{
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    fun getDashboardItemsList(){
        showProgrssDialog(resources.getString(R.string.please_wait))
        firestoreclass().getDashboardItemsList(this@DashboardFragment)
    }
}