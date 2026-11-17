# Functional Requirements Document: Argos Home

**Technological University of the Riviera Maya**
Engineering in Software Development and Management — "IT91"

Presented by:

- Sánchez Trejo Cesar David — 2302073
- Santos Absalón Aarón de Jesus — 2302042

Class of 2026-2027 · Development for Smart Devices · Unit I — Responsive Interface Design
Ing. Cesar David Hernández Hernández · Playa del Carmen, Quintana Roo · September 15, 2026

---

## Table of contents

- I. Introduction
- II. Content
  - 2.1. Application Overview
    - 2.1.1. Problem Statement
    - 2.1.2. Application Purpose
    - 2.1.3. User Goals
  - 2.2. Feature Functionality by Device
    - 2.2.1. Feature Adaptation Table
  - 2.3. Data Flow and Storage
    - 2.3.1. Data Collection (Wearable)
    - 2.3.2. Data Storage (Mobile Phone)
    - 2.3.3. Data Display (Smart Display)
- III. Conclusion
- IV. Bibliographic Sources

---

## I. Introduction

Argos Home is a multi-platform application that centralizes the daily care of pets within a single household, distributing its functionality across three types of devices mobile phone, smartwatch, and smart display so that each one participates in a genuine and differentiated way in the overall experience, rather than acting as a simple scaled-down replica of the mobile app.

This document follows the official functional specification template for the assignment and is aimed at both readers with prior software development experience and those approaching a project of this kind for the first time: each section explains not only what the application does, but why each functionality was designed to behave that way on each device, backing these decisions with official technical documentation, international standards, and recent academic literature on household coordination and the Internet of Things applied to pets.

---

## II. Content

Functional Requirements Document: Argos Home.

### 2.1. Application Overview

#### 2.1.1. Problem Statement

Caring for a pet is rarely the responsibility of a single person: in most households, two or more members a partner, children, roommates, relatives take turns feeding it, walking it, and keeping track of its health. This informal division of tasks, however, almost never relies on a tool that makes it visible to everyone involved at the same time, which leads to recurring and predictable errors: the pet is fed twice in a row because each caregiver assumed it was their turn, a scheduled walk is forgotten because no one confirmed having done it, or a vaccine is delayed because the responsibility of scheduling it remained unclear among several adults.

This phenomenon is not exclusive to pet care, nor is it anecdotal: human-computer interaction research on household coordination has documented, through field studies with real households, that much of the work involved in organizing and dividing domestic tasks is "invisible" it is not recorded in any shared system and that this invisibility is precisely what causes tasks to be duplicated, forgotten, or to create friction among household members (Sohn et al., 2012). Applied to pet care, the problem is compounded because, unlike a household chore that can be postponed without serious consequences, missing a vaccine or duplicating a meal has a direct impact on the animal's health.

A second, more technical problem adds to this coordination gap: the digital scheduling or reminder solutions that families adopt informally chat groups, shared notes, generic calendars are not designed with the assumption that confirming a task should happen at the exact moment and from whichever device the person physically has closest at hand (the phone in their pocket, the watch on their wrist, or the fixed screen in the kitchen).

Academic evidence on the Internet of Things applied to pet care confirms that much of the recent research in this field has focused precisely on closing gaps in monitoring, feeding, and activity logging through connected systems (Pico-Valencia & Holgado-Terriza, 2025; Own et al., 2011), which validates that centralizing and synchronizing this information across multiple devices not just among multiple people is a real and current need, not a superfluous feature.

#### 2.1.2. Application Purpose

Argos Home solves this problem by centralizing all pet-care information under the concept of a Household: a single root entity that exclusively groups the pets, the members who care for them, and every feeding, walking, or medical record, ensuring that no data from one household is ever mixed with another's, even when a single user belongs to several households at once (for example, their own home and a relative's).

Building on this foundation, the application does not simply display the same screen at three different sizes: it deliberately distributes responsibility for each feature according to what each type of device does best a design principle directly supported by Google's official Wear OS documentation, which recommends that wearable apps focus on critical tasks the user can complete in seconds, rather than transferring the full complexity of a mobile app onto the wrist (Android Developers, n.d.-a, n.d.-b).

