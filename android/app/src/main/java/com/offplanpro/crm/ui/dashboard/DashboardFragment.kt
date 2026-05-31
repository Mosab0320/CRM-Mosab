package com.offplanpro.crm.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.ActivityItem
import com.offplanpro.crm.data.entity.CrmTask
import com.offplanpro.crm.data.entity.Deal
import com.offplanpro.crm.data.entity.Lead
import com.offplanpro.crm.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeData()
    }

    private fun setupRecyclerViews() {
        binding.rvHotLeads.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() {
        db.leadDao().getAllLeads().observe(viewLifecycleOwner) { leads ->
            updateLeadStats(leads)
        }
        db.dealDao().getAllDeals().observe(viewLifecycleOwner) { deals ->
            updateDealStats(deals)
        }
        db.taskDao().getAllTasks().observe(viewLifecycleOwner) { tasks ->
            updateTaskStats(tasks)
            showUpcomingTasks(tasks.filter { !it.done }.sortedBy { it.date }.take(5))
        }
        db.activityDao().getRecentActivities().observe(viewLifecycleOwner) { activities ->
            binding.rvActivities.adapter = DashActivityAdapter(activities)
        }
        lifecycleScope.launch {
            val goal = withContext(Dispatchers.IO) { db.goalDao().getGoalsSync() }
            goal?.let {
                binding.tvDealsGoal.text = "الهدف: ${it.dealsM}"
                updateGoalBars(0.0, 0, 0, it.commM, it.dealsM, it.leadsM)
            }
        }
    }

    private fun updateLeadStats(leads: List<Lead>) {
        val active = leads.filter { it.stage != "تم الإغلاق" }
        val today = java.time.LocalDate.now().toString()
        val todayLeads = leads.filter { it.date == today }
        binding.tvLeads.text = active.size.toString()
        binding.tvNewLeads.text = "جديد اليوم: ${todayLeads.size}"

        val hotLeads = leads.filter { it.heat == "hot" && it.stage != "تم الإغلاق" }.take(4)
        binding.rvHotLeads.adapter = HotLeadMiniAdapter(hotLeads)
        binding.tvNoHotLeads.visibility = if (hotLeads.isEmpty()) View.VISIBLE else View.GONE

        lifecycleScope.launch {
            val goal = withContext(Dispatchers.IO) { db.goalDao().getGoalsSync() }
            goal?.let {
                val commPct = minOf(100, ((binding.tvCommission.text.toString().replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: 0.0) / it.commM * 100).toInt())
                updateGoalBars(0.0, leads.size, 0, it.commM, it.dealsM, it.leadsM)
            }
        }
    }

    private fun updateDealStats(deals: List<Deal>) {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        val monthlyDeals = deals.filter { deal ->
            try {
                val parts = deal.date.split("-")
                parts[0].toInt() == year && parts[1].toInt() - 1 == month && deal.status == "مكتملة"
            } catch (e: Exception) { false }
        }
        val monthlyComm = monthlyDeals.sumOf { it.myComm }
        binding.tvCommission.text = "${formatNum(monthlyComm)} ج.م"
        binding.tvDeals.text = monthlyDeals.size.toString()

        lifecycleScope.launch {
            val goal = withContext(Dispatchers.IO) { db.goalDao().getGoalsSync() }
            withContext(Dispatchers.Main) {
                goal?.let {
                    binding.tvDealsGoal.text = "الهدف: ${it.dealsM}"
                    val dealsPct = if (it.dealsM > 0) minOf(100, (monthlyDeals.size * 100 / it.dealsM)) else 0
                    val commPct = if (it.commM > 0) minOf(100, (monthlyComm / it.commM * 100).toInt()) else 0
                    binding.pbGoalDeals.progress = dealsPct
                    binding.tvGoalDealsPct.text = "$dealsPct%"
                    binding.tvGoalDealsPct.setTextColor(Color.parseColor(if (dealsPct >= 100) "#2DD4A0" else if (dealsPct >= 60) "#E2B96A" else "#F4506A"))
                    binding.pbGoalComm.progress = commPct
                    binding.tvGoalCommPct.text = "$commPct%"
                    binding.tvGoalCommPct.setTextColor(Color.parseColor(if (commPct >= 100) "#2DD4A0" else if (commPct >= 60) "#E2B96A" else "#F4506A"))
                }
            }
        }
    }

    private fun updateGoalBars(comm: Double, leadsCount: Int, dealsCount: Int, commGoal: Double, dealsGoal: Int, leadsGoal: Int) {
        val leadsPct = if (leadsGoal > 0) minOf(100, (leadsCount * 100 / leadsGoal)) else 0
        binding.pbGoalLeads.progress = leadsPct
        binding.tvGoalLeadsPct.text = "$leadsPct%"
        binding.tvGoalLeadsPct.setTextColor(Color.parseColor(if (leadsPct >= 100) "#2DD4A0" else if (leadsPct >= 60) "#E2B96A" else "#F4506A"))
    }

    private fun updateTaskStats(tasks: List<CrmTask>) {
        val now = java.time.LocalDateTime.now().toString()
        val overdue = tasks.filter { !it.done && it.date.isNotEmpty() && it.date < now }.size
        val pending = tasks.filter { !it.done }.size
        binding.tvOverdue.text = overdue.toString()
        binding.tvPending.text = "معلقة: $pending"
    }

    private fun showUpcomingTasks(tasks: List<CrmTask>) {
        binding.rvUpcomingTasks.adapter = UpcomingTaskMiniAdapter(tasks)
        binding.tvNoTasks.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun formatNum(n: Double): String {
        return when {
            n >= 1_000_000 -> "${String.format("%.1f", n / 1_000_000)}م"
            n >= 1_000 -> "${(n / 1_000).toInt()}ك"
            else -> n.toInt().toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class HotLeadMiniAdapter(private val leads: List<Lead>) : RecyclerView.Adapter<HotLeadMiniAdapter.VH>() {
    inner class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_hot_lead, parent, false)
        return VH(v)
    }

    override fun getItemCount() = leads.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val lead = leads[position]
        val v = holder.itemView
        v.findViewById<TextView>(R.id.tv_avatar)?.text = lead.name.take(1)
        v.findViewById<TextView>(R.id.tv_name)?.text = lead.name
        v.findViewById<TextView>(R.id.tv_info)?.text = "${lead.type} • ${lead.project.ifEmpty { "أي مشروع" }}"
        v.findViewById<TextView>(R.id.tv_budget)?.text = when {
            lead.budget >= 1_000_000 -> "${String.format("%.1f", lead.budget / 1_000_000)}م"
            lead.budget >= 1_000 -> "${(lead.budget / 1_000).toInt()}ك"
            else -> lead.budget.toInt().toString()
        }
    }
}

class UpcomingTaskMiniAdapter(private val tasks: List<CrmTask>) : RecyclerView.Adapter<UpcomingTaskMiniAdapter.VH>() {
    inner class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_upcoming_task, parent, false)
        return VH(v)
    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = tasks[position]
        val v = holder.itemView
        v.findViewById<TextView>(R.id.tv_date)?.text = "📅 ${task.date.take(10)}"
        val icon = when(task.type) {
            "مكالمة" -> "📞"; "معاينة" -> "🏠"; "اجتماع" -> "👥"
            "متابعة" -> "🔄"; "أوراق وعقود" -> "📄"; else -> "📌"
        }
        v.findViewById<TextView>(R.id.tv_title)?.text = "$icon ${task.title}"
        v.findViewById<TextView>(R.id.tv_client)?.text = if (task.client.isNotEmpty()) "👤 ${task.client}" else ""
        val priorityView = v.findViewById<TextView>(R.id.tv_priority)
        priorityView?.text = task.priority
        val color = when(task.priority) {
            "عاجلة" -> "#F4506A"; "مهمة" -> "#F59E2B"; else -> "#38C4F8"
        }
        priorityView?.setTextColor(Color.parseColor(color))
    }
}

class DashActivityAdapter(private val items: List<ActivityItem>) : RecyclerView.Adapter<DashActivityAdapter.VH>() {
    inner class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val v = holder.itemView
        v.findViewById<TextView>(R.id.tv_text)?.text = item.text
        v.findViewById<TextView>(R.id.tv_time)?.text = item.time
        try {
            v.findViewById<View>(R.id.v_dot)?.setBackgroundColor(Color.parseColor(item.color))
        } catch (e: Exception) { /* ignore invalid color */ }
    }
}
