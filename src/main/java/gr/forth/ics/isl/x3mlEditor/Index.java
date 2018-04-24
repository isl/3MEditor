/*
 * Copyright 2014-2018  Institute of Computer Science,
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
import gr.forth.ics.isl.x3mlEditor.utilities.Utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author samarita
 */
public class Index extends BasicServlet {

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
        response.setContentType("text/html;charset=UTF-8");

        String output = request.getParameter("output");

        if (output == null || output.equals("") || output.equals("html")) {
            response.setContentType("text/html;charset=UTF-8");
            output = "html";
        } else {
            response.setContentType("text/xml;charset=UTF-8");

        }
        PrintWriter out = response.getWriter();
        this.servletParams(request, response);

        String id = request.getParameter("id");

        String type = request.getParameter("type");
        String file = request.getParameter("file");
        String lang = request.getParameter("lang");
        String action = request.getParameter("action");
        String sourceAnalyzer = request.getParameter("sourceAnalyzer");
        String mappingSuggester = request.getParameter("mappingSuggester");
        if (mappingSuggester == null) {
            mappingSuggester = mappingSuggesterStatus;
        }
        String sourceAnalyzerFiles = "***";
        String targetType = "";
        String targetAnalyzerFiles = "***";
        String targetAnalyzer = request.getParameter("targetAnalyzer");
        if (targetAnalyzer == null) {
            targetAnalyzer = targetPathSuggesterAlgorithm;
        }

        if (action == null) {
            action = "edit";
        }

        if (lang == null && type == null && id == null && file == null) {
            response.sendRedirect("Index?id=1");
            return;

        }
        if (lang == null) {
            lang = "en";
        }
        if (id == null) {
            id = "1";
        }
        if (type == null) {
            type = "Mapping";
        }
        id = id.replace(type, "");
        StringBuilder xmlMiddle = new StringBuilder();
        if (output.equals("html")) {
            xmlMiddle.append("<output><xml>");
        }
        String xmlId = type + id + ".xml";
        DBCollection dbc = new DBCollection(DBURI, applicationCollection + "/" + type, DBuser, DBpassword);

        //Cleanup code 
        String duplicateInstanceInfoQuery = "//entity[count(instance_info)>1]/instance_info[position()>1]";
        dbc.xRemove(duplicateInstanceInfoQuery);
        dbc.xRemove("//text");
        dbc.xRemove("//if[not(*)]");

