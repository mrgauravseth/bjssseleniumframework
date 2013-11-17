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

Scenario:	NHSERS-111_S031_Note3 | 3rd UBRN field will not autotab to when patient enters 4 digits with javascript enabled
Meta:
@pending

!--Pre-requisites
!-- NHSERS-111_S030_Note3_2nd_UBRN_field_will_autotab_to_when_patient_enters_4_digits_with_javascript_enabled
GivenStories: patient_app/NHSERS-111/NHSERS-111_S030_Note3_2nd_UBRN_field_will_autotab_to_when_patient_enters_4_digits_with_javascript_enabled.story
!-- Given a Patient User has entered the first 4 digits of <UBRN> 
!-- And now has javascript enabled
Given the cursor will autotab to the ubrnField3 field on the Login page
!-- When they enter the third 4 digits of their UBRN
When I enter "0000" in the ubrnField3 textbox on the Login page		
!-- Then the cursor will not autotab to the year of birth field		
Then the cursor will autotab to the yobField field on the Login page

Examples:
|pageName|pageTitle|
|Login|e-Referral Service|