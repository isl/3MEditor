// Copyright 2014-2017 Institute of Computer Science,
//Foundation for Research and Technology - Hellas
//
//Licensed under the EUPL, Version 1.1 or - as soon they will be approved
//by the European Commission - subsequent versions of the EUPL (the "Licence");
//You may not use this work except in compliance with the Licence.
//You may obtain a copy of the Licence at:
//
//http://ec.europa.eu/idabc/eupl
//
//Unless required by applicable law or agreed to in writing, software distributed
//under the Licence is distributed on an "AS IS" basis,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the Licence for the specific language governing permissions and limitations
//under the Licence.
//
//Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
//Tel:+30-2810-391632
//Fax: +30-2810-391638
//E-mail: isl@ics.forth.gr
//http://www.ics.forth.gr/isl
//
//Authors : Georgios Samaritakis, Konstantina Konsolaki.
//
//This file is part of the 3MEditor webapp of Mapping Memory Manager project.
package gr.forth.ics.isl.x3mlEditor.utilities;

import isl.dbms.DBFile;
import gr.forth.ics.isl.x3mlEditor.BasicServlet;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author samarita
 */
public class GeneratorPolicy {

    DBFile gpf;

    public GeneratorPolicy(DBFile x3mlFile) {
        String[] gpfFiles = x3mlFile.queryString("//generator_policy_info/@generator_link/string()");
        if (gpfFiles.length == 0) {
            gpf = null;
        } else {
            gpf = x3mlFile.getCollection().getParentCollection().getParentCollection().getChildCollection("x3ml").getFile(gpfFiles[0]);

        }
    }

    public boolean exists() {
        if (gpf == null) {
            return false;
        }
        return true;
    }

    public String[] getInstanceGeneratorNames() {
        return gpf.queryString("//generator/@name/string()");
    }

    public String getArgsForInstanceGeneratorAsXML(String ign) {
        StringBuilder xml = new StringBuilder();
        //First X3ML engine built in values
        if (ign.equals("UUID")) {
            //No arguments needed
        } else if (ign.equals("Literal")) {
            xml.append("<arg name='text' type='xpath'></arg>");
            xml.append("<arg name='language' type='constant'></arg>");
        } else if (ign.equals("Constant")) {
            xml.append("<arg name='text' type='constant'></arg>");
            xml.append("<arg name='language' type='constant'></arg>");
        } else if (ign.equals("prefLabel")) {
            xml.append("<arg name='text' type='xpath'></arg>");
            xml.append("<arg name='language' type='constant'></arg>");
        } else if (ign.equals("ConcatMultipleTerms")) { //new generator
            xml.append("<arg name='prefix' type='constant'></arg>");
            xml.append("<arg name='sameTermsDelim' type='constant'></arg>");
            xml.append("<arg name='diffTermsDelim' type='constant'></arg>");
            xml.append("<arg name='text1' type='xpath'></arg>");
            xml.append("<arg name='text2' type='xpath'></arg>");
        } else {
            //Then values from gpf
            if (gpf != null) {

                String[] generators = gpf.queryString("//generator[@name='" + ign + "']");
                Utils utils = new Utils();
                if (generators.length > 0) {
                    String generator = generators[0];
                    if (generator.contains("<pattern>")) { //patterns-based
                        String patternTag = gpf.queryString("//generator[@name='" + ign + "']/pattern/string()")[0];
                        ArrayList<String> patterns = utils.findReg("(?<=\\{)[^}]+(?=\\})", patternTag, 0);
                        for (String pattern : patterns) {
                            xml.append("<arg name='").append(pattern).append("' type='xpath'></arg>");
                        }
                    } else if (generator.contains("<custom generatorClass")) { //custom-class
                        String[] customArgs = gpf.queryString("//generator[@name='" + ign + "'][custom]//set-arg/@name/string()");
                        for (String customArg : customArgs) {
                            xml.append("<arg name='").append(customArg).append("' type='xpath'></arg>");
                        }

                    }
                } else {
                    System.out.println("UNKNOWN GENERATOR!");
                }

            }
        }
        return xml.toString();
    }

}
