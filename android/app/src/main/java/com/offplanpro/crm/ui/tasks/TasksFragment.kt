package com.offplanpro.crm.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.ActivityItem
import com.offplanpro.crm.data.entity.CrmTask
import com.offplanpro.crm.databinding.FragmentTasksBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: TaskAdapter
    private var showPending = true
    private var allTasks: List<CrmTask> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TaskAdapter(
            onMarkDone = { task ->
                lifecycleScope.launch(Dispatchers.IO) {
                    db.taskDao().update(task.copy(done = true))
                    db.activityDao().insert(ActivityItem(
                        text = "تم إتمام: ${task.title}",
                        color = "#2DD4A0",
                        time = "الآن"
                    ))
                }
            }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.adapter = adapter

        binding.btnTabPending.setOnClickListener { switchTab(true) }
        binding.btnTabDone.setOnClickListener { switchTab(false) }

        db.taskDao().getAllTasks().observe(viewLifecycleOwner) { tasks ->
            allTasks = tasks
            updateList()
        }

        binding.fabAdd.setOnClickListener {
            AddTaskBottomSheet.newInstance(null).show(childFragmentManager, "add_task")
        }
    }

    private fun switchTab(pending: Boolean) {
        showPending = pending
        binding.btnTabPending.setTextColor(
            if (pending) resources.getColor(R.color.gold, null)
            else resources.getColor(R.color.text_secondary, null)
        )
        binding.btnTabDone.setTextColor(
            if (!pending) resources.getColor(R.color.gold, null)
            else resources.getColor(R.color.text_secondary, null)
        )
        updateList()
    }

    private fun updateList() {
        val filtered = if (showPending) allTasks.filter { !it.done }.sortedBy { it.date }
                       else allTasks.filter { it.done }
        adapter.submitList(filtered)
        val noMsg = if (showPending) "ما شاء الله! لا مهام معلقة 🎉" else "لا توجد مهام مكتملة"
        binding.tvNoTasks.text = noMsg
        binding.tvNoTasks.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvTasks.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
