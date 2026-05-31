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
import com.offplanpro.crm.data.entity.FollowUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddFollowUpForLeadSheet : BottomSheetDialogFragment() {
    private var clientName: String = ""

    companion object {
        fun newInstance(client: String): AddFollowUpForLeadSheet {
            val sheet = AddFollowUpForLeadSheet()
            sheet.arguments = Bundle().apply { putString("client", client) }
            return sheet
        }
    }

    override fun getTheme(): Int = R.style.BottomSheet_OffPlanPro

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet_add_followup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clientName = arguments?.getString("client") ?: ""
        val db = AppDatabase.getInstance(requireContext())

        val etClient = view.findViewById<EditText>(R.id.et_client)
        val spType = view.findViewById<Spinner>(R.id.spinner_type)
        val etDate = view.findViewById<EditText>(R.id.et_date)
        val etNextDate = view.findViewById<EditText>(R.id.et_next_date)
        val etNotes = view.findViewById<EditText>(R.id.et_notes)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        etClient?.setText(clientName)
        etDate?.setText(LocalDate.now().toString())

        val types = arrayOf("مكالمة هاتفية", "WhatsApp", "زيارة", "Email", "Meeting")
        spType?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        btnSave?.setOnClickListener {
            val client = etClient?.text?.toString()?.trim() ?: ""
            if (client.isEmpty()) {
                Toast.makeText(requireContext(), "اسم العميل مطلوب", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val followUp = FollowUp(
                client = client,
                type = spType?.selectedItem?.toString() ?: "مكالمة هاتفية",
                date = etDate?.text?.toString() ?: LocalDate.now().toString(),
                nextDate = etNextDate?.text?.toString() ?: "",
                notes = etNotes?.text?.toString() ?: "",
                status = "pending"
            )
            lifecycleScope.launch(Dispatchers.IO) {
                db.followUpDao().insert(followUp)
                db.activityDao().insert(ActivityItem(
                    text = "متابعة مع $client",
                    color = "#38C4F8",
                    time = "Now"
                ))
                requireActivity().runOnUiThread { dismiss() }
            }
        }
        btnCancel?.setOnClickListener { dismiss() }
    }
}
