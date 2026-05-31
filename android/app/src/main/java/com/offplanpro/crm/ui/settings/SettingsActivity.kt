package com.offplanpro.crm.ui.settings

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.offplanpro.crm.R
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.Lead
import com.offplanpro.crm.databinding.ActivitySettingsBinding
import com.offplanpro.crm.util.CsvManager
import com.offplanpro.crm.util.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var db: AppDatabase

    companion object {
        private const val RC_IMPORT_CSV = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("offplanpro_prefs", MODE_PRIVATE)
        db = AppDatabase.getInstance(this)

        setupToolbar()
        setupAgentName()
        setupTheme()
        setupDataButtons()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupAgentName() {
        val savedName = prefs.getString("broker_name", "") ?: ""
        binding.etAgentName.setText(savedName)
        binding.btnSaveName.setOnClickListener {
            val name = binding.etAgentName.text?.toString()?.trim() ?: ""
            prefs.edit().putString("broker_name", name).apply()
            Toast.makeText(this, "Name saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTheme() {
        val currentTheme = ThemeManager.getTheme(prefs)
        val radioToCheck = when (currentTheme) {
            "ocean" -> R.id.rb_ocean
            "purple" -> R.id.rb_purple
            "rose" -> R.id.rb_rose
            else -> R.id.rb_gold
        }
        binding.rgTheme.check(radioToCheck)

        binding.rgTheme.setOnCheckedChangeListener { group, checkedId ->
            val selectedView = group.findViewById<RadioButton>(checkedId)
            val theme = selectedView?.tag?.toString() ?: "gold"
            ThemeManager.saveTheme(prefs, theme)
            ThemeManager.applyTheme(theme)
        }
    }

    private fun setupDataButtons() {
        binding.btnExportLeads.setOnClickListener {
            lifecycleScope.launch {
                val leads = withContext(Dispatchers.IO) { db.leadDao().getAllLeadsList() }
                val csv = CsvManager.exportLeads(leads)
                CsvManager.shareAsCsv(this@SettingsActivity, "offplanpro_clients.csv", csv)
            }
        }

        binding.btnExportDeals.setOnClickListener {
            lifecycleScope.launch {
                val deals = withContext(Dispatchers.IO) { db.dealDao().getAllDealsList() }
                val csv = CsvManager.exportDeals(deals)
                CsvManager.shareAsCsv(this@SettingsActivity, "offplanpro_deals.csv", csv)
            }
        }

        binding.btnImportLeads.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "text/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            @Suppress("DEPRECATION")
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), RC_IMPORT_CSV)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMPORT_CSV && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            lifecycleScope.launch {
                try {
                    val csv = withContext(Dispatchers.IO) {
                        contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: ""
                    }
                    val leads: List<Lead> = CsvManager.importLeads(csv)
                    withContext(Dispatchers.IO) {
                        leads.forEach { db.leadDao().insert(it) }
                    }
                    Toast.makeText(
                        this@SettingsActivity,
                        "Imported ${leads.size} clients",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@SettingsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
