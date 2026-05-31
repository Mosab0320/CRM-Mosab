package com.offplanpro.crm.ui.leads

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.offplanpro.crm.R
import com.offplanpro.crm.data.entity.Lead
import com.offplanpro.crm.ui.client.ClientProfileActivity

class LeadAdapter(
    private val onDelete: (Lead) -> Unit,
    private val onEdit: (Lead) -> Unit,
    private val onAdvance: (Lead) -> Unit = {},
    private val onCall: (Lead) -> Unit = {}
) : ListAdapter<Lead, LeadAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Lead>() {
            override fun areItemsTheSame(a: Lead, b: Lead) = a.id == b.id
            override fun areContentsTheSame(a: Lead, b: Lead) = a == b
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvAvatar: TextView? = v.findViewById(R.id.tv_avatar)
        val tvName: TextView? = v.findViewById(R.id.tv_name)
        val tvPhone: TextView? = v.findViewById(R.id.tv_phone)
        val tvStageBadge: TextView? = v.findViewById(R.id.tv_stage_badge)
        val tvBudget: TextView? = v.findViewById(R.id.tv_budget)
        val tvHeat: TextView? = v.findViewById(R.id.tv_heat)
        val tvTypeBadge: TextView? = v.findViewById(R.id.tv_type_badge)
        val tvProject: TextView? = v.findViewById(R.id.tv_project)
        val tvSource: TextView? = v.findViewById(R.id.tv_source)
        val tvLastContact: TextView? = v.findViewById(R.id.tv_last_contact)
        val btnAdvance: Button? = v.findViewById(R.id.btn_advance)
        val btnCall: Button? = v.findViewById(R.id.btn_call)
        val btnDelete: Button? = v.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_lead, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val lead = getItem(position)
        holder.tvAvatar?.text = lead.name.take(1)
        holder.tvAvatar?.background = holder.itemView.context.getDrawable(
            when(lead.heat) { "hot" -> R.drawable.bg_avatar_gold; "warm" -> R.drawable.bg_avatar_amber; else -> R.drawable.bg_avatar_sky }
        )
        holder.tvName?.text = lead.name
        holder.tvPhone?.text = lead.phone

        val stageColor = when(lead.stage) {
            "New Lead" -> "#38C4F8"
            "Contacted" -> "#A78BFA"
            "Viewing" -> "#F59E2B"
            "Offer" -> "#E2B96A"
            "Negotiation" -> "#F4506A"
            "Closed" -> "#2DD4A0"
            else -> "#8896B0"
        }
        holder.tvStageBadge?.text = lead.stage
        holder.tvStageBadge?.setTextColor(Color.parseColor(stageColor))

        val heatText = when(lead.heat) { "hot" -> "🔥 Hot"; "warm" -> "🟡 Warm"; else -> "🔵 Cold" }
        val heatColor = when(lead.heat) { "hot" -> "#F4506A"; "warm" -> "#F59E2B"; else -> "#38C4F8" }
        holder.tvHeat?.text = heatText
        holder.tvHeat?.setTextColor(Color.parseColor(heatColor))

        holder.tvTypeBadge?.text = lead.type
        holder.tvBudget?.text = formatBudget(lead.budget)
        holder.tvProject?.text = lead.project.ifEmpty { "Any Project" }
        holder.tvSource?.text = lead.source
        holder.tvLastContact?.text = lead.lastContact.take(10)

        holder.btnAdvance?.setOnClickListener { onAdvance(lead) }
        holder.btnCall?.setOnClickListener { onCall(lead) }
        holder.btnDelete?.setOnClickListener { onDelete(lead) }
        holder.itemView.setOnLongClickListener { onEdit(lead); true }
        holder.itemView.setOnClickListener {
            val ctx = holder.itemView.context
            val intent = Intent(ctx, ClientProfileActivity::class.java).apply {
                putExtra(ClientProfileActivity.EXTRA_LEAD_ID, lead.id)
            }
            ctx.startActivity(intent)
        }
    }

    private fun formatBudget(b: Double): String {
        return when {
            b >= 1_000_000 -> "${String.format("%.1f", b / 1_000_000)}M EGP"
            b >= 1_000 -> "${(b / 1_000).toInt()}K EGP"
            else -> "${b.toInt()} EGP"
        }
    }
}
