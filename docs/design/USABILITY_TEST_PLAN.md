# Usability test plan: first prototype round

This round is the gate in [FIGMA_DESIGN_SPEC.md](FIGMA_DESIGN_SPEC.md) §13 week 4: no screen is marked **Ready for dev** until it has passed here. The target numbers come from [UI_UX_STRATEGY.md](../UI_UX_STRATEGY.md).

## Goals

1. **Payroll executives** can close a month's core path without help: sign in, review and approve payroll, fix a failed import, and read a client's rate card.
2. **Guards** can check in by face on a phone and at a kiosk, in their own language, first time.
3. **Supervisors** can see who on their shift hasn't checked in and get the post covered.
4. Find the wording, layout and step-order problems before any of it is built.

## Participants

There are 15 sessions, 5 per persona, recruited from agencies that do *not* use Pages Payroll daily, so habit from that product doesn't skew results.

| Persona | Recruit | Device | Language |
|---|---|---|---|
| Payroll executive | Runs payroll for 200+ deployed staff; uses Excel daily | Laptop, 1440 wide, Chrome | English |
| Supervisor | Manages one site shift of 6–20 guards | Mid-range Android phone | English or Tamil |
| Guard | Has a smartphone; mix of education levels; at least 2 who rarely use apps | Android phone and the kiosk tablet | Tamil (at least 3), English |

Pay participants for their time. Run guard sessions at or near a site, during the shift handover, and keep each one to 20 minutes or less.

## Prototypes

The figma-state ledger has the node IDs. Open each flow from **Present → Flows**.

| Flow | File / page | Starts at |
|---|---|---|
| Sign in with OTP | 10 Web Portal / 00 Sign in | Sign in |
| Can't sign in | 10 Web Portal / 00 Sign in | Can't sign in |
| Review and approve payroll | 10 Web Portal / 05 Payroll | Payroll explain |
| Employees → bulk import | 10 Web Portal / 03 People | Employees |
| Tenant onboarding (7 steps) | 10 Web Portal / 06 Setup | Industry profile |
| Guard face check-in | 11 Mobile & Kiosk / 02 Check-in | Home (flow copy) |
| Kiosk check-in | 11 Mobile & Kiosk / 05 Kiosk | Idle |
| Supervisor: cover a no-show | 11 Mobile & Kiosk / 04 Supervisor | Team night shift |

**Known prototype limits.** Tell participants beforehand that some buttons don't work yet.
- In the sign-in flow, *Verify* always shows the wrong-code state. That state is what task P1 tests.
- Onboarding fields aren't editable. Participants move with *Back*, *Save and continue* and the step list.
- In the reliever sheet, only *Assign Venkat* works. Choosing another reliever does nothing.
- The client rate card is a single static screen.
- The Tamil text hasn't had native review yet. Do that review **before** the guard sessions.

## Tasks

Read each task aloud as written. Don't name the button they need.

**Payroll executive (about 45 min)**

| # | Task | Success |
|---|---|---|
| P1 | "Sign in. Your number is +91 98400 12345." Then: "That didn't work. Get back in." | Notices the error message and uses *Send a new code* without being prompted |
| P2 | "You can't receive OTPs on your phone any more. Ask for help." | Reaches *Can't sign in* and picks the right reason |
| P3 | "Ravi's ESIC looks odd this month. Find out why, then approve October payroll." | Opens the explanation, says in their own words why ESIC applies, and approves through the confirm dialog |
| P4 | "You uploaded October joiners. Can you add them today? What's stopping row 41?" | Says 231 can be imported now, explains row 41 is a site-name typo, and chooses *Use suggestion* |
| P5 | "Before you approve: what does Marina Tech Park pay us each month for night guards, and is the contract profitable?" | Finds ₹3,09,600 and the 17.1% margin in 60 s or less |
| P6 | "You're setting up the Hyderabad office. Which state rules will apply to people there?" | Names PT monthly, LWF annual and Zone I, and understands that these come from the location |
| P7 | "Set up the company, then check: which holidays still need a date, and can you finish without the LLP's ESIC code?" | Finds the 3 holidays awaiting the gazette, and answers yes, but payroll for that entity is blocked |

**Guard (about 15 min, phone and then kiosk)**

| # | Task | Success |
|---|---|---|
| G1 | "You've reached the site for your shift. Mark your attendance." | Taps *Check in*, holds the phone for the face check and recognises the confirmation |
| G2 | "There's no signal. What happened to your check-in?" (show *Saved offline*) | Understands it was saved and will be sent later |
| G3 | "How much were you paid in October?" | Finds ₹18,450 from home |
| G4 | Kiosk: "Check in here." Then: "The camera isn't recognising you." | Waits for recognition, then finds the badge/PIN option |

**Supervisor (about 15 min)**

| # | Task | Success |
|---|---|---|
| S1 | "Who hasn't arrived for the night shift?" | Names the person or people not checked in, without scrolling the whole list twice |
| S2 | "Suresh isn't coming. Get Post 4 covered." | Assigns a reliever from the sheet and explains why Babu K is a poor choice |

## What we measure

- **Task success:** pass, pass with help, or fail, per task. The target is ≥ 80% unaided pass for each task.
- **Time on task** for P3, P4, P5 and G1. The target for G1 is under 10 s from tapping *Check in*.
- **Single Ease Question** (1–7) after each task.
- **SUS** at the end, payroll executives only. The target is ≥ 80.
- **Misreads:** every number or status the participant reads wrongly. Record these even when the task passes. One misread amount counts as a severity-1 issue.

## Running a session

- Use one moderator and one note-taker. Record the screen and audio only, with consent. Don't record faces in guard sessions. The kiosk camera frame in the prototype is a still image, so no real face capture happens.
- Every person and amount in the prototypes is fictional. Don't let participants type their real Aadhaar, phone or bank details.
- Ask them to think aloud. When they're stuck, wait 20 seconds, then ask "What are you looking for?" Count a direct hint as *pass with help*.
- In Tamil sessions, the moderator speaks Tamil, and the note-taker writes down the exact words that confused the participant.

## After the round

1. Within 2 days, log each issue in the design backlog with:
   - the task
   - the persona
   - a severity rating: 1 blocks the task, 2 slows it, 3 is cosmetic
   - the screen node ID
2. Fix every severity-1 and severity-2 issue in Figma. If the fix changes a component, the library must be published again and the product files updated to the new version.
3. Re-test only the changed tasks, with 2 or 3 users.
4. Mark a screen **Ready for dev** when:
   - every task that touches it meets the targets above
   - it has its tablet and phone layouts ([FIGMA_DESIGN_SPEC.md](FIGMA_DESIGN_SPEC.md) §7)
   - it has dev annotations
