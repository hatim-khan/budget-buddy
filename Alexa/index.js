// Used the Alexa NodeJS SDK
// Used some template code from the Alexa NodeJS SDK Samples
// CS 160

'use strict';
const Alexa = require('alexa-sdk');
const APP_ID = undefined;

var payments;
var budgets;
var users;

var states = {
    MAINMENU: '_MAINMENU', 
    TRACKPAYMENT: '_TRACKPAYMENT',
    BUDGETADDING: '_BUDGETADDING',
    BUDGETSUMMARY: '_BUDGETSUMMARY'
};

exports.handler = (event, context, callback) => {
    const alexa = Alexa.handler(event, context);
    alexa.APP_ID = APP_ID;
    alexa.registerHandlers(newSessionHandlers, mainMenuHandlers, trackPaymentHandlers, budgetAddingHandlers, budgetSummaryHandlers);
    alexa.execute();
};

var newSessionHandlers = {
    'NewSession': function () {
        this.handler.state = states.MAINMENU;
        this.attributes['payments'] = payments;
        this.attributes['budgets'] = budgets;
        this.attributes['users'] = users;

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
    
    // i.e. "I want to track a payment"
    'TrackPaymentIntent': function() {
        var amountSlot = this.event.request.intent.slots.Amount;
        var itemSlot = this.event.request.intent.slots.Item;
        var budgetSlot = this.event.request.intent.slots.Budget;
        if (amountSlot && amountSlot.value) this.attributes['amount'] = amountSlot.value;
        if (itemSlot && itemSlot.value) this.attributes['item'] = itemSlot.value;
        if (budgetSlot && budgetSlot.value) this.attributes['budget'] = budgetSlot.value;
        
        this.handler.state = states.TRACKPAYMENT;
        this.emitWithState('TrackPaymentIntent');
    },

    // i.e. "I want to add a budget"
    'BudgetAddingIntent': function() {
        
    },

    // i.e. "I want a budget summary"
    'BudgetSummaryIntent': function() {
        
    },

    'SessionEndedRequest': function () {
        this.emit(':tell', strings.GOODBYE);
    },

    // i.e. "read recipe"
    'RecipeInstructionsIntent': function() {
        if (this.attributes['foundRecipe']) {
            this.handler.state = states.RECIPE;
            // Temporary emit
            this.attributes['recipeProgress'] = 0;
            this.emit(':ask', `Great, I will start reading the recipe. First, ` + getCurrentRecipeStep(this.attributes, false));
        }
        else {
            this.emitWithState('WhatCanISayIntent');
        }
    },

    // If the user wants says exit or stop in the main menu, end the session
    'AMAZON.StopIntent': function() {
        this.emit(':tell', strings.GOODBYE);  
    },

    'WhatCanISayIntent': function() {
        if (this.attributes['foundRecipe']) {
            this.emit(':ask', 'You can say, "ingredients", to hear a list of ingredients for ' + this.attributes['chosenRecipe'].name + ' or "recipe", to hear the recipe instructions');
        }
        else {
            this.emit(':ask', strings.MAIN_MENU_WHAT_CAN_I_SAY);
        }
    },

    'Unhandled': function() {
        this.emitWithState('WhatCanISayIntent');
    }
});

var trackPaymentHandlers = Alexa.CreateStateHandler(states.TRACKPAYMENT, {
    'TrackPaymentIntent': function() {

    }

    'PaymentAmountIntent': function() {
        
    }

    'WhatCanISayIntent': function() {
        this.emit(':ask', strings.TRACK_PAYMENT_WHAT_CAN_I_SAY;
    },
    
    'SessionEndedRequest': function () {
        this.emit('SessionEndedRequest');
    },

    'Unhandled': function() {
        this.emitWithState('WhatCanISayIntent');
    }

})

var strings = {
    // Welcome Strings
    WELCOME_TEXT: 'Budget buddy here, how can I help you budget?',

    // Main Menu Strings
    MAIN_MENU_WHAT_CAN_I_SAY: 'You can track a payment, add a budget, or ask for a budget summary',
    MAIN_MENU_CONFIRMATION: 'Are you sure you want to go back to the main menu?',
    
    // Track Payment Strings
    TRACK_PAYMENT_WHAT_CAN_I_SAY: 'You can say, I want to track a payment.',
    INGREDIENTS_FIRST_STEP: 'Great, I will list off the ingredients one by one. The first ingredient is ',
    INGREDIENTS_CONFIRMATION: '. Say, "next", to hear the next ingredient.',
    // Recipe Strings
    RECIPE_WHAT_CAN_I_SAY: 'You can say, next, to hear the next recipe step, or, main menu, to go back to the main menu.',

    // Other Strings
    GOODBYE: "Thanks for using budget buddy. Goodbye!",
    THANKS_RESPONSE: "No problem! "
};
