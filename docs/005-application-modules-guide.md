# Argos Home: Application Modules Guide (v1.0.0)

This guide describes what the user sees and can do in the first version of Argos Home: every module, submodule, screen and modal of the phone app and the watch app. It describes the behavior of the interface; the entities behind it are described in `004-data-model-guide.md`, and the steps to run both apps are in `001-project-setup-and-usage-manual.md`.

Two conventions apply to every screen of both apps:

- **Color is never alone.** Every state, role and type is written as text next to its color.
- **Actions follow the rules.** A screen only offers the actions that are legal at that moment, instead of showing them disabled.

---

## 1. Phone app (`:apps:mobile`)

### 1.1. Navigation

| Element | Behavior |
| :-- | :-- |
| Bottom navigation bar | Five sections: **Home**, **Pets**, **Calendar**, **Household** and **Profile**. The active one is highlighted by color and a heavier label. Selecting Calendar opens it on today in its default view; selecting Household or Profile returns to its main view. |
| Notification bell | In the top bar of Home, Pets, Calendar and Profile. A badge shows the number of unread notifications. A tap opens the Notifications screen over the current section. |
| System back | Closes the Notifications screen first, then walks back through the sub-screens of Pets, Household and Profile. |

### 1.2. Home

The landing screen: a summary of the active household's day.

| Area | Content |
| :-- | :-- |
| Header | The user's initials avatar, a greeting that depends on the time of day (*Good morning*, *Good afternoon*, *Good evening*), the active household as a pill, and the bell. |
| Today's care | A progress ring with the percentage of today's activities that are done, next to the number done out of the total. |
| Counters | Total, pending and completed activities of today, and the title of the next pending event (a dash when there is none). |
| Care by pet | A horizontal carousel with one card per pet (type color, avatar, name, type and breed) and the household's pet count. A **double tap** on a card opens that pet's profile in Pets. |
| Upcoming | The only scrolling list of the screen: every open activity from today on, soonest first, with its category icon, pet, schedule and state. A **double tap** on an activity opens the Calendar Day view on its date. |

**Modal: Switch household.** Opened from the household pill. Lists the households the user belongs to with their member count, marks the active one with a heavier border and a check, and offers **Create or join household**. Choosing a household makes it the active one; in v1.0.0 this changes the label only. *Create or join household* closes the modal, because that flow is not part of this version.

### 1.3. Pets

#### Pets list

- One card per pet with its type-colored avatar, name, type and breed, and a progress bar of the pet's care today.
- A counter of pets against the household limit (30).
- **Add pet**, pinned below the list. When the household is full it explains that the limit was reached.
- A tap on a pet opens its profile.

#### Pet profile (submodule)

| Area | Content |
| :-- | :-- |
| Top bar | Back control and a pencil that opens **Edit pet**. |
| Profile | Avatar in the type color, a photo button shown for design fidelity (it does not pick an image in this version), and the type and breed chips. |
| Measures | Age, weight and height, each with its icon and unit. |
| Tabs | A segmented control with **Care schedule** and **Health records**. Only the tab content scrolls. |
| Pinned actions | **Add health record** (on the Health records tab only) and **Delete pet**. |

- **Care schedule tab:** the pet's activities with their category, schedule and state, and *Confirmed by* followed by the member's name once an activity is resolved.
- **Health records tab:** the pet's medical history, newest first. Each row shows the type icon, title, date and time range, who is responsible, and the status (*Pending*, *Registered* or *Canceled*). An empty history shows *No health records yet.*

#### Add pet / Edit pet (submodule)

A full-screen form. *Add pet* starts empty with Dog selected; *Edit pet* starts with the stored values.

- An avatar that follows the selected type, with the same static photo button.
- **Full name** (required), **Type** (a four-column grid of the twelve species), **Breed / species**, **Age**, **Weight** and **Height**, each measure with its unit selector.
- **Cancel** returns to where the form was opened from; **Save** is enabled once the name is filled and opens the pet's profile.

#### Add record (submodule)

A full-screen form over the pet profile for a new medical record.

- **Record type** (Vaccine, Medication or Procedure), **Title** and **Date** are required.
- Optional **Start time** and **End time** (the end must be after the start, otherwise an error is shown), a reminder (amount and unit), **Assigned to / clinic** and **Notes** (up to 120 characters).
- **Save** adds the record to the Health records tab.

