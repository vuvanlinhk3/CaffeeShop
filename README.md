# ☕ Caffee Shop

**Caffee Shop** is a mobile application for Android developed using **Kotlin** and **XML**. It integrates **Firebase Realtime Database** to manage user authentication and store drink data in real-time.

## 🔧 Technologies Used

* **Kotlin** — Modern programming language for Android development
* **XML** — For designing UI layouts
* **Firebase Authentication** — For user sign-up, login, and password reset
* **Firebase Realtime Database** — For storing and syncing data such as drinks and cart items in real time

## 📱 App Features

### 🔐 Authentication

* **Sign Up**: Create a new account with email and password
* **Sign In**: Log in to your existing account
* **Forgot Password**: Reset your password via email

### 🏠 Home

* Welcome page after successful login
* Navigation to drinks list and user profile

### 🥤 Drinks List

* View a list of available drinks with images, names, and prices
* Add drinks to cart

### 👤 User Profile

* View user information (email, name, etc.)
* Optionally edit or update profile

### 🛒 Cart

* View all drinks added to the shopping cart
* Quantity, price, and remove option for each item

## 🔗 Firebase Setup

1. Connect the project to Firebase via **Firebase Assistant** in Android Studio
2. Enable **Authentication** with Email/Password
3. Set up **Realtime Database** with proper rules
4. Add `google-services.json` to your `app/` folder

## 🚀 Getting Started

1. Clone this repository
2. Open the project in Android Studio
3. Connect Firebase and sync dependencies
4. Build and run on your Android device/emulator