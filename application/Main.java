//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: a2
// Files: Main.java
// Course: CS 400, Spring, 2020
//
// Author: Adam Shedivy, Calvin Sienatra, Charlie Mrkvicka
// Email: ajshedivy@wisc.edu,
// Lecturer's Name: Debra Deppeler
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully
// acknowledge and credit those sources of help here. Instructors and TAs do
// not need to be credited here, but tutors, friends, relatives, room mates,
// strangers, and others do. If you received no outside help from either type
// of source, then please explicitly indicate NONE.
//
// Persons: N/A
// Online Sources: N/A
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////
/********************************************
 * File: Main.java Date: 4/21/2020 Course: CS 400 Compiler: javac.exe Platform: Windows 10
 *******************************************/
package application;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.NoSuchElementException;
import java.util.Set;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

// TEST COMMENT

/**
 * Main Milk weight application driver
 * 
 * @author aTeam 131
 *
 */
public class Main extends Application {
  private List<String> args;
  private static final int WINDOW_WIDTH = 1400;
  private static final int WINDOW_HEIGHT = 800;
  private static final String APP_TITLE = "Milk Weight App";
  
  private Stage mainStage;
  private BorderPane root;
  
  private FarmGroup cheeseFactory;
  
  private int totalMilkWeightText = 0;
  
  private int saveType = -1;
  
  private String farmIdPlaceholder;
  private Integer yearPlaceholder;
  private Integer monthPlaceholder;
  
  private LocalDate fromDatePlaceholder;
  private LocalDate toDatePlaceholder;

