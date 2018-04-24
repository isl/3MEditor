<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright 2014-2018  Institute of Computer Science,
Foundation for Research and Technology - Hellas

Licensed under the EUPL, Version 1.1 or - as soon they will be approved
by the European Commission - subsequent versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl

Unless required by applicable law or agreed to in writing, software distributed
under the Licence is distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations
under the Licence.

Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
Tel:+30-2810-391632
Fax: +30-2810-391638
E-mail: isl@ics.forth.gr
http://www.ics.forth.gr/isl

Authors : Georgios Samaritakis, Konstantina Konsolaki.

This file is part of the 3MEditor webapp of Mapping Memory Manager project.

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <xsl:template name="configuration">
        <div class="well" id="config">                     

            <form  role="form" >
                <fieldset>
                    <legend>Options</legend>
                    <br/>
                    <div class="form-group ">

                        <div class="row" >  
                            <xsl:if test="$action!=1">
                         
                                <div class="col-sm-6">
                                    <label class="control-label" for="sourceAnalyzer">Source Analyzer: </label>
                                    <div class="btn-group" id="sourceAnalyzer" data-toggle="buttons">
                                        <label id="label5">
                                            <xsl:choose>
                                                <xsl:when test="//output/sourceAnalyzer='on'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:choose>
                                                        <xsl:when test="//output/sourceAnalyzerFiles='***'">
                                                            <xsl:attribute  name="class">btn btn-default btn-sm disabled</xsl:attribute>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="sourceAnalyzer" type="radio" class="toggle" value="on" autocomplete="off">
                                                <xsl:if test="//output/sourceAnalyzer='on'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> On</xsl:text>
                                        </label>
                                        <label id="label6">
                                            <xsl:choose>
                                                <xsl:when test="//output/sourceAnalyzer!='on'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="sourceAnalyzer" type="radio" class="toggle" value="off" autocomplete="off">
                                                <xsl:if test="//output/sourceAnalyzer!='on'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> Off</xsl:text>
                                        </label>
                                    
                                    
                                    </div>
                                    <br/>
                                    <br/>
                             
                                    <p>
                                        Once a source schema file or an example xml file is uploaded the source analyzer engine is enabled by default (If uploaded
                                        file is an xsd schema file, then user will also have to <b>MANUALLY</b> define an element name as root).<br/>
                                        User may choose to disable it. When it is enabled, source paths free text input fields are replaced by select boxes.
                                        Select box options are all possible xpaths. 
                                        <br/>
                                        <b>BEWARE! If both source schema and an example xml file are uploaded, schema is the one used to fill select boxes.</b>
                                        <br/>
                                        <b>WARNING! At the moment, source analyzer engine works with xml files and may work with xsd files. No other file format is accepted.
                                            If you encounter problems with uploaded xsd files, try using a generated XML template file instead.</b>

                                    
                                    </p>
                                </div>
                            </xsl:if>                           
                            <div class="col-sm-6">

                                <label class="control-label" for="sourcePaths">Source Paths: </label>
                                <div class="btn-group" id="sourcePaths" data-toggle="buttons">
                                    <label id="label50">
                                        <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                        <input name="sourcePaths" type="radio" class="toggle" value="mini" autocomplete="off">

                                        </input>                                            
                                        <xsl:text> Short</xsl:text>
                                    </label>
                                    <label id="label60">
                                        <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                        <input name="sourcePaths" type="radio" class="toggle" value="full" autocomplete="off">
                                            
                                        </input>                                            
                                        <xsl:text> Full</xsl:text>
                                    </label>
                                   
                                    
                                </div>
                                <br/>
                                <br/>
                             
                                <p>
                                    Chooses whether source xpaths view is stripped (<b>Short mode</b>) or complete (<b>Full mode</b>).
                                    Default mode is <b>"Short"</b>, which strips actual xpaths to create a more compact view:
                                    e.g. <code>root/IdentityOfObject/CodeNumber/CodeValue</code> is shown as <code>../CodeValue</code>
                                    <br/>
                                    If user chooses <b>"Full"</b> mode, then xpath is not processed and its actual XML value is shown, thus creating wider
                                    source columns.
                                        
                                </p>
                                 
                            </div>
                        </div>
                        <xsl:if test="$action!=1">

                            <div class="row" style="border-top: 1px #e5e5e5 solid;">
                                <br/>
                                <div class="col-sm-6">
                                    <label class="control-label" for="generators">Generators: </label>
                                    <div class="btn-group" id="generators" data-toggle="buttons">
                                        <label id="label7">
                                            <xsl:choose>
                                                <xsl:when test="$generatorsStatus='auto'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                               
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="generators" type="radio" class="toggle" value="auto" autocomplete="off">
                                                <xsl:if test="$generatorsStatus='auto'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> Auto</xsl:text>
                                        </label>
                                        <label id="label8">
                                            <xsl:choose>
                                                <xsl:when test="$generatorsStatus='manual'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="generators" type="radio" class="toggle" value="manual" autocomplete="off">
                                                <xsl:if test="$generatorsStatus='manual'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> Manual</xsl:text>
                                        </label>
                                   
                                    
                                    </div>
                                    <br/>
                                    <br/>
                               
                                    <p> 
                                        If user chooses <b>"Auto"</b>, then editor provides a list of available generator
                                        names (either built-in x3ml engine generators such as <code>UUID</code>, <code>Literal</code> or generator policy file generators, if such a file is uploaded). 
                                        <br/>
                                        Of course, user may choose to override suggestions and add a new generator name. However, any generator names that are not
                                        in the list, will be highlighted with red color.
                                        <br/>                                   
                                        If a valid generator name is selected, then arguments are created automatically and user simply
                                        fills in remaining fields.
                                
                                        <br/>
                                        Default mode is <b>"Manual"</b> for now. Once more generator policy files are uploaded and implementation is
                                        tested thoroughly, default mode will become <b>"Auto"</b>. 
                                    </p>
                                 
                                </div>                           
                            </div>
                        </xsl:if>                      
                        <div class="row" style="border-top: 1px #e5e5e5 solid;">
                            <br/>
                            <xsl:if test="$action!=1">

                                <div class="col-sm-6">
                                    <label class="control-label" for="targetAnalyzer">Target Analyzer: </label>                                                                
                                    <div class="btn-group" id="targetAnalyzer" data-toggle="buttons">
                                        <label id="label2">
                                            <xsl:choose>
                                                <xsl:when test="//output/targetAnalyzer='2'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:when test="//output/targetAnalyzer='0' or //output/targetType='xml' or //output/targetType='owl' or //output/targetType='ttl' or //output/targetType='nt' or //output/targetType='Mixed'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm disabled</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="targetAnalyzer" type="radio" class="toggle" value="2" autocomplete="off">
                                                <xsl:if test="//output/targetAnalyzer='2'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> eXist queries</xsl:text>
                                        </label>
                                        <label id="label3">
                                            <xsl:choose>
                                                <xsl:when test="//output/targetAnalyzer='3' or //output/targetType='owl' or //output/targetType='ttl' or //output/targetType='nt' or //output/targetType='Mixed'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:when test="//output/targetAnalyzer='0' or //output/targetType='xml'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm disabled</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="targetAnalyzer" type="radio" class="toggle" value="3" autocomplete="off">
                                                <xsl:if test="//output/targetAnalyzer='3'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> Jena reasoner</xsl:text>
                                        </label>
                                        <label id="label4">
                                            <xsl:choose>
                                                <xsl:when test="//output/targetAnalyzer='4' and //domain/target_node/entity/type!=''">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:when test="//output/targetAnalyzer='0' or //output/targetType!='xml' or //domain/target_node/entity/type=''">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm disabled</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="targetAnalyzer" type="radio" class="toggle" value="4" autocomplete="off">
                                                <xsl:if test="//output/targetAnalyzer='4'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> XML</xsl:text>
                                        </label>
                                        <label id="label0">
                                            <xsl:choose>
                                                <xsl:when test="//output/targetAnalyzer='0' or (//output/targetAnalyzer='4' and //domain/target_node/entity/type='')">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="targetAnalyzer" type="radio" class="toggle" value="0" autocomplete="off">
                                                <xsl:if test="//output/targetAnalyzer='0'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> None</xsl:text>
                                        </label>
                                    
                                    </div>
                                    <br/>
                                    <br/>
                              
                                    <p>
                                        <b>eXist queries</b>: It only works with RDFS or RDF schema files. 
                                        Target analyzer engine is based on Xquery queries performed on RDFS schemas stored in eXist.
                                        Schemas have to be well formed XML files containing certain tags (<code>rdfs:Class, rdf:Property, rdfs:domain</code> etc.).
                                        User chooses valid options from a select box. If there are no target schemas, user simply fills input fields with free text.
                                    </p>
                                    <p>
                                        <b>Jena reasoner</b>: It works with RDFS (RDF/XML), RDF (RDF/XML), TTL (Turtle), NT (N-Triples) or OWL (RDF/XML) schema files. 
                                        Target analyzer engine is based on Jena reasoner. User chooses valid options from a select box.
                                        If there are no target schemas, user simply fills in input fields with free text. 
                                        <b>(WARNING! The first time user clicks a row to edit, it may take some time to create combos)</b>
                                    </p>
                                    <p>
                                        <b>XML</b>: It works with <b>ONE</b> XSD or XML schema file. 
                                        After user has defined <b>MANUALLY</b> a target xpath as root, target analyzer engine parses file and discovers all available xpaths.
                                        User chooses xpaths from a select box.
                                        If there are no target schemas, user simply fills in input fields with free text. 
                                    </p>
                                    <p>
                                        <b>None</b>: It works with any type of schema files (or even without schema files at all).
                                        User simply fills in input fields with free text.
                                    </p>
                                </div>
                            </xsl:if>
                            <div class="col-sm-6">

                                <label class="control-label" for="sourcePaths">Target Paths: </label>
                                <div class="btn-group" id="targetPaths" data-toggle="buttons">
                                    <label id="label51">
                                        <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                        <input name="targetPaths" type="radio" class="toggle" value="mini" autocomplete="off">

                                        </input>                                            
                                        <xsl:text> Short</xsl:text>
                                    </label>
                                    <label id="label61">
                                        <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                        <input name="targetPaths" type="radio" class="toggle" value="full" autocomplete="off">
                                            <xsl:if test="//output/targetAnalyzer!='0'">
                                                <xsl:attribute  name="checked">checked</xsl:attribute>
                                            </xsl:if>
                                        </input>                                            
                                        <xsl:text> Full</xsl:text>
                                    </label>
                                   
                                    
                                </div>
                                <br/>
                                <br/>
                                <p>
                                    Chooses whether target paths view is stripped (<b>Short mode</b>) or complete (<b>Full mode</b>).
                                    Default mode is <b>"Short"</b>, which strips actual xpaths to create a more compact view:
                                    e.g. XML: <code>root/IdentityOfObject/CodeNumber/CodeValue</code> is shown as <code>../CodeValue</code>
                                    RDF:<code>crm:E25_Man-Made_Feature</code> is shown as <code>E25_Man-Made_Feature</code>
                                    <br/>
                                    If user chooses <b>"Full"</b> mode, then path is not processed and its actual value is shown, thus creating wider
                                    target columns.
                                        
                                </p>
                                 
                            </div>
                        </div>
                         
                       
                        <xsl:if test="$action!=1">
 
                            <div class="row" style="border-top: 1px #e5e5e5 solid;">
                                <br/>
                                <div class="col-sm-12">
                                    <label class="control-label" for="targetAnalyzer">Mapping Suggester: </label>
                                    <div class="btn-group" id="mappingSuggester" data-toggle="buttons">
                                        <label id="label9">
                                            <xsl:choose>
                                                <xsl:when test="//output/mappingSuggester='on'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>                                              
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>                                                   
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="mappingSuggester" type="radio" class="toggle" value="on" autocomplete="off">
                                                <xsl:if test="//output/mappingSuggester='on'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> On</xsl:text>
                                        </label>
                                        <label id="label10">
                                            <xsl:choose>
                                                <xsl:when test="//output/mappingSuggester!='on'">
                                                    <xsl:attribute  name="class">btn btn-default btn-sm active</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute  name="class">btn btn-default btn-sm</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <input name="mappingSuggester" type="radio" class="toggle" value="off" autocomplete="off">
                                                <xsl:if test="//output/mappingSuggester!='on'">
                                                    <xsl:attribute  name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>                                            
                                            <xsl:text> Off</xsl:text>
                                        </label>
                                    
                                    
                                    </div>
                                    <br/>
                                    <br/>
                             
                                    <p>
                                        Under construction

                                    
                                    </p>
                                </div>
                            </div>
                        </xsl:if>                  
                    </div>
                </fieldset>
            </form>
        </div>
    </xsl:template>
</xsl:stylesheet>
