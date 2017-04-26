
'use strict';
const Alexa = require('alexa-sdk');
const APP_ID = undefined;
var rp = require('request-promise');

var states = {
    MAINMENU: '_MAINMENU',
};
var household = 'Area 51';
var groupBudgets;
var groupMembers;

exports.handler = (event, context, callback) => {
    rp('https://budget-buddy-2.firebaseio.com/Group.json')
    .then(function (body) {
        groupBudgets = createGroupBudgetsArray(JSON.parse(body)[household].groupBudgets);
        groupMembers = createGroupMembersArray(JSON.parse(body)[household].groupMembers);
    })
    .catch(function (err) {
        console.log(err);
    })
    .finally(function (body) {
        const alexa = Alexa.handler(event, context);
        alexa.APP_ID = APP_ID;
        // alexa.registerHandlers(newSessionHandlers, mainMenuHandlers, ingredientsRequestHandlers, recipeRequestHandlers);
        alexa.execute();
    });
};

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
