// Used the Alexa NodeJS SDK
// Used some template code from the Alexa NodeJS SDK Samples
// CS 160

'use strict';
const Alexa = require('alexa-sdk');
const APP_ID = 'amzn1.ask.skill.c47f2777-1b30-436f-a55a-7a56d2895dc7';
var rp = require('request-promise');
var request = require('request');

var household = 'Area 51';
var groupBudgets;
var groupMembers;
var groupMembersDict;

var states = {
    MAINMENU: '_MAINMENU', 
    TRACKPAYMENT: '_TRACKPAYMENT', 
    BUDGETADDING: '_BUDGETADDING',
    BUDGETSUMMARY: '_BUDGETSUMMARY'
};

var paymentStates = {
    item: "_item",
    amount: "_amount",
    name: "_name",
    type: "_type",
    budget: "_budget"
};

var budgetStates = {
    type: "_type",
    name: "_name",
    amount: "_amount"
};

exports.handler = (event, context, callback) => {
    rp('https://budget-buddy-2.firebaseio.com/Group.json')
    .then(function (body) {
        groupMembersDict = JSON.parse(body)[household].groupMembers;
        groupBudgets = createGroupBudgetsArray(JSON.parse(body)[household].groupBudgets);
        groupMembers = createGroupMembersArray(JSON.parse(body)[household].groupMembers);
    })
    .catch(function (err) {
        console.log(err);
    })
    .finally(function (body) {
        const alexa = Alexa.handler(event, context);
        alexa.APP_ID = APP_ID;
        alexa.registerHandlers(newSessionHandlers, mainMenuHandlers, trackPaymentHandlers, budgetAddingHandlers, budgetSummaryHandlers);
        alexa.execute();
    });
};

var newSessionHandlers = {
    'NewSession': function () {
        this.handler.state = states.MAINMENU;
        // if the user has asked to go to main menu (used for YES and NO Intents)
        this.attributes['requestedMainMenu'] = false;
        this.emit(':ask', strings.WELCOME_TEXT);
    },

    'SessionEndedRequest': function () {
        this.emit(':tell', strings.GOODBYE);
    },

    'Unhandled': function() {
        this.emit('NewSession');
    },
};

var mainMenuHandlers = Alexa.CreateStateHandler(states.MAINMENU, {
    'TrackPaymentIntent': function() {
        this.handler.state = states.TRACKPAYMENT;
        this.emitWithState('TrackPaymentIntent');
    },

    'BudgetAddingIntent': function() {
        this.handler.state = states.BUDGETADDING;
        this.emitWithState('BudgetAddingIntent');
    },

    'BudgetSummaryIntent': function() {
        this.handler.state = states.BUDGETSUMMARY;
        this.emitWithState('BudgetSummaryIntent');
    },

    'SessionEndedRequest': function () {
        this.emit(':tell', strings.GOODBYE);
    },

    'WhatCanISayIntent': function() {
        this.emit(':ask', strings.MAIN_MENU_WHAT_CAN_I_SAY);
    },

    'Unhandled': function() {
        this.emitWithState('WhatCanISayIntent');
    }
});

