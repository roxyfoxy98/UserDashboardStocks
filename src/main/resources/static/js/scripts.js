(function ($) {
    "use strict";


    /*==================================================================
    [ Validate ]*/
    var input = $('.validate-input .input100');

    $('.validate-form').on('submit',function(){
        var check = true;

        for(var i=0; i<input.length; i++) {
            if(validate(input[i]) == false){
                showValidate(input[i]);
                check=false;
            }
        }

        return check;
    });


    $('.validate-form .input100').each(function(){
        $(this).focus(function(){
            hideValidate(this);
        });
    });

    function validate (input) {
        if($(input).attr('type') == 'email' || $(input).attr('name') == 'email') {
            if($(input).val().trim().match(/^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\]?)$/) == null) {
                return false;
            }
        }
        else {
            if($(input).val().trim() == ''){
                return false;
            }
        }
    }

    function showValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).addClass('alert-validate');
    }

    function hideValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).removeClass('alert-validate');
    }



})(jQuery);


function MakePosNeg() {
    let temp = document.getElementById("negativeorpositive");
let image2= document.getElementById("arrow");



        if (temp.textContent.indexOf('-') == 0) {temp.className = "negative";
                                               image2.src="/img/600px-Red_Arrow_Down.png"}
        else {temp.className = "positive";}

}
onload = MakePosNeg()



function MakePosNeg2() {
    let temp = document.getElementById("profitrelated");



    if (temp.textContent.indexOf('-') == 0) {temp.className = "negative";}
    else {temp.className = "positive";}

}
onload = MakePosNeg2()


var holidays = [
    '2016-01-01', '2016-01-18', '2016-02-15', '2016-03-25', '2016-05-30', '2016-07-04', '2016-09-05', '2016-11-24', '2016-12-26',
    '2017-01-02', '2017-01-16', '2017-02-20', '2017-04-14', '2017-05-29', '2017-07-04', '2017-09-04', '2017-11-23', '2017-12-25',
    '2018-01-01', '2018-01-15', '2018-02-19', '2018-03-30', '2018-05-28', '2018-07-04', '2018-09-03', '2018-11-22', '2018-12-25',
    '2019-01-01', '2019-01-21', '2019-02-18', '2019-04-19', '2019-05-27', '2019-07-04', '2019-09-02', '2019-11-28', '2019-12-25',
    '2020-01-01', '2020-01-20', '2020-02-17', '2020-04-10', '2020-05-25', '2020-07-03', '2020-09-07', '2020-11-26', '2020-12-25'
];

var earlyCloseDates = [
    '2018-07-03', '2018-11-23', '2018-12-24',
    '2019-07-03', '2019-11-29', '2019-12-24',
    '2020-11-27', '2020-12-24'
];

function nasdaqStatus(currentDateISOString) {


    var now = moment(currentDateISOString).tz('America/New_York');

    if (!isWorkingDay(now)) {
        var nextOpenDate = nextWorkingDay(now).hour(9).minute(30).second(0);
        var timeToWait = moment.duration(nextOpenDate.diff(now, 'seconds'), 'seconds');
        return {currentStatus: 'CLOSED', nextStatus: 'OPEN', timeUntilNextStatus: durationToString(timeToWait)};
    }

    var openDate = moment(now).hour(9).minute(30).second(0);
    if (now.isBefore(openDate)) {
        var timeToWait = moment.duration(openDate.diff(now, 'seconds'), 'seconds');
        return {currentStatus: 'CLOSED', nextStatus: 'OPEN', timeUntilNextStatus: durationToString(timeToWait)};
    }

    var closingHour = isEarlyClose(now)? 13 : 16;
    var closeDate = moment(now).hour(closingHour).minute(0).second(0);
    if (now.isAfter(closeDate)) {
        var nextOpenDate = nextWorkingDay(now).hour(9).minute(30).second(0);
        var timeToWait = moment.duration(nextOpenDate.diff(now, 'seconds'), 'seconds');
        return {currentStatus: 'CLOSED', nextStatus: 'OPEN', timeUntilNextStatus: durationToString(timeToWait)};
    }

    var timeToWait = moment.duration(closeDate.diff(now, 'seconds'), 'seconds');
    return {currentStatus: 'OPEN', nextStatus: 'CLOSED', timeUntilNextStatus: durationToString(timeToWait)};
}

function isWorkingDay(now) {
    if (isHoliday(now)) return false;
    var SUNDAY = 0;
    var SATURDAY = 6;
    return !(now.day() == SUNDAY || now.day() == SATURDAY);
}

function isHoliday(now) {
    for (var i = 0; i < holidays.length; i++) {
        var holiday = moment.tz(holidays[i], "America/New_York");
        if (now.isSame(holiday, 'day')) {
            return true;
        }
    }
    return false;
}

function isEarlyClose(now) {
    for (var i = 0; i < earlyCloseDates.length; i++) {
        var earlyCloseDate = moment.tz(earlyCloseDates[i], "America/New_York");
        if (now.isSame(earlyCloseDate, 'day')) {
            return true;
        }
    }
    return false;
}

function nextWorkingDay(now) {
    var candidate = moment(now).add(1, 'days');
    while(!isWorkingDay(candidate))
        candidate = candidate.add(1, 'days');
    return candidate;
}

function durationToString(duration) {
    return duration.days() + 'd ' + duration.hours() + 'h ' + duration.minutes() + 'm ' + duration.seconds() + 's';
}

function myTimer() {
    var status = nasdaqStatus();
    $('#status').html(status.currentStatus);
    $('#nextStatus').html(status.nextStatus);
    $('#timeUntilNextStatus').html(status.timeUntilNextStatus);
}


setInterval(myTimer, 1000);


