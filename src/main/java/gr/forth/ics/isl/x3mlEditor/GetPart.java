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
import static gr.forth.ics.isl.x3mlEditor.BasicServlet.applicationCollection;
import gr.forth.ics.isl.x3mlEditor.utilities.Utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author samarita
 */
public class GetPart extends BasicServlet {

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
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");
        String part = request.getParameter("part");
        String type = request.getParameter("type");
        String mode = request.getParameter("mode");
        String xpath = request.getParameter("xpath");
        String targetAnalyzer = request.getParameter("targetAnalyzer");
        String sourceAnalyzer = request.getParameter("sourceAnalyzer");

        int targetMode;
        if (targetAnalyzer == null) {
            targetAnalyzer = "2";
        }

        if (sourceAnalyzer == null) {
            sourceAnalyzer = "off";
        }
        targetMode = Integer.parseInt(targetAnalyzer);

        if (type == null) {
            type = "Mapping";
        }

        String xmlId = type + id + ".xml";
        DBCollection dbc = new DBCollection(DBURI, applicationCollection + "/" + type, DBuser, DBpassword);
        String collectionPath = getPathforFile(dbc, xmlId, id);
        DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);
        String query = "";
        String xsl = "";
        if (part == null) { //using xpath instead

            if (xpath != null) {
                query = xpath;
                if (mode.equals("edit")) {
                    if (xpath.endsWith("domain")) {
                        xsl = baseURL + "/xsl/edit/domain.xsl";
                    } else if (xpath.endsWith("instance_generator")) {
                        xsl = baseURL + "/xsl/generators/edit/instance_generator.xsl";
                    } else if (xpath.contains("label_generator[")) {
                        xsl = baseURL + "/xsl/generators/edit/label_generator.xsl";
                    } else {
                        xsl = baseURL + "/xsl/edit/link.xsl";
                    }
                } else if (mode.startsWith("noRelation")) {//Special mode! Must update XML before returning HTML
                    if (mode.equals("noRelationUpdate")) {
                        String[] results = mappingFile.queryString(query + "/../domain/source_node/string()");
                        String domainValue = "";
                        if (results != null && results.length > 0) {
                            domainValue = mappingFile.queryString(query + "/../domain/source_node/string()")[0];
                        }
                        mappingFile.xUpdate(query + "/path/source_relation", "<relation/>");
                        mappingFile.xUpdate(query + "/range/source_node", domainValue);
                    } else if (mode.equals("noRelationRestore")) {
                        mappingFile.xUpdate(query + "/range/source_node", "");

                    }
                    xsl = baseURL + "/xsl/edit/link.xsl";

                } else {
                    if (xpath.endsWith("path")) {
                        xsl = baseURL + "/xsl/path.xsl";
                    } else if (xpath.endsWith("range")) {
                        xsl = baseURL + "/xsl/range.xsl";
                    } else if (xpath.endsWith("domain")) {
                        xsl = baseURL + "/xsl/domain.xsl";
                    } else if (xpath.endsWith("mappings")) {
                        xsl = baseURL + "/xsl/mappings.xsl";
                    } else if (xpath.endsWith("instance_generator")) {
                        xsl = baseURL + "/xsl/generators/instance_generator.xsl";
                    } else if (xpath.contains("label_generator[")) {
                        xsl = baseURL + "/xsl/generators/label_generator.xsl";
                    }

                }
            }

        } else {

            if (part.equals("info")) { //Need to add extra info from x3ml tag
                query = "for $i in //x3ml/info\n"
                        + "return\n"
                        + "<x3ml>\n"
                        + "{$i/../@version}\n"
                        + "{$i/../@source_type}\n"
                        + "{$i}\n"
                        + "{$i/../namespaces}\n"
                        + "</x3ml>";
            } else {
                query = "//" + part;
            }
            xsl = baseURL + "/xsl/edit/" + part + ".xsl";
            if (mode.equals("view") || mode.startsWith("instance")) {
                xsl = baseURL + "/xsl/" + part + ".xsl";

            }
        }
        
        System.out.println("QUERY="+query);
        String[] queryResults = mappingFile.queryString(query);
        String output = "";
        if (queryResults != null && queryResults.length > 0 && baseURL != null) {
            String result = queryResults[0];
            if (result.startsWith("<link")) { //Edit mode

                String index = getIndex(xpath);
                String noRelation = "";
                if (mode.startsWith("noRelation") && !mode.endsWith("Restore")) {//Add noRelation attribute to choose noRelation View
                    noRelation = "noRelation=''";
                }

                result = result.replaceFirst("<path>", "<path sourceAnalyzer='" + sourceAnalyzer + "' targetMode='" + targetMode + "' xpath='" + xpath + "/path" + "' " + noRelation + " index='" + index + "'>");
                result = result.replaceFirst("<range>", "<range sourceAnalyzer='" + sourceAnalyzer + "' targetMode='" + targetMode + "' xpath='" + xpath + "/range" + "' " + noRelation + ">");

            } else if (result.startsWith("<domain")) { //Edit mode
                query = "count(//mapping)";
                queryResults = mappingFile.queryString(query);
                String mapIndex = getIndex(xpath);

                result = result.replaceFirst("<domain", "<domain sourceAnalyzer='" + sourceAnalyzer + "' targetMode='" + targetMode + "' xpath='" + xpath + "' mappingsCount='" + queryResults[0] + "' index='" + mapIndex + "' ");
            } else if (result.startsWith("<instance_generator")) {
                result = result.replaceFirst("<instance_generator", "<instance_generator container='" + xpath + "' generatorsStatus='" + request.getParameter("generatorsStatus") + "'");
            } else if (result.startsWith("<label_generator")) {
                result = result.replaceFirst("<label_generator", "<label_generator container='" + xpath + "' generatorsStatus='" + request.getParameter("generatorsStatus") + "'");
            }
            if (result.startsWith("<mappings>") && mode.startsWith("instance")) {              
                    result = result.replaceFirst("<mappings", "<mappings mode='" + mode + "' status='" + request.getParameter("generatorsStatus") + "'");
            }
            if (result.startsWith("<path>")) {
                System.out.println("HERE!!!");
                String index = getIndex(xpath);
                System.out.println(xpath);
                String [] linkTemplateTable = mappingFile.queryString(xpath+"/../@template/string()");
                String linkTemplate="";
                if (linkTemplateTable.length>0) {
                    linkTemplate = "linkTemplate='"+linkTemplateTable[0]+"'";
                }
//                System.out.println(xpath+"/../@template");
//                System.out.println(linkTemplate);
                result = result.replaceFirst("<path>", "<path index='" + index + "' "+linkTemplate+">");
            }
            if (mode.equals("viewNoRelation")) {
                result = result.replaceFirst(">", " noRelation=''>");
            }
            System.out.println(result);
            output = transform(result, xsl);
            if (output != null) {
                if (output.contains("mapping[]") || output.contains("link[]")) { //Special case! Need to find out position
                    Utils utils = new Utils();
                    ArrayList<String> mappings = utils.findReg("mapping\\[\\d+\\]", xpath, 0);
                    if (!mappings.isEmpty()) {
                        output = output.replaceAll("mapping\\[\\]", mappings.get(0));
                    }
                    ArrayList<String> links = utils.findReg("link\\[\\d+\\]", xpath, 0);
                    if (!links.isEmpty()) {
                        output = output.replaceAll("link\\[\\]", links.get(0));
                    }
                }
            }

        } else {
            output = "Something went wrong! Please reload page.";
        }

        try {
            /* TODO output your page here. You may use following sample code. */
            out.println(output);
        } finally {
            out.close();
        }
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
