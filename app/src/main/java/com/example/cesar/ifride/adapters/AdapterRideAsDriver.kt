package com.example.cesar.ifride.adapters

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cesar.ifride.R
import com.example.cesar.ifride.models.RideModel
import com.example.cesar.ifride.utils.Util
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdapterRideAsDriver(
    private val context: Context?,
    private val ridesList: Map<String, RideModel>
) : RecyclerView.Adapter<AdapterRideAsDriver.MyViewHolder>() {

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
        val (key, ride) = ridesList.entries.elementAt(position)
        putRide(holder.rideLayout, key, ride)
    }

    private fun putRide(rideLayout: LinearLayout, key: String, ride: RideModel) {
        Util.linearLayoutAnimator(context!!,rideLayout)

        rideLayout.addView(adapterRide.setDateAndPrice(this.context,ride))
        rideLayout.addView(setSeats(ride))
        rideLayout.addView(setCancelRideBtn(key, ride))
    }

    private fun setSeats(ride: RideModel): LinearLayout {
        var llCarSeatsLayout = Util.standardLinearLayout(context)
        llCarSeatsLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            Util.dpToPx(this.context, 80f).toInt()
        ).apply {
            marginStart = Util.dpToPx(context, 10f).toInt()
            marginEnd = Util.dpToPx(context, 10f).toInt()
        }

        var carSeats = TextView(context)
        var carSeatsLabel = TextView(context)

        carSeatsLabel.apply {
            text = context!!.getString(R.string.car_seats_label)
            setTextAppearance(R.style.ride_label_text)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        carSeats.apply {
            text = ride.availableCarSeats.toString()
            setTextAppearance(R.style.ride_value_text)
            gravity = Gravity.CENTER
        }

        llCarSeatsLayout.orientation = LinearLayout.VERTICAL
        llCarSeatsLayout.setBackgroundResource(R.drawable.border_ride_informations)
        llCarSeatsLayout.addView(carSeatsLabel)
        llCarSeatsLayout.addView(carSeats)

        return llCarSeatsLayout
    }

    private fun setCancelRideBtn(key: String, ride: RideModel): View? {
        var linearLayout = Util.standardLinearLayout(context)
        var cancelBtn = Button(ContextThemeWrapper(context, R.style.ride_confirm_btn))

        cancelBtn.apply {
            text = resources.getText(R.string.cancel_ride_btn)

            setBackgroundResource(R.drawable.border_cancel_ride)
        }

        cancelBtnAction(cancelBtn, key, ride)

        linearLayout.gravity = Gravity.CENTER
        linearLayout.addView(cancelBtn)

        return linearLayout
    }

    private fun cancelBtnAction(cancelBtn: Button, key: String, ride: RideModel) {
        cancelBtn.setOnClickListener {
            val db = Firebase.firestore

            db.collection("Rides").document(key).delete().addOnSuccessListener {
                Log.d("TAG", "Carona excluida com sucesso")
            }
            .addOnFailureListener { e ->
                Log.d("TAG", "Erro ao excluir carona", e)
            }
        }
    }


}