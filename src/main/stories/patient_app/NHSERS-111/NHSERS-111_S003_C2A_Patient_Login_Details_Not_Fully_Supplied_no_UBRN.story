Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2A		
@comment CV: blocked until I ask tash how to handle this

Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 
	
Scenario:	NHSERS-111_S003_C2A | Patient Login Details Not Fully Supplied - no UBRN
Meta:
@pending

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "Choose and Book" on the Login page
And a Patient User doesnt supply a UBRN	
!-- And supplies a valid <Year of Birth>	
When I enter "<yearofbirth>" in the yobField textbox on the Login page
!-- And supplies valid <Password>
And I enter "<password>" in the passwordField textbox on the Login page
!-- When they submit their login details on the login page	
And I click the login button on the Login page	
!-- Then the  Patient User is unable to login	
And the Login Page will be shown
Then the text "Please enter reference number, year of birth and password to continue" should be displayed on the LoginPage
!-- And nothing is recorded in the Activity Log
!-- And failed login attempts does not increase


Examples:
|pageName|pageTitle|yearofbirth|password |
|Login|e-Referral Service|1971|Password01|
|Login|e-Referral Service|1975|Password05|
|Login|e-Referral Service|1999|password|