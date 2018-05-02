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
import isl.dbms.DBMSException;
import static gr.forth.ics.isl.x3mlEditor.BasicServlet.applicationCollection;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import schemareader.SchemaFile;

/**
 *
 * @author samarita
 */
public class Update extends BasicServlet {

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
        String type = request.getParameter("type");
        String xpath = request.getParameter("xpath");
        String newValue = request.getParameter("value");
        String action = request.getParameter("action");

        if (type == null) {
            type = "Mapping";
        }

        String xmlId = type + id + ".xml";
        DBCollection dbc = new DBCollection(DBURI, applicationCollection + "/" + type, DBuser, DBpassword);
        String collectionPath = getPathforFile(dbc, xmlId, id);
        DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);

        if (action != null) {

            if (action.equals("raw")) { //editXML mode
                String[] values = mappingFile.queryString(xpath);

                if (values != null) {
                    if (values.length > 0) {

                        String initialValue = mappingFile.getXMLAsString();

                        try {
                            if (xpath.contains("namespaces")) { //dummy check, will have to replace
                                mappingFile.xRemove("//x3ml/info");
                                mappingFile.xRemove("//x3ml/namespaces");
                                mappingFile.xInsertBefore("//x3ml/mappings", newValue);
                            } else if (xpath.endsWith("/domain")) { //DOMAIN
                                mappingFile.xInsertAfter(xpath, newValue);
                                mappingFile.xRemove(xpath + "[1]");
                            } else if (xpath.endsWith("/path/..")) { //LINK
                                mappingFile.xInsertAfter(xpath, newValue);
                                mappingFile.xRemove(xpath + "[1]");
                            } else if (xpath.endsWith("/domain/..")) { //NEW MAPPING
                                mappingFile.xInsertAfter(xpath, newValue);
                                mappingFile.xRemove(xpath);
                            } else if (xpath.endsWith("/mappings")) {
                                mappingFile.xInsertAfter(xpath, newValue);
                                mappingFile.xRemove(xpath + "[1]");
                            } else if (xpath.contains("_generator")) {//generators                             
                                mappingFile.xInsertAfter(xpath, newValue);
                                if (xpath.endsWith("]")) {//label 
                                    mappingFile.xRemove(xpath);
                                } else {//instance
                                    mappingFile.xRemove(xpath + "[1]");
                                }
                            }
                            DBFile updatedMappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);
                            out.println(updateXML(updatedMappingFile, initialValue));
                        } catch (DBMSException ex) {
                            mappingFile.setXMLAsString(initialValue); //Revert to saved content
                            out.println("Update failed. Reverting to stored value.\nException message follows:\n\n" + ex.getMessage());
                        }

                    }
                }
            } else if (action.equals("operator")) {

                //Must check if tree may be simplified
                String grandpaTagQuery = xpath + "/../../name()";
                String grandpaTag = mappingFile.queryString(grandpaTagQuery)[0];

                if (grandpaTag.equals(newValue.toLowerCase())) { //simplify tree
                    mappingFile.xCopyInside(xpath + "/if", xpath + "/../..");
                    mappingFile.xRemove(xpath + "/..");
                } else {
                    mappingFile.xRename(xpath, newValue.toLowerCase());
                }

                //New approach (return entire if block instead)
                String rootIfTagPath = xpath.substring(0, xpath.indexOf("/if"));
                String[] entireIfBlock = mappingFile.queryString(rootIfTagPath + "/if");
                if (entireIfBlock != null && entireIfBlock.length > 0) {
                    String frag = entireIfBlock[0];
                    frag = frag.replaceFirst("/?>", " targetMode='' container='" + rootIfTagPath + "' xpath='" + rootIfTagPath + "' relPos=''$0");

                    String output = transform(frag, super.baseURL + "/xsl/edit/if-rule.xsl");

                    out.println(output);
                }

            }

        } else {

            boolean isAttribute = false;
            String attributeName = "";

            String fatherXpath = xpath;
            if (pathIsAttribute(xpath)) { //Generic for attributes
                attributeName = xpath.substring(xpath.lastIndexOf("/") + 2);
                fatherXpath = xpath.substring(0, xpath.lastIndexOf("/"));
                isAttribute = true;
            }
            String[] values = mappingFile.queryString(xpath + "/string()");

            String currentValue = "";

            //Hack from x3mlEditor to support old x3ml format
            if (values.length == 0 && xpath.contains("instance_generator/arg") && xpath.endsWith("@type")) {
                values = new String[1];
                values[0] = newValue;
                mappingFile.xAddAttribute(fatherXpath, attributeName, newValue);
            }
             if (values.length == 0 && xpath.endsWith("/instance_info/constant")) {//missing constant inside instance_info
                values = new String[1];
                values[0] = "";               
                 String grandpaPath = fatherXpath.substring(0, xpath.lastIndexOf("/"));
                 mappingFile.xUpdate(grandpaPath, "<constant/>");
            } 

            if (values.length > 0) {
                currentValue = values[0];

                String strippedNewValue = newValue;

                if ((xpath.matches(".*?/entity\\[\\d+\\]/type\\[\\d+\\]") || xpath.contains("/relationship"))) {
                    String targetType = request.getParameter("targetType");

                    if (targetType == null || !targetType.equals("xml")) {
                        if (newValue.startsWith("http://") || newValue.startsWith("https://")) { //Stripping slashes...
                            String endingSlash = "";

                            if (newValue.endsWith("/")) {
                                newValue = newValue.substring(0, newValue.length() - 1);
                                endingSlash = "/";
                            }
                            strippedNewValue = newValue.substring(newValue.lastIndexOf("/") + 1) + endingSlash;
                        } else if (newValue.contains(":")) {//Stripping prefixes. Is it safe? 
                            strippedNewValue = newValue.substring(newValue.indexOf(":") + 1);
                        }
                    }
                }
                out.println(strippedNewValue);

                //Following snippet added to automatically change instance generator from UUID to Literal and
                //vice versa, when user uses a Literal value for Class
                List<String> literalValues = Arrays.asList("http://www.w3.org/2000/01/rdf-schema#Literal",
                        "http://www.w3.org/2001/XMLSchema#dateTime",
                        "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral", //VRE
                        "http://www.w3.org/2001/XMLSchema#date",//VRE
                        "http://www.w3.org/2001/XMLSchema#string",//VRE
                        "http://www.w3.org/2001/XMLSchema#float",//VRE
                        "http://www.w3.org/2001/XMLSchema#int"//VRE
                );

                if (literalValues.contains(newValue) || literalValues.contains(currentValue)) {

                    String instanceGenerators[] = mappingFile.queryString(xpath + "/../instance_generator");
                    if (instanceGenerators.length > 0) {
                        if (literalValues.contains(newValue)) {//Update to Literal generator
                            String literalInstanceGeneratorArgs = "    <arg name=\"text\" type=\"xpath\">text()</arg>\n"
                                    + "    <arg name=\"language\" type=\"constant\">en</arg>";

                            mappingFile.xUpdate(xpath + "/../instance_generator/@name", "Literal");
                            mappingFile.xUpdate(xpath + "/../instance_generator", literalInstanceGeneratorArgs);
                        } else if (literalValues.contains(currentValue)) {//Revert to UUID generator
                            mappingFile.xUpdate(xpath + "/../instance_generator/@name", "UUID");
                            mappingFile.xUpdate(xpath + "/../instance_generator", "");
                        }
                    }

                }
                //Following snippet added to automatically fill in source node when it is empty and source_relation is
                //given a value
                if (xpath.endsWith("/path/source_relation/relation[1]")) {
                    mappingFile.xUpdate(xpath + "/../../../range/source_node[.='']", newValue);
                }

                if (!currentValue.equals(newValue)) {
                    if (isAttribute) {
                        System.out.println("NEWVAL=" + newValue);
                        if (newValue.contains("<")) {//For some peculiar reason, we had problems when newValue contained <...
                            newValue = newValue.replaceAll("<", "&lt;");
                        }
                        mappingFile.xAddAttribute(fatherXpath, attributeName, newValue);
                    } else {

                        mappingFile.xUpdate(xpath, newValue);
                    }

                }
            } else {
                out.println("XML field missing. Contact administrator!");
            }
        }
    }

    private String updateXML(DBFile mappingFile, String initialValue) {
        String validation = validateXML(mappingFile.getXMLAsString());

        if (!validation.equals("Valid xml.")) {
            mappingFile.setXMLAsString(initialValue); //Revert to saved content
            mappingFile.store();
            return validation;
        }
        return "Update complete!";

    }

    private String validateXML(String xmlContent) {
        SchemaFile sch = new SchemaFile(schemaFolder + "Mapping.xsd");
        xmlContent = xmlContent.replaceAll("(?s)<admin>.*?</admin>", "");
        return sch.validate(xmlContent, "en");
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
