package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener{
	
	private DepartmentService depService;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private TableColumn<Department, Department> tableColumnEdit;
	
	@FXML
	private TableColumn<Department, Department> tableColumnRemove;
	
	@FXML
	private Button btnNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department dep = new Department();
		createDialogForm(dep, "/gui/DepartmentForm.fxml", parentStage);
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.depService = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
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
		initEditButtons();
		initRemoveButtons();
	}
	
	private void createDialogForm(Department dep, String absoluteName, Stage parentStage) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(dep);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		} 
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
		
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}
	
	private void initEditButtons() {
		
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>(){
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", 
								Utils.currentStage(event)));
			}
		});
		
	}
	
	private void initRemoveButtons() {
		
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null)  {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}

		});
	}

	private void removeEntity(Department obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if (result.get() == ButtonType.OK) {
			if(depService == null) {
				throw new IllegalStateException("Service was null!");
			}
			try {
				depService.remove(obj);
				updateTableView();
			} 
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