#### Modal: Delete pet

A warning dialog that names the pet and explains that its care and health records are removed too and that this cannot be undone. **Delete** (red) confirms; **Cancel** closes it.

### 1.4. Calendar

| Area | Content |
| :-- | :-- |
| Top bar | Title and the bell. |
| View selector | **Month**, **Week** and **Day**. Day is the default view. |
| Pet filter | **Filter by Pet**: *All pets* or a single pet. Applies to every view. |

- **Month view:** a grid of the month with previous and next controls. Days with activities show a dot with their count. A tap on a day opens it in Day view.
- **Week view:** a summary of the selected day with a mini month beside it, followed by that day's activity cards. Picking a day in the mini month changes the selected day.
- **Day view:** a date navigator (*Today* or the weekday, with the date) with previous and next day arrows, the schedule header with the number of activities, and the day's activity cards. An empty day shows *No activities for this day.*
- **New event**, available in every view, opens the New activity modal.

**Activity card.** Pet avatar, a TASK or EVENT badge, the state pill (*Pending*, *Confirmed by me*, *Completed* or *Canceled*), the title, "category, pet", chips for the date, the time or time range, the frequency, the reminder and the assignee, and the note (up to two lines). Below it, one button per legal action: **Cancel**, **Done** and **Confirm**. A resolved card shows no buttons. A change applied here is pushed to the watch at once.

**Modal: New activity.** A bottom sheet with a **Task / Event** toggle. The shared fields stay in place and the type-specific ones change:

- **Title** and **Pet** (required), **Task type** or **Event type** with suggestions, **Date**, **Time** (an event needs a start and an end time, and the end must be after the start), **Reminder** (amount and unit), **Frequency**, **Assigned to** (*You* or a household member) and **Notes** (up to 120 characters).
- **Save** is enabled once the required fields are valid; the new card appears in the calendar at once. **Cancel** or **Close** discards it.

### 1.5. Household

#### Household main view

| Area | Content |
| :-- | :-- |
| Header | Tone of the household's color, the members counter against the limit (21), the house avatar, the household name, the user's role badge, and two actions: **Invite member** and **View schedule** (opens the Calendar in Month view). |
| Members | One card per member: initials avatar, full name (with *(you)* for the local user), role badge (*Admin* or *member*) and, for an admin, a remove icon on every other member. Only the cards scroll. |
| Tools | For an admin (*Admin tools*): **Transfer admin rights**, **Invite new member** and **Leave household**. For a member (*Members tools*): only **Leave household**. |

**Modal: Remove member.** Names the member and the household and warns that this cannot be undone. **Remove** (red) confirms; **Cancel** closes it.

**Modal: Leave household.** Asks for confirmation and reminds an admin to transfer the administration before leaving if other members remain. In v1.0.0 there is no other household to land in, so confirming only closes the dialog.

#### Invite member (submodule)

A segmented control with two tabs that share the **Pending invitations** list (email, *Pending* status and a remove control).

- **QR code and digit code:** a scannable QR code to join the household, the **6-digit code**, an *Expires in* countdown of ten minutes that replaces the code with a new one when it reaches zero, and **Share invite link**, which opens the system share sheet with the join link.
- **By email:** an email field and **Send invitation**, enabled only for a valid address. Sending adds the address to the pending list.

#### Transfer administration (submodule)

- An information card explaining that the new admin gains full control, that the user becomes a regular member, and that the password is required.
- The list of the other members, each selectable with a radio button. A double tap on a member opens the confirmation directly.
- **Transfer administration**, enabled once a member is selected, opens the confirmation.

**Modal: Confirm transfer.** A password field with a show or hide control. **Transfer** swaps the roles and returns to the main view, where the admin tools are no longer shown. A wrong password shows a generic error.

### 1.6. Profile

#### Profile main view

| Area | Content |
| :-- | :-- |
| Header | Large initials avatar with a pencil (a double tap opens Edit profile), the display name, the email, the active household as a pill (opens the same Switch household modal as Home), and the bell. |
| Counters | Households the user belongs to, pets in the active household, and the user's role there. |
| Account settings | **Edit profile**, **Change password** and **Change email**. |
| My households | The user's households against the limit (8), each with its color, member count, role and an *Active* mark, plus **Create or join household** (opens the Switch household modal) and **Sign out**. |

