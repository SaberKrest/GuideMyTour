# 🏞️ Tourism Guide Desktop Application

A **Java Swing-based desktop application** for exploring and managing tourist destinations.  
It features **Admin and User roles**, a modern UI, and a **local SQLite database** for data persistence.

---

## 🚀 Application Overview

The **Tourism Guide** app allows users to browse, search, and save favorite destinations, while administrators can manage all destination data — including adding new locations, editing existing ones, and moderating reviews.  

It combines **object-oriented programming**, **Swing GUI design**, and **modular architecture** for scalability and maintainability.

---

## ✨ Features

### 🔐 User Authentication
- Secure **Sign Up / Sign In** system for both **users** and **admins**.  
- Optional **Guest Mode** for browsing without logging in.

### 👑 Admin Role
- **Add** new destinations with descriptions, prices, and images.  
- **Edit** or **Delete** existing destinations.  
- Manage database entries directly through the UI.

### 🙋‍♂️ User Role
- **Browse** all tourist destinations.  
- **Search** by name or location.  
- **Sort** destinations by **popularity** or **price**.  
- **Save** favorite destinations to “My Saved Places.”  
- **Write Reviews** — rate (1–5 stars) and comment on destinations.

### 🎨 Dynamic UI
- Modern interface built with **Java Swing + FlatLaf** for Light/Dark themes.  
- **Tabbed view** for each destination (Overview, Tourist Spots, Reviews, etc.).  
- **Responsive card-based layout** with smooth navigation.  
- **Theme customization** (Light / Dark mode).  

---

## 🧠 Design & Implementation   

The project is fully modular, following **OOP principles** and **MVC-like architecture**.

| Layer | Responsibility |
|-------|----------------|
| **Model** | Data entities (Destination, User, Review) |
| **Service / Manager** | Business logic and database operations |
| **GUI** | Swing-based presentation layer |
| **Utility** | Password hashing, layout helpers, and reusable components |

<<<<<<< HEAD
<<<<<<< HEAD
### **Admin Access**
=======
git clone [https://github.com/SaberKrest/Tourism-Guide]
>>>>>>> cd5b178 (added screenshots of the program.)
=======
## 🧩 Key Concepts
- **Encapsulation & Modularity:** Each functionality is isolated within its package.  
- **Event Handling:** All user actions are handled via Swing’s `ActionListener`, `MouseAdapter`, etc.  
- **Error Handling:** Input validation, exceptions, and friendly `JOptionPane` messages.  
- **Database Persistence:** Managed by `DatabaseManager` (SQLite-based).  

---

## 🛠️ Tech Stack

| Component | Technology |
|------------|-------------|
| **Language** | Java (JDK 11+) |
| **UI Framework** | Java Swing |
| **Look & Feel** | [FlatLaf](https://www.formdev.com/flatlaf/) |
| **Database** | SQLite (via `sqlite-jdbc` driver) |
| **Password Hashing** | [jBCrypt](https://github.com/patrickfav/bcrypt) |

---

## 📁 Project Structure

```
GuideMyTour/
├── images/                                  # Admin-uploaded destination images
├── lib/                                     # External .jar libraries
│ ├── flatlaf-x.x.jar
│ ├── jbcrypt-x.x.jar
│ └── sqlite-jdbc-x.x.x.jar
├── src/
│ └── com/
│ └── tourism/
│ ├── database/
│ │ └── DatabaseManager.java                 # Handles SQLite database operations
│ ├── gui/
│ │ ├── components/                          # Custom reusable UI components
│ │ │ ├── CardFactory.java
│ │ │ └── WrapLayout.java
│ │ ├── AddPlacePanel.java
│ │ ├── DashboardPanel.java
│ │ ├── DestinationDetailPanel.java
│ │ ├── MainFrame.java                       # Main JFrame managing all panels
│ │ ├── SavedPlacesPanel.java
│ │ ├── SettingsPanel.java
│ │ ├── SignInPanel.java
│ │ ├── SignUpPanel.java
│ │ └── WelcomePanel.java
│ ├── main/
│ │ └── Main.java                            # Application entry point
│ ├── model/
│ │ ├── Destination.java
│ │ ├── Review.java
│ │ └── User.java
│ ├── resources/
│ │ ├── assets/                              # Images, icons, and backgrounds
│ │ │ ├── back-arrow.png
│ │ │ ├── placeholder.jpg
│ │ │ ├── star.png
│ │ │ ├── tick-mark.png
│ │ │ ├── wallpaper-bg.png
│ │ │ └── welcome-bg.png
│ │ └── fonts/
│ │ └── SAMAN___.TTF
│ ├── user/
│ │ └── UserService.java                     # Manages user sessions
│ └── util/
│ └── PasswordHashing.java                   # Password encryption utilities
├── .gitignore
├── README.md
├── tourism.db                               # SQLite database (auto-created on first run)
└── TourismGuide.iml                         # IntelliJ project configuration
```

## ⚙️ Setup & Installation

### 1. Prerequisites
- Java **JDK 11+** installed.
- A Java IDE like **IntelliJ IDEA** or **Eclipse**.

### 2. Clone the Repository
```bash
git clone https://github.com/SaberKrest/GuideMyTour.git
```

### 3. Configure Your IDE (IntelliJ Example)
Set Up JDK

File → Project Structure → Project → SDK → select JDK 11 or newer

Add Required Libraries

Go to File → Project Structure → Modules → Dependencies

Click + → JARs or Directories

Add all .jar files from the /lib folder (flatlaf, jbcrypt, sqlite-jdbc)

Click Apply and OK

Mark Resources Folder

Right-click src/com/tourism/resources

Select Mark Directory as → Resources Root

### 4. Run the Application

Navigate to:
src/com/tourism/main/Main.java

Right-click → Run 'Main.main()'

The application will launch and automatically create tourism.db in the project root.

## 🔑 Admin Access

To create an admin account, enter the following Admin Code during sign-up:
>>>>>>> 736591a36c8bb985132c0c6a2ff78a50e9bd1af9

Admin Code: ADMIN1S23

## 🧩 Future Enhancements

Cloud database integration for online access

AI-based destination recommendations

Integration with live travel APIs (Google Maps, TripAdvisor, etc.)

Export trip plans to PDF

## 📸 Application Preview

