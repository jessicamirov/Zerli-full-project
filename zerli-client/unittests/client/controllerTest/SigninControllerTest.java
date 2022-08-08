package client.controllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import client.controller.Ilogin;
import client.controller.SigninController;
import client.model.ClientCartManager;
import client.model.ClientOrderManager;
import client.model.ClientProductManager;
import client.model.ClientUserManager;
import client.model.EchoClient;
import client.model.Model;

import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.TooManyAttempts;
import common.request_data.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


@RunWith(MockitoJUnitRunner.class)

class SigninControllerTest {

	
	@Mock
	private Model MockServiceModel;
	private ClientUserManager MockServiceClientUserManager;
	private ClientProductManager MockServiceClientProductManager;
	private ClientOrderManager MockServiceClientOrderManager;
	private ClientCartManager MockServiceClientCartManager;
	
	User mApproved ;
	@FXML
	Text message;
	@FXML
	TextField email;
	@FXML
	PasswordField password;
	Ilogin log;
	SigninController login;
	
	String user;
	String passwordd;
	boolean flag = false;
	ClientUserManager usermanager;
	ClientProductManager productmanager;
	ClientOrderManager ordermanager;
	ClientCartManager usermanager4;
	class stubLogin implements Ilogin{
	
		@Override
		public void setUsername(String s) {
			MockServiceModel.setUsername(s);
		}
		@Override
		public String getTextUser() {
			return user;
		}

		@Override
		public String getTextPassword() {
			return passwordd;
		}
	}
	
	@BeforeEach
	void setUp() throws Exception {
		//Init mocks
		MockServiceModel = Mockito.mock(Model.class);
		MockServiceClientUserManager= Mockito.mock(ClientUserManager.class);
		MockServiceClientProductManager=Mockito.mock(ClientProductManager.class);
		MockServiceClientOrderManager=Mockito.mock(ClientOrderManager.class);
		MockServiceClientCartManager=Mockito.mock(ClientCartManager.class);
		
		//constructor injection
		log = new stubLogin();
		login = new SigninController(log,MockServiceModel);
	
		message=new Text();
		
		//We will initialize for the first time the 'message' so that there will be no null.
		Field field = login.getClass().getDeclaredField("message");//get field
		field.setAccessible(true);
		message.setText("");
		field.set(login,message); //set field
		
		user="m";
		passwordd="m";
	}

	/* functionality:This function checks the case that Number of attempts is bigger than MAX_ATTEMPS
	   (In this case, the function 'setCurrentUser()'(in ClientUserManager)
	    throw 'TooManyAttempts()' and 'onSignin'(in SigninController) catch her.)
	 * input:ClientUserManager with attemptCount > MAX_ATTEMPS
	 * expected results:Massage of "Invalid inputs, or User is Loged-in"  */
	@Test
	void attemptCountBigTest() throws Exception {
		String expected="Invalid inputs, or User is Loged-in";
		//attemptCount > MAX_ATTEMPS
		MockServiceClientUserManager.attemptCount=100;
		
		usermanager=new ClientUserManager(new EchoClient("localhost",5555),MockServiceClientUserManager);
		when(MockServiceModel.getUserManager()).thenReturn(usermanager);
		
		login.onSignin(new ActionEvent());
		assertEquals(expected,login.getMessage());
	}
	
	/* functionality:This function checks the case that we want to set a current user but the user is null
	   (In this case, the function 'setCurrentUser()'(in ClientUserManager)
	    throw 'PermissionDenied()' and 'onSignin'(in SigninController) catch her.)
	 * input:User user = null;
	 * expected results:Massage of "Invalid inputs, or User is Loged-in" 
	   and verify that 'getUser()' called at least one */
	@Test
	void UserIsNullTest() throws Exception {
		String expected="Invalid inputs, or User is Loged-in";
		
		when(MockServiceClientUserManager.getUser(user, passwordd)).thenReturn(null);
		usermanager = new ClientUserManager(new EchoClient("localhost",5555),MockServiceClientUserManager);
		usermanager.attemptCount=0;
		when(MockServiceModel.getUserManager()).thenReturn(usermanager);
		login.onSignin(new ActionEvent());
		assertEquals(expected,login.getMessage());
		verify(MockServiceClientUserManager,atLeastOnce()).getUser(user,passwordd);
	}
	
