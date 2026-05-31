package com.offplanpro.crm.ui.deals

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.ActivityItem
import com.offplanpro.crm.data.entity.Deal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddDealBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(): AddDealBottomSheet = AddDealBottomSheet()
    }

    override fun getTheme(): Int = R.style.BottomSheet_OffPlanPro

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet_add_deal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = AppDatabase.getInstance(requireContext())

        val etClient = view.findViewById<EditText>(R.id.et_client)
        val etProject = view.findViewById<EditText>(R.id.et_project)
        val etUnit = view.findViewById<EditText>(R.id.et_unit)
        val spStatus = view.findViewById<Spinner>(R.id.spinner_status)
        val etValue = view.findViewById<EditText>(R.id.et_value)
        val etCommPct = view.findViewById<EditText>(R.id.et_comm_pct)
        val etMyPct = view.findViewById<EditText>(R.id.et_my_pct)
        val tvCommTotal = view.findViewById<TextView>(R.id.tv_comm_total)
        val tvMyComm = view.findViewById<TextView>(R.id.tv_my_comm)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        val statuses = arrayOf("Completed", "Ongoing", "Cancelled")
        spStatus?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spStatus?.setSelection(1) // default to جارية

        val calcComm = {
            val v = etValue?.text?.toString()?.toDoubleOrNull() ?: 0.0
            val p = etCommPct?.text?.toString()?.toDoubleOrNull() ?: 3.0
            val mp = etMyPct?.text?.toString()?.toDoubleOrNull() ?: 100.0
            val total = Math.round(v * p / 100).toDouble()
            val mine = Math.round(total * mp / 100).toDouble()
            tvCommTotal?.text = "${formatNum(total)} EGP"
            tvMyComm?.text = "${formatNum(mine)} EGP"
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = calcComm()
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        }
        etValue?.addTextChangedListener(watcher)
        etCommPct?.addTextChangedListener(watcher)
        etMyPct?.addTextChangedListener(watcher)

        btnSave?.setOnClickListener {
            val client = etClient?.text?.toString()?.trim() ?: ""
            val project = etProject?.text?.toString()?.trim() ?: ""
            val value = etValue?.text?.toString()?.toDoubleOrNull() ?: 0.0
            if (client.isEmpty() || project.isEmpty() || value == 0.0) {
                Toast.makeText(requireContext(), "يرجى ملء الحقول المطلوبة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val commPct = etCommPct?.text?.toString()?.toDoubleOrNull() ?: 3.0
            val myPct = etMyPct?.text?.toString()?.toDoubleOrNull() ?: 100.0
            val commTotal = Math.round(value * commPct / 100).toDouble()
            val myComm = Math.round(commTotal * myPct / 100).toDouble()
            val deal = Deal(
                client = client,
                project = project,
                unit = etUnit?.text?.toString()?.trim() ?: "",
                type = "بيع أوف بلان",
                value = value,
                commPct = commPct,
                commTotal = commTotal,
                myPct = myPct,
                myComm = myComm,
                date = LocalDate.now().toString(),
                collectDate = "",
                status = spStatus?.selectedItem?.toString() ?: "Ongoing",
                contractStage = "",
                notes = ""
            )
            lifecycleScope.launch(Dispatchers.IO) {
                db.dealDao().insert(deal)
                db.activityDao().insert(ActivityItem(
                    text = "صفقة جديدة: $client — $project 💰",
                    color = "#E2B96A",
                    time = "Now"
                ))
                requireActivity().runOnUiThread { dismiss() }
            }
        }
        btnCancel?.setOnClickListener { dismiss() }
    }

    private fun formatNum(n: Double): String {
        return when {
            n >= 1_000_000 -> "${String.format("%.1f", n / 1_000_000)}م"
            n >= 1_000 -> "${(n / 1_000).toInt()}ك"
            else -> n.toInt().toString()
        }
    }
}
