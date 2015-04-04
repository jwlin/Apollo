jQuery(document).ready(function () {
    
    var apps = [
        'agrpla', 'anogla', 'athros', 'blager', 'cataqu', 'cenexi', 'cercap', 'cimlec', 'copflo', 'diacit',
        'drobia', 'drobip', 'droele', 'droeug', 'drofic', 'drokik', 'drorho', 'drotak', 'ephdan', 'euraff', 
        'fraocc', 'gerbue', 'halhal', 'homvit', /*'hyaazt',*/ 'ladful', 'lathes', 'lepdec', 'limlun', 'loxrec',
        'luccup', 'mansex', 'maydes', 'oncfas', 'onttau', 'oruabi', 'pacven', 'partep', 'tigcal', 'tripre',
    ];

    var jxhr = [];
    var data = [];
    jQuery.each(apps, function(i, app){
        url = 'https://' +  location.hostname + '/' + apps[i] + '/StatisticsService';
        jxhr.push(
            jQuery.getJSON(url, function (json) {
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
        var dfd = jQuery.Deferred(),
            promises = jQuery.makeArray(arguments),
            len = promises.length,
            counter = 0,
            resolve = function() {
                counter++;
                if(counter === len) {
                    dfd["resolve"]();   
                }
            };
        jQuery.each(promises, function(i, item) {
            item.always(resolve); 
        });
        return dfd.promise();
    };

    var res = whenAll.apply(jQuery, jxhr);
    res.done(function(){        
        data.sort( function(a,b) {
            var d1 = new Date(a["last_modified"]);
            var d2 = new Date(b["last_modified"]);
            d1 = new Date(d1.getFullYear(), d1.getMonth(), d1.getDate(),0,0,0);
            d2 = new Date(d2.getFullYear(), d2.getMonth(), d2.getDate(),0,0,0);
            if (d1 < d2) return 1;
            else if (d1 > d2) return -1;
            else {
                if (parseInt(a["annotations"]) < parseInt(b["annotations"])) return 1;
                else if (parseInt(a["annotations"]) > parseInt(b["annotations"])) return -1;
                else return 0;
            }
        });
        //console.log(data);
        var sum = 0;
        for (var i=0; i<data.length; ++i) {
            sum += parseInt(data[i]["annotations"]);
        }
        jQuery('#total-annotations').html(sum);
        jQuery('#over-species').html(data.length);
        var str = '';
        var count = data.length;
        var dateFmt = function(dateString){
            tokens = dateString.split(" "); // ex. Wed May 14 08:09:33 EDT 2014
            return tokens[1] + " " + tokens[2] + " " + tokens[tokens.length-1];
        };
        if (count > 5) { count=5; }
        for (var i=0; i<count ; ++i) {
            var sname = data[i]["species"];
            var snum = data[i]["annotations"];
            var sdate = dateFmt(data[i]["last_modified"]);
            str += "<tr><td><a href='https://i5k.nal.usda.gov/" + sname.replace(" ", "_") + "' target='_blank'>" + sname + "</a></td>"
                + "<td>" + snum + "</td><td>" + sdate + "</td></tr>;"
        }
        if (str != '') {
            jQuery('#recently-annotations').append(str);
        }
        else { // no annotation, hide the table
            jQuery('#recently-annotations').hide();
        }
    });
});
