package com.example.data

object SampleDataRepository {

    val starterDecks: List<PresentationDeck> = listOf(
        PresentationDeck(
            id = "deck_solar_system",
            title = "Gravity & Orbital Motion",
            subtitle = "Visualizing why planets stay in orbit — without heavy math",
            audience = "SMP Science",
            theme = SlideTheme.DARK_NEON,
            slides = listOf(
                SlideItem(
                    id = "s1",
                    layoutType = SlideLayoutType.TITLE,
                    heading = "Gravity & Orbital Motion",
                    subheading = "SMP Science • Visual Concept Deck • Powered by NovaDeck",
                    bulletPoints = listOf(
                        "Why the Moon doesn't crash into Earth",
                        "How speed and pull balance in space"
                    ),
                    speakerNotes = "Start by asking the class what happens when you throw a ball faster and faster."
                ),
                SlideItem(
                    id = "s2",
                    layoutType = SlideLayoutType.KEY_POINTS,
                    heading = "Two Ingredients of an Orbit",
                    subheading = "Gravity pulls inward while forward speed keeps moving",
                    bulletPoints = listOf(
                        "Gravity acts like an invisible elastic cord pulling toward Earth",
                        "Forward velocity makes an object try to travel in a straight line",
                        "When pull and speed balance, the path curves into a continuous loop",
                        "Closer orbits require higher speed to avoid falling inward"
                    ),
                    speakerNotes = "Use the Newton's cannonball thought experiment to make this visual."
                ),
                SlideItem(
                    id = "s3",
                    layoutType = SlideLayoutType.COMPARISON,
                    heading = "Mass vs. Weight",
                    subheading = "Commonly confused in SMP exams — here is the fast difference",
                    leftColumnTitle = "Mass (kg)",
                    leftColumnPoints = listOf(
                        "Amount of matter inside an object",
                        "Stays identical on Earth and the Moon",
                        "Measured with a balance scale"
                    ),
                    rightColumnTitle = "Weight (Newtons)",
                    rightColumnPoints = listOf(
                        "Gravitational pull acting on that mass",
                        "Changes when gravity changes (1/6 on Moon)",
                        "Calculated with W = m × g"
                    ),
                    speakerNotes = "Remind classmates: astronauts have the same mass on the Moon, just less weight."
                ),
                SlideItem(
                    id = "s4",
                    layoutType = SlideLayoutType.SUMMARY,
                    heading = "30-Second Takeaways",
                    subheading = "Everything you need for tomorrow's quiz",
                    bulletPoints = listOf(
                        "Orbits = forward motion + continuous gravitational free-fall",
                        "Mass stays constant everywhere; weight depends on local gravity",
                        "Greater mass or closer distance creates stronger gravitational pull"
                    ),
                    speakerNotes = "Pause for 5 seconds so classmates can jot down the 3 bullets."
                ),
                SlideItem(
                    id = "s5",
                    layoutType = SlideLayoutType.QA,
                    heading = "Quick Class Q&A",
                    subheading = "Think & discuss with your study partner",
                    bulletPoints = listOf(
                        "If the Sun vanished instantly, would Earth spiral in or fly straight?",
                        "Why do GPS satellites stay aloft without firing engines nonstop?"
                    ),
                    speakerNotes = "Answer: Earth would fly off in a straight tangent line!"
                )
            )
        ),
        PresentationDeck(
            id = "deck_office_kickoff",
            title = "5-Minute Project Kickoff",
            subtitle = "Aligned goals, milestones, and owners in five crisp slides",
            audience = "Young Pro / Office",
            theme = SlideTheme.MINIMALIST_TECH,
            slides = listOf(
                SlideItem(
                    id = "o1",
                    layoutType = SlideLayoutType.TITLE,
                    heading = "Q4 Campus Study Sprint",
                    subheading = "Fast alignment deck • Built with NovaDeck Slide Studio",
                    bulletPoints = listOf(
                        "Launch timeline: 4 weeks",
                        "Core outcome: +30% active student completion"
                    ),
                    speakerNotes = "Keep the intro under 45 seconds so the team focuses on decisions."
                ),
                SlideItem(
                    id = "o2",
                    layoutType = SlideLayoutType.KEY_POINTS,
                    heading = "Core Objectives",
                    subheading = "Three measurable targets for this sprint",
                    bulletPoints = listOf(
                        "Ship mobile-friendly onboarding under 60 seconds",
                        "Publish 12 bite-sized STEM & productivity modules",
                        "Collect weekly student feedback from 3 partner schools"
                    ),
                    speakerNotes = "Confirm owners for each objective before moving on."
                ),
                SlideItem(
                    id = "o3",
                    layoutType = SlideLayoutType.COMPARISON,
                    heading = "In Scope vs. Later",
                    subheading = "Protecting focus so we ship on time",
                    leftColumnTitle = "This Sprint (Now)",
                    leftColumnPoints = listOf(
                        "Core study flow & mini checks",
                        "Topic-to-slide AI templates",
                        "Markdown & PDF export"
                    ),
                    rightColumnTitle = "Next Phase (Backlog)",
                    rightColumnPoints = listOf(
                        "Live classroom multiplayer polls",
                        "Custom school branding portal",
                        "Offline video caching"
                    ),
                    speakerNotes = "Politely defer backlog items unless someone flags a blocker."
                ),
                SlideItem(
                    id = "o4",
                    layoutType = SlideLayoutType.SUMMARY,
                    heading = "Immediate Next Steps",
                    subheading = "Ready to execute starting Monday",
                    bulletPoints = listOf(
                        "Design sign-off by Wednesday 16:00",
                        "Prototype review with pilot students on Friday",
                        "Weekly 15-min async check-ins inside NovaDeck"
                    ),
                    speakerNotes = "Wrap up and ask if any blockers remain."
                )
            )
        )
    )

