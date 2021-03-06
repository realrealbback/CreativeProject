package FxController;

import DAO.UserDAO;
import DataSetControl.RecentInquiryData;
import Network.Protocol;
import Network.clientMain;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
	
	@FXML // 아이디
	public TextField tf_id;
	
	@FXML // 비밀번호
	public PasswordField pf_password;
	
	@FXML // 로그인 버튼
	public Button btn_login;
	
	@FXML // 회원가입 버튼
	public Button btn_signup;
	
	@FXML // 로그인 버튼 클릭
	public void login(ActionEvent event) {
		String id = tf_id.getText();
		String pw = pf_password.getText();
		
		//  clientMain.writePacket(Protocol.PT_REQ_LOGIN + "`" + id + "`" + pw);
		ArrayList<Object> objectList = new ArrayList<Object>();
		objectList.add(Protocol.PT_REQ_LOGIN);
		objectList.add(id);
		objectList.add(pw);
		clientMain.writeObject(objectList);
		objectList.clear();

		  while (true) {
		  	ArrayList<Object> packet =(ArrayList<Object>) clientMain.readObject();
		  	//String packetArr[] = packet.split("`");
		  	String packetType =(String) packet.get(0);
		  	System.out.println(packetType+")"+(String)packet.get(1));
		  	switch (packetType) {
		  		case Protocol.PT_RES_LOGIN: {
		  			String loginResult =(String)packet.get(1);
		  			switch (loginResult) {
		  				case Protocol.RES_LOGIN_Y: {
		  					try {
		  						FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/MainDisplay.fxml"));
		  						Parent root = (Parent)loader.load();

		  						//	로그인한 유저 정보 mainDisplay로 넘기기
		  						MainDisplayController mainDisplayController = loader.<MainDisplayController>getController();
		  						mainDisplayController.setSaveUserId(id);

		  						Stage primaryStage = (Stage) btn_login.getScene().getWindow();
		  						primaryStage.setScene(new Scene(root));
		  						primaryStage.centerOnScreen();
		  						primaryStage.show();
		  						return;
		  					} catch (Exception e) {
		  						e.printStackTrace();
		  					}
		  				}
		  				case Protocol.RES_LOGIN_N: {
		  					ShowAlert.showAlert("WARNING", "로그인 알림창", "로그인 실패 : 아이디 또는 비밀번호가 일치하지 않습니다.");
		  					return;
		  				}
		  			}
		  		}
		  	}
		  }
		
		//	id, pw 입력 정보 체크
//		UserDAO userDAO = new UserDAO();
//		boolean checkUser = userDAO.checkUser(id, pw);
//		if(!checkUser){
//			ShowAlert.showAlert("WARNING", "로그인 알림창", "로그인 실패 : 아이디 또는 비밀번호가 일치하지 않습니다.");
//		}
//		else {
//			ShowAlert.showAlert("INFORMATION", "로그인 알림창", "로그인 성공");
//			try {
//				FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/MainDisplay.fxml"));
//				Parent root = (Parent)loader.load();
//
//				//	로그인한 유저 정보 mainDisplay로 넘기기
//				MainDisplayController mainDisplayController = loader.<MainDisplayController>getController();
//				mainDisplayController.setSaveUserId(id);
//
//				Stage primaryStage = (Stage) btn_login.getScene().getWindow();
//				primaryStage.setScene(new Scene(root));
//				primaryStage.centerOnScreen(); //중앙으로 창 띄우기
//				primaryStage.show();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	@FXML // 회원가입 버튼 클릭
	public void signUp(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("../FXML/sign_up.fxml"));
			Scene scene = new Scene(root);
			Stage primaryStage = (Stage) btn_signup.getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override // 로그인 화면 출력
	public void start(Stage primaryStage) {
		try {
			System.setProperty("prism.lcdtext", "false"); //폰트 깨짐 방지
			Font.loadFont(getClass().getResourceAsStream("/FXML/CSS/resource/SsurroundAir.ttf"), 10); //폰트 설정
			Font.loadFont(getClass().getResourceAsStream("/FXML/CSS/resource/Cafe24.ttf"), 10); //폰트 설정
			Parent root = FXMLLoader.load(Main.class.getResource("../FXML/login.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//	최근조회 생성자 초기화
		RecentInquiryData recentInquiryData = new RecentInquiryData();
		launch(args);
	}
}
