package com.example.medicineapplication.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import com.example.medicineapplication.LogInActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.databinding.FragmentProfileBinding
import androidx.core.content.edit
import com.example.medicineapplication.AboutAppActivity

import com.example.medicineapplication.CommonQuestionsActivity
import com.example.medicineapplication.EditProfileActivity
import com.example.medicineapplication.SettingActivity
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService

import com.example.medicineapplication.AppEvaluationActivity
import com.example.medicineapplication.CommonQuationsPage
import com.example.medicineapplication.EditProfile
import com.example.medicineapplication.SettingPage
//import com.example.medicineapplication.api.ApiClient
//import com.example.medicineapplication.api.ApiService

import com.example.medicineapplication.model.GenericResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary_color)

//       // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // click
        clickToButton()

        return root
    }

    //click fun
    private fun clickToButton() {
        binding.profileLayout.setOnClickListener {

            //go to profile page
            val intent= Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
        binding.questionLayout.setOnClickListener {
            //go to Question page
            val intent= Intent(requireContext(), CommonQuestionsActivity::class.java)
            startActivity(intent)
        }
        binding.settingLayout.setOnClickListener {
            //go to Setting page
            val intent= Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
        binding.infoLayout.setOnClickListener {
            //go to Info page
            val intent= Intent(requireContext(), AboutAppActivity::class.java)
            startActivity(intent)

            val intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
            Log.e("Profile Page", "Go To Profile Page")
        }
        binding.questionLayout.setOnClickListener {
            val intent = Intent(requireContext(), CommonQuationsPage::class.java)
            startActivity(intent)
            Log.e("Question Page", "Go To Question Page")
        }
        binding.settingLayout.setOnClickListener {
            val intent = Intent(requireContext(), SettingPage::class.java)
            startActivity(intent)
            Log.e("Setting Page", "Go To Setting Page")
        }
        binding.infoLayout.setOnClickListener {
            val intent = Intent(requireContext(), AboutAppActivity::class.java)
            startActivity(intent)
            Log.e("Info Page", "Go To Info Page")

        }
        binding.rateLayout.setOnClickListener {
            val intent = Intent(requireContext(), AppEvaluationActivity::class.java)
            startActivity(intent)
            Log.e("rate Page", "Go To rate Page")
        }
        binding.locationLayout.setOnClickListener {
            //go to Location page
            Log.e("Location Page", "Go To Location Page")
        }
        binding.favoriteLayout.setOnClickListener {
            //go to Favorite page
            val bundle = Bundle().apply {
                putString("page_type", "favorite_fragment")
            }
            findNavController().navigate(R.id.navigation_favorite, bundle)
        }
        binding.deleteAccount.setOnClickListener {
            //delete Account
            Log.e("Delete Account", "Delete Account")
        }
        binding.logout.setOnClickListener {
//            showLogoutConfirmationDialog()
        }
    }


    private fun logoutUser() {
        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        sharedPref.edit { clear() }

        val intent = Intent(requireContext(), LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish() // ← مهم لمنع الرجوع من الزر الخلفي
    }

//    private fun showLogoutConfirmationDialog() {
//        val dialog = AlertDialog.Builder(requireContext())
//            .setTitle("تأكيد تسجيل الخروج")
//            .setMessage("هل أنت متأكد أنك تريد تسجيل الخروج؟")
//            .setPositiveButton("نعم") { _, _ ->
//                performLogout()
//            }
//            .setNegativeButton("إلغاء", null)
//            .create()
//        dialog.show()
//
//        // تغيير لون أزرار الحوار بعد إظهاره
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
//            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//
//    }

//    private fun performLogout() {
//        val sharedPref =
//            requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
//        val token = "Bearer " + sharedPref.getString("ACCESS_TOKEN", "")
//        val apiService = ApiClient.instance.create(ApiService::class.java)
//
//        apiService.logout(token).enqueue(object : Callback<GenericResponse> {
//            override fun onResponse(
//                call: Call<GenericResponse>,
//                response: Response<GenericResponse>
//            ) {
//                Toast.makeText(requireContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
//                logoutUser()
//            }
//
//            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
//                Toast.makeText(
//                    requireContext(),
//                    "فشل الاتصال بالخادم، يتم تسجيل الخروج محليًا",
//                    Toast.LENGTH_SHORT
//                ).show()
//                logoutUser()
//            }
//        })
//    }

}