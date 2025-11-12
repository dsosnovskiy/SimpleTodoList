# ✍️ Simple Todo List App

**A simple, reactive, and modern task management application for Android.**

This project demonstrates the use of modern Google libraries and adheres to Clean Architecture principles (MVVM with Kotlin Flow) for building a scalable and highly testable mobile solution.

## ✨ Core Features

* **Reactive UI:** Fully developed using **Jetpack Compose** for a smooth, modern, and adaptive user interface.
* **Flexible Task Management:** Standard CRUD operations (Create, Read, Update, Delete), including toggling completion status and assignment status (`isAssigned`).
* **Robust Reminders:** Ability to schedule precise time-based notifications using the **Android AlarmManager**.
* **Permission Handling:** The application correctly handles necessary runtime permissions, including `POST_NOTIFICATIONS` (for Android 13+) and `SCHEDULE_EXACT_ALARM`.
* **Drag-and-Drop Sorting:** Convenient manual sorting of uncompleted tasks for easy prioritization.
* **Multi-Select Mode:** Functionality to select multiple tasks simultaneously for bulk deletion.

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

![Welcome Screen](/assets/screenshots/welcomeScreen.png)

![PermissonPrompt Screen](/assets/screenshots/permissionPromptScreen.png)

![Main Screen Todos](/assets/screenshots/mainScreenTodos.png)

![Main Screen Multi-select Mode](/assets/screenshots/mainScreenMultiselectMode.png)

![Main Screen Text Dialog](/assets/screenshots/mainScreenTextDialog.png)

![Main Screen Reminder Without Permissions](/assets/screenshots/mainScreenReminderWithoutPermissions.png)

![Main Screen Reminder Without Permissions](/assets/screenshots/mainScreenReminderWithoutPermissions.png)

![Main Screen Reminder Without Permissions](/assets/screenshots/mainScreenReminder.png)