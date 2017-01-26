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
    <xsl:include href="domain.xsl"/>
    <xsl:include href="path.xsl"/>
    <xsl:include href="range.xsl"/>
    <xsl:include href="entity.xsl"/>
    <xsl:include href="various.xsl"/>
    <xsl:include href="viewOnly.xsl"/>



    <xsl:output method="html"/>
    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="mappings" name="mappings">
        <xsl:if test="$action=2">
            
             <div class="btn-group-vertical  btn-sm pull-right actionsToolbar" >
                                            <button title="Click to view" id="table_view-btn" type="button" class="btn btn-default btn-sm " data-loading-text="Loading...">
                                                <span class="glyphicon glyphicon-eye-open pull-left"></span>
                                                <span class="pull-left" style="margin-left:5px;">View mode</span>
                                            </button>
                                            <button title="Click to collapse/expand all maps" id="collapseExpandAll-btn" type="button" class="btn btn-default btn-sm" data-loading-text="Loading...">
                                                <span class="glyphicon glyphicon-sort pull-left"></span> 
                                                <span class="pull-left" style="margin-left:5px;">Collapse</span>
                                               
                                                <br/>
                                               
                                                <span class="pull-left" style="margin-left:18px;">Expand All</span>

                                            </button>
                                            <button title="Click to scroll to top" id="scrollTop-btn" type="button" class="btn btn-default btn-sm" data-loading-text="Loading...">
                                                <span class="fa fa-chevron-up pull-left"></span> 
                                                <span class="pull-left" style="margin-left:3px;">Top</span>
                                            </button>
                                           
                                            <button title="Click to scroll to bottom" id="scrollBottom-btn" type="button" class="btn btn-default btn-sm" data-loading-text="Loading...">
                                                <span class="fa fa-chevron-down pull-left"></span> 
                                                <span class="pull-left" style="margin-left:3px;">Bottom</span>
                                            </button>
                                            
                                           
                                            <button title="" id="rawXML-btn" type="button" class="btn btn-default btn-sm" data-loading-text="Loading...">
                                                <span class="fa fa-lg  fa-code pull-left"></span>
                                                <span class="pull-left" style="margin-left:3px;">XML</span> 
                                            </button>
                                           
                                        </div>
        </xsl:if>
        
        
        <fieldset>
            <legend style="font-size:80% !important;padding:2px 0 2px 0;" align="right"> 
                
                <xsl:choose>
                    <xsl:when test="$action=0">Click on a row to edit the matching table</xsl:when>
                    <xsl:when test="$action=1">View mode</xsl:when>
                    <xsl:when test="$action=2">Instance Generator mode</xsl:when>

                    <xsl:otherwise>Click on a row to edit the matching table</xsl:otherwise>
                    
                    
                </xsl:choose>
            </legend>
        
            <table class="table  table-condensed">
                                   
                <xsl:for-each select="mapping">
                    <xsl:variable name="mappingPos" select="position()"/>
                    <thead>
                        <tr>
                            <th></th>
                            <th class="sourceCol">SOURCE <img class="columnHide" title="Click to collapse column" src="images/collapse-column.png"/><!--i class="columnHide fa fa-arrow-left" title="Click to collapse column"></i-->
                            </th>                           
                            <th class="targetCol">

                                <div class="row">
                                    <div class="col-xs-6">
                                        TARGET <img class="columnHide" title="Click to collapse column" src="images/collapse-column.png"/>
                                    </div>
                                    <xsl:if test="//additional">
                                        <div class="col-xs-6">
                                            CONSTANT EXPRESSION
                                        </div>
                                    </xsl:if>
                                    

                                </div>
                                
                            </th>
                            <th class="ifCol">IF RULE <img class="columnHide" title="Click to collapse column" src="images/collapse-column.png"/></th>
                            <th class="commentsHead" >COMMENTS <img class="columnHide" title="Click to collapse column" src="images/collapse-column.png"/>
                                <button  title="Click to collapse/expand map" type="button" class="btn btn-default btn-sm collapseExpand pull-right">
                                    <span class="glyphicon glyphicon-sort"></span>
                                </button>
                            </th>
                        </tr>
                    </thead>
                    <tbody class="mapping" id="{concat('//x3ml/mappings/mapping[',$mappingPos,']')}" data-xpath="{concat('//x3ml/mappings/mapping[',$mappingPos,']')}">

                        
                        <xsl:apply-templates select="domain">
                            <xsl:with-param name="mappingPos" select="$mappingPos"/>
                        </xsl:apply-templates> 
                        <xsl:for-each select="link">
                            <xsl:variable name="linkPos" select="position()"/>

                            <xsl:apply-templates select=".">
                                <xsl:with-param name="mappingPos" select="$mappingPos"/>
                                <xsl:with-param name="linkPos" select="$linkPos"/>

                            </xsl:apply-templates>      
                        </xsl:for-each>
                           
                        <tr class="empty">
                            <td  colspan="5"  style="border-left-width:0;">                              
                                <div class="row">
                                    <xsl:if test="$action!=0">
                                        <xsl:attribute name="style">display:none;</xsl:attribute>
                                    </xsl:if>
                                    <div class="col-xs-1 col-xs-offset-5">
                                        <button data-xpath="{concat('//x3ml/mappings/mapping[',$mappingPos,']/link')}" id="{concat('add***','//x3ml/mappings/mapping[',$mappingPos,']/link')}" type="button" class="btn btn-default btn-sm add" title="Click to add link">
                                            <span class="glyphicon glyphicon-plus"></span>&#160;Link</button>
                                    </div>
                                
                                    <div class="col-xs-1">
                                        <button data-xpath="{concat('//x3ml/mappings/mapping[',$mappingPos,']')}" id="{concat('add***','//x3ml/mappings/mapping[',$mappingPos,']')}" title="Click to add map" type="button" class="btn btn-default btn-sm  add">
                                            <span class="glyphicon glyphicon-plus"></span>&#160;Map</button>
                                    </div>
                                   
                                </div> 
                            </td>    
                        </tr>       
                    </tbody>
                                                  
                </xsl:for-each>
            </table>
        </fieldset>
    </xsl:template>
    
   
</xsl:stylesheet>
