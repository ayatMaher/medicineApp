package com.example.medicineapplication.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.FragmentProfileBinding


@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //status bar
        val window = requireActivity().window
        window.statusBarColor= ContextCompat.getColor(requireContext(),R.color.primary_color)

//       // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // click
        ClickToButton()
        return root
    }
    //click fun
    private fun ClickToButton(){
        binding.profileLayout.setOnClickListener {
            //go to profile page
            Log.e("Profile Page", "Go To Profile Page")
        }
        binding.questionLayout.setOnClickListener {
            //go to Question page
            Log.e("Question Page", "Go To Question Page")
        }
        binding.settingLayout.setOnClickListener {
            //go to Setting page
            Log.e("Setting Page", "Go To Setting Page")
        }
        binding.infoLayout.setOnClickListener {
            //go to Info page
            Log.e("Info Page", "Go To Info Page")
        }
        binding.rateLayout.setOnClickListener {
            //go to rate page
            Log.e("rate Page", "Go To rate Page")
        }
        binding.locationLayout.setOnClickListener {
            //go to Location page
            Log.e("Location Page", "Go To Location Page")
        }
        binding.favoriteLayout.setOnClickListener {
            //go to Favorite page
            findNavController().navigate(R.id.navigation_favorite)
            Log.e("Favorite Page", "Go To Favorite Page")
        }
        binding.deleteAccount.setOnClickListener {
            //delete Account
            Log.e("Delete Account", "Delete Account" )
        }
        binding.logout.setOnClickListener {
            //log out
            Log.e("Log out", "Log Out" )
        }
    }

}