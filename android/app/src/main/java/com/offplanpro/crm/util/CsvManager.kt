package com.offplanpro.crm.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.offplanpro.crm.data.entity.Deal
import com.offplanpro.crm.data.entity.Lead
import java.io.File

object CsvManager {

    private val LEAD_HEADER = "name,phone,type,heat,stage,budget,project,source,nationality,notes,date"
    private val DEAL_HEADER = "client,project,unit,type,value,commPct,commTotal,myPct,myComm,date,collectDate,status,contractStage,notes"

    fun exportLeads(leads: List<Lead>): String {
        val sb = StringBuilder(LEAD_HEADER).append("\n")
        leads.forEach { lead ->
            sb.append(
                listOf(
                    escape(lead.name),
                    escape(lead.phone),
                    escape(lead.type),
                    escape(lead.heat),
                    escape(lead.stage),
                    lead.budget.toString(),
                    escape(lead.project),
                    escape(lead.source),
                    escape(lead.nationality),
                    escape(lead.notes),
                    escape(lead.date)
                ).joinToString(",")
            ).append("\n")
        }
        return sb.toString()
    }

    fun exportDeals(deals: List<Deal>): String {
        val sb = StringBuilder(DEAL_HEADER).append("\n")
        deals.forEach { deal ->
            sb.append(
                listOf(
                    escape(deal.client),
                    escape(deal.project),
                    escape(deal.unit),
                    escape(deal.type),
                    deal.value.toString(),
                    deal.commPct.toString(),
                    deal.commTotal.toString(),
                    deal.myPct.toString(),
                    deal.myComm.toString(),
                    escape(deal.date),
                    escape(deal.collectDate),
                    escape(deal.status),
                    escape(deal.contractStage),
                    escape(deal.notes)
                ).joinToString(",")
            ).append("\n")
        }
        return sb.toString()
    }

    fun importLeads(csv: String): List<Lead> {
        val lines = csv.trim().lines()
        if (lines.size < 2) return emptyList()
        return lines.drop(1).mapNotNull { line ->
            val cols = parseLine(line)
            if (cols.size < 11) return@mapNotNull null
            try {
                Lead(
                    name = cols[0],
                    phone = cols[1],
                    type = cols[2],
                    heat = cols[3],
                    stage = cols[4],
                    budget = cols[5].toDoubleOrNull() ?: 0.0,
                    project = cols[6],
                    source = cols[7],
                    nationality = cols[8],
                    notes = cols[9],
                    date = cols[10]
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun shareAsCsv(context: Context, filename: String, content: String) {
        val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(cacheDir, filename)
        file.writeText(content)
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, filename)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share CSV"))
    }

    private fun escape(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun parseLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                ch == '"' && !inQuotes -> inQuotes = true
                ch == '"' && inQuotes && i + 1 < line.length && line[i + 1] == '"' -> {
                    sb.append('"'); i++
                }
                ch == '"' && inQuotes -> inQuotes = false
                ch == ',' && !inQuotes -> {
                    result.add(sb.toString()); sb.clear()
                }
                else -> sb.append(ch)
            }
            i++
        }
        result.add(sb.toString())
        return result
    }
}
