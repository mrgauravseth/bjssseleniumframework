Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria Note 3
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:	NHSERS-111_S028_Note3 | UBRN field will autotab when patient enters 4 digits with javascript enabled

GivenStories: patient_app/LoadBrowser.story	
Given the current page has title of "<pageTitle>" on the Login page
!-- Given a Patient User has <UBRN>
!-- And has javascript enabled
When I enter "0000" in the ubrnField1 textbox on the Login page	
!-- Then the culrsor will autotab to the next field
Then the cursor will autotab to the ubrnField2 field on the Login page

Examples:
|pageName|pageTitle|
|Login|e-Referral Service|
