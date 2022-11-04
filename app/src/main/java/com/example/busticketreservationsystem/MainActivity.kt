package com.example.busticketreservationsystem

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.interfaces.BottomNavigationBackPressed

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            replace(R.id.main_fragment_container, RegisterFragment())
        }

//        val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus", MODE_PRIVATE)
//
//        val editor: SharedPreferences.Editor = writeSharedPreferences.edit()
//
//        if(savedInstanceState == null) {
//            when (writeSharedPreferences.getString("status", "")) {
//                "" -> {
//                    editor.putString("status", LoginStatus.NEW.name)
//                    editor.commit()
//                    supportFragmentManager.commit {
//                        replace(R.id.main_fragment_container, RegisterFragment())
//                    }
//                }
//                LoginStatus.SKIPPED.name -> {
//                    supportFragmentManager.commit {
//                        replace(R.id.main_fragment_container, HomePageFragment())
//                    }
//                }
//                LoginStatus.LOGGED_IN.name -> {
//                    supportFragmentManager.commit {
//                        replace(R.id.main_fragment_container, HomePageFragment())
//                    }
//                }
//                LoginStatus.NEW.name -> {
//                    supportFragmentManager.commit {
//                        replace(R.id.main_fragment_container, RegisterFragment())
//                    }
//                }
//            }
//        }
//
    }

//    override fun onBackPressed() {
//        val fragment =
//            this.supportFragmentManager.findFragmentById(R.id.homePageFragmentContainer)
//        (fragment as? BottomNavigationBackPressed)?.onBackPressed()?.not()?.let {
//            super.onBackPressed()
//        }
//    }
}