/*
 * Copyright 2014-2016 Institute of Computer Science,
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


/*
 * Variables initialization
 */
var comboAPI, goAhead;
var clipboard = {
    "mapping": "",
    "link": ""
};
var clipBoardValue = "";
var sourcePaths = "mini";
var targetPaths = "mini";
var targetRoot = $(".targetPath").first().html();
var sourceRoot = $(".sourcePath").first().html();

/*
 * Page initialization
 */
$(document).ready(function() {
//$.ajaxSetup({
//  timeout: 10000
//});

    $('.description').popover({
        trigger: "hover",
        placement: "right"
    });

    $("#matching_table").on("click", ".collapseExpand", function() {
        $(this).parentsUntil("thead").parent().next("tbody").children("tr.path, tr.range").toggle();
    });

    $("#matching_table").on("click", ".columnShow", function() {
        var $this = $(this);
        var colName = $this.closest("th").attr('class');
        $("." + colName).toggle();
        $("." + colName + ":hidden").remove();
    });

    $("#matching_table").on("click", ".columnHide", function() {
        var $this = $(this);
        var colName = $this.closest("th").attr('class');
        $("th." + colName).hide().after("<th class='" + colName + "'><i title='Click to expand column' class='columnShow fa fa-arrow-right'></i></th>");
        $("td." + colName).hide().after("<td class='" + colName + "'>&#160;</td>");
    });


    if (mode === 0) { //edit_mode
        comboAPI = $('#targetAnalyzer input:radio:checked').val();


        $('#table_view-btn').click(function() {

            $("body").css("opacity", "0.4");

            var url = "GetPart?id=" + id + "&part=mappings&mode=view";
            var $btn = $(this);
            $btn.button('loading');
            var req = $.myPOST(url, "", "", 10000);
            req.done(function(data) {
                checkResponse(data);
                $("#matching_table>div.mappings").html(data);
                if (sourcePaths === "full") {
                    $(".sourcePath").each(function(index) {
                        var $sourcePathSpan = $(this);
                        findProperPathValue($sourcePathSpan);
                    });
                }
                if (targetPaths === "full") {
                    $(".targetPath").each(function(index) {
                        var $targetPathSpan = $(this);
                        findProperPathValue($targetPathSpan);
                    });
                }
                $btn.button('reset');
                $('.description').popover({
                    trigger: "hover",
                    placement: "right"
                });

                $('.collapseExpand').click(function() {
                    $(this).parentsUntil(".empty").parent().prevAll("tr.path, tr.range").toggle();
                });
                $(".empty").find("div.row").css("display", "block");
                $("body").css("opacity", "1");


            });
            req.fail(function() {
                alert("Connection with server lost. Action failed!");
                $btn.button('reset');
                $("body").css("opacity", "1");
            });


        });
        $('#collapseExpandAll-btn').click(function() {
            $("tr.path, tr.range").toggle();
        });
        $('#scrollTop-btn').click(function() {
            $("html, body").animate({scrollTop: 0}, "slow");
        });
        $('#scrollBottom-btn').click(function() {
            $("html, body").animate({scrollTop: $(document).height()}, "slow");
        });
        $('#info_rawXML-btn, #rawXML-btn').click(function() {
            $("#myModal").find("textarea").val("");
            var xpath = "";

            if ($(".active").children("a").html() === "Info") {
                xpath = "//x3ml/*[name()='info' or name()='namespaces']";

            } else {
                if ($(".edit").length === 0) { //All
                    xpath = "//x3ml/mappings";
                } else if ($(".edit").length === 1) { //Domain
                    xpath = $(".edit").attr("data-xpath");
                } else if ($(".edit").length === 2) {//Link
                    xpath = $(".edit").attr("data-xpath");
                    xpath = xpath + "/..";
                } else if ($(".edit").length === 3) {//New map
                    xpath = $(".edit").attr("data-xpath");
                    xpath = xpath + "/..";
                }
            }
            $("#myModal").find(".xpath").html(xpath);

            var url = "Action?id=" + id + "&xpath=" + xpath + "&action=raw";
            var req = $.myPOST(url);
            req.done(function(data) {
                checkResponse(data);

                $("#myModal").find("textarea").val(data);
            });


            $("#myModal").modal('show');

        });
    } else if (mode === 2) { //generators mode
        if (generatorsStatus === "auto") {
            getInstanceGeneratorNamesAndFillCombos();
        } else {
            fillInstanceCombos(".arg");
        }
    }

});



/*
 * Handler fired when clicking tabs (Info, Matching etc)
 */
$('.nav a').click(function(e) {
    e.preventDefault();
    if ($(this).html() === "About") {
        $("#about").load("readme.html");
    } else if ($(this).html() === "Analysis") {
        $("#graph").load("analysis.html");
    } else if ($(this).html() === "Transformation") {
        $("#x3mlEngine").load(("x3mlEngine.html"), function() {
            var sourceFilename = "";
            if ($("info_view-btn").is(':visible')) {//edit_mode
                sourceFilename = $("div:visible>a:contains('view xml')").attr("title");
            } else {
                sourceFilename = $("a:contains('view xml')").attr("title");
            }
//            alert(sourceFilename)
            var url = "FetchBinFile?file=" + encodeURIComponent(sourceFilename)+"&type=xml_link";
            var req = $.myPOST(url, "xml");
            req.done(function(xml) {
                checkResponse(xml);

//            $.post(url, "xml").done(function(xml) {
                var xmlString = (new XMLSerializer()).serializeToString(xml);
                $("#sourceFile").val(xmlString);
            });

//            var generatorPolicyFilename = $("a:contains('view generator xml')").attr("href");
//            var url = "FetchBinFile?file=" + generatorPolicyFilename;
            var url = "";
            if ($("info_view-btn").is(':visible')) {//edit_mode
                url = $("div:visible>a:contains('view generator xml')").attr("href");
            } else {
                url = $("a:contains('view generator xml')").attr("href");
            }

//            alert(url)
//            $.post(url, "xml").done(function(xml) {
            var req = $.myPOST(url, "xml");
            req.done(function(xml) {
//                checkResponse(xml); //Fetch does not return html with title, should not check

                var xmlString = (new XMLSerializer()).serializeToString(xml);

                $("#generator").val(xmlString);
            });

        });

    }
});








