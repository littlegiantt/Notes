package com.obibe.notesapp.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.obibe.notesapp.R

fun Fragment.showToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.replaceFragmentWithAnim(container: Int, fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(
            android.R.anim.slide_in_left,
            0,
            0,
            android.R.anim.slide_out_right
        )
        .replace(container, fragment)
        .commit()
}

fun Fragment.replaceFragmentWithAnim(container: Int, fragment: Fragment) {
    this.fragmentManager
        ?.beginTransaction()
//        ?.setCustomAnimations(
//            android.R.anim.slide_out_right,
//            android.R.anim.slide_in_left
//        )
        ?.replace(container, fragment)
        ?.commit()
}

fun Fragment.replaceFragmentWithBackStack(container: Int, fragment: Fragment) {
    this.fragmentManager
        ?.beginTransaction()
        ?.setCustomAnimations(
            R.anim.slide_in_bottom,
            0,
            0,
            R.anim.slide_out_bottom
            )
        ?.replace(container, fragment)
        ?.addToBackStack(fragment.javaClass.simpleName)
        ?.commit()
}