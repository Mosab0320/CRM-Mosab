package com.offplanpro.crm.ui.followups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.offplanpro.crm.data.AppDatabase
import com.offplanpro.crm.data.entity.FollowUp
import com.offplanpro.crm.databinding.FragmentFollowupsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowUpsFragment : Fragment() {
    private var _binding: FragmentFollowupsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFollowupsBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = FollowUpAdapter(
            onMarkDone = { followUp ->
                lifecycleScope.launch(Dispatchers.IO) {
                    db.followUpDao().update(followUp.copy(status = if (followUp.status == "done") "pending" else "done"))
                }
            },
            onDelete = { followUp ->
                lifecycleScope.launch(Dispatchers.IO) { db.followUpDao().delete(followUp) }
            }
        )
        binding.rvFollowups.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFollowups.adapter = adapter

        db.followUpDao().getAllFollowUps().observe(viewLifecycleOwner) { followUps ->
            adapter.submitList(followUps)
            binding.tvNoFollowups.visibility = if (followUps.isEmpty()) View.VISIBLE else View.GONE
            binding.rvFollowups.visibility = if (followUps.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.fabAdd.setOnClickListener {
            AddFollowUpBottomSheet.newInstance(null).show(childFragmentManager, "add_followup")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
