package com.offplanpro.crm.data

import com.offplanpro.crm.data.entity.*

object DatabaseSeeder {
    suspend fun seedIfEmpty(db: AppDatabase) {
        if (db.leadDao().getLeadsCount() > 0) return

        val leads = listOf(
            Lead(name="Mohamed Khaled", phone="01001234567", type="Buyer", heat="hot", stage="Viewing", budget=2500000.0, project="Madinaty - Q", unitType="Villa", source="Facebook", nationality="Egyptian", lastContact="2026-04-03", notes="Wants villa with garden, ready to negotiate", date="2026-04-01"),
            Lead(name="Sara Ahmed", phone="01112345678", type="Investor", heat="hot", stage="Offer", budget=4000000.0, project="Sheikh Zayed - Z5", unitType="Duplex", source="Referral", nationality="Egyptian", lastContact="2026-04-04", notes="Looking for high rental yield", date="2026-04-02"),
            Lead(name="Karim Abdullah", phone="01234567890", type="Buyer", heat="warm", stage="Contacted", budget=1800000.0, project="Any nearby project", unitType="Apartment", source="Instagram", nationality="Egyptian", lastContact="2026-04-02", notes="", date="2026-04-03"),
            Lead(name="Mona Samir", phone="01098765432", type="Buyer", heat="cold", stage="New Lead", budget=3000000.0, project="Fifth Settlement", unitType="Penthouse", source="Website", nationality="Gulf", lastContact="2026-03-28", notes="Not answering calls", date="2026-04-04"),
            Lead(name="Ahmed Hassan", phone="01187654321", type="Investor", heat="warm", stage="Negotiation", budget=6000000.0, project="New Alamein", unitType="Apartment", source="WhatsApp", nationality="Egyptian", lastContact="2026-04-04", notes="Wants two apartments", date="2026-03-30"),
            Lead(name="Dina Mahmoud", phone="01278901234", type="Buyer", heat="hot", stage="Closed", budget=1500000.0, project="October - M1", unitType="Apartment", source="Facebook", nationality="Egyptian", lastContact="2026-04-01", notes="Successfully closed", date="2026-03-25")
        )
        leads.forEach { db.leadDao().insert(it) }

        val deals = listOf(
            Deal(client="Dina Mahmoud", project="October - M1", unit="A-304", type="Off-Plan Sale", value=1500000.0, commPct=3.0, commTotal=45000.0, myPct=100.0, myComm=45000.0, date="2026-04-01", collectDate="2026-05-01", status="Completed", contractStage="Final Contract Signed", notes=""),
            Deal(client="Amira Tarek", project="Sheikh Zayed Z5", unit="B-101", type="Off-Plan Sale", value=3800000.0, commPct=3.0, commTotal=114000.0, myPct=50.0, myComm=57000.0, date="2026-03-15", collectDate="2026-04-15", status="Completed", contractStage="Full Payment", notes="Co-brokered"),
            Deal(client="Ahmed Hassan", project="New Alamein", unit="C-502", type="Off-Plan Sale", value=2800000.0, commPct=3.0, commTotal=84000.0, myPct=100.0, myComm=84000.0, date="2026-04-03", collectDate="", status="Ongoing", contractStage="Preliminary Contract", notes="Awaiting transfer")
        )
        deals.forEach { db.dealDao().insert(it) }

        val projects = listOf(
            Project(name="Madinaty - Q", developer="Arab Group", location="Madinaty", delivery="2027-12-01", priceFrom=2000000.0, priceTo=8000000.0, down=10.0, years=8, comm=3.0, status="Active", desc="Villas & townhouses - pool, gym, sports club"),
            Project(name="Sheikh Zayed Z5", developer="Tatweer Misr", location="Sheikh Zayed", delivery="2028-06-01", priceFrom=3000000.0, priceTo=12000000.0, down=15.0, years=10, comm=3.0, status="Active", desc="Integrated development - internal mall"),
            Project(name="October M1", developer="Housing & Development", location="6th October", delivery="2026-06-01", priceFrom=1200000.0, priceTo=3500000.0, down=10.0, years=7, comm=2.5, status="Active", desc="Apartments & duplexes - near delivery"),
            Project(name="New Alamein", developer="Capital Development", location="Alamein", delivery="2028-12-01", priceFrom=2500000.0, priceTo=15000000.0, down=20.0, years=10, comm=4.0, status="Active", desc="Coastal - sea view")
        )
        projects.forEach { db.projectDao().insert(it) }

        val units = listOf(
            CrmUnit(code="A-204", project="Madinaty - Q", type="Villa", area=320.0, rooms=4, price=5500000.0, status="Available", desc="Ground + 1st floor - private garden"),
            CrmUnit(code="B-101", project="Sheikh Zayed Z5", type="Duplex", area=250.0, rooms=4, price=4200000.0, status="Sold", desc="Floors 1+2 - garden view"),
            CrmUnit(code="C-502", project="New Alamein", type="Apartment", area=150.0, rooms=3, price=2800000.0, status="Reserved", desc="Floor 5 - sea view"),
            CrmUnit(code="D-301", project="October M1", type="Apartment", area=130.0, rooms=3, price=1800000.0, status="Available", desc=""),
            CrmUnit(code="E-705", project="New Alamein", type="Penthouse", area=400.0, rooms=5, price=12000000.0, status="Available", desc="Rooftop - 360 view")
        )
        units.forEach { db.unitDao().insert(it) }

        val tasks = listOf(
            CrmTask(title="Villa viewing with Mohamed", type="Viewing", priority="Urgent", date="2026-04-06T10:00", client="Mohamed Khaled", project="Madinaty - Q", notes="", done=false),
            CrmTask(title="Send price offer to Sara", type="Contracts", priority="Important", date="2026-04-05T14:00", client="Sara Ahmed", project="Sheikh Zayed Z5", notes="", done=false),
            CrmTask(title="Follow up Ahmed - bank transfer", type="Follow-up", priority="Urgent", date="2026-04-05T11:00", client="Ahmed Hassan", project="New Alamein", notes="", done=false),
            CrmTask(title="Call Karim Abdullah", type="Call", priority="Normal", date="2026-04-07T09:00", client="Karim Abdullah", project="", notes="", done=false),
            CrmTask(title="Developer meeting - new allocations", type="Meeting", priority="Important", date="2026-04-08T13:00", client="", project="Madinaty - Q", notes="", done=true)
        )
        tasks.forEach { db.taskDao().insert(it) }

        val followups = listOf(
            FollowUp(client="Mona Samir", type="Phone Call", date="2026-04-04", nextDate="2026-04-07", notes="No answer - retry", status="pending"),
            FollowUp(client="Karim Abdullah", type="WhatsApp", date="2026-04-03", nextDate="2026-04-06", notes="Sent project catalog - awaiting reply", status="pending"),
            FollowUp(client="Dina Mahmoud", type="Phone Call", date="2026-04-01", nextDate="", notes="Successfully closed", status="done")
        )
        followups.forEach { db.followUpDao().insert(it) }

        db.goalDao().insert(Goal(dealsM=5, commM=200000.0, leadsM=30, visitsM=15, followupsM=50, dealsY=50, commY=2000000.0, brokerName="Agent"))

        val activities = listOf(
            ActivityItem(text="Deal closed: Dina Mahmoud - October M1", color="#E2B96A", time="3 days ago"),
            ActivityItem(text="New lead: Mona Samir from Website", color="#38C4F8", time="1 day ago"),
            ActivityItem(text="Stage updated: Ahmed Hassan → Negotiation", color="#A78BFA", time="1 day ago"),
            ActivityItem(text="Viewing scheduled: Mohamed Khaled - Madinaty", color="#F59E2B", time="1 hour ago"),
            ActivityItem(text="Deal completed: Amira Tarek - Sheikh Zayed", color="#2DD4A0", time="20 days ago")
        )
        activities.forEach { db.activityDao().insert(it) }
    }
}
