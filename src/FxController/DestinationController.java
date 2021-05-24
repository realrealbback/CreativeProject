package FxController;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import DAO.InquireByRegionDAO;
import DTO.DestinationDTO;
import DataSetControl.RegionList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DestinationController implements Initializable{
    @FXML private ComboBox<String> comboBoxClassification;
    @FXML private ComboBox<String> comboBoxDo;
    @FXML private ComboBox<String> comboBoxCity;
    @FXML private TableView<DestinationDTO> myTableView;
	@FXML private TableColumn<DestinationDTO, String> sortColumn;
	@FXML private TableColumn<DestinationDTO, String> nameColumn;
	@FXML private TableColumn<DestinationDTO, String> addressColumn;
	@FXML private TableColumn<DestinationDTO, String> gradeColumn;

    @Override
	public void initialize(URL location, ResourceBundle bundle) {
        //  RegionList class에서 Do 호출
        String[] DoList = RegionList.Do;
        comboBoxDo.getItems().addAll(DoList);
        comboBoxClassification.setItems(FXCollections.observableArrayList(
				"통합", "해수욕장", "휴양림", "관광지"));
        sortColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSortation()));
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDo() + " " + cellData.getValue().getCity() + " " + cellData.getValue().getAddress()));
		gradeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getScope())));
	}

    //  도 선택 시 시/군 추가
    @FXML
    public void handleCbBoxDo(ActionEvent e) {
        comboBoxCity.getItems().clear();
        String Do = comboBoxDo.getValue();
        // 특별시, 광역시인 경우 제외
        if(!(Do.equals("서울특별시") || Do.equals("울산광역시") || Do.equals("인천광역시") || Do.equals("대전광역시") || Do.equals("제주특별자치도") || Do.equals("대구광역시") || Do.equals("부산광역시"))){
            //  RegionList class에서 hsMap 호출
            HashMap<String, String[]> hsMap = RegionList.hsMap;
            String[] cityList = hsMap.get(Do);
            comboBoxCity.getItems().addAll(cityList);
        }
    }
    //  검색 버튼 event
    @FXML
	public void handleBtnSearch(ActionEvent e) {
        myTableView.getItems().clear();
        ArrayList<DestinationDTO> list = null;
        InquireByRegionDAO inquireByRegionDAO = new InquireByRegionDAO();
        String sortation, Do, city;
        if(comboBoxDo.getValue() == null){
            ShowAlert.showAlert("WARNING", "경고", "도 미선택");
            return;
        }
        else if(comboBoxCity.getValue() == null && !(comboBoxDo.getValue().equals("서울특별시") || comboBoxDo.getValue().equals("인천광역시") || comboBoxDo.getValue().equals("울산광역시") 
        || comboBoxDo.getValue().equals("대전광역시") || comboBoxDo.getValue().equals("대구광역시") || comboBoxDo.getValue().equals("제주특별자치도") || comboBoxDo.getValue().equals("부산광역시"))){
            ShowAlert.showAlert("WARNING", "경고", "시/군 미선택");
            return;
        }
        //  구분 미선택 조회
        if(comboBoxClassification.getValue() == null || comboBoxClassification.getValue().equals("통합")){
            Do = comboBoxDo.getValue();
            if(Do.equals("서울특별시") || Do.equals("인천광역시") || Do.equals("울산광역시") || Do.equals("대전광역시") || Do.equals("대구광역시") || Do.equals("제주특별자치도") || Do.equals("부산광역시")){
                list = inquireByRegionDAO.inquireDestinationByRegion("", Do, "");
            }
            else{
                city = comboBoxCity.getValue();
                list = inquireByRegionDAO.inquireDestinationByRegion("", Do, city);
            }
        }
        //  구분 선택 조회
        else{
            sortation = comboBoxClassification.getValue();
            Do = comboBoxDo.getValue();
            if(Do.equals("서울특별시") || Do.equals("인천광역시") || Do.equals("울산광역시") || Do.equals("대전광역시") || Do.equals("대구광역시") || Do.equals("제주특별자치도") || Do.equals("부산광역시")){
                list = inquireByRegionDAO.inquireDestinationByRegion(sortation, Do, "");
            }
            else{
                city = comboBoxCity.getValue();
                list = inquireByRegionDAO.inquireDestinationByRegion(sortation, Do, city);
            }
        }
        if(list == null){
            ShowAlert.showAlert("INFORMATION", "알림창", "검색 결과가 없습니다.");
            return;
        }
        myTableView.getItems().addAll(list);
	}
    @FXML
    public void doubleClickMouse(MouseEvent event){
        if(myTableView.getSelectionModel().getSelectedItem()!=null){
            if(event.getClickCount() > 1){
                try {
                    FXMLLoader loader = null;
                    if(myTableView.getSelectionModel().getSelectedItem().getSortation().equals("해수욕장")){
                        loader = new FXMLLoader(getClass().getResource("../FXML/beach_detail.fxml"));
                    }
                    if(myTableView.getSelectionModel().getSelectedItem().getSortation().equals("휴양림")){
                        loader = new FXMLLoader(getClass().getResource("../FXML/forest_lodge_detail.fxml"));
                    }
                    if(myTableView.getSelectionModel().getSelectedItem().getSortation().equals("관광지")){
                        loader = new FXMLLoader(getClass().getResource("../FXML/tourist_spot_detail.fxml"));
                    }
                    Parent root = (Parent)loader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    System.out.println(12314);
                   // ModifyController modifyController = loader.<ModifyController>getController();
                   // modifyController.setData(dataList, list, temp, expenseList, incomeList);
                    stage.showAndWait();
                    
                }
                catch(Exception e) {
                    System.out.println(e);
                }
            }

        }

    }
}
