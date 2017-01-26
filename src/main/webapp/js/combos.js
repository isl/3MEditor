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
 * Scripts used for combo filling
 */


var languages = [{id: "ab", text: "Abkhaz (ab)"},
    {id: "aa", text: "Afar (aa)"},
    {id: "af", text: "Afrikaans (af)"},
    {id: "ak", text: "Akan (ak)"},
    {id: "sq", text: "Albanian (sq)"},
    {id: "am", text: "Amharic (am)"},
    {id: "ar", text: "Arabic (ar)"},
    {id: "an", text: "Aragonese (an)"},
    {id: "hy", text: "Armenian (hy)"},
    {id: "as", text: "Assamese (as)"},
    {id: "av", text: "Avaric (av)"},
    {id: "ae", text: "Avestan (ae)"},
    {id: "ay", text: "Aymara (ay)"},
    {id: "az", text: "Azerbaijani (az)"},
    {id: "bm", text: "Bambara (bm)"},
    {id: "ba", text: "Bashkir (ba)"},
    {id: "eu", text: "Basque (eu)"},
    {id: "be", text: "Belarusian (be)"},
    {id: "bn", text: "Bengali, Bangla (bn)"},
    {id: "bh", text: "Bihari (bh)"},
    {id: "bi", text: "Bislama (bi)"},
    {id: "bs", text: "Bosnian (bs)"},
    {id: "br", text: "Breton (br)"},
    {id: "bg", text: "Bulgarian (bg)"},
    {id: "my", text: "Burmese (my)"},
    {id: "ca", text: "Catalan, Valencian (ca)"},
    {id: "ch", text: "Chamorro (ch)"},
    {id: "ce", text: "Chechen (ce)"},
    {id: "ny", text: "Chichewa, Chewa, Nyanja (ny)"},
    {id: "zh", text: "Chinese (zh)"},
    {id: "cv", text: "Chuvash (cv)"},
    {id: "kw", text: "Cornish (kw)"},
    {id: "co", text: "Corsican (co)"},
    {id: "cr", text: "Cree (cr)"},
    {id: "hr", text: "Croatian (hr)"},
    {id: "cs", text: "Czech (cs)"},
    {id: "da", text: "Danish (da)"},
    {id: "dv", text: "Divehi, Dhivehi, Maldivian (dv)"},
    {id: "nl", text: "Dutch (nl)"},
    {id: "dz", text: "Dzongkha (dz)"},
    {id: "en", text: "English (en)"},
    {id: "eo", text: "Esperanto (eo)"},
    {id: "et", text: "Estonian (et)"},
    {id: "ee", text: "Ewe (ee)"},
    {id: "fo", text: "Faroese (fo)"},
    {id: "fj", text: "Fijian (fj)"},
    {id: "fi", text: "Finnish (fi)"},
    {id: "fr", text: "French (fr)"},
    {id: "ff", text: "Fula, Fulah, Pulaar, Pular (ff)"},
    {id: "gl", text: "Galician (gl)"},
    {id: "ka", text: "Georgian (ka)"},
    {id: "de", text: "German (de)"},
    {id: "el", text: "Greek, Modern (el)"},
    {id: "gn", text: "Guaran? (gn)"},
    {id: "gu", text: "Gujarati (gu)"},
    {id: "ht", text: "Haitian, Haitian Creole (ht)"},
    {id: "ha", text: "Hausa (ha)"},
    {id: "he", text: "Hebrew (modern) (he)"},
    {id: "hz", text: "Herero (hz)"},
    {id: "hi", text: "Hindi (hi)"},
    {id: "ho", text: "Hiri Motu (ho)"},
    {id: "hu", text: "Hungarian (hu)"},
    {id: "ia", text: "Interlingua (ia)"},
    {id: "id", text: "Indonesian (id)"},
    {id: "ie", text: "Interlingue (ie)"},
    {id: "ga", text: "Irish (ga)"},
    {id: "ig", text: "Igbo (ig)"},
    {id: "ik", text: "Inupiaq (ik)"},
    {id: "io", text: "Ido (io)"},
    {id: "is", text: "Icelandic (is)"},
    {id: "it", text: "Italian (it)"},
    {id: "iu", text: "Inuktitut (iu)"},
    {id: "ja", text: "Japanese (ja)"},
    {id: "jv", text: "Javanese (jv)"},
    {id: "kl", text: "Kalaallisut, Greenlandic (kl)"},
    {id: "kn", text: "Kannada (kn)"},
    {id: "kr", text: "Kanuri (kr)"},
    {id: "ks", text: "Kashmiri (ks)"},
    {id: "kk", text: "Kazakh (kk)"},
    {id: "km", text: "Khmer (km)"},
    {id: "ki", text: "Kikuyu, Gikuyu (ki)"},
    {id: "rw", text: "Kinyarwanda (rw)"},
    {id: "ky", text: "Kyrgyz (ky)"},
    {id: "kv", text: "Komi (kv)"},
    {id: "kg", text: "Kongo (kg)"},
    {id: "ko", text: "Korean (ko)"},
    {id: "ku", text: "Kurdish (ku)"},
    {id: "kj", text: "Kwanyama, Kuanyama (kj)"},
    {id: "la", text: "Latin (la)"},
    {id: "lb", text: "Luxembourgish, Letzeburgesch (lb)"},
    {id: "lg", text: "Ganda (lg)"},
    {id: "li", text: "Limburgish, Limburgan, Limburger (li)"},
    {id: "ln", text: "Lingala (ln)"},
    {id: "lo", text: "Lao (lo)"},
    {id: "lt", text: "Lithuanian (lt)"},
    {id: "lu", text: "Luba-Katanga (lu)"},
    {id: "lv", text: "Latvian (lv)"},
    {id: "gv", text: "Manx (gv)"},
    {id: "mk", text: "Macedonian (mk)"},
    {id: "mg", text: "Malagasy (mg)"},
    {id: "ms", text: "Malay (ms)"},
    {id: "ml", text: "Malayalam (ml)"},
    {id: "mt", text: "Maltese (mt)"},
    {id: "mi", text: "M?ori (mi)"},
    {id: "mr", text: "Marathi (Mar??h?) (mr)"},
    {id: "mh", text: "Marshallese (mh)"},
    {id: "mn", text: "Mongolian (mn)"},
    {id: "na", text: "Nauru (na)"},
    {id: "nv", text: "Navajo, Navaho (nv)"},
    {id: "nb", text: "Norwegian Bokm?l (nb)"},
    {id: "nd", text: "North Ndebele (nd)"},
    {id: "ne", text: "Nepali (ne)"},
    {id: "ng", text: "Ndonga (ng)"},
    {id: "nn", text: "Norwegian Nynorsk (nn)"},
    {id: "no", text: "Norwegian (no)"},
    {id: "ii", text: "Nuosu (ii)"},
    {id: "nr", text: "South Ndebele (nr)"},
    {id: "oc", text: "Occitan (oc)"},
    {id: "oj", text: "Ojibwe, Ojibwa (oj)"},
    {id: "cu", text: "Old Church Slavonic, Church Slavonic, Old Bulgarian (cu)"},
    {id: "om", text: "Oromo (om)"},
    {id: "or", text: "Oriya (or)"},
    {id: "os", text: "Ossetian, Ossetic (os)"},
    {id: "pa", text: "Panjabi, Punjabi (pa)"},
    {id: "pi", text: "P?li (pi)"},
    {id: "fa", text: "Persian (Farsi) (fa)"},
    {id: "pl", text: "Polish (pl)"},
    {id: "ps", text: "Pashto, Pushto (ps)"},
    {id: "pt", text: "Portuguese (pt)"},
    {id: "qu", text: "Quechua (qu)"},
    {id: "rm", text: "Romansh (rm)"},
    {id: "rn", text: "Kirundi (rn)"},
    {id: "ro", text: "Romanian (ro)"},
    {id: "ru", text: "Russian (ru)"},
    {id: "sa", text: "Sanskrit (Sa?sk?ta) (sa)"},
    {id: "sc", text: "Sardinian (sc)"},
    {id: "sd", text: "Sindhi (sd)"},
    {id: "se", text: "Northern Sami (se)"},
    {id: "sm", text: "Samoan (sm)"},
    {id: "sg", text: "Sango (sg)"},
    {id: "sr", text: "Serbian (sr)"},
    {id: "gd", text: "Scottish Gaelic, Gaelic (gd)"},
    {id: "sn", text: "Shona (sn)"},
    {id: "si", text: "Sinhala, Sinhalese (si)"},
    {id: "sk", text: "Slovak (sk)"},
    {id: "sl", text: "Slovene (sl)"},
    {id: "so", text: "Somali (so)"},
    {id: "st", text: "Southern Sotho (st)"},
    {id: "az", text: "South Azerbaijani (az)"},
    {id: "es", text: "Spanish, Castilian (es)"},
    {id: "su", text: "Sundanese (su)"},
    {id: "sw", text: "Swahili (sw)"},
    {id: "ss", text: "Swati (ss)"},
    {id: "sv", text: "Swedish (sv)"},
    {id: "ta", text: "Tamil (ta)"},
    {id: "te", text: "Telugu (te)"},
    {id: "tg", text: "Tajik (tg)"},
    {id: "th", text: "Thai (th)"},
    {id: "ti", text: "Tigrinya (ti)"},
    {id: "bo", text: "Tibetan Standard, Tibetan, Central (bo)"},
    {id: "tk", text: "Turkmen (tk)"},
    {id: "tl", text: "Tagalog (tl)"},
    {id: "tn", text: "Tswana (tn)"},
    {id: "to", text: "Tonga (Tonga Islands) (to)"},
    {id: "tr", text: "Turkish (tr)"},
    {id: "ts", text: "Tsonga (ts)"},
    {id: "tt", text: "Tatar (tt)"},
    {id: "tw", text: "Twi (tw)"},
    {id: "ty", text: "Tahitian (ty)"},
    {id: "ug", text: "Uyghur, Uighur (ug)"},
    {id: "uk", text: "Ukrainian (uk)"},
    {id: "ur", text: "Urdu (ur)"},
    {id: "uz", text: "Uzbek (uz)"},
    {id: "ve", text: "Venda (ve)"},
    {id: "vi", text: "Vietnamese (vi)"},
    {id: "vo", text: "Volap?k (vo)"},
    {id: "wa", text: "Walloon (wa)"},
    {id: "cy", text: "Welsh (cy)"},
    {id: "wo", text: "Wolof (wo)"},
    {id: "fy", text: "Western Frisian (fy)"},
    {id: "xh", text: "Xhosa (xh)"},
    {id: "yi", text: "Yiddish (yi)"},
    {id: "yo", text: "Yoruba (yo)"},
    {id: "za", text: "Zhuang, Chuang (za)"},
    {id: "zu", text: "Zulu (zu)"}
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
        } else if (xpath.indexOf("/instance_info/language") !== -1) {
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
    $this.select2('val', $this.val());
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


