Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C6
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S025_C6 | Patient unable to login with a UBRN associated with cancelled request

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "<pageTitle>" on the Login page
!-- Given a Patient attempts to login with valid login details and <UBRN> associated with cancelled request
When I enter "<ubrn>" in the ubrn textboxes on the Login page
And I enter "<yearofbirth>" in the yobField textbox on the Login page
And I enter "<password>" in the passwordField textbox on the Login page
!-- When they submit their login details on the login page	
And I click the login button on the Login page
!-- Then the  Patient User is unable to login
Then the Login Page will be shown
!-- And error message "You cannot make an appointment using this reference number. Please contact your GP practise for more information" displayed
And the text "You cannot make an appointment using this reference number. Please contact your GP practice for more information." should be displayed on the Login page	


Examples:
|pageName|pageTitle|ubrn|yearofbirth|password|
|Login|e-Referral Service|1110 0000 0005|1971|Password05|
	