Similarly, Google Assistant's official conversation design guidelines establish that devices with a screen and a voice assistant should be designed with a "voice-first" approach, where the screen visually reinforces what has already been communicated verbally, rather than requiring the user to walk over and tap the screen for every interaction (Google for Developers, n.d.-a, n.d.-b).

The result is an architecture in which the phone concentrates full management and historical detail, the smartwatch handles exclusively the immediate confirmation with the least possible effort, and the smart display turns the household's status into ambient information, visible from a distance and accessible by voice to any member passing through the kitchen or living room.

This division of responsibilities not only prevents duplicate meals or walks: it also solves the technical problem of synchronization across devices, relying on the WebSocket protocol standardized by the IETF to propagate each confirmation within seconds to every device in the same household (Fette & Melnikov, 2011), and on logical data partitioning by household identifier, as recommended in MongoDB's official documentation, to prevent information leaks between different households sharing the same infrastructure (MongoDB, n.d.).

#### 2.1.3. User Goals

With Argos Home, anyone who is part of a household with one or more pets can achieve the following concrete goals:

- Coordinate daily care without relying on memory or text messages between caregivers, since every meal or walk confirmation is logged with date, time, and author, and is immediately visible to all household members.
- Confirm a task from whichever device is physically closest at hand at any given moment, without needing to unlock the phone and open the full application for something as simple as marking that the pet has already eaten.
- Avoid duplication and misunderstandings between caregivers, thanks to the system detecting near-simultaneous confirmations of the same task and clearly showing who completed it first.
- Maintain a cumulative, reliable, and shared medical history, so that any household member not just whoever took the pet to the vet has visibility into administered vaccines and diagnosed conditions.
- Belong to more than one household without data overlapping, switching between, for example, their own home and a relative's, with the guarantee that each household keeps its data completely isolated.
- Delegate responsibilities in an orderly way, through a QR-code or numeric invitation system that allows new caregivers to be added to the household without compromising the security or integrity of existing data.

### 2.2. Feature Functionality by Device

This section details the specific features and how their functionality is adapted for each of the three target device types.

The table below details Argos Home's core features. For each one, all three devices participate with a genuinely distinct behavior: when a device does not carry out the complete action, it still reflects, notifies, or announces its outcome, so that no device is left without a real function within that feature.

The design of each behavior follows the core capabilities of each form factor general-purpose computing on mobile, glanceable monitoring on the wearable, and ambient multimodal interaction on the smart display as documented in the official Android Developers and Google for Developers guidelines, as well as in the ISO/IEC 18004 standard for QR code generation.

#### 2.2.1. Feature Adaptation Table

