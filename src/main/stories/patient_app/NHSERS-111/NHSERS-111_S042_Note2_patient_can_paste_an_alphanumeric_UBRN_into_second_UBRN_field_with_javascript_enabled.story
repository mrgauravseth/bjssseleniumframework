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

Scenario:	NHSERS-111_S042_Note2 | patient can paste an alphanumeric UBRN into second UBRN field with javascript enabled	
Meta:
@bug 1960
	
GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "Choose and Book" on the Login page
!-- Given a Patient User has <UBRN>	
!-- And javascript enabled
!-- When they paste special, numeric and alphabetic characters in the second UBRN field
When I paste <UBRN> in the ubrnField2 textbox on the Login page
!-- Then all characters will be displayed	
Then the ubrnField2 textbox should contain the text "<UBRN>" on the Login page


Examples:
|pageName|pageTitle|UBRN|	
|Login|e-Referral Service|-2a#	|	
|Login|e-Referral Service|}7?a 	|	
|Login|e-Referral Service|66~@   |