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
 * Batch delete method (either maps or links) used by right click menu
 */
function batchDelete(type) {
    //First make any editable rows view-only for uniformity
    viewOnly();

    confirmDialog();
    if (goAhead) {

        var selected;
        if (type === "map") {
            selected = selectedMaps;
        } else {
            selected = selectedRows;
        }
        var url = "Delete?id=" + id + "&selected=" + selected + '&targetAnalyzer=' + comboAPI;
        var req = $.myPOST(url);
        req.done(function(data) {
            checkResponse(data);
            $(".mappings").html(data);
            $(".empty>td>div").attr("style", "display:block"); //Showing otherwise hidden add buttons


            if (type === "map") {
                console.log("delete maps " + selected);

            } else if (type === "link") {
                console.log("delete links " + selected);
            }
        });

    }

}


/*
 * Delete method (either map or link) used by right click menu
 */
function deleteBlock(type, xpath) {
    //First make any editable rows view-only for uniformity
    viewOnly();

    confirmDialog();
    if (goAhead) {

        var url = "Delete?id=" + id + "&xpath=" + xpath + '&targetAnalyzer=' + comboAPI;
        var req = $.myPOST(url);
        req.done(function(data) {
            checkResponse(data);

            if (type === "map") {
                console.log("delete map with xpath " + xpath);
                var $map = $("tbody.mapping[id='" + xpath + "']");
                //Updating following mappings
                updateFollowingSiblingsOnDelete($map, "tbody")

                //Deleting html
                $map.prev().remove();
                $map.remove();
            } else if (type === "link") {
                console.log("delete link with xpath " + xpath);
                var $link = $("tr[data-xpath='" + xpath + "']");
                //Updating following links
                updateFollowingSiblingsOnDelete($link, ".path, .range");

                //Deleting html
                $link.remove();

            }
        });

    }

}
