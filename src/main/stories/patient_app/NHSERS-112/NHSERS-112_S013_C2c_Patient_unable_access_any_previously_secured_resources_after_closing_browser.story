Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C2c

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S013_C2c | Patient unable access any previously secured resources after closing browser	
Meta:
@pending

!-- GivenStories: NHSERS-112.Story	

Given a patient has closed browser
And have reopened a new browser	
When they try to navigate to a previously accessed secured URL	
Then the patient is directed to the login screen	
And the patient is not navigated back into an active session	


	


