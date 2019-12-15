package com.ayanot.discoveryourfantasy

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.io.File

//TODO: Test fragment without realizations
/**
 * <h3>Test fragment</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
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
            activity?.getSharedPreferences(InitActivity.LOGIN_PREF, MODE_PRIVATE)?.edit()?.clear()?.apply()
            MainActivity.TOKEN = null
            MainActivity.USER_NAME = null
            val path = activity?.filesDir?.parent
            Log.d("Files", path)
            if (path != null) {
                clearData(path)
            }
            (activity as MainActivity).refreshData()
        }
        loginView = view.findViewById(R.id.loginView)
        emailView = view.findViewById(R.id.emailView)
        loginView.text = MainActivity.CLIENT_ID
        emailView.text = MainActivity.USER_NAME
    }


    private fun clearData(path: String) {
        val directory = File(path)
        val files = directory.listFiles()
        if (files != null) {
            for (i in files.indices) {
                if (files[i].isDirectory) clearData(files[i].path)
                files[i].delete()
                Log.d("Files", "Delete file = " + files[0].path)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}
