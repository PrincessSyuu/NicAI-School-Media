const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_student_123";
const BOB_UID = "bob_student_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validProfilePayload(uid, email = "student@sma-nusantara.sch.id") {
  const now = new Date(Date.now() - 1000);
  return {
    userId: uid,
    email,
    displayName: "Alice Student",
    accountType: "school_workspace",
    proActive: false,
    completedModuleIds: [],
    dailyPromptsUsed: 0,
    promptDate: "2026-11-22",
    createdAt: now,
    updatedAt: now,
  };
}

function validDeckPayload(uid) {
  const now = new Date(Date.now() - 1000);
  return {
    userId: uid,
    title: "Gravity & Orbital Motion",
    subtitle: "SMP Science Visual Deck",
    audience: "SMP Students",
    themeName: "DARK_NEON",
    aiGenerated: true,
    slides: [
      {
        id: "s1",
        layoutType: "TITLE",
        heading: "Gravity & Orbital Motion",
        subheading: "SMP Science",
        bulletPoints: ["Why planets stay in orbit"],
        leftColumnTitle: "",
        leftColumnPoints: [],
        rightColumnTitle: "",
        rightColumnPoints: [],
        speakerNotes: "Start with a quick question.",
      },
    ],
    createdAt: now,
    updatedAt: now,
  };
}

test("Unauthenticated users cannot read or write profiles or decks", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID))
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("decks").get()
  );
});

test("Authenticated owner can create and partially update UserProfile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ref = aliceDb.collection("users").doc(ALICE_UID);

  await assertSucceeds(ref.set(validProfilePayload(ALICE_UID)));
  await assertSucceeds(ref.get());

  // Partial update: toggle proActive
  await assertSucceeds(
    ref.update({
      proActive: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Partial update: completed modules
  await assertSucceeds(
    ref.update({
      completedModuleIds: ["mod_smp_algebra"],
      updatedAt: new Date(Date.now() - 300),
    })
  );

  // Reject ghost field / shadow update
  await assertFails(
    ref.update({
      proActive: false,
      isAdmin: true,
      updatedAt: new Date(Date.now() - 200),
    })
  );

  // Reject immutable createdAt mutation
  await assertFails(
    ref.update({
      createdAt: new Date(Date.now() - 100),
      updatedAt: new Date(Date.now() - 100),
    })
  );
});

test("Cross-user access to UserProfile and PII email is blocked", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .set(validProfilePayload(BOB_UID, "bob@gmail.com"));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({
      proActive: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Authenticated owner can list, create, update, and delete personal decks; @DocumentId key rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const decksCol = aliceDb.collection("users").doc(ALICE_UID).collection("decks");

  // Empty subcollection list must succeed
  await assertSucceeds(decksCol.get());

  // Create valid deck (no `id` field inside body)
  await assertSucceeds(decksCol.doc("deck_1").set(validDeckPayload(ALICE_UID)));

  // Update theme & slides
  await assertSucceeds(
    decksCol.doc("deck_1").update({
      themeName: "EXECUTIVE_PITCH",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject extra `id` field in payload (would break @DocumentId deserialization)
  await assertFails(
    decksCol.doc("deck_2").set({
      ...validDeckPayload(ALICE_UID),
      id: "deck_2",
    })
  );

  // Reject future timestamp
  await assertFails(
    decksCol.doc("deck_3").set({
      ...validDeckPayload(ALICE_UID),
      createdAt: new Date(Date.now() + 60000),
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  await assertSucceeds(decksCol.doc("deck_1").delete());
});

test("Cross-user access to another student's decks is blocked", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .collection("decks")
      .doc("bob_deck")
      .set(validDeckPayload(BOB_UID));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDecks = aliceDb.collection("users").doc(BOB_UID).collection("decks");

  await assertFails(bobDecks.get());
  await assertFails(bobDecks.doc("bob_deck").get());
  await assertFails(bobDecks.doc("bob_deck").delete());
});
