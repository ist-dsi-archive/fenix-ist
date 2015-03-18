package pt.ist.fenix.task.exportData.parking;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.WordUtils;
import org.fenixedu.academic.FenixEduAcademicConfiguration;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.util.email.Sender;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.idcards.domain.SantanderCardInformation;
import org.fenixedu.parking.domain.ParkingGroup;
import org.fenixedu.parking.domain.ParkingParty;
import org.joda.time.DateTime;

@Task(englishTitle = "Send car park users to remote car park")
public class ExportCarParkUsers extends CustomTask {

    private static final String EMAIL_ADDRESSES_TO_SEND_DATA =
            "arcodocego@empark.pt, mamede@empark.pt, aneves@empark.es, luis.cruz@tecnico.pt";

    @Override
    public void runTask() throws Exception {
        final Set<ParkingGroup> allowedGroups =
                Bennu.getInstance().getParkingGroupsSet().stream().filter(this::isMemberOfGroup).collect(Collectors.toSet());

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(stream, true);
        printStream.print("[Clientes]\r\n");

        Bennu.getInstance().getUserSet().stream().filter(u -> u.getPerson() != null && isCarParkUser(u, allowedGroups))
                .forEach(u -> appendUserInfo(printStream, u));

        appendTestUserInfo(printStream);

        final String filename = "utents_ist.ini";
        final byte[] byteArray = stream.toByteArray();
        output(filename, byteArray);
        sendParkingInfoToRemoteCarPark(filename, byteArray);
    }

    private void appendTestUserInfo(PrintStream printStream) {
        printStream.print(invert("4f86bb68"));
        printStream.print("=a");
        printStream.print(truncate(20, WordUtils.capitalizeFully(StringNormalizer.normalize("Utilizador Teste"))));
        printStream.print(date(new DateTime(2015, 2, 27, 0, 0, 0)));
        printStream.print(date(new DateTime(2015, 4, 1, 0, 0, 0)));
        printStream.print("\r\n");
    }

    private void appendUserInfo(final PrintStream printStream, final User user) {
        final String rfid = getUserRFID(user);
        if (rfid != null) {
            printStream.print(toHex(rfid.trim()));
            printStream.print("=a");
            printStream.print(truncate(20,
                    WordUtils.capitalizeFully(StringNormalizer.normalize(user.getProfile().getDisplayName()))));
            printStream.print(date(getStartDate(user)));
            printStream.print(date(getEndDate(user)));
            printStream.print("\r\n");
        }
    }

    private String toHex(final String rfid) {
        return invert(makeStringLeftBlock(Long.toHexString(Long.parseLong(rfid)), 8));
    }

    private String date(DateTime dt) {
        return dt.toString("dd/MM/yyyy");
    }

    private String truncate(final int i, final String s) {
        return makeStringBlock(s.substring(0, Math.min(s.length(), i)), i);
    }

    private String makeStringBlock(String content, int size) {
        int fillerLength = size - content.length();
        if (fillerLength < 0) {
            throw new DomainException("Content is bigger than string block.");
        }
        StringBuilder blockBuilder = new StringBuilder(size);
        blockBuilder.append(content);

        for (int i = 0; i < fillerLength; i++) {
            blockBuilder.append(" ");
        }

        return blockBuilder.toString();
    }

    private String makeStringLeftBlock(String content, int size) {
        int fillerLength = size - content.length();
        if (fillerLength < 0) {
            throw new DomainException("Content is bigger than string block.", content);
        }
        StringBuilder blockBuilder = new StringBuilder(size);
        for (int i = 0; content.length() + i < size; i++) {
            blockBuilder.append("0");
        }
        blockBuilder.append(content);

        return blockBuilder.toString();
    }

    private String getUserRFID(final User user) {
        final Person person = user.getPerson();
        final ParkingParty parkingParty = person.getParkingParty();
        final Long cn = parkingParty.getCardNumber();
        return cn == null ? getLastMifareSerialNumber(person) : cn.toString();
    }

