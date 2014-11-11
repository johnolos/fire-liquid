import org.json.JSONObject;
import org.json.JSONException;
import com.icy_sun.config.AppConf;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FacebookSignInServlet extends HttpServlet {

	// TODO: Create state to prevent forgery around session
	// Add permissions needed in Facebook application.
	// Add proper links and web hook

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String code = req.getParameter("code");
		if (code == null || code.equals("")) {
			// an error occurred, handle this
		}

		String token = null;
		try {
			String g = "https://graph.facebook.com/oauth/access_token?client_id="
					+ AppConf.FACEBOOK_APP_ID
					+ "&redirect_uri="
					+ URLEncoder
							.encode("http://icy-sun.appspot.com/sign/facebook/",
									"UTF-8")
					+ "&client_secret="
					+ AppConf.FACEBOOK_SECRET + "&code=" + code;
			URL u = new URL(g);
			System.out.println(u);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			token = b.toString();
			if (token.startsWith("{"))
				throw new Exception("error on requesting token: " + token
						+ " with code: " + code);
		} catch (Exception e) {
			// an error occurred, handle this
			System.out.println("Lol");
		}
		System.out.println("Token: " + token);

		String graph = null;
		try {
			String g = "https://graph.facebook.com/me?" + token;
			URL u = new URL(g);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			graph = b.toString();
		} catch (Exception e) {
			// an error occurred, handle this
			System.out.println("LOL2");
		}

		String facebookId;
		String firstName;
		String middleNames;
		String lastName;
		String email;
		Gender gender;
		try {
			JSONObject json = new JSONObject(graph);
			facebookId = json.getString("id");
			firstName = json.getString("first_name");
			if (json.has("middle_name"))
				middleNames = json.getString("middle_name");
			else
				middleNames = null;
			if (middleNames != null && middleNames.equals(""))
				middleNames = null;
			lastName = json.getString("last_name");
			email = json.getString("email");
			if (json.has("gender")) {
				String g = json.getString("gender");
				if (g.equalsIgnoreCase("female"))
					gender = Gender.FEMALE;
				else if (g.equalsIgnoreCase("male"))
					gender = Gender.MALE;
				else
					gender = Gender.OTHER;
			} else {
				gender = Gender.OTHER;
			}
		} catch (JSONException e) {
			// an error occurred, handle this
		}
	}
}