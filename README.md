
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

**Journals**
  - The user can manually add journal entries. This feature is still under production because the user cannot edit or remove previous journals.

**Contacts Page**

  - The user can view their contacts, and select any amount to their emergency contacts. This feature is still under production. Eventually functionality for removing contacts from the Emergency contact list will implemented.

**Questionnaire**
  - The user is prompted with two questionnaires after logging in. This requests user preferences for certain app functions. This feature utilizes the Contacts Page to collect Emergency contacts list.



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

**AddJournal.java**
- Class AddJournal
  - Method
    - onCreate: handles creating the UI for the AddJournal activity.
    - saveInformation: reterieves information in each text box and pushes the information to Firebase.
	- updateInformation: reterieves infromation in each text box and updates that journal in Firebase.
	- popJournalText: gets single journal information and populates the EditText boxes.
	- updateFieldInFirebase: updates journal information in Firebase.
	
**Journal.java**
- Class Journal
  - Method
    - Journal: constructor of the Journal class which creates the Journal object for writing to Firebase.
	- toString: converts object to its string representation.
	
**Datatable.java**
-Class Datatable
  - Method
    - onCreate: handles generating the UI and retrieves Journal dateAndTime from Firebase and displays them in a ListView.
	- removeJournal: gets the ID for the selected journal and removes it from Firebase.
	- editJournal: gets the journal's dateAndTime and a boolean set to true and passes to AddJournal activity.
	
**ContactsPage.java**
- Class ContactsPage
     - Method
        - onCreate: generates UI and checks to see if it has permission to access contacts.
        - checkPermission: checks if the app has permission to access the devices contacts, and requests access if it does not.
        - getContactList: grabs contacts from user device.

**ContactLayout.java**
- Class ContactLayout
   - Method
      - Contact: generates a contact UI which acts as a template for ContactsPage.
      -

**QuestionnairePersonal.java**
  - Class QuestionnairePersonal
     - Method
        - onCreate: generates UI and assigns all UI and firebase implements to variables.
        - storeQuestionnaireData: Checks the validity of the inputs, and saves it to a Questionnaire object.

**QuestionnaireMedical.java**
  - Class QuestionnaireMedical
     - Method
        - onCreate: generates UI and assigns all UI and firebase implements to variables.
        - onClick: adds all remaining user input data to Questionnaire object and sends it to firebase.

**Questionnaire.java**
  - Class Questionnaire
     - Method
        - Questionnaire: Creates an Object containing the data given by the questionnaire.
        - toString: Currently for debugging purposes. returns the data in String form.






## Contributing
Please open a Pull Request with documented features.