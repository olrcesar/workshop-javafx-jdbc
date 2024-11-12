package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	
	private DepartmentService depService;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btnNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtnNewAction() {
		System.out.println("onBtnNewAction");
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.depService = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initialize();
	}
	
	private void initialize() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<Department, Integer>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<Department, String>("name"));;
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		if(depService == null) {
			throw new IllegalStateException("Service was null!");
		}
		
		List<Department> list = depService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
}
