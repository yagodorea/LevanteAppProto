import * as functions from 'firebase-functions';
const googlesheets = require("google-sheets-manager");
// substituir pelo JSON (não vou colocar no Github aqui)
const creds = require('./client_secret.json');

const ServiceAccount = googlesheets.ServiceAccount;
const GoogleSheet = googlesheets.GoogleSheet;

const GOOGLE_SPREADSHEETID = '1QyrGQ6emZj_8MH7XHaZgE9FnDpGUzTGephq1XB1sEeA'; // update placeholder value
const GOOGLE_SHEETID = 0; // update placeholder value

const authClass = new ServiceAccount(creds);
const sheetAPI = new GoogleSheet(authClass, GOOGLE_SPREADSHEETID, GOOGLE_SHEETID);

// Activates when writing to occurrences folder
exports.writeToSheets = functions.database.ref('/occurrences/{id}')
    .onWrite((change: any, context: any) => {
        if (!change.after.exists()) {
            return null;
        }
        console.log('Occurrence written. ID: ' + context.params.id);

        const defaultCallback = (err: any, res: any) => {
            if (err) {
                throw err;
            }
            console.log(res)
            return res;
        };

        console.log(change.after.val())

        return sheetAPI.getData((err: any, res: any) => {
            if (err) {
                throw err;
            }
            let obj = change.after.val();
            sheetAPI.setData([[context.params.id, obj.contexto, obj.timestamp, obj.lat, obj.lon, obj.forca, obj.imgRef,
            obj.violenciaAbusiva, obj.impedimentoGravacao, obj.agenteSemIdentificacao, obj.usoIndevidoArmaDeFogo, obj.abusoAutoridade, obj.humilhacaoTortura, obj.subornoExtorsao]], {
                    range: {
                        startRow: res.length + 1,
                        startCol: 1,
                        endRow: res.length + 1,
                        endCol: 14,
                    },
                    majorDimension: "ROWS",
                }, defaultCallback);
        });
    });

