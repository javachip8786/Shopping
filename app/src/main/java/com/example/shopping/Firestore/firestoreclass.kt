package com.example.shopping.Firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.shopping.Firestore.models.Address
import com.example.shopping.Firestore.models.cart_item
import com.example.shopping.Firestore.models.user
import com.example.shopping.activities.*
import com.example.shopping.ui.fragments.DashboardFragment
import com.example.shopping.ui.fragments.OrdersFragment
import com.example.shopping.ui.fragments.ProductFragment
import com.example.shopping.utils.constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myshoppal.models.Order
import com.myshoppal.models.Product
import com.myshoppal.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_checkout.*

class firestoreclass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: user) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.UserRegisterationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val u = document.toObject(user::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        constants.MYSHOPPAL_PREF,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                //Key : = LOGGED_IN_USERNAME
                //Value = firstname lastname
                editor.putString(
                    constants.LOGGED_IN_USERNAME,
                    "${u.firstName} ${u.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(u)
                    }

                    is SettingsActivity -> {
                        activity.userDetailsSuccess(u)
                    }
                }
                // END
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String,Any>){
        mFireStore.collection(constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener{
                when(activity){
                    is UserProfile -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener{e ->
                when(activity){
                    is UserProfile -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating user details",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + constants.getFileExtention(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // TODO Step 8: Pass the success result to base class.
                        // START
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfile -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                        // END
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfile -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadProductDetail(activity: AddProductActivity, productinfo: Product){
        mFireStore.collection(constants.PRODUCTS)
            .document()
            .set(productinfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener{e ->
                activity.hideProgressDialog()
            }
    }

    fun getProductList(fragment: Fragment){
        mFireStore.collection(constants.PRODUCTS)
            .whereEqualTo(constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val productList: ArrayList<Product> = ArrayList()
                for(i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }

                when(fragment){
                    is ProductFragment -> {
                        fragment.successProductListFromFirestore(productList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is ProductFragment -> {
                        fragment.hideProgressdialog()
                    }
                }

                Log.e("Get Product List", "Error while getting product list.", e)
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment){
        mFireStore.collection(constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                val productList: ArrayList<Product> = ArrayList()
                for(i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }
                fragment.successDashboardItemList(productList)
            }
            .addOnFailureListener{

                fragment.hideProgressdialog()
            }
    }

    fun getAllProductList(activity: Activity){
        mFireStore.collection(constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                val productsList: ArrayList<Product>  = ArrayList();
                for(i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when(activity){
                    is cartListActivity -> {
                        activity.successProductListFromFirestore(productsList)
                    }
                    is CheckoutActivity -> {
                        activity.successProductListFromFirestore(productsList)
                    }
                }

            }
            .addOnFailureListener{
                when(activity){
                    is cartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

            }
    }

    fun removeItemFromCart(context: Context, cart_id: String){
        mFireStore.collection(constants.CART_ITEM)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when(context){
                    is cartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener{
                when(context){
                    is cartListActivity -> {
                        context.hideProgressDialog()
                    }

                }
            }
    }

    fun updateMyCart(context: Context, card_id: String, itemHashMap: HashMap<String, Any>){
        mFireStore.collection(constants.CART_ITEM)
            .document(card_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when(context){
                    is cartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when(context) {
                    is cartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
            }
    }

    fun addAdress(activity: AddEditAddressActivity, addressInfo: Address){
        mFireStore.collection(constants.ADDRESS)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun getCartList(activity: Activity){
        mFireStore.collection(constants.CART_ITEM)
            .whereEqualTo(constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<cart_item> = ArrayList()
                for(i in document.documents){
                    val cartItem= i.toObject(cart_item::class.java)
                    if (cartItem != null) {
                        cartItem.id = i.id
                    }
                    if (cartItem != null) {
                        list.add(cartItem)
                    }
                }
                when(activity){
                    is cartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener{
                when(activity){
                    is cartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun updateAllDetail(activity: CheckoutActivity, cartList: ArrayList<cart_item>){
        val writeBatch = mFireStore.batch()
        for( cartItem in cartList){
            val productHashMap = HashMap<String, Any>()
            productHashMap[constants.STOCK_QUANTITY] =
                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(constants.PRODUCTS)
                .document(cartItem.product_id)

            writeBatch.update(documentReference, productHashMap)
        }


        for( cartItem in cartList){
            val documentReference = mFireStore.collection(constants.CART_ITEM)
                .document(cartItem.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {
            activity.allDetailsUpdatedSuccessfully()
        }
            .addOnFailureListener {
            activity.hideProgressDialog()
        }
    }

    fun getMyOrderList(fragment: OrdersFragment){
        mFireStore.collection(constants.ORDERS)
            .whereEqualTo(constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {document ->
                val list: ArrayList<Order> = ArrayList()
                for(i in document.documents){
                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id
                    list.add(orderItem)
                }
                fragment.populateOrderListInUI(list)
            }
            .addOnFailureListener{
                fragment.hideProgressdialog()
            }
    }

    fun placeOrder(activity: CheckoutActivity,order: Order){
        mFireStore.collection(constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlaceSuccess()
            }
            .addOnFailureListener{
                activity.hideProgressDialog()
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String ){
        mFireStore.collection(constants.CART_ITEM)
            .whereEqualTo(constants.USER_ID, getCurrentUserID())
            .whereEqualTo(constants.PRODUCT_ID,productId)
            .get()
            .addOnSuccessListener { document ->
                if((document.documents.size > 0)){
                    activity.productExistInCart()
                }
                else{
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener{
                activity.hideProgressDialog()
            }
    }

    fun deleteProduct(fragment: ProductFragment, productId : String){
        mFireStore.collection(constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.ProductDeleteSuccess()
            }
            .addOnFailureListener{
                fragment.hideProgressdialog()
            }
    }

    fun addCartItem(activity: ProductDetailsActivity, addToCart: cart_item){
        mFireStore.collection(constants.CART_ITEM)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }



    fun getAddessesList(activity: AddressListActivity){
        mFireStore.collection(constants.ADDRESS)
            .whereEqualTo(constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val addressList: ArrayList<Address> = ArrayList()
                for(i in document.documents){
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }
                activity.successAddressListFromFireStore(addressList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(constants.ADDRESS)
            .document(addressId)

            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(constants.ADDRESS)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String){
        mFireStore.collection(constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                if(product!=null){
                    activity.productDetailsSuccess(product)
                }

            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

}