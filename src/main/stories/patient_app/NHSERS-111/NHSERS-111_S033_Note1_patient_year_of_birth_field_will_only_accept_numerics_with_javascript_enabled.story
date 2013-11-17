Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria Note 1
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S033_Note1 | patient year of birth field will only accept numerics with javascript enabled 	
Meta:
@pending

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "Choose and Book" on the Login page
!-- Given a Patient User has <UBRN>
!-- And has javascript enabled
!-- When they enter alphabetic, numerical and special characters in year of birth field
When I enter "XYZ123XYZ" in the yobField textbox on the Login page
!-- Then only numerics will be displayed
Then the yobField textbox should contain the text "123" on the Login page

Examples:
|pageName|pageTitle|
|Login|e-Referral Service|