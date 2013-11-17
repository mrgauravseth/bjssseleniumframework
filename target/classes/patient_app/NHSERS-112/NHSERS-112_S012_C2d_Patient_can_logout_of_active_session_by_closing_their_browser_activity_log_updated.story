Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C2

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S012_C2d | Patient can logout of active session by closing their browser - activity log updated
Meta:
@pending


!--Pre-requisites
!--NHSERS-112_S011_C2 | Patient can logout of active session by closing their browser

!-- GivenStories: NHSERS-112.Story

Given a Patient has closed their browser
When the patient User is no longer authenticated by e-RS
Then the activity Log will be updated with details regarding the Log Out operation
And <SessionID> is logged
And Activity <Activity> is logged 
And Date <Date> is logged
And Time <Time> is logged
!-- Needs clarification from Dev/BAs as to what will be logged
!-- Format of Activity Log is YYYY-MM-DD HH:mm:ss,SSS [ACTIVITY] SessionID = "XXXXXXXXXX" UBRN = "XXXXXXXXXXXXX" Reason Code = "xxxxxxxxxxxx"
!-- <SessionID> = XXXXXXXXXXX
!-- <Activity> is LOGOUT
!-- <Date> format is YYYY-MM-DD
!-- <Time> format is HH:mm:ss,SSS

Examples:
|SessionID		|Activity	|Date		|Time  	|			
|SessionID x	|LOGOUT 	|Date x		|Time x	|	
	
