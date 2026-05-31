package com.offplanpro.crm.ui.client

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.CallLogEntry
import com.offplanpro.crm.data.entity.ClientNote
import com.offplanpro.crm.data.entity.Lead
import com.offplanpro.crm.databinding.ActivityClientProfileBinding
import com.offplanpro.crm.ui.leads.AddLeadBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ClientProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientProfileBinding
    private lateinit var db: AppDatabase
    private var leadId: Long = 0L
    private var currentLead: Lead? = null
    private lateinit var timelineAdapter: TimelineAdapter

    companion object {
        const val EXTRA_LEAD_ID = "extra_lead_id"
        private const val RC_ATTACH_FILE = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        leadId = intent.getLongExtra(EXTRA_LEAD_ID, 0L)
        db = AppDatabase.getInstance(this)

        setupToolbar()
        setupTimeline()
        setupActions()
        loadLead()
        observeTimeline()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnEdit.setOnClickListener {
            currentLead?.let { lead ->
                AddLeadBottomSheet.newInstance(lead)
                    .show(supportFragmentManager, "edit_lead")
            }
        }
    }

    private fun setupTimeline() {
        timelineAdapter = TimelineAdapter()
        binding.rvTimeline.layoutManager = LinearLayoutManager(this)
        binding.rvTimeline.adapter = timelineAdapter
        binding.rvTimeline.isNestedScrollingEnabled = false
    }

    private fun setupActions() {
        binding.btnCall.setOnClickListener {
            currentLead?.let { lead ->
                val phone = lead.phone.trim()
                if (phone.isNotEmpty()) {
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    startActivity(dialIntent)
                    showLogCallDialog(lead)
                } else {
                    Toast.makeText(this, "No phone number", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnWhatsapp.setOnClickListener {
            currentLead?.let { lead ->
                val phone = lead.phone.replace(Regex("[^\\d+]"), "")
                val url = "https://wa.me/$phone"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }

        binding.btnAttach.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            @Suppress("DEPRECATION")
            startActivityForResult(Intent.createChooser(intent, "Attach File"), RC_ATTACH_FILE)
        }

        binding.fabAddNote.setOnClickListener {
            showAddNoteDialog()
        }
    }

    private fun loadLead() {
        lifecycleScope.launch {
            val lead = withContext(Dispatchers.IO) {
                db.leadDao().getAllLeadsList().find { it.id == leadId }
            }
            lead?.let {
                currentLead = it
                bindLeadToViews(it)
            } ?: run {
                Toast.makeText(this@ClientProfileActivity, "Client not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun bindLeadToViews(lead: Lead) {
        binding.tvAvatar.text = lead.name.take(1).uppercase()
        binding.tvAvatar.background = getDrawable(
            when (lead.heat) {
                "hot" -> R.drawable.bg_avatar_gold
                "warm" -> R.drawable.bg_avatar_amber
                else -> R.drawable.bg_avatar_sky
            }
        )
        binding.tvName.text = lead.name
        binding.tvPhone.text = lead.phone

        val heatText = when (lead.heat) { "hot" -> "🔥 Hot"; "warm" -> "🟡 Warm"; else -> "🔵 Cold" }
        val heatColor = when (lead.heat) { "hot" -> "#F4506A"; "warm" -> "#F59E2B"; else -> "#38C4F8" }
        binding.tvHeatBadge.text = heatText
        binding.tvHeatBadge.setTextColor(Color.parseColor(heatColor))

        val stageColor = when (lead.stage) {
            "New Lead" -> "#38C4F8"
            "Contacted" -> "#A78BFA"
            "Viewing" -> "#F59E2B"
            "Offer" -> "#E2B96A"
            "Negotiation" -> "#F4506A"
            "Closed" -> "#2DD4A0"
            else -> "#8896B0"
        }
        binding.tvStageBadge.text = lead.stage
        binding.tvStageBadge.setTextColor(Color.parseColor(stageColor))

        binding.tvTypeBadge.text = lead.type
        binding.tvBudget.text = formatBudget(lead.budget)
        binding.tvProject.text = lead.project.ifEmpty { "Any" }
        binding.tvSource.text = lead.source.ifEmpty { "—" }
        binding.tvLastContact.text = lead.lastContact.take(10).ifEmpty { "—" }
    }

    private fun observeTimeline() {
        db.clientNoteDao().getNotesByLead(leadId).observe(this) { notes ->
            timelineAdapter.submitList(notes)
            binding.tvNoTimeline.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
            binding.rvTimeline.visibility = if (notes.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun showLogCallDialog(lead: Lead) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val tvTitle = TextView(this).apply {
            text = "Log Call with ${lead.name}"
            textSize = 16f
            setTextColor(Color.parseColor("#E8ECF5"))
            setPadding(0, 0, 0, 16)
        }
        layout.addView(tvTitle)

        val tvOutcomeLabel = TextView(this).apply {
            text = "Outcome:"
            textSize = 12f
            setTextColor(Color.parseColor("#8896B0"))
        }
        layout.addView(tvOutcomeLabel)

        val outcomes = arrayOf("No Answer", "Connected", "Callback Scheduled", "Deal Discussed")
        val spinnerOutcome = Spinner(this).apply {
            adapter = ArrayAdapter(this@ClientProfileActivity, android.R.layout.simple_spinner_item, outcomes)
                .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }
        layout.addView(spinnerOutcome)

        val tvDurationLabel = TextView(this).apply {
            text = "Duration:"
            textSize = 12f
            setTextColor(Color.parseColor("#8896B0"))
            setPadding(0, 16, 0, 0)
        }
        layout.addView(tvDurationLabel)

        val etDuration = EditText(this).apply {
            hint = "e.g. 2:35"
            setTextColor(Color.parseColor("#E8ECF5"))
            setHintTextColor(Color.parseColor("#4A5570"))
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        }
        layout.addView(etDuration)

        val tvNotesLabel = TextView(this).apply {
            text = "Notes:"
            textSize = 12f
            setTextColor(Color.parseColor("#8896B0"))
            setPadding(0, 16, 0, 0)
        }
        layout.addView(tvNotesLabel)

        val etNotes = EditText(this).apply {
            hint = "Call notes..."
            setTextColor(Color.parseColor("#E8ECF5"))
            setHintTextColor(Color.parseColor("#4A5570"))
            inputType = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 2
        }
        layout.addView(etNotes)

        AlertDialog.Builder(this)
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val outcome = spinnerOutcome.selectedItem?.toString() ?: "No Answer"
                val duration = etDuration.text?.toString()?.trim() ?: ""
                val notes = etNotes.text?.toString()?.trim() ?: ""
                val ts = nowTimestamp()

                lifecycleScope.launch(Dispatchers.IO) {
                    db.callLogDao().insert(
                        CallLogEntry(
                            leadId = leadId,
                            clientName = lead.name,
                            duration = duration,
                            notes = notes,
                            outcome = outcome,
                            timestamp = ts
                        )
                    )
                    db.clientNoteDao().insert(
                        ClientNote(
                            leadId = leadId,
                            text = "Call — $outcome${if (duration.isNotEmpty()) " ($duration)" else ""}${if (notes.isNotEmpty()) ": $notes" else ""}",
                            timestamp = ts,
                            type = "call"
                        )
                    )
                    currentLead?.let { l ->
                        val updated = l.copy(lastContact = ts.take(10))
                        db.leadDao().update(updated)
                        withContext(Dispatchers.Main) {
                            currentLead = updated
                            bindLeadToViews(updated)
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddNoteDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val tvTitle = TextView(this).apply {
            text = "Add Note"
            textSize = 16f
            setTextColor(Color.parseColor("#E8ECF5"))
            setPadding(0, 0, 0, 16)
        }
        layout.addView(tvTitle)

        val etNote = EditText(this).apply {
            hint = "Write a note..."
            setTextColor(Color.parseColor("#E8ECF5"))
            setHintTextColor(Color.parseColor("#4A5570"))
            inputType = android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
        }
        layout.addView(etNote)

        AlertDialog.Builder(this)
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val text = etNote.text?.toString()?.trim() ?: ""
                if (text.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        db.clientNoteDao().insert(
                            ClientNote(
                                leadId = leadId,
                                text = text,
                                timestamp = nowTimestamp(),
                                type = "note"
                            )
                        )
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_ATTACH_FILE && resultCode == RESULT_OK) {
            val uri: Uri = data?.data ?: return
            val uriString = uri.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                db.clientNoteDao().insert(
                    ClientNote(
                        leadId = leadId,
                        text = "Attachment: $uriString",
                        timestamp = nowTimestamp(),
                        type = "attachment"
                    )
                )
            }
        }
    }

    private fun nowTimestamp(): String =
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    private fun formatBudget(b: Double): String {
        return when {
            b >= 1_000_000 -> "${String.format("%.1f", b / 1_000_000)}M EGP"
            b >= 1_000 -> "${(b / 1_000).toInt()}K EGP"
            else -> "${b.toInt()} EGP"
        }
    }

    inner class TimelineAdapter : ListAdapter<ClientNote, TimelineAdapter.VH>(DIFF) {

        companion object {
            val DIFF = object : DiffUtil.ItemCallback<ClientNote>() {
                override fun areItemsTheSame(a: ClientNote, b: ClientNote) = a.id == b.id
                override fun areContentsTheSame(a: ClientNote, b: ClientNote) = a == b
            }
        }

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvIcon: TextView = v.findViewById(R.id.tv_icon)
            val tvText: TextView = v.findViewById(R.id.tv_text)
            val tvTimestamp: TextView = v.findViewById(R.id.tv_timestamp)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_timeline, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val note = getItem(position)
            val (icon, color) = when (note.type) {
                "call" -> "📞" to "#2DD4A0"
                "note" -> "📝" to "#A78BFA"
                "stage_change" -> "→" to "#38C4F8"
                "task" -> "✓" to "#F59E2B"
                "deal" -> "💰" to "#E2B96A"
                "attachment" -> "📎" to "#8896B0"
                else -> "•" to "#8896B0"
            }
            holder.tvIcon.text = icon
            holder.tvIcon.setTextColor(Color.parseColor(color))
            holder.tvText.text = note.text
            holder.tvTimestamp.text = note.timestamp.take(16).replace("T", " ")
        }
    }
}
