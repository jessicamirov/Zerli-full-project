package server.modelTest;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.request_data.Shop;
import common.request_data.User;
import server.model.EchoServer;
import server.model.ServerUserManager;

class ServerUserManagerTest {
	ServerUserManager serverusermanager;
	@BeforeEach
	void setUp() throws Exception {
		serverusermanager = new ServerUserManager(new User() );
	}

	
	@Test
	void WrongShopTest() {
	boolean flag= serverusermanager.addNewIncomeReport(Shop.NONE,"", "", "", "", "");
		String expected = "Error in the input - 'Shop'";
		assertEquals(serverusermanager.statusIncomeReport,expected);
		assertFalse(flag);
	}

	@Test
	void WrongYearTest() {
	boolean flag= serverusermanager.addNewIncomeReport(Shop.HAIFA,"2025", "", "", "", "");
		String expected = "The input 'Year' is bigger than current year!";
	
		assertFalse(flag);
	}
	
	@Test
	void WrongMonthTest() {
	boolean flag= serverusermanager.addNewIncomeReport(Shop.HAIFA,"2020", "23", "", "", "");
		String expected = "The input 'Month' is wrong!";
		assertFalse(flag);
		
		
	}
	@Test
	void WrongIncomeTest() {
	boolean flag= serverusermanager.addNewIncomeReport(Shop.HAIFA,"2020", "10", "-21", "", "");
		String expected =  "The input 'Income' is negative!";
		assertFalse(flag);
	}
	@Test
	void WrongTNOTest() {
	boolean flag= serverusermanager.addNewIncomeReport(Shop.HAIFA,"2020", "10", "-21", "-1", "");
		String expected = "The input 'TNO' is negetive";
		assertFalse(flag);
	}
	
}
