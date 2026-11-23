# Argos Home: Data Model Guide

This document describes the data model of the first version (v1.0.0) of Argos Home: the entities, their fields and types, the enumerations, the relationships between entities and the validation rules and invariants the apps enforce. It documents the **schema** only. The sample household the apps generate at startup is not reproduced here, and every example value in this document is fictional and marked as such.

The model lives in the `domain/` package of each module (see `002-repository-structure-guide.md`). Types are given as they appear in the Kotlin source: `String`, `Int`, `Long`, `Double`, `Boolean`, `LocalDate` and `LocalTime` (from `java.time`), and `?` marks a field that may be empty (`null`).

---

## 1. Overview

```
Household 1 --- * HouseholdMember          (each member has one MemberRole)
Household 1 --- * Pet
Household 1 --- * Invitation               (pending, by email)
Household 1 --- 1 InviteCode               (current QR / 6-digit code)
Pet       1 --- * ScheduledActivity        (linked by pet name)
Pet       1 --- * MedicalRecord            (linked by pet id)
User      1 --- 1 UserProfile
User      * --- * Household                (a user belongs to several households)
User      1 --- * Notification             (each one tagged with a household name)
```

- The **household** is the root entity: members, pets, activities and medical records always belong to one household.
- The **phone** holds every entity. The **watch** holds only the shared ones (household, members, pets, activities) plus its own notification list.
- In v1.0.0 everything is kept **in memory**. There is no database, no server and no user account, so nothing survives closing the app.

### Where each entity lives

| Entity | Phone | Watch | Crosses the Data Layer |
| :-- | :-- | :-- | :-- |
| `Household`, `HouseholdMember`, `Pet` | Yes | Yes (identical copy) | No |
| `ScheduledActivity` | Yes (authoritative) | Yes (local copy, updated by the phone) | Only its id and state, through the messages in section 9 |
| `MedicalRecord` | Yes | No | No |
| Member role, `Invitation`, `InviteCode` | Yes | No | No |
| `UserProfile` | Yes | No | No |
| `Notification` | Yes (phone model) | Yes (watch model) | No |

---

## 2. Household

### 2.1. `Household`

| Field | Type | Description |
| :-- | :-- | :-- |
| `name` | `String` | Display name of the household. |
| `members` | `List<HouseholdMember>` | People who belong to the household. |
| `pets` | `List<Pet>` | Pets registered in the household. |

**Rules**

- A household holds at most **30 pets** (`Household.MAX_PETS`). The add pet action is refused once the limit is reached.
- A household holds at most **21 members** (`Household.MAX_MEMBERS`). The Household screen shows the count against this limit.

### 2.2. `HouseholdMember`

| Field | Type | Description |
| :-- | :-- | :-- |
| `id` | `String` | Stable identifier of the member. |
| `name` | `String` | Full name shown on cards and assignments. |

One member is the **local member**: the person the install acts as. Since v1.0.0 has no sign-in, every action taken on either device is attributed to the local member.

### 2.3. Member role (phone only)

On the phone, members are presented with a role through `HouseholdMemberUi`:

| Field | Type | Description |
| :-- | :-- | :-- |
| `id` | `String` | Same id as the `HouseholdMember`. |
| `name` | `String` | Same name as the `HouseholdMember`. |
| `role` | `MemberRole` | `ADMIN` or `MEMBER`. |

**Rules**

- The local member starts as the `ADMIN` and is listed first.
- Only an `ADMIN` can manage members: invite, remove and transfer the admin role. A `MEMBER` can only leave the household.
- The local member can never remove themself.
- **Transferring the admin role** swaps two roles in one step: the chosen member becomes `ADMIN` and the former admin becomes `MEMBER`. The transfer requires the user's password.

### 2.4. `HouseholdColor`

An enumeration that assigns one of the four brand color families to a household for its header and avatar: `PRIMARY`, `SECONDARY`, `TERTIARY`, `QUATERNARY`.

### 2.5. Household lists of the user (phone only)

A user may belong to several households. Two read-only views describe them:

- `HouseholdSummary` (Home switcher): `name: String`, `memberCount: Int`.
- `ProfileHousehold` (Profile list): `name: String`, `memberCount: Int`, `role: MemberRole`, `color: HouseholdColor`, `active: Boolean`.

**Rules**

- A user belongs to at most **8 households** (`ProfileHouseholds.MAX_HOUSEHOLDS`).
- Exactly one household is **active** at a time.
- In v1.0.0 only one household carries real data. Switching the active household changes the label shown in the headers, not the data.

---

## 3. Invitations (phone only)

### 3.1. `Invitation`

