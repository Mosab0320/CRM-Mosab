package com.offplanpro.crm.ui.projects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.offplanpro.crm.R
import com.offplanpro.crm.data.entity.Project

class ProjectAdapter : ListAdapter<Project, ProjectAdapter.VH>(DIFF) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Project>() {
            override fun areItemsTheSame(a: Project, b: Project) = a.id == b.id
            override fun areContentsTheSame(a: Project, b: Project) = a == b
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val project = getItem(position)
        val v = holder.itemView
        v.findViewById<TextView>(R.id.tv_project_name)?.text = project.name
        v.findViewById<TextView>(R.id.tv_developer)?.text = project.developer
        v.findViewById<TextView>(R.id.tv_location)?.text = "📍 ${project.location}"
        v.findViewById<TextView>(R.id.tv_delivery)?.text = "تسليم: ${project.delivery.take(7)}"
        v.findViewById<TextView>(R.id.tv_comm_badge)?.text = "${project.comm}% عمولة"
        v.findViewById<TextView>(R.id.tv_price_range)?.text = "${formatPrice(project.priceFrom)} — ${formatPrice(project.priceTo)}"
        v.findViewById<TextView>(R.id.tv_down)?.text = "${project.down}%"
        v.findViewById<TextView>(R.id.tv_years)?.text = "${project.years} سنة"
    }

    private fun formatPrice(p: Double): String {
        return when {
            p >= 1_000_000 -> "${String.format("%.1f", p / 1_000_000)}م"
            p >= 1_000 -> "${(p / 1_000).toInt()}ك"
            else -> "${p.toInt()}"
        }
    }
}
