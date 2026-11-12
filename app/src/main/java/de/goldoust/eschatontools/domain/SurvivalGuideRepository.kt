package de.goldoust.eschatontools.domain

object SurvivalGuideRepository {

    fun getProtocols(isFarsi: Boolean = false): List<SurvivalProtocol> {
        return if (isFarsi) getFarsiProtocols() else getEnglishProtocols()
    }

    private fun getEnglishProtocols(): List<SurvivalProtocol> = listOf(
        // 1. CPR
        SurvivalProtocol(
            id = "cpr_protocol",
            title = "CPR & Cardiac Arrest Protocol",
            category = ProtocolCategory.FIRST_AID,
            summary = "Perform high-quality continuous chest compressions (100-120 bpm) followed by 2 rescue breaths until victim resumes breathing or medical aid arrives.",
            urgency = UrgencyLevel.CRITICAL,
            requiredTools = listOf("Nitrile Gloves", "CPR Face Shield / Barrier", "AED (if available)"),
            warnings = listOf(
                "Do NOT interrupt chest compressions for more than 10 seconds.",
                "Ensure victim is lying flat on a firm, hard surface before starting compressions.",
                "If alone with an unresponsive adult, call for help before beginning CPR."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Check Responsiveness & Breathing",
                    description = "Tap victim's shoulders firmly and ask loudly 'Are you OK?'. Look at chest for 5-10 seconds for normal breathing. If unresponsive and not breathing (or only gasping), shout for help immediately."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "Position Hands on Center of Chest",
                    description = "Place heel of one hand in center of chest (lower half of sternum). Interlock fingers of second hand over first. Keep arms straight and shoulders positioned directly over hands."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Deliver High-Frequency Chest Compressions",
                    description = "Compress chest at least 2 to 2.4 inches (5-6 cm) deep at a rate of 100 to 120 compressions per minute (to the beat of 'Stayin' Alive'). Allow chest to recoil completely after each compression.",
                    warning = "Do not lean on the chest between compressions."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Give 2 Rescue Breaths (30:2 Ratio)",
                    description = "Tilt head back and lift chin. Pinch nose closed, seal mouth over victim's mouth using a barrier, and blow gently for 1 second until chest rises. Give 2 breaths, then resume compressions instantly."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Attach AED if Available",
                    description = "Power on Automated External Defibrillator. Attach pads to bare chest (upper right, lower left side). Follow voice prompts. Clear victim before shock delivery."
                )
            ),
            proTips = listOf(
                "Rotate rescuers every 2 minutes if multiple people are present to prevent compression fatigue.",
                "Hands-only CPR (continuous compressions without breaths) is effective if untrained in rescue breathing."
            )
        ),

