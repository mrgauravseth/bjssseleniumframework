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

Scenario:	NHSERS-111_S032_Note1 | patient UBRN field will only accept numerics with javascript enabled 
Meta:
@pending

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "Choose and Book" on the Login page
!-- Given a Patient User has <UBRN>	
!-- And has javascript enabled
!-- When they enter alphabetic and special characters in UBRN field
When I enter "ABCD EFGH IJKL" in the ubrn textboxes on the Login page
!-- Then they will not be displayed
Then UBRN fields will be empty

Examples:
|pageName|pageTitle|
|Login|e-Referral Service|