    private String invert(final String rfid) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rfid.length(); i++, i++) {
            final int offset = rfid.length() - i;
            builder.append(rfid.substring(offset - 2, offset));
        }
        return builder.toString();
    }

    private DateTime getStartDate(final User user) {
        final Person person = user.getPerson();
        final ParkingParty parkingParty = person.getParkingParty();
        return parkingParty != null && parkingParty.getCardStartDate() != null ? parkingParty.getCardStartDate() : new DateTime();
    }

    private DateTime getEndDate(final User user) {
        final Person person = user.getPerson();
        final ParkingParty parkingParty = person.getParkingParty();
        return parkingParty != null && parkingParty.getCardEndDate() != null ? parkingParty.getCardEndDate() : new DateTime()
                .plusYears(1);
    }

    private boolean isCarParkUser(final User user, final Set<ParkingGroup> allowedGroups) {
        final Person person = user.getPerson();
        final ParkingParty parkingParty = person.getParkingParty();
        return isActive(parkingParty, allowedGroups);
    }

    private static String getLastMifareSerialNumber(final Person person) {
        final Stream<SantanderCardInformation> infos = person.getSantanderCardsInformationSet().stream();
        final String line = infos.map(i -> i.getDchpRegisteLine()).max(ExportCarParkUsers::compareDHCPLines).orElse(null);
        return line == null ? null : getMifareSerialNumber(line);
    }

    private static String getMifareSerialNumber(String line) {
        final int offset = line.length() - 550 - 1;
        return line.substring(offset - 10, offset);
    }

    private static int compareDHCPLines(final String l1, String l2) {
        return l1.substring(1, 9).compareTo(l2.substring(1, 9));
    }

    private boolean isActive(final ParkingParty pp, final Set<ParkingGroup> allowedGroups) {
        return pp != null && pp.getCardStartDate() != null && pp.getCardStartDate().isBeforeNow()
                && (pp.getCardEndDate() == null || pp.getCardEndDate().isAfterNow())
                && allowedGroups.contains(pp.getParkingGroup());
    }

    private boolean isMemberOfGroup(final ParkingGroup pg) {
        final String name = pg.getGroupName();
        return name.equals("Jubilados")

        || name.equals("IPSFL")

                || name.equals("3º ciclo")

                || name.equals("Investigadores")

                || name.equals("Bolseiros")

                || name.equals("Especiais")

                || name.equals("Não Docentes")

                || name.equals("Docentes")

                || name.equals("ADIST - Informática")

                || name.equals("2º ciclo")

//                || name.equals("Limitados")

//                || name.equals("Limitados1")

//                || name.equals("Limitados2")

//                || name.equals("Limitados3")

//                || name.equals("Limitados4")

//                || name.equals("Limitados5")
        ;
    }

    private void sendParkingInfoToRemoteCarPark(String filename, byte[] byteArray) throws AddressException, MessagingException {
        final Properties properties = new Properties();
        properties.put("mail.smtp.host", FenixEduAcademicConfiguration.getConfiguration().getMailSmtpHost());
        properties.put("mail.smtp.name", FenixEduAcademicConfiguration.getConfiguration().getMailSmtpName());
        properties
                .put("mailSender.max.recipients", FenixEduAcademicConfiguration.getConfiguration().getMailSenderMaxRecipients());
        properties.put("mail.debug", "false");
        final Session session = Session.getDefaultInstance(properties, null);

        final Sender sender = Bennu.getInstance().getSystemSender();

        final Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender.getFromAddress()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_ADDRESSES_TO_SEND_DATA));
        message.setSubject("Utentes IST - Atualização");
        message.setText("Listagem atualizada de utentes do IST: " + new DateTime().toString("yyyy-MM-dd HH:mm"));

        MimeBodyPart messageBodyPart = new MimeBodyPart();

        Multipart multipart = new MimeMultipart();

        messageBodyPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(byteArray, "text/plain");
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

}
