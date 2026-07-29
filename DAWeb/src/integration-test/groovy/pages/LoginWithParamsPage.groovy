package pages;
import geb.Page

/**
 * 
 * @author jens Peters
 * Tests available Login Page
 *
 */
public class LoginWithParamsPage extends Page {
	
	static url = "/daweb3/login/auth"
	static at = { title ==~ /Anmeldung|Login/ }

	static content = {
		loginForm { $("form")}
		loginButton { $("input", id: "submit") }
	}
	
	void login(String usr, String pwd) {
		j_username = usr
		j_password = pwd
		loginButton.click()
	}

}
