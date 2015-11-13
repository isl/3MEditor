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
package gr.forth.ics.isl.x3mlEditor;

import isl.dbms.DBCollection;
import isl.dbms.DBFile;
import static gr.forth.ics.isl.x3mlEditor.BasicServlet.applicationCollection;
import gr.forth.ics.isl.x3mlEditor.utilities.GeneratorPolicy;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author samarita
 */
public class Services extends BasicServlet {

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
        servletParams(request, response);

        if (stateOfSite.equals("off")) {
            response.sendRedirect("maintenance.html");
            return;
        }
        String method = request.getParameter("method");
        PrintWriter out = response.getWriter();

        String output = request.getParameter("output");

        if (output == null || output.equals("") || output.equals("html")) {
            response.setContentType("text/html;charset=UTF-8");
            output = "html";
        } else {
            response.setContentType("text/xml;charset=UTF-8");
        }
        StringBuilder xmlMiddle = new StringBuilder();

        if (method == null) {
            method = "";
        }

        String id = request.getParameter("id");

        String xmlId = "Mapping" + id + ".xml";
        DBCollection dbc = new DBCollection(DBURI, applicationCollection + "/Mapping", DBuser, DBpassword);
        String collectionPath = getPathforFile(dbc, xmlId, id);

        if (method.equals("export")) {
            xmlMiddle.append(getDBFileContent(collectionPath, xmlId));
            String content = xmlMiddle.toString();
            content = content.replace("<?xml-stylesheet type=\"text/xsl\" href=\"crm_mapping-v2.0.xsl\"?>", "");
            content = content.replaceAll("(?s)<admin>.*?</admin>", "");
            out.write(content);
        } else if (method.equals("viewPublished")) {
            if (collectionPath == null) {
                response.sendRedirect("message.html");
            } else {
                DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);
                String status = mappingFile.queryString("//admin/status/string()")[0];
                xmlMiddle.append("<output><xml>");
                String xsl = baseURL + "/xsl/x3ml.xsl";

                if (status.equals("published")) {
                    xmlMiddle.append(mappingFile.toString());

                    xmlMiddle.append("</xml>");

                    xmlMiddle.append("<viewMode>").append("1").append("</viewMode>");
                    xmlMiddle.append("<editorType>").append(editorType).append("</editorType>");
                    xmlMiddle.append("<type>").append("Mapping").append("</type>");
                    xmlMiddle.append("<id>").append(id).append("</id>");
                } else {
                    xmlMiddle.append("</xml>");
                    response.sendRedirect("message.html");

                }

                xmlMiddle.append("</output>");
                out.write(transform(xmlMiddle.toString(), xsl));
            }

        } else if (method.equals("instanceGeneratorNames")) {

            xmlMiddle.append("{ \"text\": \"Built In\", \"children\": [\n");
            for (String builtInName : instanceGeneratorNamesBuiltInX3MLEngine) {
                xmlMiddle.append("{ \"id\": \"").append(builtInName).append("\", \"text\": \"").append(builtInName).append("\" },");
            }
            xmlMiddle = xmlMiddle.delete(xmlMiddle.length() - 1, xmlMiddle.length()); //to remove last comma
            xmlMiddle.append("] }\n");
            DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);

            GeneratorPolicy gpf = new GeneratorPolicy(mappingFile);
            if (gpf.exists()) {
                String[] gpfFileGenNames = gpf.getInstanceGeneratorNames();
                xmlMiddle.append(",{ \"text\": \"Generator File\", \"children\": [\n");

                for (String genName : gpfFileGenNames) {
                    xmlMiddle.append("{ \"id\": \"").append(genName).append("\", \"text\": \"").append(genName).append("\" },");
                }
                xmlMiddle = xmlMiddle.delete(xmlMiddle.length() - 1, xmlMiddle.length()); //to remove last comma
                xmlMiddle.append("] }\n");
            }

            out.println("{\n"
                    + "    \"results\": [\n"
                    + "            { \"id\": \"\", \"text\": \"\" }");//Adding empty default
            out.println(",\n" + xmlMiddle);
            out.println("]\n"
                    + "}");

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
