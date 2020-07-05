document.write(unescape("%3Cscript src='script/jquery.ztree.all.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='script/jquery.ztree.core.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='script/jquery.ztree.core.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='script/bootstrap.min.js' type='text/javascript'%3E%3C/script%3E"));
document.write(unescape("%3Cscript src='script/popper.min.js' type='text/javascript'%3E%3C/script%3E"));

var folders = {
    "Hybris":"b12dd0cc95afc7cb641f9595182ed47745a091da",
    "Media":"18a761d386e187f35190562ec39844b8b0211e2b"
};
var zNodes =[{ id:"1001N", name:"Loading..", open:true}];
$(document).ready(function(){
    $('input[type=file]').change(function () {
        console.log(this.files[0].mozFullPath);
    });
    $($("button")[1]).click(function(){
        $("input[type='text']").siblings("span").html("");
        if($($("input[type='text']")[0]).val() == "")
            $(".err").html("Destination location empty!");
        else
        if($($("input[name='dhfolder']")).val() == "")
            $(".err").html("Upload to 'My Folder' not allowed");
        else if($($("input[type='text']")[1]).val() == "")
            $(".err").html("Source location empty!");
        else{
           $.ajax({
              method: "POST",
              url: "util",
              data: { dfolder: $("input[name='dhfolder']").val(), sfolder: $("input[name='sfolder']").val() }
            })
            .done(function( msg ) {
                $(".err").html(JSON.parse(msg).error);
                $(".success").html(JSON.parse(msg).output);
            });
        }
    });
    $($("button")[0]).click(function(){
        $("input[type='text']").val("");
        $("input[type='text']").siblings("span").html("");
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
