<%--

    Copyright © 2002 Instituto Superior Técnico

    This file is part of FenixEdu Academic.

    FenixEdu Academic is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Academic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page isELIgnored="true"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>

<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>

<html:xhtml/>

	<bean:define id="registration" name="registration" scope="request" type="org.fenixedu.academic.domain.student.Registration"/>

	<div style="float: right;">
		<bean:define id="personID" name="registration" property="student.person.username"/>
		<html:img align="middle" src="<%= request.getContextPath() + "/user/photo/" + personID.toString()%>" altKey="personPhoto" bundle="IMAGE_RESOURCES" styleClass="showphoto"/>
	</div>

	<h2><bean:message key="label.visualizeRegistration" bundle="ACADEMIC_OFFICE_RESOURCES"/></h2>
	
	<p>
		<html:link page="/curriculumValidation.do?method=visualizeStudent" paramId="studentID" paramName="registration" paramProperty="student.externalId">
			<bean:message key="link.student.backToStudentDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/>
		</html:link>
	</p>
	
	<p class="mvert2">
		<span class="showpersonid">
		<bean:message key="label.student" bundle="ACADEMIC_OFFICE_RESOURCES"/>: 
			<fr:view name="registration" property="student" schema="student.show.personAndStudentInformation.short">
				<fr:layout name="flow">
					<fr:property name="labelExcluded" value="true"/>
				</fr:layout>
			</fr:view>
		</span>
	</p>
		
	<logic:messagesPresent message="true">
		<ul class="list7 mtop2 warning0" style="list-style: none;">
			<html:messages id="message" message="true" bundle="ACADEMIC_OFFICE_RESOURCES">
				<li>
					<span><!-- Error messages go here --><bean:write name="message" /></span>
				</li>
			</html:messages>
		</ul>
	</logic:messagesPresent>

	<logic:present name="registration" property="ingression">
		<h3 class="mtop2 mbottom05 separator2"><bean:message key="label.registrationDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
	</logic:present>
	
	<logic:notPresent name="registration" property="ingression">
		<h3 class="mtop2 mbottom05 separator2"><bean:message key="label.registrationDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
	</logic:notPresent>
	<bean:define id="registration" name="registration" type="org.fenixedu.academic.domain.student.Registration"/>

<%--<table>
	<tr>
		<td> --%>
		
			<%-- Registration Details --%>
			<logic:present name="registration" property="ingression">
			<fr:view name="registration" schema="student.registrationDetail" >
				<fr:layout name="tabular">
					<fr:property name="classes" value="tstyle4 thright thlight"/>
					<fr:property name="rowClasses" value=",,,,,,,,"/>
				</fr:layout>
			</fr:view>
			</logic:present>
			<logic:notPresent name="registration" property="ingression">
			<fr:view name="registration" schema="student.registrationsWithStartData" >
				<fr:layout name="tabular">
					<fr:property name="classes" value="tstyle4 thright thlight mtop0"/>
					<fr:property name="rowClasses" value=",,,,,,,"/>
				</fr:layout>
			</fr:view>
			</logic:notPresent>
		
		<%--</td>
		
		
	</tr>
</table>--%>
		
	<%-- Curricular Plans --%>
	
	<academic:allowed operation="MANAGE_REGISTRATIONS" program="<%= registration.getDegree() %>">
		<h3 class="mbottom05 mtop25 separator2"><bean:message key="label.studentCurricularPlans" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
		
		<fr:view name="registration" property="sortedStudentCurricularPlans" schema="student.studentCurricularPlans" >
			<fr:layout name="tabular">
				<fr:property name="classes" value="tstyle2 thright thlight thcenter"/>
				<fr:property name="groupLinks" value="false"/>
				
				<fr:property name="linkFormat(setEvaluations)" value="/curriculumValidation.do?method=prepareCurriculumValidation&amp;studentCurricularPlanId=${externalId}" />
				<fr:property name="key(setEvaluations)" value="link.student.setEvaluations" />
				<fr:property name="bundle(setEvaluations)" value="ACADEMIC_OFFICE_RESOURCES" />
				<fr:property name="contextRelative(setEvaluations)" value="true" />      					   						
			</fr:layout>
		</fr:view>	
	</academic:allowed>