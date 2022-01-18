# STRapp (Seizure Technical Response app) 

## Features

**Internal Login**
- The user is able to login with their email and password

**Internal Signup**
- The user is able to signup for the application using email, username, and password

**Google Login/Signup**
- The user can signup and login with their Google account

**Twitter Login/Signup**
- The user can signup and login with their twitter account. Note: This feature is currently under production as we're having some minor issues with the API keys.

**Forget Password**
- The user can reset their password if they have forgotten it.


## How to start?

1. Clone the repository using  ` git clone <repo_url> `
2. Open the project folder in Android Studio.
3. Build the project by clicking on the green hammer icon at the top.


## Known Bugs
- Logging in with Twitter is inconsistent as we are experiencing minor issues with the API keys.

## File Structure

**ForgetPassword.java**
- Class ForgetPassword
  - Method
    - resetPassword: handles validating the user email and sending it to Firebase for deeper authentication.

**LoginAdapter.java**
- Class LoginAdapter
  - Method
    - createFragment: handles creating the signup and login tabs based on what the user has clicked on.

**LoginPage.java**
- Class LoginPage
  - Method
    - onCreate: handles UI element animations and selecting the correct tab.
    - googleSignIn: handles sending the user to the Google sign in page
    - createGoogleRequest: handles building the Google request and making sure the user has not signed in before
    - onActivityResult: handles getting the response from the Google login API
    - handleSignInResult: handles sendind the user to the correct page after signing up    

**LoginTabFragment.java**
- Class LoginTabFragment
  - Method
    - onCreate: handles the UI animcations of the login tab
    - loginUser: handles validating the user email and password and then logging them in using Firebase authentication

**SignupTabFragment.java**
- Class SignupTabFragment
  - Method
    - onCreate: handles the UI animcations of the signup tab
    - signupUser: handles validating the email, password, username, and confirm password of the user and then signing them up using Firebase authentication.

**User.java**
- Class User
  - Method
    - User: the constructor of the class which handles creating the user object for writing to Firebase. Note: DO NOT SET THE CLASS VARIABLES TO PRIVATE. This will break firebase since it needs public variables for serialization.

**TwitterLogin.java**
- Class TwitterLogin
  - Method
    - onCreate: handles directing the user to the Twitter login page and getting the response from it.




## Contributing
Please open a Pull Request with documented features.