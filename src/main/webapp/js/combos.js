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
 * Scripts used for combo filling
 */

     
var languages = [{id: "", text: ""},
{id: "ab", text: "ab"},
{id: "aa", text: "aa"},
{id: "af", text: "af"},
{id: "ak", text: "ak"},
{id: "sq", text: "sq"},
{id: "am", text: "am"},
{id: "ar", text: "ar"},
{id: "an", text: "an"},
{id: "hy", text: "hy"},
{id: "as", text: "as"},
{id: "av", text: "av"},
{id: "ae", text: "ae"},
{id: "ay", text: "ay"},
{id: "az", text: "az"},
{id: "bm", text: "bm"},
{id: "ba", text: "ba"},
{id: "eu", text: "eu"},
{id: "be", text: "be"},
{id: "bn", text: "bn"},
{id: "bh", text: "bh"},
{id: "bi", text: "bi"},
{id: "bs", text: "bs"},
{id: "br", text: "br"},
{id: "bg", text: "bg"},
{id: "my", text: "my"},
{id: "ca", text: "ca"},
{id: "ch", text: "ch"},
{id: "ce", text: "ce"},
{id: "ny", text: "ny"},
{id: "zh", text: "zh"},
{id: "cv", text: "cv"},
{id: "kw", text: "kw"},
{id: "co", text: "co"},
{id: "cr", text: "cr"},
{id: "hr", text: "hr"},
{id: "cs", text: "cs"},
{id: "da", text: "da"},
{id: "dv", text: "dv"},
{id: "nl", text: "nl"},
{id: "dz", text: "dz"},
{id: "en", text: "en"},
{id: "eo", text: "eo"},
{id: "et", text: "et"},
{id: "ee", text: "ee"},
{id: "fo", text: "fo"},
{id: "fj", text: "fj"},
{id: "fi", text: "fi"},
{id: "fr", text: "fr"},
{id: "ff", text: "ff"},
{id: "gl", text: "gl"},
{id: "ka", text: "ka"},
{id: "de", text: "de"},
{id: "el", text: "el"},
{id: "gn", text: "gn"},
{id: "gu", text: "gu"},
{id: "ht", text: "ht"},
{id: "ha", text: "ha"},
{id: "he", text: "he"},
{id: "hz", text: "hz"},
{id: "hi", text: "hi"},
{id: "ho", text: "ho"},
{id: "hu", text: "hu"},
{id: "ia", text: "ia"},
{id: "id", text: "id"},
{id: "ie", text: "ie"},
{id: "ga", text: "ga"},
{id: "ig", text: "ig"},
{id: "ik", text: "ik"},
{id: "io", text: "io"},
{id: "is", text: "is"},
{id: "it", text: "it"},
{id: "iu", text: "iu"},
{id: "ja", text: "ja"},
{id: "jv", text: "jv"},
{id: "kl", text: "kl"},
{id: "kn", text: "kn"},
{id: "kr", text: "kr"},
{id: "ks", text: "ks"},
{id: "kk", text: "kk"},
{id: "km", text: "km"},
{id: "ki", text: "ki"},
{id: "rw", text: "rw"},
{id: "ky", text: "ky"},
{id: "kv", text: "kv"},
{id: "kg", text: "kg"},
{id: "ko", text: "ko"},
{id: "ku", text: "ku"},
{id: "kj", text: "kj"},
{id: "la", text: "la"},
{id: "lb", text: "lb"},
{id: "lg", text: "lg"},
{id: "li", text: "li"},
{id: "ln", text: "ln"},
{id: "lo", text: "lo"},
{id: "lt", text: "lt"},
{id: "lu", text: "lu"},
{id: "lv", text: "lv"},
{id: "gv", text: "gv"},
{id: "mk", text: "mk"},
{id: "mg", text: "mg"},
{id: "ms", text: "ms"},
{id: "ml", text: "ml"},
{id: "mt", text: "mt"},
{id: "mi", text: "mi"},
{id: "mr", text: "mr"},
{id: "mh", text: "mh"},
{id: "mn", text: "mn"},
{id: "na", text: "na"},
{id: "nv", text: "nv"},
{id: "nb", text: "nb"},
{id: "nd", text: "nd"},
{id: "ne", text: "ne"},
{id: "ng", text: "ng"},
{id: "nn", text: "nn"},
{id: "no", text: "no"},
{id: "ii", text: "ii"},
{id: "nr", text: "nr"},
{id: "oc", text: "oc"},
{id: "oj", text: "oj"},
{id: "cu", text: "cu"},
{id: "om", text: "om"},
{id: "or", text: "or"},
{id: "os", text: "os"},
{id: "pa", text: "pa"},
{id: "pi", text: "pi"},
{id: "fa", text: "fa"},
{id: "pl", text: "pl"},
{id: "ps", text: "ps"},
{id: "pt", text: "pt"},
{id: "qu", text: "qu"},
{id: "rm", text: "rm"},
{id: "rn", text: "rn"},
{id: "ro", text: "ro"},
{id: "ru", text: "ru"},
{id: "sa", text: "sa"},
{id: "sc", text: "sc"},
{id: "sd", text: "sd"},
{id: "se", text: "se"},
{id: "sm", text: "sm"},
{id: "sg", text: "sg"},
{id: "sr", text: "sr"},
{id: "gd", text: "gd"},
{id: "sn", text: "sn"},
{id: "si", text: "si"},
{id: "sk", text: "sk"},
{id: "sl", text: "sl"},
{id: "so", text: "so"},
{id: "st", text: "st"},
{id: "az", text: "az"},
{id: "es", text: "es"},
{id: "su", text: "su"},
{id: "sw", text: "sw"},
{id: "ss", text: "ss"},
{id: "sv", text: "sv"},
{id: "ta", text: "ta"},
{id: "te", text: "te"},
{id: "tg", text: "tg"},
{id: "th", text: "th"},
{id: "ti", text: "ti"},
{id: "bo", text: "bo"},
{id: "tk", text: "tk"},
{id: "tl", text: "tl"},
{id: "tn", text: "tn"},
{id: "to", text: "to"},
{id: "tr", text: "tr"},
{id: "ts", text: "ts"},
{id: "tt", text: "tt"},
{id: "tw", text: "tw"},
{id: "ty", text: "ty"},
{id: "ug", text: "ug"},
{id: "uk", text: "uk"},
{id: "ur", text: "ur"},
{id: "uz", text: "uz"},
{id: "ve", text: "ve"},
{id: "vi", text: "vi"},
{id: "vo", text: "vo"},
{id: "wa", text: "wa"},
{id: "cy", text: "cy"},
{id: "wo", text: "wo"},
{id: "fy", text: "fy"},
{id: "xh", text: "xh"},
{id: "yi", text: "yi"},
{id: "yo", text: "yo"},
{id: "za", text: "za"},
{id: "zu", text: "zu"}
]

