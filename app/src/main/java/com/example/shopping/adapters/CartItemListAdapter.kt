package com.example.shopping.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping.Firestore.firestoreclass
import com.example.shopping.Firestore.models.cart_item
import com.example.shopping.R
import com.example.shopping.activities.cartListActivity
import com.example.shopping.utils.GlideLoader
import com.example.shopping.utils.constants
import kotlinx.android.synthetic.main.item_cart_layout.view.*

class CartItemListAdapter(private val context: Context,
                          private var list: ArrayList<cart_item>,
                          private val updateCartItem: Boolean
)  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cart_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image,holder.itemView.iv_cart_item_image)
            holder.itemView.tv_cart_item_price.text = "${model.price}"
            holder.itemView.tv_cart_item_title.text = model.title
            holder.itemView.tv_cart_quantity.text = model.cart_quantity

            if (model.cart_quantity == "0") {
                holder.itemView.ib_remove_cart_item.visibility = View.GONE
                holder.itemView.ib_add_cart_item.visibility = View.GONE

                if (updateCartItem) {
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                } else {
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                }

                holder.itemView.tv_cart_quantity.text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.itemView.tv_cart_quantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            } else {
                if (updateCartItem) {
                    holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_add_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_delete_cart_item.visibility = View.VISIBLE
                } else {

                    holder.itemView.ib_remove_cart_item.visibility = View.GONE
                    holder.itemView.ib_add_cart_item.visibility = View.GONE
                    holder.itemView.ib_delete_cart_item.visibility = View.GONE
                }
//                holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
//                holder.itemView.ib_add_cart_item.visibility = View.VISIBLE

                holder.itemView.tv_cart_quantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorDarkGrey
                    )
                )
            }

            holder.itemView.ib_delete_cart_item.setOnClickListener{
                when(context){
                    is cartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }
                firestoreclass().removeItemFromCart(context,model.id)
            }

            holder.itemView.ib_remove_cart_item.setOnClickListener {
                if(model.cart_quantity == "1"){
                    firestoreclass().removeItemFromCart(context, model.id)
                }
                else{
                    val cartQuantity: Int = model.cart_quantity.toInt()
                    val itemHashMap = HashMap<String,Any>()
                    itemHashMap[constants.CART_QUANTITY] = (cartQuantity - 1).toString()
                    if(context is cartListActivity){
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                    firestoreclass().updateMyCart(context, model.id, itemHashMap)
                }
            }

            holder.itemView.ib_add_cart_item.setOnClickListener {
                val cartQuantity: Int = model.cart_quantity.toInt()

                if(cartQuantity < model.stock_quantity.toInt()){
                    val itemHashMap = HashMap<String,Any>()
                    itemHashMap[constants.CART_QUANTITY] = (cartQuantity + 1).toString()
                    if(context is cartListActivity){
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                    firestoreclass().updateMyCart(context, model.id, itemHashMap)
                }
                else{
                    if(context is cartListActivity){
                        context.showErrorSnackBar(
                            "No more Available", true
                        )
                    }
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}