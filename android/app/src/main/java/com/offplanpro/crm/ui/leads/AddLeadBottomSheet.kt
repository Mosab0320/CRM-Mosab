package com.offplanpro.crm.ui.leads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.ActivityItem
import com.offplanpro.crm.data.entity.Lead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddLeadBottomSheet : BottomSheetDialogFragment() {
    private lateinit var db: AppDatabase
    private var editLead: Lead? = null

    companion object {
        private const val ARG_LEAD_ID = "lead_id"
        fun newInstance(lead: Lead?): AddLeadBottomSheet {
            val sheet = AddLeadBottomSheet()
            lead?.let {
                sheet.arguments = Bundle().apply { putLong(ARG_LEAD_ID, it.id) }
            }
            return sheet
        }
    }

    override fun getTheme(): Int = R.style.BottomSheet_OffPlanPro

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet_add_lead, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())

        val tvTitle = view.findViewById<TextView>(R.id.tv_sheet_title)
        val etName = view.findViewById<EditText>(R.id.et_name)
        val etPhone = view.findViewById<EditText>(R.id.et_phone)
        val spType = view.findViewById<Spinner>(R.id.spinner_type)
        val spHeat = view.findViewById<Spinner>(R.id.spinner_heat)
        val spStage = view.findViewById<Spinner>(R.id.spinner_stage)
        val etBudget = view.findViewById<EditText>(R.id.et_budget)
        val etProject = view.findViewById<EditText>(R.id.et_project)
        val etUnitType = view.findViewById<EditText>(R.id.et_unit_type)
        val etSource = view.findViewById<EditText>(R.id.et_source)
        val etNationality = view.findViewById<EditText>(R.id.et_nationality)
        val etNotes = view.findViewById<EditText>(R.id.et_notes)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        val types = arrayOf("Buyer", "Investor", "Seller")
        val heats = arrayOf("hot", "warm", "cold")
        val stages = arrayOf("New Lead", "Contacted", "Viewing", "Offer", "Negotiation", "Closed")
        spType?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spHeat?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, heats).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spStage?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stages).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val leadId = arguments?.getLong(ARG_LEAD_ID)
        if (leadId != null && leadId > 0L) {
            tvTitle?.text = "Edit Client"
            lifecycleScope.launch(Dispatchers.IO) {
                val lead = db.leadDao().getAllLeadsList().find { it.id == leadId }
                lead?.let {
                    editLead = it
                    requireActivity().runOnUiThread {
                        etName?.setText(it.name)
                        etPhone?.setText(it.phone)
                        spType?.setSelection(types.indexOf(it.type).coerceAtLeast(0))
                        spHeat?.setSelection(heats.indexOf(it.heat).coerceAtLeast(0))
                        spStage?.setSelection(stages.indexOf(it.stage).coerceAtLeast(0))
                        etBudget?.setText(it.budget.toInt().toString())
                        etProject?.setText(it.project)
                        etUnitType?.setText(it.unitType)
                        etSource?.setText(it.source)
                        etNationality?.setText(it.nationality)
                        etNotes?.setText(it.notes)
                    }
                }
            }
        }

        btnSave?.setOnClickListener {
            val name = etName?.text?.toString()?.trim() ?: ""
            val phone = etPhone?.text?.toString()?.trim() ?: ""
            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Name and phone are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val lead = Lead(
                id = editLead?.id ?: 0,
                name = name,
                phone = phone,
                type = spType?.selectedItem?.toString() ?: "Buyer",
                heat = spHeat?.selectedItem?.toString() ?: "warm",
                stage = spStage?.selectedItem?.toString() ?: "New Lead",
                budget = etBudget?.text?.toString()?.toDoubleOrNull() ?: 0.0,
                project = etProject?.text?.toString()?.trim() ?: "",
                unitType = etUnitType?.text?.toString()?.trim() ?: "",
                source = etSource?.text?.toString()?.trim() ?: "",
                nationality = etNationality?.text?.toString()?.trim() ?: "",
                notes = etNotes?.text?.toString()?.trim() ?: "",
                date = editLead?.date ?: LocalDate.now().toString(),
                lastContact = LocalDate.now().toString()
            )
            lifecycleScope.launch(Dispatchers.IO) {
                if (editLead != null) {
                    db.leadDao().update(lead)
                } else {
                    db.leadDao().insert(lead)
                    db.activityDao().insert(ActivityItem(
                        text = "New client: $name from ${lead.source}",
                        color = "#38C4F8",
                        time = "Now"
                    ))
                }
                requireActivity().runOnUiThread { dismiss() }
            }
        }
        btnCancel?.setOnClickListener { dismiss() }
    }
}
