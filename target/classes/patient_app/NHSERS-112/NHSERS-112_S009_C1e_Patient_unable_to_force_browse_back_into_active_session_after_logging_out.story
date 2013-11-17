Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C1e

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S009_C1e | Patient unable to force browse back into active session after logging out
Meta:
@pending

!--Pre-requisites
!--NHSERS-112_S002_C1 | Patient confirms Log Out while updating a referral	

Given Scenario NHSE-RS-112.Story

!-- added this given
Given a patient user session url
Given a patient user has selected logout
And are on the login page displaying the logged out confirmation page
When they try to access a URL within Choose and Book that is only visible to logged in user ERS	
Then they will not be able to access any previously secured page	
