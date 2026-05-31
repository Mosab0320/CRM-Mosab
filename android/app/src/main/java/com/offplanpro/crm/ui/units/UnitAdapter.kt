package com.offplanpro.crm.ui.units

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.offplanpro.crm.R
import com.offplanpro.crm.data.entity.CrmUnit

class UnitAdapter : ListAdapter<CrmUnit, UnitAdapter.VH>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CrmUnit>() {
            override fun areItemsTheSame(a: CrmUnit, b: CrmUnit) = a.id == b.id
            override fun areContentsTheSame(a: CrmUnit, b: CrmUnit) = a == b
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_unit, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val unit = getItem(position)
        val v = holder.itemView
        v.findViewById<TextView>(R.id.tv_code)?.text = unit.code
        v.findViewById<TextView>(R.id.tv_project_name)?.text = unit.project
        v.findViewById<TextView>(R.id.tv_type)?.text = unit.type
        v.findViewById<TextView>(R.id.tv_area)?.text = "${unit.area.toInt()} م²"
        v.findViewById<TextView>(R.id.tv_rooms)?.text = "${unit.rooms} غرف"
        v.findViewById<TextView>(R.id.tv_price)?.text = formatPrice(unit.price)
        v.findViewById<TextView>(R.id.tv_desc)?.text = unit.desc
        val statusView = v.findViewById<TextView>(R.id.tv_status_badge)
        statusView?.text = unit.status
        when (unit.status) {
            "متاح" -> { statusView?.setTextColor(Color.parseColor("#2DD4A0")); statusView?.setBackgroundColor(Color.parseColor("#202DD4A0")) }
            "محجوز" -> { statusView?.setTextColor(Color.parseColor("#F59E2B")); statusView?.setBackgroundColor(Color.parseColor("#20F59E2B")) }
            "مباع" -> { statusView?.setTextColor(Color.parseColor("#F4506A")); statusView?.setBackgroundColor(Color.parseColor("#20F4506A")) }
        }
    }

    private fun formatPrice(p: Double): String {
        return when {
            p >= 1_000_000 -> "${String.format("%.1f", p / 1_000_000)}م EGP"
            p >= 1_000 -> "${(p / 1_000).toInt()}ك EGP"
            else -> "${p.toInt()} EGP"
        }
    }
}