    val studyModules: List<StudyModule> = listOf(
        StudyModule(
            id = "mod_smp_algebra",
            title = "Algebra Without the Panic",
            subject = "Math",
            gradeLevel = GradeLevel.SMP,
            difficulty = StudyDifficulty.GENTLE_START,
            estimatedMinutes = 6,
            goal = "Understand variables like x as mystery boxes and solve 1-step and 2-step equations calmly.",
            tldrConcept = "A variable (like x) is just a placeholder for a missing number. An equation works like a balanced scale: whatever you add, subtract, multiply, or divide on one side, do the exact same to the other side.",
            walkthroughSteps = listOf(
                "Step 1 — Spot the mystery box: In 2x + 3 = 11, x is the hidden number we want alone.",
                "Step 2 — Peel the outer layer first: Subtract 3 from BOTH sides -> 2x = 8.",
                "Step 3 — Unlock the multiplier: Divide BOTH sides by 2 -> x = 4.",
                "Step 4 — Quick sanity check: Plug 4 back in: 2(4) + 3 = 11. Balanced!"
            ),
            miniChecks = listOf(
                MiniCheckQuestion(
                    id = "mc_alg_1",
                    question = "If x + 7 = 15, what should you do to both sides to isolate x?",
                    options = listOf("Add 7", "Subtract 7", "Multiply by 7"),
                    correctIndex = 1,
                    encouragingFeedback = "Spot on! Subtracting 7 from both sides leaves x = 8 cleanly."
                ),
                MiniCheckQuestion(
                    id = "mc_alg_2",
                    question = "Solve: 3x - 2 = 10. What is x?",
                    options = listOf("x = 4", "x = 3", "x = 12"),
                    correctIndex = 0,
                    encouragingFeedback = "Awesome job! Add 2 -> 3x = 12, then divide by 3 -> x = 4."
                )
            ),
            curatedResources = listOf(
                CuratedResource(
                    title = "Intro to Equations & Variables",
                    sourceName = "Khan Academy",
                    sourceType = ResourceSourceType.OFFICIAL_OPEN_EDU,
                    attributionNote = "Open Educational Resource • Free licensed curriculum link",
                    url = "https://www.khanacademy.org/math/algebra-basics",
                    durationLabel = "8 min read/practice"
                ),
                CuratedResource(
                    title = "Equality Explorer Interactive Lab",
                    sourceName = "PhET Interactive Simulations (UC Boulder)",
                    sourceType = ResourceSourceType.OFFICIAL_OPEN_EDU,
                    attributionNote = "CC-BY 4.0 Open Simulation • University of Colorado Boulder",
                    url = "https://phet.colorado.edu/en/simulations/equality-explorer",
                    durationLabel = "Interactive"
                )
            )
        ),
        StudyModule(
            id = "mod_smp_photosynthesis",
            title = "How Plants Eat Sunlight",
            subject = "Biology",
            gradeLevel = GradeLevel.SMP,
            difficulty = StudyDifficulty.GENTLE_START,
            estimatedMinutes = 5,
            goal = "Remember the photosynthesis inputs, outputs, and why leaves are green without rote memorization.",
            tldrConcept = "Leaves act like tiny solar-powered kitchens. They take in water (from roots), carbon dioxide (from air), and sunlight (captured by chlorophyll) to cook glucose (plant food) and release fresh oxygen.",
            walkthroughSteps = listOf(
                "Step 1 — Gather ingredients: Water (H2O) travels up roots + CO2 enters through tiny leaf pores (stomata).",
                "Step 2 — Turn on the solar panel: Chlorophyll inside chloroplasts traps sunlight energy.",
                "Step 3 — Check the finished meal: The plant builds Glucose (C6H12O6) for energy and growth.",
                "Step 4 — Gift to humans: Oxygen (O2) is released back into the air!"
            ),
            miniChecks = listOf(
                MiniCheckQuestion(
                    id = "mc_bio_1",
                    question = "Which gas do plants absorb from the air during photosynthesis?",
                    options = listOf("Oxygen (O2)", "Carbon Dioxide (CO2)", "Nitrogen (N2)"),
                    correctIndex = 1,
                    encouragingFeedback = "Yes! Plants breathe in CO2 through stomata and release Oxygen."
                ),
                MiniCheckQuestion(
                    id = "mc_bio_2",
                    question = "Where inside the plant cell does photosynthesis happen?",
                    options = listOf("Chloroplast", "Nucleus", "Ribosome"),
                    correctIndex = 0,
                    encouragingFeedback = "Nailed it! Chloroplasts hold green chlorophyll to catch sunlight."
                )
            ),
            curatedResources = listOf(
                CuratedResource(
                    title = "Photosynthesis: Crash Course Biology",
                    sourceName = "CrashCourse (YouTube)",
                    sourceType = ResourceSourceType.YOUTUBE_EDU,
                    attributionNote = "YouTube Educational Channel • External link with creator credit",
                    url = "https://www.youtube.com/watch?v=sQK3Yr4Sc_k",
                    durationLabel = "13 min video"
                ),
                CuratedResource(
                    title = "Photosynthesis Overview",
                    sourceName = "Khan Academy Biology",
                    sourceType = ResourceSourceType.OFFICIAL_OPEN_EDU,
                    attributionNote = "Open Educational Resource • Official lesson page",
                    url = "https://www.khanacademy.org/science/biology/photosynthesis-in-plants",
                    durationLabel = "6 min guide"
                )
            )
        ),
        StudyModule(
            id = "mod_sma_newton",
            title = "Newton's 3 Laws Made Visual",
            subject = "Physics",
            gradeLevel = GradeLevel.SMA,
            difficulty = StudyDifficulty.CORE,
            estimatedMinutes = 8,
            goal = "Connect inertia, F = m·a, and action-reaction pairs to real everyday motion and SMA problems.",
            tldrConcept = "Law 1: Objects keep doing what they're already doing unless pushed. Law 2: Bigger force = faster acceleration; heavier mass = harder to accelerate (F = m·a). Law 3: Every push pushes back equally in the opposite direction.",
            walkthroughSteps = listOf(
                "Step 1 — Law 1 (Inertia): When a bus brakes suddenly, your body keeps moving forward because it resists change.",
                "Step 2 — Law 2 (F = m·a): Pushing an empty shopping cart is easy; pushing a full cart with the same force accelerates it less.",
                "Step 3 — Law 3 (Action-Reaction): Rocket engines push exhaust downward; the exhaust pushes the rocket upward with equal force.",
                "Step 4 — Exam trick: Always draw a quick Free-Body Diagram arrow before plugging numbers into F = m·a."
            ),
            miniChecks = listOf(
                MiniCheckQuestion(
                    id = "mc_phy_1",
                    question = "A 4 kg skateboard accelerates at 3 m/s². What net force is acting on it?",
                    options = listOf("1.33 N", "7 N", "12 N"),
                    correctIndex = 2,
                    encouragingFeedback = "Clean calculation! F = m × a = 4 kg × 3 m/s² = 12 N."
                ),
                MiniCheckQuestion(
                    id = "mc_phy_2",
                    question = "Why do you lurch forward when a car stops fast?",
                    options = listOf("Inertia (Newton's 1st Law)", "Gravity increases", "Friction disappears"),
                    correctIndex = 0,
                    encouragingFeedback = "Exactly right! Your body wants to keep moving at the car's previous speed."
                )
            ),
            curatedResources = listOf(
                CuratedResource(
                    title = "Forces and Motion: Basics Simulation",
                    sourceName = "PhET Interactive Simulations",
                    sourceType = ResourceSourceType.OFFICIAL_OPEN_EDU,
                    attributionNote = "CC-BY 4.0 Open Simulation • University of Colorado Boulder",
                    url = "https://phet.colorado.edu/en/simulations/forces-and-motion-basics",
                    durationLabel = "Interactive Lab"
                ),
                CuratedResource(
                    title = "Newton's Laws: Crash Course Physics",
                    sourceName = "CrashCourse (YouTube)",
                    sourceType = ResourceSourceType.YOUTUBE_EDU,
                    attributionNote = "YouTube Educational Channel • External link with creator credit",
                    url = "https://www.youtube.com/watch?v=kKKM8Y-u7ds",
                    durationLabel = "11 min video"
                )
            )
        ),
        StudyModule(
            id = "mod_sma_cells",
            title = "Inside the Cell Factory",
            subject = "Biology",
            gradeLevel = GradeLevel.SMA,
            difficulty = StudyDifficulty.CHALLENGE,
            estimatedMinutes = 9,
            goal = "Master eukaryotic organelles by mapping each structure to a real-world factory department.",
            tldrConcept = "Think of a cell as a mini tech factory: the Nucleus is the HQ holding DNA blueprints, Ribosomes are 3D printers making proteins, ER + Golgi are the packaging & shipping line, and Mitochondria are the power generators making ATP.",
            walkthroughSteps = listOf(
                "Step 1 — Command HQ (Nucleus): Protects DNA and sends mRNA instructions out.",
                "Step 2 — Assembly Line (Ribosomes + Rough ER): Reads mRNA to build and fold proteins.",
                "Step 3 — Shipping Hub (Golgi Apparatus): Tags, packs, and routes proteins where they belong.",
                "Step 4 — Power Grid (Mitochondria): Converts nutrients into ATP energy currency."
            ),
            miniChecks = listOf(
                MiniCheckQuestion(
                    id = "mc_cell_1",
                    question = "Which organelle packages and ships proteins to their final destination?",
                    options = listOf("Golgi Apparatus", "Lysosome", "Vacuole"),
                    correctIndex = 0,
                    encouragingFeedback = "Great memory! Think of the Golgi Apparatus as the cell's express shipping hub."
                )
            ),
            curatedResources = listOf(
                CuratedResource(
                    title = "Biology 2e — Cell Structure Chapter",
                    sourceName = "OpenStax (Rice University)",
                    sourceType = ResourceSourceType.OFFICIAL_OPEN_EDU,
                    attributionNote = "CC-BY 4.0 Open Textbook • Rice University",
                    url = "https://openstax.org/books/biology-2e/pages/4-introduction",
                    durationLabel = "Open Textbook"
                ),
                CuratedResource(
                    title = "What Are You? Exploring Cells",
                    sourceName = "Kurzgesagt – In a Nutshell (YouTube)",
                    sourceType = ResourceSourceType.YOUTUBE_EDU,
                    attributionNote = "YouTube Educational Channel • External link with creator credit",
                    url = "https://www.youtube.com/watch?v=URUJD5NEXC8",
                    durationLabel = "7 min animation"
                )
            )
        ),
        StudyModule(
            id = "mod_smp_english",
            title = "Write Confident Paragraphs",
            subject = "English",
            gradeLevel = GradeLevel.SMP,
            difficulty = StudyDifficulty.CORE,
            estimatedMinutes = 5,
            goal = "Structure essays and presentation talking points clearly using the PEEL method.",
            tldrConcept = "Never stare at a blank page again. Use PEEL: Point (your main idea), Evidence (one fact or example), Explain (why it matters), and Link (connect to the next idea).",
            walkthroughSteps = listOf(
                "Step 1 — Point: State your main idea in 1 crisp sentence.",
                "Step 2 — Evidence: Back it up with a real example, quote, or stat.",
                "Step 3 — Explain: Tell the reader how your evidence proves your point.",
                "Step 4 — Link: Bridge smoothly to your next paragraph or slide."
            ),
            miniChecks = listOf(
                MiniCheckQuestion(
                    id = "mc_eng_1",
                    question = "In the PEEL structure, what does the second 'E' stand for?",
                    options = listOf("Explain", "Edit", "Exaggerate"),
                    correctIndex = 0,
                    encouragingFeedback = "Correct! Point -> Evidence -> Explain -> Link."
                )
            ),
            curatedResources = listOf(
                CuratedResource(
                    title = "Paragraphs & Topic Sentences",
                    sourceName = "Purdue OWL",
                    sourceType = ResourceSourceType.OFFICIAL_OPEN_EDU,
                    attributionNote = "Official Open Writing Lab Resource • Purdue University",
                    url = "https://owl.purdue.edu/owl/general_writing/academic_writing/paragraphs_and_paragraphing/index.html",
                    durationLabel = "5 min guide"
                )
            )
        ),
        StudyModule(
            id = "mod_sma_history",
            title = "1945 & Youth Movements",
            subject = "History",
            gradeLevel = GradeLevel.SMA,
            difficulty = StudyDifficulty.CORE,
            estimatedMinutes = 7,
            goal = "Trace how youth activism shaped key turning points around 1945 with clear cause-and-effect timelines.",
            tldrConcept = "History isn't just dates—it's people making urgent decisions under pressure. Youth groups in 1945 pushed leaders to act boldly, showing how organized communication and timing reshape nations.",
            walkthroughSteps = listOf(
                "Step 1 — Context: Understand the power vacuum and global shifts at the end of WWII in 1945.",
                "Step 2 — Youth Catalyst: Young activists urged rapid, independent action instead of waiting.",
                "Step 3 — Turning Point: Key negotiations led to decisive public proclamations and new civic institutions.",
                "Step 4 — Exam Lens: Group events into Cause -> Action -> Impact instead of memorizing isolated dates."
            ),
            miniChecks = listOf(
                MiniCheckQuestion(
                    id = "mc_his_1",
                    question = "What is the smartest way to remember historical events for an exam?",
                    options = listOf(
                        "Group them as Cause -> Action -> Impact",
                        "Memorize random page numbers",
                        "Skip the context and only read years"
                    ),
                    correctIndex = 0,
                    encouragingFeedback = "Yes! Cause -> Action -> Impact helps you answer essay questions effortlessly."
                )
            ),
            curatedResources = listOf(
                CuratedResource(
                    title = "World History: 20th Century Turning Points",
                    sourceName = "Khan Academy History",
                    sourceType = ResourceSourceType.OFFICIAL_OPEN_EDU,
                    attributionNote = "Open Educational Resource • Free curated history timeline",
                    url = "https://www.khanacademy.org/humanities/world-history",
                    durationLabel = "10 min overview"
                )
            )
        ),
        StudyModule(
            id = "mod_pro_focus",
            title = "Beat Procrastination in 15 Mins",
            subject = "Productivity",
            gradeLevel = GradeLevel.PRO_LIFE,
            difficulty = StudyDifficulty.GENTLE_START,
            estimatedMinutes = 4,
            goal = "Start studying when your brain feels stuck or overwhelmed using micro-sprints and active recall.",
            tldrConcept = "Procrastination isn't laziness—it's brain friction from a task feeling too huge. Shrink the start to a 10-minute 'Zero-Pressure Sprint' and test yourself with 2 quick questions instead of rereading notes passively.",
            walkthroughSteps = listOf(
                "Step 1 — Shrink the start: Tell yourself 'Just 10 minutes on 1 mini goal.' Starting breaks 80% of resistance.",
                "Step 2 — Clear 1 distraction: Put your phone face-down or open just one study tab.",
                "Step 3 — Active Recall > Rereading: Close your notes and explain the idea in 3 bullet points (or to NicAI!).",
                "Step 4 — Reward the sprint: Take a 3-minute stretch before deciding whether to do round two."
            ),
            miniChecks = listOf(
                MiniCheckQuestion(
                    id = "mc_pro_1",
                    question = "Which technique helps your brain remember material much faster?",
                    options = listOf(
                        "Rereading the same page 5 times",
                        "Closing notes and recalling 3 key points",
                        "Highlighting every single sentence"
                    ),
                    correctIndex = 1,
                    encouragingFeedback = "Active recall wins every time! Even 2 minutes of self-testing boosts memory."
                )
            ),
            curatedResources = listOf(
                CuratedResource(
                    title = "How to Study Effectively (Evidence-Based)",
                    sourceName = "Learning Scientists",
                    sourceType = ResourceSourceType.OFFICIAL_OPEN_EDU,
                    attributionNote = "Open Cognitive Science Guides • learningscientists.org",
                    url = "https://www.learningscientists.org/downloadable-materials",
                    durationLabel = "Visual posters"
                )
            )
        )
    )

