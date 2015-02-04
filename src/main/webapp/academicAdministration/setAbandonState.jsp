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
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html:xhtml />
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers"	prefix="fr"%>

<h2>
	<bean:message key="label.academicAdministration.setAbandonState" bundle="FENIX_IST_RESOURCES"/>
</h2>

<div class="infoop2">
	<bean:message key="message.academicAdministration.abandonState.info" bundle="FENIX_IST_RESOURCES"/>
</div>

<br/>
<fr:form id="chooseSemesterForm" action="/setAbandonState.do?method=confirmUpdate">
	<fr:edit id="executionSemester" name="updateAbandonStateBean">
		<fr:schema bundle="ACADEMIC_OFFICE_RESOURCES" type="pt.ist.fenix.ui.struts.action.academicAdministration.UpdateAbandonStateBean">
			<fr:slot name="whenToAbandon" layout="menu-select" key="message.execution.course.management.choose.semester" required="true">
				<fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.NotClosedExecutionPeriodsProvider" />
				<fr:property name="format" value="\${qualifiedName}" />
			</fr:slot>
			<fr:layout>
				<fr:property name="classes" value="tstyle5 mtop05 mbottom15" />
				<fr:property name="columnClasses" value=",,error0" />
			</fr:layout>
		</fr:schema>
		<fr:destination name="invalid" path="/setAbandonState.do?method=prepare"/>
	</fr:edit>
	
	<html:submit><bean:message key="button.updateStates" bundle="FENIX_IST_RESOURCES"/></html:submit>
</fr:form>

<br/>	
<logic:present name="confirm">	
	<bean:define id="previousExecutionSemester" name="updateAbandonStateBean" property="whenToAbandon.previousExecutionPeriod"/>
	<bean:define id="previousExecutionSemesterName" name="updateAbandonStateBean" property="whenToAbandon.previousExecutionPeriod.qualifiedName" type="java.lang.String"/>
	<bean:define id="previousPreviousExecutionSemesterName" name="previousExecutionSemester" property="previousExecutionPeriod.qualifiedName" type="java.lang.String"/>
	<p class="warning1">
		<bean:message key="message.academicAdministration.abandonState.warning" bundle="FENIX_IST_RESOURCES" arg0="<%= previousPreviousExecutionSemesterName %>"
		 arg1="<%= previousExecutionSemesterName %>"/>
	</p>
	<p>
		<bean:message key="label.operatioConfirmation" bundle="FENIX_IST_RESOURCES"/>
	</p>

	<fr:form id="confirmUpdate" action="/setAbandonState.do">
		<html:hidden property="method" value="updateState" />
		<fr:edit id="updateAbandonState" name="updateAbandonStateBean" visible="false"/>
			
		<html:submit><bean:message key="button.confirm" bundle="ACADEMIC_OFFICE_RESOURCES"/></html:submit>
		<html:submit onclick="this.form.method.value='prepare'"><bean:message key="button.cancel" bundle="ACADEMIC_OFFICE_RESOURCES"/></html:submit>
	</fr:form>
</logic:present>