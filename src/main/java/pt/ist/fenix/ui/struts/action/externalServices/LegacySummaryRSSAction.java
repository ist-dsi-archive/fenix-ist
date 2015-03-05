/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of Fenix IST.
 *
 * Fenix IST is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fenix IST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenix.ui.struts.action.externalServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.cms.domain.Category;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

@Mapping(path = "/summariesRSS", module = "publico")
public class LegacySummaryRSSAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        DomainObject obj = FenixFramework.getDomainObject(request.getParameter("id"));
        if (obj instanceof ExecutionCourse && FenixFramework.isDomainObjectValid(obj)) {
            ExecutionCourse course = (ExecutionCourse) obj;
            if (course.getSite() != null) {
                Category cat = course.getSite().categoryForSlug("summary");
                if (cat != null) {
                    response.setHeader("Location", cat.getRssUrl());
                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    return null;
                }
            }
        }
        response.sendError(404);
        return null;
    }

}
