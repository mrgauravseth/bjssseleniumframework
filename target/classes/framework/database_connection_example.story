Story: Database example

Meta: 
@author Chi Voong
@example database example
@skip
@run false

Scenario: example of database check steps

GivenStories: patient_app/LoadBrowser.story

Then the UBRN <ubrn> is logged
Then the failed login count for ubrn <ubrn> is <failedLoginCount>

Examples:
|pageName|pageTitle|ubrn|failedLoginCount|
|Login|e-Referral Service|419000000001|2|
