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

Scenario:	NHSERS-111_S035_Note1 | patient UBRN field will accept alphanumerics with javascript disabled	
Meta:
@pending
	
Given a Patient User has <UBRN>	
And javascript is disabled
When they enter special, numberic and alphabetic characters in UBRN field
Then any characters will be displayed	

Examples:
|pageName|pageTitle|
|Login|e-Referral Service|