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
    <xsl:include href="externalFileLink.xsl"/>
    <xsl:output method="html"/>

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="info" name="info">
        <div class="well" id="infoWell" >                     
            <form role="form" class="infoForm">
                <fieldset >
                    
                    <legend >General</legend>
                    <div class="form-group">
                        <span class="help-block">This section consists of general information about this mapping.</span>
                        <div class="row">
                            <div class="col-sm-8">
                                <label class="control-label">Title</label>                                   
                                <p class="form-control-static">
                                    <xsl:value-of select="title"/>
                                    <a  onclick="window.open(document.URL+'&amp;output=xml','_blank');return false;"  href=""> view XML file</a>

                                </p>
                            </div>
                            <div class="col-sm-2"> 
                                <label class=" control-label">Source type</label>
                                <p class="form-control-static">
                                    <xsl:value-of select="//x3ml/@source_type"/>
                                </p>
                            </div>
                            <div class="col-sm-2">          
                                <label class="control-label">Version</label>
                                <p class="form-control-static">
                                    <xsl:value-of select="//x3ml/@version"/>
                                </p>
                            </div>
                        </div>
                    </div>  
                    <div class="form-group">    
                        <div class="row">  
                            <div class="col-sm-12">
                                <label class="control-label">Explanation of project</label>
                                <p class="form-control-static">
                                    <xsl:value-of select="general_description"/>
                                </p>
                            </div>
                        </div>
                    
                    </div>
                </fieldset>       
                <xsl:for-each select="source[1]">
                    <fieldset>
                        <legend>Source</legend>
                        <div class="row">
                            <span class="help-block col-sm-8">This section consists of information about the source schema. If you upload an XSD file and define a root element manually, the "Source Analyzer" option is enabled 
                                (<b>Configuration tab</b>) and you may select source paths from a drop down.
                            </span>
                            <div class="col-sm-4"> 
                                <label class="control-label">Collection</label>
                                <p class="form-control-static">
                                    <xsl:value-of select="source_collection"/>
                                </p>
                            </div>
                        </div>
                        <xsl:apply-templates select="source_info"/>             
                    </fieldset> 
                </xsl:for-each>     
                <xsl:for-each select="target[1]">
                    <fieldset>
                        <legend>Target</legend>
                        <div class="row">
                            <span class="help-block col-sm-8">This section consists of information about the target schema(s). If you do not upload at least one target schema file,
                                then you will have to fill in target paths using text input fields. Once a target schema file is uploaded (for xsd files you will also 
                                have to define a root element manually), the "Target Analyzer" option is
                                enabled (<b>Configuration tab</b>) and you may use one of our analyzers. If you choose
                                to do so, you may select appropriate target paths from a drop down.
                            </span>
                            <div class="col-sm-4">
                                <label class="control-label">Collection</label>
                                <p class="form-control-static">
                                    <xsl:value-of select="target_collection"/>
                                </p>
                            </div> 
                        </div>
                        <xsl:apply-templates select="target_info"/>     
                    </fieldset>       
                </xsl:for-each>
                <xsl:for-each select="//x3ml/namespaces">
                    <fieldset>
                        <legend>Namespaces</legend>
                        <div class="row">
                            <span class="help-block col-sm-12">This section consists of information about namespaces not declared in source or target schemas block.
                            </span>
                           
                        </div>
                        <xsl:call-template name="namespaces"/>     
                    </fieldset>       
                </xsl:for-each>
                <xsl:apply-templates select="mapping_info"/>      
                <xsl:apply-templates select="example_data_info"/>    
            </form>
        </div>
    </xsl:template>
    
    <xsl:template match="source_info" name="source_info">
       
        <div class="form-group" style="padding-top:2px;">
            <div class="row">
                <xsl:if test="position()!=last()">
                    <xsl:attribute name="style">
                        <xsl:text>border-bottom: 1px #e5e5e5 solid;"</xsl:text>
                    </xsl:attribute>
                </xsl:if>
                <div class="col-sm-3">
                    <label class="control-label">Schema</label>                                   
                    <p class="form-control-static">
                        <xsl:value-of select="source_schema"/>   
                            &#160; 
                        <xsl:for-each select="source_schema">
                            <xsl:call-template name="externalFileLink" >       

                            </xsl:call-template>
                        </xsl:for-each>
                    </p>
                </div> 
                <div class="col-sm-2">          
                    <label class="control-label">Type</label>
                    <p class="form-control-static">
                        <xsl:value-of select="source_schema/@type"/>
                    </p>
                </div>
                <div class="col-sm-2"> 
                    <label class=" control-label">Version</label>
                    <p class="form-control-static">
                        <xsl:value-of select="source_schema/@version"/>
                    </p>
                </div>
                <xsl:for-each select="namespaces">
                    <xsl:call-template name="namespaces"/>     
                </xsl:for-each>
                    
                
            </div>
        </div>
    </xsl:template>
    
    <xsl:template  name="namespaces">
        <xsl:choose>
            <xsl:when test="name(..)='x3ml'">
                <xsl:for-each select="namespace">
                    <div class="row">
            
                        <div class="col-sm-4">
                            <label class="control-label">Namespace prefix</label>    
                            <p class="form-control-static">
                                <xsl:value-of select="@prefix"/>   
                                     
                            </p>
                        </div>
                        <div class="col-sm-8">                                  
                            <label class="control-label">Namespace uri</label>       
                            <p class="form-control-static">
                                <xsl:value-of select="@uri"/>
                            </p>             
                        </div>
                    </div> 
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:for-each select="namespace">
                    <div style="padding-left:0;">
                        <xsl:attribute name="class">
                            <xsl:choose>
                                <xsl:when test="position()=1">
                                    <xsl:text>col-sm-5</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>col-sm-5 col-sm-offset-7</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <div class="col-sm-4">
                            <label class="control-label">Namespace prefix</label>    
                            <p class="form-control-static">
                                <xsl:value-of select="@prefix"/>   
                                     
                            </p>
                        </div>
                        <div class="col-sm-8">                                  
                            <label class="control-label">Namespace uri</label>       
                            <p class="form-control-static">
                                <xsl:value-of select="@uri"/>
                            </p>             
                        </div>
                    </div> 
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>
              

                   
    </xsl:template>
    
    <xsl:template match="target_info" name="target_info">
           
        <div class="form-group" style="padding-top:2px;">
            
            <div class="row">
                <xsl:if test="position()!=last()">
                    <xsl:attribute name="style">
                        <xsl:text>border-bottom: 1px #e5e5e5 solid;"</xsl:text>
                    </xsl:attribute>
                </xsl:if>
                <div class="col-sm-3">
                    <label class="control-label">Schema</label>                                   
                    <p class="form-control-static">
                        <xsl:value-of select="target_schema"/>   
                        &#160;                        
                        <xsl:call-template name="externalFileLink">       

                        </xsl:call-template>
                    </p>
                </div> 
                <div class="col-sm-2">          
                    <label class="control-label">Type</label>
                    <p class="form-control-static">
                        <xsl:value-of select="target_schema/@type"/>
                    </p>
                </div>
                <div class="col-sm-2"> 
                    <label class=" control-label">Version</label>
                    <p class="form-control-static">
                        <xsl:value-of select="target_schema/@version"/>
                    </p>
                </div>
                <xsl:for-each select="namespaces">
                    <xsl:call-template name="namespaces"/>     
                </xsl:for-each>
 
            </div>               
        </div>

       
           
    </xsl:template>
    
    <xsl:template match="mapping_info" name="mapping_info">
        <fieldset>
            <legend>Mapping</legend>
            <div class="form-group">
                <span class="help-block">This section consists of information about who creates and supports this mapping.
                </span>
                <div class="row">
                    <div class="col-sm-4">
                        <label class="control-label">Created by (Organization)</label>                                   
                        <p class="form-control-static">
                            <xsl:value-of select="mapping_created_by_org"/>   
                                                
                        </p>
                    </div> 
                    <div class="col-sm-4">          
                        <label class="control-label">Contact person(s)</label>
                        <p class="form-control-static">
                            <xsl:value-of select="mapping_created_by_person"/>
                        </p>
                    </div>
                    <div class="col-sm-4"> 
                        <label class=" control-label">In collaboration with</label>
                        <p class="form-control-static">
                            <xsl:value-of select="in_collaboration_with"/>
                        </p>
                    </div>
                   
                   
                </div>               
            </div>
           
        </fieldset>         
    </xsl:template>
    <xsl:template match="example_data_info" name="example_data_info">
        <fieldset>
            <legend>Sample data and Generator policy</legend>
            <div class="form-group">
                <span class="help-block">This section consists of information about example data (source and target), generator policy and thesaurus. 
                    Once a source record XML file is uploaded, the "Transformation" tab is enabled (<b>Transformation tab</b>).
                    In order to test how your source record XML file transforms to RDF/XML, N-triples or Turtle, you will probably also have to upload a generator policy XML file. 
                    You may also want to upload a thesaurus file.
                    <br/>If you have not uploaded an XSD source schema yet, the "Source Analyzer" option will also be enabled 
                    once a source record XML file is uploaded (<b>Configuration tab</b>) and you may select source paths from a drop down.
                </span>
                <div class="row">
                    <div class="col-sm-8">
                        <label class="control-label">Provided by</label>                                   
                        <p class="form-control-static">
                            <xsl:value-of select="example_data_from"/>   
                                                
                        </p>
                    </div> 
                   
                    <div class="col-sm-2"> 
                        <label class=" control-label">Source record</label>
                        <p class="form-control-static">
                            <xsl:value-of select="example_data_source_record"/>
                            <br/>
                            <xsl:for-each select="example_data_source_record/@*">                                                                                                            
                                <xsl:call-template name="externalFileLink">
                                </xsl:call-template>                                                     
                                <br/>
                            </xsl:for-each>
                            
                        </p>
                    </div>
                   
                    <div class="col-sm-2"> 
                        <label class=" control-label">Target record</label>
                        <p class="form-control-static">
                            <xsl:value-of select="example_data_target_record"/>
                            <!--                            <a href="#"> view rdf</a> -->
                            <br/>
                            <xsl:for-each select="example_data_target_record">
                                <xsl:call-template name="externalFileLink">       

                                </xsl:call-template>
                            </xsl:for-each>
                        </p>
                    </div>
                   
                </div>          
                <div class="row">
                    <div class="col-sm-8">          
                        <label class="control-label">Contact person(s)</label>
                        <p class="form-control-static">
                            <xsl:value-of select="example_data_contact_person"/>
                        </p>
                    </div>
                    <div class="col-sm-2"> 
                        <label class=" control-label">Generator policy</label>
                        <p class="form-control-static">
                            <xsl:value-of select="generator_policy_info"/>
                            <br/>
                            <xsl:for-each select="generator_policy_info/@generator_link">
                                <xsl:call-template name="externalFileLink">      

                                </xsl:call-template>
                            </xsl:for-each>
                        </p>
                    </div>
                    <div class="col-sm-2"> 
                        <label class=" control-label">Thesaurus</label>
                        <p class="form-control-static">
                            <xsl:value-of select="thesaurus_info"/>
                            <br/>
                            <xsl:for-each select="thesaurus_info/@thesaurus_link">
                                <xsl:call-template name="externalFileLink">       

                                </xsl:call-template>
                            </xsl:for-each>
                        </p>
                    </div>
                   
                </div>                 
            </div>
           
        </fieldset>         
    </xsl:template>
</xsl:stylesheet>
