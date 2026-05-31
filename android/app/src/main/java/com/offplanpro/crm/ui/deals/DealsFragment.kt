package com.offplanpro.crm.ui.deals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.databinding.FragmentDealsBinding

class DealsFragment : Fragment() {
    private var _binding: FragmentDealsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDealsBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = DealAdapter()
        binding.rvDeals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDeals.adapter = adapter

        db.dealDao().getAllDeals().observe(viewLifecycleOwner) { deals ->
            adapter.submitList(deals)
            val completed = deals.filter { it.status == "مكتملة" }
            val totalComm = completed.sumOf { it.myComm }
            binding.tvTotalComm.text = "${formatNum(totalComm)} ج.م"
            binding.tvCompletedDeals?.text = completed.size.toString()
            binding.tvNoDeals.visibility = if (deals.isEmpty()) View.VISIBLE else View.GONE
            binding.rvDeals.visibility = if (deals.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.fabAdd.setOnClickListener {
            android.widget.Toast.makeText(requireContext(), "إضافة صفقة - قريباً", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatNum(n: Double): String {
        return when {
            n >= 1_000_000 -> "${String.format("%.1f", n / 1_000_000)}م"
            n >= 1_000 -> "${(n / 1_000).toInt()}ك"
            else -> n.toInt().toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