  private void generateLoadSavePanel() {
    TilePane loadSavePanelPane = new TilePane(); // Create the load panel pane

    Button loadButton = new Button("Browse A Farm Directory Data"); // Create a load data button
    Button saveButton = new Button("Save to file"); // Create a save data button

    // Construct an EventHandler for load data
    EventHandler<ActionEvent> loadEvent = new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        
        DirectoryChooser chooseCSVDirectory = new DirectoryChooser();
        File selectedDirectory = chooseCSVDirectory.showDialog(mainStage);
        boolean fuse = true;
        
        try {
          if(selectedDirectory != null) {
            //System.out.println(selectedDirectory.getAbsolutePath());
            System.out.println("TEST");
            cheeseFactory = new FarmGroup();
            
            String pathToDir = selectedDirectory.getAbsolutePath();
            
            File[] listOfCSV = selectedDirectory.listFiles();
            //System.out.println(listOfCSV);
            
            InputParser parser = new InputParser();
            
            for(File file: listOfCSV) {
              System.out.println(file.getName());
              
              parser = new InputParser();
              try {
                parser.inputData(file.getAbsolutePath());
              } catch (IOException e1) {
                // Error opening file
                fuse = false;
                System.out.println("Error: File cannot be opened!");
                generateStatusMessage("Error. File cannot be opened!");
                break;
              } catch (Exception e2) {
                System.out.println(e2);
              }
              
              // parser.printData();
              DataMap<String, String, Integer> inputData = parser.getData();
              
              Set<String> farmIds = inputData.keySet();
              for(String farmId: farmIds) {
                for(String date: inputData.getV(farmId).keySet()) {
                  try {
                  String[] splittedDate = date.split("-");
                  Integer year = Integer.parseInt(splittedDate[0]);
                  Integer month = Integer.parseInt(splittedDate[1]);
                  Integer day = Integer.parseInt(splittedDate[2]);
                  
                  LocalDate convDate = LocalDate.of(year, month, day);
                  
                  cheeseFactory.insertMilkWeight(farmId, convDate, inputData.getDeepV(farmId, date));
                  }catch(Exception e3) {
                    System.out.println(e3);
                  }
                  
                }
                
              }
              
            }
            
            
          }else {
            fuse = false;
            generateStatusMessage("Error. Directory is empty!");
          }
        }catch(Exception E) {
          fuse = false;
          generateStatusMessage("Error. Loading file failed!");
        }
        
        if(fuse) {
          generateStatusMessage("File successfully loaded!");
        }
        
      }
    };

    loadButton.setOnAction(loadEvent); // Set the action of load button

    // Construct an EventHandler for save data
    EventHandler<ActionEvent> saveEvent = new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        // EVENT IF SAVE DATA IS CLICKED
        if(saveType == -1) {
          generateStatusMessage("Please generate a graph first!");
        }else if(saveType == 1) {
          ExportData exportFarmReport = new ExportData(cheeseFactory);
          
          FileChooser fileChooser = new FileChooser();
          
          //Set extension filter for text files
          FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
          fileChooser.getExtensionFilters().add(extFilter);

          //Show save file dialog
          File file = fileChooser.showSaveDialog(mainStage);
          
          System.out.println(file.getAbsolutePath());
          
          try {
            exportFarmReport.exportFarmReport(file.getAbsolutePath(), farmIdPlaceholder, yearPlaceholder);
          } catch (IOException e1) {
            generateStatusMessage("Error saving file!");
          }
          
          
        }else if(saveType == 2) {
          ExportData exportFarmReport = new ExportData(cheeseFactory);
          
          FileChooser fileChooser = new FileChooser();
          
          //Set extension filter for text files
          FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
          fileChooser.getExtensionFilters().add(extFilter);

          //Show save file dialog
          File file = fileChooser.showSaveDialog(mainStage);
          
          System.out.println(file.getAbsolutePath());
          
          try {
            exportFarmReport.exportAnnualreport(file.getAbsolutePath(), yearPlaceholder);
          } catch (IOException e1) {
            generateStatusMessage("Error saving file!");
          }
        }else if(saveType == 3) {
          ExportData exportFarmReport = new ExportData(cheeseFactory);
          
          FileChooser fileChooser = new FileChooser();
          
          //Set extension filter for text files
          FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
          fileChooser.getExtensionFilters().add(extFilter);

          //Show save file dialog
          File file = fileChooser.showSaveDialog(mainStage);
          
          System.out.println(file.getAbsolutePath());
          
          try {
            exportFarmReport.exportMonthlyReport(file.getAbsolutePath(), yearPlaceholder, monthPlaceholder);
          } catch (IOException e1) {
            generateStatusMessage("Error saving file!");
          }
          
        }else if(saveType == 4) {
          ExportData exportFarmReport = new ExportData(cheeseFactory);
          
          FileChooser fileChooser = new FileChooser();
          
          //Set extension filter for text files
          FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
          fileChooser.getExtensionFilters().add(extFilter);

          //Show save file dialog
          File file = fileChooser.showSaveDialog(mainStage);
          
          System.out.println(file.getAbsolutePath());
          
          try {
            exportFarmReport.exportDateRangeReport(file.getAbsolutePath(), fromDatePlaceholder, toDatePlaceholder);
          } catch (IOException e1) {
            generateStatusMessage("Error saving file!");
          }
        }

      }
    };

    saveButton.setOnAction(saveEvent); // Set the action of save button

    loadSavePanelPane.getChildren().add(loadButton); // Add the buttons to the pane
    loadSavePanelPane.getChildren().add(saveButton);

    loadSavePanelPane.setPadding(new Insets(15, 15, 15, 15));
    root.setRight(loadSavePanelPane); // Set the right root pane
  }

  private void generateGetReportPanel() {
    GridPane getReportPanelPane = new GridPane();

    // Create a farm id text field
    final TextField farmIDText = new TextField();
    farmIDText.setPrefColumnCount(15);
    farmIDText.setPromptText("Farm ID");
    GridPane.setConstraints(farmIDText, 0, 0);
    getReportPanelPane.getChildren().add(farmIDText);

    // Create a year text field
    final TextField yearText = new TextField();
    yearText.setPrefColumnCount(15);
    yearText.setPromptText("Year");
    GridPane.setConstraints(yearText, 0, 1);
    getReportPanelPane.getChildren().add(yearText);

    // Create a get farm report button
    Button getFarmReportButton = new Button("Get Farm Report");
    GridPane.setConstraints(getFarmReportButton, 0, 2);
    getReportPanelPane.getChildren().add(getFarmReportButton);

    // Construct an EventHandler to get farm report
    EventHandler<ActionEvent> getFarmReportEvent = new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        // EVENT IF GET FARM REPORT IS CLICKED
        try {
          if(farmIDText.getText() != null && !farmIDText.getText().isEmpty()
              && yearText.getText() != null && !yearText.getText().isEmpty()) {
            
            String farmId = farmIDText.getText();
            Integer year = Integer.parseInt(yearText.getText());
            
            try {
              for(Double per: cheeseFactory.getFarmReport(farmId, year)) {
                System.out.println(per);
              }
              generateFarmReportGraph(farmId, cheeseFactory.getFarmReport(farmId, year));
              
              totalMilkWeightText = cheeseFactory.getTotalMilkWeightForFarmAndYear(farmId, year);
              
              generateOptionPane();
              
              farmIdPlaceholder = farmId;
              yearPlaceholder = year;
              
              
              generateStatusMessage("Farm report successfully generated!");
              
              //System.out.println();
            } catch (NoSuchFieldException e1) {
              generateStatusMessage("Error. Given farm is invalid!");
            } catch (MissingFormatArgumentException e2) {
              generateStatusMessage("Error. Stated farm is incomplete!");
            } catch(NoSuchElementException e3) {
              generateStatusMessage("Error. Given year is invalid!");
            }
            
            
          }
        }catch(NumberFormatException e2) {
          generateStatusMessage("Input must be an integer!");
        }

      }
    };

    getFarmReportButton.setOnAction(getFarmReportEvent); // Set the action of get farm report button

    // Create a year text field for annual report
    final TextField yearAnnualText = new TextField();
    yearAnnualText.setPrefColumnCount(15);
    yearAnnualText.setPromptText("Year");
    GridPane.setConstraints(yearAnnualText, 0, 3);
    getReportPanelPane.getChildren().add(yearAnnualText);

    // Create a get annual report button
    Button getAnnualReportButton = new Button("Get Annual Report");
    GridPane.setConstraints(getAnnualReportButton, 0, 4);
    getReportPanelPane.getChildren().add(getAnnualReportButton);

    // Construct an EventHandler to annual data
    EventHandler<ActionEvent> getAnnualReportEvent = new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        // EVENT IF GET ANNUAL REPORT IS CLICKED
        try {
          if(yearAnnualText.getText() != null && !yearAnnualText.getText().isEmpty()) {
            Integer year = Integer.parseInt(yearAnnualText.getText());
            boolean fuse = true;
            HashMap<String, Double> annualReport = null;
            try {
              annualReport = cheeseFactory.getAnnualReport(year);
              totalMilkWeightText = cheeseFactory.getTotalMilkWeightForAllFarmAndYear(year);
            }catch(NoSuchElementException e1) {
              fuse = false;
              generateStatusMessage("Error. Year is invalid!");
            }
            
            if(fuse) {
              yearPlaceholder = year;
              
              generateAnnualReportGraph(annualReport);
              generateStatusMessage("Annual report successfully generated!");
              generateOptionPane();
            }
          }
        }catch(NumberFormatException e2) {
          generateStatusMessage("Input must be an integer!");
        }

      }
    };

    getAnnualReportButton.setOnAction(getAnnualReportEvent); // Set the action of get annual report
                                                             // button

    // Create a year text field for monthly report
    final TextField yearMonthlyText = new TextField();
    yearMonthlyText.setPrefColumnCount(15);
    yearMonthlyText.setPromptText("Year");
    GridPane.setConstraints(yearMonthlyText, 0, 5);
    getReportPanelPane.getChildren().add(yearMonthlyText);

    // Create a monthly text field for monthly report
    final TextField monthMonthlyText = new TextField();
    monthMonthlyText.setPrefColumnCount(15);
    monthMonthlyText.setPromptText("Month");
    GridPane.setConstraints(monthMonthlyText, 0, 6);
    getReportPanelPane.getChildren().add(monthMonthlyText);

    // Create a get monthly report button
    Button getMonthlyReportButton = new Button("Get Monthly Report");
    GridPane.setConstraints(getMonthlyReportButton, 0, 7);
    getReportPanelPane.getChildren().add(getMonthlyReportButton);

    // Construct an EventHandler to get monthly data
    EventHandler<ActionEvent> getMonthlyReportEvent = new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        // EVENT IF GET MONTHLY REPORT IS CLICKED
        if(yearMonthlyText.getText() != null && !yearMonthlyText.getText().isEmpty() && 
            monthMonthlyText.getText() != null && !monthMonthlyText.getText().isEmpty()) {
          try {
            if(Integer.parseInt(monthMonthlyText.getText()) >=1 && Integer.parseInt(monthMonthlyText.getText()) <=12) {
              
              Integer year = Integer.parseInt(yearMonthlyText.getText());
              Integer month = Integer.parseInt(monthMonthlyText.getText());
              
              boolean fuse = true;
              HashMap<String, Double> monthlyReport = null;
              
              try {
                monthlyReport = cheeseFactory.getMonthlyReport(year, month);
                totalMilkWeightText = cheeseFactory.getTotalMilkWeightForAllFarmAndMonth(year, month);
              }catch(NoSuchElementException e1) {
                fuse = false;
                generateStatusMessage("Error. Year is invalid!");
              }
              
              if(fuse) {
                yearPlaceholder = year;
                monthPlaceholder = month;

                generateMonthlyReportGraph(monthlyReport);
                generateStatusMessage("Monthly report successfully generated!");
                generateOptionPane();
              }
              
            }else {
              generateStatusMessage("Error. Month given is invalid!");
            }
          }catch(NumberFormatException e2) {
            generateStatusMessage("Input must be an integer!");
          }
          
        }

      }
    };

    getMonthlyReportButton.setOnAction(getMonthlyReportEvent); // Set the action of get monthly
                                                               // report button

    // Create a from date text field
    final TextField fromDateText = new TextField();
    fromDateText.setPrefColumnCount(15);
    fromDateText.setPromptText("From Date mm/dd/yyy");
    GridPane.setConstraints(fromDateText, 0, 8);
    getReportPanelPane.getChildren().add(fromDateText);

    // Create a to date text field
    final TextField toDateText = new TextField();
    toDateText.setPrefColumnCount(15);
    toDateText.setPromptText("To Date mm/dd/yyy");
    GridPane.setConstraints(toDateText, 0, 9);
    getReportPanelPane.getChildren().add(toDateText);

    // Create a get date report button
    Button getDateReportButton = new Button("Get Date Report");
    GridPane.setConstraints(getDateReportButton, 0, 10);
    getReportPanelPane.getChildren().add(getDateReportButton);

    // Construct an EventHandler for date to date report data
    EventHandler<ActionEvent> getDateReportEvent = new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        // EVENT IF GET DATE REPORT IS CLICKED
        if(fromDateText.getText() != null && toDateText.getText() != null) {
          boolean fuse = true;
          HashMap<String, Double> dateRangeReport = null;
          LocalDate parsedFromDate = null;
          LocalDate parsedToDate = null;
          try {
            parsedFromDate = parseDate(fromDateText.getText());
            parsedToDate = parseDate(toDateText.getText());
            
            try {
              dateRangeReport = cheeseFactory.getDateRangeReport(parsedFromDate, parsedToDate);
              totalMilkWeightText = cheeseFactory.getTotalMilkWeightForAllFromDateToDate(parsedFromDate, parsedToDate);              
              
            }catch(DateTimeException e2) {
              fuse = false;
              generateStatusMessage("Error: from date is larger than to date!");
            }
            
          }catch(IllegalArgumentException e1) {
            fuse = false;
            generateStatusMessage("Error: " + e1.getMessage());
          }
          
          if(fuse) {
            fromDatePlaceholder = parsedFromDate;
            toDatePlaceholder = parsedToDate;
            
            generateDateRangeReportGraph(dateRangeReport);
            generateStatusMessage("Date range report successfully generated!");
            generateOptionPane();
          }
          
          
        }

      }
    };

    getDateReportButton.setOnAction(getDateReportEvent); // Set the action of get date report button
    
    getReportPanelPane.setPadding(new Insets(15, 15, 15, 15));
    
    root.setRight(getReportPanelPane);
  }
  
  private LocalDate parseDate(String date) throws IllegalArgumentException{
    String[] dateSplitted = date.split("/");
    
    if(dateSplitted.length < 3) {
      throw new IllegalArgumentException("Date give is an incorrect format!");
    }
    
    LocalDate parsedDate = null;
    
    try {
      Integer month = Integer.parseInt(dateSplitted[0]);
      Integer day = Integer.parseInt(dateSplitted[1]);
      Integer year = Integer.parseInt(dateSplitted[2]);
      
      parsedDate = LocalDate.of(year, month, day);
      
      
    }catch(NumberFormatException e) {
      throw new IllegalArgumentException("Date given is not an integer!");
    }catch(DateTimeException e) {
      throw new IllegalArgumentException("Date given is invalid!");
    }
    
    return parsedDate;
  }

  private void generateTitle() {
    // Create the title label
    Label title = new Label("Milk Weight");
    title.setTextAlignment(TextAlignment.CENTER); // Set center
    title.setFont(Font.font("Comic Sans MS", 35)); // Set the font family and size
    title.setPadding(new Insets(15, 15, 15, 15)); // Create paddings
    StackPane titleBox = new StackPane(); // Create a stack pane for the title
    titleBox.getChildren().add(title); // Place the label to the stackpane
    StackPane.setAlignment(title, Pos.CENTER); // Align center

    root.setTop(titleBox);
  }
  
  private void generateDateRangeReportGraph(HashMap<String, Double> dateRangeReport) {
    
    // defining the axes
    final CategoryAxis xAxis = new CategoryAxis(); // plot against time
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Farms");
    xAxis.setAnimated(true); // axis animations are removed
    yAxis.setLabel("Milk Weight Percentage");
    yAxis.setAnimated(true); // axis animations are removed
    
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    
    lineChart.setAnimated(true);
    
    XYChart.Series<String, Number> farm = new XYChart.Series<>();
    farm.setName("Farms");
    
    lineChart.getData().add(farm);
    
    Set<String> farmIds = dateRangeReport.keySet();
    ArrayList<String> farmIdsSorted = new ArrayList<>();
    
    for(String farmId: farmIds) {
      farmIdsSorted.add(farmId);
    }
    
    Collections.sort(farmIdsSorted);
    System.out.println(farmIdsSorted);
    
    
    for(int i = 0; i < farmIdsSorted.size(); i++) {
      farm.getData().add(new XYChart.Data<>(farmIdsSorted.get(i), dateRangeReport.get(farmIdsSorted.get(i))));
    }
    
    saveType = 4;

    root.setCenter(lineChart);
    
  }
  
  private void generateMonthlyReportGraph(HashMap<String, Double> monthlyReport) {
    
    // defining the axes
    final CategoryAxis xAxis = new CategoryAxis(); // plot against time
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Farms");
    xAxis.setAnimated(true); // axis animations are removed
    yAxis.setLabel("Milk Weight Percentage");
    yAxis.setAnimated(true); // axis animations are removed
    
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    
    lineChart.setAnimated(true);
    
    XYChart.Series<String, Number> farm = new XYChart.Series<>();
    farm.setName("Farms");
    
    lineChart.getData().add(farm);
    
    Set<String> farmIds = monthlyReport.keySet();
    ArrayList<String> farmIdsSorted = new ArrayList<>();
    
    for(String farmId: farmIds) {
      farmIdsSorted.add(farmId);
    }
    
    Collections.sort(farmIdsSorted);
    
    
    for(int i = 0; i < farmIdsSorted.size(); i++) {
      farm.getData().add(new XYChart.Data<>(farmIdsSorted.get(i), monthlyReport.get(farmIdsSorted.get(i))));
    }
    
    saveType = 3;

    root.setCenter(lineChart);
    
  }
  
  private void generateAnnualReportGraph(HashMap<String, Double> annualReport) {
    
    // defining the axes
    final CategoryAxis xAxis = new CategoryAxis(); // plot against time
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Farms");
    xAxis.setAnimated(true); // axis animations are removed
    yAxis.setLabel("Milk Weight Percentage");
    yAxis.setAnimated(true); // axis animations are removed
    
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    
    lineChart.setAnimated(true);
    lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);
    
    XYChart.Series<String, Number> farm = new XYChart.Series<>();
    farm.setName("Farms");
    
    lineChart.getData().add(farm);
    
    Set<String> farmIds = annualReport.keySet();
    ArrayList<String> farmIdsSorted = new ArrayList<>();
    
    for(String farmId: farmIds) {
      farmIdsSorted.add(farmId);
    }
    
    
    Collections.sort(farmIdsSorted);
    
    
    System.out.println(farmIdsSorted);
    
    
    for(int i = 0; i < farmIdsSorted.size(); i++) {
      System.out.println(farmIdsSorted.get(i));
      farm.getData().add(new XYChart.Data<>(farmIdsSorted.get(i), annualReport.get(farmIdsSorted.get(i))));
    }
    
    saveType = 2;
    
    root.setCenter(lineChart);
    
  }
  
  private void generateFarmReportGraph(String farmId, ArrayList<Double> percentages) {
    
    // defining the axes
    final CategoryAxis xAxis = new CategoryAxis(); // plot against time
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Month");
    xAxis.setAnimated(true); // axis animations are removed
    yAxis.setLabel("Milk Weight");
    yAxis.setAnimated(true); // axis animations are removed

    // creating the line chart with two axis created above
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    
    lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);

    lineChart.setAnimated(true); // disable animations

    XYChart.Series<String, Number> farm = new XYChart.Series<>();
    farm.setName(farmId);

    // add series to chart
    lineChart.getData().add(farm);

    for(int i = 0; i < 12; i++) {
      farm.getData().add(new XYChart.Data<>(Integer.toString(i+1), percentages.get(i)));
    }
    
    saveType = 1;
   
    root.setCenter(lineChart);
  }

  private void generateGraph() {
    // defining the axes
    final CategoryAxis xAxis = new CategoryAxis(); // plot against time
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Month");
    xAxis.setAnimated(false); // axis animations are removed
    yAxis.setLabel("Milk Weight");
    yAxis.setAnimated(false); // axis animations are removed

    // creating the line chart with two axis created above
    final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    lineChart.setAnimated(false); // disable animations

    XYChart.Series<String, Number> farm1 = new XYChart.Series<>();
    farm1.setName("Farm 1");

    XYChart.Series<String, Number> farm2 = new XYChart.Series<>();
    farm2.setName("Farm 2");

    // add series to chart
    lineChart.getData().add(farm1);
    lineChart.getData().add(farm2);

    farm1.getData().add(new XYChart.Data<>("1", 2));
    farm1.getData().add(new XYChart.Data<>("2", 10));
    farm1.getData().add(new XYChart.Data<>("3", 1));

    farm2.getData().add(new XYChart.Data<>("1", 1));
    farm2.getData().add(new XYChart.Data<>("2", 5));
    farm2.getData().add(new XYChart.Data<>("3", 9));
    farm2.getData().add(new XYChart.Data<>("4", 1));
    farm2.getData().add(new XYChart.Data<>("5", 5));
    farm2.getData().add(new XYChart.Data<>("6", 9));
    farm2.getData().add(new XYChart.Data<>("7", 1));
    farm2.getData().add(new XYChart.Data<>("8", 5));
    farm2.getData().add(new XYChart.Data<>("9", 9));
    farm2.getData().add(new XYChart.Data<>("10", 1));
    farm2.getData().add(new XYChart.Data<>("11", 5));
    farm2.getData().add(new XYChart.Data<>("12", 9));


    root.setCenter(lineChart);
  }
  
  private void generateStatusMessage(String msg) {
    GridPane statusMsgBox = new GridPane();
    
    Label statusText = new Label("Status: " + msg);
    statusText.setFont(Font.font("Comic Sans MS", 20));
    statusText.setPadding(new Insets(15, 15, 15, 15));
    
    statusMsgBox.getChildren().add(statusText);
    
    root.setBottom(statusMsgBox);
    
  }
  
  private void generateOptionPane() {
    TilePane leftPane = new TilePane(Orientation.VERTICAL);
    
    String[] options = {"Load/Save Data", "Get Report"};
    ComboBox<String> comboBox = new ComboBox<String>(FXCollections.observableArrayList(options));
    
    // Create a listener for the combobox
    comboBox.valueProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> arg, String arg1, String arg2) {
        if (arg.getValue() == options[0]) { // If input/output data is chosen
          // Generate the load save panel
          generateLoadSavePanel();

        } else if (arg.getValue() == options[1]) { // If filter Data
          // Generate the filter data panel
          generateGetReportPanel();

        }
      }
    });
    
    leftPane.getChildren().add(comboBox);
    totalMilkWeightText = this.totalMilkWeightText;
    
    Label totalMilkLabel = new Label("Total milk weight: " + totalMilkWeightText + " lbs");
    leftPane.getChildren().add(totalMilkLabel);
    
    leftPane.setPadding(new Insets(15, 15, 15, 15));
    
    root.setLeft(leftPane); // Place the combobox button to the left borderpane
    
    
  }

  @Override
  public void start(Stage arg0) throws Exception {
    
    mainStage = arg0;
    
    this.root = new BorderPane();
    
    this.cheeseFactory = new FarmGroup();

    generateOptionPane();
    
    generateStatusMessage("Please load the milk weight data!");

    generateTitle(); // Generate the title

    generateGraph(); // Generate graph

    // Create the main scene
    Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

    arg0.setTitle(APP_TITLE); // Set window title
    arg0.setScene(mainScene); // Set main scene
    arg0.show(); // Show the scene
  }

  public static void main(String[] args) {
    launch(args);
  }

}
