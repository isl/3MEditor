<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright 2014-2019  Institute of Computer Science,
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

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    
    
    <xsl:template name="namespace" match="namespace">
        <xsl:param name="namespaceXpath"/>

        <xsl:variable name="xpath">
            <xsl:choose>
                <xsl:when test="$namespaceXpath!=''">
                    <xsl:value-of select="$namespaceXpath"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@xpath"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="pos">
            <xsl:choose>
                <xsl:when test="@pos">
                    <xsl:value-of select="@pos"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="position()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:if test="$xpath!=''">
            <div  id="{$xpath}" data-xpath="{$xpath}">
          
                <xsl:choose>
                    <xsl:when test="contains($xpath,'source') or contains($xpath,'target')">
                        <xsl:attribute name="class">                    
                            <xsl:choose>
                                <xsl:when test="$pos=1">
                                    <xsl:text>col-sm-5 namespace</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>col-sm-5 col-sm-offset-7 namespace</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>       
                        </xsl:attribute>
                        <xsl:attribute name="style">
                            <xsl:text>padding-left:0;</xsl:text>
                        </xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="class">
                            <xsl:text>row namespace</xsl:text>
                        </xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            
                <div class="col-sm-4">
                    <label class="control-label">Namespace prefix</label>    
                    <input id="{concat($xpath,'/@prefix')}" type="text" class="form-control input-sm namePrefix" placeholder="Fill in value" data-xpath="{concat($xpath,'/@prefix')}">
                        <xsl:attribute name="value">
                            <xsl:value-of select="@prefix"></xsl:value-of>
                        </xsl:attribute>
                    </input>
                </div>
                <div class="col-sm-7" style="padding-right:0;padding-bottom:2px;">                                  
                    <label class="control-label">Namespace uri</label>       
                    <input id="{concat($xpath,'/@uri')}" type="text" class="form-control input-sm nameURI" placeholder="Fill in value" data-xpath="{concat($xpath,'/@uri')}">
                        <xsl:attribute name="value">
                            <xsl:value-of select="@uri"></xsl:value-of>
                        </xsl:attribute>
                    </input>        
                  
                </div>
                     
         
                <button title="Delete Namespace" type="button" id="{concat('delete***',$xpath)}">
                    <xsl:attribute name="class">
                        <xsl:choose>
                            <xsl:when test="$pos!=1 or count(../namespace)>1">     
                                <xsl:text>close namespaceDeleteButton </xsl:text> 
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>close namespaceDeleteButton hidden</xsl:text> 
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <span class="fa fa-times smallerIcon" ></span>
                    <span class="sr-only">Close</span>
                </button>    
           
                        
            </div> 
             
        </xsl:if>

                   
    </xsl:template>
    
    

</xsl:stylesheet>
