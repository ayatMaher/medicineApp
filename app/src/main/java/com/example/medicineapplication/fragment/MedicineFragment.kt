package com.example.medicineapplication.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.CategoryAdapter
import com.example.medicineapplication.adapter.MedicineAdapter
import com.example.medicineapplication.api.ApiClient
import com.example.medicineapplication.databinding.FragmentMedicineBinding
import com.example.medicineapplication.model.Category
import com.example.medicineapplication.model.FavoriteMedicineRequest
import com.example.medicineapplication.model.FavoriteMedicineResponse
import com.example.medicineapplication.model.GeneralResponse
import com.example.medicineapplication.model.Treatment
import com.example.medicineapplication.model.TreatmentsSearchResponse
import com.example.medicineapplication.model.ViewCategoriesResponse
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




@Suppress("DEPRECATION")
class MedicineFragment : Fragment(), CategoryAdapter.ItemClickListener,
    MedicineAdapter.ItemClickListener {

    private var _binding: FragmentMedicineBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var isFromQRScan = false

    // category
    private lateinit var categoryAdapter: CategoryAdapter
    val data = ArrayList<Category>()

    // medicine
    private lateinit var medicineAdapter: MedicineAdapter
    private val medicineData = ArrayList<Treatment>()

    private var token: String = " "
    private var userId: Int = -1

    private var medicineName: String = ""
    private var categoryId: String? = ""

    private var selectedCategoryId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMedicineBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""
        userId = sharedPref.getInt("USER_ID", -1)


        //status bar
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white2)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val pageType = arguments?.getString("page_type")
        if (pageType == "medicine_type") {
            binding.backArrow.visibility = View.VISIBLE
        } else {
            binding.backArrow.visibility = View.GONE
        }
        // back arrow
        binding.backArrow.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_firstFragment)
        }

        categoryId = selectedCategoryId ?: arguments?.getString("category_id") ?: ""

        if (categoryId == null) {
            categoryId = ""
        }

        if (token.isNotEmpty()) {
            if (categoryId?.isNotEmpty() == true && medicineName.isEmpty()) {
                fetchTreatments(token, categoryId.toString(), "")
                Toast.makeText(requireContext(), " أولاً", Toast.LENGTH_SHORT).show()
            } else if (categoryId?.isNotEmpty() == true && medicineName.isNotEmpty()) {
                fetchTreatments(token, categoryId.toString(), medicineName)
                Toast.makeText(requireContext(), " 1111111111", Toast.LENGTH_SHORT).show()
            } else if (categoryId == null && medicineName.isNotEmpty()) {
                fetchTreatments(token, "", medicineName)
            }
        } else {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
        }

        binding.qr.setOnClickListener {
            startQRScanner()
        }

        //category
        showCategory()
        //medicine
        setupMedicineRecycler()
        setupSearchListeners()
        return root
    }


    private val qrScannerLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        if (intentResult != null) {
            if (intentResult.contents != null) {
                Toast.makeText(requireContext(), "Scanned: ${intentResult.contents}", Toast.LENGTH_LONG).show()
                Log.d("ansam", "ID: ${intentResult.contents}")

                binding.edtSearch.setText(intentResult.contents)
                medicineName = intentResult.contents ?: ""
                isFromQRScan = true

                // هنا ممكن تستدعي fetchTreatments لو بدك تبحث تلقائياً
                fetchTreatments(token, "", intentResult.contents ?: "")
            } else {
                Toast.makeText(requireContext(), "Scan cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun setupSearchListeners() {
        // الضغط على Enter من الكيبورد
        binding.edtSearch.setOnEditorActionListener { _: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                performSearch()
                true
            } else {
                false
            }
        }

        // الضغط على أيقونة البحث
        binding.searchImage.setOnClickListener {
            performSearch()
        }
    }


    private fun performSearch() {
        medicineName = binding.edtSearch.text.toString().trim()
        if (medicineName.isEmpty()) {
            Toast.makeText(requireContext(), "يرجى إدخال اسم الدواء", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPref =
            requireActivity().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPref.getString("ACCESS_TOKEN", "") ?: ""



        if (token.isNotEmpty()) {
            fetchTreatments(token, categoryId.toString(), medicineName)
        } else {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchTreatments(token: String, categoryId: String, treatment: String) {
        ApiClient.apiService.treatmentsSearch(token, categoryId, treatment)
            .enqueue(object : Callback<TreatmentsSearchResponse> {
                override fun onResponse(
                    call: Call<TreatmentsSearchResponse>,
                    response: Response<TreatmentsSearchResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val results =
                            response.body()?.data?.filterIsInstance<Treatment>() ?: emptyList()
                        medicineAdapter.updateData(results)

                        Toast.makeText(
                            requireContext(),
                            "${categoryId},${treatment}تم العثور على نتائج",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        Toast.makeText(
                            requireContext(),
                            "${categoryId},${treatment} لم يتم العثور على نتائج",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TreatmentsSearchResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "فشل الاتصال: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }


    private fun showCategory() {

        ApiClient.apiService.viewCategories(token)
            .enqueue(object : Callback<ViewCategoriesResponse> {
                override fun onResponse(
                    call: Call<ViewCategoriesResponse>,
                    response: Response<ViewCategoriesResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val categories = response.body()?.data ?: emptyList()
                        val updatedList = categories.map {
                            it.copy(isFeatured = true)
                        }
                        data.addAll(updatedList)
                        //Toast.makeText(requireContext(), "تم العثور على نتائج الانواع", Toast.LENGTH_SHORT).show()
                        val selectedCategoryName = arguments?.getString("category_name")
                        categoryAdapter = CategoryAdapter(
                            requireActivity(),
                            data,
                            this@MedicineFragment,
                            selectedCategoryName
                        )
                        binding.rvCategory.adapter = categoryAdapter
                    } else {
                        // Toast.makeText(requireContext(), " لم يتم العثور على نتائج الانواع", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ViewCategoriesResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "فشل الاتصال: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })

    }


    private fun setupMedicineRecycler() {
        medicineAdapter = MedicineAdapter(requireActivity(), medicineData, this)
        binding.rvCategoryMedicine.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategoryMedicine.adapter = medicineAdapter
    }

    private fun storeSearchTreatment(token: String, userId: String, treatmentId: String) {
        ApiClient.apiService.storeSearchTreatment(token, userId, treatmentId)
            .enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(
                    call: Call<GeneralResponse>,
                    response: Response<GeneralResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        //val categories = response.body()?.data ?: emptyList()
                        Toast.makeText(
                            requireContext(),
                            "نجح ${response.body()}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            " لم يتم العثور على نتائج",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "فشل الاتصال: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })

    }


    private fun addMedicineToFavorite(medicineId: Int) {

        if (token.isEmpty() || userId == -1) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        val request = FavoriteMedicineRequest(userId, medicineId)

        ApiClient.apiService.storFavoriteMedicine(token, request)
            .enqueue(object : Callback<FavoriteMedicineResponse> {
                override fun onResponse(
                    call: Call<FavoriteMedicineResponse>,
                    response: Response<FavoriteMedicineResponse>
                ) {
                    val errorBody = response.errorBody()?.string()

                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            requireContext(),
                            "تمت الإضافة إلى المفضلة",
                            Toast.LENGTH_SHORT
                        ).show()

                        // ✅ تحديث حالة isFavorite في القائمة وإبلاغ الـ Adapter
                        val index = medicineData.indexOfFirst { it.id == medicineId }
                        if (index != -1) {
                            medicineData[index].is_favorite = true
                            medicineAdapter.notifyItemChanged(index)
                        }

                    } else {
                        val errorMessage = try {
                            val json = org.json.JSONObject(errorBody ?: "")
                            json.optJSONObject("data")?.optString("error")
                                ?: "فشل في الإضافة للمفضلة"
                        } catch (e: Exception) {
                            "فشل في الإضافة للمفضلة${e.message}"
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FavoriteMedicineResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "خطأ: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun startQRScanner() {
        val integrator = IntentIntegrator(requireActivity())
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("امسح رمز QR الخاص بالدواء")
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.setBarcodeImageEnabled(true)

        // بدال initiateScan() العادي، نستخدم intent ونمرره للـ launcher
        val scanIntent = integrator.createScanIntent()
        qrScannerLauncher.launch(scanIntent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (!isFromQRScan) {
            binding.edtSearch.setText("")
            medicineName = ""
        }
        // بعد ما تتحقق وتحدث حالة التفريغ، نعيد تعيين الفلاغ لـ false للاستعداد للمرات القادمة
        isFromQRScan = false
    }



    override fun onItemClick(position: Int, id: String) {
        selectedCategoryId = id
        val categoryName = data[position].name
        val categoryId = data[position].id
        val bundle = Bundle().apply {
            putString("category_name", categoryName)
            putString("category_id", categoryId.toString())
            putString("page_type", "medicine_type")
        }
        findNavController().navigate(R.id.navigation_medicines, bundle)
    }


    override fun onItemClickMedicine(position: Int, id: String) {

        storeSearchTreatment(token, userId.toString(), id.toString())
        val treatment = medicineAdapter.data[position]
        val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
        intent.putExtra("pharmacy_name","pharmacy")
        intent.putExtra("medicine", treatment)
        startActivity(intent)
    }


    override fun onAddMedicineToFavorite(medicineId: Int) {
        addMedicineToFavorite(medicineId)
    }




}