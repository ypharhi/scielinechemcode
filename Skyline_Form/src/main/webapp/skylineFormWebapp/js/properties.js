/**
 * properties.js:
 * centralize global variables.
 * Loads data form app.properties.
 * 
 * 
 * remarks:
 * 
 * 1) 'prop.precision': The precision of the system.
 * 2) 'prop.dateFormat.userDateFormatClient': usually used for comparison and validation with momentJS.
 * 3) 'prop.dateFormat.datepickerFormat': date format for input with class date-picker.
 * 4) 'prop.dateFormat.savedConventionDbDateFormat': date convention format that saved in the DB.
 * 5) 'prop.dataChanged': get dataChanged flag (detect change for all of the elements in the page).
 * 6) 'prop.operators': get operators object.
 * 7) 'prop.onChangeAjaxFlag': detect if onChangeAjax function is running.
 *  8) 'prop.springMessagesObj': object that holds spring messages.
 **/


"use strict";
var prop = {

    /**
     * dataChanged flag:  detect change for all of the elements in the page.
     */
    dataChanged: false,

    /**
     * onChangeAjaxFlag:  detect if onChangeAjax function is running.
     */
    onChangeAjaxFlag: false,

    /**
     * declare operators.
     */
    operators: {
        'eq': function (x, y) {
            return x == y;
        },
        'ne': function (x, y) {
            return x != y;
        },
        'gt': function (x, y) {
            return x > y;
        },
        'ge': function (x, y) {
            return x >= y;
        },
        'lt': function (x, y) {
            return x < y;
        },
        'le': function (x, y) {
            return x <= y;
        }
    },

    /**
     * declare springMessagesObj: object that holds spring messages.
     */
    springMessagesObj: {},

    /**
     * precision
     */
    precision: undefined,


    /**
     * dateFormat:
     * 
     * userDateFormatClient: usually used for comparison and validation with momentJS 
     * datepickerFormat: date format for input with class date-picker 
     * savedConventionDbDateFormat: date convention format that saved in the DB
     */
    dateFormat: undefined

};