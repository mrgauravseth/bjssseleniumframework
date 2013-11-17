Story: Professional App Happy Path

Meta: 
@author Scott Redden
@example Professional App Happy Path
@prof_app


Scenario: Professional App Happy Path

Meta:
@skip

GivenStories: patient_app/LoadBrowser.story

Given I am on the professional application page
When I focus on the new window with page title of "e-Referral Service" on the professional application page
And I select "Local System Administration" on the role dropdown on the professional application page
And I click the send button on the professional application page
Then the text "You are currently logged in with role: Local System Administration" should be displayed on the professional application page

Examples:
|pageName|pageTitle|
|professional application|Authentication|