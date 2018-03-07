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
    <xsl:include href="generators/instance_generator.xsl"/>
    <xsl:include href="generators/label_generator.xsl"/>

    
    <xsl:output method="html"/>

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="entity" name="entity">   
        <xsl:param name="pathSoFar"/>
        <div class="row">
            <div>
                <xsl:choose>
                    <xsl:when test="additional">
                        <xsl:attribute name="class">col-xs-6 iconContainer</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="class">col-xs-12 iconContainer</xsl:attribute>

                    </xsl:otherwise>
                </xsl:choose>    
               
                <xsl:for-each select="type">
                    <xsl:variable name="strippedURL">
                        <xsl:choose>
                            <xsl:when test="contains(.,'rdf-schema#Literal')">
                                <xsl:text>rdf-schema#Literal</xsl:text>                        
                            </xsl:when>
                            <xsl:when test="substring(., string-length())='/'">
                                <xsl:call-template name="substring-after-last-and-remove-prefix">
                                    <xsl:with-param name="string" select="substring(., 0, string-length())" />
                                    <xsl:with-param name="delimiter" select="'/'" />
                                </xsl:call-template>
                                <xsl:text>/</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:call-template name="substring-after-last-and-remove-prefix">
                                    <xsl:with-param name="string" select="." />
                                    <xsl:with-param name="delimiter" select="'/'" />
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
              
                    </xsl:variable>
            
                    <xsl:if test="position()=1">
                        <span style="float:left;">
                            <xsl:choose>

                                <xsl:when test="name(../../..)='range'">
                                    <xsl:if test="position()=1">
                                        <span >
                                            <xsl:choose>
                                                <xsl:when test="../additional">
                                                    <xsl:attribute name="style">margin-right:0px;</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute name="style">margin-right:30px;</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                    
                                            <img src="images/range.png"/>
                                        </span>   
                                    </xsl:if>  
                                </xsl:when>
                                <xsl:when test="name(../../..)='domain'">
                                    <xsl:if test="position()=1">
                                        <span >
                                            <xsl:choose>
                                                <xsl:when test="../additional">
                                                    <xsl:attribute name="style">margin-right:0px;</xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:attribute name="style">margin-right:30px;</xsl:attribute>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                    
                                            <img src="images/domain.png"/>
                                        </span>   
                                  
                                    
                                    
                                    </xsl:if>  
                                </xsl:when>
                                <xsl:otherwise>
                                    <span>
                                        <xsl:choose>
                                            <xsl:when test="../additional">
                                                <xsl:attribute name="style">margin-right:0px;</xsl:attribute>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:attribute name="style">margin-right:30px;</xsl:attribute>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    
                                        <img src="images/intermediate.png"/>
                                    </span>   
                               
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:if test="../additional">
                                <span style="margin-right:7px;">
                                    <img src="images/constant.png"/>
                                </span> 
                            </xsl:if>
                        </span>
                    </xsl:if>
          
            
                    <span title="{.}" class="targetPath" data-miniPath="{$strippedURL}" data-fullPath="{.}">
                        <xsl:if test="../additional and string-length($strippedURL)>20"> <!-- Trying to follow Korina's guidelines-->
                            <xsl:attribute name="style">display: block;</xsl:attribute>  
                        </xsl:if>
                        <xsl:if test="position()>1">
                            <xsl:attribute name="style">margin-left:53px;</xsl:attribute>                 
                        </xsl:if> 
                        <xsl:value-of select="$strippedURL"/>  
                    </span>
                    <xsl:if test="position()=1">
                        <div class="instance_info">
                            <xsl:attribute name="style">
                                <xsl:choose>
                                    <xsl:when test="../@variable or ../@global_variable or 
 ../instance_info/constant or ../instance_info/language or ../instance_info/description">
                                    </xsl:when>
                                    <xsl:otherwise>
                                        background-color:transparent;
                                    </xsl:otherwise>
                                </xsl:choose>
                                
                            </xsl:attribute>
                            
                            <xsl:choose>
                                <xsl:when test="../instance_info/constant">
                                    <xsl:if test=".!=''">
                        &#160;=&#160;<span class="constant">"<xsl:value-of select="../instance_info/constant"/>"</span>       
                                        <xsl:if test="../@variable or ../@global_variable  or ../instance_info/language or ../instance_info/description">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                    </xsl:if>       
                                </xsl:when>
                                <xsl:otherwise>&#160;</xsl:otherwise>
                            </xsl:choose>
                            <xsl:if test="../@variable">
                                <span style="margin-left:3px;">
                                    <img style="" src="images/MapVariable-Icon.png" title="variable applied on map"/>

                                    <span  class="variable" title="variable applied on map"> 
                                        <xsl:text> [</xsl:text>
                                        <xsl:value-of select="../@variable"/>  
                                        <xsl:text>]</xsl:text>
                                    </span>                           							
                                    <xsl:if test="../@global_variable or ../instance_info/language or ../instance_info/description">
                                        <xsl:text>, </xsl:text>
                                    </xsl:if>
                                </span>
                            </xsl:if>
                            <xsl:if test="../@global_variable">
                                <span style="margin-left:3px;">
                                    <img style="" src="images/GlobalVariable-Icon.png" title="variable applied globally"/>

                                    <span class="global_variable" title="variable applied globally">
                                        <xsl:text> [</xsl:text>
                                        <xsl:value-of select="../@global_variable"/>
                                        <xsl:text>]</xsl:text>
                                    </span>                           							
                                   
                                    <xsl:if test="../instance_info/language or ../instance_info/description">
                                        <xsl:text>, </xsl:text>
                                    </xsl:if>
                                </span>
                            </xsl:if>
                           
                           
                  
                            
                            <xsl:choose>
                                <xsl:when test="../instance_info/description">
                        &#160;
                                    <xsl:variable name="description" select="../instance_info/description"/>
                                    <img style="" src="images/Description-Icon.png" class="description" title="Description" data-container="body" data-trigger="focus" data-toggle="popover" data-content="{$description}"/>
                                    <xsl:if test="../instance_info/language">
                                        <xsl:text> , </xsl:text>
                                    </xsl:if>                               
                                </xsl:when>
                                <xsl:otherwise>&#160;
                      
                        
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="../instance_info/language">
                        &#160;
                                    <span class="language">
                                        <xsl:value-of select="../instance_info/language"/>
                                    </span>                               
                                </xsl:when>
                                <xsl:otherwise>&#160;
                        
                        
                                </xsl:otherwise>
                            </xsl:choose>
                        </div>
                    </xsl:if>
                    <br/>
                  
                   
                </xsl:for-each>
               
                <xsl:if test="$action=2">
          
                    <!-- Entity generators -->
                    <div id="{concat($pathSoFar,'/generators')}">
                        <xsl:attribute name="class">               
                            <xsl:choose>
                                <xsl:when test="additional">
                                    <xsl:text>generatorsBox</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>generatorsBox</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:for-each select="instance_generator">
                            <xsl:call-template name="instance_generator">
                                <xsl:with-param name="pathSoFar" select="concat($pathSoFar,'/instance_generator')"/>
                            </xsl:call-template>
                        </xsl:for-each>
                
                        <xsl:for-each select="label_generator">
                            <xsl:call-template name="label_generator">
                                <xsl:with-param name="pathSoFar" select="concat($pathSoFar,'/label_generator[',position(),']')"/>
                            </xsl:call-template>
                        </xsl:for-each>
                        <div class="col-xs-12 generatorButtons"> 
                            <button data-loading-text="Adding..."  class="btn btn-link btn-sm add"  data-xpath="{concat('add***',$pathSoFar,'/instance_generator')}" id="{concat('add***',$pathSoFar,'/instance_generator')}" title="Add Instance Generator" type="button">
                                <xsl:if test="instance_generator">
                                    <xsl:attribute name="style">display:none;</xsl:attribute>
                                </xsl:if>
                                <xsl:text>Add instance generator</xsl:text>
                            </button>
                        </div>
                        <div class="col-xs-12 generatorButtons"> 
                            <button data-loading-text="Adding..."  class="btn btn-link btn-sm add" data-xpath="{concat('add***',$pathSoFar,'/label_generator')}" id="{concat('add***',$pathSoFar,'/label_generator')}" title="Add Label Generator" type="button">
                                <xsl:text>Add label generator</xsl:text>
                            </button>
                        </div>
                
                    </div>   
          
                </xsl:if>
            </div>
        
            <xsl:if test="additional">
                <div class="grayColor col-xs-6">
                    <xsl:for-each select="additional">
                        <xsl:apply-templates select=".">
                            <xsl:with-param name="pathSoFar" select="concat($pathSoFar,'/additional[',position(),']/entity')"/>
                        </xsl:apply-templates>
                    </xsl:for-each>
                </div>
            </xsl:if>
           
       
        
        </div>
       
    </xsl:template>
    
    <xsl:template match="additional">
        <xsl:param name="pathSoFar"/>
            
        <div class="row">
            <div class="col-xs-5">

                  
                <xsl:text>[</xsl:text>
                <xsl:variable name="strippedRelationship">
                    <xsl:choose>
                        <xsl:when test="substring(relationship, string-length())='/'">
                            <xsl:call-template name="substring-after-last-and-remove-prefix">
                                <xsl:with-param name="string" select="substring(relationship, 0, string-length())" />
                                <xsl:with-param name="delimiter" select="'/'" />
                            </xsl:call-template>
                            <xsl:text>/</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="substring-after-last-and-remove-prefix">
                                <xsl:with-param name="string" select="relationship" />
                                <xsl:with-param name="delimiter" select="'/'" />
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                  
                </xsl:variable>
                <i title="{relationship}" class="targetPath" data-miniPath="{$strippedRelationship}" data-fullPath="{relationship}">
                    <xsl:value-of select="$strippedRelationship"/>
                </i>
                <xsl:text>]</xsl:text>

            </div>
            <div class="col-xs-7">

                  
                <xsl:text>[</xsl:text>
              
               
                <xsl:variable name="strippedURL">
                    <xsl:choose>
                        <xsl:when test="contains(.,'rdf-schema#Literal')">
                            <xsl:text>rdf-schema#Literal</xsl:text>                        
                        </xsl:when>
                        <xsl:when test="substring(entity/type, string-length())='/'">
                            <xsl:call-template name="substring-after-last-and-remove-prefix">
                                <xsl:with-param name="string" select="substring(entity/type, 0, string-length())" />
                                <xsl:with-param name="delimiter" select="'/'" />
                            </xsl:call-template>
                            <xsl:text>/</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="substring-after-last-and-remove-prefix">
                                <xsl:with-param name="string" select="entity/type" />
                                <xsl:with-param name="delimiter" select="'/'" />
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
              
                </xsl:variable>
                <span title="{entity/type}" class="targetPath" data-miniPath="{$strippedURL}" data-fullPath="{entity/type}">
                    <xsl:value-of select="$strippedURL"/> 
                </span>
                <xsl:choose>
                    <xsl:when test="entity/instance_info/constant">
                        <xsl:if test=".!=''">
                        &#160;=&#160;<span class="constant">"<xsl:value-of select="entity/instance_info/constant"/>"</span>       
                            
                        </xsl:if>       
                    </xsl:when>
                    <xsl:otherwise>&#160;</xsl:otherwise>
                </xsl:choose>
                  
                <xsl:text>]</xsl:text>
                
                <xsl:if test="entity/instance_info/language or entity/instance_info/description">
                    
                    <br/>
                    <div class="instance_info">
                        <xsl:if test="entity/instance_info/description">
                            <xsl:variable name="description" select="entity/instance_info/description"/>
                            <img style="" src="images/Description-Icon.png" class="description" title="Description" data-container="body" data-trigger="focus" data-toggle="popover" data-content="{$description}"/>
                            <xsl:if test="entity/instance_info/language">
                                <xsl:text> , </xsl:text>
                            </xsl:if>                             
                            
                        </xsl:if>
                         &#160;
                        <span class="language">
                            <xsl:value-of select="entity/instance_info/language"/>
                        </span>     
                    </div>
                </xsl:if> 

            </div>
        </div>
        <xsl:if test="$action=2">
            <div id="{concat($pathSoFar,'/generators')}" class="col-xs-12 generatorsBox">
              
               
               
                <xsl:for-each select="entity/instance_generator">
                    <xsl:call-template name="instance_generator">
                        <xsl:with-param name="pathSoFar" select="concat($pathSoFar,'/instance_generator')"/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:for-each select="entity/label_generator">
                    <xsl:call-template name="label_generator">
                        <xsl:with-param name="pathSoFar" select="concat($pathSoFar,'/label_generator[',position(),']')"/>
                    </xsl:call-template>
                </xsl:for-each>
                <div class="col-xs-12 additionalGeneratorButtons"> 

                    <button data-loading-text="Adding..."  style="padding-left:0;padding-right:20px;" class="btn btn-link btn-sm add"  data-xpath="{concat('add***',$pathSoFar,'/instance_generator')}" id="{concat('add***',$pathSoFar,'/instance_generator')}" title="Add Instance Generator" type="button">
                        <xsl:if test="entity/instance_generator">
                            <xsl:attribute name="style">display:none;padding-left:0;padding-right:20px;</xsl:attribute>
                        </xsl:if>
                        <xsl:text>Add instance generator</xsl:text>
                    </button>
                </div>
                <div class="col-xs-12 additionalGeneratorButtons"> 
                    <button data-loading-text="Adding..."  style="padding-left:0;" class="btn btn-link btn-sm add"  data-xpath="{concat('add***',$pathSoFar,'/label_generator')}" id="{concat('add***',$pathSoFar,'/label_generator')}" title="Add Label Generator" type="button">
                        <xsl:text>Add label generator</xsl:text>
                    </button>
                </div>
                
            </div>
        </xsl:if>
              
    </xsl:template>

</xsl:stylesheet>
