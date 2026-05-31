package com.offplanpro.crm.ui.followups

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
import com.offplanpro.crm.data.entity.FollowUp

class FollowUpAdapter(
    private val onMarkDone: (FollowUp) -> Unit,
    private val onDelete: (FollowUp) -> Unit
) : ListAdapter<FollowUp, FollowUpAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<FollowUp>() {
            override fun areItemsTheSame(a: FollowUp, b: FollowUp) = a.id == b.id
            override fun areContentsTheSame(a: FollowUp, b: FollowUp) = a == b
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_followup, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val fu = getItem(position)
        val v = holder.itemView

        v.findViewById<TextView>(R.id.tv_client)?.text = fu.client
        v.findViewById<TextView>(R.id.tv_type_badge)?.text = fu.type
        v.findViewById<TextView>(R.id.tv_date)?.text = "📅 ${fu.date}"
        v.findViewById<TextView>(R.id.tv_next_date)?.text = if (fu.nextDate.isNotEmpty()) "التالي: ${fu.nextDate}" else ""
        v.findViewById<TextView>(R.id.tv_notes)?.text = fu.notes.ifEmpty { "" }

        val statusBadge = v.findViewById<TextView>(R.id.tv_status_badge)
        if (fu.status == "done") {
            statusBadge?.text = "مكتملة"
            statusBadge?.setTextColor(Color.parseColor("#2DD4A0"))
            statusBadge?.setBackgroundColor(Color.parseColor("#202DD4A0"))
            v.alpha = 0.6f
        } else {
            statusBadge?.text = "معلقة"
            statusBadge?.setTextColor(Color.parseColor("#F59E2B"))
            statusBadge?.setBackgroundColor(Color.parseColor("#20F59E2B"))
            v.alpha = 1f
        }

        val btnDone = v.findViewById<Button>(R.id.btn_done)
        btnDone?.text = if (fu.status == "done") "↩ إعادة" else "✓ تم"
        btnDone?.setOnClickListener { onMarkDone(fu) }
    }
}
