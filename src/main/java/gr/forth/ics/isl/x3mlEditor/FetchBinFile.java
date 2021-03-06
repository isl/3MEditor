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

import isl.dbms.DBFile;
import gr.forth.ics.isl.x3mlEditor.utilities.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author konsolak
 */
public class FetchBinFile extends BasicServlet {

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
        request.setCharacterEncoding("UTF-8");

        // Get the absolute path of the image ServletContext sc = getServletContext();
        String filename = request.getParameter("file");
        String type = request.getParameter("type");

        String path = uploadsFolder;
        DBFile uploadsDBFile = new DBFile(super.DBURI, super.adminCollection, "Uploads.xml", super.DBuser, super.DBpassword);
        String use = "";
        if (type != null) {
            if (filename.endsWith("rdf") || filename.endsWith("rdfs") || filename.endsWith("ttl") || filename.endsWith("owl") || filename.endsWith("nt")) {
                if (type.equals("example_data_target_record")) {
                    use = "rdf_link";
                } else if (type.equals("thesaurus_link")) {
                    use = "thesaurus_link";
                } else {
                    use = "schema_file";
                }
            } else if (filename.endsWith("xml")) {
                if (type.equals("generator_link")) {
                    use = "generator_link";
                } else {
                    use = "xml_link";
                }
            }
        }
        String mime = new Utils().findMime(uploadsDBFile, filename, use);
        path = path + mime + System.getProperty("file.separator");
        String mimeType = getServletContext().getMimeType(filename);
        path = path + filename;
        filename = filename.substring(filename.lastIndexOf("/") + 1);
        // Set content size
        File file = new File(path);
        System.out.println(path);
        if (!file.exists()) {
            System.out.println(filename);

            if (filename.endsWith("xml")) {//fail safe...
                path = path.replace("xml_schema", "example_files");
                file = new File(path);
            }

//            if (filename.endsWith("ttl") && use.equals("rdf_link")) {//OBSOLETE! Should not happenSpecial case (generated ttl file from Transformation tab)
//                path = targetRecordsFolder+filename;
//                file = new File(path);
//            }
//            if (response.getContentType() == null) {
//                response.setContentType(mimeType + ";charset=UTF-8");
//                response.setHeader("Content-disposition", "inline; filename=\"" + filename + "\"");
//            }
        } else {
            if (response.getContentType() == null) {
                response.setContentType(mimeType + ";charset=UTF-8");
                response.setHeader("Content-disposition", "inline; filename=\"" + filename + "\"");
            }
        }

        response.setContentLength((int) file.length());
        // Open the file and output streams
        FileInputStream in = new FileInputStream(file);
        OutputStream out = response.getOutputStream();

        // Copy the contents of the file to the output stream
        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        in.close();
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
