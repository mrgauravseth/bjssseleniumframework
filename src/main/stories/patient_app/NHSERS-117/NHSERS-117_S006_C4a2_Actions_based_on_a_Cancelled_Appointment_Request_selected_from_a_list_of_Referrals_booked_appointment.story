Story: NHSERS-117 Patient Views Referral  

Meta:
@Gaurav Seth
@sprint 1
@testphase Core Regression 
@lastReviewed
@inUse True
@acceptanceCriteria c1xxx
@Comments - This test has not been fully automated because 1) Cancelled Appointment Request is not working( bug) 2) because its not possible to Cancel an Appointment and also because it requires to pass a Update database query which we are not
keen to do while the test is in execution 


Narrative:	
In order to check my referral
As a Patient User 
I want to view my referral in e-RS 

Scenario: NHSERS-117_S006_C4a2 | Actions based on a Cancelled Appointment Request selected from a list of Referrals booked appointment
Meta:
@pending
!-- Pre-requisites
!-- NHSERS-111_S001 PatientLoginSuccess
!-- NHSERS-114_S002 Patient should be able to progress a Booked, Directly Bookable Telephone Assessment referral from a list
GivenStories: patient_app/NHSERS-111/NHSERS-111_S001_C00_Happy_Path_Login.story
!-- Given that the Patient is logged in to eRS
Given I am on the Login page
When I click the view all Referrals button on the Referral Details page
!-- And the chosen Appointment Request has been Cancelled since the Referrals list was populated
!-- And has selected an Appointment from the referral list with <UBRN> with <Status> <Service Type> <Appointment Type> <Appointment Date/Time>
And I click the <ReferralNo> radio option on the View Referral page
!-- When the Patient selects the Continue button
And I click the continue Referral button on the View Referral page
!-- Then the Appointment Request cannot be seen
!-- And an appropriate Error Message is displayed
!-- And the patient can choose to manage a different UBRN
!-- Then I am on the View Referral page
!------Choose a different UBRN NO---
And I click the view Referrals button on the Referral Details page
!-- And the chosen Appointment Request has been Cancelled since the Referrals list was populated
!-- And has selected an Appointment from the referral list with <UBRN> with <Status> <Service Type> <Appointment Type> <Appointment Date/Time>
And I click the <AnotherReferral> radio option on the View Referral page
!-- When the Patient selects the Continue button
And I click the continue Referral button on the View Referral page
!-- Then the Appointment Request cannot be seen
!-- And an appropriate Error Message is displayed
!-- And the patient can choose to manage a different UBRN
Then I am on the View Referral page
Given I am on the View Referral page
!-- And or Patient can Log Out
When I click the logoutApp link on the View Referral page
And I click the logoutYes button on the View Referral page
Then I am on the Login page

Examples:
|pageName	|pageTitle		|ubrn 			| yob	|password	|SessionID		|Activity	|Date		|Time  		|Reason Code   |ReferralNo		|AnotherReferral|	
|Login		|Choose and Book|1130 0000 0011	| 1971 	|Password01	|SessionID x	|LOGIN 		|Date x		|Time x		|Reason Code x |bookedReferral 	|unbookedneverBooked|
