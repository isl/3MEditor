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
 * Gets view html for specific xpath
 */
function viewOnlySpecificPath(xpath) {

    $("#matching_table").find("tr[data-xpath='" + xpath + "']").each(function(index) {
        var $row = $(this);

        var url = "GetPart?id=" + id + "&xpath=" + $(this).attr("data-xpath") + "&mode=view";
        var req = $.myPOST(url);
        req.done(function(data) {
            checkResponse(data);

            var $data = $(data);
            highlightLink($data);
            $row.hide();
            $row.replaceWith($data);
            $row.fadeIn(500);
        });
        req.fail(function() {
            alert("Connection with server lost. Action failed!");
        });
    });
}
/*
 * Gets view html for specific generator
 */
function viewOnlyGenerator() {
    $(".focus").each(function(index) {
        var $editableToView = $(this);
        var url = "GetPart?id=" + id + "&xpath=" + $(this).attr("data-xpath") + "&mode=view&generatorsStatus=" + generatorsStatus;
        var req = $.myPOST(url);
        req.done(function(data) {
            checkResponse(data);

            $editableToView.replaceWith(data);

        });
        req.fail(function() {
            alert("Connection with server lost. Action failed!");
        });

    });
}
/*
 * Refreshes html table in generators mode
 */
function refreshTable() {
    var url = "GetPart?id=" + id + "&xpath=//mappings&mode=instance&generatorsStatus=" + generatorsStatus;
    var req = $.myPOST(url);
    req.done(function(data) {
        checkResponse(data);

        $(".mappings").html(data);

        if (generatorsStatus === "auto") {
            fillInstanceCombos();
        } else {
            fillInstanceCombos(".arg");
        }
        $("body").css("opacity", "1");
    });
    req.fail(function() {
        alert("Connection with server lost. Action failed!");
        $("body").css("opacity", "1");
    });
}
/*
 * Gets view html for entire matching table
 */
function viewOnly() {
//Then make previously edited part viewable...
    $("#matching_table").find(".edit").each(function(index) {
        var $row = $(this);
        if (typeof $(this).attr("data-xpath") !== 'undefined') { //if path===undefined abort useless request

            var mode = "view"; //Default
            if ($row.hasClass("noRelation")) {
                mode = "viewNoRelation";
            }

            var url = "GetPart?id=" + id + "&xpath=" + $(this).attr("data-xpath") + "&mode="+mode;
            var req = $.myPOST(url);

            req.done(function(data) {
                checkResponse(data);

                var $data = $(data);

                //Code added to support accordion columns
                var $head = $("thead").first().clone();//Changed because I do not need all theads!
                data = hideColumns($head, data);
                $data = $(data);

                highlightLink($data);
                $row.hide();
                $row.replaceWith($data);
                $row.fadeIn(500);

            });
            req.fail(function() {
                alert("Connection with server lost. Action failed!");
            });
        }
    });

    //Also hide help rows
    $(".dummyHeader").remove();
    $(".dummyDomain").remove();
}

/*
 * Highlights link in matching table for 10 seconds
 */
function highlightLink($data) {

    if (sourcePaths === "full") {
        $data.find(".sourcePath").each(function(index) {
            var $sourcePathSpan = $(this);
            findProperPathValue($sourcePathSpan);
        });
    }
    if (targetPaths === "full") {
        $data.find(".targetPath").each(function(index) {
            var $targetPathSpan = $(this);
            findProperPathValue($targetPathSpan);
        });
    }

    if ($data.hasClass("path")) {
        $data.css("border-top", "5px solid #29BCB5").css("border-left", "5px solid #29BCB5").css("border-right", "5px solid #29BCB5");
    } else {
        $data.css("border-bottom", "5px solid #29BCB5").css("border-left", "5px solid #29BCB5").css("border-right", "5px solid #29BCB5");
    }

    setTimeout(
            function() {
                if ($data.hasClass("path")) {
                    $data.css("border-top", "1px solid black").css("border-left", "1px solid black").css("border-right", "1px solid black");
                } else {
                    $data.css("border-bottom", "1px solid black").css("border-left", "1px solid black").css("border-right", "1px solid black");
                }
            }, 10000
            );
}

/*
 * Changes configuration options 
 */
