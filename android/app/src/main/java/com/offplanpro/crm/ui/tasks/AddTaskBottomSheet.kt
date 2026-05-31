package com.offplanpro.crm.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.CrmTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddTaskBottomSheet : BottomSheetDialogFragment() {
    private var editTask: CrmTask? = null

    companion object {
        private const val ARG_TASK_ID = "task_id"
        fun newInstance(task: CrmTask?): AddTaskBottomSheet {
            val sheet = AddTaskBottomSheet()
            task?.let { sheet.arguments = Bundle().apply { putLong(ARG_TASK_ID, it.id) } }
            return sheet
        }
    }

    override fun getTheme(): Int = R.style.BottomSheet_OffPlanPro

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = AppDatabase.getInstance(requireContext())

        val etTitle = view.findViewById<EditText>(R.id.et_title)
        val spType = view.findViewById<Spinner>(R.id.spinner_type)
        val spPriority = view.findViewById<Spinner>(R.id.spinner_priority)
        val etDate = view.findViewById<EditText>(R.id.et_date)
        val etClient = view.findViewById<EditText>(R.id.et_client)
        val etProject = view.findViewById<EditText>(R.id.et_project)
        val etNotes = view.findViewById<EditText>(R.id.et_notes)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        val types = arrayOf("Call", "Viewing", "Meeting", "Follow-up", "Contracts", "تسجيل")
        val priorities = arrayOf("Normal", "Important", "Urgent")
        spType?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spPriority?.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        etDate?.setText(LocalDateTime.now().format(formatter))

        val taskId = arguments?.getLong(ARG_TASK_ID)
        if (taskId != null && taskId > 0L) {
            view.findViewById<TextView>(R.id.tv_sheet_title)?.text = "تعديل مهمة"
            lifecycleScope.launch(Dispatchers.IO) {
                val task = db.taskDao().getAllTasksList().find { it.id == taskId }
                task?.let {
                    editTask = it
                    requireActivity().runOnUiThread {
                        etTitle?.setText(it.title)
                        spType?.setSelection(types.indexOf(it.type).coerceAtLeast(0))
                        spPriority?.setSelection(priorities.indexOf(it.priority).coerceAtLeast(0))
                        etDate?.setText(it.date)
                        etClient?.setText(it.client)
                        etProject?.setText(it.project)
                        etNotes?.setText(it.notes)
                    }
                }
            }
        }

        btnSave?.setOnClickListener {
            val title = etTitle?.text?.toString()?.trim() ?: ""
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "عنوان المهمة مطلوب", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val task = CrmTask(
                id = editTask?.id ?: 0,
                title = title,
                type = spType?.selectedItem?.toString() ?: "Call",
                priority = spPriority?.selectedItem?.toString() ?: "Normal",
                date = etDate?.text?.toString() ?: LocalDateTime.now().format(formatter),
                client = etClient?.text?.toString()?.trim() ?: "",
                project = etProject?.text?.toString()?.trim() ?: "",
                notes = etNotes?.text?.toString()?.trim() ?: "",
                done = false
            )
            lifecycleScope.launch(Dispatchers.IO) {
                if (editTask != null) db.taskDao().update(task) else db.taskDao().insert(task)
                requireActivity().runOnUiThread { dismiss() }
            }
        }
        btnCancel?.setOnClickListener { dismiss() }
    }
}
