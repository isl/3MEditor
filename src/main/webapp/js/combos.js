/*
 * Copyright 2014-2015 Institute of Computer Science,
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
 * Scripts used for combo filling
 */

function getInstanceGeneratorNamesAndFillCombos() {
    var url = "Services?id=" + id + "&method=instanceGeneratorNames";
    $.ajax({
        url: url,
        dataType: 'json'
    }).success(function(data) {
        instanceGeneratorsNames = data;
//        alert(instanceGeneratorsNames)
        fillInstanceCombos();
    });
}

function refreshCombos(updatedXpath, self) {

    var pathFound = false;
    $('.edit').find('input.select2').each(function(index) { //Needs refining!
        var xpath = $(this).attr('data-xpath');
        if (updatedXpath === xpath) {
            pathFound = true;
            if (self) {
                fillCombo($(this), true);
            }
        } else {
            if (pathFound === true) {
                fillCombo($(this), false);
            }
        }
        console.log(updatedXpath + " vs " + xpath + " PATH FOUND:" + pathFound);
    });
}



function fillCombo($this, setValue) {

    var xpath = $this.attr('data-xpath');
    if (typeof $this.attr("data-xpath") !== 'undefined') {

        var url;
        if (xpath.indexOf("/source_relation") !== -1 || xpath.indexOf("/source_node") !== -1) {
            fillXMLSchemaCombo($this, "source");
        } else if (targetType === "xml") {
            fillXMLSchemaCombo($this, "target");
        } else {
            url = 'GetListValues?id=' + id + '&xpath=' + xpath + '&targetAnalyzer=' + comboAPI;
            $.ajax({
                url: url,
                dataType: 'json'
            }).success(function(data) {
                json = data;
                if (setValue) {
                    var oldValue = $this.val().trim();
                    var wrongValue = false;
                    if (JSON.stringify(json).indexOf('"' + oldValue + '"') === -1) {
                        wrongValue = true;
                    }
                    $this.select2({
                        allowClear: true,
                        placeholder: "Select a value",
                        data: json,
                        initSelection: function(element, callback) {
                            var data = {id: $this.attr("data-id"), text: $this.val()};
                            callback(data);
                        }
                    });

                    if (wrongValue) {
                        $this.parent().find(".select2-chosen").html("<span style='color:red;'>Value " + oldValue + " is no longer valid!</span>");
                    } else {
                        $this.select2('val', $this.val());
                    }

                } else {

                    var oldValue = $this.select2('val');
                    if (typeof oldValue === 'object') {
                        $this.select2({
                            allowClear: true,
                            placeholder: "Select a value",
                            data: json
                        });
                        oldValue = $this.parent().find(".select2-chosen").html();
                        if (JSON.stringify(json).indexOf('"' + oldValue + '"') === -1) {
                            if (oldValue.indexOf(":") !== -1) {
                                oldValue = oldValue.substring(oldValue.indexOf(':') + 1);
                            }
                            $this.parent().find(".select2-chosen").html("<span style='color:red;'>Value " + oldValue + " is no longer valid!</span>");
                        }
                        $("div[title='" + xpath + "']").first().remove();
                    } else {
                        $this.select2({
                            allowClear: true,
                            placeholder: "Select a value",
                            data: json
                        });

                        if ($this.parent().find(".select2-chosen").html() === "&nbsp;") {
                            $this.parent().find(".select2-chosen").html("<span style='color:red;'>Value " + oldValue.substring(oldValue.indexOf(':') + 1) + " is no longer valid!</span>");
                        }
                    }
                }
                $(".loader").hide();
            });
        }

    }

}

function fillInstanceCombos(selector) {
    var $selector;
    if (typeof selector === 'undefined') {
        $selector = $(".select2");
    } else {
        $selector = $(selector).find(".select2");
    }

    $selector.each(function() {

        var $this = $(this);



        var oldValue = $this.val().trim();
        var wrongValue = false;
        var data = instanceGeneratorsNames;
        if ($this.attr('title') === 'Argument type') {
            data = [{id: "", text: ""}, {id: "xpath", text: "xpath"}, {id: "xpathPosition", text: "xpathPosition"}, {id: "constant", text: 'constant'}, {id: "position", text: 'position'}]; //type values
        } else {
            if (JSON.stringify(instanceGeneratorsNames).indexOf('"' + oldValue + '"') === -1) {
                wrongValue = true;
            }
        }

        $this.select2({
            allowClear: true,
            placeholder: "Select a value",
            createSearchChoice: function(term, data) {
                if ($(data).filter(function() {
                    return this.text.localeCompare(term) === 0;
                }).length === 0) {
                    return {
                        id: term,
                        text: term
                    };
                }
            },
            data: data,
            initSelection: function(element, callback) {
                var data = {id: $this.attr("data-id"), text: $this.val()};
                callback(data);
            }
        });

        if (wrongValue) {
            $this.parent().find(".select2-chosen").html("<span style='color:red;'>Generator " + oldValue + " does not exist!</span>");
        } else {
            $this.select2('val', $this.val());
        }
        $this.next(".loader").hide();

    });

}