var trackPaymentHandlers = Alexa.CreateStateHandler(states.TRACKPAYMENT, {
    'TrackPaymentIntent': function() {
        this.attributes['trackingPaymentState'] = 'person';
        this.attributes['payment'] = {};
        this.emit(':ask', 'Who purchased this?');
    },

    'PaymentInformationIntent': function() {
        switch(this.attributes['trackingPaymentState']) {
            case 'person':
                var personSlot = this.event.request.intent.slots.person.value;
                this.attributes['payment'].person = personSlot;
                if (personSlot == null) {
                    console.log('person slot is null');
                    this.emitWithState("WhatCanISayIntent")
                }
                this.attributes['trackingPaymentState'] = 'amount';
                this.emit(':ask', 'How much was the purchase?');
                break;
            case 'amount':
                var amountSlot = this.event.request.intent.slots.amount.value;
                this.attributes['payment'].amount = parseInt(amountSlot);
                if (amountSlot == null) {
                    console.log('amount slot is null');
                    this.emitWithState("WhatCanISayIntent")
                }
                this.attributes['trackingPaymentState'] = 'item';
                this.emit(':ask', 'What was the purchase for?');
                break;
            case 'item':
                var itemSlot = this.event.request.intent.slots.item.value;
                if (itemSlot == null) {
                    console.log('item slot is null');
                    this.emitWithState("WhatCanISayIntent")
                    break;
                }
                this.attributes['payment'].item = itemSlot;
                this.attributes['trackingPaymentState'] = 'type';
                this.emit(':ask', 'Was it for a personal or a group budget?');
                break;
            case 'type':
                var typeSlot = this.event.request.intent.slots.type.value;
                if (typeSlot == null) {
                    console.log('type slot is null');
                    this.emitWithState("WhatCanISayIntent")
                    break;
                }
                this.attributes['payment'].type = typeSlot;
                this.attributes['trackingPaymentState'] = 'budget';
                this.emit(':ask', 'What is the name of the budget?');
                break;
            case 'budget':
                var budgetSlot = this.event.request.intent.slots.budget.value;
                this.attributes['payment'].budget = budgetSlot;
                if (budgetSlot == null) {
                    console.log('budget slot is null');
                    this.emitWithState("WhatCanISayIntent")
                    break;
                }
                // currently only adding payments to Cleaning Supplies group budget
                var options = {
                    method: 'PUT',
                    uri: 'https://budget-buddy-2.firebaseio.com/Group/Area%2051/payments/1.json',
                    body: {
                        amountSpent: this.attributes['payment'].amount,
                        notes: this.attributes['payment'].item,
                        purchaseDate: '4/25/2017',
                        username: this.attributes['payment'].person
                    },
                    json: true // Automatically stringifies the body to JSON 
                };

                var alexaThis = this;
                rp(options)
                    .then(function (parsedBody) {
                        console.log('succesful push of payment');
                    })
                    .catch(function (err) {
                        // POST failed... 
                        // this.emit(':tell', 'Ah, that did not work correctly. Returning to the main menu now.');
                    })
                    .finally(function (body) {
                        alexaThis.handler.state = states.MAINMENU;
                        alexaThis.emit(':tell', 'Thanks for tracking a payment, you are now back at the main menu!');
                    });
        }
    },

    // Ask the user if they are sure they want to exit to main menu
    'MainMenuIntent': function () {
        this.attributes['requestedMainMenu'] = true;
        this.emit(':ask', strings.MAIN_MENU_CONFIRMATION);
    },

    'YesIntent': function () {
        if (this.attributes['requestedMainMenu'] = true) {
            this.attributes['requestedMainMenu'] = false;
            this.handler.state = states.MAINMENU;
            this.emit(':tell', 'You are now at the main menu, you say "What can I say?" to find out what to say.');
        }
        this.emitWithState('Unhandled');
    },

    'NoIntent': function() {
        this.attributes['requestedMainMenu'] = false;
    },

    'WhatCanISayIntent': function() {
        switch(this.attributes['trackingPaymentState']) {
            case 'person':
                var username = this.attributes['payment'].person;
                this.emit(':ask', 'I could not find that user "' + username +
                    '" in this group. Please provide a valid username');
                break;
            case 'amount':
                this.emit(':ask', 'Sorry, I did not get that. How much did you spend on this payment?');
            case 'item':
                this.emit(':ask', 'Sorry, I did not get that. What was the purchase you made for?');
                break;
            case 'type':
                this.emit(':ask', 'Sorry, I did not get that. Is your payment a personal or group payment?');
                break;
            case 'budget':
                var budgetName = this.attributes['payment'].budget;
                this.emit(':ask', 'The budget ' + budgetName + ' does not exist. What is the name of the budget?');
                break;
        }
    },
    
    'SessionEndedRequest': function () {
        this.emit('SessionEndedRequest');
    },

    'Unhandled': function() {
        switch(this.attributes['trackingPaymentState']) {
            case 'person':
                var personSlot = this.event.request.intent.slots.person.value;
                this.emit(':ask', 'I could not find the user ' + personSlot.toString() + ' in' +
                    'this group. Please provide a valid username');
                break;
            case 'amount':
                this.emit(':ask', 'Sorry, I did not get that. How much did you spend on this payment?');
                break;
            case 'item':
                this.emit(':ask', 'Sorry, I did not get that. What was the purchase you made for? You can say' +
                    'it was a purchase for mac and cheese');
                break;
            case 'type':
                this.emit(':ask', 'Sorry, I did not get that. Is your payment a personal or group payment?');
                break;
            case 'budget':
                var budgetSlot = this.event.request.intent.slots.budget.value;
                this.emit(':ask', 'The budget ' + budgetSlot.toString() + ' does not exist. What is the name of the budget?');
                break;
        }
    }

});

