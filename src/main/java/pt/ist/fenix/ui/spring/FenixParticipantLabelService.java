package pt.ist.fenix.ui.spring;

import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;
import org.fenixedu.academic.thesis.ui.service.ParticipantLabelService;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.springframework.stereotype.Service;

import pt.ist.fenixedu.contracts.domain.accessControl.ActiveResearchers;

@Service
public class FenixParticipantLabelService implements ParticipantLabelService {

    @Override
    public String getInstitutionRole(ThesisProposalParticipant participant) {
        User user = participant.getUser();
        Teacher teacher = user.getPerson().getTeacher();

        if (teacher != null && teacher.getTeacherAuthorization().isPresent()) {
            return BundleUtil.getString("resources.FenixEduThesisProposalsResources", "label.participant.active.teacher");
        }

        if (new ActiveResearchers().isMember(user)) {
            return BundleUtil.getString("resources.FenixEduThesisProposalsResources", "label.participant.contracted.researcher");
        }

        return null;
    }

}
