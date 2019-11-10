package com.ayanot.discoveryourfantasy

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

//TODO: Test fragment without realizations
class ProfileFragment : Fragment() {
    lateinit var disconnectButton: Button
    lateinit var loginView: TextView
    lateinit var emailView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        disconnectButton = view.findViewById(R.id.disconnectButton)
        disconnectButton.setOnClickListener {
            activity?.getSharedPreferences(InitActivity.TOKEN_PREF, MODE_PRIVATE)?.edit()?.clear()?.apply()
        }
        loginView = view.findViewById(R.id.loginView)
        emailView = view.findViewById(R.id.emailView)
        loginView.text = MainActivity.CLIENT_ID
        emailView.text = MainActivity.USER_NAME
    }
}
