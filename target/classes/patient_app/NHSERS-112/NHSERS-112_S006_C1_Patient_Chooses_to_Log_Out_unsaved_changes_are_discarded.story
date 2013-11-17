Story: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C1
@comment CV: can't really be tested now, since you cannot make any changes in the ers app at the moment.

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S006_C1 | Patient Chooses to Log Out - unsaved changes are discarded

Meta:
@pending

!--Pre-requisites
!--NHSERS-112_S002_C1 | Patient confirms Log Out while updating a referral
!--NHSERS-111_S001_C1 | Patient Logs in with valid Details

!-- Given Story: NHSERS-112.Story, 
!-- NHSERS-111.Story
!-- GivenStories: patient_app/NHSERS-112/NHSERS-112_S002_C1_Patient_confirms_Log_Out_while_updating_a_referral.story,
patient_app/NHSERS-112/NHSERS-112Given.story
 

Given a patient user has logged out while updating a referral
And has chosen not to save changes
When they log back in
And access the page <CurrentPage>
!-- Then changes made prior to logging out have not been saved 
Then changes made prior to logging out have not been saved for the <CurrentPage>

Examples:
|Logout				|CurrentPage  		|pageName	|pageTitle|
|Top right		    |View All referrals	|Login		|Choose and Book|
|Bottom left		|View All referrals	|Login		|Choose and Book|
