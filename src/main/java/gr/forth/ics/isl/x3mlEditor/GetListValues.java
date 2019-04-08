/*
 * Copyright 2014-2019  Institute of Computer Science,
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
package gr.forth.ics.isl.x3mlEditor;

import isl.dbms.DBCollection;
import isl.dbms.DBFile;
import isl.reasoner.OntologyReasoner;
import static gr.forth.ics.isl.x3mlEditor.BasicServlet.applicationCollection;
import gr.forth.ics.isl.x3mlEditor.utilities.Schema;
import gr.forth.ics.isl.x3mlEditor.utilities.SchemaAnalyzer;
import gr.forth.ics.isl.x3mlEditor.utilities.Utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author samarita
 */
public class GetListValues extends BasicServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        servletParams(request, response);
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");
        String type = request.getParameter("type");
        String xpath = request.getParameter("xpath");
        String targetAnalyzer = request.getParameter("targetAnalyzer");

        int targetMode;
        if (targetAnalyzer == null) {
            targetAnalyzer = "2";
        }
        targetMode = Integer.parseInt(targetAnalyzer);

        if (type == null) {
            type = "Mapping";
        }

        String xmlId = type + id + ".xml";
        DBCollection dbc = new DBCollection(super.DBURI, applicationCollection + "/" + type, super.DBuser, super.DBpassword);
        String collectionPath = getPathforFile(dbc, xmlId, id);
        DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);

        String[] values = mappingFile.queryString(xpath + "/string()");
        String currentValue = "";
        if (values.length > 0) {
            currentValue = values[0];
        }

        String[] targetSchemas = mappingFile.queryString("for $i in //target_info/target_schema\n"
                + "return\n"
                + "if ($i/@schema_file) \n"
                + "then $i/@schema_file/string()\n"
                + "else \n"
                + "\"\"");

        ArrayList<String> resultsList = new ArrayList<String>();
        String[] results;

        String output = "";
        if (targetSchemas.length > 0) {

            if (targetMode == 2) {

                SchemaAnalyzer analyzer = new SchemaAnalyzer(mappingFile);

                HttpSession session = sessionCheck(request, response);
                if (session == null) {
                    session = request.getSession();
                }

                results = getListValues(mappingFile, xpath, targetSchemas, session);
                resultsList = new ArrayList(Arrays.asList(results));
                if (resultsList.size() > 0) {
                    output = tableToJSON(resultsList, analyzer);
//                    output = tableToJSON(resultsList, filenameAndType, filenameAndPrefix, filenameAndURI);
                }
         
            } else if (targetMode == 3) {//
                
                HttpSession session = sessionCheck(request, response);
                if (session == null) {
                    session = request.getSession();

                }
                OntologyReasoner ont = (OntologyReasoner) session.getAttribute("modelInstance_" + id);

                if (ont == null) {
                    ont = getOntModel(mappingFile, id);
                    session.setAttribute("modelInstance_" + id, ont);
                }

                resultsList = getListValues(mappingFile, xpath, ont);
            }
        }

        if (resultsList != null) {
            if (targetMode != 2) {
                if (resultsList.size() > 0) {
                    if (resultsList.size() == 1 && resultsList.get(0).startsWith("Error:")) {
                        output = resultsList.get(0);
                    } else {
                        String[] prefixes = mappingFile.queryString("//target_info//namespace/@prefix/string()");
                        String[] uris = mappingFile.queryString("//target_info//namespace/@uri/string()");
                        HashMap<String, String> prefixAndURI = new HashMap<String, String>();

                        for (int i = 0; i < prefixes.length; i++) {
                            prefixAndURI.put(uris[i], prefixes[i]);

                        }
                        output = tableToJSON(resultsList, prefixAndURI);
                    }
                }
            }
            if (output.startsWith("Error:")) {
                out.println(output);
            } else {
                out.println("{\n"
                        + "    \"results\": [\n"
                        + "            { \"id\": \"\", \"text\": \"\" }");//Adding empty default
                if (!output.equals("")) {
                    out.println(",\n" + output);
                }

                out.println("]\n"
                        + "}");
            }

        } else {
            out.println("Reasoner did not work properly and generated resultsList was null! Try a different reasoner or disable it completely.");
        }
        out.close();

    }

    //MODE 3
    private ArrayList<String> getListValues(DBFile mappingFile, String xpath, OntologyReasoner ont) {
        ArrayList<String> resultsList = null;
        if ((xpath.contains("/domain/target_node/entity[") && !xpath.contains("/relationship")) || ((xpath.contains("type[") && !xpath.contains("type[1]")))) {
            try {
                resultsList = ont.getAllClasses();
            } catch (Exception ex) {
                System.out.println("JENA REASONER MALFUNCTION");
                ex.printStackTrace();
            }
        } else if (xpath.contains("/relationship")) {
            xpath = xpathResolver(xpath, "relationship");

            String[] selectedEntities = mappingFile.queryString(xpath);
            if (selectedEntities.length > 0) {
                if (selectedEntities.length == 1 && selectedEntities[0].equals("")) {
                    resultsList = new ArrayList<String>();
                } else {
                    for (String selection : selectedEntities) {
                        selection = replacePrefixWithURI(mappingFile, selection);
                        ArrayList<String> propResults = new ArrayList<String>();
                        try {
                            propResults = ont.listProperties(selection);
                        } catch (Exception ex) {
                            propResults = new ArrayList<String>();
                            propResults.add("Error:" + ex.getMessage());
                            ex.printStackTrace();
                        }                       
                        if (resultsList != null) {//First remove duplicates
                            resultsList.removeAll(propResults);
                            resultsList.addAll(propResults); //Then merge
                        } else {
                            resultsList = propResults;
                        }
                    }
                }
            } else {
                resultsList = new ArrayList<String>();
            }

        } else if (xpath.contains("/entity")) {
            xpath = xpathResolver(xpath, "entity");

            String[] selectedProperty = mappingFile.queryString(xpath);
            if (selectedProperty.length == 0 && xpath.contains("internal_node")) { //No internal_node in P row
                selectedProperty = mappingFile.queryString(xpath.replace("internal_node[last()]/", ""));
            }
            if (selectedProperty.length > 0) {
                if (selectedProperty.length == 1 && selectedProperty[0].equals("")) {
                    resultsList = new ArrayList<String>();

                } else {
                    String path = selectedProperty[0];
                    path = replacePrefixWithURI(mappingFile, path);
                    try {
                        resultsList = ont.listObjects(path);
                    } catch (Exception ex) {
                        resultsList = new ArrayList<String>();
                        resultsList.add("Error:" + ex.getMessage());
                        ex.printStackTrace();
                    }

                }
            } else {
                resultsList = new ArrayList<String>();

            }

        } else {
            try {
                resultsList = ont.getAllClasses();
            } catch (Exception ex) {
                resultsList = new ArrayList<String>();
                resultsList.add("Error:" + ex.getMessage());
                ex.printStackTrace();
            }
        }
        if (resultsList != null) {
            resultsList = new Utils().sort(resultsList); //Use custom order
        }
        return resultsList;
    }

    private String replacePrefixWithURI(DBFile mappingFile, String selection) {
        if (selection.contains(":")) {
            String prefix = selection.split(":")[0];
            String[] uris = mappingFile.queryString("//namespace[@prefix='" + prefix + "']/@uri/string()");
            if (uris.length > 0) {
                selection = selection.replaceFirst(prefix + ":", uris[0]);
            }
        }
        return selection;
    }

    private String xpathResolver(String xpath, String type) {

        if (type.equals("relationship")) {
            if (xpath.contains("/additional[")) {
                xpath = xpath.replaceAll("/additional\\[[^$]+", "/type/string()");
            } else if (xpath.endsWith("/relationship[1]") && !xpath.contains("domain/")) {
                xpath = xpath.replaceAll("/link\\[[^$]+", "/domain/target_node/entity/type/string()");
            } else {
                xpath = xpath + "/preceding-sibling::*[1]/type/string()";
            }
        } else {
            if (xpath.contains("/additional[")) {
                xpath = xpath + "/../../relationship/string()";
            } else if (xpath.contains("/range/target_node/entity")) {
                xpath = xpath.replaceAll("/range[^$]+", "/path/target_relation/relationship[last()]/string()");
            } else if (xpath.contains("/path/target_relation")) {
                xpath = xpath + "/../preceding-sibling::*[1]/string()";
            }
        }
        return xpath;
    }

    //MODE 2
    private String[] getListValues(DBFile mappingFile, String xpath, String[] schemaFilenames, HttpSession session) {
        String currentSchemaFilename = schemaFilenames[0];
        if (schemaFilenames[0].startsWith("../")) {
            currentSchemaFilename = schemaFilenames[0].substring(3);
        }
        DBFile cidocRdfsFile = new DBFile(DBURI, x3mlCollection, currentSchemaFilename, DBuser, DBpassword);

        Schema cidocSchema = new Schema(cidocRdfsFile, schemaFilenames);
        String[] resultsList = new String[]{};

        StringBuffer schemas = new StringBuffer();
        for (String tSchema : schemaFilenames) {
            schemas.append(tSchema);
        }
        HashMap<String, String[]> resourcesList = null;
        if (session != null) {
            resourcesList = (HashMap) session.getAttribute("resourcesList");
        }

        if ((xpath.contains("/domain/target_node/entity[") && !xpath.contains("/relationship") && !xpath.contains("/additional")) || ((xpath.contains("type[") && !xpath.contains("type[1]")))) {
            resultsList = cidocSchema.getAllClasses();
            if (resourcesList == null) {
                resourcesList = new HashMap<String, String[]>();
            }
            resourcesList.put(schemas.toString() + currentSchemaFilename, resultsList);
            session.setAttribute("resourcesList", resourcesList);
        } else if (xpath.contains("/relationship")) {

            xpath = xpathResolver(xpath, "relationship");

            String[] selectedEntities = mappingFile.queryString(xpath);

            StringBuffer selEnts = new StringBuffer("ENTITIES");
            for (String selectedEntity : selectedEntities) {
                selEnts.append(selectedEntity);
            }

            if (resourcesList != null && resourcesList.containsKey(schemas.toString() + currentSchemaFilename + selEnts.toString())) {
                resultsList = resourcesList.get(schemas.toString() + currentSchemaFilename + selEnts.toString());
            } else {

                if (selectedEntities.length > 0) {
                    if (selectedEntities.length == 1 && selectedEntities[0].equals("")) {
                        resultsList = new String[]{};
                    } else {
                        ArrayList<String> mergedClassHierarchyList = new ArrayList<String>();
                        for (String entity : selectedEntities) {
                            if (entity.contains(":")) {
                                entity = entity.substring(entity.indexOf(":") + 1);
                            }

                            ArrayList<String> classHierarchy = new ArrayList<String>();
                            cidocSchema.getSuperClassesOf(entity, classHierarchy);
                            for (String cl : classHierarchy) {
                                if (cl.contains("/")) { //in case class has full URI name, strip it
                                    cl = cl.substring(cl.lastIndexOf("/") + 1);
                                }
                                if (!mergedClassHierarchyList.contains(cl)) {
                                    mergedClassHierarchyList.add(cl);
                                }
                            }
                        }
                        ArrayList<String> propertiesList = cidocSchema.getPropertiesFor(mergedClassHierarchyList);
                        resultsList = propertiesList.toArray(new String[propertiesList.size()]);
                    }
                } else {
                    resultsList = new String[]{};
                }

                if (resourcesList == null) {
                    resourcesList = new HashMap<String, String[]>();
                }
                resourcesList.put(schemas.toString() + currentSchemaFilename + selEnts.toString(), resultsList);
                session.setAttribute("resourcesList", resourcesList);

            }
        } else if (xpath.contains("/entity")) {
            xpath = xpathResolver(xpath, "entity");

            String[] selectedProperty = mappingFile.queryString(xpath);
            StringBuilder selProp = new StringBuilder("PROPERTIES");
            for (String selectedP : selectedProperty) {
                selProp.append(selectedP);
            }
            if (resourcesList != null && resourcesList.containsKey(schemas.toString() + currentSchemaFilename + selProp.toString())) {
                resultsList = resourcesList.get(schemas.toString() + currentSchemaFilename + selProp.toString());
            } else {

                if (selectedProperty.length > 0) {
                    if (selectedProperty.length == 1 && selectedProperty[0].equals("")) {
                        resultsList = new String[]{};

                    } else {
                        String path = selectedProperty[0];
                        if (path.contains(":")) {
                            path = path.substring(path.indexOf(":") + 1);
                        }
                        String range = cidocSchema.getRangeForProperty(path);
                        ArrayList<String> classHierarchy = new ArrayList<String>();
                        cidocSchema.getSubClassesOf(range, classHierarchy);
                        resultsList = classHierarchy.toArray(new String[classHierarchy.size()]);
                    }
                } else {
                    resultsList = new String[]{};

                }
                if (resourcesList == null) {
                    resourcesList = new HashMap<String, String[]>();
                }
                resourcesList.put(schemas.toString() + currentSchemaFilename + selProp.toString(), resultsList);
                session.setAttribute("resourcesList", resourcesList);
            }

        } else {
            resultsList = cidocSchema.getAllClasses();
        }
        return resultsList;
    }

    private String tableToJSON(ArrayList<String> table, SchemaAnalyzer analyzer) {
        StringBuilder output = new StringBuilder();

        String title = "";
        LinkedHashMap<String, ArrayList> valuesBySchema = new LinkedHashMap();
        for (String res : table) {

            if (res.contains("######")) {
                String[] resValues = res.split("######");
                title = resValues[0];
                if (title.startsWith("../")) {//LEGACY code, maybe not needed 
                    title = title.substring(3);
                }
                res = resValues[1];

                ArrayList valuesSoFar = valuesBySchema.get(title);
                if (valuesSoFar == null) {
                    valuesSoFar = new ArrayList();

                    valuesSoFar.add(res);

                } else {
                    if (!valuesSoFar.contains(res)) { //no duplicates
                        valuesSoFar.add(res);
                    }
                }
                valuesBySchema.put(title, valuesSoFar);

            }

        }
        for (Map.Entry<String, ArrayList> entry : valuesBySchema.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();

            value = new Utils().sort(value);

            String prefix = "";
            String type = "";
            //Replace filename with friendly name
            if (key.startsWith("cidoc_crm")) {
                type = analyzer.getTitleForFile(key, "target");
                if (type == null) {
                    key = "../" + key;
                    type = analyzer.getTitleForFile(key, "target");
                }
                ArrayList<String> prefixes = analyzer.getPrefixesForFile(key, "target");
                if (!prefixes.isEmpty()) {
                    prefix = analyzer.getPrefixesForFile(key, "target").get(0);
                }
            } else {
                ArrayList<String> prefixes = analyzer.getPrefixesForFile(key, "target");
                if (!prefixes.isEmpty()) {
                    prefix = analyzer.getPrefixesForFile(key, "target").get(0);
                }
                type = analyzer.getTitleForFile(key, "target");

            }

            output.append("{ \"text\": \"").append(type).append("\", \"children\": [\n");

            for (String val : value) {
                String strippedURL = val;
//                System.out.println("STRIP="+strippedURL);
                String endingSlash = "";

                if (val.startsWith("http://") || val.startsWith("https://")) { //If URL then strip slashes part
                    if (val.endsWith("/")) {
                        val = val.substring(0, val.length() - 1);
                        endingSlash = "/";

                    }

                    ArrayList<String> prefixForHttpValue = analyzer.getURIandPrefixForValueStartingWithHttp(val);
                    if (prefixForHttpValue == null) {

                        strippedURL = val.substring(val.lastIndexOf("/") + 1);
                        output.append("{\"id\":\"").append(val + endingSlash).append("\",");
                    } else {
                        if (prefixForHttpValue.size() == 2) {
                            String prefixFound = prefixForHttpValue.get(1);
                            strippedURL = val.substring(prefixForHttpValue.get(0).length());
                            output.append("{\"id\":\"").append(prefixFound).append(":").append(strippedURL + endingSlash).append("\",");
                        } else {
                            strippedURL = val.substring(val.lastIndexOf("/") + 1);
                            output.append("{\"id\":\"").append(val + endingSlash).append("\",");
                        }

                    }
                } else {
                    output.append("{\"id\":\"").append(prefix).append(":").append(val).append("\",");
                }
                output.append(" \"text\":\"").append(strippedURL + endingSlash).append("\"},");

            }
            output = output.delete(output.length() - 1, output.length());
            output.append("]},");
        }

        String out = "";
        if (output.length() > 0) {
            out = output.substring(0, output.length() - 1);
        }

        return out;

    }

    //DEPRECATED
