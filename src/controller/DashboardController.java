package controller;

import application.MoneyMateApplication;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import model.Acount;
import model.AcountDAOException;
import model.Category;
import model.User;
import model.Charge;

public class DashboardController implements Initializable {

    @FXML
    private MenuItem SignOff;
    @FXML
    private Label allExpenses;
    @FXML
    private TableView<Charge> expenseTable;
    @FXML
    private TableColumn<Charge, String> nameColumn;
    @FXML
    private TableColumn<Charge, LocalDate> dateColumn;
    @FXML
    private TableColumn<Charge, Category> categoryColumn; // Ensure this is Category type
    @FXML
    private TableColumn<Charge, Double> amountColumn;
    @FXML
    private Button addExpense;
    @FXML
    private ImageView changeUser;
    @FXML
    private BarChart<String, Number> expenseChart;
    @FXML
    private Label monthlyExpense;
    @FXML
    private Label nameDashboard;

    private String username;
    
    private ObservableList<Charge> chargeData = FXCollections.observableArrayList();
    @FXML
    private Circle circleImage;
    @FXML
    private ComboBox<String> selectFilter;
    
    private ObservableList<String> filterList;
    @FXML
    private Button deleteExpense;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filterList = selectFilter.getItems();
        filterList.addAll("Category", "Monthly", "Year-by-Year");
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setCellFactory(column -> new TableCell<Charge, Category>() {
            
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName()); // Ensure 'getName' is the correct method to get the category name from a Category object
                }
            }
        });
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        amountColumn.setCellFactory(column -> new TableCell<Charge, Double>() {
        @Override
        protected void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                Charge charge = getTableView().getItems().get(getIndex());
                double totalCost = charge.getCost() * charge.getUnits();
                setText(String.format("€%.2f", totalCost));
            }
        }
    });

        try {
            User currentUser = Acount.getInstance().getLoggedUser();
            if (currentUser != null) {
                this.username = currentUser.getNickName();
                nameDashboard.setText(this.username);
                if (currentUser.getImage() != null) {
                    circleImage.setFill(new ImagePattern(currentUser.getImage()));
                }
                loadData();
                setupChart("Category");
                updateMonthlyExpenses(); 
            }
        } catch (AcountDAOException | IOException ex) {
            Logger.getLogger(editUserController.class.getName()).log(Level.SEVERE, null, ex); 
        }
        
        selectFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setupChart(newValue);
        });

        expenseChart.getStylesheets().add(getClass().getResource("/style/GeneralStyleDashboard.css").toExternalForm());

        NumberAxis yAxis = (NumberAxis) expenseChart.getYAxis();
        yAxis.setLabel("Amount");
    }    

    private void updateMonthlyExpenses() {
        LocalDate now = LocalDate.now();
        double total = chargeData.stream()
            .filter(charge -> charge.getDate().getMonth() == now.getMonth() && charge.getDate().getYear() == now.getYear())
            .mapToDouble(charge -> charge.getCost() * charge.getUnits())
            .sum();
        monthlyExpense.setText(String.format("€%.2f", total));
    }

    private void loadData() throws IOException {
        try {
            List<Charge> charges = Acount.getInstance().getUserCharges();
            if (charges != null) {
                chargeData.setAll(charges);
            } else {
                chargeData.clear();
            }
            expenseTable.setItems(chargeData);
        } catch (AcountDAOException e) {
            e.printStackTrace();
        }
    }

    private void setupChart(String filter) {
        expenseChart.getData().clear();

        if (filter.equals("Category")) {
            setupCategoryChart();
        } else if (filter.equals("Monthly")) {
            setupMonthlyChart();
        } else if (filter.equals("Year-by-Year")) {
            setupYearByYearChart();
        }

        // Ensure the x-axis is properly updated
        expenseChart.layout();
    }

    private void setupCategoryChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Double> summary = chargeData.stream()
            .collect(Collectors.groupingBy(
                charge -> charge.getCategory().getName(),  // Assuming getCategory().getName() is correct
                Collectors.summingDouble(Charge::getCost)));

        summary.forEach((categoryName, sum) -> series.getData().add(new XYChart.Data<>(categoryName, sum)));

        series.setName("Expenses by Category");
        expenseChart.getData().add(series);
    }



    private void setupMonthlyChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<Month, Double> summary = chargeData.stream()
            .collect(Collectors.groupingBy(
                charge -> charge.getDate().getMonth(),
                TreeMap::new,
                Collectors.summingDouble(Charge::getCost)));

        summary.forEach((month, sum) -> series.getData().add(new XYChart.Data<>(month.toString(), sum)));

        series.setName("Monthly Expenses");
        expenseChart.getData().add(series);
    }

    private void setupYearByYearChart() {
        XYChart.Series<String, Number> seriesLastYear = new XYChart.Series<>();
        XYChart.Series<String, Number> seriesThisYear = new XYChart.Series<>();
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int previousYear = currentYear - 1;

        Map<Month, Double> lastYearSummary = chargeData.stream()
            .filter(charge -> charge.getDate().getYear() == previousYear)
            .collect(Collectors.groupingBy(
                charge -> charge.getDate().getMonth(),
                TreeMap::new,
                Collectors.summingDouble(Charge::getCost)));

        Map<Month, Double> thisYearSummary = chargeData.stream()
            .filter(charge -> charge.getDate().getYear() == currentYear)
            .collect(Collectors.groupingBy(
                charge -> charge.getDate().getMonth(),
                TreeMap::new,
                Collectors.summingDouble(Charge::getCost)));

        lastYearSummary.forEach((month, sum) -> seriesLastYear.getData().add(new XYChart.Data<>(month.toString(), sum)));
        thisYearSummary.forEach((month, sum) -> seriesThisYear.getData().add(new XYChart.Data<>(month.toString(), sum)));

        seriesLastYear.setName(String.valueOf(previousYear));
        seriesThisYear.setName(String.valueOf(currentYear));

        expenseChart.getData().addAll(seriesLastYear, seriesThisYear);
    }

    @FXML
    private void SignOffClicked(ActionEvent event) throws Exception {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/hello-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void allExpensesClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/OverviewExpense-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addExpenseClicked(ActionEvent event) {
        
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/AddExpense-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void changeUserPressed(MouseEvent event) {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/view/editUser-view.fxml"));
            Parent root = fxmlloader.load();
            MoneyMateApplication.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteExpenseClicked(ActionEvent event) throws IOException {
        Charge selectedCharge = expenseTable.getSelectionModel().getSelectedItem();
        if (selectedCharge != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this charge?");
            alert.setContentText("Name: " + selectedCharge.getName() + "\nAmount: " + selectedCharge.getCost() + "\nCategory: " + selectedCharge.getCategory()  + "\nDate: " + selectedCharge.getDate());

            if (alert.showAndWait().get() == ButtonType.OK) {
                chargeData.remove(selectedCharge);
                try {
                    Acount.getInstance().removeCharge(selectedCharge); 
                } catch (AcountDAOException e) {
                    showErrorMessage("Failed to delete the charge: " + e.getMessage());
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Charge Selected");
            alert.setContentText("Please select a charge in the table.");
            alert.showAndWait();
        }
    
    }
     private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("An error occurred: " + message);
        alert.showAndWait();
    }
}
