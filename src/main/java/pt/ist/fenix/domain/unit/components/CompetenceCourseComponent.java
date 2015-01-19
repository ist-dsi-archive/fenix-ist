package pt.ist.fenix.domain.unit.components;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.degreeStructure.BibliographicReferences;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseInformation;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

import pt.ist.fenix.domain.unit.UnitSite;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@ComponentType(name = "departmentCourse", description = "Provides a specific competence course")
public class CompetenceCourseComponent extends UnitSiteComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        CompetenceCourse competenceCourse = FenixFramework.getDomainObject(globalContext.getRequestContext()[1]);
        ExecutionSemester semester = executionSemester(competenceCourse, globalContext.getRequestContext());
        globalContext.put("competenceCourse", wrap(competenceCourse, semester));
        globalContext.put("executionSemesterUrls", executionSemesterUrls(competenceCourse, page));
    }

    private Map<String, Object> wrap(CompetenceCourse competenceCourse, ExecutionSemester semester) {
        HashMap<String, Object> wrap = Maps.newHashMap();
        wrap.put("name", competenceCourse.getNameI18N(semester).toLocalizedString());
        wrap.put("courseLoads", competenceCourse.getSortedCompetenceCourseLoads(semester));
        wrap.put("mainBibliographicReferences",
                bibliographicReferences(competenceCourse, BibliographicReferences.BibliographicReferenceType.MAIN, semester));
        wrap.put("secondaryBibliographicReferences",
                bibliographicReferences(competenceCourse, BibliographicReferences.BibliographicReferenceType.SECONDARY, semester));
        wrap.put("executionSemester", semester);
        wrap.put("evaluationMethod", competenceCourse.getLocalizedEvaluationMethod(semester));
        wrap.put("program", competenceCourse.getProgramI18N(semester).toLocalizedString());
        wrap.put("objectives", competenceCourse.getObjectivesI18N(semester).toLocalizedString());
        wrap.put("regime", competenceCourse.getRegime(semester).getLocalizedName());
        wrap.put("isBasic", competenceCourse.isBasic(semester));
        wrap.put("competenceCourseLevel", competenceCourse.getLocalizedEvaluationMethod(semester));
        wrap.put("associatedCurricularCourses", competenceCourse.getAssociatedCurricularCoursesSet());
        wrap.put("competenceCourseGroupUnit", competenceCourse.getCompetenceCourseGroupUnit(semester));
        return wrap;
    }

    private Map<ExecutionSemester, String> executionSemesterUrls(CompetenceCourse competenceCourse, Page page) {
        Map<ExecutionSemester, String> semesterUrl = Maps.newTreeMap(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR);
        for (ExecutionSemester semester : executionSemesters(competenceCourse)) {
            semesterUrl.put(semester,
                    String.format("%s/%s/%s", page.getAddress(), competenceCourse.getExternalId(), semester.getExternalId()));
        }
        return semesterUrl;
    }

    private List<BibliographicReferences.BibliographicReference> bibliographicReferences(CompetenceCourse competenceCourse,
            BibliographicReferences.BibliographicReferenceType type, ExecutionSemester semester) {
        Predicate<BibliographicReferences.BibliographicReference> isType =
                bibliographicReference -> bibliographicReference.getType().equals(type);
        return bibliographicReferences(competenceCourse, semester).stream().filter(isType).collect(toList());
    }

    private List<BibliographicReferences.BibliographicReference> bibliographicReferences(CompetenceCourse competenceCourse,
            ExecutionSemester semester) {
        if (competenceCourse.getBibliographicReferences(semester) != null) {
            return competenceCourse.getBibliographicReferences(semester).getBibliographicReferencesList();
        } else {
            return Lists.newArrayList();
        }
    }

    private ExecutionSemester executionSemester(CompetenceCourse competenceCourse, String[] requestContext) {
        if (requestContext.length >= 3 && !Strings.isNullOrEmpty(requestContext[2])) {
            return FenixFramework.getDomainObject(requestContext[2]);
        } else {
            Optional<CompetenceCourseInformation> information =
                    competenceCourse.getCompetenceCourseInformationsSet().stream().findFirst();
            return information.isPresent() ? information.get().getExecutionPeriod() : ExecutionSemester
                    .readActualExecutionSemester();
        }
    }

    private TreeSet<ExecutionSemester> executionSemesters(CompetenceCourse competenceCourse) {
        final TreeSet<ExecutionSemester> result = Sets.newTreeSet(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR);
        ExecutionSemester semester = competenceCourse.getStartExecutionSemester();
        result.add(semester);
        while (semester.hasNextExecutionPeriod()) {
            semester = semester.getNextExecutionPeriod();
            result.add(semester);
        }
        return result;
    }

    public static boolean supportsSite(Site site) {
        return site instanceof UnitSite && ((UnitSite) site).getUnit() instanceof DepartmentUnit;
    }

}
