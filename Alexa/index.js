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
    }
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
                this.attributes['payment'].person = toTitleCase(personSlot);
                this.attributes['trackingPaymentState'] = 'amount';
                this.emit(':ask', 'How much was the purchase?');
                break;
            case 'amount':
                var amountSlot = this.event.request.intent.slots.amount.value;
                this.attributes['payment'].amount = parseInt(amountSlot);
                this.attributes['trackingPaymentState'] = 'item';
                this.emit(':ask', 'What was the purchase?');
                break;
            case 'item':
                var itemSlot = this.event.request.intent.slots.item.value;
                this.attributes['payment'].item = itemSlot;
                this.attributes['trackingPaymentState'] = 'type';
                this.emit(':ask', 'Was it for a personal or a group budget?');
                break;
            case 'type':
                var typeSlot = this.event.request.intent.slots.type.value;
                this.attributes['payment'].type = typeSlot.toLowerCase();
                this.attributes['trackingPaymentState'] = 'budget';
                this.emit(':ask', 'What is the name of the budget?');
                break;
            case 'budget':
                var budgetSlot = this.event.request.intent.slots.budget.value;
                this.attributes['payment'].budget = toTitleCase(budgetSlot);

                var groupPayment = (this.attributes['payment'].type == 'group');
                var amountRemainingInBudget = calculateRemainingInBudget(this.attributes['payment'].budget, this.attributes['payment'].type,
                    this.attributes['payment'].person, this.attributes['payment'].amount);
                var amountSpentInBudget = calculateSpentInBudget(this.attributes['payment'].budget, this.attributes['payment'].type,
                    this.attributes['payment'].person, this.attributes['payment'].amount);

                // currently only adding payments to Cleaning Supplies group budget
                var options = {
                    method: 'PUT',
                    uri: generateAddingPaymentURI.call(this),
                    body: {
                        amountSpent: this.attributes['payment'].amount,
                        notes: this.attributes['payment'].item,
                        purchaseDate: '05/10/2017',
                        username: this.attributes['payment'].person,
                        groupPayment: groupPayment
                    },
                    json: true // Automatically stringifies the body to JSON 
                };

                var alexaThis = this;
                rp(options)
                    .then(function (parsedBody) {
                        console.log('succesful push of payment');

                        // currently only adding payments to Cleaning Supplies group budget
                        var options = {
                            method: 'PATCH',
                            uri: generateBudgetUpdatingURI(alexaThis),
                            body: {
                                amountLeftInBudget: amountRemainingInBudget,
                                amountSpentInBudget: amountSpentInBudget
                            },
                            json: true // Automatically stringifies the body to JSON
                        };

                        rp(options)
                            .then(function (parsedBody) {
                                console.log('patched budget');
                            })

                            .catch(function (err) {
                                console.log('something went wrong');
                                alexaThis.handler.state = states.MAINMENU;
                                alexaThis.emit(':tell', 'Sorry, something went wrong with updating the amount. Please try again.');
                            })
                            .finally(function (body) {
                            alexaThis.handler.state = states.MAINMENU;
                            alexaThis.emit(':tell', 'Thanks for tracking a payment, you are now back at the main menu!');
                        })
                    })

                    //TODO: better error handling?
                    .catch(function (err) {
                        // POST failed...
                        alexaThis.handler.state = states.MAINMENU;
                        alexaThis.emit(':tell', 'Sorry, something went wrong with adding the payment. Please try again.');
                    })
                    .finally(function (body) {
                        console.log('finished with PATCH to budget and PUT to payments');
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

    //TODO: fully implement this
    'NoIntent': function() {
        this.attributes['requestedMainMenu'] = false;
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

            //TODO: Add name questioning for personal budgets
            case 'type':
                var typeSlot = this.event.request.intent.slots.type.value;
                if (typeSlot == 'group') {
                    this.attributes['budget'].type = 'group';
                }
                else {
                    this.attributes['budget'].type = 'personal';
                    this.attributes['budgetAddingState'] = 'person';
                    this.emit(':ask', 'What is your name?');
                    break;
                }
                this.attributes['budgetAddingState'] = 'name';
                this.emit(':ask', 'What is the name of the budget?');
                break;
            case 'person':
                var personSlot = this.event.request.intent.slots.person.value;
                this.attributes['budget'].person = toTitleCase(personSlot);
                this.attributes['budgetAddingState'] = 'name';
                this.emit(':ask', 'What is the name of the budget?');
                break;
            case 'name':
                var nameSlot = this.event.request.intent.slots.budget.value;
                this.attributes['budget'].name = toTitleCase(nameSlot);
                this.attributes['budgetAddingState'] = 'amount';
                this.emit(':ask', 'How much are you planning to spend per month?');
                break;
            case 'amount':
                var amountSlot = this.event.request.intent.slots.amount.value;
                this.attributes['budget'].amount = amountSlot;

                var alexaThis = this;
                var name = capitalizeFirstLetter(this.attributes['budget'].name);

                var uriString;
                console.log(alexaThis.attributes['budget'].type);
                if (alexaThis.attributes['budget'].type == 'personal') {
                    uriString = 'https://budget-buddy-2.firebaseio.com/Group/Area 51/' + 'groupMembers' + '/' + alexaThis.attributes['budget'].person + '/personalBudgets/'
                        + alexaThis.attributes['budget'].name + '.json';
                }
                else if (alexaThis.attributes['budget'].type == 'group') {
                    uriString = 'https://budget-buddy-2.firebaseio.com/Group/Area 51/' + 'groupBudgets' + '/'
                        + name + '.json';
                }

                var options = {
                    method: 'PUT',
                    uri: uriString,
                    body: {
                        groupBudget: (this.attributes['budget'].type == 'group'),
                        name: this.attributes['budget'].name,
                        amountLeftInBudget: this.attributes['budget'].amount,
                        amountSpentInBudget: 0,
                        budgetLimit: this.attributes['budget'].amount
                    },
                    json: true // Automatically stringifies the body to JSON 
                };

                console.log(options);
                rp(options)
                    .then(function (parsedBody) {
                        console.log('successful push of budget');
                        console.log(parsedBody);
                    })
                    .catch(function (err) {
                        // POST failed... 
                    })
                    .finally(function (body) {
                        console.log('ending push of budget');
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

//constructor for Budget object
function Budget(amountLeftInBudget, amountSpentInBudget, budgetLimit, groupBudget, name, payments) {
    this.amountLeftInBudget = amountLeftInBudget;
    this.amountSpentInBudget = amountSpentInBudget;
    this.budgetLimit = budgetLimit;
    this.groupBudget = groupBudget;
    this.budgetName = name;
    this.payments = payments;
}

//constructor for GroupMember object
function GroupMember(username, personalBudgets) {
    this.username = username;
    this.personalBudgets = personalBudgets;
}

function generateBudgetUpdatingURI(alexaThis) {
    var uriString;
    if (alexaThis.attributes['payment'].type == 'personal') {
        uriString = 'https://budget-buddy-2.firebaseio.com/Group/Area 51/' + 'groupMembers' + '/' + alexaThis.attributes['payment'].person + '/personalBudgets/'
            + alexaThis.attributes['payment'].budget + '.json';
    }
    else if (alexaThis.attributes['payment'].type == 'group') {
        uriString = 'https://budget-buddy-2.firebaseio.com/Group/Area 51/' + 'groupBudgets' + '/'
            + alexaThis.attributes['payment'].budget + '.json';
    }
    return uriString;
}

function generateBudgetAddingURI(alexaThis) {
    var uriString;
    if (alexaThis.attributes['budget'].type == 'personal') {
        uriString = 'https://budget-buddy-2.firebaseio.com/Group/Area 51/' + 'groupMembers' + '/' + alexaThis.attributes['budget'].person + '/personalBudgets/'
            + alexaThis.attributes['budget'].budget + '.json';
    }
    else if (alexaThis.attributes['budget'].type == 'group') {
        uriString = 'https://budget-buddy-2.firebaseio.com/Group/Area 51/' + 'groupBudgets' + '/'
            + alexaThis.attributes['budget'].budget + '.json';
    }
    return uriString;
}

function generateAddingPaymentURI() {
    var paymentIndex = calculatePaymentIndex(this.attributes['payment'].budget, this.attributes['payment'].type, this.attributes['payment'].person);
    var uriString;
    if (this.attributes['payment'].type == 'personal') {
        uriString = 'https://budget-buddy-2.firebaseio.com/Group/Area 51/' + 'groupMembers' + '/' + this.attributes['payment'].person + '/personalBudgets/'
            + this.attributes['payment'].budget + '/payments/' + paymentIndex + '.json';
    }
    else if (this.attributes['payment'].type == 'group') {
        uriString = 'https://budget-buddy-2.firebaseio.com/Group/Area 51/' + 'groupBudgets' + '/'
            + this.attributes['payment'].budget + '/payments/' + paymentIndex + '.json';
    }
    return uriString;
}

//create an array of 'Budget' objects from parsed JSON input
function createGroupBudgetsArray(groupBudgetsDict) {
    var groupBudgets = [];
    for (var key in groupBudgetsDict) {
        if (!groupBudgetsDict.hasOwnProperty(key)) {
            continue;
        }
        var budgetObject = groupBudgetsDict[key];
        if (budgetObject['payments'] == undefined) {
            budgetObject['payments'] = {};
        }
        var budget = new Budget(budgetObject['amountLeftInBudget'],
                                budgetObject['amountSpentInBudget'],
                                budgetObject['budgetLimit'],
                                budgetObject['groupBudget'],
                                budgetObject['name'],
                                budgetObject['payments']);
        groupBudgets.push(budget);
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

function findMember(name) {
    for (var i = 0; i < groupMembers.length; i++) {
        if (groupMembers[i].username == name) return groupMembers[i];
    }
}

function findBudget(budgets, name) {
    for (var i = 0; i < budgets.length; i++) {
        if (budgets[i].budgetName == name) return budgets[i];
    }
}

function getPayments(budget) {
    return budget.payments;
}

function calculatePaymentIndex(budgetName, type, memberName) {
    var payments;
    var paymentIndex;
    var budget;
    if (type == 'personal') {
        var member = findMember(memberName);
        var budgets = getPersonalBudgets(member);
        budget = findBudget(budgets, budgetName);
        payments = getPayments(budget);
        paymentIndex = Object.keys(payments).length;
    }
    else if (type == 'group') {
        budget = findBudget(groupBudgets, budgetName);
        payments = getPayments(budget);
        paymentIndex = Object.keys(payments).length;
    }
    return paymentIndex;
}

function calculateRemainingInBudget(budgetName, type, memberName, amount) {
    var budget;
    if (type == 'personal') {
        var member = findMember(memberName);
        var budgets = getPersonalBudgets(member);
        budget = findBudget(budgets, budgetName);
        return budget.amountLeftInBudget - amount;
    }
    else if (type == 'group') {
        budget = findBudget(groupBudgets, budgetName);
        return budget.amountLeftInBudget - amount;
    }
}

function calculateSpentInBudget(budgetName, type, memberName, amount) {
    var budget;
    if (type == 'personal') {
        var member = findMember(memberName);
        var budgets = getPersonalBudgets(member);
        budget = findBudget(budgets, budgetName);
        return budget.amountSpentInBudget + amount;
    }
    else if (type == 'group') {
        budget = findBudget(groupBudgets, budgetName);
        return budget.amountSpentInBudget + amount;
    }
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function toTitleCase(str)
{
    return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
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
