import * as functions from 'firebase-functions';


// Activates when writing to occurrences folder
exports.writeToSheets = functions.database.ref('/occurrences/{id}')
    .onWrite((change: functions.Change<functions.database.DataSnapshot>, context: functions.EventContext) => {
        console.log('Occurrence written. ID: ' + context.params.id);
        console.log('change');
        console.log(change);
        console.log('context');
        console.log(context);
        return new Promise(resolve => resolve(change));
    });