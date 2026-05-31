package com.offplanpro.crm.ui.pipeline

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.Lead
import com.offplanpro.crm.databinding.FragmentPipelineBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PipelineFragment : Fragment() {
    private var _binding: FragmentPipelineBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    private val stages = listOf("New Lead", "Contacted", "Viewing", "Offer", "Negotiation", "Closed")
    private val stageColors = listOf("#38C4F8", "#A78BFA", "#F59E2B", "#E2B96A", "#F4506A", "#2DD4A0")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPipelineBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.leadDao().getAllLeads().observe(viewLifecycleOwner) { leads ->
            buildKanban(leads)
        }
    }

    private fun buildKanban(leads: List<Lead>) {
        binding.kanbanContainer.removeAllViews()
        val density = requireContext().resources.displayMetrics.density
        val colWidthPx = (200 * density).toInt()
        stages.forEachIndexed { index, stage ->
            val stageLeads = leads.filter { it.stage == stage }
            val col = createColumn(stage, stageColors[index], stageLeads, colWidthPx)
            binding.kanbanContainer.addView(col)
        }
    }

    private fun createColumn(stage: String, color: String, leads: List<Lead>, widthPx: Int): LinearLayout {
        val density = requireContext().resources.displayMetrics.density
        val col = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(widthPx, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins((4 * density).toInt(), 0, (4 * density).toInt(), 0)
            }
            setBackgroundColor(Color.parseColor("#0F1422"))
            setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())
        }

        val header = TextView(requireContext()).apply {
            text = "$stage (${leads.size})"
            textSize = 11f
            setTextColor(Color.parseColor(color))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (36 * density).toInt())
        }
        col.addView(header)

        if (leads.isEmpty()) {
            val empty = TextView(requireContext()).apply {
                text = "No clients"
                textSize = 12f
                setTextColor(Color.parseColor("#4A5570"))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (60 * density).toInt())
            }
            col.addView(empty)
        } else {
            leads.forEach { lead ->
                col.addView(createLeadCard(lead, color))
            }
        }
        return col
    }

    private fun createLeadCard(lead: Lead, color: String): CardView {
        val density = requireContext().resources.displayMetrics.density
        val card = CardView(requireContext()).apply {
            radius = (8 * density)
            setCardBackgroundColor(Color.parseColor("#111520"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, (6 * density).toInt()) }
            cardElevation = 0f
        }
        val inner = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())
        }
        val nameView = TextView(requireContext()).apply {
            text = lead.name
            textSize = 13f
            setTextColor(Color.parseColor("#E8ECF5"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        val budgetView = TextView(requireContext()).apply {
            text = "${when { lead.budget >= 1_000_000 -> "${String.format("%.1f", lead.budget / 1_000_000)}M"; lead.budget >= 1_000 -> "${(lead.budget/1000).toInt()}K"; else -> lead.budget.toInt().toString() }} EGP"
            textSize = 12f
            setTextColor(Color.parseColor("#E2B96A"))
        }
        val heatView = TextView(requireContext()).apply {
            text = when(lead.heat) { "hot" -> "🔥 ساخن"; "warm" -> "🟡 دافئ"; else -> "🔵 بارد" }
            textSize = 11f
            setTextColor(Color.parseColor("#8896B0"))
        }
        inner.addView(nameView)
        inner.addView(budgetView)
        inner.addView(heatView)
        card.addView(inner)

        card.setOnClickListener {
            showStageChangeDialog(lead)
        }
        return card
    }

    private fun showStageChangeDialog(lead: Lead) {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("تغيير مرحلة ${lead.name}")
            .setItems(stages.toTypedArray()) { _, which ->
                val newStage = stages[which]
                if (newStage != lead.stage) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        db.leadDao().update(lead.copy(stage = newStage, lastContact = java.time.LocalDate.now().toString()))
                    }
                }
            }
            .create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
