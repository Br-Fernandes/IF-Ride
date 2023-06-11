package com.example.cesar.ifride

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectionButtonsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectionButtonsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_selection_buttons, container, false)

        linearLayout = view.findViewById(R.id.ll_options)
        textView1 = view.findViewById(R.id.txt_option1)
        textView2 = view.findViewById(R.id.txt_option2)

        changeViewColors(textView1)

        textView1.setOnClickListener { changeViewColors(textView1) }
        textView2.setOnClickListener { changeViewColors(textView2) }

        return view
    }


    private fun changeViewColors(textView: TextView) {
        val currentIndex = linearLayout.indexOfChild(textView)
        val previousTextView: TextView? = linearLayout.getChildAt(currentIndex - 1) as? TextView
        val nextTextView: TextView? = linearLayout.getChildAt(currentIndex + 1) as? TextView

        if (textView.id == R.id.txt_option1) {
            if (nextTextView != null) {
                textView.setBackgroundResource(R.drawable.border_directions_left_selected)
                nextTextView.setTextColor(resources.getColor(R.color.black))
                nextTextView.setBackgroundResource(R.drawable.border_directions_right)
                nextTextView.setTextColor(resources.getColor(R.color.white))
            }
        } else {
            if (previousTextView != null) {
                textView.setBackgroundResource(R.drawable.border_directions_right_selected)
                textView.setTextColor(resources.getColor(R.color.black))
                previousTextView.setBackgroundResource(R.drawable.border_directions_left)
                previousTextView.setTextColor(resources.getColor(R.color.white))
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectionButtonsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectionButtonsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}