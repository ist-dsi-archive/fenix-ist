package pt.ist.fenix.ui.struts.action.messaging;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Teacher;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.contacts.MobilePhone;
import org.fenixedu.academic.domain.contacts.Phone;
import org.fenixedu.academic.domain.contacts.WebAddress;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.spaces.domain.occupation.SharedOccupation;

import pt.ist.fenix.domain.homepage.HomepageSite;
import pt.ist.fenixedu.contracts.domain.Employee;

public class PersonBean {
    private Person person;

    public List<WebAddress> getWebAddresses() {
        return person.getWebAddresses();
    }

    public List<MobilePhone> getMobilePhones() {
        return person.getMobilePhones();
    }

    public List<EmailAddress> getEmailAddresses() {
        return person.getEmailAddresses();
    }

    public List<Phone> getPhones() {
        return person.getPhones();
    }

    public String getUsername() {
        return person.getUsername();
    }

    public String getName() {
        return person.getName();
    }

    public PersonBean(Person p) {
        person = p;
    }

    public HomepageSite getHomepage() {
        return person.getHomepage();
    }

    public Teacher getTeacher() {
        return person.getTeacher();
    }

    public Employee getEmployee() {
        return person.getEmployee();
    }

    public Student getStudent() {
        return person.getStudent();
    }

    public User getUser() {
        return person.getUser();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getExternalId() {
        return person.getExternalId();
    }

    public Set<Space> getActivePersonSpaces() {
        Set<Space> toRet = new HashSet<Space>();
        for (SharedOccupation so : person.getUser().getSharedOccupationSet()) {
            if (so.isActive() && so.getSpaces().iterator().next() != null) {
                toRet.add(so.getSpaces().iterator().next());
            }
        }
        return toRet;
    }

}
