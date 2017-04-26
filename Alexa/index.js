// Used the Alexa NodeJS SDK
// Used some template code from the Alexa NodeJS SDK Samples
// CS 160

'use strict';
const Alexa = require('alexa-sdk');
const APP_ID = undefined;
var rp = require('request-promise');

var household = 'Area 51';
var groupBudgets;
var groupMembers;
var groupMembersDict;
var states = {
    MAINMENU: '_MAINMENU', 
    TRACKPAYMENT: '_TRACKPAYMENT', 
    BUDGETADDING: '_BUDGETADDING',
    BUDGETSUMMARY: '_BUDGETSUMMARY',
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
                this.attributes['trackingPaymentState'] = 'amount';
                this.emit(':ask', 'How much was the purchase?');
                break;
            case 'amount':
                var amountSlot = this.event.request.intent.slots.amount.value;
                this.attributes['payment'].amount = amountSlot;
                this.attributes['trackingPaymentState'] = 'item';
                this.emit(':ask', 'What was the purchase?');
            case 'item':
                var itemSlot = this.event.request.intent.slots.item.value;
                this.attributes['payment'].item = itemSlot;
                this.attributes['trackingPaymentState'] = 'type';
                this.emit(':ask', 'Was it for a personal or group budget?');
                break;
            case 'type':
                var typeSlot = this.event.request.intent.slots.type.value;
                this.attributes['payment'].type = typeSlot;
                this.attributes['trackingPaymentState'] = 'budget';
                this.emit(':ask', 'What is the name of the budget?');
                break;
            case 'budget':
                var budgetSlot = this.event.request.intent.slots.budget.value;
                this.attributes['payment'].budget = budgetSlot;
                this.emit(':tell', 'Thanks for tracking a payment, you are now back at the main menu!');

                // // currently only adding payments to Cleaning Supplies group budget
                // var options = {
                //     method: 'POST',
                //     uri: 'https://budget-buddy-2.firebaseio.com/Group/Area%2051/groupBudgets/Cleaning%20Supplies/payments.json',
                //     body: {
                //         purchaseDate: '4/25/2017',
                //         amountSpent: this.attributes['payment'].amount,
                //         username: this.attributes['payment'].person,
                //         notes: this.attributes['payment'].item,
                //     },
                //     json: true // Automatically stringifies the body to JSON 
                // };
                 
                // rp(options)
                //     .then(function (parsedBody) {
                //         console.log('succesful push of payment')
                //     })
                //     .catch(function (err) {
                //         // POST failed... 
                //         this.emit(':tell', 'Ah, that did not work correctly. Returning to the main menu now.');
                //     })
                //     .finally(function (body) {
                //         this.handler.states = states.MAINMENU;
                //         this.emit(':tell', 'Thanks for tracking a payment, you are now back at the main menu!');
                //     });
        }
    },

    // Ask the user if they are sure they want to exit to main menu
    'MainMenuIntent': function () {
        this.attributes['requestedMainMenu'] = true;
        this.emit(':ask', strings.MAIN_MENU_CONFIRMATION);
    },

    'WhatCanISayIntent': function() {
        this.emit(':ask', strings.TRACKPAYMENT_WHAT_CAN_I_SAY);
    },
    
    'SessionEndedRequest': function () {
        this.emit('SessionEndedRequest');
    },

    'Unhandled': function() {
        this.emitWithState('WhatCanISayIntent');
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
                var options = {
                    method: 'POST',
                    uri: 'https://budget-buddy-2.firebaseio.com/Group/Area%2051/groupBudgets.json',
                    body: {
                        groupBudget: this.attributes['budget'].type,
                        name: this.attributes['budget'].name,
                        amountLeftInBudget: this.attributes['budget'].amount,
                        amountSpentInBudget: 0,
                        budgetLimit: this.attributes['budget'].amount,
                    },
                    json: true // Automatically stringifies the body to JSON 
                };
                 
                rp(options)
                    .then(function (parsedBody) {
                        console.log('succesful push of budget')
                    })
                    .catch(function (err) {
                        // POST failed... 
                    })
                    .finally(function (body) {
                        this.handler.states = states.MAINMENU;
                        this.emit(':tell', 'Thanks for adding a budget, you are now back at the main menu!');
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