        // 2. Severe Bleeding & Tourniquet
        SurvivalProtocol(
            id = "tourniquet_protocol",
            title = "Severe Bleeding & Tourniquet Application",
            category = ProtocolCategory.TRAUMA,
            summary = "Apply direct firm pressure to wound. Place tourniquet 2-3 inches above bleeding site on limb. Tighten windlass until bleeding stops completely.",
            urgency = UrgencyLevel.CRITICAL,
            requiredTools = listOf("CAT Tourniquet / Improvised 2-inch band", "Rigid Windlass Stick", "Marker / Pen", "Hemostatic Dressing / Clean Cloth"),
            warnings = listOf(
                "NEVER apply a tourniquet directly over a joint (elbow, knee). Place 2-3 inches above joint if necessary.",
                "NEVER loosen or remove a tourniquet once applied in the field; doing so can release accumulated toxins causing fatal cardiac shock.",
                "Do NOT cover a tourniquet with blankets or clothing; it must remain clearly visible to triage responders."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Identify Arterial vs Venous Hemorrhage",
                    description = "Bright red spurting blood indicates arterial bleeding (fatal within 3 minutes). Dark red steady flow indicates venous bleeding. Direct pressure is required immediately."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "Apply Direct Pressure & Elevate",
                    description = "Pack wound tightly with sterile gauze or clean cloth. Press down firmly with both hands using full body weight. Elevate wound above heart if possible."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Position Tourniquet High & Tight",
                    description = "Wrap tourniquet strap 2-3 inches above wound site (or high on arm/thigh if wound location is unclear). Thread strap tightly through buckle."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Twist Windlass until Bleeding Stops",
                    description = "Turn windlass rod continuously until bright red bleeding ceases completely AND distal pulse is no longer felt. Lock windlass firmly in retaining clip.",
                    warning = "Tourniquet application is extremely painful for victim; explain that pain is normal and life-saving."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Document Application Time",
                    description = "Write the exact application time (e.g. 'T = 14:45') on victim's forehead or on tourniquet time strap using permanent marker."
                )
            ),
            proTips = listOf(
                "If bleeding persists after first tourniquet, apply a second tourniquet 2 inches above the first one.",
                "Improvise tourniquets using cravats, triangular bandages, or webbing with a sturdy windlass stick (never thin wire or shoelaces)."
            )
        ),

        // 3. Fractures
        SurvivalProtocol(
            id = "fracture_protocol",
            title = "Fracture Immobilization & Splinting",
            category = ProtocolCategory.TRAUMA,
            summary = "Control bleeding, check distal circulation, immobilize joint above and below fracture with a padded rigid splint, and elevate.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("SAM Splint / Rigid Wooden Slats", "Cravats / Bandages", "Padding Cloths", "Ice Pack"),
            warnings = listOf(
                "Do NOT attempt to push protruding bones back under skin (open fracture).",
                "Do NOT force a bent bone straight if resistance or extreme resistance is met.",
                "Check pulse, sensation, and capillary refill before and after applying splint."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Assess Injury & Control Bleeding",
                    description = "Expose limb. Look for deformity, swelling, bruising, or exposed bone. Control open bleeding with gentle pressure dressing around exposed bone."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "Check PMS (Pulse, Motor, Sensory)",
                    description = "Check distal pulse (radial or foot pulse). Ask victim to wiggle fingers/toes and identify which digit you are touching. Note any numbness or tingling."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Prepare Rigid Splint & Soft Padding",
                    description = "Shape SAM splint or gather rigid sticks/magazines. Pad splint with soft cloths, moss, or clothing to prevent skin necrosis."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Immobilize Joints Above & Below Fracture",
                    description = "Position splint along limb extending past the joint above and joint below fracture. Secure firmly with bandages or cravats without cutting off blood circulation."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Recheck Circulation & Elevate",
                    description = "Recheck PMS distal to splint every 15 minutes. Elevate limb and apply cold pack wrapped in cloth to reduce swelling."
                )
            ),
            proTips = listOf(
                "For arm fractures, construct a sling and swath using a triangular bandage to secure arm firmly against torso.",
                "An injured leg can be splinted to the uninjured leg ('anatomical splint') if rigid materials are unavailable."
            )
        ),

        // 4. Hypothermia
        SurvivalProtocol(
            id = "hypothermia_protocol",
            title = "Hypothermia Triage & Field Treatment",
            category = ProtocolCategory.ENVIRONMENTAL,
            summary = "Shield victim from wind and rain, strip wet clothing, apply passive warming layers and active heat to core torso area.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("Dry Wool Blankets / Sleeping Bag", "Vapor Barrier Tarp", "Chemical Heat Packs", "Warm Sweet Liquids"),
            warnings = listOf(
                "Do NOT rub or massage cold extremities; doing so forces cold, acidic blood back to heart causing lethal ventricular fibrillation ('afterdrop').",
                "Do NOT apply direct high heat (scalding water, direct fire exposure) to cold skin.",
                "Handle severe hypothermia victims extremely gently; rough movement can trigger cardiac arrest."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Remove from Cold Environment",
                    description = "Move victim immediately into shelter out of wind, snow, and rain. Lay down a sleeping pad or dry pine boughs underneath to insulate from cold ground."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "Strip Wet Clothing & Dry Victim",
                    description = "Gently cut or pull off wet clothing. Towel dry victim thoroughly starting with head and torso."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Apply Passive Warming Layers",
                    description = "Wrap victim in dry wool blankets, sleeping bags, or thermal space blankets. Cover head with beanie or warm hood."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Apply Active Central Heating",
                    description = "Place warm water bottles or chemical heat packs wrapped in cloth on central core area: armpits, sides of neck, and groin.",
                    warning = "Never place heat packs directly against bare cold skin."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Provide Warm Electrolyte / Sugar Drinks",
                    description = "If victim is fully conscious and capable of swallowing safely, administer warm, sweet liquids (warm sugary water or broth). Avoid caffeine and alcohol."
                )
            ),
            proTips = listOf(
                "Build a 'hypothermia burrito': ground insulation + sleeping bag + warm water bottles at core + outer windproof vapor barrier tarp."
            )
        ),

        // 5. Heatstroke
        SurvivalProtocol(
            id = "heatstroke_protocol",
            title = "Heatstroke & Heat Exhaustion Management",
            category = ProtocolCategory.ENVIRONMENTAL,
            summary = "Rapidly cool victim using cold water dousing, fanning, ice on arterial points, and electrolyte rehydration if conscious.",
            urgency = UrgencyLevel.CRITICAL,
            requiredTools = listOf("Cold Water", "Ice Packs / Cold Wet Towels", "Electrolyte Solution / ORS", "Hand Fan"),
            warnings = listOf(
                "Heatstroke (temp > 104°F/40°C, hot dry skin, confusion) is a life-threatening emergency.",
                "Do NOT give fluids to an unconscious or vomiting victim."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Move to Deep Shade & Elevate Feet",
                    description = "Relocate victim out of direct sunlight into shaded breeze. Lay victim down and elevate feet 8-12 inches to promote venous return."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "Remove Excess Outer Outerwear",
                    description = "Strip off heavy jackets, boots, socks, and tight clothing to maximize skin evaporation."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Aggressive Active Cooling",
                    description = "Douse skin with cool water continuously. Fan vigorously. Apply ice packs wrapped in thin cloth to neck, armpits, and groin."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Oral Rehydration",
                    description = "If alert and conscious, provide small sips of cool electrolyte drink (oral rehydration salts, pinch of salt + sugar in water) every 10 minutes."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Monitor Consciousness & Airway",
                    description = "Continuously recheck breathing, mental clarity, and temperature. If shivering occurs, slow cooling slightly."
                )
            ),
            proTips = listOf(
                "Mist or spray cold water while fanning; evaporative cooling is the fastest field mechanism for lowering body temperature."
            )
        ),

        // 6. Water Purification
        SurvivalProtocol(
            id = "water_purification",
            title = "Water Purification & Filtration",
            category = ProtocolCategory.HYDRATION,
            summary = "Pre-filter sediment, boil at rolling boil for 1 full minute (3 mins at high altitude), or chemically treat with chlorine/iodine.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("Bandana / Filter Mesh", "Metal Pot / Kettle", "Chlorine / Iodine Tablets", "Clear PET Bottles"),
            warnings = listOf(
                "Never drink untreated surface water near industrial runoff, mine tailings, or agricultural fields (boiling does not remove toxic heavy metals or chemicals).",
                "Freezing water does NOT destroy bacteria or Giardia cysts."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Pre-Filter Turbid Water",
                    description = "Pour cloudy water through a clean bandana, cloth, or multi-layer sand/charcoal filter to remove sediment, algae, and suspended solids."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "Boiling (Gold Standard)",
                    description = "Bring pre-filtered water to a rolling boil for 1 full minute. At elevations above 2,000 meters (6,500 ft), boil continuously for 3 full minutes."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Chemical Treatment (Chlorine/Iodine)",
                    description = "Add purification tablets (or 2 drops of unscented household bleach per liter). Shake well and let sit covered for 30 minutes (60 mins if water is cold)."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Solar Disinfection (SODIS)",
                    description = "Fill clear PET plastic bottles with clear pre-filtered water. Place horizontally on dark surface under direct sunlight for 6 hours (2 days if cloudy)."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Safe Storage",
                    description = "Store purified water in clean, capped containers. Aerate flat-tasting boiled water by pouring back and forth between clean vessels."
                )
            ),
            proTips = listOf(
                "Construct an emergency tripod filter: gravel (top) -> sand -> crushed charcoal (bottom cloth layer) to clarify murky water before boiling."
            )
        ),

        // 7. Emergency Shelters
        SurvivalProtocol(
            id = "emergency_shelters",
            title = "Emergency Shelter Construction",
            category = ProtocolCategory.SHELTER,
            summary = "Select hazard-free site, construct sturdy ridgepole framework, layer 2-3 feet of insulating debris (leaves/pine boughs), and build elevated bedding.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("Tarp / Mylar Blanket", "Paracord / Vines", "Cutting Tool", "Deadfall Branches"),
            warnings = listOf(
                "Avoid building under dead branches ('widowmakers') or in dry riverbeds subject to flash floods.",
                "Never burn open fires inside small unventilated debris shelters due to carbon monoxide poisoning risk."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Site Selection & Hazard Check",
                    description = "Choose level high ground sheltered from wind. Inspect overhead trees for dead branches. Ensure distance from insect nests and standing water."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "Debris Hut Frame Setup",
                    description = "Prop a sturdy 9-10 foot ridgepole onto a 3-4 foot tree fork or tripod. Place rib sticks along both sides at a 45-degree angle forming a tight wedge."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Thick Insulation Layering",
                    description = "Pile 2 to 3 feet of dry leaves, pine needles, moss, or grass over the ribbing. Thicker insulation prevents body heat radiation loss."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Construct Elevated Bedding",
                    description = "Build a 1-foot thick mattress of dry pine boughs or leaves inside shelter to insulate your body from conductive heat loss to cold ground."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Seal Entrance",
                    description = "Block shelter door with a backpack or bundle of leafy branches after crawling inside feet-first."
                )
            ),
            proTips = listOf(
                "A thermal reflective emergency space blanket lined on the interior wall of a lean-to reflects up to 90% of your body heat back to you."
            )
        ),

        // 8. Fire Crafting
        SurvivalProtocol(
            id = "fire_crafting",
            title = "Fire Crafting & Tinder Selection",
            category = ProtocolCategory.FIRE,
            summary = "Prepare dry fine tinder, organize matchstick to arm-sized firewood, ignite spark with ferro rod, and build airflow-optimized fire structure.",
            urgency = UrgencyLevel.MODERATE,
            requiredTools = listOf("Ferrocerium Rod / Matches", "Fixed Blade Knife", "Tinder Bundle", "Dry Wood"),
            warnings = listOf(
                "Clear 6-foot circle around fire pit of all combustible leaf litter down to bare mineral soil.",
                "Do NOT use wet river rocks for fire ring; trapped internal steam can cause rocks to explode violently."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Gather 3 Fuel Categories",
                    description = "Collect Tinder (dry fine fibers/birch bark), Kindling (pencil-thin sticks), and Fuelwood (wrist to arm-thick dry logs). Gather twice as much as you think you need."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "Prepare Tinder Bird's Nest",
                    description = "Process birch bark curls, dry grass, cedar shavings, or feather sticks into a tight, fluffy bird's nest bundle with a central indentation."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Ignite Spark with Ferro Rod",
                    description = "Place ferro rod tip directly against tinder bundle. Scrape spine of knife blade firmly down rod at 45° angle pushing hot sparks into center."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Nurture Ember to Flame",
                    description = "Once tinder catches spark, lift bundle carefully, cup hands, and blow gently and continuously until it bursts into flame."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Build Teepee / Log Cabin Structure",
                    description = "Transfer flaming tinder under kindling teepee structure. Add larger fuelwood incrementally maintaining bottom airflow gaps."
                )
            ),
            proTips = listOf(
                "Resin-rich fatwood (pine stump core) and dry birch bark burn vigorously even when cold or damp."
            )
        ),

        // 9. Foraging & Edible Plants
        SurvivalProtocol(
            id = "foraging_guide",
            title = "Foraging & Edible Plants (Safety Protocol)",
            category = ProtocolCategory.FORAGING,
            summary = "Perform Universal Edibility Test, avoid high-risk plant characteristics (milky sap, umbrella flowers, white berries), and focus on easily identifiable plants.",
            urgency = UrgencyLevel.MODERATE,
            requiredTools = listOf("Foraging Guide / Reference", "Pot for Boiling", "Cutting Knife"),
            warnings = listOf(
                "NEVER eat unknown mushrooms or wild fungi in a survival situation; toxins can cause fatal liver/kidney failure.",
                "Avoid plants with umbrella-shaped flower clusters (resembles deadly poison hemlock & water hemlock).",
                "Avoid plants with milky/cloudy sap, red thorns, or bitter almond smells."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Universal Edibility Test (UET) - Touch Test",
                    description = "Rub crushed leaf or stem onto inside of wrist or elbow. Wait 15 minutes. Inspect for redness, burning, itching, or rash."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "UET - Lip & Tongue Test",
                    description = "Touch small piece of prepared plant to outer lip for 3 minutes. If no reaction, place on tongue for 15 minutes without chewing or swallowing. Spitting if bitter or burning."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "UET - Chew & Wait Test",
                    description = "Chew small piece thoroughly and hold in mouth for 15 minutes. If no adverse reaction, swallow small piece and wait 8 hours without eating anything else."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Safely Identifiable Flora - Dandelions",
                    description = "Entire dandelion plant is edible. Young leaves raw or boiled, yellow flower petals edible, roots boiled or roasted as tea substitute."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Cattails ('Supermarket of the Swamp')",
                    description = "Peeled lower white stalk shoots raw or boiled; starch-rich rhizome roots washed and roasted; pollen heads nutritious flour substitute."
                )
            ),
            proTips = listOf(
                "Pine needles (from 5-needle white pine or ponderosa) boiled into tea provide 4x the Vitamin C of oranges."
            )
        ),

        // 10. Signal Fires & Emergency Signalling
        SurvivalProtocol(
            id = "signal_fires",
            title = "Signal Fires & Emergency Signalling",
            category = ProtocolCategory.SIGNALLING,
            summary = "Construct 3 signal fires in an equilateral triangle (100ft apart), generate thick smoke with green boughs, and use whistle/mirror standards.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("Signal Mirror / Foil", "Emergency Whistle", "Green Boughs / Rubber for Smoke", "Fire Starter"),
            warnings = listOf(
                "Never aim high-powered laser pointers directly at cockpit windows of rescue aircraft.",
                "Ensure signal fires are laid in cleared open clearings visible from high altitudes."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "Ground-to-Air Symbols",
                    description = "Construct large 10-foot 'SOS' or 'V' (Requires Assistance) or 'X' (Medical Aid Needed) using dark rocks, logs, or dug trenches in open clearing."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "International 3-Signal Fire Triangle",
                    description = "Prepare 3 signal fires positioned 100 feet apart in an equilateral triangle or straight line (universal distress pattern)."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "Generate High-Visibility Smoke",
                    description = "Once signal fires are burning hot, stack green pine boughs, damp leaves, or rubber/plastic onto coals to create dense white or black smoke columns."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "Signal Mirror Flash",
                    description = "Reflect sunlight using mirror, foil, or screen glass toward aircraft or distant search vessel horizon. Form a 'V' with fingers to sight target."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "Acoustic Whistle Signals",
                    description = "Blow emergency whistle in 3 short distinct blasts (distress signal). Wait 1 minute, then repeat continuously."
                )
            ),
            proTips = listOf(
                "Whistle sound travels up to 3 times further than human voice shouting and conserves vital stamina."
            )
        )
    )

    private fun getFarsiProtocols(): List<SurvivalProtocol> = listOf(
        // 1. CPR
        SurvivalProtocol(
            id = "cpr_protocol",
            title = "احیای قلبی ریوی (CPR) و ایست قلبی",
            category = ProtocolCategory.FIRST_AID,
            summary = "انجام ماساژ قفسه سینه مداوم با کیفیت بالا (۱۰ تا ۱۲۰ بار در دقیقه) و ۲ تنفس مصنوعی تا زمان بازگشت تنفس مصدوم یا رسیدن اورژانس.",
            urgency = UrgencyLevel.CRITICAL,
            requiredTools = listOf("دستکش نیتریل", "شیلد تنفس CPR / ماسک سدکننده", "دستگاه شوک AED (در صورت وجود)"),
            warnings = listOf(
                "هرگز ماساژ قفسه سینه را بیش از ۱۰ ثانیه قطع نکنید.",
                "اطمینان حاصل کنید که مصدوم روی یک سطح کاملاً سخت و صاف دراز کشیده است.",
                "اگر با یک بزرگسال غیرهوشیار تنها هستید، قبل از شروع احیا فوراً درخواست کمک کنید."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "بررسی هوشیاری و تنفس",
                    description = "شانه مصدوم را محکم تکان دهید و بلند بپرسید 'حالت خوبه؟'. ۵ تا ۱۰ ثانیه به قفسه سینه نگاه کنید. اگر پاسخی نداد و تنفس نداشت، فوراً درخواست کمک کنید."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "قرار دادن دست‌ها در مرکز قفسه سینه",
                    description = "پاشنه یک دست را در مرکز قفسه سینه (نیمه پایینی جناغ) قرار دهید. انگشتان دست دوم را در دست اول قفل کنید. بازوها را کاملاً مستقیم نگه دارید."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "انجام ماساژ قفسه سینه با ریتم بالا",
                    description = "قفسه سینه را حداقل ۵ تا ۶ سانتی‌متر با سرعت ۱۰۰ تا ۱۲۰ بار در دقیقه فشار دهید. اجازه دهید قفسه سینه بعد از هر فشار کاملاً به حالت اول برگردد.",
                    warning = "بین ماساژها روی قفسه سینه تکیه ندهید."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "دادن ۲ تنفس مصنوعی (نسبت ۳۰ به ۲)",
                    description = "سر را به عقب تکیه داده و چانه را بالا بکشید. بینی را بگیرید، دهان خود را روی دهان مصدوم بگذارید و ۱ ثانیه فوت کنید تا قفسه سینه بالا بیاید."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "اتصال دستگاه شوک AED در صورت وجود",
                    description = "دستگاه AED را روشن کنید. پدها را روی قفسه سینه برهنه بسبانید (بالا سمت راست، پایین سمت چپ). دستورات صوتی دستگاه را دنبال کنید."
                )
            ),
            proTips = listOf(
                "اگر چند نفر حضور دارند، هر ۲ دقیقه جای خود را عوض کنید تا دچار خستگی ماساژ نشوید.",
                "ماساژ تنها (بدون تنفس مصنوعی) نیز در صورت عدم آموزش تنفس نجات‌بخش و مؤثر است."
            )
        ),

        // 2. Tourniquet
        SurvivalProtocol(
            id = "tourniquet_protocol",
            title = "خونریزی شدید و بستن تورنیکه (شریان‌بند)",
            category = ProtocolCategory.TRAUMA,
            summary = "اعمال فشار مستقیم روی زخم. بستن تورنیکه ۵ تا ۷ سانتی‌متر بالاتر از محل خونریزی روی اندام و چرخاندن اهرم تا توقف کامل خونریزی.",
            urgency = UrgencyLevel.CRITICAL,
            requiredTools = listOf("تورنیکه تاکتیکی CAT / باند ۵ سانتی", "چوب اهرم محکم", "ماژیک / قلم", "پانسمان استریل / پارچه تمیز"),
            warnings = listOf(
                "هرگز تورنیکه را مستقیماً روی مفصل (آرنج یا زانو) نبندید. در صورت لزوم ۵ تا ۷ سانتی‌متر بالاتر ببندید.",
                "هرگز تورنیکه بسته‌شده را در میدان باز نکنید؛ این کار باعث آزاد شدن سموم انباشته شده و شوک کشنده قلبی می‌شود.",
                "تورنیکه را زیر لباس یا پتو نپوشانید؛ باید برای تیم امداد کاملاً قابل رؤیت باشد."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "تشخیص خونریزی سرخرگی از سردرگی",
                    description = "خون قرمز روشن و جهنده نشان‌دهنده خونریزی سرخرگی است (در ۳ دقیقه کشنده است). فشار مستقیم فوری مورد نیاز است."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "اعمال فشار مستقیم و بالا نگه داشتن",
                    description = "زخم را با گاز استریل یا پارچه تمیز کاملاً پر کنید. با هر دو دست و وزن بدن فشار محکم وارد کنید."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "بستن تورنیکه در محلی بالا و محکم",
                    description = "تسمه تورنیکه را ۵ تا ۷ سانتی‌متر بالاتر از زخم ببندید. تسمه را از سگک رد کرده و محکم بکشید."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "چرخاندن اهرم تا توقف کامل خونریزی",
                    description = "میله اهرم را آن‌قدر بچرخانید تا خونریزی قرمز روشن کاملاً قطع شود و نبض اندام حس نشود. اهرم را قفل کنید.",
                    warning = "بستن تورنیکه برای مصدوم بسیار دردناک است؛ به او توضیح دهید که این درد طبیعی و نجات‌بخش است."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "ثبت زمان بستن تورنیکه",
                    description = "زمان دقیق بستن تورنیکه (مثلاً T = 14:45) را روی پیشانی مصدوم یا برچسب تورنیکه با ماژیک بنویسید."
                )
            ),
            proTips = listOf(
                "اگر خونریزی پس از تورنیکه اول ادامه داشت، تورنیکه دوم را ۵ سانتی‌متر بالاتر از اولی ببندید.",
                "از باندهای پارچه‌ای با اهرم چوبی محکم استفاده کنید (هرگز از سیم باریک یا بند کفش استفاده نکنید)."
            )
        ),

        // 3. Fracture
        SurvivalProtocol(
            id = "fracture_protocol",
            title = "آتل‌بندی و تثبیت شکستگی",
            category = ProtocolCategory.TRAUMA,
            summary = "کنترل خونریزی، بررسی جریان خون اندام، آتل‌بندی مفصل بالا و پایین شکستگی با آتل محکم و پدگذاری شده و بالا نگه داشتن اندام.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("آتل SAM / تخته چوب محکم", "باند و پارچه", "پد نرم", "کیسه یخ"),
            warnings = listOf(
                "هرگز سعی نکنید استخوان بیرون‌زده را به زیر پوست بازگردانید (شکستگی باز).",
                "در صورت مقاومت شدید، استخوان کج شده را به زور راست نکنید.",
                "قبل و بعد از بستن آتل، نبض، حس و حرکت انگشتان را بررسی کنید."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "ارزیابی آسیب و کنترل خونریزی",
                    description = "اندام را برهنه کنید. شکل غیرطبیعی، تورم یا استخوان بیرون زده را بررسی کنید. خونریزی را با پانسمان ملایم کنترل کنید."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "بررسی نبض، حس و حرکت (PMS)",
                    description = "نبض انتهای اندام را حس کنید. از مصدوم بخواهید انگشتان خود را تکان دهد و حس لمس را تأیید کند."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "آماده‌سازی آتل و پد نرم",
                    description = "آتل یا چوب‌ها را با پارچه نرم، لباس یا خزه پدگذاری کنید تا از آسیب به پوست جلوگیری شود."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "تثبیت مفاصل بالا و پایین شکستگی",
                    description = "آتل را طوری قرار دهید که از مفصل بالا و مفصل پایین شکستگی عبور کند. آن را با باند محکم ببندید."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "بررسی مجدد جریان خون و بالا نگه داشتن",
                    description = "هر ۱۵ دقیقه حس و نبض اندام را مجدداً بررسی کنید. اندام را بالا نگه دارید و کیسه یخ بگذارید."
                )
            ),
            proTips = listOf(
                "برای شکستگی دست، با دستمال سه گوش آویز گردن بسازید تا دست به تنه متصل بماند.",
                "پای آسیب دیده را می‌توان به پای سالم آتل کرد ('آتل آناتومیک')."
            )
        ),

        // 4. Hypothermia
        SurvivalProtocol(
            id = "hypothermia_protocol",
            title = "مدیریت و درمان سرمازدگی (هیپوترمی)",
            category = ProtocolCategory.ENVIRONMENTAL,
            summary = "محافظت مصدوم در برابر باد و باران، خارج کردن لباس‌های خیس، لایه‌بندی گرمایشی غیرفعال و اعمال گرما به مرکز بدن.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("پتو پشمی خشک / کیسه‌خواب", "چادر یا پلاستیک عایق", "کیسه آب گرم / پد گرمایشی", "نوشیدنی گرم شیرین"),
            warnings = listOf(
                "هرگز اندام‌های سرد را ماساژ ندهید؛ این کار باعث بازگشت خون اسیدی سرد به قلب و فیبریلاسیون کشنده قلبی می‌شود.",
                "از گرمای مستقیم و شدید (آب داغ، آتش مستقیم) روی پوست سرد استفاده نکنید.",
                "با مصدوم هیپوترمی شدید بسیار آرام رفتار کنید؛ حرکات ناگهانی می‌تواند باعث ایست قلبی شود."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "انتقال به محیط گرم و دور از باد",
                    description = "مصدوم را فوراً به پناهگاه برده و از باد و برف دور کنید. زیر او زیرانداز عایق یا شاخه‌های خشک کاج قرار دهید."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "خارج کردن لباس‌های خیس و خشک کردن",
                    description = "لباس‌های خیس را آرام قیچی کرده یا خارج کنید. سر و تنه مصدوم را کاملاً با حوله خشک کنید."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "لایه‌بندی گرمایشی غیرفعال",
                    description = "مصدوم را در پتوهای پشمی خشک، کیسه‌خواب یا پتوی نجات نجات بپیچید. سر او را با کلاه بپوشانید."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "اعمال گرمای فعال به مرکز بدن",
                    description = "بطری‌های آب گرم یا کیسه آب گرم پیچیده شده در پارچه را در زیربغل، طرفین گردن و کشاله ران قرار دهید."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "دادن نوشیدنی گرم شیرین",
                    description = "اگر مصدوم کاملاً هوشیار است و می‌تواند بلع انجام دهد، مایعات گرم و شیرین به او بدهید."
                )
            ),
            proTips = listOf(
                "ساخت 'بوریتوی هیپوترمی': زیرانداز عایق + کیسه‌خواب + بطری‌های آب گرم در تنه + پلاستیک بادگیر بیرونی."
            )
        ),

        // 5. Heatstroke
        SurvivalProtocol(
            id = "heatstroke_protocol",
            title = "مدیریت گرمازدگی شدید و خستگی گرمایی",
            category = ProtocolCategory.ENVIRONMENTAL,
            summary = "خنک‌سازی سریع مصدوم با پاشیدن آب سرد، باد زدن، قرار دادن یخ روی نقاط شریانی و جبران مایعات و الکترولیت‌ها.",
            urgency = UrgencyLevel.CRITICAL,
            requiredTools = listOf("آب سرد", "کیسه یخ / حوله خیس", "محلول الکترولیت ORS", "بادبزن"),
            warnings = listOf(
                "گرمازدگی شدید (دمای بدن بالای ۴۰ درجه، پوست داغ و خشک، گیجی) اورژانس حیاتی است.",
                "به مصدوم بی‌هوش یا دارای تهوع هیچ مایعاتی ندهید."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "انتقال به سایه و بالا دادن پاها",
                    description = "مصدوم را به سایه و خنکی منتقل کنید. او را دراز کشیده و پاها را ۲۰ تا ۳۰ سانتی‌متر بالا بیاورید."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "خارج کردن لباس‌های اضافی",
                    description = "کاپشن، پوتین، جوراب و لباس‌های تنگ را درآورید تا تبخیر پوستی به حداکثر برسد."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "خنک‌سازی فعال و شدید",
                    description = "پوست را مدام با آب خنک خیس کنید. شدیداً باد بزنید. کیسه‌های یخ را روی گردن، زیربغل و کشاله ران بگذارید."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "جبران مایعات خوراکی",
                    description = "اگر هوشیار است، جرعه‌های کوچک از مایعات الکترولیت (آب + نمک و شکر) به او بدهید."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "پایش تنفس و سطح هوشیاری",
                    description = "تنفس و دمای بدن را مدام پایش کنید. در صورت بروز لرز، خنک‌سازی را کمی کندتر کنید."
                )
            ),
            proTips = listOf(
                "پاشیدن اسپری آب سرد همزمان با باد زدن سریع‌ترین روش کاهش دمای بدن در شرایط میدانی است."
            )
        ),

        // 6. Water Purification
        SurvivalProtocol(
            id = "water_purification",
            title = "تصفیه و ضدعفونی آب در طبیعت",
            category = ProtocolCategory.HYDRATION,
            summary = "پیش‌فیلتر گل‌ولای، جوشاندن آب به مدت ۱ دقیقه کامل (۳ دقیقه در ارتفاعات) یا ضدعفونی شیمیایی با قرص کلر یا ید.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("پارچه فیلتر / دستمال", "ظرف فلزی / کتری", "قرص کلر / ید", "بطری پلاستیکی شفاف"),
            warnings = listOf(
                "هرگز آب‌های سطحی نزدیک فاضلاب صنعتی یا کشاورزی را ننوشید (جوشاندن فلزات سنگین را از بین نمی‌برد).",
                "یخ زدن آب باعث از بین رفتن باکتری‌ها یا انگل‌ها نمی‌شود."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "پیش‌فیلتر آب گل‌آلود",
                    description = "آب کدر را از پارچه تمیز یا فیلتر چندلایه ماسه و زغال عبور دهید تا رسوبات و ذرات معلق جدا شوند."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "جوشاندن (استاندارد طلایی)",
                    description = "آب صاف‌شده را به مدت ۱ دقیقه کامل بجوشانید. در ارتفاعات بالای ۲۰۰۰ متر، ۳ دقیقه بجوشانید."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "ضدعفونی شیمیایی با کلر یا ید",
                    description = "قرص تصفیه را اضافه کرده، خوب تکان دهید و ۳۰ دقیقه (اگر آب سرد است ۶۰ دقیقه) بگذارید بماند."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "ضدعفونی خورشیدی (SODIS)",
                    description = "بطری‌های پلاستیکی شفاف را از آب صاف‌شده پر کنید و ۶ ساعت زیر آفتاب مستقیم روی سطح تیره قرار دهید."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "نگهداری امن",
                    description = "آب تصفیه‌شده را در ظرف دربسته و تمیز نگهداری کنید."
                )
            ),
            proTips = listOf(
                "ساخت فیلتر سه‌پایه اضطراری: سنگریزه (بالا) -> ماسه -> زغال خردشده (پایین) برای شفاف کردن آب قبل از جوشاندن."
            )
        ),

        // 7. Shelters
        SurvivalProtocol(
            id = "emergency_shelters",
            title = "ساخت پناهگاه اضطراری",
            category = ProtocolCategory.SHELTER,
            summary = "انتخاب محل امن بدون خطر، ساخت اسکلت تیرک اصلی، ایجاد لایه پوششی ۵۰-۷۰ سانتی‌متری از برگ و شاخه‌ها و بستر خواب برجسته.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("پلاستیک / پتوی نجات", "طناب / پیچک", "ابزار برش / چاقو", "شاخه چوب‌های خشک"),
            warnings = listOf(
                "از ساخت پناهگاه زیر شاخه‌های خشک درختان یا در بستر خشک رودخانه‌ها خودداری کنید.",
                "هرگز درون پناهگاه‌های کوچک و بدون تهویه آتش روشن نکنید (خطر مسمومیت با گاز مونوکسید کربن)."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "انتخاب محل و بررسی خطرات",
                    description = "محل مرتفع و دور از باد را انتخاب کنید. شاخه‌های بالای سر و فاصله از لانه‌های حشرات را بررسی کنید."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "ساخت اسکلت دیواره پناهگاه",
                    description = "یک تیرک محکم ۳ متری را روی سه پایه یا تنه درخت تکیه دهید. شاخه‌های جانبی را با زاویه ۴۵ درجه بچینید."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "پوشش‌دهی با لایه عایق ضخیم",
                    description = "۵۰ تا ۷۰ سانتی‌متر از برگ‌های خشک، سوزن‌های کاج یا خزه روی اسکلت بریزید."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "ساخت بستر خواب برجسته",
                    description = "یک تشک ۳۰ سانتی‌متری از شاخه‌های نرم کاج یا برگ خشک در داخل ساختار بسازید تا از سرمازمین عایق شوید."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "مسدود کردن ورودی",
                    description = "پس از ورود، ورودی پناهگاه را با کوله‌پشتی یا دسته شاخه‌ها مسدود کنید."
                )
            ),
            proTips = listOf(
                "پتوی نجات بازتاب‌دهنده در دیواره داخلی پناهگاه تا ۹۰٪ گرمای بدن را به سمت شما بازتاب می‌دهد."
            )
        ),

        // 8. Fire
        SurvivalProtocol(
            id = "fire_crafting",
            title = "افروختن آتش و انتخاب خاشاک (تیندر)",
            category = ProtocolCategory.FIRE,
            summary = "آماده‌سازی خاشاک خشک و ریز، چیدن چوب‌های کوچک تا بزرگ، ایجاد جرقه با سنگ چخماق/فیروراد و ساخت ساختار آتش با تهویه مناسب.",
            urgency = UrgencyLevel.MODERATE,
            requiredTools = listOf("سنگ چخماق (Ferro Rod) / کبریت", "چاقو", "بسته خاشاک خشک", "چوب خشک"),
            warnings = listOf(
                "شعاع ۲ متری اطراف آتش را از تمام برگ‌ها و مواد سوختنی پاکسازی کنید.",
                "از سنگ‌های خیس رودخانه برای دور آتش استفاده نکنید؛ بخار داخل آن‌ها می‌تواند باعث انفجار سنگ شود."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "جمع‌آوری ۳ دسته سوخت",
                    description = "خاشاک ریز (تیندر)، چوب‌های باریک به ضخامت مداد، و کنده‌های خشک جمع‌آوری کنید. دو برابر نیازتان جمع کنید."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "آماده‌سازی لانه پرنده خاشاک",
                    description = "پوست درخت توس، علف خشک یا تراشه‌های چوب را به صورت یک لانه نرم فشرده بسازید."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "ایجاد جرقه با چخماق",
                    description = "نوک سنگ چخماق را روی لانه خاشاک بگذارید و پشت چاقو را با زاویه ۴۵ درجه روی آن بکشید تا جرقه بزند."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "تبدیل اخگر به شعله",
                    description = "وقتی خاشاک جرقه گرفت، آن را آرام برداشته و ملایم و مداوم فوت کنید تا شعله‌ور شود."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "ساخت ساختار مخروطی آتش",
                    description = "شعله را زیر چوب‌های باریک قرار داده و به تدریج چوب‌های بزرگتر را اضافه کنید."
                )
            ),
            proTips = listOf(
                "چوب‌های صمغ‌دار صنوبر و پوست درخت توس حتی در صورت خیس بودن بسیار عالی می‌سوزند."
            )
        ),

        // 9. Foraging
        SurvivalProtocol(
            id = "foraging_guide",
            title = "گیاهان خوراکی و تست ایمنی خوراکی بودن",
            category = ProtocolCategory.FORAGING,
            summary = "انجام تست عمومی خوراکی بودن، اجتناب از گیاهان با شیره سفید، گل‌های چتری و قارچ‌های ناشناخته در طبیعت.",
            urgency = UrgencyLevel.MODERATE,
            requiredTools = listOf("راهنمای گیاهان", "ظرف پخت", "چاقو"),
            warnings = listOf(
                "هرگز قارچ‌های ناشناخته را در شرایط بقا نخورید؛ سموم آن‌ها می‌تواند باعث نارسایی کشنده کبد و کلیه شود.",
                "از گیاهان دارای گل‌های چتری (مشابه شوکران سمی) کاملاً پرهیز کنید.",
                "از گیاهان دارای شیره سفید/شیری یا بوی بادام تلخ پرهیز کنید."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "تست عمومی خوراکی بودن - تست پوستی",
                    description = "برگ له شده را روی مچ دست یا آرنج بمالید. ۱۵ دقیقه صبر کنید. سوزش، قرمزی یا خارش را بررسی کنید."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "تست لب و زبان",
                    description = "قطعه کوچکی را ۳ دقیقه روی لب بگذارید. اگر مشکلی نبود، ۱۵ دقیقه روی زبان بگذارید بدون اینکه ببلعید."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "تست جویدن و انتظار",
                    description = "قطعه کوچک را کاملاً بجوید و ۱۵ دقیقه در دهان نگه دارید. در صورت عدم واکنش، ببلعید و ۸ ساعت صبر کنید."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "گیاهان ایمن شناخته‌شده - قاصدک",
                    description = "تمام اجزای قاصدک خوراکی است. برگ‌های جوان خام یا پخته، گل‌های زرد و ریشه‌های پخته قابل مصرف هستند."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "نیلوفر آبی و لوئی ('سوپرمارکت باتلاق')",
                    description = "شاخه‌های سفید پایینی لوئی خام یا پخته، و ریشه‌های سرشار از نشاسته آن کباب‌شده قابل مصرفند."
                )
            ),
            proTips = listOf(
                "جوشانده سوزن‌های کاج ۵-سوزنه به عنوان چای، ۴ برابر پرتقال ویتامین C دارد."
            )
        ),

        // 10. Signals
        SurvivalProtocol(
            id = "signal_fires",
            title = "علامت‌دهی اضطراری و آتش سیگنال",
            category = ProtocolCategory.SIGNALLING,
            summary = "ساخت ۳ آتش سیگنال به صورت مثلث متساوی‌الاضلاع (۳۰ متری)، ایجاد دود غلیظ با شاخه‌های سبز و استفاده از آینه و سوت.",
            urgency = UrgencyLevel.HIGH,
            requiredTools = listOf("آینه سیگنال / فویل", "سوت اضطراری", "شاخه‌های سبز کاج / لاستیک برای دود", "جرقه زن"),
            warnings = listOf(
                "هرگز لیزرهای قوی را مستقیماً به سمت کابین خلبان هواپیمای امداد نشانه نروید.",
                "اطمینان حاصل کنید آتش‌های سیگنال در فضای باز و قابل دید از بالا روشن شوند."
            ),
            steps = listOf(
                ProtocolStep(
                    stepNumber = 1,
                    title = "علامت‌های بزرگ روی زمین",
                    description = "علامت‌های بزرگ ۳ متری 'SOS' یا 'V' (نیاز به کمک) یا 'X' (کمک پزشکی) با سنگ‌های تیره یا تنه درختان بسازید."
                ),
                ProtocolStep(
                    stepNumber = 2,
                    title = "مثلث بین‌المللی ۳ آتش سیگنال",
                    description = "۳ آتش سیگنال با فاصله ۳۰ متر از هم به صورت مثلث متساوی‌الاضلاع آماده کنید (الگوی اضطراری بین‌المللی)."
                ),
                ProtocolStep(
                    stepNumber = 3,
                    title = "ایجاد دود غلیظ قابل رؤیت",
                    description = "وقتی آتش داغ شد، شاخه‌های سبز کاج، برگ‌های خیس یا پلاستیک روی آن بریزید تا ستون دود سفید یا سیاه ایجاد شود."
                ),
                ProtocolStep(
                    stepNumber = 4,
                    title = "انعکاس آینه سیگنال",
                    description = "نور آفتاب را با آینه یا شیشه صفحه به سمت هواپیما یا شناور نجات بتابانید."
                ),
                ProtocolStep(
                    stepNumber = 5,
                    title = "سیگنال‌های صوتی سوت",
                    description = "سوت اضطراری را به صورت ۳ سوت کوتاه و مجزا (سیگنال خطر) بزنید. ۱ دقیقه صبر کنید و تکرار نمایید."
                )
            ),
            proTips = listOf(
                "صدای سوت تا ۳ برابر دورتر از فریاد انسان می‌رسد و انرژی حیاتی بدن را حفظ می‌کند."
            )
        )
    )
}
