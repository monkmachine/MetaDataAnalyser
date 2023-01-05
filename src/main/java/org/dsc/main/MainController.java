package org.dsc.main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.dsc.utilties.DBConnection;
import org.dsc.utilties.jsonReader;
import org.dsc.utilties.tikaRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public PasswordField password;
    @FXML
    public TextField jdbcUrl;
    @FXML
    public TextField userId;
    @FXML
    public Button testConnection;
    @FXML
    public Label connectionTest;
    @FXML
    public Label progressTxt;
    @FXML
    public Label inSelectedFolder;
    @FXML
    public Label outSelectedFolder;
    @FXML
    public Button runProcess;
    public Button stopProcess;
    @FXML
    ComboBox<String> dbDropDown;
    @FXML
    private  ProgressBar bar;
    private Thread metaScrapeThread;
    private final DBConnection dbCon = new DBConnection();


    @FXML
    protected void onInSelectedFolderButtonClick() {
        setSelectedFolderLabel("in");
    }

    @FXML
    protected void onOutSelectedFolderButtonClick() {
        setSelectedFolderLabel("out");
    }

    @FXML
    protected void onTestConnection() {
        System.out.println(jdbcUrl.getText());
        String conUrl = jdbcUrl.getText();
        dbCon.setJDBCUrl(conUrl);
        try {
            dbCon.setDBConnection();
            connectionTest.setTextFill(Color.rgb(45, 189, 45));
            connectionTest.setStyle("-fx-font-weight: bold;");
            connectionTest.setText("Success");
        } catch (SQLException e) {
            connectionTest.setTextFill(Color.rgb(163, 42, 42));
            connectionTest.setWrapText(true);
            connectionTest.setStyle("-fx-font-weight: bold;");
            connectionTest.setText(e.getMessage());
        }

    }
    @FXML
    protected void onRunProcess() {
        runLlongTask();
    }

    @FXML
    protected void onChangeDbDropDown(){
        jdbcUrl.setText(dbCon.jDBCUrlformat(dbDropDown.getSelectionModel().getSelectedItem()));
    }
    protected void setSelectedFolderLabel(String inOutFolder) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        Window mainStage = null;
        File selectedFile = directoryChooser.showDialog(mainStage);
        if (selectedFile != null) {
            if (Objects.equals(inOutFolder, "in")) {
                inSelectedFolder.setText(selectedFile.getAbsolutePath());
            } else {
                outSelectedFolder.setText(selectedFile.getAbsolutePath());
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> dbDropDownOptions =
                FXCollections.observableArrayList(
                        "Postgres",
                        "SQL Server",
                        "Oracle"
                );
        dbDropDown.setItems(dbDropDownOptions);
        dbDropDown.getSelectionModel().selectFirst();

        jdbcUrl.setText(dbCon.jDBCUrlformat(dbDropDown.getSelectionModel().getSelectedItem()));


    }

    public void killThread(){
        System.out.println(metaScrapeThread.getState());
        metaScrapeThread.interrupt();
        System.out.println("ProcessKilled");
        bar.progressProperty().unbind();
        bar.progressProperty().set(0);
        System.out.println(metaScrapeThread.getState());
        try {
            dbCon.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        progressTxt.setText("Process Stopped");
    }
    void runLlongTask() {
        Task<Void> metaTask = new processFolder();
        metaScrapeThread = new Thread(metaTask);
        metaScrapeThread.setDaemon(true);
        //bind progress bar to both task progress property
        bar.progressProperty().bind(metaTask.progressProperty());
        metaScrapeThread.start();
    }


    class processFolder extends Task<Void>{

        protected Void call() throws Exception {

            dbCon.setJDBCUrl(jdbcUrl.getText());
            try {
                dbCon.setDBConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            File f = new File("G:\\SampleFiles");
            File[] pathnames;
            pathnames = f.listFiles();
            int i = 0;
            tikaRequest tr = new tikaRequest();
            tr.setServiceUrl("http://localhost:9998/meta");
            if (pathnames != null) {
                for (File file : pathnames) {
                    i = i  +1;
                    tr.setUploadFile(file.getAbsolutePath());
                    InputStream resp;
                    try {
                        resp = tr.makeTikaRequest();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //Thread.sleep(1000);
                    jsonReader jsonRead;
                    try {
                        jsonRead = new jsonReader();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    Map<String,String> keyValPairs;
                    try {
                        keyValPairs = jsonRead.processMetaFile(resp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    for (Map.Entry<String, String> entry : keyValPairs.entrySet()) {

                        try {
                            dbCon.runStatement(file.getName(),entry.getKey() ,entry.getValue());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    dbCon.commit();
                    updateProgress(i,pathnames.length);
                    String progressTxtVal = "Files Processed  " + i + "/" + pathnames.length;
                    Platform.runLater(() -> progressTxt.setText(progressTxtVal));
                }
            }
            dbCon.close();
            Platform.runLater(() -> {
                if (pathnames != null) {
                    Platform.runLater(() -> progressTxt.setText("Process complete " + pathnames.length + " files were processed"));
                }
            });
            return null;
        }
    }
}
