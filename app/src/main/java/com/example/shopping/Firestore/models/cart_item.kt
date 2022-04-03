package com.example.shopping.Firestore.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class cart_item(
    val user_id: String = "",
    val product_id: String = "",
    val title: String = "",
    val price: String = "",
    val image: String = "",
    var cart_quantity: String = "",
    var stock_quantity: String = "",
    var id: String = "",
) : Parcelable