var budgetAddingHandlers = Alexa.CreateStateHandler(states.BUDGETADDING, {
    'BudgetAddingIntent': function() {
        this.attributes['budgetAddingState'] = 'type';
        this.attributes['budget'] = {};
        this.emit(':ask', 'What type of budget is this?');
    },

    'BudgetInformationIntent': function() {
        switch(this.attributes['budgetAddingState']) {
            case 'type':
                var typeSlot = this.event.request.intent.slots.type.value;
                if (typeSlot == 'group') {
                    this.attributes['budget'].type = true;
                }
                else {
                    this.attributes['budget'].type = false;
                }
                
                this.attributes['budgetAddingState'] = 'name';
                this.emit(':ask', 'What is the name of the budget?');
                break;
            case 'name':
                var nameSlot = this.event.request.intent.slots.budget.value;
                this.attributes['budget'].name = nameSlot;
                this.attributes['budgetAddingState'] = 'amount';
                this.emit(':ask', 'How much are you planning to spend per month?');
                break;
            case 'amount':
                var amountSlot = this.event.request.intent.slots.amount.value;
                this.attributes['budget'].amount = amountSlot;
                this.handler.state = states.MAINMENU;
                this.emit(':tell', 'Thanks for adding a budget, you are now back at the main menu!');
                var options = {
                    method: 'PUT',
                    uri: 'https://budget-buddy-2.firebaseio.com/Group/Area%2051/groupBudgets/Food.json',
                    body: {
                        groupBudget: this.attributes['budget'].type,
                        name: this.attributes['budget'].name,
                        amountLeftInBudget: this.attributes['budget'].amount,
                        amountSpentInBudget: 0,
                        budgetLimit: this.attributes['budget'].amount,
                    },
                    json: true // Automatically stringifies the body to JSON 
                };
                 
                var alexaThis = this;
                rp(options)
                    .then(function (parsedBody) {
                        console.log('succesful push of budget');
                    })
                    .catch(function (err) {
                        // POST failed... 
                    })
                    .finally(function (body) {
                        alexaThis.handler.state = states.MAINMENU;
                        alexaThis.emit(':tell', 'Thanks for adding a budget, you are now back at the main menu!');
                    });
            }
    },

    // Ask the user if they are sure they want to exit to main menu
    'MainMenuIntent': function () {
        this.attributes['requestedMainMenu'] = true;
        this.emit(':ask', strings.MAIN_MENU_CONFIRMATION);
    },
    
    'WhatCanISayIntent': function() {
        this.emit(':ask', strings.BUDGETADDING_WHAT_CAN_I_SAY);
    },
    
    'SessionEndedRequest': function () {
        this.emit('SessionEndedRequest'); // calls session ended on newSessionHandler
    },

    'Unhandled': function() {
        this.emitWithState('WhatCanISayIntent');
    }
        
});

