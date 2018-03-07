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
package gr.forth.ics.isl.x3mlEditor.utilities;

import isl.dbms.DBFile;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author samarita
 */
public class SchemaAnalyzer {

    DBFile mappingFile;
    ArrayList<String> targetSchemas = new ArrayList<String>();
    ArrayList<String> targetSchemaTypes = new ArrayList<String>();
    ArrayList<String> targetSchemaTitles = new ArrayList<String>();

    HashMap<String, ArrayList<String>> targetSchemaPrexifes = new HashMap();
    HashMap<String, ArrayList<String>> targetSchemaURIs = new HashMap();

    ArrayList<String> sourceSchemas = new ArrayList<String>();
    ArrayList<String> sourceSchemaTypes = new ArrayList<String>();
    ArrayList<String> sourceSchemaTitles = new ArrayList<String>();

    HashMap<String, ArrayList<String>> sourceSchemaPrexifes = new HashMap();
    HashMap<String, ArrayList<String>> sourceSchemaURIs = new HashMap();

    ArrayList<String> globalNamespacePrefixes = new ArrayList<String>();
    ArrayList<String> globalNamespaceURIs = new ArrayList<String>();

    ArrayList<String> allNamespacePrefixes = new ArrayList<String>();
    ArrayList<String> allNamespaceURIs = new ArrayList<String>();

    public SchemaAnalyzer(DBFile mappingFile) {
        this.mappingFile = mappingFile;
        analyzeSchemas("target");
        analyzeSchemas("source");
        analyzeGlobalNamespaces();
        analyzeAllNamespaces();

    }

