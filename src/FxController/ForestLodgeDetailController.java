package FxController;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import DAO.DetailDAO;
import DTO.BeachDTO;
import DTO.FavoriteDTO;
import DTO.ForestLodgeDTO;
import DTO.ReviewDTO;
import DataSetControl.RegionList;
import Network.Protocol;
import Network.clientMain;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class ForestLodgeDetailController implements Initializable {

	@FXML private Text resultTextName;
	@FXML private Text resultTextAddress;
	@FXML private Text resultTextPhoneNum;
	@FXML private Text resultTextAmenities;
	@FXML private Text resultTextCapacityPeople;
	@FXML private Text reusltTextEnterFee;
	@FXML private Text resultTextAcconmodation;
	@FXML private TextArea resultTextHomePage;
	@FXML private Button btn_favorite;
	@FXML private Button btn_registerImg;
	@FXML private ComboBox<Integer> cb_star;
	ObservableList<Integer> starList = FXCollections.observableArrayList(1, 2, 3, 4, 5);
	@FXML private Button btn_registerReview;
	@FXML private TextArea ta_reviewContent;
	@FXML private TableView<ReviewDTO> tv_review;
	@FXML private TableColumn<ReviewDTO, String> tc_date;
	@FXML private TableColumn<ReviewDTO, String> tc_content;
	@FXML private TableColumn<ReviewDTO, String> tc_star;
	@FXML private TableColumn<ReviewDTO, String> tc_writer;
	@FXML private PieChart pieChart;
	@FXML private WebView webView;
	private Tooltip tooltip;
	private PieChart.Data pData;
	private String forestCode;	//	????????? beachCode ?????? ??????
	private String userId;		//	????????? userId ?????? ??????
	private String destinationCode;	// 	????????? destinationCode ?????? ??????
	private String destinationName;	//	????????? destinationName ?????? ??????
	private byte[] imageInByte;
	private double latitude;
	private double longitude;
	public void setLatLng() { //????????? ??? ?????????
		WebEngine webEngine = webView.getEngine();

		webEngine.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) -> {
			System.out.println(newValue);
			if (newValue == Worker.State.SUCCEEDED) {
				System.out.println("finished loading");
				String lat1 = Double.toString(latitude);
				String lnt2 = Double.toString(longitude);
				webEngine.executeScript("document.getElementById('keyword').value='" + lat1 + " " + lnt2 +"'");
				webEngine.executeScript("test()");
				String html = (String) webEngine.executeScript("document.getElementById('keyword').value");
				System.out.println(html);
			}/*from w  w  w.java  2s.co  m*/
		});
		webEngine.load("http://localhost:8081/detail.html");
	}
	public void setForestLodgeCode(String forestcode){
		this.forestCode = forestcode;
		DetailDAO detailDAO = new DetailDAO();
		ForestLodgeDTO forestLodgeDTO = detailDAO.detailForestLodge(forestcode);
		resultTextName.setText(forestLodgeDTO.getName());
		resultTextAddress.setText(forestLodgeDTO.getDo() + " " + forestLodgeDTO.getCity() + " " + forestLodgeDTO.getAddress());
		resultTextPhoneNum.setText(forestLodgeDTO.getPhone_num());
		resultTextAmenities.setText(forestLodgeDTO.getAmenities());
		resultTextCapacityPeople.setText(forestLodgeDTO.getCapacity_people());
		reusltTextEnterFee.setText(forestLodgeDTO.getEnter_fee());
		resultTextAcconmodation.setText(forestLodgeDTO.getAccommodation());
		resultTextHomePage.setText(forestLodgeDTO.getHome_page());
		latitude = forestLodgeDTO.getLatitude();
		longitude = forestLodgeDTO.getLongitude();
		destinationName = forestLodgeDTO.getName();

		
	}
	public void setSaveUserId(String userId){
		this.userId = userId;
	}
	public void setDestinationCode(String destinationCode){
		this.destinationCode = destinationCode;
		tv_review.getItems().clear();
		DetailDAO detailDAO = new DetailDAO();
		ArrayList<ReviewDTO> list = detailDAO.inquireReview(destinationCode);
		tv_review.getItems().addAll(list);
	}
	public void setDestinationName(String destinationName){
		this.destinationName = destinationName;
	}
	public void setForestDetail(String forestCode, String userId, String destinationCode, String destinationName) {
		this.forestCode = forestCode;
		this.userId = userId;
		this.destinationCode = destinationCode;
		this.destinationName = destinationName;
//		clientMain.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.REQ_FOREST_DETAIL+ "`" + forestCode + "`" + destinationCode);
		ArrayList<Object> objectList = new ArrayList<Object>();
		objectList.add(Protocol.PT_REQ_VIEW);
		objectList.add(Protocol.REQ_FOREST_DETAIL);
		objectList.add(forestCode);
		objectList.add(destinationCode);
		clientMain.writeObject(objectList);
		objectList.clear();

		while (true) {
			// String packet = clientMain.readPacket();
			// String packetArr[] = packet.split("`");
			// String packetType = packetArr[0];
			// String packetCode = packetArr[1];
			ArrayList<Object> arrList = (ArrayList<Object>) clientMain.readObject();
			String packetType = (String) arrList.get(0);
			String packetCode = (String) arrList.get(1);

			if (packetType.equals(Protocol.PT_RES_VIEW)) {
				switch (packetCode) {
					case Protocol.RES_FOREST_DETAIL_Y: {
						// ForestLodgeDTO forestLodgeDTO = (ForestLodgeDTO) clientMain.readObject();
						ForestLodgeDTO forestLodgeDTO = (ForestLodgeDTO) arrList.get(2);
						resultTextName.setText(forestLodgeDTO.getName());
						resultTextAddress.setText(forestLodgeDTO.getDo() + " " + forestLodgeDTO.getCity() + " " + forestLodgeDTO.getAddress());
						resultTextPhoneNum.setText(forestLodgeDTO.getPhone_num());
						resultTextAmenities.setText(forestLodgeDTO.getAmenities());
						resultTextCapacityPeople.setText(forestLodgeDTO.getCapacity_people());
						reusltTextEnterFee.setText(forestLodgeDTO.getEnter_fee());
						resultTextAcconmodation.setText(forestLodgeDTO.getAccommodation());
						resultTextHomePage.setText(forestLodgeDTO.getHome_page());
						System.out.println("forestDTO ??????");

						// ArrayList<ReviewDTO> list = (ArrayList<ReviewDTO>) clientMain.readObject();
						ArrayList<ReviewDTO> list = (ArrayList<ReviewDTO>) arrList.get(3);
						tv_review.getItems().addAll(list);
						System.out.println("??????DTO ??????");
						
						setLatLon("?????????", forestCode);
						return;
					}
					case Protocol.RES_FOREST_DETAIL_N: {
						ShowAlert.showAlert("WARNING", "??????", "????????? ????????? ??????????????? ?????????????????????.");
						return;
					}
				}
			}
		}
	}
	public void setLatLon(String sortation, String code) {
		ArrayList<Object> objectList = new ArrayList<Object>();
		objectList.add(Protocol.PT_REQ_VIEW);
		objectList.add(Protocol.REQ_LATLON);
		objectList.add(sortation);
		objectList.add(code);
		clientMain.writeObject(objectList);
		objectList.clear();
		
		while (true) {
			// String packet = clientMain.readPacket();
			// String packetArr[] = packet.split("`");
			// String packetType = packetArr[0];
			// String packetCode = packetArr[1];
			ArrayList<Object> arrList = (ArrayList<Object>) clientMain.readObject();
			String packetType = (String)arrList.get(0);
			String packetCode = (String)arrList.get(1);
			
			if (packetType.equals(Protocol.PT_RES_VIEW)) {
				switch (packetCode) {
					case Protocol.RES_LATLON_Y: {
						// BeachDTO beachDTO = (BeachDTO) clientMain.readObject();
						String latLon = (String) arrList.get(2);
						String splitStr[] = latLon.split(" ");
						latitude = Double.parseDouble(splitStr[0]);
						longitude = Double.parseDouble(splitStr[1]);
						return;
					}
					case Protocol.RES_LATLON_N: {
						ShowAlert.showAlert("WARNING", "??????", "??????, ?????? ????????? ?????????????????????.");
						return;
					}
				}
			}
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setLatLng();
		cb_star.setItems(starList);
		tc_date.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getReporting_date()).toString()));
		tc_content.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
		tc_star.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getScope())));
		tc_writer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser_id()));
	}
	// 	???????????? ??????
	@FXML
	public void handleBtnFavorite(ActionEvent event){
		FavoriteDTO favoriteDTO = new FavoriteDTO(userId, destinationCode, destinationName, Timestamp.valueOf(LocalDateTime.now()),"?????????");
//		clientMain.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.REQ_CREATE_FAVORITES);
//		clientMain.writeObject(favoriteDTO);

		ArrayList<Object> objectList = new ArrayList<Object>();
		objectList.add(Protocol.PT_REQ_RENEWAL);
		objectList.add(Protocol.REQ_CREATE_FAVORITES);
		objectList.add(favoriteDTO);
		clientMain.writeObject(objectList);
		objectList.clear();

		while (true) {
			ArrayList<Object> packet =(ArrayList<Object>) clientMain.readObject();
			System.out.println(packet);
			//String packetArr[] = packet.split("`");
			String packetType = (String) packet.get(0);
			String packetCode = (String) packet.get(1);
			
			if (packetType.equals(Protocol.PT_RES_RENEWAL)) {
				switch (packetCode) {
					case Protocol.RES_CREATE_FAVORITES_Y: {
						ShowAlert.showAlert("INFORMATION", "?????????", "???????????? ?????? ??????!");
						return;
					}
					case Protocol.RES_CREATE_FAVORITES_N: {
						ShowAlert.showAlert("WARNING", "??????", "?????? ????????? ?????????????????????.");
						return;
					}
				}
			}
		}
	}
	//	????????? ??????
	@FXML
	public void handleBtnAgeStat(ActionEvent event){
		pieChart.getData().clear();
		
		//clientMain.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.REQ_STATISTICS_DETAIL + "`" + "?????????" + "`" + destinationCode);
		ArrayList<Object> objectList = new ArrayList<Object>();
		objectList.add(Protocol.PT_REQ_VIEW);
		objectList.add(Protocol.REQ_STATISTICS_DETAIL);
		objectList.add("?????????");
		objectList.add(destinationCode);
		clientMain.writeObject(objectList);
		objectList.clear();


		while (true) {
			ArrayList<Object> packet =(ArrayList<Object>) clientMain.readObject();
			//String packetArr[] = packet.split("`");
			String packetType = (String) packet.get(0);
			String packetCode = (String) packet.get(1);
			
			if (packetType.equals(Protocol.PT_RES_VIEW)) {
				switch (packetCode) {
					case Protocol.RES_STATISTICS_DETAIL_Y: {
						HashMap<Integer, Integer> hsMap = (HashMap<Integer, Integer>) packet.get(2);
						for(int i = 10; i <= 60; i+=10){
							if(hsMap.get(i) != 0){
								String age = Integer.toString(i) + "???";
								System.out.println(age);
								if(i == 60){
									age += " ??????";
								}
								pData = new PieChart.Data(age, hsMap.get(i));
								pieChart.getData().add(pData);
							}
						} 
						pieChartCaption(pieChart);
						return;
					}
					case Protocol.RES_STATISTICS_DETAIL_N: {
						ShowAlert.showAlert("WARNING", "??????", "????????? ?????? ????????? ?????????????????????.");
						return;
					}
				}
			}
		}
		
//		DetailDAO detailDAO = new DetailDAO();
//		HashMap<Integer, Integer> hsMap = detailDAO.ageStatistic(destinationCode);
//		for(int i = 10; i <= 60; i+=10){
//			if(hsMap.get(i) != 0){
//				String age = Integer.toString(i) + "???";
//				System.out.println(age);
//				if(i == 60){
//					age += " ??????";
//				}
//				pData = new PieChart.Data(age, hsMap.get(i));
//				pieChart.getData().add(pData);
//			}
//		}
//		pieChartCaption(pieChart);
	}
	//	?????? ??????
	@FXML
	public void handleBtnGenderStat(ActionEvent event){
		pieChart.getData().clear();
		//clientMain.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.REQ_STATISTICS_DETAIL + "`" + "??????" + "`" + destinationCode);
		ArrayList<Object> objectList = new ArrayList<Object>();
		objectList.add(Protocol.PT_REQ_VIEW);
		objectList.add(Protocol.REQ_STATISTICS_DETAIL);
		objectList.add("??????");
		objectList.add(destinationCode);
		clientMain.writeObject(objectList);
		objectList.clear();

		while (true) {
			ArrayList<Object> packet = (ArrayList<Object>)clientMain.readObject();
			//String packetArr[] = packet.split("`");
			String packetType = (String) packet.get(0);
			String packetCode = (String) packet.get(1);
			
			if (packetType.equals(Protocol.PT_RES_VIEW)) {
				switch (packetCode) {
					case Protocol.RES_STATISTICS_DETAIL_Y: {
						String genderResult = (String)packet.get(2);
						//	"/"??? ?????? -> ?????? ?????????/?????? ?????????
						int menCount = Integer.parseInt(genderResult.split("/")[0]);
						int womenCount = Integer.parseInt(genderResult.split("/")[1]);
						pData = new PieChart.Data("??????", menCount);
						pieChart.getData().add(pData);
						pData = new PieChart.Data("??????", womenCount);
						pieChart.getData().add(pData);

						pieChartCaption(pieChart);
						return;
					}
					case Protocol.RES_STATISTICS_DETAIL_N: {
						ShowAlert.showAlert("WARNING", "??????", "?????? ?????? ????????? ?????????????????????.");
						return;
					}
				}
			}
		}
//		DetailDAO detailDAO = new DetailDAO();
//		String genderResult = detailDAO.genderStatistic(destinationCode);
//		//	"/"??? ?????? -> ?????? ?????????/?????? ?????????
//		int menCount = Integer.parseInt(genderResult.split("/")[0]);
//		int womenCount = Integer.parseInt(genderResult.split("/")[1]);
//		pData = new PieChart.Data("??????", menCount);
//		pieChart.getData().add(pData);
//		pData = new PieChart.Data("??????", womenCount);
//		pieChart.getData().add(pData);
//
//		pieChartCaption(pieChart);

	}
	//	???????????? ??????
	@FXML
	public void handleBtnRegionStat(ActionEvent event){
		pieChart.getData().clear();
		//clientMain.writePacket(Protocol.PT_REQ_VIEW + "`" + Protocol.REQ_STATISTICS_DETAIL + "`" + "?????????" + "`" + destinationCode);
		ArrayList<Object> objectList = new ArrayList<Object>();
		objectList.add(Protocol.PT_REQ_VIEW);
		objectList.add(Protocol.REQ_STATISTICS_DETAIL);
		objectList.add("?????????");
		objectList.add(destinationCode);
		clientMain.writeObject(objectList);
		objectList.clear();

		while (true) {
			ArrayList<Object> packet = (ArrayList<Object>) clientMain.readObject();
			//String packetArr[] = packet.split("`");
			String packetType = (String) packet.get(0);
			String packetCode = (String) packet.get(1);
			
			if (packetType.equals(Protocol.PT_RES_VIEW)) {
				switch (packetCode) {
					case Protocol.RES_STATISTICS_DETAIL_Y: {
						HashMap<String, Integer> hsMap = (HashMap<String, Integer>)packet.get(2);
						final String[] region = RegionList.Do;
						for(int i = 0; i < region.length; i++){
							if(hsMap.get(region[i]) != 0){
								pData = new PieChart.Data(region[i], hsMap.get(region[i]));
								pieChart.getData().add(pData);
							}
						}
						pieChartCaption(pieChart);
						return;
					}
					case Protocol.RES_STATISTICS_DETAIL_N: {
						ShowAlert.showAlert("WARNING", "??????", "???????????? ?????? ????????? ?????????????????????.");
						return;
					}
				}
			}
		}
//		DetailDAO detailDAO = new DetailDAO();
//		HashMap<String, Integer> hsMap = detailDAO.regionStatistic(destinationCode);
//		final String[] region = RegionList.Do;
//		for(int i = 0; i < region.length; i++){
//			if(hsMap.get(region[i]) != 0){
//				pData = new PieChart.Data(region[i], hsMap.get(region[i]));
//				pieChart.getData().add(pData);
//			}
//		}
//		pieChartCaption(pieChart);
	}
	//	?????? ?????????
	private void pieChartCaption(PieChart pieChart){
		final Label caption = new Label("");
		caption.setTextFill(Color.DARKORANGE);
		caption.setStyle("-fx-font: 24 arial;");

		tooltip = new Tooltip("");

		tooltip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");


		for (final PieChart.Data data : pieChart.getData()) {
			Tooltip.install(data.getNode(),tooltip);
			applyMouseEvents(data);
		}
	}
	//	????????? ????????? ?????? ?????? ?????????
	private void applyMouseEvents(final PieChart.Data data) {

		final Node node = data.getNode();

		node.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				node.setEffect(new Glow());
				String styleString = "-fx-border-color: white; -fx-border-width: 3; -fx-border-style: dashed;";
				node.setStyle(styleString);
				tooltip.setText(String.valueOf(data.getName() + "\n" + (int)data.getPieValue()) );
			}
		});

		node.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				node.setEffect(null);
				node.setStyle("");
			}
		});

		node.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {
				pData = data;
				System.out.println("Selected data " + pData.toString());
			}
		});
	}
	//	????????? ??????
	@FXML
	public void registerImg (ActionEvent event){
		// ?????? ?????? ???
		FileChooser fc = new FileChooser();
		fc.setTitle("????????? ??????");
		fc.setInitialDirectory(new File("C:")); // default ???????????? ??????
		// ????????? ??????
		ExtensionFilter imgType = new ExtensionFilter("image file", "*.jpg", "*.gif", "*.png");
		fc.getExtensionFilters().add(imgType);

		File selectedFile =  fc.showOpenDialog(null); // showOpenDialog??? ?????? ???????????? ?????? ????????? ???????????? ????????? ?????? ????????? ????????? ???????????? ????????????.
		//System.out.println(selectedFile);               // ????????? ????????? ????????????.
		if (selectedFile == null) return;
		try {
			//	image to byteArray
			BufferedImage originalImage = ImageIO.read(selectedFile);

			//	????????? ????????? ?????????
			String val = (selectedFile).toString();
			int pos = val.lastIndexOf(".");
			String ext = val.substring(pos + 1, val.length());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if ("jpg".equals(ext.toLowerCase())){
				ImageIO.write(originalImage, "jpg", baos);
			}
			if ("png".equals(ext.toLowerCase())){
				ImageIO.write(originalImage, "png", baos);
			}
			if ("gif".equals(ext.toLowerCase())){
				ImageIO.write(originalImage, "gif", baos);
			}
			baos.flush();
			//	????????? ????????? ????????? ??????
			imageInByte = baos.toByteArray();
			//System.out.println(Arrays.toString(imageInByte));

			baos.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}

	//	?????? ?????? ??????
	@FXML
	public void registerReview (ActionEvent event) {
		if(cb_star.getValue() == null){
			ShowAlert.showAlert("WARNING", "??????", "?????? ?????????");
			return;
		}
		else if(ta_reviewContent.getText().equals("")){
			ShowAlert.showAlert("WARNING", "??????", "?????? ?????????");
			return;
		}

		String content = ta_reviewContent.getText();
		int scope = cb_star.getValue();
		Timestamp reportingDate = Timestamp.valueOf(LocalDateTime.now());
		ReviewDTO reviewDTO = new ReviewDTO(userId, content, scope, destinationCode, destinationName, null, reportingDate, imageInByte);
		
//		clientMain.writePacket(Protocol.PT_REQ_RENEWAL + "`" + Protocol.REQ_CREATE_REVIEW);
//		clientMain.writeObject(reviewDTO);

		ArrayList<Object> objectList = new ArrayList<Object>();
		objectList.add(Protocol.PT_REQ_RENEWAL);
		objectList.add(Protocol.REQ_CREATE_REVIEW);
		objectList.add(reviewDTO);
		clientMain.writeObject(objectList);
		objectList.clear();
		
		while (true) {
			ArrayList<Object> packet =(ArrayList<Object>) clientMain.readObject();
			//System.out.println(packet);
			//String packetArr[] = packet.split("`");
			String packetType = (String) packet.get(0);
			String packetCode = (String) packet.get(1);
			
			if (packetType.equals(Protocol.PT_RES_RENEWAL)) {
				switch (packetCode) {
					case Protocol.RES_CREATE_REVIEW_Y: {
						ShowAlert.showAlert("WARNING", "??????", "?????? ????????? ?????????????????????.");
						tv_review.getItems().clear();
						setForestDetail(forestCode, userId, destinationCode, destinationName);
						return;
					}
					case Protocol.RES_CREATE_REVIEW_N: {
						ShowAlert.showAlert("WARNING", "??????", "?????? ????????? ?????????????????????.");
						return;
					}
				}
			}
		}
		
//		DetailDAO detailDAO = new DetailDAO();
//		detailDAO.insertReview(reviewDTO);
//		ShowAlert.showAlert("INFORMATION", "?????????", "?????? ?????? ??????");
//		setDestinationCode(destinationCode);
	}
	//	?????? ????????? ?????? ????????? ?????? ????????????
	@FXML
	public void doubleClickMouse(MouseEvent event){
		if(tv_review.getSelectionModel().getSelectedItem()!=null){
			if(event.getClickCount() > 1){
				try{
					FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/review_detail.fxml"));
					Parent root = (Parent)loader.load();
					Stage stage = new Stage();
					stage.setScene(new Scene(root));
					ReviewDTO reviewDTO = tv_review.getSelectionModel().getSelectedItem();
					ReviewDetailController reviewDetailController = loader.<ReviewDetailController>getController();
					reviewDetailController.setReviewDTO(reviewDTO);
					stage.showAndWait();
				}catch(Exception e) {
					System.out.println(e);
				}
			}
		}

	}

}
