package com.dalftech.rydegorider.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.daasuu.cat.CountAnimationTextView
import com.dalftech.rydegorider.Activity.WelcomeScreenActivity
import com.dalftech.rydegorider.Helper.CustomDialog
import com.dalftech.rydegorider.Helper.SharedHelper.getKey
import com.dalftech.rydegorider.Helper.SharedHelper.putKey
import com.dalftech.rydegorider.Helper.URLHelper
import com.dalftech.rydegorider.R
import com.dalftech.rydegorider.RydeGoDriverApplication
import com.dalftech.rydegorider.RydeGoDriverApplication.Companion.trimMessage
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.util.*

/**
 * @Developer android
 * @Company android
 */
/**
 * A simple [Fragment] subclass.
 */
class SummaryFragment : Fragment(), View.OnClickListener {

    var imgBack: ImageView? = null
    var cardLayout: LinearLayout? = null
    var noOfRideTxt: CountAnimationTextView? = null
    var scheduleTxt: CountAnimationTextView? = null
    var revenueTxt: CountAnimationTextView? = null
    var cancelTxt: CountAnimationTextView? = null
    var ridesCard: CardView? = null
    var cancelCard: CardView? = null
    var scheduleCard: CardView? = null
    var revenueCard: CardView? = null
    var currencyTxt: TextView? = null
    var rides = 0
    var revenue = 0
    var schedule = 0
    var cancel = 0
    var doubleRevenue: Double? = null
    var TAG = "SummaryFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_summary, container, false)

