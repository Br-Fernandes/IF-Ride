package com.example.cesar.ifride.adapters

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.ContextThemeWrapper

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cesar.ifride.R
import com.example.cesar.ifride.models.RideModel
import com.example.cesar.ifride.utils.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AdapterRide(

    private val context: Context?,
    private val ridesList: Map<String, RideModel>

) : RecyclerView.Adapter<AdapterRide.MyViewHolder>() {

    constructor() : this(context = null, ridesList = emptyMap())

    var isOpened = false
    var db = Firebase.firestore

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rideLayout: LinearLayout = itemView.findViewById(R.id.ll_ride)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rideLayout = LayoutInflater.from(parent.context).inflate(R.layout.ride_adapter, parent, false)
        return  MyViewHolder(rideLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val (key, ride) = ridesList.entries.elementAt(position)
        putRide(this.context ,holder.rideLayout, key, ride)
    }

    override fun getItemCount() = ridesList.size

    public fun putRide(context: Context?,linearLayout: LinearLayout, key: String, ride: RideModel) {
        Util.removeLinearLayoutChildren(linearLayout)

        val dateHourLayout = Util.standardLinearLayout(context!!)
        val priceLayout = Util.standardLinearLayout(context)

        val dateHourRide = TextView(context)
        val dateHourRideLabel = TextView(context)
        val priceRide = TextView(context)
        val priceRideLabel = TextView(context)


        dateHourRideLabel.apply {
            text = context.getString(R.string.date_hour_label)
            setTextAppearance(R.style.ride_label_text)
            setTextColor(context.getColor(R.color.dark_gray))
        }
        dateHourRide.apply {
            text = Util.formatDate(ride.dateHour)
            setTextAppearance(R.style.ride_value_text)
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.light_gray))
        }
        dateHourLayout.addView(dateHourRideLabel)
        dateHourLayout.addView(dateHourRide)

        priceRideLabel.apply {
            text = context.getString(R.string.price_label)
            setTextAppearance(R.style.ride_label_text)
            setTextColor(context.getColor(R.color.dark_gray))
        }
        priceRide.apply {
            text = "R$ ${ride.price}"
            setTextAppearance(R.style.ride_value_text)
            setTextColor(context.getColor(R.color.light_gray))
            gravity = Gravity.CENTER
        }
        priceLayout.addView(priceRideLabel)
        priceLayout.addView(priceRide)

        dateHourLayout.orientation = LinearLayout.VERTICAL
        priceLayout.orientation = LinearLayout.VERTICAL

        linearLayout.addView(dateHourLayout)
        linearLayout.addView(priceLayout)

        onOpenRideInformatins(context,linearLayout, key ,ride)
    }

    public fun onOpenRideInformatins( context: Context,rideLayout: LinearLayout, key: String, ride: RideModel) {
        rideLayout.setOnClickListener {
            if (!isOpened){
                val animator = ValueAnimator.ofInt(
                    Util.dpToPx(context!!, 90f).toInt(),
                    Util.dpToPx(context, 450f).toInt()
                )
                animator.duration = 800

                animator.addUpdateListener { animation ->
                    val height = animation.animatedValue as Int
                    rideLayout.layoutParams.height = height
                    rideLayout.requestLayout()
                }
                animator.start()
                Util.removeLinearLayoutChildren(rideLayout)
                putRideInformations(context,rideLayout, key ,ride)
                isOpened = true
            }
        }
    }

    public fun putRideInformations(context: Context, rideLayout: LinearLayout, key: String, ride: RideModel) {
        rideLayout.orientation = LinearLayout.VERTICAL
        var closeBtn = setCloseBtn(context,rideLayout, key ,ride)
        var dateAndPrice =  setDateAndPrice(context,ride)
        var driverAndSeats = setDriverAndSeats(context,ride)
        var confirmBtn = setConfirmBtn(context, key ,ride)

        rideLayout.addView(closeBtn)
        rideLayout.addView(dateAndPrice)
        rideLayout.addView(driverAndSeats)
        rideLayout.addView(confirmBtn)
    }

    public fun setCloseBtn(context: Context, rideLayout: LinearLayout, key: String , ride: RideModel): LinearLayout {
        var linearLayout = Util.standardLinearLayout(this.context)
        linearLayout.apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Util.dpToPx(context, 40f).toInt()
            )
        }

        var imageView = ImageView(this.context)
        imageView.apply {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.close_x))
            background = resources.getDrawable(R.drawable.circle_border)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            foregroundGravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                Util.dpToPx(context, 25f).toInt(),
                Util.dpToPx(context, 25f).toInt()
            ).apply {
                setMargins(
                    Util.dpToPx(context, 2f).toInt(),
                    Util.dpToPx(context, 2f).toInt(),
                    Util.dpToPx(context, 2f).toInt(),
                    Util.dpToPx(context, 2f).toInt()
                )
            }
        }
        onCloseRideInformations(imageView, rideLayout, key ,ride)
        linearLayout.addView(imageView)

        return linearLayout
    }

    public fun setDateAndPrice(context: Context?, ride: RideModel): LinearLayout {
        var linearLayout = Util.standardLinearLayout(context)
        var llDateHourLayout = Util.standardLinearLayout(context)
        var llPriceLayout = Util.standardLinearLayout(context)

        var dateHour = TextView(context)
        var dateHourLabel = TextView(context)
        var price = TextView(context)
        var priceLabel = TextView(context)


        dateHourLabel.apply {
            text = context!!.getString(R.string.date_hour_label)
            setTextAppearance(R.style.ride_label_text)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        dateHour.apply {
            text = Util.formatDate(ride.dateHour)
            setTextAppearance(R.style.ride_value_text)
            gravity = Gravity.CENTER
        }

        priceLabel.apply {
            text = context!!.getString(R.string.price_label)
            setTextAppearance(R.style.ride_label_text)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        price.apply {
            text = "R$ ${ride.price}"
            setTextAppearance(R.style.ride_value_text)
            gravity = Gravity.CENTER
        }

        llDateHourLayout.orientation = LinearLayout.VERTICAL
        llDateHourLayout.setBackgroundResource(R.drawable.border_ride_informations)
        llDateHourLayout.addView(dateHourLabel)
        llDateHourLayout.addView(dateHour)

        llPriceLayout.orientation = LinearLayout.VERTICAL
        llPriceLayout.setBackgroundResource(R.drawable.border_ride_informations)
        llPriceLayout.addView(priceLabel)
        llPriceLayout.addView(price)

        linearLayout.addView(llDateHourLayout)
        linearLayout.addView(llPriceLayout)

        return linearLayout
    }

    public fun setDriverAndSeats(context: Context?, ride: RideModel): LinearLayout {
        var linearLayout = Util.standardLinearLayout(context)
        var llDriverLayout = Util.standardLinearLayout(context)
        var llCarSeatsLayout = Util.standardLinearLayout(context)

        var driver = TextView(context)
        var driverLabel = TextView(context)
        var carSeats = TextView(context)
        var carSeatsLabel = TextView(context)

        driverLabel.apply {
            text = context!!.getString(R.string.driver_label)
            setTextAppearance(R.style.ride_label_text)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        driver.apply {
            db.collection("Users").whereEqualTo("registration", ride.driverRegistration)
                .get().addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.isNotEmpty()) {
                        val driverName = querySnapshot.documents[0].get("name").toString()
                        text = driverName
                    }
                }
            setTextAppearance(R.style.ride_value_text)
            gravity = Gravity.CENTER
        }

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

        llDriverLayout.orientation = LinearLayout.VERTICAL
        llDriverLayout.setBackgroundResource(R.drawable.border_ride_informations)
        llDriverLayout.addView(driverLabel)
        llDriverLayout.addView(driver)

        llCarSeatsLayout.orientation = LinearLayout.VERTICAL
        llCarSeatsLayout.setBackgroundResource(R.drawable.border_ride_informations)
        llCarSeatsLayout.addView(carSeatsLabel)
        llCarSeatsLayout.addView(carSeats)

        linearLayout.addView(llDriverLayout)
        linearLayout.addView(llCarSeatsLayout)

        return linearLayout
    }

    private fun setConfirmBtn(context: Context, key: String, ride: RideModel): LinearLayout {
        var linearLayout = Util.standardLinearLayout(context)
        var confirmBtn = Button(ContextThemeWrapper(context, R.style.ride_confirm_btn))

        confirmBtn.apply {
            text = resources.getText(R.string.confirm_ride_btn)
            setBackgroundResource(R.drawable.border_confirm_ride)
        }

        confirmBtnAction(confirmBtn, key, ride)

        linearLayout.gravity = Gravity.CENTER
        linearLayout.addView(confirmBtn)

        return linearLayout
    }

    private fun confirmBtnAction(confirmBtn: Button, key: String, ride: RideModel) {
        confirmBtn.setOnClickListener {
            val db = Firebase.firestore
            val auth = FirebaseAuth.getInstance()
            val currentUserEmail = auth.currentUser!!.email
            Log.d("TAG", currentUserEmail.toString())

            val query = db.collection("Users").whereEqualTo("email", currentUserEmail)
            query.get().addOnSuccessListener {querySnapshot ->
                val document = querySnapshot.documents[0]
                val passengerRef = document.getString("registration")

                val updatedAvailableCarSeats = ride.availableCarSeats - 1

                val queryRide = db.collection("Rides").document(key)
                queryRide.get().addOnSuccessListener { documentSnapshot ->
                    val passengers = documentSnapshot.get("passengers") as? ArrayList<String> ?: arrayListOf()

                    passengers.add(passengerRef!!)

                    val updates = hashMapOf<String, Any>(
                        "passengers" to passengers,
                        "availableCarSeats" to updatedAvailableCarSeats
                    )

                    db.collection("Rides").document(key).update(updates)
                        .addOnSuccessListener {
                            Log.d("TAG", "Atualizou a Carona com sucesso")
                        }
                        .addOnFailureListener { e ->
                            Log.d("TAG", "Deu errado ao atualizar a carona")
                        }

                }
            }
        }
    }

    private fun onCloseRideInformations(imageView: ImageView,rideLayout: LinearLayout, key: String ,ride: RideModel)     {
        imageView.setOnClickListener {
            if (isOpened) {
                val animator = ValueAnimator.ofInt(
                    Util.dpToPx(context!!, 450f).toInt(),
                    Util.dpToPx(context, 90f).toInt()
                )
                animator.duration = 1000

                animator.addUpdateListener { animation ->
                    val height = animation.animatedValue as Int
                    rideLayout.layoutParams.height = height
                    rideLayout.requestLayout()
                }
                animator.start()
                Util.removeLinearLayoutChildren(rideLayout)
                putRide(this.context, rideLayout, key ,ride)
                rideLayout.orientation = LinearLayout.HORIZONTAL
                isOpened = false
            }
        }
    }
}