| Field | Type | Description |
| :-- | :-- | :-- |
| `id` | `String` | Identifier of the invitation. |
| `email` | `String` | Address the invitation was sent to. |
| `status` | `InviteStatus` | Always `PENDING` in v1.0.0. |

### 3.2. `InviteCode`

| Field | Type | Description |
| :-- | :-- | :-- |
| `code` | `String` | Six decimal digits, leading zeros kept. |
| `link` | `String` | Shareable join link that carries the code. |

**Rules**

- An invitation by email is only created for an address that passes the email format check (section 10).
- The invite code is always exactly **6 digits** and is generated at random.
- A code is valid for **10 minutes** (`INVITE_VALIDITY_SECONDS`). When the countdown reaches zero, or when the user asks for it, a new code and link replace the old ones and the old code stops being valid.
- The QR code shown on screen encodes the current link.
- v1.0.0 has no backend, so invitations are never accepted; they stay pending until they are removed.

---

## 4. Pet

### 4.1. `Pet`

| Field | Type | Default | Description |
| :-- | :-- | :-- | :-- |
| `id` | `String` | | Stable identifier of the pet. |
| `name` | `String` | | Full name of the pet. Required. |
| `type` | `PetType` | | Species; drives the avatar icon and color. |
| `breed` | `String` | empty | Breed or species detail. |
| `ageYears` | `Int` | `0` | Age in whole years. |
| `weight` | `Double` | `0.0` | Weight value, read with `weightUnit`. |
| `weightUnit` | `WeightUnit` | `KG` | Unit of the weight. |
| `height` | `Double` | `0.0` | Height value, read with `heightUnit`. |
| `heightUnit` | `HeightUnit` | `CM` | Unit of the height. |

### 4.2. Enumerations

| Enumeration | Values |
| :-- | :-- |
| `PetType` | `DOG`, `CAT`, `BIRD`, `RABBIT`, `SNAKE`, `RODENT`, `FISH`, `ARACHNID`, `REPTILE`, `POULTRY`, `AMPHIBIAN`, `OTHER` |
| `WeightUnit` | `KG` (kg), `G` (g), `LB` (lb) |
| `HeightUnit` | `CM` (cm), `M` (m), `IN` (in) |

**Rules**

- The name is the only required field of the pet form. A new pet starts as `DOG`.
- Age accepts up to three digits. Weight and height accept digits and a single decimal point.
- Deleting a pet also deletes all of its medical records, so no orphan history is left.

*Fictional example:* a pet named "Biscuit", type `CAT`, 4 years, 3.8 `KG`, 25 `CM`.

---

## 5. Scheduled activity

A scheduled activity is one care item of the household: a **task** (feeding, walk, play) or an **event** (a vet visit, a vaccination). This is the entity the phone and the watch agree on.

### 5.1. `ScheduledActivity`

| Field | Type | Description |
| :-- | :-- | :-- |
| `id` | `String` | Stable identifier; the only field that travels over the Data Layer, together with the state. |
| `type` | `ItemType` | `TASK` or `EVENT`. |
| `petName` | `String` | Name of the pet the activity is for. |
| `title` | `String` | Short title shown on the card. |
| `category` | `String` | Free-text kind of task or event (for example Walking, Feeding, Vaccination). |
| `date` | `LocalDate` | Day of the activity. |
| `startTime` | `LocalTime` | Start time. |
| `endTime` | `LocalTime?` | End time. Only events carry a range. |
| `recurrence` | `Recurrence` | How often it repeats. |
| `reminderAmount` | `Int` | Reminder lead, read with `reminderUnit`. |
| `reminderUnit` | `ReminderUnit` | Unit of the reminder lead. |
| `assignedTo` | `String` | Name of the member responsible for it. |
| `note` | `String` | Free-text note. |
| `state` | `ActivityState` | Current state; starts as `PENDING`. |
| `resolvedBy` | `String?` | Who made the last accepted transition. Empty until then. |
| `resolvedAtMillis` | `Long?` | When that happened, in epoch milliseconds. Empty until then. |

`subtitle` is derived as "category, pet" and is not stored.

### 5.2. Enumerations

| Enumeration | Values | Notes |
| :-- | :-- | :-- |
| `ItemType` | `TASK`, `EVENT` | |
| `ActivityState` | `PENDING`, `CONFIRMED`, `DONE`, `CANCELED` | `DONE` and `CANCELED` are terminal. |
| `Recurrence` | `ONCE`, `DAILY`, `WEEKLY`, `MONTHLY` | |
| `ReminderUnit` | `MINUTES`, `HOURS`, `DAYS`, `MONTHS` | Shared with medical records. |

### 5.3. State machine

