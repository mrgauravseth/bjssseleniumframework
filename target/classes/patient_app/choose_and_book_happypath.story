Story: choose and book login

Meta: 
@author Scott Redden
@example choose and book login
@web_app
@comments CV: no longer needed
@skip

Scenario: example of additional reflection steps
Meta:


GivenStories: patient_app/LoadBrowser.story

Given I am on the choose and book login page
When I enter "1110" in the reference number box one textbox on the choose and book login page
And I enter "0000" in the reference number box two textbox on the choose and book login page
And I enter "0001" in the reference number box three textbox on the choose and book login page
And I enter "1971" in the year of birth textbox on the choose and book login page
And I enter "Password01" in the password textbox on the choose and book login page
And I click the log in button on the choose and book login page
Then the text "Referral Details" should be displayed on the choose and book login page

Examples:

|pageName|pageTitle|
|Login|e-Referral Service|