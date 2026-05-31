package com.offplanpro.crm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.offplanpro.crm.databinding.ActivityMainBinding
import com.offplanpro.crm.ui.dashboard.DashboardFragment
import com.offplanpro.crm.ui.deals.DealsFragment
import com.offplanpro.crm.ui.followups.FollowUpsFragment
import com.offplanpro.crm.ui.leads.LeadsFragment
import com.offplanpro.crm.ui.pipeline.PipelineFragment
import com.offplanpro.crm.ui.projects.ProjectsFragment
import com.offplanpro.crm.ui.tasks.TasksFragment
import com.offplanpro.crm.ui.units.UnitsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_dashboard -> DashboardFragment()
                R.id.nav_leads -> LeadsFragment()
                R.id.nav_pipeline -> PipelineFragment()
                R.id.nav_projects -> ProjectsFragment()
                R.id.nav_units -> UnitsFragment()
                R.id.nav_deals -> DealsFragment()
                R.id.nav_tasks -> TasksFragment()
                R.id.nav_followups -> FollowUpsFragment()
                else -> DashboardFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
