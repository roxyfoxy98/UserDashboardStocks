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