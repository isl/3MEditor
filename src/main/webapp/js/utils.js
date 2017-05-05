/*
 * Copyright 2014-2017 Institute of Computer Science,
 * Foundation for Research and Technology - Hellas
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 *
 * Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
 * Tel:+30-2810-391632
 * Fax: +30-2810-391638
 * E-mail: isl@ics.forth.gr
 * http://www.ics.forth.gr/isl
 *
 * Authors : Georgios Samaritakis, Konstantina Konsolaki.
 *
 * This file is part of the 3MEditor webapp of Mapping Memory Manager project.
 */

$(window).on('beforeunload', function() {
    closeAndUnlock(id);
});

function toggleCollapseExpandImage($btn) {
    var imgSrc = $btn.children("img").attr("src");
    if (imgSrc === "images/collapse-map.png") {
        $btn.children("img").attr("src", "images/expand-map.png");
    } else if (imgSrc === "images/expand-map.png") {
        $btn.children("img").attr("src", "images/collapse-map.png");
    }
}


function initScrollbar(table) {
    if (table === "Mappings") {
        $(".mappings").mCustomScrollbar({
            theme: "rounded-dots-dark",
            autoHideScrollbar: true
        });

        $("#matching_table").on("click", "#scrollTop-btn", function() {
            $(".mappings").mCustomScrollbar("scrollTo", "top");
        });

        $("#matching_table").on("click", "#scrollBottom-btn", function() {
            $(".mappings").mCustomScrollbar("scrollTo", "bottom");

        });

    } else if (table === "Generators") {
//        $("#generatorsTab .mappings").mCustomScrollbar({
//            theme: "rounded-dots-dark",
//            autoHideScrollbar: true
//        });
        $("#generatorsTab .mappings").mCustomScrollbar({
            theme: "rounded-dots-dark",
            autoHideScrollbar: false
        });

        $("#generatorsTab").on("click", "#scrollTop-btn", function() {
            $("#generatorsTab .mappings").mCustomScrollbar("scrollTo", "top");
        });
        $("#generatorsTab").on("click", "#scrollBottom-btn", function() {
            $("#generatorsTab .mappings").mCustomScrollbar("scrollTo", "bottom");

        });
    }


}
function closeAndUnlock(id) {
//Decided tha popup is no longer necessary...
    $.ajax({
        url: 'Action?action=close&id=' + id,
        async: false
    });
}

function  confirmDialog() {
    confirmDialog("");
}

function viewGraph() {
    window.open('/Maze/singlemapping.html?id=' + id, '_blank');
    return false;
}

function  confirmDialog(type) {
    if (type === "Mapping") {
        goAhead = confirm("This action will delete all mapping contents! Are you sure?");
    } else if (type === "GeneratorName") {
        goAhead = confirm("This action will replace all generator arguments! Are you sure?");
    } else {
        goAhead = confirm("Are you sure?");
    }
}


/*
 * Hides matching table columns to save space
 * @param {type} $header
 * @param {type} data
 * @returns {unresolved}
 */
function hideColumns($header, data) {
    var $data = $("<tbody>" + data + "</tbody>"); //Added wrapper element tbody

    var dataType;
    if ($data.find("td").length === 6) {//Domain
        dataType = "domain";
    } else if ($data.find("td").length === 12) {//Link
        dataType = "link";
    }


    if ($header.find("th.sourceCol").length === 2) {
        $data.find("td.sourceCol").after("<td class='sourceCol'>&#160;</td>").hide();

    }
    if ($header.find("th.targetCol").length === 2) {
        $data.find("td.targetCol").after("<td class='targetCol'>&#160;</td>").hide();

    }
    if ($header.find("th.ifCol").length === 2) {
        $data.find("td.ifCol").after("<td class='ifCol'>&#160;</td>").hide();

    }
    if ($header.find("th.commentsHead").length === 2) {
        $data.find("td.commentsHead").after("<td class='commentsHead'>&#160;</td>").hide();
    }
    return $data.html();

}
/*
 *  Chooses whether to show mini or full xpath
 */
function findProperPathValue($element) {
    var paths;
    if ($element.attr("class") === "sourcePath") {
        paths = sourcePaths;
    } else if ($element.attr("class") === "targetPath") {
        paths = targetPaths;
    }
    var value = $element.attr("data-" + paths + "Path");
    $element.html(value);
}

function checkResponse(data) {

    if (typeof data === "string" && data.indexOf("<title>3M</title>") !== -1) {
        window.location.assign("/3M")
    }
}
/*
 * Added functions
 */

(function($) {
    $.myPOST = function(url, data, dataType, timeout) {
        if (typeof timeout === 'undefined') {
            timeout = 20000; //Default timeout is 20secs if none is specified
        }

        var settings = {
            type: "POST", //predefine request type to POST
            'url': url,
            'data': data,
            'dataType': dataType,
            'timeout': timeout
        };
        return $.ajax(settings);
    };
})(jQuery);

String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};
String.prototype.startsWith = function(searchString, position) {
    position = position || 0;
    return this.indexOf(searchString, position) === position;
};
String.prototype.replaceAll = function(str1, str2, ignore)
{
    return this.replace(new RegExp(str1.replace(/([\/\,\!\\\^\$\{\}\[\]\(\)\.\*\+\?\|\<\>\-\&])/g, "\\$&"), (ignore ? "gi" : "g")), (typeof (str2) === "string") ? str2.replace(/\$/g, "$$$$") : str2);
};

jQuery.fn.swapWith = function(to) {
    return this.each(function() {
        var copy_to = $(to).clone(true);
        var copy_from = $(this).clone(true);
        $(to).replaceWith(copy_from);
        $(this).replaceWith(copy_to);
    });
};