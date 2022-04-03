package com.example.shopping.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.R
import com.myshoppal.models.Order
import com.myshoppal.ui.adapters.MyOrdersListAdapter
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.fragment_product.*

class OrdersFragment : BaseFragment() {

//    private lateinit var notificationsViewModel: NotificationsViewModel
//    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root: View = inflater.inflate(R.layout.fragment_orders, container, false)

        return root
    }

    fun populateOrderListInUI(ordersList: ArrayList<Order>){
        hideProgressdialog()
        if(ordersList.size > 0){
            rv_my_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE

            rv_my_order_items.layoutManager = LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)

            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            rv_my_order_items.adapter = myOrdersAdapter
        }
        else{
            rv_my_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }
    }

    private fun getMyOrderList(){
        showProgrssDialog(resources.getString(R.string.please_wait))
        firestoreclass().getMyOrderList(this@OrdersFragment)
    }

    override fun onResume() {
        super.onResume()
        getMyOrderList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}