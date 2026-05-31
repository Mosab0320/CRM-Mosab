package com.offplanpro.crm.ui.followups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.FollowUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddFollowUpBottomSheet : BottomSheetDialogFragment() {
    private var editFollowUp: FollowUp? = null

    companion object {
        private const val ARG_ID = "followup_id"
        fun newInstance(followUp: FollowUp?): AddFollowUpBottomSheet {
            val sheet = AddFollowUpBottomSheet()
            followUp?.let { sheet.arguments = Bundle().apply { putLong(ARG_ID, it.id) } }
            return sheet
        }
    }

    override fun getTheme(): Int = R.style.BottomSheet_OffPlanPro

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet_add_followup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = AppDatabase.getInstance(requireContext())

        val etClient = view.findViewById<EditText>(R.id.et_client)
        val spType = view.findViewById<Spinner>(R.id.spinner_type)
        val etDate = view.findViewById<EditText>(R.id.et_date)
        val etNextDate = view.findViewById<EditText>(R.id.et_next_date)
        val etNotes = view.findViewById<EditText>(R.id.et_notes)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        val types = arrayOf("مكالمة هاتفية", "واتساب", "زيارة", "بريد إلكتروني", "اجتماع")
        spType?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        etDate?.setText(LocalDate.now().toString())

        val followUpId = arguments?.getLong(ARG_ID)
        if (followUpId != null && followUpId > 0L) {
            lifecycleScope.launch(Dispatchers.IO) {
                val fu = db.followUpDao().getAllFollowUps().value?.find { it.id == followUpId }
                fu?.let {
                    editFollowUp = it
                    requireActivity().runOnUiThread {
                        etClient?.setText(it.client)
                        spType?.setSelection(types.indexOf(it.type).coerceAtLeast(0))
                        etDate?.setText(it.date)
                        etNextDate?.setText(it.nextDate)
                        etNotes?.setText(it.notes)
                    }
                }
            }
        }

        btnSave?.setOnClickListener {
            val client = etClient?.text?.toString()?.trim() ?: ""
            if (client.isEmpty()) {
                Toast.makeText(requireContext(), "اسم العميل مطلوب", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fu = FollowUp(
                id = editFollowUp?.id ?: 0,
                client = client,
                type = spType?.selectedItem?.toString() ?: "مكالمة هاتفية",
                date = etDate?.text?.toString() ?: LocalDate.now().toString(),
                nextDate = etNextDate?.text?.toString() ?: "",
                notes = etNotes?.text?.toString() ?: "",
                status = "pending"
            )
            lifecycleScope.launch(Dispatchers.IO) {
                if (editFollowUp != null) db.followUpDao().update(fu) else db.followUpDao().insert(fu)
                requireActivity().runOnUiThread { dismiss() }
            }
        }
        btnCancel?.setOnClickListener { dismiss() }
    }
}
