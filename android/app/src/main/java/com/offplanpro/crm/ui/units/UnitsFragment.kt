package com.offplanpro.crm.ui.units

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.CrmUnit
import com.offplanpro.crm.databinding.FragmentUnitsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnitsFragment : Fragment() {
    private var _binding: FragmentUnitsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: UnitAdapter
    private var allUnits: List<CrmUnit> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUnitsBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UnitAdapter()
        binding.rvUnits.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUnits.adapter = adapter

        setupSpinners()
        db.unitDao().getAllUnits().observe(viewLifecycleOwner) { units ->
            allUnits = units
            filterUnits()
        }
    }

    private fun setupSpinners() {
        val types = listOf("All Types", "Apartment", "Duplex", "Penthouse", "Villa", "Townhouse", "Office")
        val statuses = listOf("All Status", "Available", "Reserved", "Sold")

        lifecycleScope.launch(Dispatchers.IO) {
            val projectNames = db.projectDao().getProjectNames()
            withContext(Dispatchers.Main) {
                val projects = listOf("All Projects") + projectNames
                binding.spinnerProject.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, projects)
                binding.spinnerType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
                binding.spinnerStatus.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)

                val listener = object : android.widget.AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) = filterUnits()
                    override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
                }
                binding.spinnerProject.onItemSelectedListener = listener
                binding.spinnerType.onItemSelectedListener = listener
                binding.spinnerStatus.onItemSelectedListener = listener
            }
        }
    }

    private fun filterUnits() {
        val project = binding.spinnerProject.selectedItem?.toString() ?: ""
        val type = binding.spinnerType.selectedItem?.toString() ?: ""
        val status = binding.spinnerStatus.selectedItem?.toString() ?: ""
        val filtered = allUnits.filter { unit ->
            (project == "All Projects" || unit.project == project) &&
            (type == "All Types" || unit.type == type) &&
            (status == "All Status" || unit.status == status)
        }
        adapter.submitList(filtered)
        binding.tvNoUnits.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvUnits.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
