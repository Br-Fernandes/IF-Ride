package com.example.cesar.ifride.adapters

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.cesar.ifride.MineRidesActivity
import com.example.cesar.ifride.R
import com.example.cesar.ifride.models.RideModel
import com.example.cesar.ifride.utils.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdapterRideAsPassenger(

    private val context: Context?,
    private val ridesList: Map<String, RideModel>

) : RecyclerView.Adapter<AdapterRideAsPassenger.MyViewHolder>() {

    constructor() : this(context = null, ridesList = emptyMap())

    private val adapterRide = AdapterRide()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rideLayout: LinearLayout = itemView.findViewById(R.id.ll_ride)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rideLayout = LayoutInflater.from(parent.context).inflate(R.layout.ride_adapter, parent, false)
        return MyViewHolder(rideLayout)
    }

    override fun getItemCount() = ridesList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        for ((key, ride) in ridesList) {
            putRide(holder.rideLayout,key, ride)
        }
    }

    private fun putRide(rideLayout: LinearLayout ,key: String,ride: RideModel) {
        Util.linearLayoutAnimator(context!!,rideLayout)

        rideLayout.addView(adapterRide.setDateAndPrice(this.context,ride))
        rideLayout.addView(adapterRide.setDriverAndSeats(this.context,ride))
        rideLayout.addView(setCancelRideBtn(key, ride))
    }

    private fun setCancelRideBtn(key: String,ride: RideModel): LinearLayout {
        var linearLayout = Util.standardLinearLayout(context)
        var confirmBtn = Button(ContextThemeWrapper(context, R.style.ride_confirm_btn))

        confirmBtn.apply {
            text = resources.getText(R.string.cancel_ride_btn)

            setBackgroundResource(R.drawable.border_cancel_ride)
        }

        cancelBtnAction(confirmBtn, key, ride)

        linearLayout.gravity = Gravity.CENTER
        linearLayout.addView(confirmBtn)

        return linearLayout
    }

    private fun cancelBtnAction(cancelBtn: Button, key: String,ride: RideModel) {
        cancelBtn.setOnClickListener {
            val db = Firebase.firestore
            val auth = FirebaseAuth.getInstance()
            val currentUserEmail = auth.currentUser!!.email

            val query = db.collection("Users").whereEqualTo("email", currentUserEmail)
            query.get().addOnSuccessListener { querySnapshot ->
                val userDocument = querySnapshot.documents[0]

                db.collection("Rides").document(key).get().addOnSuccessListener {querySnapshot2 ->
                    val availableCarSeatsUpdated = querySnapshot2.get("availableCarSeats").toString().toInt() + 1
                    val passengers = querySnapshot2.get("passengers") as? ArrayList<String> ?: arrayListOf()
                    passengers.remove(userDocument.get("registration"))

                    val update = hashMapOf<String, Any>(
                        "availableCarSeats" to availableCarSeatsUpdated,
                        "passengers" to passengers
                    )

                    db.collection("Rides").document(key).update(update)
                    MineRidesActivity.getInstance()!!.seeRidesAsPassenger()
                }
                .addOnFailureListener { e ->
                    Log.d("TAG", "Falha ao encontrar documento pela key")
                }
            }
            .addOnFailureListener { e ->
                Log.d("TAG", "Falha ao encontrar documento com o currentUserEmail")
            }
        }
    }
}