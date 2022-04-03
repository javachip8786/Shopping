package com.example.shopping.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object constants {
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"

    const val MYSHOPPAL_PREF: String = "MyShopPalPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_user"
    const val EXTRA_USER_DETAIL:  String = "extra_user_detail"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val IMAGE: String = "image"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val PRODUCT_IMAGE: String = "Product_Image"
    const val USER_ID: String = "user_id"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val CART_ITEM: String = "cart_items"
    const val PRODUCT_ID: String = "product_id"
    const val CART_QUANTITY: String = "cart_quantity"
    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val ADDRESS: String = "address"
    const val OTHER: String = "Other"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val ORDERS: String = "orders"
    const val STOCK_QUANTITY: String = "stock_quantity"

    const val EXTRA_MY_ORDER_DETAILS: String = "extra_My_Order_Details"



    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtention(activity: Activity, uri: Uri?): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}