        String collectionPath = getPathforFile(dbc, xmlId, id);
        DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);
        //If there is source schema without namespaces block, add it
        mappingFile.xInsertAfter("//source_info[not(namespaces)]/source_schema", "<namespaces><namespace prefix='' uri=''/></namespaces>");
        //If there is namespaces without at least one namespace, add it
        mappingFile.xAppend("//namespaces[not(namespace)]", "<namespace prefix='' uri=''/>");
        mappingFile.xRemove("//info/target[position()>1]"); // Deleting second target if it exists!
        mappingFile.xAddAttribute("//x3ml", "editor", "3MEditor v3.2"); // Adding 3MEditor version
        String mappingFileAsString = getDBFileContent(collectionPath, xmlId);

        xmlMiddle.append(mappingFileAsString);

        if (output.equals("html")) {
            xmlMiddle.append("</xml>");
            if (mappingFileAsString.contains("<source>")) {
                System.out.println("NEW");
                xmlMiddle.append("<schemaVersion>").append("1.2+").append("</schemaVersion>");
            } else {
                System.out.println("OLD");
                xmlMiddle.append("<schemaVersion>").append("1.1").append("</schemaVersion>");
            }

            String xsl = baseURL + "/xsl/x3ml.xsl";
            if (stateOfSite.equals("off")) {
                response.sendRedirect("maintenance.html");
                return;
            }

            HttpSession session = sessionCheck(request, response);
            if (session == null) {
                session = request.getSession(true);
            }

            //Actions: 0=edit, 1=view, 2=instance+label
            if (action.equals("instance")) {
                xmlMiddle.append("<viewMode>").append("2").append("</viewMode>");
                xmlMiddle.append("<generator mode='").append(generatorsStatus).append("'>").append("instance").append("</generator>");

                String[] usernames = mappingFile.queryString("//admin/locked/string()");
                if (usernames.length > 0) {
                    session.setAttribute("username", usernames[0]);
                }

                session.setAttribute("id", id);
                session.setAttribute("action", action);
                session.setAttribute("resourcesList", new HashMap<String, String[]>());

            } else if (action.equals("view")) {
                xmlMiddle.append("<viewMode>").append("1").append("</viewMode>");
                session.setAttribute("id", id);
                session.setAttribute("action", action);
            } else if (action.equals("edit")) {
                xmlMiddle.append("<viewMode>").append("0").append("</viewMode>");
                xmlMiddle.append("<generator mode='").append(generatorsStatus).append("'>").append("instance").append("</generator>");

                //Target analysis
                HashMap<String, String> targetAnalysisResult = analyzeTarget(mappingFile);
                targetType = targetAnalysisResult.get("targetType");
                targetAnalyzerFiles = targetAnalysisResult.get("targetAnalyzerFiles");
                if (targetType.equals("None")) {
                    targetAnalyzer = "0";
                } else if (targetType.equals("xml")) {

                    if (mappingFile.queryString("//x3ml/mappings/mapping[1]/domain/target_node/entity[1]/type[1]/string()")[0].length() > 0) {//Has root
                        targetAnalyzer = "4";
                    } else {
                        targetAnalyzer = "0";
                    }

                } else if (targetType.equals("owl") || targetType.equals("ttl") || targetType.equals("nt") || targetType.equals("Mixed")) { //Jena reasoner when mixed 
                    targetAnalyzer = "3";
                }

//                if (mappingFile.queryString("//target_info/target_schema/@schema_file/string()").length == 0) {
//                    targetAnalyzer = "0"; //If no target schemas specified, then choose None!
//                }
                //Source analysis
                HashMap<String, String> sourceAnalysisResult = analyzeSource(mappingFile, sourceAnalyzer);
                sourceAnalyzer = sourceAnalysisResult.get("sourceAnalyzer");
                sourceAnalyzerFiles = sourceAnalysisResult.get("sourceAnalyzerFiles");

                if (sourceAnalyzer == null) {
                    sourceAnalyzer = sourceAnalyzerStatus;
                }

                String[] usernames = mappingFile.queryString("//admin/locked/string()");
                if (usernames.length > 0) {
                    session.setAttribute("username", usernames[0]);
                }

                session.setAttribute("resourcesList", new HashMap<String, String[]>());
                session.setAttribute("id", id);
                session.setAttribute("action", action);
            }

            xmlMiddle.append("<lang>").append(lang).append("</lang>");
            xmlMiddle.append("<editorType>").append(editorType).append("</editorType>");

            if (file != null) {
                xmlMiddle.append("<file>").append(file).append("</file>");
            } else {
                xmlMiddle.append("<sourceAnalyzer>").append(sourceAnalyzer).append("</sourceAnalyzer>");
                xmlMiddle.append("<sourceAnalyzerFiles>").append(sourceAnalyzerFiles).append("</sourceAnalyzerFiles>");
                xmlMiddle.append("<mappingSuggester>").append(mappingSuggester).append("</mappingSuggester>");

                xmlMiddle.append("<targetAnalyzer>").append(targetAnalyzer).append("</targetAnalyzer>");
                xmlMiddle.append("<targetType>").append(targetType).append("</targetType>");
                xmlMiddle.append("<targetAnalyzerFiles>").append(targetAnalyzerFiles).append("</targetAnalyzerFiles>");

                xmlMiddle.append("<type>").append(type).append("</type>");
                xmlMiddle.append("<id>").append(id).append("</id>");
                xmlMiddle.append("<RDFVisualizerURL>").append(RDFVisualizerURL).append("</RDFVisualizerURL>");

            }

            xmlMiddle.append("</output>");
            out.write(transform(xmlMiddle.toString(), xsl));

        } else {
            String content = xmlMiddle.toString();
            content = content.replaceAll("(?s)<admin>.*?</admin>", "");
            out.write(content);
        }

        out.close();

    }

    private HashMap<String, String> analyzeTarget(DBFile mappingFile) {
        String targetType = "";
        String targetAnalyzerFiles = "";

        String targetQuery = "for $i in //target_info/target_schema\n"
                + "return\n"
                + "$i/@schema_file/string()\n";

        String[] results = mappingFile.queryString(targetQuery);
        ArrayList<String> targetSchemas = new ArrayList<String>(Arrays.asList(results));

        if (targetSchemas.size() > 0) {
            for (String target : targetSchemas) {
                if (targetType.equals("")) { //First schema
                    if (target.endsWith(".rdfs") || target.endsWith(".rdf")) {
                        targetType = "rdf";
                    } else if (target.endsWith(".xsd") || target.endsWith(".xml")) {
                        targetType = "xml";
                    } else if (target.endsWith(".owl")) {
                        targetType = "owl";
                    } else if (target.endsWith(".ttl")) {
                        targetType = "ttl";
                    } else if (target.endsWith(".nt")) {
                        targetType = "nt";
                    }
                } else {
                    if (targetType.equals("rdf")) {
                        if (target.endsWith(".xsd") || target.endsWith(".xml") || target.endsWith(".owl") || target.endsWith(".ttl") || target.endsWith(".nt")) {
                            targetType = "Mixed";
                        }
                    } else if (targetType.equals("xml")) {
                        if (target.endsWith(".xsd") || target.endsWith(".rdf") || target.endsWith(".owl") || target.endsWith(".ttl") || target.endsWith(".nt")) {
                            targetType = "Mixed";
                        }
                    } else if (targetType.equals("owl")) {
                        if (target.endsWith(".xsd") || target.endsWith(".rdf") || target.endsWith(".xml") || target.endsWith(".ttl") || target.endsWith(".nt")) {
                            targetType = "Mixed";
                        }
                    } else if (targetType.equals("ttl")) {
                        if (target.endsWith(".xsd") || target.endsWith(".rdf") || target.endsWith(".xml") || target.endsWith(".owl")||target.endsWith(".nt")) {
                            targetType = "Mixed";
                        }
                     } else if (targetType.equals("nt")) {
                        if (target.endsWith(".xsd") || target.endsWith(".rdf") || target.endsWith(".xml") || target.endsWith(".owl")||target.endsWith(".ttl")) {
                            targetType = "Mixed";
                        }
                    }
                }
                targetAnalyzerFiles = targetAnalyzerFiles + target + "***";
            }

            targetAnalyzerFiles = targetAnalyzerFiles.substring(0, targetAnalyzerFiles.length() - 3); //Remove last extra ***

        } else {
            targetType = "None";
            targetAnalyzerFiles = "***";
        }

        HashMap<String, String> targetAnalysisResult = new HashMap<String, String>();
        targetAnalysisResult.put("targetType", targetType);
        targetAnalysisResult.put("targetAnalyzerFiles", targetAnalyzerFiles);
        return targetAnalysisResult;

    }

    private HashMap<String, String> analyzeSource(DBFile mappingFile, String sourceAnalyzer) {
        String sourceAnalyzerFiles = "***";
        //TODO!!!!
        String sourceQuery = "let $i := //source_info[1]/source_schema/@schema_file\n"
                + "let $k := //example_data_source_record/@xml_link\n"
                + "return\n"
                + "<sourceAnalyzer>\n"
                + "{$i}\n"
                + "{$k}\n"
                + "</sourceAnalyzer>";
        String[] results = mappingFile.queryString(sourceQuery);
        if (results.length == 1) { //First check for source schema
            String res = results[0];
            if (!res.equals("<sourceAnalyzer/>")) { //Something exists...
                String schemaFile = new Utils().getMatch(res, "(?<=schema_file=\").*?(?=\")");
                String instanceFile = new Utils().getMatch(res, "(?<=xml_link=\").*?(?=\")");
                if (!(schemaFile.endsWith(".xsd") || schemaFile.endsWith(".xml"))) {
                    schemaFile = "";
                }
                if (!(instanceFile.endsWith(".xml"))) {
                    instanceFile = "";
                }

                sourceAnalyzerFiles = schemaFile + "***" + instanceFile;
                if (sourceAnalyzerFiles.equals("***")) {
                    sourceAnalyzer = "off";
                }

            } else {
                sourceAnalyzer = "off";
            }
        }
        HashMap<String, String> sourceAnalysisResult = new HashMap<String, String>();
        sourceAnalysisResult.put("sourceAnalyzer", sourceAnalyzer);
        sourceAnalysisResult.put("sourceAnalyzerFiles", sourceAnalyzerFiles);
        return sourceAnalysisResult;

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