function getInstanceGeneratorNamesAndFillCombos() {
    var url = "Services?id=" + id + "&method=instanceGeneratorNames";
    var req = $.myPOST(url, "", "json");
    req.done(function(data) {
        checkResponse(data);

//    $.ajax({
//        url: url,
//        dataType: 'json'
//    }).success(function(data) {
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
//    alert(xpath)

    if (typeof $this.attr("data-xpath") !== 'undefined') {

        var url;
        if (xpath.indexOf("/source_relation") !== -1 || xpath.indexOf("/source_node") !== -1) {
            fillXMLSchemaCombo($this, "source");
        } else if (xpath.indexOf("/instance_info/language")!==-1) {
            fillInstanceInfoCombo($this);
        } else if (targetType === "xml") {
            fillXMLSchemaCombo($this, "target");
        } else {
            url = 'GetListValues?id=' + id + '&xpath=' + xpath + '&targetAnalyzer=' + comboAPI;
            var req = $.myPOST(url, "", "json");
            req.done(function(data) {
                checkResponse(data);

                json = data;
                if (setValue) {
                    var oldValue = $this.val().trim();
                    var wrongValue = false;

                    if (JSON.stringify(json).indexOf(oldValue) === -1) {
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
                        if (JSON.stringify(json).indexOf(oldValue) === -1) {
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
            req.fail(function(jqXHR) {

                var responseText = jqXHR.responseText;
                console.log(responseText);
                var message = $(responseText).find("li").last().html();

                alert("Problem occurred! Read following exception message and either try again or choose a different reasoner:\n" + message);
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
function fillInstanceInfoCombo($this) {

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
        data: languages,
        initSelection: function(element, callback) {
            var data = {id: $this.attr("data-id"), text: $this.val()};
            callback(data);
        }
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
                var req = $.myPOST(url, {fileName: sourceAnalyzerFile, root: sourceRoot}, "json");
                req.done(function(data) {
                    checkResponse(data);

                    sourceAnalyzerPaths = data.results;
                    fillComboWithPaths($this, sourceAnalyzerPaths);
                });
                req.fail(function(xhr) {
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
            var req = $.myPOST(url, {fileName: targetFile, root: targetRoot}, "json");
            req.done(function(data) {
                checkResponse(data);

                targetXPaths = data.results;
                fillComboWithPaths($this, targetXPaths);
            });
            req.fail(function(xhr) {
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
//alert(xpath)
    var domainValue = getDomainValueForLink(xpath);


//alert(domainValue)
    var paths;
    if (targetType === "xml" && xpath.indexOf("/target_") !== -1) {
        paths = targetXPaths;
    } else {
        paths = sourceAnalyzerPaths;
    }
    if (typeof domainValue === 'undefined') { //New map case, no need to filter
        return paths;
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
                var strippedItem = {id: item.id.substring(id.indexOf(domainValue + "/") + domainValue.length + 1), text: item.text.substring(id.indexOf(domainValue + "/") + domainValue.length + 1)};
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


