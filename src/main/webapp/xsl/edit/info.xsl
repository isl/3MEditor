<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright 2014-2017 Institute of Computer Science,
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
    <xsl:include href="../externalFileLink.xsl"></xsl:include>
    <xsl:include href="namespaces.xsl"></xsl:include>

    <xsl:output method="html"></xsl:output>
    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="info" name="info">
        <div class="well">
            <form role="form" class="infoForm">
                <fieldset>
                    <legend>General</legend>
                    <div class="form-group">
                        <span class="help-block">This section consists of general information about this mapping.</span>
                        <div class="row">
                            <div class="col-sm-8 required">
                                <label class="control-label" for="generalTitle">Title</label>
                                <input id="generalTitle" type="text" class="form-control required input-sm" placeholder="Fill in value" data-xpath="//x3ml/info/title" required="required">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="title"></xsl:value-of>
                                    </xsl:attribute>
                                </input>
                                <a onclick="window.open(document.URL+'&amp;output=xml','_blank');return false;" href=""> view XML file</a>
                            </div>
                            <div class="col-sm-2">
                                <label class=" control-label" for="sourceType">Source type</label>
                                <input id="sourceType" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//x3ml/@source_type">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="//x3ml/@source_type"></xsl:value-of>
                                    </xsl:attribute>
                                </input>
                            </div>
                            <div class="col-sm-2">
                                <label class="control-label" for="version">Version</label>
                                <input id="version" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//x3ml/@version">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="//x3ml/@version"></xsl:value-of>
                                    </xsl:attribute>
                                </input>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="row">
                            <div class="col-sm-12">
                                <label class="control-label" for="generalDescription">Explanation of project</label>
                                <textarea id="generalDescription" placeholder="Fill in value" class="form-control input-sm" rows="2" data-xpath="//x3ml/info/general_description">
                                    <xsl:value-of select="general_description"></xsl:value-of>
                                </textarea>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <xsl:for-each select="source">

                    <fieldset>
                        <legend>Source</legend>
                        <div class="row">

                            <span class="help-block col-sm-8">This section consists of information about the source schema. If you upload an XSD file and define a root element manually, the "Source Analyzer" option is enabled 
                                (<b>Configuration tab</b>) and you may select source paths from a drop down.
                            </span>
                            <div class="col-sm-4">
                                <label class="control-label" for="sourceCollection">Collection</label>
                                <input type="text" id="sourceCollection" class="form-control input-sm" placeholder="Fill in value" data-xpath="//x3ml/info/source/source_collection">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="source_collection"></xsl:value-of>
                                    </xsl:attribute>
                                </input>
                            </div>
                        </div>
                        <xsl:apply-templates select="source_info"></xsl:apply-templates>
                        <div class="row">
                            <div class="col-sm-2 col-sm-offset-5">
                                <br></br>
                                <button id="addSource" type="button" class="btn btn-default btn-block  add btn-sm">
                                    <span class="glyphicon glyphicon-plus"></span>&#160;Source
                                </button>
                            </div>
                        </div>
                    </fieldset>
                </xsl:for-each>
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
                            <label class="control-label" for="targetCollection">Collection</label>
                            <input id="targetCollection" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//x3ml/info/target/target_collection">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="target_collection"></xsl:value-of>
                                </xsl:attribute>
                            </input>
                        </div>
                    </div>
                    <xsl:apply-templates select="target/target_info"></xsl:apply-templates>
                    <div class="row">
                        <div class="col-sm-2 col-sm-offset-5">
                            <br></br>
                            <button id="addTarget" type="button" class="btn btn-default btn-block  add btn-sm">
                                <span class="glyphicon glyphicon-plus"></span>&#160;Target
                            </button>
                        </div>
                    </div>
                </fieldset>
                <xsl:for-each select="../namespaces">
                    <xsl:call-template name="namespaces">
                        <xsl:with-param name="namespacesPath" select="'//x3ml/namespaces'"/>
                    </xsl:call-template>     
                </xsl:for-each>

                <xsl:apply-templates select="mapping_info"></xsl:apply-templates>
                <xsl:apply-templates select="example_data_info"></xsl:apply-templates>
            </form>
        </div>
    </xsl:template>
    <xsl:template match="source_info" name="source_info">
        <xsl:variable name="pos">
            <xsl:choose>
                <xsl:when test="@pos">
                    <xsl:value-of select="@pos"></xsl:value-of>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="position()"></xsl:value-of>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="pathSoFar">
            <xsl:value-of select="concat('//x3ml/info/source/source_info[',$pos,']')"></xsl:value-of>
        </xsl:variable>
        <div id="{$pathSoFar}" class="source_info" data-xpath="{$pathSoFar}" style="padding-top:2px;">

            <div class="form-group">

                <div class="row">
                    <xsl:if test="$pos!=1">
                        <xsl:attribute name="style">
                            <xsl:text>border-top: 1px #e5e5e5 solid;padding-top:2px;</xsl:text>
                        </xsl:attribute>
                    </xsl:if>
                    <div class="col-sm-3">
                        <label class="control-label" for="{concat($pathSoFar,'/source_schema')}">Schema</label>
                        <input id="{concat($pathSoFar,'/source_schema')}" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="{concat($pathSoFar,'/source_schema')}">
                            <xsl:attribute name="value">
                                <xsl:value-of select="source_schema"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                        <xsl:for-each select="source_schema">
                            <div>
                                <xsl:call-template name="externalFileLink">
                                </xsl:call-template>
                                <xsl:if test="@schema_file">
                                    <button class="btn btn-default btn-link btn-sm deleteFile" type="button" title="Delete source schema" id="{concat('delete***',$pathSoFar,'/source_schema/@schema_file')}">
                                        <span class="glyphicon glyphicon-remove"></span>
                                    </button>
                                </xsl:if>
                            </div>
                            <span title="Upload rdfs or xsd file" data-xpath="{concat($pathSoFar,'/source_schema')}" class="fileUpload">
                                <xsl:if test="@schema_file">
                                    <xsl:attribute name="style">
                                        <xsl:text>display:none;</xsl:text>
                                    </xsl:attribute>
                                </xsl:if>
                                
                            </span>
                        </xsl:for-each>
                    </div>
                    <div class="col-sm-2">
                        <label class="control-label" for="{concat($pathSoFar,'/source_schema/@type')}">Type</label>
                        <input id="{concat($pathSoFar,'/source_schema/@type')}" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="{concat($pathSoFar,'/source_schema/@type')}">
                            <xsl:attribute name="value">
                                <xsl:value-of select="source_schema/@type"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="col-sm-2">
                        <label class=" control-label" for="{concat($pathSoFar,'/target_schema/@version')}">Version</label>
                        <input id="{concat($pathSoFar,'/source_schema/@version')}" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="{concat($pathSoFar,'/source_schema/@version')}">
                            <xsl:attribute name="value">
                                <xsl:value-of select="source_schema/@version"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                   

                    <xsl:variable name="source_infoPos" select="position()"/>
                    <button title="Delete Source" type="button" class="close sourceInfoDeleteButton" id="{concat('delete***',$pathSoFar)}">
                      
                        <span class="fa fa-times smallerIcon" ></span>
                        <span class="sr-only">Close</span>
                    </button>    
                    <xsl:for-each select="namespaces">
                        <xsl:call-template name="namespaces">
                            <xsl:with-param name="namespacesPath" select="concat($pathSoFar,'/namespaces')"/>
                        </xsl:call-template>     
                    </xsl:for-each>

                </div>
            </div>
        </div>
    </xsl:template>
    
    
    
    <xsl:template match="target_info" name="target_info">
        <xsl:variable name="pos">
            <xsl:choose>
                <xsl:when test="@pos">
                    <xsl:value-of select="@pos"></xsl:value-of>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="position()"></xsl:value-of>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="pathSoFar">
            <xsl:value-of select="concat('//x3ml/info/target/target_info[',$pos,']')"></xsl:value-of>
        </xsl:variable>
        <div id="{$pathSoFar}" class="target_info" data-xpath="{$pathSoFar}" style="padding-top:2px;">
            <div class="form-group">
              
                <div class="row">
                    <xsl:if test="$pos!=1">
                        <xsl:attribute name="style">
                            <xsl:text>border-top: 1px #e5e5e5 solid;padding-top:2px;</xsl:text>
                        </xsl:attribute>
                    </xsl:if>
                    <div class="col-sm-3">
                        <label class="control-label" for="{concat($pathSoFar,'/target_schema')}">Schema</label>
						
                        <input title="Target schema" id="{concat($pathSoFar,'/target_schema')}" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="{concat($pathSoFar,'/target_schema')}">
                            <xsl:attribute name="value">
                                <xsl:value-of select="target_schema"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                        <div>
                            <xsl:call-template name="externalFileLink">
                            </xsl:call-template>
                            <xsl:if test="target_schema/@schema_file">
                                <button class="btn btn-default btn-link btn-sm deleteFile" type="button" title="Delete target schema" id="{concat('delete***',$pathSoFar,'/target_schema/@schema_file')}">
                                    <span class="glyphicon glyphicon-remove"></span>
                                </button>
                            </xsl:if>
                        </div>
                        <span title="Upload rdf,rdfs,owl,ttl or xsd file" data-xpath="{concat($pathSoFar,'/target_schema/@schema_file')}" class="fileUpload">
                            <xsl:if test="target_schema/@schema_file">
                                <xsl:attribute name="style">
                                    <xsl:text>display:none;</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                        </span>
                    </div>
                    <div class="col-sm-2">
                        <label class="control-label" for="{concat($pathSoFar,'/target_schema/@type')}">Type</label>
                        <input id="{concat($pathSoFar,'/target_schema/@type')}" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="{concat($pathSoFar,'/target_schema/@type')}">
                            <xsl:attribute name="value">
                                <xsl:value-of select="target_schema/@type"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="col-sm-2">
                        <label class=" control-label" for="{concat($pathSoFar,'/target_schema/@version')}">Version</label>
                        <input id="{concat($pathSoFar,'/target_schema/@version')}" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="{concat($pathSoFar,'/target_schema/@version')}">
                            <xsl:attribute name="value">
                                <xsl:value-of select="target_schema/@version"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                  					
                                        
                   
                    <xsl:variable name="target_infoPos" select="position()"/>
                    <button title="Delete Target" type="button" class="close targetInfoDeleteButton" id="{concat('delete***',$pathSoFar)}">
                       
                        <span class="fa fa-times smallerIcon" ></span>
                        <span class="sr-only">Close</span>
                    </button>    
                    <xsl:for-each select="namespaces">
                        <xsl:call-template name="namespaces">
                            <xsl:with-param name="namespacesPath" select="concat($pathSoFar,'/namespaces')"/>
                        </xsl:call-template>     
                    </xsl:for-each>
                </div>
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
                        <label class="control-label" for="mappingInfoCreated">Created by (Organization)</label>
                        <input id="mappingInfoCreated" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//info/mapping_info/mapping_created_by_org">
                            <xsl:attribute name="value">
                                <xsl:value-of select="mapping_created_by_org"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="col-sm-4">
                        <label class="control-label" for="mappingInfoPerson">Contact person(s)</label>
                        <input id="mappingInfoPerson" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//info/mapping_info/mapping_created_by_person">
                            <xsl:attribute name="value">
                                <xsl:value-of select="mapping_created_by_person"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="col-sm-4">
                        <label class=" control-label" for="mappingInfoColab">In collaboration with</label>
                        <input id="mappingInfoColab" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//info/mapping_info/in_collaboration_with">
                            <xsl:attribute name="value">
                                <xsl:value-of select="in_collaboration_with"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                </div>
            </div>
        </fieldset>
    </xsl:template>
    <xsl:template match="example_data_info" name="example_data_info">
        <fieldset>
            <legend>Sample data</legend>
            <div class="form-group">
                <span class="help-block">This section consists of information about example data (source and target) and generator policy. 
                    Once a source record XML file is uploaded, the "Transformation" tab is enabled (<b>Transformation tab</b>).
                    In order to test how your source record XML file transforms to RDF/XML, N-triples or Turtle, you will probably also have to upload a generator policy XML file. 
                </span>
                <span class="help-block">If you have not uploaded an XSD source schema yet, the "Source Analyzer" option will also be enabled 
                    once a source record XML file is uploaded (<b>Configuration tab</b>) and you may select source paths from a drop down.
                </span>
                <div class="row">
                    <div class="col-sm-3">
                        <label class="control-label" for="exampleDataFrom">Provided by</label>
                        <input id="exampleDataFrom" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//info/example_data_info/example_data_from">
                            <xsl:attribute name="value">
                                <xsl:value-of select="example_data_from"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="col-sm-3">
                        <label class="control-label" for="exampleDataPerson">Contact person(s)</label>
                        <input id="exampleDataPerson" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//info/example_data_info/example_data_contact_person">
                            <xsl:attribute name="value">
                                <xsl:value-of select="example_data_contact_person"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="col-sm-2">
                        <label class=" control-label" for="exampleDataSourceRecord">Source record</label>
                        <input id="exampleDataSourceRecord" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//info/example_data_info/example_data_source_record">
                            <xsl:attribute name="value">
                                <xsl:value-of select="example_data_source_record"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                        <xsl:for-each select="example_data_source_record/@*">
                            <div class="{name()}">
                                                        
                                <xsl:call-template name="externalFileLink">
                                </xsl:call-template>
                                <xsl:choose>
                                    <xsl:when test="name()='html_link'">
                                        <button class="btn btn-default btn-link btn-sm deleteFile" type="button" title="Delete html file" id="{concat('delete***//x3ml/info/example_data_info/example_data_source_record/@html_link')}">
                                            <span class="glyphicon glyphicon-remove"></span>
                                        </button>
                                    </xsl:when>
                                    <xsl:when test="name()='xml_link'">
                                        <button class="btn btn-default btn-link btn-sm deleteFile" type="button" title="Delete xml file" id="{concat('delete***//x3ml/info/example_data_info/example_data_source_record/@xml_link')}">
                                            <span class="glyphicon glyphicon-remove"></span>
                                        </button>
                                    </xsl:when>
                                </xsl:choose>
                            </div>
                        </xsl:for-each>
                                                
                        <span data-xpath="//x3ml/info/example_data_info/example_data_source_record/@html_link" class="fileUpload html_link">
                            <xsl:if test="example_data_source_record/@html_link">
                                <xsl:attribute name="style">
                                    <xsl:text>display:none;</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                        </span>
                        <span data-xpath="//x3ml/info/example_data_info/example_data_source_record/@xml_link" class="fileUpload xml_link">
                            <xsl:if test="example_data_source_record/@xml_link">
                                <xsl:attribute name="style">
                                    <xsl:text>display:none;</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                        </span>

						
                    </div>
                    <div class="col-sm-2">
                        <label class=" control-label" for="generatorPolicy">Generator policy</label>
                        <input id="generatorPolicy" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//info/example_data_info/generator_policy_info">
                            <xsl:attribute name="value">
                                <xsl:value-of select="generator_policy_info"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                        <xsl:for-each select="generator_policy_info">
                                                    
                            <div class="generator_link">
                                <xsl:for-each select="@generator_link">
                                    <xsl:call-template name="externalFileLink">
                                    </xsl:call-template>
                                </xsl:for-each>
                                <xsl:if test="@generator_link">
                                    <button class="btn btn-default btn-link btn-sm deleteFile" type="button" title="Delete generator policy" id="{concat('delete***//x3ml/info/example_data_info/generator_policy_info/@generator_link')}">
                                        <span class="glyphicon glyphicon-remove"></span>
                                    </button>
                                </xsl:if>
                            </div>
                            <span data-xpath="{concat('//x3ml/info/example_data_info/generator_policy_info/@generator_link')}" class="fileUpload">
                                <xsl:if test="@generator_link">
                                    <xsl:attribute name="style">
                                        <xsl:text>display:none;</xsl:text>
                                    </xsl:attribute>
                                </xsl:if>
                            </span>
                                                    
                                                    
                        </xsl:for-each>
                    </div>
                    <div class="col-sm-2">
                        <label class=" control-label" for="exampleDataTargetRecord">Target record</label>
                        <input id="exampleDataTargetRecord" type="text" class="form-control input-sm" placeholder="Fill in value" data-xpath="//info/example_data_info/example_data_target_record">
                            <xsl:attribute name="value">
                                <xsl:value-of select="example_data_target_record"></xsl:value-of>
                            </xsl:attribute>
                        </input>
                        <xsl:for-each select="example_data_target_record">
                                                    
                            <div>
                                <xsl:call-template name="externalFileLink">
                                </xsl:call-template>
                                <xsl:if test="@rdf_link">
                                    <button class="btn btn-default btn-link btn-sm deleteFile" type="button" title="Delete rdf link" id="{concat('delete***//x3ml/info/example_data_info/example_data_target_record/@rdf_link')}">
                                        <span class="glyphicon glyphicon-remove"></span>
                                    </button>
                                </xsl:if>
                            </div>
                            <span data-xpath="{concat('//x3ml/info/example_data_info/example_data_target_record/@rdf_link')}" class="fileUpload">
                                <xsl:if test="@rdf_link">
                                    <xsl:attribute name="style">
                                        <xsl:text>display:none;</xsl:text>
                                    </xsl:attribute>
                                </xsl:if>
                            </span>
                                                    
                                                    
                        </xsl:for-each>
                    </div>
                </div>
            </div>
        </fieldset>
	
    </xsl:template>
</xsl:stylesheet>
