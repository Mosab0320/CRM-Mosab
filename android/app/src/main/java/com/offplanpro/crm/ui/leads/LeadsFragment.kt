package com.offplanpro.crm.ui.leads

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.ActivityItem
import com.offplanpro.crm.data.entity.Lead
import com.offplanpro.crm.databinding.FragmentLeadsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class LeadsFragment : Fragment() {
    private var _binding: FragmentLeadsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private var allLeads: List<Lead> = emptyList()
    private lateinit var adapter: LeadAdapter

    private val stages = listOf("ليد جديد", "تم التواصل", "معاينة", "عرض سعر", "تفاوض", "تم الإغلاق")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeadsBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupRecyclerView()
        setupSearch()
        setupFab()
        observeLeads()
    }

    private fun setupSpinners() {
        val stageList = listOf("كل المراحل") + stages
        val heats = listOf("كل الحرارة", "hot", "warm", "cold")
        binding.spinnerStage.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stageList)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerHeat.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, heats)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        val listener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, v: View?, pos: Int, id: Long) = filterLeads()
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }
        binding.spinnerStage.onItemSelectedListener = listener
        binding.spinnerHeat.onItemSelectedListener = listener
    }

    private fun setupRecyclerView() {
        adapter = LeadAdapter(
            onDelete = { lead ->
                AlertDialog.Builder(requireContext())
                    .setTitle("حذف عميل")
                    .setMessage("هل تريد حذف ${lead.name}؟")
                    .setPositiveButton("نعم") { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) { db.leadDao().delete(lead) }
                    }
                    .setNegativeButton("لا", null)
                    .show()
            },
            onEdit = { lead ->
                AddLeadBottomSheet.newInstance(lead).show(childFragmentManager, "edit_lead")
            },
            onAdvance = { lead ->
                val idx = stages.indexOf(lead.stage)
                if (idx < stages.size - 1) {
                    val newStage = stages[idx + 1]
                    lifecycleScope.launch(Dispatchers.IO) {
                        val updated = lead.copy(stage = newStage, lastContact = LocalDate.now().toString())
                        db.leadDao().update(updated)
                        db.activityDao().insert(ActivityItem(
                            text = "${lead.name} انتقل إلى: $newStage",
                            color = "#A78BFA",
                            time = "الآن"
                        ))
                    }
                }
            },
            onCall = { lead ->
                AddFollowUpForLeadSheet.newInstance(lead.name).show(childFragmentManager, "followup")
            }
        )
        binding.rvLeads.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLeads.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = filterLeads()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            AddLeadBottomSheet.newInstance(null).show(childFragmentManager, "add_lead")
        }
    }

    private fun observeLeads() {
        db.leadDao().getAllLeads().observe(viewLifecycleOwner) { leads ->
            allLeads = leads
            filterLeads()
        }
    }

    private fun filterLeads() {
        val query = binding.etSearch.text?.toString()?.lowercase() ?: ""
        val stageFilter = binding.spinnerStage.selectedItem?.toString() ?: ""
        val heatFilter = binding.spinnerHeat.selectedItem?.toString() ?: ""
        val filtered = allLeads.filter { lead ->
            val matchQuery = query.isEmpty() || lead.name.lowercase().contains(query) || lead.phone.contains(query)
            val matchStage = stageFilter == "كل المراحل" || lead.stage == stageFilter
            val matchHeat = heatFilter == "كل الحرارة" || lead.heat == heatFilter
            matchQuery && matchStage && matchHeat
        }
        adapter.submitList(filtered)
        binding.tvCount.text = "${filtered.size} عميل"
        binding.tvNoLeads.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvLeads.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
