# **Tourism Guide Desktop Application**

This is a Java Swing-based desktop application for managing and browsing tourist destinations. It features a dual-role system (Admin and User) with distinct functionalities, a clean, modern UI, and a local SQLite database for data persistence.

## **Application Preview**

## **Features**

* **User Authentication:** Secure Sign Up and Sign In system for users and admins.  
* **Guest Mode:** Allows browsing destinations without an account.  
* **Admin Role:**  
  * **Add** new tourist destinations with details, prices, and images.  
  * **Edit** existing destination information.  
  * **Delete** destinations from the database.  
* **User Role:**  
  * Browse and search all available destinations.  
  * **Save** favorite destinations to a personal "My Saved Places" list.  
  * **Write Reviews:** Submit ratings (1-5 stars) and comments for destinations.  
* **Dynamic UI:**  
  * Sort destinations by popularity or price.  
  * Search for destinations by name or location.  
  * View detailed destination pages with image galleries, tabbed info (Overview, Tourist Spots, etc.), and user reviews.  
  * Change application theme (Light Mode / Dark Mode).

## **Design and Implementation**

This project was built using core Java principles and a clean, modular structure.

* **Use of OOP Concepts:** The application is built on OOP principles. Data is encapsulated in model classes (Destination, User, Review), and behavior is managed by service (UserService) and manager (DatabaseManager) classes.  
* **GUI Design (Java Swing):** The user interface is built entirely with Java Swing, using a CardLayout to manage different panels (views). Custom components like CardFactory and WrapLayout are used to create a dynamic and responsive UI.  
* **Event Handling:** All user interactions are handled through Swing's event listeners (ActionListener, MouseAdapter, etc.) to trigger database operations and panel navigation.  
* **Use of Packages:** The project is organized into distinct packages (gui, database, model, user, util) to separate concerns (e.g., UI logic from database logic), making the code easier to maintain and understand.  
* **Database:** A local SQLite database is used for all data persistence, managed by the DatabaseManager class which centralizes all SQL queries.  
* **Error Handling:** User input is validated (e.g., checking for empty fields, number formats) and database errors are handled with try-catch blocks and user-friendly JOptionPane dialogs.

## **Tech Stack**

* **Language:** Java (JDK 11+)  
* **UI Framework:** Java Swing  
* **Look and Feel:** [FlatLaf](https://www.formdev.com/flatlaf/) (for modern light and dark themes)  
* **Database:** SQLite (using sqlite-jdbc driver)  
* **Password Hashing:** [jBCrypt](https://github.com/patrickfav/bcrypt)

## **Project Structure**

Here is the high-level structure of the project's source code:

TourismGuideByGemini/  
├── images/                \# Stores Admin-uploaded destination images  
├── lib/                   \# Contains external .jar libraries  
│   ├── flatlaf-x.x.jar  
│   ├── jbcrypt-x.x.jar  
│   └── sqlite-jdbc-x.x.x.jar  
├── src/  
│   └── com/  
│       └── tourism/  
│           ├── database/  
│           │   └── DatabaseManager.java     \# Handles all SQLite database operations  
│           ├── gui/  
│           │   ├── components/            \# Reusable UI components  
│           │   │   ├── CardFactory.java  
│           │   │   └── WrapLayout.java  
│           │   ├── AddPlacePanel.java  
│           │   ├── DashboardPanel.java  
│           │   ├── DestinationDetailPanel.java  
│           │   ├── MainFrame.java         \# The main JFrame holding all panels  
│           │   ├── SavedPlacesPanel.java  
│           │   ├── SettingsPanel.java  
│           │   ├── SignInPanel.java  
│           │   ├── SignUpPanel.java  
│           │   └── WelcomePanel.java  
│           ├── main/  
│           │   └── Main.java                \# Main entry point of the application  
│           ├── model/  
│           │   ├── Destination.java  
│           │   ├── Review.java  
│           │   └── User.java  
│           ├── resources/  
│           │   ├── assets/                \# Icons, fonts, and background images  
│           │   │   ├── back-arrow.png  
│           │   │   ├── placeholder.jpg  
│           │   │   ├── star.png  
│           │   │   ├── tick-mark.png  
│           │   │   ├── wallpaper-bg.png  
│           │   │   └── welcome-bg.png  
│           │   └── fonts/  
│           │       └── SAMAN\_\_\_.TTF  
│           ├── user/  
│           │   └── UserService.java         \# Manages the current user's session  
│           └── util/  
│               └── PasswordHashing.java   \# Utility for hashing and checking passwords  
├── .gitignore  
├── README.md              \# This file  
├── tourism.db             \# The SQLite database file (created on first run)  
└── TourismGuide.iml       \# IntelliJ project file

## **Setup & Running Guide**

1. **Prerequisites:**  
   * Java JDK (version 11 or higher).  
   * A Java IDE (e.g., IntelliJ IDEA or Eclipse).  
2. **Clone the Repository:**  
   git clone \[\[https://github.com/SaberKrest/GuideMyTour\](https://github.com/SaberKrest/GuideMyTour)\]

3. **Configure IDE (IntelliJ IDEA Example):**  
   * Open the cloned project folder in IntelliJ.  
   * **Set JDK:** Go to File \> Project Structure... \> Project and select your JDK (11 or newer).  
   * **Add Libraries:**  
     1. Go to File \> Project Structure... \> Modules \> Dependencies.  
     2. Click the \+ icon and select JARs or directories....  
     3. Navigate to the /lib folder in the project and select all the .jar files (flatlaf, jbcrypt, sqlite-jdbc).  
     4. Click Apply and OK.  
   * **Mark Resources Folder:**  
     1. In the Project explorer, right-click the src/com/tourism/resources folder.  
     2. Select Mark Directory as \> Resources Root. This is crucial for the app to find fonts and icons.  
4. **Run the Application:**  
   * Navigate to src/com/tourism/main/Main.java.  
   * Right-click the file and select Run 'Main.main()'.  
   * The application will start, and the tourism.db file will be created in the root directory.

<<<<<<< HEAD
### **Admin Access**
=======
git clone [https://github.com/SaberKrest/Tourism-Guide]
>>>>>>> cd5b178 (added screenshots of the program.)

To sign up as an admin, use the following special admin code on the Sign Up page:  
Admin Code: ADMIN1S23