```
            +-----------> CONFIRMED ------+
            |                 |           |
 PENDING ---+                 v           v
            +-------------> DONE      CANCELED
            |                             ^
            +-----------------------------+
```

| Current state | Allowed targets |
| :-- | :-- |
| `PENDING` | `CONFIRMED`, `DONE`, `CANCELED` |
| `CONFIRMED` | `DONE`, `CANCELED` |
| `DONE` | none (terminal) |
| `CANCELED` | none (terminal) |

**Rules**

- Tasks and events offer the same set of targets.
- Only legal transitions are offered as buttons on both devices, always in the order cancel, done, confirm. A resolved activity offers none.
- Every transition, whether it comes from a phone card or a watch message, goes through one function on the phone (`ActivityRepository.applyTransition`), which validates it again before applying it.
- A refused transition writes nothing. The answer carries the reason and, if the activity was already resolved, who resolved it and when.
- An accepted transition stores the actor (the local member) and the time in `resolvedBy` and `resolvedAtMillis`.

### 5.4. Creation rules (New activity modal)

- Title, pet and start time are required.
- An event also requires an end time, and the end time must be strictly after the start time.
- The reminder amount accepts up to three digits.
- The note is limited to **120 characters**.
- Activities created on the phone stay on the phone in v1.0.0; they do not reach the watch.

*Fictional example:* a `TASK` titled "Morning walk", category Walking, for "Biscuit", at 8:00 AM, `DAILY`, reminder 15 `MINUTES`, state `PENDING`.

### 5.5. Derived values

- **Day progress (Home):** canceled activities are ignored. Total is today's remaining activities, completed counts those in `DONE`, and the percentage is completed over total (zero when there are none).
- **Upcoming list (Home):** activities in `PENDING` or `CONFIRMED` from today on, soonest first.
- **Next event (Home):** the soonest `PENDING` activity whose start is still ahead of the current time.
- **Pet progress (Pets):** for each pet, today's activities in `CONFIRMED` or `DONE` over today's activities that are not canceled.

---

## 6. Medical record (phone only)

A medical record is one entry of a pet's health history. It is a separate entity from the calendar activity.

### 6.1. `MedicalRecord`

| Field | Type | Default | Description |
| :-- | :-- | :-- | :-- |
| `id` | `String` | | Identifier of the record. |
| `petId` | `String` | | Id of the pet it belongs to. |
| `type` | `MedicalRecordType` | | Kind of record. Required. |
| `title` | `String` | | Short title. Required. |
| `date` | `LocalDate` | | Day of the record. Required. |
| `startTime` | `LocalTime?` | empty | Start of the appointment window. |
| `endTime` | `LocalTime?` | empty | End of the appointment window. |
| `reminderAmount` | `Int` | `0` | Reminder lead, read with `reminderUnit`. |
| `reminderUnit` | `ReminderUnit` | `MINUTES` | Unit of the reminder lead. |
| `assignedTo` | `String` | empty | Member or clinic responsible for it. |
| `status` | `MedicalRecordStatus` | | Stored status. |
| `note` | `String` | empty | Free-text note, up to 120 characters. |

### 6.2. Enumerations

| Enumeration | Values |
| :-- | :-- |
| `MedicalRecordType` | `VACCINE`, `MEDICATION`, `PROCEDURE` |
| `MedicalRecordStatus` | `PENDING`, `REGISTERED`, `CANCELED` |

**Rules**

- The status shown is **derived from the date**: a `CANCELED` record stays canceled, a record dated before today reads `REGISTERED`, and any other reads `PENDING`.
- When both times are present, the end time must be strictly after the start time. An open end is allowed.
- A pet's history is listed newest first.

*Fictional example:* a `VACCINE` titled "Annual booster", dated next month, assigned to "Example Vet Clinic", read as `PENDING`.

---

## 7. Notification

Notifications are modeled separately on each device and are never exchanged between them.

### 7.1. Phone `Notification`

| Field | Type | Description |
| :-- | :-- | :-- |
| `id` | `String` | Identifier of the notification. |
| `type` | `NotificationType` | Kind of notification; drives the icon. |
| `title` | `String` | Main line. |
| `message` | `String` | Secondary line. |
| `householdName` | `String` | Household the notification belongs to, shown as a chip and used by the filter. |
| `timestampMillis` | `Long` | When it happened, in epoch milliseconds; drives the day sections and the relative time. |
| `read` | `Boolean` | Whether the user has read it; unread ones show a dot and count on the bell. |

### 7.2. Watch `Notification`

