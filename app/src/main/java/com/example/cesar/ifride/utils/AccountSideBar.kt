
import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.cesar.ifride.*
import com.example.cesar.ifride.utils.Util.Companion.auth
import com.example.cesar.ifride.utils.Util.Companion.db
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class AccountSideBar {
    val auth = FirebaseAuth.getInstance()

    companion object {
        fun configureSideBar(activity: Activity,drawerLayout: DrawerLayout) {
            drawerLayout.openDrawer(GravityCompat.START)

            drawerLayout.findViewById<ImageView>(R.id.img_arrow_back).setOnClickListener {
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            val query = db.collection("Users").whereEqualTo("email", auth.currentUser!!.email)
            query.get().addOnSuccessListener { querySnapshot ->
                val currentUserName = querySnapshot.documents[0].get("name").toString()
                var txtFullName: TextView = drawerLayout.findViewById(R.id.txt_user_name)
                val (firstName) = currentUserName.split(" ")

                txtFullName.text = "Olá, $firstName"
            }

            val navigationView = drawerLayout.findViewById<NavigationView>(R.id.navigation_view)
            navigationView.setNavigationItemSelectedListener { menuItem ->
                val context = navigationView.context

                when (menuItem.itemId) {
                    R.id.my_data -> {
                        val intent = Intent(context, MyProfileActivity::class.java)
                        context.startActivity(intent)
                        true
                    }
                    R.id.sign_out -> {
                        auth.signOut()
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        true
                    }
                    R.id.delete_account -> {
                        val alertDialogBuilder = AlertDialog.Builder(context)
                        alertDialogBuilder.setMessage(R.string.alert_dialog_message)
                        alertDialogBuilder.setPositiveButton("Sim") { dialog, which ->
                            val currentUser = auth.currentUser

                            val query = db.collection("Users").whereEqualTo("email", currentUser!!.email)
                            query.get().addOnSuccessListener { querySnapshot ->
                                querySnapshot.documents[0].reference.delete().addOnSuccessListener {
                                    currentUser.delete()
                                    val intent = Intent(context, RegisterActivity::class.java)
                                    context.startActivity(intent)

                                    activity.finish()
                                }
                            }
                        }
                        alertDialogBuilder.setNegativeButton("Não") { dialog, which ->
                            dialog.dismiss()
                        }

                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()

                        true
                    }
                    else -> false
                }
            }
        }
    }
}
