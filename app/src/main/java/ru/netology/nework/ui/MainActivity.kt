package ru.netology.nework.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.ActivityMainBinding
import ru.netology.nework.viewModel.AuthViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    override fun onStart() {
        super.onStart()
        when(findNavController(R.id.navHostFragment).currentDestination?.id) {
            R.id.welcomeFragment -> {
                if (viewModel.authenticated) {
                    findNavController(R.id.navHostFragment).navigate(R.id.welcomeFragmentToPostsWallFragment)
                }
            }
            else -> Unit
        }
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}