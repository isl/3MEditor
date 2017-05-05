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
package gr.forth.ics.isl.x3mlEditor;

import isl.dbms.DBCollection;
import isl.dbms.DBFile;
import isl.reasoner.OntologyReasoner;
import static gr.forth.ics.isl.x3mlEditor.BasicServlet.applicationCollection;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author samarita
 */
public class Delete extends BasicServlet {

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

        String xpath = request.getParameter("xpath");
        String id = request.getParameter("id");
        String type = request.getParameter("type");
        String targetAnalyzer = request.getParameter("targetAnalyzer");
        if (targetAnalyzer == null) {
            targetAnalyzer = targetPathSuggesterAlgorithm;
        }

        if (type == null) {
            type = "Mapping";
        }

        String xmlId = type + id + ".xml";
        DBCollection dbc = new DBCollection(super.DBURI, applicationCollection + "/" + type, super.DBuser, super.DBpassword);
        String collectionPath = getPathforFile(dbc, xmlId, id);
        DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);

        if (!xpath.startsWith("//")) {
            xpath = "//" + xpath;
        }

        if (xpath.contains("/intermediate[")) { //Faux element intermediate
            String position = xpath.substring(xpath.lastIndexOf("[") + 1, xpath.lastIndexOf("]"));

            if (xpath.contains("source_relation")) { //Source intermediate
                int relationPosition = Integer.parseInt(position) + 1;
                String nodePath = xpath.replaceAll("/intermediate", "/node");
                mappingFile.xRemove(nodePath);
                String relationPath = xpath.replaceAll("/intermediate\\[\\d+\\]", "/relation[" + relationPosition + "]");
                mappingFile.xRemove(relationPath);
            } else { //Target intermediate
                int relationshipPosition = Integer.parseInt(position) + 1;
                String entityPath = xpath.replaceAll("/intermediate", "/entity");
                mappingFile.xRemove(entityPath);
                String relationshipPath = xpath.replaceAll("/intermediate\\[\\d+\\]", "/relationship[" + relationshipPosition + "]");
                mappingFile.xRemove(relationshipPath);
            }
        } else if (xpath.endsWith("/equals") || xpath.endsWith("/exists") || xpath.endsWith("/narrower")) {

            String rootIfTagPath = xpath.substring(0, xpath.indexOf("/if"));
            String grandpaTagQuery = xpath + "/../../name()";
            String grandpaTag = mappingFile.queryString(grandpaTagQuery)[0];

            if (grandpaTag.equals("and") || grandpaTag.equals("or") || grandpaTag.equals("not")) {

                String ifBlockPath = "/../..";
                if (grandpaTag.equals("not")) {
                    ifBlockPath = "/../../../..";
                }

                String countQuery = "count(" + xpath + ifBlockPath + "/if)";
                String ifCount = mappingFile.queryString(countQuery)[0];

                if (ifCount.equals("1")) { //Last if, should delete entire or block instead
                    mappingFile.xRemove(xpath + ifBlockPath);
                } else if (ifCount.equals("2")) {
                    String xpathToRemove = xpath.substring(0, xpath.lastIndexOf("/"));

                    if (grandpaTag.equals("not")) {
                        xpathToRemove = xpath.substring(0, xpath.lastIndexOf("/not"));

                    }

                    mappingFile.xRemove(xpathToRemove);
                    String xpathToMove = xpathToRemove.substring(0, xpathToRemove.lastIndexOf("["));
                    mappingFile.xCopyInside(xpathToMove + "/*", xpathToMove + "/../..");
                    xpathToRemove = xpathToMove.substring(0, xpathToMove.lastIndexOf("/"));
                    mappingFile.xRemove(xpathToRemove);

                } else {
                    if (grandpaTag.equals("not")) {
                        mappingFile.xRemove(xpath + "/../../..");
                    } else {
                        mappingFile.xRemove(xpath + "/..");
                    }
                }

            } else {
                mappingFile.xRemove(xpath + "/..");
            }

            //New approach (return entire if block instead)
            String[] entireIfBlock = mappingFile.queryString(rootIfTagPath + "/if");
            if (entireIfBlock != null && entireIfBlock.length > 0) {
                String frag = entireIfBlock[0];
                frag = frag.replaceFirst("/?>", " targetMode='' container='" + rootIfTagPath + "' xpath='" + rootIfTagPath + "' relPos=''$0");

                String output = transform(frag, super.baseURL + "/xsl/edit/if-rule.xsl");

                out.println(output);
            }

        } else {

            mappingFile.xRemove(xpath);
        }
        if (xpath.startsWith("//x3ml/info/target/target_info")) { 

//            if (targetAnalyzer.equals("3")) { //Deleting target schema means replacing ont model
                OntologyReasoner ont = getOntModel(mappingFile, id);

                HttpSession session = sessionCheck(request, response);
                if (session == null) {
                    session = request.getSession();
                }
                session.setAttribute("modelInstance_" + id, ont);
//            }

          
        }

        out.close();
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
