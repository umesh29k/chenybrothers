document.write(unescape("%3Cscript src='script/jquery.ztree.all.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='script/jquery.ztree.core.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='script/jquery.ztree.core.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='script/bootstrap.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='script/popper.min.js' type='text/javascript'%3E%3C/script%3E"));

var zNodes =[{ id:"1001N", name:"Loading..", open:true}];
$(document).ready(function(){
    var ready = false;
    if(sessionStorage.getItem("access") != undefined){
        if(sessionStorage.getItem("access") != "allowed")
            window.open("/","_self");
    }
    else
        window.open("/","_self");
    $(document).bind('keypress', function(e) {
         if(e.keyCode==13 && ready){
              submit();
         }
    });
    $($("button")[1]).click(function(){
        submit();
    });
    $($("button")[0]).click(function(){
        $("input[type='text']").val("");
        $(".err").html("");
        $(".success").html("");
    });
    $.ajax({
      method: "GET",
      url: "api/1001N"
    })
    .done(function( data ) {
        zNodes = JSON.parse(data);
        $.fn.zTree.init($("#treeDemo"), setting, JSON.parse(data));
        $(".loader").hide();
        $(".body").show();
        ready = true;
    });
    $.fn.zTree.init($("#treeDemo"), setting, zNodes);
});
setInterval(function() {
   if ($("input[name='dfolder'").length) {
      $("#treeDemo a").click(function(d){
        var v = $(this).children(".node_name").html();
        $("input[name='dfolder'").val($(this).children(".node_name").html());
        $("input[name='dhfolder'").val(zNodes.filter(function(d){ return d.name === v;})[0].id);
      });
   }
}, 1000);
var setting = {
    data: {
        simpleData: {
        enable: true
        }
    }
};
function submit(){
    if($($("input[type='text']")[0]).val() == "")
        $(".err").html("Destination location empty!");
    else
    if($($("input[name='dhfolder']")).val() == "")
        $(".err").html("Upload to 'My Folder' not allowed");
    else if($($("input[type='text']")[1]).val() == "")
        $(".err").html("Source location empty!");
    else{
       $(".err").html("");
       $(".success").html("");
       $(".loader").show();
       $.ajax({
          method: "POST",
          url: "util",
          data: { dfolder: $("input[name='dhfolder']").val(), sfolder: $("input[name='sfolder']").val() }
        })
        .done(function( msg ) {
            $(".err").html(JSON.parse(msg).error);
            $(".success").html(JSON.parse(msg).output);
            sessionStorage.clear();
            $(".loader").hide();
        });
    }
}
