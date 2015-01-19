<%--

    Copyright © ${project.inceptionYear} Instituto Superior Técnico

    This file is part of Fenix IST.

    Fenix IST is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Fenix IST is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page isELIgnored="true"%>
<%@ page language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/struts-example-1.0" prefix="app" %>

<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter"%>
<html:xhtml/>

<%@page import="org.apache.struts.util.RequestUtils"%>

<em><bean:message key="label.researchPortal" bundle="RESEARCHER_RESOURCES"/></em>
<h2 id="header"><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.viewCurriculum.title"/></h2>

<bean:define id="personId" name="person" property="externalId"/>

<fr:form action="<%="/viewCurriculum.do?personOID=" + personId %>">
<fr:edit id="executionYearIntervalBean" name="executionYearIntervalBean" visible="false"/>

<p class="mbottom025"><bean:message key="label.choosen.interval" bundle="RESEARCHER_RESOURCES"/>:</p>

<table class="tstyle5 mtop025">
<tr>
	<td>
		<bean:message key="label.start" bundle="RESEARCHER_RESOURCES"/>:	  
		<fr:edit id="firstYear" name="executionYearIntervalBean" slot="firstExecutionYear">
			<fr:layout name="menu-select">
			<fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.ExecutionYearsProvider"/>
			<fr:property name="format" value="${year}"/>
			<fr:property name="defaultText" value="label.undefined"/>
			<fr:property name="key" value="true"/>
			<fr:property name="bundle" value="RESEARCHER_RESOURCES"/>
		</fr:layout>
		</fr:edit>
	</td>						  
	<td>
		<bean:message key="label.end" bundle="RESEARCHER_RESOURCES"/>:

		<fr:edit id="finalYear" name="executionYearIntervalBean" slot="finalExecutionYear">
			<fr:layout name="menu-select">
			<fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.ExecutionYearsProvider"/>
			<fr:property name="format" value="${year}"/>
			<fr:property name="defaultText" value="label.undefined"/>
			<fr:property name="key" value="true"/>
			<fr:property name="bundle" value="RESEARCHER_RESOURCES"/>
		</fr:layout>
		</fr:edit>
    </td>                         
    <td>
		<html:submit><bean:message key="label.filter" bundle="RESEARCHER_RESOURCES"/></html:submit>
	</td>
</tr>
</table>
</fr:form>

<p id="index" class="mbottom025"><bean:message key="label.index" />:</p>
<ol class="mtop025">
	<li><%=GenericChecksumRewriter.NO_CHECKSUM_PREFIX%><a href="#personalInformationTitle"><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.viewCurriculum.personalInformationTitle"/></a></li>
    <logic:notEmpty name="lectures">
    <li><%=GenericChecksumRewriter.NO_CHECKSUM_PREFIX%><a href="#lecturesTitle"><bean:message bundle="DEPARTMENT_MEMBER_RESOURCES" key="label.teacher.details.lecturedCoursesInformation"/></a></li>
	</logic:notEmpty>
	<logic:notEmpty  name="orientedThesis">	
		<li><%=GenericChecksumRewriter.NO_CHECKSUM_PREFIX%><a href="#guidancesTitle"><bean:message bundle="DEPARTMENT_MEMBER_RESOURCES" key="label.teacher.details.orientationInformation"/></a></li>
	</logic:notEmpty>
	<logic:notEmpty name="functions">
	<li><%=GenericChecksumRewriter.NO_CHECKSUM_PREFIX%><a href="#functionsTitle"><bean:message bundle="DEPARTMENT_MEMBER_RESOURCES" key="label.teacher.details.functionsInformation"/></a></li>
	</logic:notEmpty>
</ol>

<!-- Personal Information -->
<p id='personalInformationTitle' class="separator2" style="position: relative; width: 99%; margin-top: 2em;">
	<span><bean:message bundle="RESEARCHER_RESOURCES" key="researcher.viewCurriculum.personalInformationTitle"/></span>
	<span style="position: absolute; right: 5px;"><%=GenericChecksumRewriter.NO_CHECKSUM_PREFIX%><a href="#header"><bean:message key="label.top" /></a></span>
</p>

