package com.offplanpro.crm.ui.deals

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.offplanpro.crm.R
import com.offplanpro.crm.data.entity.Deal

class DealAdapter : ListAdapter<Deal, DealAdapter.VH>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Deal>() {
            override fun areItemsTheSame(a: Deal, b: Deal) = a.id == b.id
            override fun areContentsTheSame(a: Deal, b: Deal) = a == b
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_deal, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val deal = getItem(position)
        val v = holder.itemView
        v.findViewById<TextView>(R.id.tv_client)?.text = deal.client
        v.findViewById<TextView>(R.id.tv_project)?.text = deal.project
        v.findViewById<TextView>(R.id.tv_value)?.text = formatPrice(deal.value)
        v.findViewById<TextView>(R.id.tv_my_comm)?.text = formatPrice(deal.myComm)
        v.findViewById<TextView>(R.id.tv_date)?.text = deal.date.take(10)

        val statusBadge = v.findViewById<TextView>(R.id.tv_status_badge)
        statusBadge?.text = deal.status
        when (deal.status) {
            "Completed" -> { statusBadge?.setTextColor(Color.parseColor("#2DD4A0")); statusBadge?.setBackgroundColor(Color.parseColor("#202DD4A0")) }
            "Ongoing" -> { statusBadge?.setTextColor(Color.parseColor("#F59E2B")); statusBadge?.setBackgroundColor(Color.parseColor("#20F59E2B")) }
            "Cancelled" -> { statusBadge?.setTextColor(Color.parseColor("#F4506A")); statusBadge?.setBackgroundColor(Color.parseColor("#20F4506A")) }
        }
    }

    private fun formatPrice(p: Double): String {
        return when {
            p >= 1_000_000 -> "${String.format("%.1f", p / 1_000_000)}M EGP"
            p >= 1_000 -> "${(p / 1_000).toInt()}K EGP"
            else -> "${p.toInt()} EGP"
        }
    }
}
