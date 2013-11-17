Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C2A
@comment CV: blocked until I ask tash how to handle this

Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S002_C2A | Patient Login Details Not Fully Supplied - no Year of Birth
Meta:
@pending

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "<pageTitle>" on the Login page
!-- Given a Patient User supplies a valid <UBRN>
When I enter "<ubrn>" in the ubrn textboxes on the Login page
!-- And no Year of birth
And I enter "" in the yobField textbox on the Login page
!-- And valid <Password>
And I enter "<password>" in the passwordField textbox on the Login page
!-- When they submit their login details on the login page	
And I click the login button on the Login page	
!-- Then the  Patient User is unable to login
Then the Login Page will be shown
!-- And "Please enter reference number, year of birth and password to continue" error message will be shown	
And the text "Please enter reference number, year of birth and password to continue" should be displayed on the LoginPage
!-- And nothing is recorded in the Activity Log	
And failed login attempts does not increase
And the failed login count for ubrn "<ubrn>" is 0
	
Examples:
|pageName|pageTitle|ubrn | password	|	
|Login|e-Referral Service|1110 0000 0001| Password01|
