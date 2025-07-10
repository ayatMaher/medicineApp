package com.example.medicineapplication.fragment

import android.content.Context.MODE_PRIVATE
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
import com.bumptech.glide.Glide
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.api.ApiService
import com.example.medicineapplication.AboutAppActivity
import com.example.medicineapplication.AppEvaluationActivity
import com.example.medicineapplication.CommonQuestionsActivity
import com.example.medicineapplication.EditProfileActivity
import com.example.medicineapplication.SettingActivity
import com.example.medicineapplication.model.DeleteResponse
import com.example.medicineapplication.model.GenericResponse
import com.example.medicineapplication.model.UserResponse
import org.json.JSONObject
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
        loadUserImage()
        return root
    }

    //click fun
    private fun clickToButton() {
        binding.profileLayout.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
            Log.e("Profile Page", "Go To Profile Page")
        }
        binding.questionLayout.setOnClickListener {
            val intent = Intent(requireContext(), CommonQuestionsActivity::class.java)
            startActivity(intent)
            Log.e("Question Page", "Go To Question Page")
        }
        binding.settingLayout.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
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
            showDeleteAccountDialog()
        }
        binding.logout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
        loadUserImage()
    }

    private fun loadUserImage() {
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = "Bearer " + sharedPref.getString("ACCESS_TOKEN", "")

        val apiService = ApiClient.instance.create(ApiService::class.java)
        apiService.getCurrentUser(token).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()!!.data
                    Toast.makeText(
                        requireContext(),
                        response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    //show uaer image
                    user.image?.let {
                        Glide.with(this@ProfileFragment)
                            .load(it)
                            .placeholder(R.drawable.user)
                            .into(binding.profileImage)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()

                    if (errorBody != null && errorBody.trim().startsWith("{")) {
                        try {
                            val json = JSONObject(errorBody)
                            val errorMessage = json.optString("message", "حدث خطأ")
                            Toast.makeText(
                                requireContext(),
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()

                            if (errorMessage.contains("غير مصرح")) {
                                // 🧹 حذف التوكن
                                val sharedPref = requireActivity().getSharedPreferences(
                                    "MyAppPrefs",
                                    MODE_PRIVATE
                                )
                                sharedPref.edit { clear() }

                                // 🔁 إعادة التوجيه إلى تسجيل الدخول
                                val intent =
                                    Intent(requireContext(), LogInActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                requireActivity().finish()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "فشل في الاتصال بالخادم",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "فشل الاتصال: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun logoutUser() {
        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPref.edit { clear() }

        val intent = Intent(requireContext(), LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish() // ← مهم لمنع الرجوع من الزر الخلفي
    }

    private fun showLogoutConfirmationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("تأكيد تسجيل الخروج")
            .setMessage("هل أنت متأكد أنك تريد تسجيل الخروج؟")
            .setPositiveButton("نعم") { _, _ ->
                performLogout()
            }
            .setNegativeButton("إلغاء", null)
            .create()
        dialog.show()

        // تغيير لون أزرار الحوار بعد إظهاره
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

    }

    private fun performLogout() {
        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""

        val apiService = ApiClient.instance.create(ApiService::class.java)

        apiService.logout(token).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                Toast.makeText(requireContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
                logoutUser()
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "فشل الاتصال بالخادم، يتم تسجيل الخروج محليًا",
                    Toast.LENGTH_SHORT
                ).show()
                logoutUser()
            }
        })
    }

    private fun deleteAccount() {
        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs",MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "المستخدم غير مسجل الدخول", Toast.LENGTH_SHORT).show()
            return
        }

        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        val apiService = ApiClient.instance.create(ApiService::class.java)
        val userId = sharedPref.getInt("USER_ID", -1)
        Log.e("user id ", userId.toString())
        Log.e("bearerToken", bearerToken)

        if (userId == -1) {
            Toast.makeText(requireContext(), "معرف المستخدم غير موجود", Toast.LENGTH_SHORT).show()
            return
        }
        apiService.deleteAccount(bearerToken, userId).enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(
                call: Call<DeleteResponse>,
                response: Response<DeleteResponse>
            ) {
                Log.e("response", response.toString())
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()

                    // 🧹 حذف التوكن والانتقال لتسجيل الدخول
                    sharedPref.edit { clear() }
                    val intent = Intent(requireContext(), LogInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("delete_error_body", errorBody ?: "no error body")
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "خطأ في الاتصال: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("❗ تأكيد حذف الحساب")
            .setMessage("هل أنت متأكد أنك تريد حذف حسابك؟ لا يمكن التراجع عن هذا الإجراء.")
            .setPositiveButton("نعم") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("إلغاء", null)
            .show()
    }


}