| Field | Type | Description |
| :-- | :-- | :-- |
| `id` | `String` | Identifier of the notification. |
| `type` | `NotificationType` | Kind of notification; drives the icon. |
| `title` | `String` | Main line. |
| `message` | `String` | Secondary line. |
| `time` | `String` | Ready-made time label. |
| `date` | `String` | Ready-made relative day label shown in the detail. |

### 7.3. `NotificationType`

`REMINDER`, `MEMBER_JOINED`, `INVITE`, `VACCINE_DUE`, `MEMBERSHIP`, `ADMIN_TRANSFER`.

**Rules**

- On the phone, notifications are listed newest first and grouped into Today, Yesterday and older dates.
- *Mark all read* sets `read` to true on every notification.
- The household filter offers every household present in the list.

---

## 8. User profile (phone only)

### 8.1. `UserProfile`

| Field | Type | Description |
| :-- | :-- | :-- |
| `firstNames` | `String` | Given name or names. Required. |
| `lastNames` | `String` | Last name or names. Required. |
| `email` | `String` | Account email address. |
| `birthDate` | `LocalDate` | Birth date, stored and shown as dd/MM/yyyy. |
| `phoneDigits` | `String` | National phone number, digits only. |

**Derived values**

- **Display name:** the first given name and the first last name.
- **Initials:** the first letter of each of the first two words of the display name, uppercase.

**Rules**

- The birth date can only be a past date.
- The phone number keeps digits only, up to **10 digits**, and is displayed grouped as 3-3-4 (fictional example: `000 000 0000`).
- The email is changed in its own flow, which requires a valid email format and the current password.
- Changing the password requires the current password, a new password that passes a minimum strength check, and a matching confirmation.
- Every account error is shown as a generic message, so the screen never reveals which check failed or what the exact requirements are.
- v1.0.0 has no accounts: the profile and any password change are kept in memory for the session only.

---

## 9. Messages between the phone and the watch

The two devices exchange two messages over the Wearable Data Layer `MessageClient`, encoded as UTF-8 JSON objects.

| Path | Direction | Message |
| :-- | :-- | :-- |
| `/argos/activity/update` | Watch to phone | `ActivityUpdate` |
| `/argos/activity/result` | Phone to watch | `ActivityResult` |

### 9.1. `ActivityUpdate`

| Field | JSON key | Type | Description |
| :-- | :-- | :-- | :-- |
| `activityId` | `activityId` | `String` | Activity the watch wants to change. |
| `targetState` | `targetState` | `ActivityState` (name) | State the watch asks for. |
| `requestedAtMillis` | `requestedAtMillis` | `Long` | When the user tapped, in epoch milliseconds. |
| `memberId` | `memberId` | `String` | Member who asked (the local member). |

### 9.2. `ActivityResult`

| Field | JSON key | Type | Description |
| :-- | :-- | :-- | :-- |
| `activityId` | `activityId` | `String` | Activity the answer refers to. |
| `accepted` | `accepted` | `Boolean` | Whether the phone applied the change. |
| `resultingState` | `resultingState` | `ActivityState` (name) | The state really stored on the phone, on rejection too. |
| `reason` | `reason` | `RejectionReason?` (name) | Why it was refused. Empty when accepted. |
| `actorName` | `actorName` | `String?` | Who resolved the activity. |
| `resolvedAtMillis` | `resolvedAtMillis` | `Long?` | When it was resolved. |

`RejectionReason`: `ALREADY_DONE`, `ALREADY_CANCELED`, `INVALID_TRANSITION`, `UNKNOWN_ACTIVITY`.

**Rules**

- The phone sends an `ActivityResult` as the answer to every `ActivityUpdate`, and also pushes one to the watch after any accepted change made on the phone itself.
- Decoding never throws. A payload that is not valid JSON, misses a required field or carries an unknown enumeration name is discarded and logged.
- Enumerations travel by name and are read with a lenient lookup that returns nothing on an unknown name.

---

## 10. Validation rules summary

| Rule | Where it applies | Behavior |
| :-- | :-- | :-- |
| Email format | Invite by email, change email | One `@`, a dot in the domain, no spaces. |
| Time range | New event, medical record | The end time must be strictly after the start time; an open end is allowed. |
| Required fields | Pet form, new activity, medical record, edit profile | Save stays disabled until they are filled. |
| Note length | New activity, medical record | Up to 120 characters. |
| Numeric input | Age, reminder amount, weight, height, phone | Digits only (a single decimal point for weight and height), with length limits. |
| Limits | Household | 30 pets, 21 members; a user belongs to up to 8 households. |
| Invite code | Household | 6 digits, valid for 10 minutes, then replaced. |
| Password | Change password, transfer admin, change email | Checked against the current password; a new password must pass a minimum strength check; errors are generic. |
| Birth date | Edit profile | Past dates only. |
