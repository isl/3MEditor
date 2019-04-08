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
package gr.forth.ics.isl.x3mlEditor.utilities;

import isl.dbms.DBFile;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.output.FileWriterWithEncoding;

/**
 *
 * @author samarita
 */
public class Utils {

    /**
     *
     * @param f
     * @param enc
     * @return
     */
    public String readFile(File f, String enc) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
            byte[] arr = new byte[(int) f.length()];
            in.read(arr, 0, arr.length);
            in.close();

            return new String(arr, enc);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            throw new Error("IO Error in readFile");
        }
    }

    /**
     *
     * @param f
     * @param cont
     */
    public void writeFile(String filename, String cont) {

        File targetFile = new File(filename);
        File parent = targetFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        try {

//            BufferedWriter doc = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
//            doc.write(cont);
           
            PrintWriter writer = new PrintWriter(targetFile, "UTF-8");
            writer.println(cont);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            throw new Error(ex.getMessage());
        }
    }

    /**
     *
     * @param regexp
     * @param text
     * @param flags
     * @return
     */
    public ArrayList<String> findReg(String regexp, String text, int flags) {

        ArrayList<String> results = new ArrayList();
        Pattern pattern;
        try {
            pattern = Pattern.compile(regexp, flags);
            Matcher matcher = pattern.matcher(text);
            boolean found = false;

            while ((found = matcher.find())) {
                results.add(matcher.group());
            }
        } catch (PatternSyntaxException ex) {
            System.out.println(ex.getDescription());
            System.out.println(ex.getMessage());
        }

        return results;

    }

    /**
     *
     * @return
     */
    public String getDate() {

        Calendar cal = new GregorianCalendar(Locale.getDefault());
        DecimalFormat myformat = new DecimalFormat("00");

        // Get the components of the date
        // int era = cal.get(Calendar.ERA);               // 0=BC, 1=AD
        int year = cal.get(Calendar.YEAR);             // 2002
        int month = cal.get(Calendar.MONTH) + 1;           // 0=Jan, 1=Feb, ...
        int day = cal.get(Calendar.DAY_OF_MONTH);      // 1...
        //   int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=Sunday, 2=Monday
        return myformat.format(day) + "-" + myformat.format(month) + "-" + year;
    }

    public String getDateYearFirst() {
        String now = getDate();
        String[] dateParts = now.split("-");
        if (dateParts.length > 2) {
            return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
        } else {
            return null;
        }
    }

    /**
     *
     */
    public void lengthyProcess() {
        for (int i = 0; i < 1000000; i++) {
            System.out.println(i);
            i = i + 1;
        }
    }

    /**
     *
     * @param input
     * @param pattern
     * @return
     */
    public String getMatch(String input, String pattern) {
        String ResultString = "";
        try {
            Pattern Regex = Pattern.compile(pattern,
                    Pattern.DOTALL);
            Matcher RegexMatcher = Regex.matcher(input);
            if (RegexMatcher.find()) {
                ResultString = RegexMatcher.group();
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        return ResultString;
    }

    /**
     * Time method
     *
     * @return Current time as <CODE>String</CODE> in hh:mm:ss format
     */
    public String getTime() {
        Calendar cal = new GregorianCalendar(Locale.getDefault());

        // Get the components of the time
        //    int hour12 = cal.get(Calendar.HOUR);            // 0..11
        // Create the DecimalFormat object only one time.
        DecimalFormat myformat = new DecimalFormat("00");

        int hour24 = cal.get(Calendar.HOUR_OF_DAY);     // 0..23
        int min = cal.get(Calendar.MINUTE);             // 0..59
        int sec = cal.get(Calendar.SECOND);             // 0..59
        return myformat.format(hour24) + myformat.format(min) + myformat.format(sec);
    }

    public String getTimeWithDelims(String delim) {
        String now = getTime();
        return now.substring(0, 2) + delim + now.substring(2, 4) + delim + now.substring(4, 6);
    }

    /**
     * Unzip it (This implementation works only when zip contains files-folders
     * with ASCII filenames Greek characters break the code!
     *
     * @param zipFile input zip file
     * @param outputFolder
     */
    public void unZipIt(String zipFile, String outputFolder) {

        String rootFolderName = "";
        String rootFlashFilename = "";
        byte[] buffer = new byte[1024];

        try {

            //get the zip file content
            ZipInputStream zis
                    = new ZipInputStream(new FileInputStream(outputFolder + File.separator + zipFile));
            //get the zipped file list entry

            ZipEntry ze = zis.getNextEntry();

            boolean rootDirFound = false;
            boolean flashFileFound = false;
            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                if (!ze.getName().contains("__MACOSX")) {
                    if (ze.isDirectory()) {
                        if (rootDirFound == false) {
                            rootFolderName = newFile.getName();
                            rootDirFound = true;
                        }
                        new File(newFile.getParent()).mkdirs();
                    } else {
                        FileOutputStream fos = null;

                        new File(newFile.getParent()).mkdirs();

                        if (flashFileFound == false && newFile.getName().endsWith(".swf") && !newFile.getName().startsWith(".")) {
                            rootFlashFilename = newFile.getName();
                            flashFileFound = true;
                        }

                        fos = new FileOutputStream(newFile);

                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }

                        fos.close();
                    }
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            //Special Postprocessing for EKBMM
            if (!rootFlashFilename.equals("")) {
                File rootFlashFile = new File(outputFolder + File.separator + rootFolderName + File.separator + rootFlashFilename);
                rootFlashFile.renameTo(new File(outputFolder + File.separator + rootFolderName + File.separator + "index.swf"));

                if (!rootFolderName.equals("")) {
                    File rootFolder = new File(outputFolder + File.separator + rootFolderName);
                    rootFolder.renameTo(new File(outputFolder + File.separator + zipFile.substring(0, zipFile.lastIndexOf("."))));

                }
            }

            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param list
     * @return
     */
    public ArrayList<String> sort(ArrayList<String> list) {
        try {
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String x, String y) {

                    if (x.contains("/")) { //in case class has full URI name, strip it
                        x = x.substring(x.lastIndexOf("/") + 1);
                    }
                    if (y.contains("/")) { //in case class has full URI name, strip it
                        y = y.substring(y.lastIndexOf("/") + 1);
                    }
                    if (x.contains(":")) { //in case class has prefix, strip it
                        x = x.substring(x.lastIndexOf(":") + 1);
                    }
                    if (y.contains(":")) { //in case class has prefix, strip it
                        y = y.substring(y.lastIndexOf(":") + 1);
                    }

                    //Bad temp solution...will replace in the future
                    String character = x.charAt(1) + "";
                    if (character.matches("\\d{1}")) {
                        x = x.substring(1).split("_")[0];
                    } else {
                        x = x.substring(2).split("_")[0];
                    }
                    character = y.charAt(1) + "";
                    if (character.matches("\\d{1}")) {
                        y = y.substring(1).split("_")[0];
                    } else {
                        y = y.substring(2).split("_")[0];
                    }

                    if (x.endsWith("i")) {
                        x = x.replace("i", "");
                    }
                    if (y.endsWith("i")) {
                        y = y.replace("i", "");
                    }
                    Integer a = 2000; //Declared initial values as fail safes for non numeric values
                    Integer b = 2000; //Declared initial values as fail safes for non numeric values
                    try {
                        a = Integer.parseInt(x);
                    } catch (NumberFormatException ex) {

                    }
                    try {
                        b = Integer.parseInt(y);
                    } catch (NumberFormatException ex) {
                    }
                    return a.compareTo(b);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return list;
        }
        return list;
    }

    /**
     *
     * @param uploads
     * @param file
     * @param use
     * @return
     */
    public static String findMime(DBFile uploads, String file, String use) {

        file = file.substring(file.lastIndexOf(".") + 1);
        file = file.toLowerCase();

        String[] mimes = uploads.queryString("//mime[type='" + file + "']/../name()");
        if (mimes.length == 0) {
            return "Other";
        } else {
            if (use != null && use.length() > 0 && mimes.length > 1) {
                String mime = uploads.queryString("//mime[type/@use ='" + use + "' and type='" + file + "']/../name()")[0];
                return mime;
            } else {
                return mimes[0];
            }
        }

    }

}