	/* functionality:This function checks the case that we want to configure a current user 
	   that has not yet been approved
	   (In this case, the function 'setCurrentUser()'(in ClientUserManager)
	   returns false to 'onSignin'(in SigninController) ).
	 * input:User that no approved;
	 * expected results:Massage of "Your Account had been blocked \r\n Or yet approved by manager" 
	   and verify that 'getUser()' called at least one*/
	@Test
	void UserNotApproveTest() throws Exception {
		String expected="Your Account had been blocked \r\n Or yet approved by manager";
		User approve = new User(user,passwordd);
		approve.setApproved(false); // users not approved
		when(MockServiceClientUserManager.getUser(user, passwordd)).thenReturn(approve);
		usermanager = new ClientUserManager(new EchoClient("localhost",5555),MockServiceClientUserManager);
		usermanager.attemptCount=0;
		when(MockServiceModel.getUserManager()).thenReturn(usermanager);
		login.onSignin(new ActionEvent());
		assertEquals(expected,login.getMessage());
		verify(MockServiceClientUserManager,atLeastOnce()).getUser(user,passwordd);
	}
	
	/* functionality:This function checks the case that a user who is already connected,
	   , tries to connect again.
	   (In this case, the function 'setCurrentUser()'(in ClientUserManager)
	   throw 'PermissionDenied()' and 'onSignin'(in SigninController) catch her).
	 * input:User that already connected. in our case is mock that return false
	    And causes an Exception injection of 'PermissionDenied()' to'onSignin'(in SigninController).
	 * expected results:Massage of "Invalid inputs, or User is Loged-in" 
	   verify that 'logInfo()'and 'getUser()' called at least one */ 
	@Test
	void IfAlreadyLoginTest() throws Exception {
		String expected="Invalid inputs, or User is Loged-in";
		User approve = new User(user,passwordd);
		approve.setApproved(true);
		approve.logInfo=true;//User already connected
		//We depend on logInUser() and getUser() 
		when(MockServiceClientUserManager.getUser(user, passwordd)).thenReturn(approve);
		when(MockServiceClientUserManager.logInUser(approve)).thenReturn(false);
		
		usermanager = new ClientUserManager(new EchoClient("localhost",5555),MockServiceClientUserManager);
		usermanager.attemptCount=0;
		when(MockServiceModel.getUserManager()).thenReturn(usermanager);
		login.onSignin(new ActionEvent());
		assertEquals(expected,login.getMessage());
		verify(MockServiceClientUserManager,atLeastOnce()).getUser(user,passwordd);
		verify(MockServiceClientUserManager,atLeastOnce()).logInUser(any());
	}
	
	/* functionality:This function checks the case that a user who is already connected,
	   , tries to connect again.
	   (In this case, the function 'setCurrentUser()'(in ClientUserManager)
	   throw 'PermissionDenied()' and 'onSignin'(in SigninController) catch her).
	 * input:User that already connected. in our case is mock that return false
	    And causes an Exception injection of 'PermissionDenied()' to'onSignin'(in SigninController).
	 * expected results:Massage of "Invalid inputs, or User is Loged-in" */
	@Test
	void UserLoginSuccessedTest() throws Exception {
		User approve = new User(user,passwordd);
		approve.setApproved(true);
		approve.logInfo=false; //User not connected
		
		when(MockServiceClientUserManager.getUser(user, passwordd)).thenReturn(approve);
		when(MockServiceClientUserManager.logInUser(approve)).thenReturn(true);
		when(MockServiceClientUserManager.getCurrentUser()).thenReturn(approve);
		
		usermanager = new ClientUserManager(new EchoClient("localhost",5555),MockServiceClientUserManager);
		usermanager.currentUser =approve;
		usermanager.attemptCount=0;
		
		productmanager = new ClientProductManager(new EchoClient("localhost",5555),MockServiceClientProductManager);
		productmanager.currentUser =approve;

		ordermanager = new ClientOrderManager(new EchoClient("localhost",5555),MockServiceClientOrderManager);
		ordermanager.setCurrentUser(approve);
		
		
		usermanager4= new ClientCartManager(new EchoClient("localhost",5555), MockServiceClientCartManager);
		usermanager4.setCurrentUser(approve);
		
		
		
		when(MockServiceModel.getUserManager()).thenReturn(usermanager);
		when(MockServiceModel.getProductManager()).thenReturn(productmanager);
		when(MockServiceModel.getOrderManager()).thenReturn(ordermanager);
		when(MockServiceModel.getCartManager()).thenReturn(usermanager4);
		
		login.onSignin(new ActionEvent());
		assertTrue(usermanager.currentUser.equals(approve));
		assertTrue(productmanager.currentUser.equals(approve));
		assertTrue(ordermanager.currentUser.equals(approve));
		assertTrue(usermanager4.currentUser.equals(approve));
	}
	//@Test
	//void Account throws Exception {
		
		
		
		
		
		
	}

