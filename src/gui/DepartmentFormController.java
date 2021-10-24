package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
	public void setDepartment(Department entity) {
		this.entity=entity;
	}
	
	private DepartmentService service;
	
	public void setDepartmentService(DepartmentService service) {
		this.service=service;
	}
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	public void subscibeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}
	
	public void updateFormData() {
		if (entity==null) {
			throw new IllegalStateException("Entity is null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(String.valueOf(entity.getName()));
	}
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML 
	private Button btSave;
	
	@FXML 
	private Button btCancel;
	
	@FXML 
	public void onBtSaveAction(ActionEvent event) {
		if (entity==null)
			throw new IllegalStateException("Entity is null");
		if (service==null)
			throw new IllegalStateException("Service is null");
		try {
			entity = returnDepartmentObjectWithTypedIdAndName();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.returnCurrentStage(event).close();
		}catch (ValidationException e) {
			setErrorMessages(e.getErros());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
		

	}

	private Department returnDepartmentObjectWithTypedIdAndName() {
		Department obj = new Department();
		
		ValidationException exception = new ValidationException("Validation Error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if (txtName.getText()==null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		
		if (exception.getErros().size()>0) {
			throw exception;
		}
		return obj;
	}

	@FXML 
	public void onBtCancelAction(ActionEvent event) {
		Utils.returnCurrentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	private void setErrorMessages(Map<String,String>errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
