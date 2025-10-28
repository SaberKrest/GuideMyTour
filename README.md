Tourism Guide Desktop Application

This is a Java Swing-based desktop application for managing and browsing tourist destinations. It features a dual-role system (Admin and User) with distinct functionalities, a clean, modern UI, and a local SQLite database for data persistence.

Features

User Authentication: Secure Sign Up and Sign In system for users and admins.

Guest Mode: Allows browsing destinations without an account.

Admin Role:

Add new tourist destinations with details, prices, and images.

Edit existing destination information.

Delete destinations from the database.

User Role:

Browse and search all available destinations.

Save favorite destinations to a personal "My Saved Places" list.

Write Reviews: Submit ratings (1-5 stars) and comments for destinations.

Dynamic UI:

Sort destinations by popularity or price.

Search for destinations by name or location.

View detailed destination pages with image galleries, tabbed info (Overview, Tourist Spots, etc.), and user reviews.

Change application theme (Light Mode / Dark Mode).

Tech Stack

Language: Java (JDK 11+)

UI Framework: Java Swing

Look and Feel: FlatLaf (for modern light and dark themes)

Database: SQLite (using sqlite-jdbc driver)

Password Hashing: jBCrypt

Project Structure

Here is the high-level structure of the project's source code:

TourismGuideByGemini/
├── images/                # Stores user-uploaded destination images
├── lib/                   # Contains external .jar libraries
│   ├── flatlaf-x.x.jar
│   ├── jbcrypt-x.x.jar
│   └── sqlite-jdbc-x.x.x.jar
├── src/
│   └── com/
│       └── tourism/
│           ├── database/
│           │   └── DatabaseManager.java     # Handles all SQLite database operations
│           ├── gui/
│           │   ├── components/            # Reusable UI components
│           │   │   ├── CardFactory.java
│           │   │   └── WrapLayout.java
│           │   ├── AddPlacePanel.java
│           │   ├── DashboardPanel.java
│           │   ├── DestinationDetailPanel.java
│           │   ├── MainFrame.java         # The main JFrame holding all panels
│           │   ├── SavedPlacesPanel.java
│           │   ├── SettingsPanel.java
│           │   ├── SignInPanel.java
│           │   ├── SignUpPanel.java
│           │   └── WelcomePanel.java
│           ├── main/
│           │   └── Main.java                # Main entry point of the application
│           ├── model/
│           │   ├── Destination.java
│           │   ├── Review.java
│           │   └── User.java
│           ├── resources/
│           │   ├── assets/                # Icons, fonts, and background images
│           │   │   ├── back-arrow.png
│           │   │   ├── placeholder.jpg
│           │   │   ├── star.png
│           │   │   ├── tick-mark.png
│           │   │   ├── wallpaper-bg.png
│           │   │   └── welcome-bg.png
│           │   └── fonts/
│           │       └── SAMAN___.TTF
│           ├── user/
│           │   └── UserService.java         # Manages the current user's session
│           └── util/
│               └── PasswordHashing.java   # Utility for hashing and checking passwords
├── .gitignore
├── README.md              # This file
├── tourism.db             # The SQLite database file (created on first run)
└── TourismGuide.iml       # IntelliJ project file


Setup & Running

Clone the Repository:

git clone [your-repository-url]


Configure IDE:

Open the project in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).

Ensure you have a Java JDK (version 11 or higher) configured.

Add Libraries:

Go to your project's build path settings (in IntelliJ: File > Project Structure > Modules > Dependencies).

Add all the .jar files from the /lib folder as dependencies. This project requires:

flatlaf-x.x.jar (for the UI theme)

sqlite-jdbc-x.x.x.jar (for the database)

jbcrypt-x.x.jar (for password hashing)

Run the Application:

Find the Main.java file in src/com/tourism/main/.

Run the main method to start the application.

Admin Access

To sign up as an admin, use the following special admin code on the Sign Up page:
Admin Code: ADMIN1S23