package com.offplanpro.crm.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.Deal
import com.offplanpro.crm.data.entity.Goal
import com.offplanpro.crm.databinding.FragmentGoalsBinding
import java.util.Calendar

class GoalsFragment : Fragment() {
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private var currentGoals: Goal? = null
    private var allDeals: List<Deal> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.goalDao().getGoals().observe(viewLifecycleOwner) { goal ->
            currentGoals = goal
            updateGoalUI()
        }
        db.dealDao().getAllDeals().observe(viewLifecycleOwner) { deals ->
            allDeals = deals
            updateGoalUI()
        }
        db.leadDao().getAllLeads().observe(viewLifecycleOwner) { leads ->
            updateLeadsGoal(leads.size)
        }
    }

    private fun updateGoalUI() {
        val goal = currentGoals ?: return
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        val monthlyDeals = allDeals.filter { d ->
            try { val p = d.date.split("-"); p[0].toInt() == year && p[1].toInt() - 1 == month && d.status == "مكتملة" }
            catch (e: Exception) { false }
        }
        val yearlyDeals = allDeals.filter { d ->
            try { val p = d.date.split("-"); p[0].toInt() == year && d.status == "مكتملة" }
            catch (e: Exception) { false }
        }
        val mComm = monthlyDeals.sumOf { it.myComm }
        val yComm = yearlyDeals.sumOf { it.myComm }

        // Monthly commission
        val commMPct = if (goal.commM > 0) minOf(100, (mComm / goal.commM * 100).toInt()) else 0
        binding.tvCommMPct.text = "$commMPct%"
        binding.pbCommM.progress = commMPct
        binding.tvCommMDetail.text = "${formatNum(mComm)} / ${formatNum(goal.commM)} ج.م"

        // Monthly deals
        val dealsMPct = if (goal.dealsM > 0) minOf(100, monthlyDeals.size * 100 / goal.dealsM) else 0
        binding.tvDealsMPct.text = "$dealsMPct%"
        binding.pbDealsM.progress = dealsMPct
        binding.tvDealsMDetail.text = "${monthlyDeals.size} / ${goal.dealsM} صفقة"

        // Yearly commission
        val commYPct = if (goal.commY > 0) minOf(100, (yComm / goal.commY * 100).toInt()) else 0
        binding.tvCommYPct.text = "$commYPct%"
        binding.pbCommY.progress = commYPct
        binding.tvCommYDetail.text = "${formatNum(yComm)} / ${formatNum(goal.commY)} ج.م"

        // Yearly deals
        val dealsYPct = if (goal.dealsY > 0) minOf(100, yearlyDeals.size * 100 / goal.dealsY) else 0
        binding.tvDealsYPct.text = "$dealsYPct%"
        binding.pbDealsY.progress = dealsYPct
        binding.tvDealsYDetail.text = "${yearlyDeals.size} / ${goal.dealsY} صفقة"
    }

    private fun updateLeadsGoal(count: Int) {
        val goal = currentGoals ?: return
        val pct = if (goal.leadsM > 0) minOf(100, count * 100 / goal.leadsM) else 0
        binding.tvLeadsMPct.text = "$pct%"
        binding.pbLeadsM.progress = pct
        binding.tvLeadsMDetail.text = "$count / ${goal.leadsM} ليد"
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