<ul>			
<li><bean:message key="researcher.viewCurriculum.name" bundle="RESEARCHER_RESOURCES"/>: <strong><fr:view name="person" property="nickname"/></strong> <logic:present name="person" property="teacher"> (<fr:view name="person" property="teacher.teacherId"/>)</li>
</logic:present>

<logic:present role="role(TEACHER)">		
	<logic:present name="person" property="teacher">
		<logic:present name="person" property="teacher.category">
			<li><bean:message key="label.teacher.category" bundle="APPLICATION_RESOURCES"/>: <fr:view name="person" property="teacher.category.name"/></li>
		</logic:present>
	</logic:present>
</logic:present>
</ul>
	
<logic:present name="person" property="teacher">

<!-- Lectures -->
<logic:notEmpty name="lectures">
<p id='lecturesTitle' class="separator2" style="position: relative; width: 99%;">
	<span><bean:message bundle="DEPARTMENT_MEMBER_RESOURCES" key="label.teacher.details.lecturedCoursesInformation"/></span>
	<span style="position: absolute; right: 5px;"><%=GenericChecksumRewriter.NO_CHECKSUM_PREFIX%><a href="#header"><bean:message key="label.top" /></a></span>
</p>


	<ul>
    <logic:iterate id="lecture" name="lectures" type="org.fenixedu.academic.domain.ExecutionCourse">
		<li>
		
		 <fr:view name="lecture" property="nome"/>
		 (<fr:view name="lecture" property="executionYear.year"/>, <fr:view name="lecture" property="executionPeriod.name"/>, <fr:view name="lecture" property="degreePresentationString"/>)</li>
	</logic:iterate>
	</ul>
</logic:notEmpty>

<!-- Final Works -->
<logic:notEmpty  name="orientedThesis">	
<p id='guidancesTitle' class="separator2" style="position: relative; width: 99%; margin-top: 2em;">
	<span><bean:message bundle="DEPARTMENT_MEMBER_RESOURCES" key="label.teacher.details.orientationInformation"/></span>
	<span style="position: absolute; right: 5px;"><%=GenericChecksumRewriter.NO_CHECKSUM_PREFIX%><a href="#header"><bean:message key="label.top" /></a></span>
</p>
</logic:notEmpty>

<logic:notEmpty name="secondCycleThesis">
<p class="indent1 mtop15"><em><bean:message key="label.common.masterDegree" bundle="DEPARTMENT_MEMBER_RESOURCES"/></em></p>
<ul>

<logic:notEmpty name="orientedThesis">
<logic:iterate id="thesis" name="orientedThesis">
   <li><fr:view name="thesis" property="title"/>, <fr:view name="thesis" property="student.person.name" /> (<bean:message key="label.teacher.details.orientationInformation.masterDegreeProofDate" bundle="DEPARTMENT_MEMBER_RESOURCES"/>:
       <fr:view name="thesis" property="evaluation" type="org.joda.time.DateTime">
           <fr:layout name="null-as-label">
               <fr:property name="key" value="true" />
               <fr:property name="bundle" value="RESEARCHER_RESOURCES" />
               <fr:property name="label" value="label.researcher.thesis.notEvaluated" />
           </fr:layout>
       </fr:view>)
   </li>
</logic:iterate>
</logic:notEmpty>
</ul>
</logic:notEmpty>


<!-- Functions -->	
<logic:notEmpty name="functions">
<p id='functionsTitle' class="separator2" style="position: relative; width: 99%; margin-top: 2em;">
	<span><bean:message bundle="DEPARTMENT_MEMBER_RESOURCES" key="label.teacher.details.functionsInformation"/></span>
	<span style="position: absolute; right: 5px;"><%=GenericChecksumRewriter.NO_CHECKSUM_PREFIX%><a href="#header"><bean:message key="label.top" /></a></span>
</p>

<ul>
<logic:iterate id="personFunction" name="functions">
	<li>
		<span class="color888"><fr:view name="personFunction" property="beginDateInDateType"/> <bean:message key="label.until" bundle="RESEARCHER_RESOURCES"/> <fr:view name="personFunction" property="endDateInDateType"/></span>, 
		<fr:view name="personFunction" property="function.name"/> (<fr:view name="personFunction" property="unit.name"/>)
	</li>
</logic:iterate>
</ul>
</logic:notEmpty>

</logic:present>

<script type="text/javascript" language="javascript">
switchGlobal();
</script>
