package seng202.team8.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import seng202.team8.model.CrimeRecord;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class MapController extends GUIController implements Initializable {


    @FXML
    public TextField startBeatField;

    @FXML
    public DatePicker startDatePicker;

    @FXML
    public DatePicker endDatePicker;

    @FXML
    public Text startDateText;

    @FXML
    public Text endDateText;

    @FXML
    public TextField primaryDescField;

    @FXML
    public TextField locationField;

    @FXML
    public TextField endBeatField;

    @FXML
    public Text startBeatText;

    @FXML
    public Text endBeatText;

    @FXML
    public TextField startWardField;

    @FXML
    public TextField endWardField;

    @FXML
    public Text startWardText;

    @FXML
    public Text endWardText;

    @FXML
    public ComboBox<String> arrestComboBox;

    @FXML
    public ComboBox<String> domesticComboBox;

    @FXML
    public TextField markNumberField;

    @FXML
    public Text markNumberText;

    @FXML
    private WebView mapView;
    
    private WebEngine webEngine;

    private ArrayList<CrimeRecord> records = getManager().getLocalCopy();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initMap();
    }

    private void initMap() {
    	webEngine = mapView.getEngine();
        webEngine.load(Objects.requireNonNull(getClass().getResource("/map.html")).toExternalForm());

        // Filter text fields
        startDateText.setText("");
        endDateText.setText("");
        startBeatText.setText("");
        endBeatText.setText("");
        startWardText.setText("");
        endWardText.setText("");

        // Combo box initialization
        arrestComboBox.getItems().addAll("Don't filter", "Yes", "No");
        domesticComboBox.getItems().addAll("Don't filter", "Yes", "No");
        arrestComboBox.getSelectionModel().select("Don't filter");
        domesticComboBox.getSelectionModel().select("Don't filter");
    }

    /**
     * Places a marker on the map for
     * the first 'number' Crime Records in the
     * given ArrayList
     * This breaks if too many markers are put together
     * That number being about 1000 or so
     * @param records The ArrayList of CrimeRecords to be marked on the map
     * @param number The number of records to place
     */
    public void placeNumMarkers(ArrayList<CrimeRecord> records, int number) {
        // Lowers number to the total number of records
        number = Math.min(records.size(), number);

        //Go through a for loop a set number of times
        for (int i = 0; i < number; i++) {
            CrimeRecord crime = records.get(i);
            String scriptToExecute = "placeMarker(" + crime.getLatitude() + ", " +
                    crime.getLongitude() + ", \"" +
                    crime.getCaseNum() + "\");";
            webEngine.executeScript(scriptToExecute);
        }
    }

    /**
     * Filters the records contained within this controller
     * Which determines what it mapped
     */
    public void filterMap() {
        records = getManager().getLocalCopy(); // Reset records to complete dataset
        filterDates();
        records = SearchCrimeData.filterByPrimaryDesc(records, primaryDescField.getText());
        records = SearchCrimeData.filterByCrimeLocation(records, locationField.getText());
        filterBeats();
        filterWards();
        if (!arrestComboBox.getValue().equals("Don't filter")) {
            records = SearchCrimeData.filterByArrest(records, arrestComboBox.getValue().equals("Yes"));
        }
        if (!domesticComboBox.getValue().equals("Don't filter")) {
            records = SearchCrimeData.filterByDomesticViolence(records, domesticComboBox.getValue().equals("Yes"));
        }
    }

    /**
     * Filters the module's records ArrayList by removing crime record's that didn't occur between the dates provided
     * in the GUI's date pickers. Displays an error message for the date pickers if they contain invalid dates.
     */
    private void filterDates() {
        String startDate = startDatePicker.getEditor().getText();
        String endDate = endDatePicker.getEditor().getText();
        try {
            records = SearchCrimeData.filterByDate(records, startDate, endDate);
            startDateText.setText("");
            endDateText.setText("");
        } catch (ParseException e) { // Display error messages for appropriate date pickers
            if (!startDate.equals("") && !SearchCrimeData.isValidDate(startDate)) {
                startDateText.setText("Invalid Date");
            } else {
                startDateText.setText("");
            }
            if (!endDate.equals("") && !SearchCrimeData.isValidDate(endDate)) {
                endDateText.setText("Invalid Date");
            } else {
                endDateText.setText("");
            }
        }
    }

    /**
     * Filters the module's records by removing records that do not occur
     * within the beat range provided in the beat entry fields
     *
     * Displays an error message if the entry fields contain invalid beats
     */
    private void filterBeats() {
        String startBeatString = startBeatField.getText();
        String endBeatString = endBeatField.getText();
        try {
            records = SearchCrimeData.filterByCrimeLocationBeat(records,
                    Integer.parseInt(startBeatString), Integer.parseInt(endBeatString));
            startBeatText.setText("");
            endBeatText.setText("");
        } catch (NumberFormatException e) { // Display error messages for appropriate beat fields
            if (!startBeatString.equals("") && !isInteger(startBeatString)) {
                startBeatText.setText("Invalid Beat");
            } else {
                startBeatText.setText("");
            }
            if (!endBeatString.equals("") && !isInteger(endBeatString)) {
                endBeatText.setText("Invalid Beat");
            } else {
                endBeatText.setText("");
            }
        }
    }

    /**
     * Filters the module's records by removing records that do not occur
     * within the ward range provided in the ward entry fields
     *
     * Displays an error message if the entry fields contain invalid ward
     */
    private void filterWards() {
        String startWardString = startWardField.getText();
        String endWardString = endWardField.getText();
        try {
            records = SearchCrimeData.filterByCrimeWard(records,
                    Integer.parseInt(startWardString), Integer.parseInt(endWardString));
            startWardText.setText("");
            endWardText.setText("");
        } catch (NumberFormatException e) { // Display error messages for appropriate ward fields
            if (!startWardString.equals("") && !isInteger(startWardString)) {
                startWardText.setText("Invalid Ward");
            } else {
                startWardText.setText("");
            }
            if (!endWardString.equals("") && !isInteger(endWardString)) {
                endWardText.setText("Invalid Ward");
            } else {
                endWardText.setText("");
            }
        }
    }

    /**
     * Places markers for all the crimes
     * in this controller's records variable
     * which is all records that abide by the filter
     *
     */
    public void mapRecords() {
        try {
            String input = markNumberField.getText();
            int quantity = Integer.parseInt(input);
            if ((quantity < 1) || (quantity > 1000)) {
                /*
                We do the same thing in this case
                as we do for a wrongly parsed int
                so just throw the same kind of exception
                 */
                throw new NumberFormatException("Wrong Size!");
            }
            //Clear the text
            markNumberText.setText("");
            //Clear any current markers
            String scriptToExecute = "deleteMarkers();";
            webEngine.executeScript(scriptToExecute);
            //Map them
            placeNumMarkers(records, quantity);

        } catch (NumberFormatException e) {
            //Display the text
            markNumberText.setText("Please enter a number between 1 and 1000");
        }

    }

}