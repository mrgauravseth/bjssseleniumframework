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

Scenario:	NHSERS-111_S030_Note3 | 2nd UBRN field will autotab to when patient enters 4 digits with javascript enabled
	
!--Pre-requisites
!--NHSERS-111_S028_Note3_UBRN_field_will_autotab_when_patient_enters_4_digits_with_javascript_enabled

GivenStories: patient_app/NHSERS-111/NHSERS-111_S028_Note3_UBRN_field_will_autotab_when_patient_enters_4_digits_with_javascript_enabled.story
!-- Given a Patient User has <UBRN>
!-- And has javascript enabled	
!-- When they enter the second 4 digits of their UBRN
Given the cursor will autotab to the ubrnField2 field on the Login page
When I enter "0000" in the ubrnField2 textbox on the Login page	
!-- Then the cursor will autotab to the 3rd UBRN field
Then the cursor will autotab to the ubrnField3 field on the Login page

Examples:
|pageName|pageTitle|
|Login|e-Referral Service|