    val partnerBoosts: List<PartnerBoost> = listOf(
        PartnerBoost(
            id = "pb_1",
            title = "STEM Olympiad Starter Pack",
            partnerName = "OpenSTEM Foundation",
            badge = "Partner Edu Placement",
            description = "Curated SMP & SMA Physics/Math practice sets sponsored to keep NovaDeck's core student tier free.",
            targetGrade = "SMP & SMA"
        ),
        PartnerBoost(
            id = "pb_2",
            title = "School & Study-Club License",
            partnerName = "NovaDeck for Campuses",
            badge = "School Pack",
            description = "Equip 30+ students or an entire class with shared slide templates, NicAI study rooms, and teacher packs.",
            targetGrade = "Schools & Orgs"
        )
    )

    val nicAiStarterPrompts: List<String> = listOf(
        "Explain Pythagoras like I'm 13",
        "Break down Newton's 2nd Law step-by-step",
        "Draft 4 crisp slide bullets about Renewable Energy",
        "Quiz me with 2 gentle questions on Photosynthesis"
    )

    val slideTopicChips: List<String> = listOf(
        "Photosynthesis (SMP Biology)",
        "Pythagorean Theorem (SMP Math)",
        "Newton's Laws of Motion (SMA Physics)",
        "AI Ethics for Teens & Young Pros",
        "Q4 Team Project Roadmap (Office)"
    )
}
