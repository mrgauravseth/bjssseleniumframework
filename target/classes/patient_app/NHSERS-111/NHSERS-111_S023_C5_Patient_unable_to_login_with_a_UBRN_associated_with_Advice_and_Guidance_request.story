Story: NHSERS-111 Patient Login - Authentication
	
Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria C5 
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S023_C5 | Patient unable to login with a UBRN associated with Advice and Guidance request

GivenStories: patient_app/LoadBrowser.story
!-- Given a Patient attempts logs in with valid login details and <UBRN> associated with Advice and Guidance request
Given the current page has title of "<pageTitle>" on the Login page
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
|Login|e-Referral Service|1110 0000 0004|1974|Password04|


