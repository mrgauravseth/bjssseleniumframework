Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C1f

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S010_C1f  | Patient Chooses to Log Out - activity log updated
Meta:
@pending

!-- skipped as per original requirements
!--Pre-requisites
!--NHSERS-112_S002_C1 | Patient confirms Log Out while updating a referral

!--GivenStories: NHSERS-112.Story

Given the patient User has clicked the Log Out button 
When the patient User is directed back to the login page
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
|SessionID	|Activity	|Date		|Time  	|	pageName|pageTitle|		
|SessionID x	|LOGOUT 	|Date x		|Time x	|Login|Choose and Book|	