//    private String tableToJSON(ArrayList<String> table, HashMap<String, String> filenameAndType, HashMap<String, String> filenameAndPrefix, HashMap<String, String> filenameAndURI) {
//        StringBuilder output = new StringBuilder();
//
//        String title = "";
//        LinkedHashMap<String, ArrayList> valuesBySchema = new LinkedHashMap();
////        System.out.println(table);
//        for (String res : table) {
//
//            if (res.contains("######")) {
//                String[] resValues = res.split("######");
//                title = resValues[0];
//                res = resValues[1];
//
//                if (res.startsWith("http://")) { //Probably have to change prefix!
//                    //Following snippet finds proper prefix for a class using URI
//                    String value = res.substring(0, res.lastIndexOf("/") + 1);
//                    for (Map.Entry<String, String> entry : filenameAndURI.entrySet()) {
////                        System.out.println("VAL=" + value);
////                        System.out.println("ENT=" + entry.getValue());
//
//                        if (value.equals(entry.getValue())) {
//                            title = entry.getKey();
//                            if (title.startsWith("../")) {
//                                title = title.substring(3);
//                            }
//                            res = res.substring(res.lastIndexOf("/") + 1);
//                        }
//                    }
//                }
//
//                ArrayList valuesSoFar = valuesBySchema.get(title);
//                if (valuesSoFar == null) {
//                    valuesSoFar = new ArrayList();
//
//                    valuesSoFar.add(res);
//
//                } else {
//                    if (!valuesSoFar.contains(res)) { //no duplicates
//                        valuesSoFar.add(res);
//                    }
//                }
//                valuesBySchema.put(title, valuesSoFar);
//
//            }
//
//        }
////        System.out.println(valuesBySchema);
//        for (Map.Entry<String, ArrayList> entry : valuesBySchema.entrySet()) {
//            String key = entry.getKey();
//            ArrayList<String> value = entry.getValue();
//
//            value = new Utils().sort(value);
//
//            String prefix = "";
//            String type = "";
//            //Replace filename with friendly name
//            if (key.startsWith("cidoc_crm")) {
//                type = filenameAndType.get(key);
//                if (type == null) {
//                    key = "../" + key;
//                    type = filenameAndType.get(key);
//                }
//                prefix = filenameAndPrefix.get(key);
//
//            } else {
//                prefix = filenameAndPrefix.get(key);
//                type = filenameAndType.get(key);
//            }
//
//            output.append("{ \"text\": \"").append(type).append("\", \"children\": [\n");
//
//            for (String val : value) {
//                String strippedURL = val;
//                if (val.startsWith("http://")) { //If URL then strip slashes part
//                    strippedURL = val.substring(val.lastIndexOf("/") + 1);
//                    output.append("{\"id\":\"").append(val).append("\",");
//                } else {
//                    output.append("{\"id\":\"").append(prefix + ":" + val).append("\",");
//                }
//                output.append(" \"text\":\"").append(strippedURL).append("\"},");
//
//            }
//            output = output.delete(output.length() - 1, output.length());
//            output.append("]},");
//        }
//
//        String out = "";
//        if (output.length() > 0) {
//            out = output.substring(0, output.length() - 1);
//        }
//
//        return out;
//
//    }
    private String tableToJSON(ArrayList<String> table, HashMap<String, String> prefixAndURI) {
        StringBuilder output = new StringBuilder();
        for (String res : table) {
            String strippedURL = res;
            String endingSlash = "";
            if (res.startsWith("http://") || res.startsWith("https://")) { //If URL then strip slashes part

                if (res.endsWith("/")) {
                    res = res.substring(0, res.length() - 1);
                    endingSlash = "/";
                }

                for (String uri : prefixAndURI.keySet()) {
                    if (uri.length() > 0) {
                        if (res.startsWith(uri)) {
                            res = res.replace(uri, prefixAndURI.get(uri) + ":");
                            continue;
                        }
                    }
                }
                strippedURL = res.substring(res.lastIndexOf("/") + 1); //Moved this line here, so if prefix exists, it is used
            }
            output.append("{\"id\":\"").append(res + endingSlash).append("\",");
            output.append("\n\"text\":\"").append(strippedURL + endingSlash).append("\"},");
        }

        String out = output.substring(0, output.length() - 1);
        return out;

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
