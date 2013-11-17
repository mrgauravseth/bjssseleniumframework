Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C2
@pending

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S011_C2 | Patient can logout of active session by closing their browser
Meta:
@pending

!--Pre-requisites
!--NHSERS-111_S001_C1 | Patient Logs in with valid Details	

!-- GivenStories: patient_app/NHSERS-112/NHSERS-112Given.story
	
Given a patient user is logged in NHSERS-112_S011
When they choose to close their browser on the Referral Details page
Then their session will be ended
Examples:
|pageName|pageTitle|
|Login|Choose and Book|