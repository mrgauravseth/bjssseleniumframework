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

Scenario:	NHSERS-111_S029_Note3 | UBRN field will not autotab when patient enters 4 digits with javascript disabled
Meta:
@pending

Given a Patient User has <UBRN>	
And javascript is disabled
When they enter the first 4 digits of their UBRN	
Then the cursor will not autotab to the 2nd UBRN field

Examples:
|pageName|pageTitle|
|Login|e-Referral Service|