$(document).ready(function () {
    $("#btnSubmit").prop("disabled", true);
    $('#xls').change(function(){
        $('#selectedFile').text($('#xls').val().match(/[^\\/]*$/)[0]);
        $("#btnSubmit").prop("disabled", false);
        $(".result label").hide();
        $(".resultOk label").text("");
        $(".resultOk label").show();
        $(".resultSkipped label").text("");
        $(".resultSkipped label").show();
        $(".resultKo label").text("");
        $(".resultKo label").show();
    });

    $("#btnSubmit").click(function (event) {
        $(".result label").hide();
        $(".resultOk label").text("");
        $(".resultOk label").show();
        $(".resultSkipped label").text("");
        $(".resultSkipped label").show();
        $(".resultKo label").text("");
        $(".resultKo label").show();

        event.preventDefault();
        //validate input fields
        if ($("#xls").val().length > 1) {
            var filename = $("#xls").val();

            var fileEextension = filename.replace(/^.*\./, '');

            if (fileEextension == filename) {
                fileEextension = '';
            } else {
                fileEextension = fileEextension.toLowerCase();
            }

            if(fileEextension != 'csv')
            {
                alert("Only CSV formats are allowed!");
                return;
            }


            var form = $('#fileUploadForm')[0];

            var fd = new FormData(form);

            $("#btnSubmit").prop("disabled", true);
            $("#xls").prop("disabled", true);

            $(".loading").removeClass("loading--hide").addClass("loading--show");

            var json;
            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: "/bin/servlets/importjobstart2",
                data: fd,
                processData: false,
                contentType: false,
                cache: false,
                success: function (data) {
                    json = JSON.parse(JSON.stringify(data));
                    CheckImportJob(json)
                },
                error: function (e) {
                    $(".result label").text("Import failed");
                    $(".result label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    $("#btnSubmit").prop("disabled", false);
                    $("#xls").prop("disabled", false);
                }
            });
        }
        else
        {
            alert("Please, fill the mandatory fields");
            return;
        }
    });

});
;
var timeout;
var checkStarted= false

function CheckImportJob(json){

    $.ajax({
        url: '/bin/servlets/stats',
        type: 'get',
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
            console.log("BBBBBBBB",data)
            if(!checkStarted){
                if(data["statsPercentage"] === 100){
                    checkStarted = false
                }else{
                    checkStarted=true
                }
            }

            if(checkStarted && data["statsPercentage"] === 100 ){
                clearTimeout(timeout);
                $(".result label").text("Processing: 100%");
                $(".result label").show();
                sleep(1000).then(() => {
                    $("#btnSubmit").prop("disabled", false);
                    $("#xls").prop("disabled", false);
                    $(".result label").text("Activity report: 100%" +" | " + " Total Articles Proccessed: "+ (data["statsSkippedRecords"]+data["statsProcessedRecords"]));
                    $(".resultOk label").text("-New articles created: " + data["statsProcessedRecords"]);
                    $(".resultOk label").show();
                    $(".resultSkipped label").text("-Skipped Articles: " + data["statsSkippedRecords"]);
                    $(".resultSkipped label").show();
                    $(".resultKo label").text("-Articles with error: " + data["statsErrorRecords"]);
                    $(".resultKo label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    $(".progress").css("width", "100%")
                    $(".progress").html("100% completed")


                })
            } else {
              if(checkStarted){
                  $(".result label").text("Proccessing report: " + data["statsPercentage"] +"% "+" | " + " Total Articles Proccessed: "+ (data["statsSkippedRecords"]+data["statsProcessedRecords"]));
                  $(".progress").css("width", data["statsPercentage"]+"%")
                  $(".progress").html(data["statsPercentage"] +"%")
              }else{
                $(".result label").text("Proccessing report: 0%");
              }

                $(".result label").show()
                $(".resultOk label").text("-New Articles created: " + data["statsProcessedRecords"]);
                $(".resultOk label").show();
                $(".resultSkipped label").text("-Skipped articles: " + data["statsSkippedRecords"]);
                $(".resultSkipped label").show();
                $(".resultKo label").text("-Articles with error: : " + data["statsErrorRecords"]);
                $(".resultKo label").show();

                timeout = setTimeout(CheckImportJob, 500, json);
            }
        },
        error:function(e){
            console.log("ERRRRROR",e)
        }

    });

    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
      }
}

// function CheckImportJob(json){
//     var jobData = {
//         id: json["id"]
//     }
//     $.ajax({
//         url: '/bin/servlets/importjobcheck2',
//         type: 'post',
//         dataType: 'json',
//         contentType: 'application/json',
//         data: JSON.stringify(jobData),
//         success: function (data) {
//             // console.log("CCCCCCC",data,+(data["progressStep"].split(".")[0]) )
//             // const percentage = +(data["progressStep"].split(".")[0])
//             console.log("BBBBBBBB",data)
//             if(data["state"] == "SUCCEDED" ){
//                 clearTimeout(timeout);
//                 $(".result label").text("Processing: 100%");
//                 $(".result label").show();
//                 sleep(1000).then(() => {
//                     $(".result label").text("Activity report:");
//                     $(".resultOk label").text("- " + data["processLogOk"]);
//                     $(".resultOk label").show();
//                     $(".resultSkipped label").text("- " + data["processLogSkipped"]);
//                     $(".resultSkipped label").show();
//                     $(".resultKo label").text("- " + data["processLogKo"]);
//                     $(".resultKo label").show();
//                     $(".loading").removeClass("loading--show").addClass("loading--hide");
//                 })
//             } else {
//                 var msg = "Processing: ";
//                 if(data["progressStep"] != undefined){
//                     msg = msg + data["progressStep"];
//                 } else{
//                     msg = msg + "0";
//                 }
//                 $(".result label").text(msg);
//                 $(".result label").show();
//                 timeout = setTimeout(CheckImportJob, 3000, json);
//             }
//         },
//         error:function(e){
//             console.log("ERRRRROR",e)
//         }

//     });

//     function sleep(ms) {
//         return new Promise(resolve => setTimeout(resolve, ms));
//       }
// }