function configurationOption(option, action) {
    if (option === "sourceAnalyzer") {
        if (action === "disable") {
            $("#sourceAnalyzer").children("label").removeClass("active");
            $("#sourceAnalyzer").find("input").removeAttr("checked");
            $("#sourceAnalyzer>#label5").addClass("disabled");
            $("#sourceAnalyzer>#label6").addClass("active");
            $("#sourceAnalyzer>#label6>input").attr("checked", "checked");
        } else {
            $("#sourceAnalyzer").children("label").removeClass("active");
            $("#sourceAnalyzer").find("input").removeAttr("checked");
            $("#sourceAnalyzer>#label5").addClass("active").removeClass("disabled");
            $("#sourceAnalyzer>#label5>input").attr("checked", "checked");


        }
    } else if (option === "targetAnalyzer") {
        if (action === "enable") {
            $("#targetAnalyzer").children("label").removeClass("active");
            $("#targetAnalyzer").find("input").removeAttr("checked");



            if (targetType == "owl" || targetType == "ttl") {
                $("#targetAnalyzer>#label2").removeClass("disabled");
                $("#targetAnalyzer>#label3").removeClass("disabled").addClass("active");
                $("#targetAnalyzer>#label3>input").attr("checked", "checked");
            } else if (targetType == "rdf") {
                $("#targetAnalyzer>#label2").removeClass("disabled").addClass("active");
                $("#targetAnalyzer>#label3").removeClass("disabled");
                $("#targetAnalyzer>#label2>input").attr("checked", "checked");
            }

        } else if (action === "enableXMLonly") {
            $("#targetAnalyzer").children("label").removeClass("active");
            $("#targetAnalyzer").find("input").removeAttr("checked");
            $("#targetAnalyzer>#label4").removeClass("disabled").addClass("active");
            $("#targetAnalyzer>#label4>input").attr("checked", "checked");
        } else {
            $("#targetAnalyzer").children("label").removeClass("active");
            $("#targetAnalyzer").find("input").removeAttr("checked");
            $("#targetAnalyzer>#label2").addClass("disabled");
            $("#targetAnalyzer>#label3").addClass("disabled");
            $("#targetAnalyzer>#label4").addClass("disabled");
            $("#targetAnalyzer>#label0").addClass("active");
            $("#targetAnalyzer>#label0>input").attr("checked", "checked");
        }
    }
}

/*
 * Gets next sibling for specific xpath
 */

function getNextPath(path) {

    var position = this.getPosition(path);
    var newPosition = "";

    var newPath;

    newPosition = parseInt(position) + 1;
    var start = path.lastIndexOf("[") + 1;
    var end = path.lastIndexOf("]") + 1;
    newPath = path.substring(0, start - 1) + "[" + newPosition + "]" + path.substring(end);

    return newPath;
}

/*
 * Gets previous sibling for specific xpath
 */
function getPreviousPath(path) {

    var position = this.getPosition(path);
    var newPosition = "";

    var newPath;

    newPosition = parseInt(position) - 1;
    var start = path.lastIndexOf("[") + 1;
    var end = path.lastIndexOf("]") + 1;
    newPath = path.substring(0, start - 1) + "[" + newPosition + "]" + path.substring(end);

    return newPath;
}

/*
 * Gets position for specific xpath
 */
function getPosition(path) {
    var start = path.lastIndexOf("[") + 1;
    var end = path.lastIndexOf("]");

    var position = "1";
    if (path.endsWith("/equals") || path.endsWith("/exists") || path.endsWith("/narrower")) { //Special treatment
        path = path.substring(0, path.lastIndexOf("/"));
    }
    if (start > 0 && end > 0 && end === path.length - 1) {
        position = path.substring(start, end);
    }
    var posAsInt = parseInt(position);
    return posAsInt;

}

/*
 * Upload file mechanism
 */

