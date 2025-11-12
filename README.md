# 📝️ Simple Todo List App

**A simple, reactive, and modern task management application for Android.**

This project demonstrates the use of modern Google libraries and adheres to Clean Architecture principles (MVVM with Kotlin Flow) for building a scalable and highly testable mobile solution.

## ✨ Core Features

---

### 📱 Modern UI & Task Management

* **Reactive UI (Jetpack Compose):** 🚀 Fully developed using **Jetpack Compose** to ensure a **smooth, modern, and adaptive** user interface that instantly reacts to data changes.
* **Flexible Task Management (CRUD):** ➕ Includes all standard operations (Create, Read, Update, Delete) with the ability to toggle completion status and assignment status (`isAssigned`).
* **Swipe-To-Reveal Actions:** 🗑️ Convenient implementation of swipe gestures (left/right) for **instant access to actions** (e.g., quick deletion or status change) without requiring an extra tap.

---

### ⏰ Prioritization & System Integration

* **Robust Reminders (AlarmManager):** 🔔 Ability to set **time-precise notifications** using the core system service **Android AlarmManager** for reliable delivery.
* **Permission Handling:** 🔐 Correctly handles all necessary runtime permissions, including `POST_NOTIFICATIONS` (for Android 13+) and `SCHEDULE_EXACT_ALARM`, ensuring uninterrupted reminder functionality.
* **Drag-and-Drop Sorting:** ⬆️⬇️ Convenient feature for **manual sorting** of uncompleted tasks via drag-and-drop, allowing for easy and quick prioritization.

---

### 🧹 Batch Operations

* **Multi-Select Mode:** ✅ Efficient function for selecting **multiple tasks simultaneously** to perform batch actions, such as bulk deletion.

## 🏛️ Architecture and Tech Stack

The project is structured following **Clean Architecture** principles (UI/Presentation, Domain/Repository, Data) and implements the **MVVM (Model-View-ViewModel)** architectural pattern.

| Category | Technologies | Purpose |
| :--- | :--- | :--- |
| **UI/View Layer** | `Jetpack Compose` | Modern declarative UI toolkit. |
| **Presentation Layer** | `ViewModel`, `StateFlow` | UI state management and data flow handling. |
| **Dependency Injection** | `Dagger Hilt` | Managing the lifecycle and injection of dependencies. |
| **Concurrency** | `Kotlin Coroutines`, `Flow` | Asynchronous data operations and reactive streams. |
| **Data Layer** | `Room Persistence Library` | Robust data storage (DAO) and reactive caching. |
| **System Services** | `AlarmManager`, `BroadcastReceiver` | Scheduling and handling system notifications. |

## ⚙️ Setup and Running

To clone, build, and run the application, you will need the following environment:

* **Android Studio:** Version **Hedgehog (2023.1)** or newer (recommended).
* **JDK:** **JDK 11** or higher.

### 📱 Device Requirements

* **Minimum Supported OS:** Android 7.1 Nougat (API 25).
* **Testing:** A device running **Android 13 (Tiramisu, API 33)** or higher is required to fully test all notification and exact alarm permission flows.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/dsosnovskiy/SimpleTodoList
    ```
2.  Open the project in Android Studio.
3.  Click the **Run** button or use the shortcut `Shift + F10`.

## 📸 Screenshots

<div align="center">
    <img src="/assets/screenshots/welcomeScreen.png" alt="Welcome Screen" width="300"/>
</div>

<div align="center">
    <img src="/assets/screenshots/permissionPromptScreen.png" alt="PermissonPrompt Screen" width="300"/>
</div>

<div align="center">
    <img src="/assets/screenshots/mainScreenTodos.png" alt="Main Screen Todos" width="300"/>
</div>

<div align="center">
    <img src="/assets/screenshots/mainScreenMultiselectMode.png" alt="Main Screen Multi-select Mode" width="300"/>
</div>

<div align="center">
    <img src="/assets/screenshots/mainScreenTextDialog.png" alt="Main Screen Text Dialog" width="300"/>
</div>

<div align="center">
    <img src="/assets/screenshots/mainScreenReminderWithoutPermissions.png" alt="Main Screen Reminder Without Permissions" width="300"/>
</div>

<div align="center">
    <img src="/assets/screenshots/mainScreenReminder.png" alt="Main Screen Reminder" width="300"/>
</div>