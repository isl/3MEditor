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

import gr.forth.ics.isl.x3mlEditor.utilities.Utils;
import isl.dbms.DBCollection;
import isl.dbms.DBFile;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author samarita
 */
public class Action extends BasicServlet {

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
        String action = request.getParameter("action");
        String type = "Mapping";

        String xmlId = type + id + ".xml";
        DBCollection dbc = new DBCollection(super.DBURI, applicationCollection + "/" + type, super.DBuser, super.DBpassword);
        String collectionPath = getPathforFile(dbc, xmlId, id);
        DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);

        if (action.equals("close")) { //Added from FeXML File servlet to unlock file and (NEW) also set lastModified tag
            mappingFile.xUpdate("//admin/locked", "no");

            int lastModifiedCount = mappingFile.queryString("//admin/lastModified").length;
            Utils utils = new Utils();
            String datestamp = utils.getDateYearFirst();
            String timestamp = utils.getTimeWithDelims(":");
            if (lastModifiedCount > 0) {
                mappingFile.xUpdate("//admin/lastModified", datestamp);
                mappingFile.xUpdate("//admin/lastModified/@time", timestamp);

            } else {
                mappingFile.xAppend("//admin", "<lastModified time='" + timestamp + "'>" + datestamp + "</lastModified>");
            }
        } else if (action.equals("paste")) {
            String xpath = request.getParameter("xpath");
            if (xpath.contains("***")) {
                String[] paths = xpath.split("\\*\\*\\*");
                mappingFile.xCopyAfter(paths[0], paths[1]);

            }

        } else if (action.equals("raw")) {
            String xpath = request.getParameter("xpath");

            String output = "";
            String[] results = mappingFile.queryString(xpath);

            if (results != null) {
                if (results.length > 0) {
                    for (String res : results) {
                        output = output + res + "\n";
                    }
                }
            }
            out.println(output);

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
