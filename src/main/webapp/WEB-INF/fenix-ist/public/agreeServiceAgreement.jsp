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
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<style>
.page{
    margin-top: 1%;
    padding: 0;
    height: 50%;
    width: 75%;
    display: block; 
    border:solid #000 1px;
}
.agreement-content{
    padding:5px;
    overflow: scroll; overflow-x:hidden;
    height:300px;
    /*-webkit-overflow-scrolling: touch;*/    
}
</style>

<div class="infoop2">
	${i18n.message("oauthapps.text.manage.applications.register")}
</div>
<div class="page">
	<div class="agreement-content">
		${serviceAgreement}
	</div>
</div>

<spring:url var="acceptServiceAgreementUrl" value="/api-service-agreement"/>

<div style="width: 80%; margin: 1em 1em 0 0; padding: 0 1em 1em 1em; text-align: left;">
	<form action="${acceptServiceAgreementUrl}" method="post">
		<p>
			<input type="checkbox" name="agreedServiceAgreement"/>
			${i18n.message("oauthapps.text.manage.applications.agree.terms")}
		</p>
		<p>
			<button class="btn btn-primary">
				${i18n.message("label.submit")}
			</button>
		</p>
	</form>
</div>