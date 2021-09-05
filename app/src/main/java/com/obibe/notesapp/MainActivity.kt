package com.obibe.notesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.obibe.notesapp.databinding.ActivityMainBinding
import com.obibe.notesapp.fragments.HomeFragment
import com.obibe.notesapp.utils.replaceFragmentWithAnim


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragmentWithAnim(R.id.fragment_container, HomeFragment())
    }

}