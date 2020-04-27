const fs = require('fs');

var args = process.argv.slice(2);

var file = fs.readFileSync(args[0], "utf-8");

var lines = file.split("\n");


for (var i = 0; i < lines.length; i++) {
    var month = Math.ceil(Math.random() * 4);
    var day = Math.ceil(Math.random() * 27);
    var hour = Math.floor(Math.random() * 24);
    var minutes = Math.floor(Math.random() * 60);
    var seconds = Math.floor(Math.random() * 60);
    var partSeconds = Math.floor(Math.random() * 99);

    if (hour < 10) {
        hour = "0" + hour.toString();
    } else {
        hour = hour.toString();
    }
    if (minutes < 10) {
        minutes = "0" + minutes.toString();
    } else {
        minutes = minutes.toString();
    }
    if (seconds < 10) {
        seconds = "0" + seconds.toString();
    } else {
        seconds = seconds.toString();
    }

    var toWrite = lines[i].replace('sysdate', 'TO_TIMESTAMP(\'2020-' + month + '-' + day + ' ' + hour + ':' + minutes + ':' + seconds + '.' + partSeconds + '\', \'YYYY-MM-DD HH24:MI:SS.FF\')')
    toWrite = toWrite.replace(';', ';\n');
    toWrite = toWrite.replace('transaction', args[2]);

    // atm_withdraw
    // toWrite = toWrite.replace('trans_id,amount,timestamp', 'trans_id,amount,time,customer_id,branch_id,account_number');

    // teller_withdraw/teller_deposit
    // toWrite = toWrite.replace('trans_id,amount,timestamp', 'trans_id,amount,time,customer_id,branch_id,account_number');

    // purchase
    // toWrite = toWrite.replace('trans_id,amount,timestamp', 'trans_id,amount,time,customer_id,account_number,vendor');


    fs.writeFile(
        // './atm_withdrawData_1.txt',
        args[1],
        toWrite,
        {
            flag: "a"
        },
        function(){}
    );
}