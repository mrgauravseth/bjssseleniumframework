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

Scenario:	NHSERS-111_S036_Note2 | patient can paste an alphanumeric UBRN into first UBRN field with javascript enabled
Meta:
@bug 1960

GivenStories: patient_app/LoadBrowser.story
Given the current page has title of "<pageTitle>" on the Login page
!-- Given a Patient User has <UBRN>	
!-- And javascript enabled
!-- When they paste special, numeric and alphabetic characters in the first UBRN field
When I paste "<UBRN>" in the ubrnField1 textbox on the Login page
!-- Then all characters will be displayed
Then the ubrnField3 textbox should contain the text "<UBRN>" on the Login page

!-- unused table
!--|UBRN  |              
!--|1a'_  -2a#          `1'g |
!--|0d$" }7?a +-98 |              
!--|)(45 66~@ x93R              |
!--|BLA999999999999BLA|
!--|BLA9999BLA9999BLA9999BLA|
!--|9999-9999-9999|


Examples:
|pageName|pageTitle|UBRN|	
|Login|e-Referral Service|1a'_|
|Login|e-Referral Service|0d$"|	
|Login|e-Referral Service|)(45|	
