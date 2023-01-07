package org.dsc.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.dsc.components.MetaPieChart;
import org.dsc.dataClasses.MetaData;
import org.dsc.dbProviders.postGrestest;
import org.dsc.utilties.DBConnection;
import org.dsc.utilties.jdbcDetails;


import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DashBoard implements Initializable {

    @FXML
    private TableView<MetaData> tableView = new TableView<>();
    @FXML
    private TableColumn<MetaData, Number> count = new TableColumn<>("count");
    @FXML
    private TableColumn<MetaData, String> metaDataKey = new TableColumn<>("Value");
    @FXML
    private PieChart pie;
    @FXML
    ResultSet rs;
    private Label label;
    private final ObservableList<MetaData> data = FXCollections.observableArrayList();
    private final ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
    private jdbcDetails jdbcDetails;

    public void passJdBcDetails(jdbcDetails jdbcDetails){
        this.jdbcDetails = jdbcDetails;
        DBConnection dbConnection = new DBConnection();
        System.out.println(jdbcDetails);
        dbConnection.setJDBCUrl(jdbcDetails.getUrl());
        try {
            dbConnection.setDBConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            rs = dbConnection.runSelectStatment();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        setMetaTableView();
        MetaPieChart mpc = new MetaPieChart();
        try {
            pie = mpc.setUpPieChart(rs, pie, pieData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {



    }

    private void setMetaTableView() {
        count.setCellValueFactory(cellData -> cellData.getValue().getCountProperty());
        metaDataKey.setCellValueFactory(cellData -> cellData.getValue().metaDataKey);
        try {
            while (true) {
                try {
                    if (!rs.next()) break;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                data.add(new MetaData(rs.getInt("value"), rs.getString("text"), ""));

            }
            tableView.setItems(data);
            tableView.getItems().forEach(data -> {
                        Tooltip tooltip = new Tooltip();
                        Duration duration = Duration.millis(1);
                        tooltip.setShowDelay(duration);
                        tooltip.setText(data.getMetaDataKey());
                    }
            );
            System.out.println(tableView.getColumns());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getPGResults() {
        postGrestest pg = new postGrestest();
        try {
            pg.setCon();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            rs = pg.runStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