function fillXMLSchemaCombo($this, type) {

    if (type === "source") {
        if (sourceAnalyzerPaths.length === 0) {
            var url = '/xPaths/filePathService';
            var sourceAnalyzerFile = "";
            if (sourceAnalyzerFiles.indexOf("***") !== -1) { //Choose file to get xpaths from
                var files = sourceAnalyzerFiles.split("***");
                var schemaFile = files[0];
                var instanceFile = files[1];
                if (schemaFile.length > 0) {
                    sourceAnalyzerFile = schemaFile;
                } else {
                    if (instanceFile.length > 0) {
                        sourceAnalyzerFile = instanceFile;
                    }
                }
            }
            if (sourceAnalyzer === "on") {
                if (sourceAnalyzerFile.endsWith(".xsd")) {
                    sourceAnalyzerFile = "../xml_schema/" + sourceAnalyzerFile;
                }
                $.post(url, {fileName: sourceAnalyzerFile}, function(data) {
                    sourceAnalyzerPaths = data.results;
                    fillComboWithPaths($this, sourceAnalyzerPaths);
                },
                        "json").error(function(xhr) {
                    var error = JSON.parse(xhr.responseText).error;
                    alert("Error reading source schema or source xml: " + error + ".\nPlease disable Source Analyzer (Configuration tab) to fill in source values.");
                });
            }
        } else {
            fillComboWithPaths($this, sourceAnalyzerPaths);
        }
    } else {

        if (targetXPaths.length === 0) {
            var url = '/xPaths/filePathService';
            var targetFile = "";
            if (targetFiles.indexOf("***") !== -1) { //Choose file to get xpaths from
                var files = targetFiles.split("***");
                targetFile = files[0]; //get first XSD for now

            } else {
                targetFile = targetFiles;//get first XSD for now
            }
            if (targetFile.endsWith(".xsd")) {
                targetFile = "../xml_schema/" + targetFile;
            }
            $.post(url, {fileName: targetFile, root: targetRoot}, function(data) {
                targetXPaths = data.results;
                fillComboWithPaths($this, targetXPaths);
            },
                    "json").error(function(xhr) {
                var error = JSON.parse(xhr.responseText).error;
                alert("Error reading target schema or target xml: " + error + ".\nPlease set Target Analyzer (Configuration tab) to value 'None'");
            });
        } else {
            fillComboWithPaths($this, targetXPaths);
        }
    }

    $(".loader").hide();
}

function fillComboWithPaths($this, filteredPaths) {
    var xpath = $this.attr("data-xpath");

    if (!xpath.endsWith("domain/source_node") && xpath.indexOf("/target_") === -1) { //Apply filtering only for source link (path or range) combos
        filteredPaths = filterValues(xpath);
    } else if (targetType === "xml" && xpath.indexOf("domain/") === -1) {
        filteredPaths = filterValues(xpath);
    }
    $this.select2({
        allowClear: true,
        placeholder: "Select a value",
        createSearchChoice: function(term, data) {
            if ($(data).filter(function() {
                return this.text.localeCompare(term) === 0;
            }).length === 0) {
                return {
                    id: term,
                    text: term
                };
            }
        },
        data: filteredPaths,
        initSelection: function(element, callback) {
            var data = {id: $this.attr("data-id"), text: $this.val()};

            callback(data);
        }
    });

}
function getDomainValueForLink(xpath) {
    var domainPath = xpath.replace(/\/link[\s\S]*/g, "/domain");
    var $domain = $("tr[data-xpath='" + domainPath + "']");
    var domainValue;

    if (targetType === "xml" && xpath.indexOf("/target_") !== -1) {
        var $domainDiv = $domain.find(".targetPath").first();
        domainValue = $domainDiv.attr("data-fullpath");
        ;


    } else {
        var $domainDiv = $domain.find(".nextToIcon");

        domainValue = $domainDiv.html();
        if ($domainDiv.children("span").length > 0) {
            domainValue = $domainDiv.children("span").attr("title");
        }
    }
//    alert(domainValue)
    return domainValue;
}
function filterValues(xpath) {

    var domainValue = getDomainValueForLink(xpath);

    var paths;
    if (targetType === "xml" && xpath.indexOf("/target_") !== -1) {
        paths = targetXPaths;
    } else {
        paths = sourceAnalyzerPaths;
    }
    var filteredPaths;
    if (!domainValue.startsWith("/")) { //If xpath does not start with a "/", add it
        domainValue = "/" + domainValue;
        filteredPaths = $.map(paths, function(item) {
            var id = item.id;
            if (id.startsWith(domainValue + "/")) {
                var strippedItem = {id: item.id.substring(domainValue.length + 1), text: item.text.substring(domainValue.length + 1)};
                return strippedItem;
            }
        });
    } else if (domainValue.startsWith("//")) {//If xpath starts with a "//"
        domainValue = domainValue.substring(2);
        filteredPaths = $.map(paths, function(item) {
            var id = item.id;
            if (id.indexOf(domainValue + "/") !== -1) {
                var strippedItem = {id: item.id.substring(id.indexOf(domainValue + "/") + domainValue.length+1), text: item.text.substring(id.indexOf(domainValue + "/") + domainValue.length+1)};
                return strippedItem;
            }
        });
    } else {
        filteredPaths = $.map(paths, function(item) {//If xpath starts with a "/"
            var id = item.id;
            if (id.startsWith(domainValue + "/")) {
                var strippedItem = {id: item.id.substring(domainValue.length + 1), text: item.text.substring(domainValue.length + 1)};
                return strippedItem;
            }
        });
    }



    return filteredPaths;

}

function fillCombos() {
    $(".edit").find('.select2').each(function(index) {
        fillCombo($(this), true);
    });
}


