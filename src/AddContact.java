
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

public class AddContact extends MIDlet implements CommandListener {

    //Main form.
    private Form form;
    private Display display;
    private Command exitCommand;
    private Command addCommand;
    // Text fields for contact data.
    private TextField familyName;
    private TextField givenName;
    private TextField number;
    private DateField birthday;

    /**
     * Constructor. Constructs the object and initializes displayables.
     */
    public AddContact() {
        form = new Form("Add PIM Contact");

        // Add command buttons 
        addCommand = new Command("Save", Command.OK, 0);
        exitCommand = new Command("Exit", Command.EXIT, 1);
        form.addCommand(addCommand);
        form.addCommand(exitCommand);
        // Create input fields 
        givenName = new TextField("Given name", "", 20, TextField.ANY);
        familyName = new TextField("Family name", "", 20, TextField.ANY);
        number = new TextField("Telephone", "", 20, TextField.NUMERIC);
        birthday = new DateField("Birthdate", DateField.DATE,
                TimeZone.getTimeZone("GMT"));

        birthday.setDate(new Date());

        form.append(givenName);
        form.append(familyName);
        form.append(number);
        form.append(birthday);

        form.setCommandListener(this);

        display = Display.getDisplay(this);
        display.setCurrent(form);
    }

    /**
     * From MIDlet.
     * Called when the MIDlet is started.
     */
    public void startApp() {
        // Check for availability of PIM interface.
        if (System.getProperty("microedition.pim.version") == null) {
            showError("PIM API not supported.", form);
        }
    }

    /**
     * From MIDlet.
     * Called to signal the MIDlet to enter the Paused state.
     */
    public void pauseApp() {
        // No implementation required.
    }

    /**
     * From MIDlet.
     * Called to signal the MIDlet to terminate.
     * @param unconditional whether the MIDlet has to be unconditionally
     * terminated
     */
    public void destroyApp(boolean unconditional) {
        // No implementation required
    }

    /**
     * From CommandListener.
     * Called by the system to indicate that a command has been invoked on a
     * particular displayable.
     * @param cmd the command that was invoked
     * @param displayable the displayable where the command was invoked
     */
    public void commandAction(Command cmd, Displayable displayable) {
        if (cmd == exitCommand) {
            notifyDestroyed();
        } else if (cmd == addCommand) {
            saveContact();
        }
    }

    /**
     * Adds contact to PIM contact list.
     */
    private void saveContact() {
        ContactList contacts = null;
        try {
            // Retrieve the contact list
            contacts = (ContactList) PIM.getInstance().openPIMList(
                    PIM.CONTACT_LIST, PIM.READ_WRITE);

            Contact contact = contacts.createContact();

            String[] name =
                    new String[contacts.stringArraySize(Contact.NAME)];

            if (contacts.isSupportedArrayElement(
                    Contact.NAME, Contact.NAME_FAMILY)) {
                name[Contact.NAME_FAMILY] = familyName.getString();
            }
            if (contacts.isSupportedArrayElement(
                    Contact.NAME, Contact.NAME_GIVEN)) {
                name[Contact.NAME_GIVEN] = givenName.getString();
            }
            if (contacts.isSupportedField(Contact.NAME)) {
                contact.addStringArray(Contact.NAME, PIMItem.ATTR_NONE, name);
            }

            if (contacts.isSupportedField(Contact.TEL)) {
                contact.addString(Contact.TEL, Contact.ATTR_HOME,
                        number.getString());
            }
            if (contacts.isSupportedField(Contact.BIRTHDAY)) {
                contact.addDate(Contact.BIRTHDAY, PIMItem.ATTR_NONE,
                        birthday.getDate().getTime());
            }
            contact.commit();
            contacts.close();
        } catch (PIMException e) {
            showError(e.getMessage(), form);
        }
    }

    /**
     * Shows error message
     * @param message the message that will be displayed
     * @param displayable the displayable where the message will be showen
     */
    private void showError(String message, Displayable displayable) {
        Alert alert = new Alert("");
        alert.setTitle("Error");
        alert.setString(message);
        alert.setType(AlertType.ERROR);
        alert.setTimeout(5000);
        display.setCurrent(alert, displayable);
    }
}