    /*
     * 
     */
    public ArrayList<String> getURIandPrefixForValueStartingWithHttp(String value) {
              ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < allNamespaceURIs.size(); i++) {

            String namespaceURI = allNamespaceURIs.get(i);
            if (!namespaceURI.equals("")) {//added because otherwise we had :http://www.w3.org/2000/01/rdf-schema#Literal issue
                if (value.startsWith(namespaceURI)) { //WE HAVE A MATCH. Return ArrayList. First item is URI, second is prefix.
                    result.add(namespaceURI);
                    result.add(allNamespacePrefixes.get(i));
                    return result;
                }
            }

        }
        return null;
    }

    public String getPrefixForURI(String URI) {

        int index = allNamespaceURIs.indexOf(URI);
        if (index != -1) {
            return allNamespacePrefixes.get(index);
        } else {
            return null;
        }
    }

    public ArrayList<String> getPrefixesForFile(String filename, String type) {
        ArrayList<String> prefixes = new ArrayList<String>();

        if (type.equals("target")) {
            prefixes = targetSchemaPrexifes.get(filename);
        } else if (type.equals("source")) {
            prefixes = sourceSchemaPrexifes.get(filename);
        }

        if (prefixes.isEmpty()) { //reverting to namespaces block  (LEGACY mode)
            prefixes = new ArrayList<String>(allNamespacePrefixes.subList(2, 3));//removing first 2 (rdfs,xsd)

        }

        return prefixes;
    }

    public String getTypeForFile(String filename, String type) {
        String schemaType = "";
        if (type.equals("target")) {
            int position = targetSchemas.indexOf(filename);
            if (position != -1) {
                schemaType = targetSchemaTypes.get(position);
            }
        } else if (type.equals("source")) {
            int position = sourceSchemas.indexOf(filename);
            if (position != -1) {
                schemaType = sourceSchemaTypes.get(position);
            }
        }
        return schemaType;

    }

    public String getTitleForFile(String filename, String type) {
        String schemaTitle = "";
        if (type.equals("target")) {
            int position = targetSchemas.indexOf(filename);
            if (position != -1) {
                schemaTitle = targetSchemaTitles.get(position);
            }
        } else if (type.equals("source")) {
            int position = sourceSchemas.indexOf(filename);
            if (position != -1) {
                schemaTitle = sourceSchemaTitles.get(position);
            }
        }
        return schemaTitle;

    }

    private String[] getSchemaBlock(String type) {
        return mappingFile.queryString("//" + type + "_info");

    }

    private void analyzeAllNamespaces() {
        String[] namespaces = mappingFile.queryString("//namespaces");
        if (namespaces.length > 0) {
            Utils utils = new Utils();

            for (String namespaceBlock : namespaces) {
                allNamespacePrefixes.addAll(utils.findReg("(?<=prefix=\")[^\"]*(?=\")", namespaceBlock, 0));
                allNamespaceURIs.addAll(utils.findReg("(?<=uri=\")[^\"]*(?=\")", namespaceBlock, 0));
            }
        }

    }

    private void analyzeGlobalNamespaces() {
        String[] namespaces = mappingFile.queryString("//x3ml/namespaces");
        if (namespaces.length > 0) {//Should be only one
            Utils utils = new Utils();

            globalNamespacePrefixes = utils.findReg("(?<=prefix=\")[^\"]*(?=\")", namespaces[0], 0);
            globalNamespaceURIs = utils.findReg("(?<=uri=\")[^\"]*(?=\")", namespaces[0], 0);
        }

    }

    private void analyzeSchemas(String type) {
        String[] schemaBlocks = getSchemaBlock(type);
        Utils utils = new Utils();
        for (String sBlock : schemaBlocks) {
            ArrayList<String> schemaFilenames = utils.findReg("(?<=schema_file=\")[^\"]*(?=\")", sBlock, 0);
            ArrayList<String> schemaTypes = utils.findReg("(?<=type=\")[^\"]*(?=\")", sBlock, 0);
            ArrayList<String> schemaTitles = utils.findReg("(?<=>)[^<]*(?=</" + type + "_schema>)", sBlock, 0);

            if (schemaFilenames.size() > 0) {
                String schemaFilename = schemaFilenames.get(0);
                String schemaType = schemaTypes.get(0);
                String schemaTitle = "";
                if (schemaTitles.size() > 0) {
                    schemaTitle = schemaTitles.get(0);
                }
                ArrayList<String> namespacePrefixes = utils.findReg("(?<=prefix=\")[^\"]*(?=\")", sBlock, 0);
                ArrayList<String> namespaceURIs = utils.findReg("(?<=uri=\")[^\"]*(?=\")", sBlock, 0);

                if (type.equals("target")) {
                    targetSchemas.add(schemaFilename); //Should be only one
                    targetSchemaTypes.add(schemaType);//Should be only one
                    targetSchemaTitles.add(schemaTitle);//Should be only one
                    targetSchemaPrexifes.put(schemaFilename, namespacePrefixes);
                    targetSchemaURIs.put(schemaFilename, namespaceURIs);
                } else if (type.equals("source")) {
                    sourceSchemas.add(schemaFilename); //Should be only one
                    sourceSchemaTypes.add(schemaType);//Should be only one
                    sourceSchemaTitles.add(schemaTitle);//Should be only one
                    sourceSchemaPrexifes.put(schemaFilename, namespacePrefixes);
                    sourceSchemaURIs.put(schemaFilename, namespaceURIs);
                }

            }

        }
    }

    public ArrayList<String> getTargetSchemas() {
        return targetSchemas;
    }

    public HashMap<String, ArrayList<String>> getTargetSchemaPrexifes() {
        return targetSchemaPrexifes;
    }

    public HashMap<String, ArrayList<String>> getTargetSchemaURIs() {
        return targetSchemaURIs;
    }

    public ArrayList<String> getSourceSchemas() {
        return sourceSchemas;
    }

    public ArrayList<String> getTargetSchemaTypes() {
        return targetSchemaTypes;
    }

    public ArrayList<String> getSourceSchemaTypes() {
        return sourceSchemaTypes;
    }

    public HashMap<String, ArrayList<String>> getSourceSchemaPrexifes() {
        return sourceSchemaPrexifes;
    }

    public HashMap<String, ArrayList<String>> getSourceSchemaURIs() {
        return sourceSchemaURIs;
    }

    public ArrayList<String> getGlobalNamespacePrefixes() {
        return globalNamespacePrefixes;
    }

    public ArrayList<String> getGlobalNamespaceURIs() {
        return globalNamespaceURIs;
    }

    public ArrayList<String> getTargetSchemaTitles() {
        return targetSchemaTitles;
    }

    public ArrayList<String> getSourceSchemaTitles() {
        return sourceSchemaTitles;
    }

    public ArrayList<String> getAllNamespacePrefixes() {
        return allNamespacePrefixes;
    }

    public ArrayList<String> getAllNamespaceURIs() {
        return allNamespaceURIs;
    }

}