function upload($this) {

    var xpath = $this.attr("data-xpath");
    var uploadMessage = "Upload File";
    var allowedExtensions;
    if (xpath.endsWith("schema_file")) {
        uploadMessage = "Upload File";
        if (xpath.endsWith("source_schema/@schema_file")) {
            allowedExtensions = ['rdf', 'rdfs', 'xsd', 'xml'];
        } else {
            allowedExtensions = ['rdf', 'rdfs', 'xsd', 'xml', 'owl', 'ttl'];
        }
    } else if (xpath.endsWith("xml_link") || xpath.endsWith("generator_link")) {
        uploadMessage = "Upload xml";
        allowedExtensions = ['xml'];

    } else if (xpath.endsWith("html_link")) {
        uploadMessage = "Upload html";
        allowedExtensions = ['html', 'htm'];

    } else if (xpath.endsWith("rdf_link")) {
        uploadMessage = "Upload rdf";
        allowedExtensions = ['rdf'];

//    }  else if (xpath.endsWith("generator_link")) {
//        uploadMessage = "Upload File";
    }
    var mappingId = id;
    $this.fineUploader({
        request: {
            endpoint: 'UploadReceiver?id=' + id + '&path=' + xpath + '&mode=' + comboAPI

        },
        multiple: false,
        validation: {
            allowedExtensions: allowedExtensions

        },
        text: {
            uploadButton: '<span class="glyphicon glyphicon-upload"></span> ' + uploadMessage
        },
        failedUploadTextDisplay: {
            mode: 'custom',
            maxChars: 40,
            responseProperty: 'error',
            enableTooltip: true
        },
        debug: false
    }).on('complete', function(event, id, fileName, responseJSON) {
        if (responseJSON.success) {
            filename = responseJSON.filename;


            var linkText = "view";
            var dataType = "";
            if (uploadMessage === "Upload rdf") {
                linkText = "view rdf";
            } else if (uploadMessage === "Upload xml") {

                if (!xpath.endsWith("generator_link")) { //Don't enable source analyzer for generator policy files
                    sourceAnalyzer = "on";
                    sourceAnalyzerFiles = sourceAnalyzerFiles.split("***")[0] + "***" + filename;
                    sourceAnalyzerPaths = "";
                    configurationOption("sourceAnalyzer", "enable");
                    viewOnly();

                    $("a:contains('Transformation')").attr("href", "#x3mlEngine").parent().removeClass("disabled").removeAttr("title");
                    linkText = "view xml";
                }
            } else if (uploadMessage === "Upload html") {
                linkText = "view html";

            } else { //Not sure if I want a default analyzer
                if (xpath.endsWith("source_schema/@schema_file")) {

                    if (filename.endsWith(".xsd")) {
                        sourceAnalyzerFiles = filename + "***" + sourceAnalyzerFiles.split("***")[1];
                        sourceAnalyzerPaths = "";
                        if ($(".sourcePath").first().html().length > 0) { //has specified root
                            sourceAnalyzer = "on";

                            configurationOption("sourceAnalyzer", "enable");
                            viewOnly();
                        } else {

                            alert("XML Schema uploaded. Once you specify a root source xpath in domain row, source analyzer is enabled! (Configuration tab)")
                        }

                    }



                } else {
                    dataType = "target_info"; //Added only for this case, may have to make use of it for other cases too.
                    if (comboAPI == 0) {
                        if (filename.endsWith(".xsd") || filename.endsWith(".xml")) {
                            if ($(".targetPath").first().html().length > 0) { //has specified root
                                comboAPI = 4;
                                configurationOption("targetAnalyzer", "enableXMLonly");
                            } else {
                                alert("XML Schema uploaded. Once you specify a root target xpath in domain row, XML target analyzer is enabled! (Configuration tab)")
                            }
                            targetType = "xml";
                            targetFiles = filename; //Atm only accept one xsd file!

                            $("#addTarget").hide();

                        } else {
                            if (filename.endsWith(".owl")) {
                                targetType = "owl";
                                comboAPI = 3;
                            } else if (filename.endsWith(".ttl")) {
                                targetType = "ttl";
                                comboAPI = 3;
                            } else {
                                targetType = "rdf";
                                comboAPI = 2;
                            }
                            configurationOption("targetAnalyzer", "enable");
                        }

                    }
                    viewOnly();
                }
            }

            if (xpath.endsWith("generator_link")) {
                url = "FetchBinFile?id=" + mappingId + "&amp;type=generator_link&amp;file=" + encodeURIComponent(filename);
                linkText = "view generator xml";
            } else if (xpath.endsWith("rdf_link")) {
                url = "FetchBinFile?id=" + mappingId + "&amp;type=example_data_target_record&amp;file=" + encodeURIComponent(filename);
            } else {
                url = "FetchBinFile?id=" + mappingId + "&amp;file=" + encodeURIComponent(filename);
            }
            var linkHtml = "<a data-type='" + dataType + "' title='" + encodeURIComponent(filename) + "' style='position:relative;top:1px;' target='_blank' href='" + url + "'>" + linkText + " </a>";
            var deleteHtml = " <button class='btn btn-default btn-link btn-sm deleteFile' type='button' title='Delete " + encodeURIComponent(filename) + "' id='delete***" + xpath + "'>" +
                    "<span class='glyphicon glyphicon-remove'></span>" +
                    "</button> ";
            var $html = $("<div>" + linkHtml + deleteHtml + "</div>");
            $this.next().css('display', 'inline');
            $this.before($html);
            $this.toggle("slow");
        } else {
            var error = responseJSON.error;
            alert(error);
        }
    });
}


