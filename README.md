
<p align="center">


  <img src="https://github.com/TheSpideX/TimePad/assets/171694578/23f0d4e4-93de-49aa-9d7e-b8d86a03ce84" width="100" />
</p>
<p align="center">
    <h1 align="center">TimePad</h1>
</p>
<p align="center">
    *A Task Management App with Repeating Tasks*
</p>
<p align="center">
    <img src="https://img.shields.io/github/license/TheSpideX/TimePad?style=flat&color=0080ff" alt="license">
    <img src="https://img.shields.io/github/last-commit/TheSpideX/TimePad?style=flat&logo=git&logoColor=white&color=0080ff" alt="last-commit">
    <img src="https://img.shields.io/github/languages/top/TheSpideX/TimePad?style=flat&color=0080ff" alt="repo-top-language">
    <img src="https://img.shields.io/github/languages/count/TheSpideX/TimePad?style=flat&color=0080ff" alt="repo-language-count">
</p>

<p align="center">
    <em>Built with Kotlin and Jetpack Compose</em>
</p>
<p align="center">
    <img src="https://img.shields.io/badge/Kotlin-7F52FF.svg?style=flat&logo=Kotlin&logoColor=white" alt="Kotlin">
    <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4.svg?style=flat&logo=Jetpack%20Compose&logoColor=white" alt="Jetpack Compose">
    <img src="https://img.shields.io/badge/Android-3DDC84.svg?style=flat&logo=Android&logoColor=white" alt="Android">
    <img src="https://img.shields.io/badge/Room-000000.svg?style=flat&logoColor=white" alt="Room">
</p>

---

## Overview

TimePad is a productivity app designed to help users manage their daily tasks efficiently. It offers a user-friendly interface with features for creating, scheduling, tracking, and completing tasks, with a special focus on handling repeating tasks. 

---

## Video

<p align="center">

https://github.com/TheSpideX/TimePad/assets/171694578/9665ec85-0969-4df3-a703-f718473cecc3

</p>
---





## Features

- **Repeating Tasks:**  Easily schedule tasks to repeat on a daily, weekly, monthly, or yearly basis.
- **Task Calendar:**  Visualize and manage your tasks on a convenient calendar view.
- **Timer:** Track the time spent on your tasks with an integrated timer.
- **Notifications and Reminders:** Receive notifications for upcoming tasks and reminders for scheduled times.
- **Task History:**  Review your past tasks and track your productivity trends.
- **Customization:**  Personalize your tasks with tags, icons, and notes.
- **Lazy Loading of Task Instances:**  Optimized for performance by generating task instances for repeating tasks only when needed, reducing database overhead.
- **Automatic Instance Pruning:**  Cleans up the database by automatically deleting completed task instances older than a month.

---

## Repository Structure

The repository is organized into the following main directories:

- `app`: Contains the main application code.
- `app/src/main/java/com/spidex/timepad`:
    - `MainActivity.kt`: The main activity for the app.
    - `AppNav.kt`: Handles navigation between screens.
    - `SoundHelper.kt`: Provides sound playback for task completion.
- `app/src/main/java/com/spidex/timepad/viewModel`:
    - `TaskViewModel.kt`:  Manages the app's data and logic.
- `app/src/main/java/com/spidex/timepad/dataChange`:
    - `AddInfoDialog.kt`, `EditInfoDialog.kt`, `DeleteDialog.kt`: Composables for adding, editing, and deleting tasks.
- `app/src/main/java/com/spidex/timepad/otherScreen`:
    - `AllTaskScreen.kt`, `TimeScreen.kt`, `CircularIndicator.kt`: Composables for other screens in the app.
- `app/src/main/java/com/spidex/timepad/mainScreen`:
    - `DashboardScreen.kt`, `SplashScreen.kt`, `HomeScreen.kt`, `TaskScreen.kt`: Main composables for the app's screens.
- `app/src/main/java/com/spidex/timepad/ui/theme`: Theme-related files.

---
## Download Link

- **MediaFire :** https://www.mediafire.com/file/j5jcp2epy0uoiqq/TimePad.apk/file
- **PlayStore :** Will be Available Soon as it is under Review

## Additional Notes

- The app uses `Task` and `TaskInstance` entities to store task information in a Room database.
- Task instances are generated on-demand for repeating tasks to optimize performance.
- The UI is built using Jetpack Compose, following a clean architecture pattern.

---
# License
```xml
Copyright 2024 Kumar Satyam . All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
---
