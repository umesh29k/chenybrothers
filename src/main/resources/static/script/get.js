document.write(unescape("%3Cscript src='script/bootstrap.min.js' type='text/javascript'%3E%3C/script%3E"));

$(document).ready(function(){
        $($("button")[1]).click(function(){
            if($($("input[type='text']")[0]).val() == "")
                $(".err").html("invalid username");
            else if($($("input[type='password']")[0]).val() == "")
                $(".err").html("invalid password");
            else{
               $.ajax({
                  method: "POST",
                  url: "api/get/",
                  data: {dat: btoa($("input[name='unm']").val()) + "#@!" + btoa($("input[name='pwd']").val())}
                })
                .done(function( data ) {
                    if(JSON.parse(data).status){
                        sessionStorage.setItem("access", "allowed");
                        window.open("todo","_self")
                    }
                    else
                        $(".err").html(JSON.parse(data).status);
                });
            }
        });
        $($("button")[0]).click(function(){
            $("input[type='text']").val("");
            $("input[type='password']").val("");
            $(".err").html("");
            $(".success").html("");
        });
});