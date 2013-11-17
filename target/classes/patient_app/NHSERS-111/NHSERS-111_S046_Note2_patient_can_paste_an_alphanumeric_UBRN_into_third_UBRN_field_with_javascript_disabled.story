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

Scenario:	NHSERS-111_S046_Note2 | patient can paste an alphanumeric UBRN into third UBRN field with javascript disabled	
Meta:
@pending
	
!--Pre-requisites
!--NHSERS-111_S029_Note3_UBRN field will not autotab when patient enters 4 digits with javascript disabled

GivenStories NHSERS-111.Story
Given a Patient User has entered the first 4 digits of <UBRN> 
And now has javascript disabled 	
When they enter the second 4 digits of their UBRN	
Then the cursor will not autotab to the next field

	
Examples: 
|pageName|pageTitle|UBRN	|	
|Login|e-Referral Service|`1'g 	|	
|Login|e-Referral Service|+-98  	|	
|Login|e-Referral Service|x93R	|	
		