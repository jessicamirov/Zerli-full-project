package client.controller;
import client.model.Model;
import common.interfaces.UserManager.PermissionDenied;
import common.interfaces.UserManager.TooManyAttempts;
import common.request_data.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * represent the sign in option
 *
 * check the sign in details and move to sign up screen if there is no sign in
 * user
 * 
 * @author Jessica, Yarden
 */


public class SigninController extends BaseController{
	@FXML
	Text message;
	@FXML
	TextField email;
	@FXML
	PasswordField password;
	Model modell;
	Ilogin iLOGIN;
	private static String CORRECT_MESSAGE = "Email & password:";
	private static String INVALID_PASSWORD = "Invalid inputs, or User is Loged-in";
	private static String NOT_APPROVED = "Your Account had been blocked \r\n Or yet approved by manager";
	public static User user;
	
	
	public SigninController() {
		iLOGIN=new Bypass();
		
	}
	//constractor injection
	public SigninController(Ilogin ilogin,Model modell) {
		iLOGIN=ilogin;
		this.model=modell;
	}
	@Override
	public void update() {
		email.setText("");
		password.setText("");
		message.setText(CORRECT_MESSAGE);
	}

	/**
	 * Sign in all users
	 *
	 *
	 * @author Katya, Aharon
	 */
	
	
	public void onSignin(ActionEvent event) throws Exception {
		String checkUsername = iLOGIN.getTextUser();
		String checkPassword = iLOGIN.getTextPassword();
		try {

			if (!model.getUserManager().setCurrentUser(checkUsername, checkPassword)) {
				message.setText(NOT_APPROVED);
				return;
			}
		} catch (PermissionDenied  | TooManyAttempts d ) {
			message.setText(INVALID_PASSWORD);
			return;
		}
		User currentUser = model.getUserManager().getCurrentUser();
		//model.setUsername(currentUser.username);
	    iLOGIN.setUsername(currentUser.username);
		model.getProductManager().setCurrentUser(currentUser);
		model.getOrderManager().setCurrentUser(currentUser);
		model.getCartManager().setCurrentUser(currentUser);
		this.model.setSceneBack();
	}
	public String getMessage()
	{
		return message.getText();
	}
	
	//************************************ Dependency injection *************************************
class Bypass implements Ilogin{
	@Override
	public String getTextUser()
	{
		return email.getText();
	}
	@Override
	public String getTextPassword()
	{
		return password.getText();
	}
	@Override
	public void setUsername(String s) {
		model.setUsername(s);
	}
}

}