        findViewsById(view)
        setClickListeners()
        providerSummary
        return view
    }

    private fun setClickListeners() {
        imgBack!!.setOnClickListener(this)
        revenueCard!!.setOnClickListener(this)
        ridesCard!!.setOnClickListener(this)
        revenueCard!!.setOnClickListener(this)
        scheduleCard!!.setOnClickListener(this)
    }

    private fun findViewsById(view: View) {
        imgBack = view.findViewById<View>(R.id.backArrow) as ImageView
        cardLayout = view.findViewById<View>(R.id.card_layout) as LinearLayout
        noOfRideTxt = view.findViewById<View>(R.id.no_of_rides_txt) as CountAnimationTextView
        scheduleTxt = view.findViewById<View>(R.id.schedule_txt) as CountAnimationTextView
        cancelTxt = view.findViewById<View>(R.id.cancel_txt) as CountAnimationTextView
        revenueTxt = view.findViewById<View>(R.id.revenue_txt) as CountAnimationTextView
        currencyTxt = view.findViewById<View>(R.id.currency_txt) as TextView
        revenueCard = view.findViewById<View>(R.id.revenue_card) as CardView
        scheduleCard = view.findViewById<View>(R.id.schedule_card) as CardView
        ridesCard = view.findViewById<View>(R.id.rides_card) as CardView
        cancelCard = view.findViewById<View>(R.id.cancel_card) as CardView
    }

    override fun onClick(v: View) {
        val manager = fragmentManager
        val transaction = manager!!.beginTransaction()
        var fragment = Fragment()
        val bundle = Bundle()
        bundle.putString("toolbar", "true")
        when (v.id) {
            R.id.backArrow -> //                getFragmentManager().popBackStackImmediate();
                activity!!.onBackPressed()
            R.id.rides_card -> {
                fragment = PastTrips()
                fragment.setArguments(bundle)
                transaction.replace(R.id.content, fragment, TAG)
                transaction.addToBackStack(TAG)
                transaction.commit()
            }
            R.id.schedule_card -> {
                fragment = OnGoingTrips()
                fragment.setArguments(bundle)
                transaction.replace(R.id.content, fragment, TAG)
                transaction.addToBackStack(TAG)
                transaction.commit()
            }
            R.id.revenue_card -> {
                fragment = EarningsFragment()
                transaction.replace(R.id.content, fragment, TAG)
                transaction.addToBackStack(TAG)
                transaction.commit()
            }
            R.id.cancel_card -> {
                fragment = PastTrips()
                fragment.setArguments(bundle)
                transaction.replace(R.id.content, fragment, TAG)
                transaction.addToBackStack(TAG)
                transaction.commit()
            }
        }
    }

    private fun setDetails() {

        val txtAnim = AnimationUtils.loadAnimation(activity, R.anim.txt_size)
        if (schedule > 0) {
            scheduleTxt!!.setAnimationDuration(500)
                    .countAnimation(0, schedule)
        } else {
            scheduleTxt!!.text = "0"
        }
        if (revenue > 0) {
            revenueTxt!!.setAnimationDuration(500)
                    .countAnimation(0, revenue)
        } else {
            revenueTxt!!.text = "0"
        }
        if (rides > 0) {
            noOfRideTxt!!.setAnimationDuration(500)
                    .countAnimation(0, rides)
        } else {
            noOfRideTxt!!.text = "0"
        }
        if (cancel > 0) {
            cancelTxt!!.setAnimationDuration(500)
                    .countAnimation(0, cancel)
        } else {
            cancelTxt!!.text = "0"
        }
        scheduleTxt!!.startAnimation(txtAnim)
        revenueTxt!!.startAnimation(txtAnim)
        noOfRideTxt!!.startAnimation(txtAnim)
        cancelTxt!!.startAnimation(txtAnim)
        currencyTxt!!.text = getKey(context!!, "currency")
    }

   // revenue = Integer.parseInt(response.optString("revenue"))
    val providerSummary: Unit
        get() {
            run {
                val customDialog = CustomDialog(requireActivity())
                customDialog.setCancelable(false)
                customDialog.show()
                val `object` = JSONObject()
                val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.SUMMARY, `object`, Response.Listener { response ->
                    try {
                        customDialog.dismiss()
                        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
                        cardLayout?.visibility = View.VISIBLE
                        cardLayout?.startAnimation(slideUp)
                        rides = response.optString("rides").toInt()
                        schedule = response.optString("scheduled_rides").toInt()
                        cancel = response.optString("cancel_rides").toInt()
                        doubleRevenue = response.optString("revenue").toDouble()
                        revenue = doubleRevenue!!.toInt()
                        //revenue = Integer.parseInt(response.optString("revenue"));
                        slideUp.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}
                            override fun onAnimationEnd(animation: Animation) {
                                setDetails()
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    customDialog.dismiss()
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"))
                                } catch (e: Exception) {
                                    displayMessage(getString(R.string.something_went_wrong))
                                    e.printStackTrace()
                                }
                            } else if (response.statusCode == 401) {
                                GoToBeginActivity()
                            } else if (response.statusCode == 422) {
                                json = trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    displayMessage(json)
                                } else {
                                    displayMessage(getString(R.string.please_try_again))
                                }
                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down))
                            } else {
                                displayMessage(getString(R.string.please_try_again))
                            }
                        } catch (e: Exception) {
                            displayMessage(getString(R.string.something_went_wrong))
                            e.printStackTrace()
                        }
                    } else {
                        if (error is NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet))
                        } else if (error is NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet))
                        } else if (error is TimeoutError) {
                            providerSummary
                        }
                    }
                })

                {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): MutableMap<String, String>? {
                        val headers = HashMap<String, String>()
                        headers["X-Requested-With"] = "XMLHttpRequest"
                        headers["Authorization"] = "Bearer " + getKey(context!!, "access_token")
                        Log.e("", "Access_Token" + getKey(context!!, "access_token"))
                        return headers
                    }
                }
                RydeGoDriverApplication.instance?.addToRequestQueue(jsonObjectRequest)
            }
        }




    fun displayMessage(toastString: String?) {
        Snackbar.make(view!!, toastString!!, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
    }

    fun GoToBeginActivity() {
        putKey(context!!, "loggedIn", getString(R.string.False))
        val mainIntent = Intent(context, WelcomeScreenActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainIntent)
        activity!!.finish()
    }
}