var budgetSummaryHandlers = Alexa.CreateStateHandler(states.BUDGETSUMMARY, {
    'BudgetSummaryIntent': function() {
        this.emit(':tell', 'You have currently spent $100 out of $200 in your groceries budget. You are also nearing your cleaning supplies budget as well.');
    },

    // Ask the user if they are sure they want to exit to main menu
    'MainMenuIntent': function () {
        this.attributes['requestedMainMenu'] = true;
        this.emit(':ask', strings.MAIN_MENU_CONFIRMATION);
    },
    
    'WhatCanISayIntent': function() {
        this.emit(':ask', strings.BUDGETSUMMARY_WHAT_CAN_I_SAY);
    },
    
    'SessionEndedRequest': function () {
        this.emit('SessionEndedRequest'); // calls session ended on newSessionHandler
    },

    'Unhandled': function() {
        this.emitWithState('WhatCanISayIntent');
    }
        
});

//constructor for GroupBudget object
function GroupBudget(amountLeftInBudget, amountSpentInBudget, budgetLimit, groupBudget, name) {
    this.amountLeftInBudget = amountLeftInBudget;
    this.amountSpentInBudget = amountSpentInBudget;
    this.budgetLimit = budgetLimit;
    this.groupBudget = groupBudget;
    this.name = name;
}

//constructor for PersonalBudget object
function PersonalBudget(amountLeftInBudget, amountSpentInBudget, budgetLimit, groupBudget, name) {
    this.amountLeftInBudget = amountLeftInBudget;
    this.amountSpentInBudget = amountSpentInBudget;
    this.budgetLimit = budgetLimit;
    this.groupBudget = groupBudget;
    this.name = name;
}

//constructor for GroupMember object
function GroupMember(username, personalBudgets) {
    this.username = username;
    this.personalBudgets = personalBudgets;
}

//create an array of 'GroupBudget' objects from parsed JSON input
function createGroupBudgetsArray(groupBudgetsDict) {
    var groupBudgets = [];
    for (var key in groupBudgetsDict) {
        if (!groupBudgetsDict.hasOwnProperty(key)) {
            continue;
        }
        var budgetObject = groupBudgetsDict[key];
        var groupBudget = new GroupBudget(budgetObject['amountLeftInBudget'], 
                                budgetObject['amountSpentInBudget'],
                                budgetObject['budgetLimit'],
                                budgetObject['groupBudget'],
                                budgetObject['name']);
        groupBudgets.push(groupBudget);
    }
    return groupBudgets;
}

function getPersonalBudgets(groupMember) {
    return createGroupBudgetsArray(groupMember.personalBudgets);
}

function createGroupMembersArray(groupMembersDict) {
    var groupMembers = [];
    for (var key in groupMembersDict) {
        if (!groupMembersDict.hasOwnProperty(key)) {
            continue;
        }
        var memberObject = groupMembersDict[key];
        var groupMember = new GroupMember(memberObject['username'], 
                                memberObject['personalBudgets']);
        groupMembers.push(groupMember);
    }
    return groupMembers;
}

var strings = {
    // Welcome Strings
    WELCOME_TEXT: 'Budget buddy here, how can I help you budget?',

    // Main Menu Strings
    MAIN_MENU_WHAT_CAN_I_SAY: 'You can ask to track a payment, add a budget, or ask for a budget summary.',
    MAIN_MENU_CONFIRMATION: 'Are you sure you want to go back to the main menu?',
    
    // Ingredients Strings
    TRACKPAYMENT_WHAT_CAN_I_SAY: 'You can ask to track a payment or ask to go back to the main menu.',
    
    // Recipe Strings
    BUDGETADDING_WHAT_CAN_I_SAY: 'You can ask to add a budget',

    //Budget Summary Strings
    BUDGETSUMMARY_WHAT_CAN_I_SAY: 'You can ask for a general budget summary or for a specific group budget',

    // Other Strings
    GOODBYE: "Thanks for using Budget Buddy. Goodbye!",
    THANKS_RESPONSE: "No problem! "
};
