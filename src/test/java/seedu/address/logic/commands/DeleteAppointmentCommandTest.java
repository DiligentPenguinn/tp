package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.AppointmentList;
import seedu.address.model.person.MedicalReport;
import seedu.address.model.person.Nric;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

// The test cases are adapted from a conversation with chatGPT
class DeleteAppointmentCommandTest {

    private Model model;
    private Person personWithAppointment;
    private Nric validNric;
    private Index validIndex;
    private Appointment appointment;
    private AppointmentList appointmentList;

    @BeforeEach
    void setUp() {
        model = new ModelManager(seedu.address.testutil.TypicalPersons.getTypicalKlinix(), new UserPrefs());

        validNric = new Nric("S1234567A");
        validIndex = Index.fromZeroBased(0);

        appointment = new Appointment(
                "Checkup",
                LocalDateTime.of(2024, 1, 1, 10, 30), // 1st Jan 2024, 10:30 AM
                LocalDateTime.of(2024, 1, 10, 15, 0), // 10th Jan 2024, 3:00 PM
                "S1234567A"
        );

        // Create a MedicalReport and add medicine usage
        MedicalReport medicalReport = new MedicalReport("", "", "", "");

        appointmentList = new AppointmentList();
        appointmentList.add(appointment);

        // Assign medical report to a person
        personWithAppointment = new PersonBuilder()
                .withNric(validNric.toString())
                .withMedicalReport(medicalReport)
                .withAppointmentList(appointmentList)
                .build();

        model.addPerson(personWithAppointment);
    }

    @Test
    void execute_personNotFound_throwsCommandException() {
        DeleteAppointmentCommand command = new DeleteAppointmentCommand(new Nric("S9999999Z"), validIndex);
        assertThrows(CommandException.class, () -> command.execute(model),
                DeleteAppointmentCommand.MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    void execute_invalidIndex_throwsCommandException() {
        Index invalidIndex = Index.fromZeroBased(5); // Out of bounds
        DeleteAppointmentCommand command = new DeleteAppointmentCommand(validNric, invalidIndex);

        assertThrows(CommandException.class, () -> command.execute(model),
                DeleteAppointmentCommand.MESSAGE_INVALID_APPOINTMENT_DISPLAYED_INDEX);
    }

    @Test
    void execute_validDeletion_appointmentListIsEmpty() throws CommandException {
        DeleteAppointmentCommand command = new DeleteAppointmentCommand(validNric, validIndex);
        command.execute(model);

        Person updatedPerson = model.findPersonByNric(validNric);
        assertEquals(0, updatedPerson.getAppointmentList().size());
    }

    @Test
    void equals() {
        DeleteAppointmentCommand command1 = new DeleteAppointmentCommand(validNric, validIndex);
        DeleteAppointmentCommand command2 = new DeleteAppointmentCommand(validNric, validIndex);
        DeleteAppointmentCommand command3 = new DeleteAppointmentCommand(new Nric("S7654321B"), validIndex);

        assertEquals(command1, command2);
        assertNotEquals(command1, command3);
    }
}
