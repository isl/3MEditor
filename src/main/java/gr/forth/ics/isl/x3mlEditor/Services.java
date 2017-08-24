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
import static gr.forth.ics.isl.x3mlEditor.BasicServlet.applicationCollection;
import gr.forth.ics.isl.x3mlEditor.utilities.GeneratorPolicy;
import gr.forth.ics.isl.x3mlEditor.utilities.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
        String output = request.getParameter("output");

        if (output == null || output.equals("") || output.equals("html")) {
            response.setContentType("text/html;charset=UTF-8");
            output = "html";
        } else {
            response.setContentType("text/xml;charset=UTF-8");
        }
        servletParams(request, response);

        if (stateOfSite.equals("off")) {
            response.sendRedirect("maintenance.html");
            return;
        }
        String method = request.getParameter("method");
        PrintWriter out = response.getWriter();

        StringBuilder xmlMiddle = new StringBuilder();

        if (method == null) {
            method = "";
        }

        String id = request.getParameter("id");

        String xmlId = "Mapping" + id + ".xml";
        DBCollection dbc = new DBCollection(DBURI, applicationCollection + "/Mapping", DBuser, DBpassword);
        String collectionPath = getPathforFile(dbc, xmlId, id);

        if (method.equals("export") || output.equals("xml")) {
            String version = request.getParameter("version");
            if (version != null) {
                dbc = new DBCollection(DBURI, versionsCollection + "/Mapping/Mapping" + id + "/" + version, DBuser, DBpassword);
                collectionPath = getPathforFile(dbc, xmlId, id);
            }

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
            for (String builtInName : generatorNamesBuiltInX3MLEngine) {
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

        } else if (method.equals("storeFile")) {
            String type = request.getParameter("type");
            String content = request.getParameter("content");
//            System.out.println("CONTENT="+request.getParameter("content"));

            String extension = "rdf";
            if (type.equals("Turtle")) {
                extension = "ttl";
            } else if (type.equals("N-triples")) {
                extension = "trig";
            }
            String filename = "Mapping" + id + "." + extension;
            new Utils().writeFile(uploadsFolder + "example_files/" + filename, content);
            //example_data_target_record
            DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);
            String[] results = mappingFile.queryString("//example_data_target_record/@rdf_link/string()");
            System.out.println(results.length);
            if (results.length == 0) {
                mappingFile.xAddAttribute("//example_data_target_record", "rdf_link", filename);
            } else if (results.length > 0) {
                mappingFile.xUpdate("//example_data_target_record/@rdf_link", filename);
            }
            out.println("Saved " + filename);

        } else if (method.equals("getNamespaces")) {
            String filename = request.getParameter("filename");
            Utils utils = new Utils();
            HashMap<String, String> results = new HashMap<String, String>();
            if (filename.endsWith("ttl") || filename.endsWith("rdf") || filename.endsWith("rdfs")) {
                File schemaFile = new File(uploadsFolder + "rdf_schema/" + filename);
                String content = utils.readFile(schemaFile, "UTF-8");

                if (filename.endsWith(".ttl")) {
                    results = findNamespaces("ttl", content);
                } else {
                    results = findNamespaces("rdf", content);
                }
            } else if (filename.endsWith("xsd")) {
                File schemaFile = new File(uploadsFolder + "xml_schema/" + filename);
                String content = utils.readFile(schemaFile, "UTF-8");
                results = findNamespaces("xsd", content);
            } else if (filename.endsWith("xml")) {

                File schemaFile = new File(uploadsFolder + "example_files/" + filename);
                if (!schemaFile.exists()) { //xml schema file may be stored in xml_schema folder ...
                    schemaFile = new File(uploadsFolder + "xml_schema/" + filename);
                }
                String content = utils.readFile(schemaFile, "UTF-8");

                results = findNamespaces("xml", content);
            }
            
            String xpath = request.getParameter("xpath");
            DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);

            String relevantNamespacesXpath = xpath.substring(0, xpath.lastIndexOf("]") + 1) + "/namespaces";
            StringBuilder namespacesXML = new StringBuilder();
            String baseNamespace = results.get("base");
            int index = 1;
            if (baseNamespace != null) {
                String namespaceXML = "<namespace prefix='' uri='" + baseNamespace + "'/>";
                namespacesXML = namespacesXML.append(namespaceXML);
                String namespaceXpath = relevantNamespacesXpath + "/namespace[" + index + "]";
                namespaceXML = namespaceXML.replaceFirst("/>", " pos='" + index + "'" + " xpath='" + namespaceXpath + "'/>");
                String html = transform(namespaceXML, super.baseURL + "/xsl/edit/namespace.xsl");
                out.println(html);
                index = index + 1;
                results.remove("base");
            }
            System.out.println(results);
            System.out.println("###="+namespacesXML.toString());
            for (String prefix : results.keySet()) {
                System.out.println("IN HERE");
                String namespaceXML = "<namespace prefix='" + prefix + "' uri='" + results.get(prefix) + "'/>";
                namespacesXML = namespacesXML.append(namespaceXML);

                String namespaceXpath = relevantNamespacesXpath + "/namespace[" + index + "]";
                namespaceXML = namespaceXML.replaceFirst("/>", " pos='" + index + "'" + " xpath='" + namespaceXpath + "'/>");
                String html = transform(namespaceXML, super.baseURL + "/xsl/edit/namespace.xsl");
                out.println(html);
                index = index + 1;
            }
            if (baseNamespace == null && results.isEmpty()) {
                System.out.println("IS EMPTY");
                 String namespaceXML = "<namespace prefix='' uri=''/>";
                namespacesXML = namespacesXML.append(namespaceXML);
                 String namespaceXpath = relevantNamespacesXpath + "/namespace[" + index + "]";
                namespaceXML = namespaceXML.replaceFirst("/>", " pos='" + index + "'" + " xpath='" + namespaceXpath + "'/>");
                String html = transform(namespaceXML, super.baseURL + "/xsl/edit/namespace.xsl");
                out.println(html);
            }
            System.out.println(relevantNamespacesXpath);
            System.out.println(namespacesXML.toString());
          

            mappingFile.xUpdate(relevantNamespacesXpath, namespacesXML.toString());
        } else if (method.equals("update")) {
            DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);
            updateX3ml(mappingFile, "1.1", "1.2");
            fixNamespacesWithoutSchemaFile(mappingFile);
            out.println("Updated! Please wait to reload mapping.");

        }

        out.close();

    }

    private void fixNamespacesWithoutSchemaFile(DBFile x3mlFile) {
        String[] namespacesToMove = x3mlFile.queryString("//target_info[not(target_schema/@schema_file!='')]//namespace");

        x3mlFile.xRemove("//target_info[not(target_schema/@schema_file!='')]");
        for (String namespaceToMove : namespacesToMove) {
            x3mlFile.xAppend("//x3ml/namespaces", namespaceToMove);
        }
        x3mlFile.xRemove("//x3ml/namespaces[count(namespace)>1]/namespace[@prefix='']");

    }

    private void updateX3ml(DBFile x3mlFile, String from, String to) {
        if (from.equals("1.1") && to.equals("1.2")) {
            x3mlFile.xInsertAfter("//x3ml/info/general_description", "<source/>");//create source block
            x3mlFile.xInsertAfter("//x3ml/info/source", "<target/>");//create target block
            x3mlFile.xMoveInside("//x3ml/info/source_info[1]/source_collection", "//x3ml/info/source");//move only source collection
            x3mlFile.xMoveInside("//x3ml/info/target_info[1]/target_collection", "//x3ml/info/target");//move first target collection
            x3mlFile.xRemove("//x3ml/info/target_info/target_collection");//delete other target collection 

            x3mlFile.xMoveBefore("//x3ml/info/source_info", "//x3ml/info/source/source_collection");//move source_info inside source
            x3mlFile.xMoveBefore("//x3ml/info/target_info", "//x3ml/info/target/target_collection");//move target_info inside target
            String[] namespaces = x3mlFile.queryString("//namespace");
            int targetInfosLength = x3mlFile.queryString("//target_info").length;

            int index = 0;
            StringBuilder newNamespacesBlock = new StringBuilder();
            for (String namespace : namespaces) {
                if (index < 2) {//DO NOTHING
                } else if (index < targetInfosLength + 2) {
                    int targetIndex = index - 1;
                    x3mlFile.xInsertAfter("//x3ml/info/target/target_info[" + targetIndex + "]/target_schema", "<namespaces>" + namespace + "</namespaces>");//create target block
                } else {
                    newNamespacesBlock = newNamespacesBlock.append(namespace);
                }
                index = index + 1;
            }
            if (newNamespacesBlock.length() > 0) {
                x3mlFile.xUpdate("//x3ml/namespaces", newNamespacesBlock.toString());
            } else {
                x3mlFile.xUpdate("//x3ml/namespaces", "<namespace prefix='' uri=''/>");
            }
        }
    }

    private HashMap<String, String> findNamespaces(String type, String content) {
        Utils utils = new Utils();
        HashMap<String, String> results = new HashMap<String, String>();

        if (type.equals("ttl")) {
            ArrayList<String> prefixAndNamespaces = utils.findReg("(?<=@prefix)\\s+.*(?=>)", content, 0);
            for (String prefixAndNamespace : prefixAndNamespaces) {
                prefixAndNamespace = prefixAndNamespace.trim();

                if (prefixAndNamespace.contains(": <")) {
                    String[] res = prefixAndNamespace.split(": <");
                    String prefix = res[0].trim();
                    String namespace = res[1].trim();
                    if (!(prefix.equals("rdf") || prefix.equals("rdfs") || prefix.equals("xsd"))) {
                        results.put(prefix, namespace);
                    }
                    results.put(prefix, namespace);
                }
            }
        } else if (type.equals("rdf") || type.equals("xsd") || type.equals("xml")) {
            ArrayList<String> prefixAndNamespaces = utils.findReg("(?<=xmlns:).*?(?=\"(\\s+|>))", content, 0);
            for (String prefixAndNamespace : prefixAndNamespaces) {
                prefixAndNamespace = prefixAndNamespace.trim();
                if (prefixAndNamespace.contains("=\"") || prefixAndNamespace.contains("= \"")) {
                    String[] res = prefixAndNamespace.split("=\\s*\"");
                    String prefix = res[0].trim();
                    String namespace = res[1].trim();
                    if (!(prefix.equals("rdf") || prefix.equals("rdfs") || prefix.equals("xsd"))) {
                        results.put(prefix, namespace);
                    }
                }
            }
            prefixAndNamespaces = utils.findReg("(?<=xml:)base.*?(?=\"(\\s+|>))", content, 0);
            for (String prefixAndNamespace : prefixAndNamespaces) {
                prefixAndNamespace = prefixAndNamespace.trim();
                if (prefixAndNamespace.contains("=\"") || prefixAndNamespace.contains("= \"")) {
                    String[] res = prefixAndNamespace.split("=\\s*\"");
                    String prefix = res[0].trim();
                    String namespace = res[1].trim();
                    if (!(prefix.equals("rdf") || prefix.equals("rdfs") || prefix.equals("xsd"))) {
                        results.put(prefix, namespace);
                    }
                }
            }
        }
        return results;
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
