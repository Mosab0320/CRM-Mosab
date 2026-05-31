package com.offplanpro.crm.ui.tasks

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.offplanpro.crm.R
import com.offplanpro.crm.data.entity.CrmTask

class TaskAdapter(
    private val onMarkDone: (CrmTask) -> Unit
) : ListAdapter<CrmTask, TaskAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CrmTask>() {
            override fun areItemsTheSame(a: CrmTask, b: CrmTask) = a.id == b.id
            override fun areContentsTheSame(a: CrmTask, b: CrmTask) = a == b
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = getItem(position)
        val v = holder.itemView

        val icon = when(task.type) {
            "مكالمة" -> "📞"; "معاينة" -> "🏠"; "اجتماع" -> "👥"
            "متابعة" -> "🔄"; "أوراق وعقود" -> "📄"; "تسجيل" -> "📝"; else -> "📌"
        }
        v.findViewById<TextView>(R.id.tv_title)?.text = "$icon ${task.title}"
        v.findViewById<TextView>(R.id.tv_date)?.text = task.date.take(10)
        v.findViewById<TextView>(R.id.tv_client)?.text = if (task.client.isNotEmpty()) "👤 ${task.client}" else ""
        v.findViewById<TextView>(R.id.tv_project)?.text = if (task.project.isNotEmpty()) "🏗️ ${task.project}" else ""

        val priorityBadge = v.findViewById<TextView>(R.id.tv_priority_badge)
        priorityBadge?.text = task.priority
        val pColor = when(task.priority) {
            "عاجلة" -> "#F4506A"; "مهمة" -> "#F59E2B"; else -> "#38C4F8"
        }
        priorityBadge?.setTextColor(Color.parseColor(pColor))

        val notesView = v.findViewById<TextView>(R.id.tv_notes)
        if (task.notes.isNotEmpty()) {
            notesView?.text = task.notes
            notesView?.visibility = View.VISIBLE
        } else {
            notesView?.visibility = View.GONE
        }

        val btnDone = v.findViewById<Button>(R.id.btn_mark_done)
        if (task.done) {
            btnDone?.text = "↩ إلغاء الإنجاز"
            v.alpha = 0.5f
        } else {
            btnDone?.text = "✓ تم الإنجاز"
            v.alpha = 1f
        }
        btnDone?.visibility = View.VISIBLE
        btnDone?.setOnClickListener { onMarkDone(task) }
    }
}
