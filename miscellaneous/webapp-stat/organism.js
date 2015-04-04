var species = "athros";
$(document).ready(function () {
    $.ajax({
        type: 'GET',
        url: 'https://' +  location.hostname + '/' + species + '/StatisticsService',
        data: "",
        dataType: "json",
        success: function(data) {
            var str = '';
            for (var j=0; j<data["ftypes"].length; ++j) {
                // type, such as gene. Only types(solumn) with num > 0 are visible
                if (parseInt(data["ftypes"][j].num) > 0) {
                    var tname = data["ftypes"][j]["name"];
                    str += "<tr><th colspan='2'>" + tname + "</th><td>" + data["ftypes"][j].num + "</td></tr>";
                    // subtype, such as gene_mRNA
                    var subtype_num = data["ftypes"][j]["subTypes"].length;
                    for (var k=0; k<subtype_num; ++k) {
                        var subname = data["ftypes"][j]["subTypes"][k]["name"].replace(tname, "");
                        subname = subname.replace("_", "");
                        str += "<tr><th></th><th>" + subname + "</th><td>" + data["ftypes"][j]["subTypes"][k].num + "</td></tr>";
                    }
                }
            }
            if (str != '') {
                $('#annotation').append(str);
            }
            else { // no annotation, hide the table
                $('#annotation').hide();
            }
        },
        error: function() {
            $('#annotation').hide();
        }
    });
});
