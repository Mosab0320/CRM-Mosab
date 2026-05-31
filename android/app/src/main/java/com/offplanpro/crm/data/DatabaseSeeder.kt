package com.offplanpro.crm.data

import com.offplanpro.crm.data.entity.*

object DatabaseSeeder {
    suspend fun seedIfEmpty(db: AppDatabase) {
        if (db.leadDao().getLeadsCount() > 0) return

        val leads = listOf(
            Lead(name="محمد خالد النجار", phone="01001234567", type="Buyer", heat="hot", stage="Viewing", budget=2500000.0, project="مدينتي - Q", unitType="Villa", source="فيسبوك", nationality="Egyptian", lastContact="2026-04-03", notes="يريد فيلا مع حديقة، جاهز للتفاوض", date="2026-04-01"),
            Lead(name="سارة أحمد رضا", phone="01112345678", type="Investor", heat="hot", stage="Offer", budget=4000000.0, project="الشيخ زايد - Z5", unitType="Duplex", source="توصية", nationality="Egyptian", lastContact="2026-04-04", notes="تبحث عن عائد إيجاري مرتفع", date="2026-04-02"),
            Lead(name="كريم عبدالله فتحي", phone="01234567890", type="Buyer", heat="warm", stage="Contacted", budget=1800000.0, project="أي مشروع قريب", unitType="Apartment", source="إنستغرام", nationality="Egyptian", lastContact="2026-04-02", notes="", date="2026-04-03"),
            Lead(name="منى سمير عوض", phone="01098765432", type="Buyer", heat="cold", stage="New Lead", budget=3000000.0, project="التجمع الخامس", unitType="Penthouse", source="موقع إلكتروني", nationality="خليجي", lastContact="2026-03-28", notes="لم يرد على المكالمات", date="2026-04-04"),
            Lead(name="أحمد حسن طارق", phone="01187654321", type="Investor", heat="warm", stage="Negotiation", budget=6000000.0, project="العلمين الجديدة", unitType="Apartment", source="WhatsApp", nationality="Egyptian", lastContact="2026-04-04", notes="يريد شقتين", date="2026-03-30"),
            Lead(name="دينا محمود سلام", phone="01278901234", type="Buyer", heat="hot", stage="Closed", budget=1500000.0, project="أكتوبر - M1", unitType="Apartment", source="فيسبوك", nationality="Egyptian", lastContact="2026-04-01", notes="تم الإغلاق بنجاح", date="2026-03-25")
        )
        leads.forEach { db.leadDao().insert(it) }

        val deals = listOf(
            Deal(client="دينا محمود سلام", project="أكتوبر - M1", unit="A-304", type="بيع أوف بلان", value=1500000.0, commPct=3.0, commTotal=45000.0, myPct=100.0, myComm=45000.0, date="2026-04-01", collectDate="2026-05-01", status="Completed", contractStage="توقيع العقد النهائي", notes=""),
            Deal(client="أميرة طارق", project="الشيخ زايد Z5", unit="B-101", type="بيع أوف بلان", value=3800000.0, commPct=3.0, commTotal=114000.0, myPct=50.0, myComm=57000.0, date="2026-03-15", collectDate="2026-04-15", status="Completed", contractStage="سداد كامل", notes="شراكة مع بروكر آخر"),
            Deal(client="أحمد حسن طارق", project="العلمين الجديدة", unit="C-502", type="بيع أوف بلان", value=2800000.0, commPct=3.0, commTotal=84000.0, myPct=100.0, myComm=84000.0, date="2026-04-03", collectDate="", status="Ongoing", contractStage="عقد مبدئي", notes="في انتظار التحويل")
        )
        deals.forEach { db.dealDao().insert(it) }

        val projects = listOf(
            Project(name="مدينتي - Q", developer="المجموعة العربية", location="مدينتي", delivery="2027-12-01", priceFrom=2000000.0, priceTo=8000000.0, down=10.0, years=8, comm=3.0, status="نشط", desc="فيلات وتاون هاوس - حمام سباحة وجيم ونادي رياضي"),
            Project(name="الشيخ زايد Z5", developer="تطوير مصر", location="الشيخ زايد", delivery="2028-06-01", priceFrom=3000000.0, priceTo=12000000.0, down=15.0, years=10, comm=3.0, status="نشط", desc="مشروع متكامل - مول تجاري داخلي"),
            Project(name="أكتوبر M1", developer="الإسكان والتعمير", location="6 أكتوبر", delivery="2026-06-01", priceFrom=1200000.0, priceTo=3500000.0, down=10.0, years=7, comm=2.5, status="نشط", desc="شقق وأدوبلكس - تسليم قريب"),
            Project(name="العلمين الجديدة", developer="العاصمة للتطوير", location="العلمين", delivery="2028-12-01", priceFrom=2500000.0, priceTo=15000000.0, down=20.0, years=10, comm=4.0, status="نشط", desc="ساحلي - إطلالة بحرية")
        )
        projects.forEach { db.projectDao().insert(it) }

        val units = listOf(
            CrmUnit(code="A-204", project="مدينتي - Q", type="Villa", area=320.0, rooms=4, price=5500000.0, status="متاح", desc="طابق أرضي + أول - حديقة خاصة"),
            CrmUnit(code="B-101", project="الشيخ زايد Z5", type="Duplex", area=250.0, rooms=4, price=4200000.0, status="مباع", desc="طابق 1+2 - إطلالة حديقة"),
            CrmUnit(code="C-502", project="العلمين الجديدة", type="Apartment", area=150.0, rooms=3, price=2800000.0, status="محجوز", desc="طابق 5 - إطلالة بحرية"),
            CrmUnit(code="D-301", project="أكتوبر M1", type="Apartment", area=130.0, rooms=3, price=1800000.0, status="متاح", desc=""),
            CrmUnit(code="E-705", project="العلمين الجديدة", type="Penthouse", area=400.0, rooms=5, price=12000000.0, status="متاح", desc="روف - إطلالة 360")
        )
        units.forEach { db.unitDao().insert(it) }

        val tasks = listOf(
            CrmTask(title="معاينة فيلا مع محمد النجار", type="Viewing", priority="Urgent", date="2026-04-06T10:00", client="محمد خالد النجار", project="مدينتي - Q", notes="", done=false),
            CrmTask(title="إرسال عرض السعر لسارة رضا", type="Contracts", priority="Important", date="2026-04-05T14:00", client="سارة أحمد رضا", project="الشيخ زايد Z5", notes="", done=false),
            CrmTask(title="متابعة أحمد حسن - التحويل البنكي", type="Follow-up", priority="Urgent", date="2026-04-05T11:00", client="أحمد حسن طارق", project="العلمين", notes="", done=false),
            CrmTask(title="مكالمة كريم فتحي", type="Call", priority="Normal", date="2026-04-07T09:00", client="كريم عبدالله فتحي", project="", notes="", done=false),
            CrmTask(title="اجتماع مطور مدينتي - الحصص الجديدة", type="Meeting", priority="Important", date="2026-04-08T13:00", client="", project="مدينتي - Q", notes="", done=true)
        )
        tasks.forEach { db.taskDao().insert(it) }

        val followups = listOf(
            FollowUp(client="منى سمير عوض", type="مكالمة هاتفية", date="2026-04-04", nextDate="2026-04-07", notes="لم ترد - أعيد المحاولة", status="pending"),
            FollowUp(client="كريم عبدالله فتحي", type="WhatsApp", date="2026-04-03", nextDate="2026-04-06", notes="أرسلت كتالوج المشاريع - ينتظر الرد", status="pending"),
            FollowUp(client="دينا محمود سلام", type="مكالمة هاتفية", date="2026-04-01", nextDate="", notes="تم الإغلاق بنجاح", status="done")
        )
        followups.forEach { db.followUpDao().insert(it) }

        db.goalDao().insert(Goal(dealsM=5, commM=200000.0, leadsM=30, visitsM=15, followupsM=50, dealsY=50, commY=2000000.0, brokerName="البروكر"))

        val activities = listOf(
            ActivityItem(text="تم تسجيل صفقة: دينا محمود - أكتوبر M1", color="#E2B96A", time="منذ 3 أيام"),
            ActivityItem(text="ليد جديد: منى سمير عوض من الموقع الإلكتروني", color="#38C4F8", time="منذ يوم"),
            ActivityItem(text="مرحلة محدّثة: أحمد حسن ← تفاوض", color="#A78BFA", time="منذ يوم"),
            ActivityItem(text="معاينة مجدولة: محمد النجار - مدينتي", color="#F59E2B", time="منذ ساعة"),
            ActivityItem(text="صفقة مكتملة: أميرة طارق - الشيخ زايد", color="#2DD4A0", time="منذ 20 يوم")
        )
        activities.forEach { db.activityDao().insert(it) }
    }
}