| Feature Name | Mobile Phone | Smartwatch | Smart Display |
| :-- | :-- | :-- | :-- |
| **Household Registration and Linking** | Complete form to create a household (name only) or join an existing one by scanning a QR code or entering a 6-digit code; selector to switch between multiple households. | Does not take part in setup but shows a brief confirmation notification once linked to the session ("Linked to: The Pérez Household"). | Announces by voice and displays an ambient welcome message once the household is set up, without taking part in the creation flow. |
| **Invitation and Member Management** | Generates the QR code (per ISO/IEC 18004) or the 6-digit code, checks its validity, removes members, and transfers administration. | Receives a one-time notification when a new member joins the household ("Ana joined the household"), with no ability to generate or revoke invitations. | Announces by voice the arrival of the new member and visually updates the household member list on the ambient panel. |
| **Pet Profile** | Creates and edits the complete profile (name, breed, type, weight, height, photo), available only to the administrator. | Shows the pet's name and a thumbnail as part of the next pending action card, with no editing option. | Displays the pet's photo or icon in large format as a quick visual identifier within the panel. |
| **Confirm Meal or Walk** | Views the full calendar of recurring schedules per pet, edits them, and manually confirms past events. | Confirms with a single tap from a card showing the next pending action ("Walk Rocky 4:00 PM"), with vibration when the scheduled time arrives. | Confirms via voice command ("Argos, did we already walk Rocky?"), with immediate update of the corresponding icon. |
| **Daily Status / Care Summary** | Shows a detailed list with checkmarks per pet and allows browsing summaries from previous days. | Shows a single glanceable card with the "up to date" or "pending" status of the next action, with no additional navigation. | Shows a permanent ambient panel with large icons per pet, visible from a distance to the whole household. |
| **Escalated Reminders and Alerts** | Receives a push notification and, if no one confirms within 30 minutes, an email flagged as "urgent pending." | Vibrates and shows a pop-up card at the exact scheduled time. | Announces the reminder out loud through the device's assistant. |
| **Medical History (vaccines and illnesses)** | Allows full editing: adding a vaccine or illness with a date and viewing the cumulative timeline (administrator only). | Shows a glanceable notification on the day a vaccine is due ("Today: Rocky's rabies vaccine"), with no access to or editing of the full history. | Shows, in read-only mode, the next scheduled vaccine within the ambient panel. |
| **Active Household Selector (multi-household)** | Freely switches between all households the user belongs to via a visible selector. | Automatically inherits the active household selected on the phone; the next-action card always reflects that current household, with no selector of its own. | Remains fixed, showing the household of the physical location where it is installed, functioning as that household's own "dashboard." |
| **Duplicate Confirmation Detection** | Shows in the daily summary who confirmed each action and at what time, allowing possible duplicates to be identified and reviewed. | If it detects that another member already confirmed the same action, it immediately switches to a "done" state showing who performed it, preventing an unnecessary second confirmation. | Issues a brief spoken notice if someone tries to confirm via voice an action that has already been logged ("Rocky was already walked by Juan 1 minute ago"). |
| **Synchronization and Connection Status** | Shows a manual retry screen with a status indicator if synchronization with the server fails. | Shows a "syncing…" icon on the card when a confirmation was saved offline and has not yet propagated. | Shows a discreet ambient update indicator while waiting for the latest data, without blocking the rest of the visible information. |
| **Administration Transfer** | Runs the complete flow to transfer the administrator role to another member, required if the sole administrator wants to leave the household. | Receives a one-time notification informing of the change in responsibility ("Ana is now the household administrator"). | Announces the change of administrator ambiently, without taking part in the transfer flow. |

### 2.3. Data Flow and Storage

This section outlines how data is managed across the three devices to ensure a seamless and consistent user experience.

#### 2.3.1. Data Collection (Wearable)

The smartwatch acts as the fast-input device for the entire system: its primary function is not to store or process information, but to immediately capture point-in-time meal or walk confirmations, along with the exact timestamp, the identifier of the user who confirmed it, and the household_id implicit in the active session at that moment.

This design responds directly to the Wear OS principle recommending that wearable apps stay focused, allowing the user to complete the critical task in seconds and avoiding deep navigation hierarchies that would be uncomfortable on such a small screen (Android Developers, n.d.-a).

When the device confirms an action without an internet connection for example, if the paired phone is out of range the event is saved locally on the watch itself and automatically synchronized once the connection is restored, showing a "syncing…" visual state in the meantime so the user knows their confirmation has not been lost.

Transfer to the household's other devices happens within seconds thanks to a persistent, bidirectional connection, as defined by the WebSocket protocol standardized by the IETF, which removes the need to open multiple HTTP connections to simulate real-time updates and considerably reduces communication overhead compared to periodic polling schemes (Fette & Melnikov, 2011).

#### 2.3.2. Data Storage (Mobile Phone)

The mobile phone functions as the storage and processing hub of the entire system: it holds the complete household management (creation, invitations, administration transfer), each pet's detailed profile, the cumulative medical history, and the logic that decides when to escalate an unconfirmed alert. Any event generated from the smartwatch or the smart display ultimately synchronizes and persists on the phone, always tied to a specific household_id that acts as the logical partition key.

This design pattern aligns with the tenant-identifier isolation strategy officially documented by MongoDB, in which every stored document includes an identifier field that ensures queries return exclusively the data for the corresponding household, preventing any information leak between different households sharing the same database (MongoDB, n.d.).

To let a user switch between several households without having to re-authenticate every time, the system uses JSON Web Tokens whose content includes the full list of household identifiers the user belongs to, following the official JWT specification published by the IETF, which allows this kind of information to be embedded as part of the token's signed claims (Jones et al., 2015).

The invitation process itself also relies on the international ISO/IEC 18004 standard for generating the QR codes that a new member scans when joining an existing household, ensuring the code is interoperable with any standard scanner on the market (International Organization for Standardization, 2024).

#### 2.3.3. Data Display (Smart Display)

