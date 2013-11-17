Story: NHSERS-111 Patient Login - Authentication

Meta:
@author Tom Dickinson
@sprint 1
@testphase Core 
@developedFromJIRA 111 10/09/13 4.35PM
@lastReviewed 
@acceptanceCriteria Note 2
		
Narrative:	
In order to manage my Appointment Request(s)	
As a Patient User 
I want to log into the e-RS 

Scenario:NHSERS-111_S044_Note2 | patient can paste an alphanumeric UBRN into first UBRN field with javascript disabled               
Meta:
@pending

Given a Patient User has <UBRN>
And javascript disabled
When they paste special, numeric and alphabetic characters in the first UBRN field
Then all characters will be displayed

Examples:
|pageName|pageTitle|UBRN  |              
|Login|e-Referral Service|1a'_     |
|Login|e-Referral Service|0d$"    |              
|Login|e-Referral Service|)(45      |              
