package com.example.medicineapplication.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medicineapplication.MedicineDetailsActivity
import com.example.medicineapplication.R
import com.example.medicineapplication.adapter.FavoriteMedicineAdapter
import com.example.medicineapplication.adapter.FavoritePharmacyAdapter
import com.example.medicineapplication.databinding.FragmentFavoriteBinding
import com.example.medicineapplication.model.Medicine
import com.example.medicineapplication.model.Pharmacy

@Suppress("DEPRECATION")
class FavoriteFragment : Fragment(), FavoriteMedicineAdapter.ItemClickListener,
    FavoritePharmacyAdapter.ItemClickListener {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    // medicine
    lateinit var favoriteMedicineAdapter: FavoriteMedicineAdapter
    private var medicine_items: ArrayList<Medicine> = ArrayList()

    //pharmacy
    lateinit var favoritePharmacyAdapter: FavoritePharmacyAdapter
    var pharmacy_items: ArrayList<Pharmacy> = ArrayList()

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //statusBar Color
        val window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        // اجعل الأيقونات داكنة إذا كان الخلفية فاتحة
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            true
        // title text
        binding.header.titleText.text = "المفضلة"
        // back arrow
        binding.header.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_firstFragment)
        }
        showPharmacy()

        binding.btnMedicines.setOnClickListener {
            clickOnMedicineButton()
            showMedicine()
        }
        binding.btnPharmacies.setOnClickListener {
            clickOnPharmacyButton()
            showPharmacy()
        }
        return root

    }

    @SuppressLint("ResourceType")
    fun clickOnMedicineButton() {
        binding.btnMedicines.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.btnMedicines.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_color
            )
        )
        binding.btnPharmacies.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.btnPharmacies.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_green
            )
        )

    }

    private fun clickOnPharmacyButton() {
        binding.btnPharmacies.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.btnPharmacies.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_color
            )
        )
        binding.btnMedicines.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.btnMedicines.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_green
            )
        )

    }

    private fun showMedicine() {
        medicine_items.clear()
        medicine_items.add(Medicine("1", "بنادول", R.drawable.medicine_img, 50.0))
        medicine_items.add(Medicine("2", "بنادول", R.drawable.medicine_img, 150.0))
        medicine_items.add(Medicine("3", "بنادول", R.drawable.medicine_img, 500.0))
        medicine_items.add(Medicine("4", "بنادول", R.drawable.medicine_img, 160.0))
        medicine_items.add(Medicine("5", "بنادول", R.drawable.medicine_img, 200.0))
        medicine_items.add(Medicine("6", "بنادول", R.drawable.medicine_img, 40.0))

        //visible
        binding.rvFavoriteMedicine.visibility = View.VISIBLE
        binding.rvFavoritePharmacy.visibility = View.GONE

        favoriteMedicineAdapter = FavoriteMedicineAdapter(requireActivity(), medicine_items, this)
        binding.rvFavoriteMedicine.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFavoriteMedicine.adapter = favoriteMedicineAdapter
    }

    private fun showPharmacy() {
        pharmacy_items.clear()
        pharmacy_items.add(
            Pharmacy(
                "1",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "غزة_التفاح_شارع النفق"
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "2",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "غزة_التفاح_شارع النفق"
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "3",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "غزة_التفاح_شارع النفق"
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "4",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "غزة_التفاح_شارع النفق"
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "5",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "غزة_التفاح_شارع النفق"
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "6",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "غزة_التفاح_شارع النفق"
            )
        )
        pharmacy_items.add(
            Pharmacy(
                "7",
                R.drawable.pharmacy2,
                "صيدلية الأسرى",
                4.5,
                "غزة_التفاح_شارع النفق"
            )
        )

        //visible
        binding.rvFavoritePharmacy.visibility = View.VISIBLE
        binding.rvFavoriteMedicine.visibility = View.GONE

        favoritePharmacyAdapter = FavoritePharmacyAdapter(requireActivity(), pharmacy_items, this)
        binding.rvFavoritePharmacy.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFavoritePharmacy.adapter = favoritePharmacyAdapter

    }

    override fun onItemClickMedicine(position: Int, id: String) {
        //when click to medicine card
        val intent = Intent(requireContext(), MedicineDetailsActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClickPharmacy(position: Int, id: String) {
        // go to pharmacy details page
    }

}