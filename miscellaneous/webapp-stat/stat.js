$(document).ready(function () {
    
    var apps = [
        'agrpla', 'anogla', 'athros', 'blager', 'cataqu', 'cenexi', 'cercap', 'cimlec', 'copflo', 'diacit',
        'drobia', 'drobip', 'droele', 'droeug', 'drofic', 'drokik', 'drorho', 'drotak', 'ephdan', 'euraff', 
        'fraocc', 'gerbue', 'halhal', 'homvit', /*'hyaazt',*/ 'ladful', 'lathes', 'lepdec', 'limlun', 'loxrec',
        'luccup', 'mansex', 'maydes', 'oncfas', 'onttau', 'oruabi', 'pacven', 'partep', 'tigcal', 'tripre',
    ];

    var jxhr = [];
    var data = [];
    $.each(apps, function(i, app){
        url = 'https://' +  location.hostname + '/' + apps[i] + '/StatisticsService';
        jxhr.push(
            $.getJSON(url, function (json) {
                if (parseInt(json["annotations"]) > 0) {
                    //console.log(json);
                    data.push(json);
                }
            })
        );
    });

    // The built-in $.when.apply($, jxhr).done() can deal with at least one getJSON() failure
    // In addition, code in $.when.apply($, jxhr).always() activate as soon as one getJSON() fails
    // So define whenAll to look at all deferrer end, even if some of them failed
    // ref: http://stackoverflow.com/questions/13493084/jquery-deferred-always-called-at-the-first-reject
    var whenAll = function() {
        var dfd = $.Deferred(),
            promises = $.makeArray(arguments),
            len = promises.length,
            counter = 0,
            resolve = function() {
                counter++;
                if(counter === len) {
                    dfd["resolve"]();   
                }
            };
        $.each(promises, function(i, item) {
            item.always(resolve); 
        });
        return dfd.promise();
    };

    var res = whenAll.apply($, jxhr);
    res.done(function(){        
        //console.log(data.length);
        //data.sort( function(a,b) {
        //    return parseInt(b["annotations"]) - parseInt(a["annotations"]);
        //});
        //console.log(data);
        
        // extract types and their subtypes
        var types = [];
        for (var i=0; i<data.length ; ++i) {
            for (var j=0; j<data[i].ftypes.length; ++j) {
                // type, such as gene. Only types(solumn) with num > 0 are visible
                if (parseInt(data[i].ftypes[j].num) > 0) {
                    var type_e = data[i].ftypes[j].name;
                    if ($.inArray(type_e, types) == -1) {
                        types.push(type_e);
                    }
                    // subtype, such as gene_mRNA
                    for (var k=0; k<data[i].ftypes[j].subTypes.length; ++k) {
                        var subtype_e = data[i].ftypes[j].subTypes[k].name;
                        if ($.inArray(subtype_e, types) == -1) {
                            types.push(subtype_e);
                        }
                    }
                }
            }
        }
        types.sort();
        //for (var i=0; i<types.length; ++i)
        //    console.log(types[i]);

        // establish index mapping between table header and its index
        var header_map = {
            "species": 1,
            "total": 2,
            "last_modified": (types.length + 3),
        };

        var dateFmt = function(dateString){
            tokens = dateString.split(" "); // ex. Wed May 14 08:09:33 EDT 2014
            return tokens[1] + " " + tokens[2] + " " + tokens[tokens.length-1];
        };

        // initialze the table
        var sum = 0;
        for (var i=0; i<data.length; ++i) {
            sum += parseInt(data[i]["annotations"]);
        }
        var str = '';
        str += "Total Annotations: " + sum + " (over " + data.length + " species)";
        str += "<table id='myTable' class='tablesorter'><thead><tr>";
        str += "<th>Species</th><th>total</th>";
        for (var i=0; i<types.length; ++i) {
            header_map[types[i]] = i + 3;
            str += "<th>" + types[i] + "</th>";
        }
        str += "<th class=\"{sorter: \'date\'}\">last modified</th></tr></thead>";
        str += "<tbody>";
        for (var j=0; j<data.length ; ++j) {
            str += "<tr><td><a href='https://i5k.nal.usda.gov/" + data[j]["species"].replace(" ", "_") + "' target='_blank'>" 
                + data[j]["species"] + "</a></td>";
            str += "<td>" + data[j]["annotations"] + "</td>";
            for (var k=0; k<types.length; ++k) {
                str += "<td>0</td>";
            }
            str += "<td>" + dateFmt(data[j]["last_modified"]) + "</td></tr>";
        }
        str += "</tbody></table>";
        $('#stat').append(str);

        // fill in values in the table
        var row_index, col_index, selector, value;
        for (var i=0; i<data.length ; ++i) {
            row_index = i+1;
            for (var j=0; j<data[i].ftypes.length; ++j) { // type
                var tname = data[i].ftypes[j].name;
                if ($.inArray(tname, types) != -1) { // Only visible columns(types) will be filled. 
                    col_index = header_map[tname];
                    selector = "#myTable tr:nth-child(" + row_index + ") td:nth-child(" + col_index + ")";
                    value = data[i].ftypes[j].num;
                    $(selector).html(value);
                    //console.log(data[i].ftypes[j].name, data[i].ftypes[j].num); // type
                    for (var k=0; k<data[i].ftypes[j].subTypes.length; ++k) { // subtype
                        var tsubname = data[i].ftypes[j].subTypes[k].name;
                        col_index = header_map[tsubname];
                        selector = "#myTable tr:nth-child(" + row_index + ") td:nth-child(" + col_index + ")";
                        value = data[i].ftypes[j].subTypes[k].num;
                        $(selector).html(value);
                        //console.log(data[i].ftypes[j].subtypes[k].name, data[i].ftypes[j].subtypes[k].num); // subtype
                    }
                }
            }
        }

        $.tablesorter.addParser({
            id: 'date',
            is: function(s) { 
                return false; // return false so this parser is not auto detected
            },
            format: function(s) { 
                // format your data for normalization
                var d = new Date(s);
                return d.toISOString();
            },
            type: 'text' // either numeric or text
        });
        $("#myTable").tablesorter({
            sortList: [[1,1]],   // initializing by sorting on the first column in descending order
        });
    });
});
