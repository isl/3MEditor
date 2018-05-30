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
package gr.forth.ics.isl.x3mlEditor.upload;

import gr.forth.ics.isl.Tidy;
import isl.dbms.DBCollection;
import isl.dbms.DBFile;
import isl.reasoner.OntologyReasoner;
import gr.forth.ics.isl.x3mlEditor.BasicServlet;
import gr.forth.ics.isl.x3mlEditor.utilities.Utils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.io.File;
import java.net.URLEncoder;
import javax.servlet.http.HttpSession;

/**
 *
 * @author samarita
 */
public class UploadReceiver extends BasicServlet {

    private static File UPLOAD_DIR;
    private static File TEMP_DIR;
    private static String CONTENT_TYPE = "text/plain;charset=UTF-8";
    private static String CONTENT_LENGTH = "Content-Length";
    private static int RESPONSE_CODE = 200;

    /**
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
    }

    /**
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String targetAnalyzer = req.getParameter("targetAnalyzer");
        if (targetAnalyzer == null) {
            targetAnalyzer = targetPathSuggesterAlgorithm;
        }

        String xpath = req.getParameter("path");
        String id = req.getParameter("id");
        String filePath = "";
        RequestParser requestParser = null;
        String filename = "";
        String msg = null;

        try {
            if (ServletFileUpload.isMultipartContent(req)) {
                requestParser = RequestParser.getInstance(req, new MultipartUploadParser(req, TEMP_DIR, getServletContext()));
                filename = doWriteTempFileForPostRequest(requestParser);
            } else {
                requestParser = RequestParser.getInstance(req, null);
                filename = requestParser.getFilename();
            }
        } catch (Exception e) {
            System.out.println("Problem handling upload request");
            e.printStackTrace();
            filename = requestParser.getFilename();
            msg = e.getMessage();
        }

        DBFile uploadsDBFile = new DBFile(super.DBURI, super.adminCollection, "Uploads.xml", super.DBuser, super.DBpassword);

        String use = "";
        if (xpath != null) {
            if (filename.endsWith("rdf") || filename.endsWith("rdfs") || filename.endsWith("ttl") || filename.endsWith("nt")) {
                if (xpath.endsWith("/@rdf_link")) {
                    use = "rdf_link";
                } else if (xpath.endsWith("/@thesaurus_link")) {
                    use = "thesaurus_link";
                } else {
                    use = "schema_file";
                }

            } else if (filename.endsWith("xml")) {
                if (xpath.endsWith("/@generator_link")) {
                    use = "generator_link";
                } else {
                    use = "xml_link";
                }
            }
        }
        String mime = new Utils().findMime(uploadsDBFile, filename, use);

        filename = URLEncoder.encode(filename, "UTF-8");

        TEMP_DIR = new File(uploadsFolder + "uploadsTemp");

        if (!TEMP_DIR.exists()) {
            TEMP_DIR.mkdirs();
        }

        UPLOAD_DIR = new File(uploadsFolder + mime + System.getProperty("file.separator") + filePath);

        if (!UPLOAD_DIR.exists()) {
            UPLOAD_DIR.mkdirs();
        }

        String contentLengthHeader = req.getHeader(CONTENT_LENGTH);
        Long expectedFileSize = StringUtils.isBlank(contentLengthHeader) ? null : Long.parseLong(contentLengthHeader);

        resp.setContentType(CONTENT_TYPE);
        resp.setStatus(RESPONSE_CODE);

        if (!ServletFileUpload.isMultipartContent(req)) {
            writeToTempFile(req.getInputStream(), new File(UPLOAD_DIR, filename), expectedFileSize);
        }

        Tidy tidy = new Tidy(DBURI, rootCollection, x3mlCollection, DBuser, DBpassword, uploadsFolder);
        String duplicate = tidy.getDuplicate(UPLOAD_DIR + System.getProperty("file.separator") + filename, UPLOAD_DIR.getAbsolutePath());
        boolean duplicateFound = false;

        if (duplicate != null) {
            new File(UPLOAD_DIR, filename).delete(); //Delete uploaded file!
            filename = duplicate;
            duplicateFound = true;
        }

        String xmlId = "Mapping" + id + ".xml";
        DBCollection dbc = new DBCollection(super.DBURI, applicationCollection + "/Mapping", super.DBuser, super.DBpassword);
        String collectionPath = getPathforFile(dbc, xmlId, id);
        DBFile mappingFile = new DBFile(DBURI, collectionPath, xmlId, DBuser, DBpassword);
        boolean isAttribute = false;
        String attributeName = "";
        if (pathIsAttribute(xpath)) { //Generic for attributes
            attributeName = xpath.substring(xpath.lastIndexOf("/") + 2);
            xpath = xpath.substring(0, xpath.lastIndexOf("/"));
            isAttribute = true;
        }
        if (isAttribute) {

            mappingFile.xAddAttribute(xpath, attributeName, filename);
            if (xpath.endsWith("/target_schema") && attributeName.equals("schema_file") && (filename.endsWith("rdfs") || filename.endsWith("rdf") || filename.endsWith("owl") || filename.endsWith("ttl") || filename.endsWith("xml") || filename.endsWith("xsd") || filename.endsWith("nt"))) {

//                if (!(filename.endsWith("ttl") || filename.endsWith("owl"))) {//Skip exist for owl or ttl
                if (!duplicateFound) {
                    //Uploading target schema files to eXist!
                    try {
                        dbc = new DBCollection(super.DBURI, x3mlCollection, super.DBuser, super.DBpassword);
                        DBFile dbf = dbc.createFile(filename, "XMLDBFile");
                        String content = new Utils().readFile(new File(UPLOAD_DIR, filename), "UTF-8");
                        dbf.setXMLAsString(content);
                        dbf.store();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        msg = "File was uploaded but eXist queries target analyzer failed. Try using another target analyzer or upload a different file. Failure message: " + ex.getMessage().replace("\n", "").replace("\r", "").replace("\"", "'");;
                    }
                }
//                }

                try {
                    OntologyReasoner ont = getOntModel(mappingFile, id);

                    HttpSession session = sessionCheck(req, resp);
                    if (session == null) {
                        session = req.getSession();
                    }
                    session.setAttribute("modelInstance_" + id, ont);
                    msg = null;
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    msg = "File was uploaded but Jena reasoner target analyzer failed. Try using another target analyzer or upload a different file. Failure message: " + ex.getMessage().replace("\n", "").replace("\r", "").replace("\"", "'");;
                }
//                }

            } else if (xpath.endsWith("/generator_policy_info") && (filename.endsWith("xml"))) {

                if (!duplicateFound) {
                    //Uploading generator policy files to eXist!
                    dbc = new DBCollection(super.DBURI, x3mlCollection, super.DBuser, super.DBpassword);
                    DBFile dbf = dbc.createFile(filename, "XMLDBFile");
                    String content = new Utils().readFile(new File(UPLOAD_DIR, filename), "UTF-8");
                    dbf.setXMLAsString(content);
                    dbf.store();
                }
            }

        } else {
            mappingFile.xUpdate(xpath, filename);
        }

        writeResponse(filename, resp.getWriter(), msg, mime);

    }

    private String doWriteTempFileForPostRequest(RequestParser requestParser) throws Exception {
        String filename = requestParser.getFilename();
        writeToTempFile(requestParser.getUploadItem().getInputStream(), new File(UPLOAD_DIR, filename), null);
        return filename;
    }

    private File writeToTempFile(InputStream in, File out, Long expectedFileSize) throws IOException {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(out);

            IOUtils.copy(in, fos);

            if (expectedFileSize != null) {
                Long bytesWrittenToDisk = out.length();
                if (!expectedFileSize.equals(bytesWrittenToDisk)) {
                    throw new IOException(String.format("Unexpected file size mismatch. Actual bytes %s. Expected bytes %s.", bytesWrittenToDisk, expectedFileSize));
                }
            }

            return out;
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private void writeResponse(String filename, PrintWriter writer, String failureReason, String mime) {
        if (failureReason == null) {

            String json = "{\"success\": true, \"filename\": \"" + filename + "\", \"mime\": \"" + mime + "\"}";
            writer.print(json);

        } else {          
            failureReason = failureReason.replaceAll("\\\\'", "");//Added because we had a problem when error message contained \'          
            writer.print("{\"error\": \"" + failureReason + "\"}");
        }

    }

}