#### Edit profile (submodule)

**Name(s)** and **Last name(s)** (required), **Birth date** (a date picker that only allows past dates) and **Phone number** (digits only, grouped as you type). A large avatar with a camera badge is shown for design fidelity; picking a photo is not part of this version. **Save** stores the changes and returns.

#### Change password (submodule)

**Current password**, **New password** and **Confirm new password**, each with a show or hide control. **Update** checks them; errors appear after the first attempt and are generic.

#### Change email (submodule)

**New email** and **Current password**. **Update** stores the new email when the address is valid and the password is correct; otherwise it shows a generic error.

**Modal: Sign out.** Warns that the user will need to sign in again. Confirming returns to Home, because v1.0.0 has no accounts.

### 1.7. Notifications

A full-screen view opened from the bell.

- A back control, the title and **Mark all read**, which clears every unread dot and the bell badge.
- **Filter by household**: *All households* or one of the households present.
- Notifications grouped into **TODAY**, **YESTERDAY** and older dates. Each card shows the type icon, the title, the household chip, the relative time (*now*, minutes, hours or days ago) and an unread dot.

---

## 2. Watch app (`:apps:wear`)

### 2.1. Navigation

The watch app is a horizontal pager with three modules: **Home**, **Actions** and **Notifications**. A page indicator stays fixed at the bottom over all three; it follows the current module and a tap on a dot jumps to that module. The watch only shows the activities assigned to the local member.

### 2.2. Home

The next activity to decide, and the states around a request.

| State | What the user sees |
| :-- | :-- |
| Next activity | A card with the pet avatar, the pet name, the activity type and title, and the schedule and time. Below it, the legal actions: **CANCELED**, **DONE** and **CONFIRMED**. The card starts on the activity nearest to the current time. |
| Syncing | *Syncing with phone...* with *Keep your phone nearby*, while a request is on its way. No action can be tapped twice in the meantime. |
| Result | The answer from the phone in words (for example *Marked as done*, *Confirmed*, *Canceled*, or *Already completed by* a member *at* a time). An accepted change moves on to the next unresolved activity after a moment; a refusal stays on screen until it is tapped. |
| Connection error | *Connection error* with an explanation and a way back, so the user can try again with the phone nearby. Nothing changes on either device. |
| Up to date | *Up to date* and *All pets cared for today*, once every activity is resolved. |

### 2.3. Actions

- **Activities list:** every open activity of the member (pending or confirmed) with the *Pending* counter. Each card shows the type avatar, the pet, colored chips for the kind and the category, and the time and date. The time and date are yellow when the activity is today and red when it is overdue.
- **Focused detail:** a tap on an activity opens the same card as Home with the same actions, and a control to go back to the list.
- **Empty state:** *All activities complete* when nothing is left.

### 2.4. Notifications

- **List:** a round-aware list of the watch's notifications, each with a colored type icon, title, message and time.
- **Focused detail:** a tap opens the notification centered on the screen with its icon, title, message, time and relative day.
- **Empty state:** shown when there are no notifications.

The watch notifications are a local list. They are not received from the phone in this version.

---

## 3. How a change travels between devices

| Where the user acts | What happens |
| :-- | :-- |
| Watch, Home or Actions | The watch asks the phone for the transition and waits. The phone validates it, applies it if it is legal and answers with the resulting state. The watch shows the answer and the phone card updates by itself. |
| Phone, Calendar card | The phone applies the transition and pushes the new state to the watch, which updates its card. |
| Phone, any other section | Pets, medical records, household members, invitations, profile and notifications stay on the phone and are never sent to the watch. |

Both apps must be open, with the phone app in the foreground, for a change to travel (see `001-project-setup-and-usage-manual.md`).

---

## 4. Outside the first version

These items appear in the interface for design fidelity or are described in `003-software-requirements-specification.md`, but they are not functional in v1.0.0:

- Creating or joining a household, and accepting an invitation.
- Switching between households with their own data (only the label changes).
- Picking a photo for a pet or for the profile.
- Real sign-in and sign-out.
- Activities created on the phone reaching the watch, and notifications sent from the phone to the watch.
- Push and email reminders, a server with real-time synchronization and persistence, and the smart display.
