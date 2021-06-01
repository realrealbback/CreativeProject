package Network;

import DAO.*;
import DBcontrol.DBconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;

import DTO.*;


public class Server extends Thread{
    Socket socket;
    private static int curUser = 0;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;
    ObjectInputStream objectInputStream = null;
    ObjectOutputStream objectOutputStream = null;

    public Server(Socket socket) throws ClassNotFoundException, SQLException {
        this.socket = socket;
        DBconnection.getConnection();
    }

    @Override
    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

//            writePacket(Protocol.PT_REQ_LOGIN_INFO);
            boolean program_stop = false;
            String id =null;

            while(true) {
                String packet = bufferedReader.readLine();
                String packetArr[] = packet.split("`");
                String packetType = packetArr[0]; // 프로토콜 타입 구분

                switch (packetType) {
                    case Protocol.PT_EXIT:{
                        writePacket(Protocol.PT_EXIT);
                        program_stop = true;
                        break;
                    }
                    case Protocol.PT_REQ_LOGIN:{
                    	String loginId = packetArr[1];
                    	String loginPassword = packetArr[2];
                    	
                    	try {
                    		UserDAO userDao = new UserDAO();
                    		boolean isCorrectUser = userDao.checkUser(loginId,  loginPassword);
                    		if (isCorrectUser) {
								writePacket(Protocol.PT_RES_LOGIN + "`" + Protocol.RES_LOGIN_Y);
								id = loginId;
                    		}
                    		else
                    			writePacket(Protocol.PT_RES_LOGIN + "`" + Protocol.RES_LOGIN_N);
                    	} catch (Exception e) {
                    		e.printStackTrace();
                    	}
                    	break;
                    }
                    case Protocol.PT_REQ_VIEW: {	//조회 요청
                    	String packetCode = packetArr[1];
                    	switch (packetCode) {
                    		case Protocol.REQ_DESTINATION_REGION:{	//지역으로 전체 검색
								InquireByRegionDAO inquireByRegionDAO = new InquireByRegionDAO();
								ArrayList<DestinationDTO> destinationDTOS = inquireByRegionDAO.inquireDestinationByRegion(packetArr[2],packetArr[3],packetArr[4],);


								if(destinationDTOS != null){
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_DESTINATION_REGION_Y);
									writeObject(destinationDTOS);
								}
								else{
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_DESTINATION_REGION_N);
								}
                    			break;
                    		}
                    		case Protocol.REQ_TOURIST_DETAIL:{
								DetailDAO detailDAO = new DetailDAO();
								DestinationDTO destinationDTO = (DestinationDTO) objectInputStream.readObject();

								TouristSpotDTO touristSpotDTO = detailDAO.detailTouristSpot(destinationDTO.getTouristSpot_code());
								ArrayList<ReviewDTO> arrayList = detailDAO.inquireReview(destinationDTO.getCode());

								if (touristSpotDTO != null) {
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_TOURIST_DETAIL_Y);
									writeObject(touristSpotDTO);
									writeObject(arrayList);
								}
								else
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_TOURIST_DETAIL_N);
                    			break;
                    		}
                    		case Protocol.REQ_FOREST_DETAIL:{
								DetailDAO detailDAO = new DetailDAO();
								DestinationDTO destinationDTO = (DestinationDTO) objectInputStream.readObject();

								ForestLodgeDTO forestLodgeDTO = detailDAO.detailForestLodge(destinationDTO.getForestLodge_code());
								ArrayList<ReviewDTO> arrayList = detailDAO.inquireReview(destinationDTO.getCode());

								if (forestLodgeDTO!=null) {
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_FOREST_DETAIL_Y);
									writeObject(forestLodgeDTO);
									writeObject(arrayList);
								}
								else
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_FOREST_DETAIL_N);
                    			break;
                    		}
                    		case Protocol.REQ_BEACH_DETAIL:{
								DetailDAO detailDAO = new DetailDAO();
								DestinationDTO destinationDTO = (DestinationDTO) objectInputStream.readObject();

								ForestLodgeDTO beachDTO = detailDAO.detailForestLodge(destinationDTO.getBeach_code());
								ArrayList<ReviewDTO> arrayList = detailDAO.inquireReview(destinationDTO.getCode());

								if (beachDTO!=null) {
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_BEACH_DETAIL_Y);
									writeObject(beachDTO);
									writeObject(arrayList);
								}
								else
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_BEACH_DETAIL_N);
                    			break;
                    		}
                    		case Protocol.REQ_TOILET:{
								InquireToiletParkingDAO inquireToiletParkingDAO = new InquireToiletParkingDAO();
                    			ArrayList<ToiletDTO> toiletDTOS = inquireToiletParkingDAO.inquireToiletByLocation(packetArr[2],packetArr[3],packetArr[4]);

								if(toiletDTOS!=null) {
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_TOILET_Y);
									writeObject(toiletDTOS);
								}
								else{
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_TOILET_N);
								}
                    			break;
                    		}
                    		case Protocol.REQ_PARKING:{
                    			InquireToiletParkingDAO inquireToiletParkingDAO = new InquireToiletParkingDAO();
                    			ArrayList<ParkingLotsDTO> parkingLotsDTOS = inquireToiletParkingDAO.inquireParkingLotByLocation(packetArr[1],packetArr[2],packetArr[3]);

								if(parkingLotsDTOS!=null) {
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_PARKING_Y);
									writeObject(parkingLotsDTOS);
								}
								else{
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_PARKING_N);
								}
                    			break;
                    		}
                    		case Protocol.REQ_DESTINATION_LOCATION:{
                    			InquireByLocationDAO inquireByLocationDAO = new InquireByLocationDAO();
                    			ArrayList<DestinationDTO>

                    			break;
                    		}
                    		case Protocol.REQ_STATISTICS:{
                    			StatisticsDAO statisticsDAO = new StatisticsDAO();

                    			break;
                    		}
                    		case Protocol.REQ_MYPAGE:{
                    			MyPageDAO myPageDAO = new MyPageDAO();
                    			UserDTO userDTO = myPageDAO.roadUser(id);
								ArrayList<ReviewDTO> arrayList = myPageDAO.inquireMyReview(id);
								ArrayList<FavoriteDTO> arrayList1 = myPageDAO.inquiryFavorite(id);

								if(userDTO != null){
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_MYPAGE_Y);
									writeObject(userDTO);
									writeObject(arrayList);
									writeObject(arrayList1);
								}
								else{
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_MYPAGE_N);
								}

                    			break;
                    		}
							case Protocol.REQ_ID_DUPLICATION:{
								UserDAO userDAO = new UserDAO();
								boolean check = userDAO.duplicationId(packetArr[2]);

								if(!check){
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_ID_DUPLICATION_Y);
								}
								else{
									bufferedWriter.write(Protocol.PT_RES_VIEW + "`" + Protocol.RES_ID_DUPLICATION_N);
								}
								break;
							}
                    	}
                    	break;
                    }
                    case Protocol.PT_REQ_RENEWAL:{
                    	String packetCode = packetArr[1];
                    	switch (packetCode) {
                    		case Protocol.REQ_SIGNUP:{
                    			UserDAO userDAO = new UserDAO();
                    			UserDTO userDTO = (UserDTO)objectInputStream.readObject();
                    			
                    			boolean isInsertUser = userDAO.insertUser(userDTO);
                    			if (isInsertUser) 
                    				bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_SIGNUP_Y);
                    			else
                    				bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_SIGNUP_N);
                    			break;
                    		}
                    		case Protocol.REQ_CREATE_REVIEW:{
                    			DetailDAO detailDAO = new DetailDAO();
                    			ReviewDTO reviewDTO = (ReviewDTO)objectInputStream.readObject();

                    			boolean isInsertReview = detailDAO.insertReview(reviewDTO);
                    			if(isInsertReview){
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_CREATE_REVIEW_Y);
								}
                    			else{
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_CREATE_REVIEW_N);
								}
                    			break;
                    		}
                    		case Protocol.REQ_DELETE_REVIEW:{
                    			MyPageDAO myPageDAO = new MyPageDAO();
                    			boolean isDeleteReview = myPageDAO.deleteReview( Integer.parseInt(packetArr[2]));

                    			if(isDeleteReview){
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_CREATE_REVIEW_Y);
								}
                    			else{
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_CREATE_REVIEW_N);
								}

                    			break;
                    		}
                    		case Protocol.REQ_UPDATE_USER:{
                    			MyPageDAO myPageDAO = new MyPageDAO();
                    			UserDTO userDTO = (UserDTO)objectInputStream.readObject();

                    			boolean check = myPageDAO.reservationUser(userDTO);
								if(check){
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_CREATE_REVIEW_Y);
								}
								else{
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_CREATE_REVIEW_N);
								}

                    			break;
                    		}
                    		case Protocol.REQ_CREATE_FAVORITES:{
                    			FavoriteDAO favoriteDAO = new FavoriteDAO();
								boolean check = favoriteDAO.addFavorite(packetArr[2], packetArr[3]);

								if(check){
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_CREATE_FAVORITES_Y);
								}
								else{
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_CREATE_FAVORITES_N);
								}

                    			break;
                    		}
                    		case Protocol.REQ_DELETE_FAVORITES:{
                    			MyPageDAO myPageDAO = new MyPageDAO();
                    			boolean check = myPageDAO.deleteFavorite(Integer.parseInt(packetArr[2]));

                    			if(check){
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_DELETE_FAVORITES_Y);
								}
                    			else{
									bufferedWriter.write(Protocol.PT_RES_RENEWAL + "`" + Protocol.RES_DELETE_FAVORITES_N);
								}

                    			break;
                    		}
                    	}
                    	break;
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writePacket(String packet) {
        try{
            bufferedWriter.write(packet + "\n");
            bufferedWriter.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

	public void writeObject(Object obj) {
		try {
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
