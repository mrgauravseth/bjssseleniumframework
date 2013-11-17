lStory: NHSE-RS-112 Patient Logout - Patient Initiated  

Meta:
@author Tom Dickinson
@Sprint 1
@testphase Core 
@developedFromJIRA 112 20/08/13 4.35PM
@lastReviewed 29/08/13 9.16AM
@AcceptanceCriteria C1
@Jira XXXX

@comments CV: test has been disabled since, application no longer has view all referrals screen for single referral

Narrative:	
In order to finish my interactions with the system
As a Patient User 
I want to log out of e-RS 

Scenario: NHSERS-112_S002_C1 | Patient confirms Log Out	while updating a referral
Meta:
@pending

!--Pre-requisites
!--NHSERS-112_S001_C1 | Patient Chooses to Log Out while updating referral

!-- Given Story: NHSERS-112.Story
GivenStories: patient_app/NHSERS-112/NHSERS-112_S001_C1_Patient_Chooses_to_Log_Out_while_updating_referral.story


Given a patient user has selected to logout while updating a referral
!-- When they confirm they want to continue without saving the changes to the referral
When I click the Yes button button on the View All referrals page
Then the patient is navigated to the login page
!-- ### Following step does not work!!!
And the text "You have logged out of Choose and Book" should be displayed on the Login page

Examples:
|Logout				|CurrentPage  		|pageName	|pageTitle|
|Top right		    |View All referrals	|Login		|Choose and Book|
|Bottom left		|View All referrals	|Login		|Choose and Book|