The smart display does not manage the full medical history or member administration functions that remain exclusive to the phone; instead, it queries a lightweight summary of the active household: the day's status for each pet and the next scheduled vaccine. Being fixed in a common area of the household, such as the kitchen or living room, it functions as a true "family dashboard" visible to any caregiver who passes by, without anyone needing to open an app to check it.

Its interaction prioritizes voice over touch as the primary channel, following Google's official design guidelines for assistant-enabled devices, which recommend assuming the user may be physically far from the device for example, cooking on the other side of the room and needs to be able to move the interaction forward using voice alone (Google for Developers, n.d.-a).

These same guidelines recommend scaling the design from a purely spoken conversation toward a multimodal experience that combines voice with supporting visual elements (Google for Developers, n.d.-b), a principle Argos Home applies directly: when it is time for a walk, the display announces the reminder by voice and, at the same time, visually reflects the change in status on the panel, so the message reaches both the person listening and the person who merely glances at the screen.

---

## III. Conclusion

In conclusion, Argos Home addresses a household coordination problem that human-computer interaction research itself has shown to be recurring and costly for households that divide tasks among several caregivers (Sohn et al., 2012), and it does so through an architecture centered on the household as a root entity, with data always isolated by household_id and synchronized in real time across three devices that play genuinely distinct roles. The phone concentrates management and historical details; the smartwatch handles immediate confirmation with the least possible effort; and the smart display turns the household's status into ambient, voice-accessible information for any nearby member.

None of the three devices is reduced to a simplified replica of the others: each one participates with its own function even in administrative tasks where it does not execute the full action, instead reflecting or announcing its outcome. This approach, supported both by official technical standards (IETF, ISO/IEC, MongoDB) and by platform design guidelines (Android Developers, Google for Developers), as well as by academic literature on the Internet of Things applied to pets and on household coordination (Pico-Valencia & Holgado-Terriza, 2025; Own et al., 2011; Sohn et al., 2012), lays a solid and professional foundation for two or more caregivers to share responsibility for a pet without friction, without duplication, and without losing sight of any detail of its care.

---

## IV. Bibliographic Sources

- Android Developers. (n.d.-a). *Principles of Wear OS development.* Google. Retrieved from https://developer.android.com/training/wearables/principles
- Android Developers. (n.d.-b). *Wear OS user interfaces.* Google. Retrieved from https://developer.android.com/training/wearables/user-interfaces
- Fette, I., & Melnikov, A. (2011). *The WebSocket protocol (RFC 6455).* Internet Engineering Task Force. https://doi.org/10.17487/RFC6455
- Google for Developers. (n.d.-a). *Design guidelines: Interactive Canvas.* Google. Retrieved from https://developers.google.com/assistant/interactivecanvas/design
- Google for Developers. (n.d.-b). *Scale your design: Conversation design.* Google. Retrieved from https://developers.google.com/assistant/conversation-design/scale-your-design
- International Organization for Standardization. (2024). *Information technology — Automatic identification and data capture techniques — QR code bar code symbology specification (ISO/IEC 18004:2024).* Retrieved from https://www.iso.org/standard/83389.html
- Jones, M., Bradley, J., & Sakimura, N. (2015). *JSON Web Token (JWT) (RFC 7519).* Internet Engineering Task Force. https://doi.org/10.17487/RFC7519
- MongoDB. (n.d.). *Build a multi-tenant architecture.* MongoDB Docs. Retrieved from https://www.mongodb.com/docs/atlas/build-multi-tenant-arch/
- Own, C., Teng, C., Zhang, J., Wen, Y., & Tsai, S. (2011). Intelligent pet monitor system with the internet of things. *International Conference on Machine Learning and Cybernetics, 2,* 471-476. https://doi.org/10.1109/ICMLC.2011.6016785
- Pico-Valencia, P., & Holgado-Terriza, J. A. (2025). The internet of things empowering the internet of pets An outlook from the academic and scientific experience. *Applied Sciences, 15*(4), 1722. https://doi.org/10.3390/app15041722
- Sohn, T., Lee, L., Zhang, S., Dearman, D., & Truong, K. (2012). An examination of how households share and coordinate the completion of errands. *Proceedings of the ACM 2012 Conference on Computer Supported Cooperative Work,* 729-738. https://doi.org/10.1145/2145204.2145315
