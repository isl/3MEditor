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
var selectedRows = new Array();
var selectedMaps = new Array();
var activeTab = $(".active").children("a").html();

/*
 * Page initialization
 */


$(document).ready(function() {


    $('.description').popover({
        trigger: "hover",
        placement: "right"
    });


    $("#matching_table, #generatorsTab").on("click", ".collapseExpand", function() {
        $(this).parentsUntil("thead").parent().next("tbody").children("tr.path, tr.range").toggle();
        toggleCollapseExpandImage($(this));
    });

    $("#matching_table, #generatorsTab").on("click", ".columnShow", function() {
        var $this = $(this);
        var type = $this.attr("type");

        var activeTab = $(".active").children("a").html();
        var tabId;
        if (activeTab === "Matching Table") {
            tabId = "#matching_table";
        } else {
            tabId = "#generatorsTab";
        }

        var btnId = $this.attr("id");
        var colName;
        if (type === "button") {

            if (btnId === "allSources-btn") {
                colName = "sourceCol";
            } else if (btnId === "allTargets-btn") {
                colName = "targetCol";
            } else if (btnId === "allRules-btn") {
                colName = "ifCol";
            } else if (btnId === "allComments-btn") {
                colName = "commentsHead";
            }
            $(tabId + " ." + colName).toggle();
            $(tabId + " ." + colName + ":hidden").remove();
            $this.addClass("columnHide").removeClass("columnShow");
            $this.attr("title", "Click to collapse column");
            $this.children("img").attr("src", "images/collapse-column.png");

        } else {
            colName = $this.closest("th").attr('class');
            $("." + colName).toggle();
            $("." + colName + ":hidden").remove();
        }


    });

    $("#matching_table, #generatorsTab").on("click", ".columnHide", function() {
        var $this = $(this);
        var imageSrc;
        var type = $this.attr("type");
        var activeTab = $(".active").children("a").html();
        var tabId;
        if (activeTab === "Matching Table") {
            tabId = "#matching_table";
        } else {
            tabId = "#generatorsTab";
        }
        var btnId = $this.attr("id");
        var colName;
        if (type === "button") {

            if (btnId === "allSources-btn") {
                colName = "sourceCol";
            } else if (btnId === "allTargets-btn") {
                colName = "targetCol";
            } else if (btnId === "allRules-btn") {
                colName = "ifCol";
            } else if (btnId === "allComments-btn") {
                colName = "commentsHead";
            }
            $this.removeClass("columnHide").addClass("columnShow");
            $this.attr("title", "Click to expand column");
            $this.children("img").attr("src", "images/expand-column.png");

            $(tabId + " th." + colName).hide().after("<th class='" + colName + "'>&#160;</th>");
            $(tabId + " td." + colName).hide().after("<td class='" + colName + "'>&#160;</td>");

        } else {
            colName = $this.closest("th").attr('class');

            $("th." + colName).hide().each(function() {
                var $this = $(this);
                if ($this.parent().hasClass("dummyHeader")) {//Use white image for dummyHeader and black for rest
                    imageSrc = "images/expand-column-white.png";
                } else {
                    imageSrc = "images/expand-column.png";
                }
                $this.after("<th class='" + colName + "'><img class='columnShow' title='Click to expand column' src='" + imageSrc + "'/></th>");
            });
            $("td." + colName).hide().after("<td class='" + colName + "'>&#160;</td>");

        }
    });


    if (mode === 0) { //edit_mode
        comboAPI = $('#targetAnalyzer input:radio:checked').val();

        $("#matching_table, #generatorsTab").on("click", "#table_view-btn", function() {
          
//        $('#table_view-btn').click(function() {

            if ($(".active").children("a").html() === "Generators") {
                if ($(".focus").length > 0) {
                    initGenerators();
                }
            } else {

                $("body").css("opacity", "0.4");

                var url = "GetPart?id=" + id + "&part=mappings&mode=view";
                var $btn = $(this);
                $btn.button('loading');
                var req = $.myPOST(url, "", "", 20000);
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
                        toggleCollapseExpandImage($(this));
                    });
                    $(".empty").find("div.row").css("display", "block");
                    $("body").css("opacity", "1");


                });
                req.fail(function() {
                    alert("Connection with server lost. Action failed!");
                    $btn.button('reset');
                    $("body").css("opacity", "1");
                });
            }

        });
        $("#matching_table, #generatorsTab").on("click", "#collapseExpandAll-btn", function() {
            var activeTab = $(".active").children("a").html();
            var tabId;
            if (activeTab === "Matching Table") {
                tabId = "#matching_table";
            } else {
                tabId = "#generatorsTab";
            }

            var imgSrc = $(this).children("img").attr("src");
            if (imgSrc === "images/collapse-map.png") {
                $(tabId + " tr.path," + tabId + " tr.range").hide();
            } else if (imgSrc === "images/expand-map.png") {
                $(tabId + " tr.path," + tabId + " tr.range").show();
            }



            toggleCollapseExpandImage($(this));
            $(tabId + " .collapseExpand").each(function() {//toggle all buttons
                toggleCollapseExpandImage($(this));
            });

        });

        $(".fixed").affix({
            offset: 150
        });

        $("#matching_table, #generatorsTab").on("click", "#scrollTop-btn", function() {
            $("html, body").animate({scrollTop: 0}, "slow");
        });
        $("#matching_table, #generatorsTab").on("click", "#scrollBottom-btn", function() {
            $("html, body").animate({scrollTop: $(document).height()}, "slow");
        });

        $("body").on("click", "#info_rawXML-btn, #rawXML-btn", function() {
//        $('#info_rawXML-btn, #rawXML-btn').click(function() {
            $("#myModal").find("textarea").val("");
            var xpath = "";

            if ($(".active").children("a").html() === "Info") {
                xpath = "//x3ml/*[name()='info' or name()='namespaces']";

            } else {
                if ($(".edit").length === 0) { //All
                    if ($(".focus").length === 0) {
                        xpath = "//x3ml/mappings";
                    } else {//Generator
                        xpath = $(".focus").attr("data-xpath");
                    }
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

        $("#generatorsTab").on("click", ".instance_generator.clickable, .label_generator.clickable", function(e) {

            viewOnlyGenerator();

            var $generator = $(this);
            var xpath = $generator.attr("data-xpath")

            var url = "GetPart?id=" + id + "&xpath=" + xpath + "&mode=edit&generatorsStatus=" + generatorsStatus;
            var req = $.myPOST(url);
            req.done(function(data) {
                checkResponse(data);
                $generator.replaceWith(data);
                if (generatorsStatus === "auto") {
                    getInstanceGeneratorNamesAndFillCombos();
                } else {
                    fillInstanceCombos(".arg");
                }
            });
            req.fail(function() {
                alert("Connection with server lost. Action failed!");
            });


        });
//        $("#generatorsTab").on("click", ".label_generator", function(e) {
//            alert("Label clicked");
//        });

    } else if (mode === 2) { //generators mode


        if (generatorsStatus === "auto") {
            getInstanceGeneratorNamesAndFillCombos();
        } else {
            fillInstanceCombos(".arg");
        }
    }
    if (schemaVersion === "1.1") {
        if (confirm("You are using an older incompatible x3ml schema version (1.1 or older). Do you wish to update your mapping to version 1.3?") === true) {
            var url = "Services?method=update&id=" + id + "&from=1.1&to=1.2";//1.2 is compatible with 1.3
            var req = $.myPOST(url);
            req.done(function(data) {
                alert(data);//Useful. DO NOT DELETE!
                location.reload();
            });
        }
    }

});


/*
 * Handler fired when user changes source paths option
 */
$("#sourcePaths input:radio").change(function() {
    sourcePaths = $(this).val();
    if (mode !== 1) {//only needed when in edit mode
        viewOnly();
    }
    $(".sourcePath").each(function(index) {
        var $sourcePathSpan = $(this);
        findProperPathValue($sourcePathSpan);
    });
});
/*
 * Handler fired when user changes target paths option
 */
$("#targetPaths input:radio").change(function() {
    targetPaths = $(this).val();
    if (mode !== 1) {//only needed when in edit mode
        viewOnly();
    }
    $(".targetPath").each(function(index) {
        var $targetPathSpan = $(this);
        findProperPathValue($targetPathSpan);
    });
});

/*
 * Handler fired when clicking tabs (Info, Matching etc)
 */
$('.nav a').click(function(e) {
    e.preventDefault();
    $("body").css("opacity", "1");
    activeTab = $(this).html();
    if ($(this).html() === "About") {
        $("#about").load("readme.html");
    } else if ($(this).html() === "Generators") {
        initGenerators();
    } else if ($(this).html() === "Analysis") {
        $("#graph").load("analysis.html");
    } else if ($(this).html() === "Transformation") {
        $("#x3mlEngine").load(("x3mlEngine.html"), function() {
            var sourceFilename = "";
            if ($("#info_view-btn").is(':visible')) {//edit_mode
                sourceFilename = $("div:visible>a:contains('view xml')").attr("title");
            } else {
                sourceFilename = $("a:contains('view xml')").attr("title");
            }
            var url = "FetchBinFile?file=" + encodeURIComponent(sourceFilename) + "&type=xml_link";
            var req = $.myPOST(url, "xml");
            req.done(function(xml) {
                checkResponse(xml);
                var xmlString = (new XMLSerializer()).serializeToString(xml);
                $("#sourceFile").val(xmlString);
            });

            url = "";
            if ($("#info_view-btn").is(':visible')) {//edit_mode
                url = $("div:visible>a:contains('view generator xml')").attr("href");
            } else {
                url = $("a:contains('view generator xml')").attr("href");
            }

            req = $.myPOST(url, "xml");
            req.done(function(xml) {
                var xmlString = (new XMLSerializer()).serializeToString(xml);
                $("#generator").val(xmlString);
            });

            url = "";
            if ($("#info_view-btn").is(':visible')) {//edit_mode
                url = $("div:visible>a:contains('view target')").attr("href");
            } else {
                url = $("a:contains('view target')").attr("href");
            }

            if (typeof url === 'undefined') { //If no target record disable Visualize
                $("#visualizeTarget").addClass("disabled");
            } else {
                req = $.myPOST(url, "xml");
                req.done(function(xml) {
                    $("#engineResult").val(xml);
                });
            }


        });

    }
});




function initGenerators() {

    $("body").css("opacity", "0.4");

    var url = "GetPart?id=" + id + "&part=mappings&mode=instance";

    var req = $.myPOST(url, "", "", 20000);
    req.done(function(data) {
        checkResponse(data);
        $("#generatorsTab").html(data);
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
        $('.description').popover({
            trigger: "hover",
            placement: "right"
        });

        $('.collapseExpand').click(function() {
            $(this).parentsUntil(".empty").parent().prevAll("tr.path, tr.range").toggle();
            toggleCollapseExpandImage($(this));

        });



        if (mode === 0) { //Fill combos only if edit mode
            if (generatorsStatus === "auto") {
                getInstanceGeneratorNamesAndFillCombos();
            } else {
                fillInstanceCombos(".arg");
            }
        } else {//if view mode 
            $("#generatorsTab .actionsToolbar").hide(); //hide actions toolbar
            $(".generatorButtons").hide(); //hide add links
            $(".additionalGeneratorButtons").hide(); //hide additional add links
            $("#generatorsTab legend").html("View mode"); //change legend

        }
        $(".fixed").affix({
            offset: 150
        });
        $("body").css("opacity", "1");


    });
    req.fail(function() {
        alert("Connection with server lost. Action failed!");
//        $btn.button('reset');
        $("body").css("opacity", "1");
    });






}





