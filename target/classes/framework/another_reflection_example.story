Story: Reflection example

Meta: 
@author Chi Voong
@example another reflection example
@skip


Scenario: example of additional reflection steps

GivenStories: framework/database_connection_example.story

When I navigate to manchester university page
Then the text "<message>" should be displayed on the manchester university page

Examples:

|pageName|pageTitle|ubrn|failedLoginCount|message|
|Login|e-Referral Service|111000000001|3|Open days|
|Login|e-Referral Service|111000000002|3|Open days|
