package com.vnetcon.jdbc.restservlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.gson.Gson;
/*
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
*/
import com.vnetcon.jdbc.rest.RestConnection;

public class RestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String inArrayParam = "inarray";
	private static final String inOnerowParam = "inonerow";
	private static final String dbConf = "/opt/vnetcon/conf/database.properties";
	private static final String emailConf = "/opt/vnetcon/con/email.properties";
	private static final String tokenHeader = "vnetcon-token";
	private static final String configSchema = "VNETCON";
	private static final String configSelect = "SELECT\r\n" + 
			" \"JSON_SQL\" \r\n" + 
			"FROM\r\n" + 
			"\"" + configSchema + "\".\"REST_SERVLET_CONFIG\" \r\n" + 
			"WHERE\r\n" + 
			" \"REST_ENDPOINT\" = ?\r\n" + 
			" AND \"VERSION\" = ?\r\n" + 
			" AND (\"ALLOWED_TOKENS\" = 'ALL' OR \"ALLOWED_TOKENS\" LIKE ?)\r\n" + 
			" AND \"ENABLED\" = 1";


	private static final String timestampfunc = "timestampfunc";
	private static final String logSql = "INSERT INTO \"" + configSchema + "\".\"REST_SERVLET_LOG\" \r\n" +
	" (\"LOGTIME\", \"SQL\", \"REQUEST_PARAMS\", \"RESPONSE_JSON\") VALUES ('{" + timestampfunc + "}',?,?,?)";
    private static final String UPLOAD_DIRECTORY = "upload";
    
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB	

    
    private Gson gson = new Gson();
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private Properties loadEmailProperties() throws SQLException {
		Properties dbProps = new Properties();
		try {
			FileInputStream fIn = new FileInputStream(emailConf);
			dbProps.load(fIn);
			fIn.close();
		}catch(Exception e) {
			throw new SQLException(e);
		}
		return dbProps;
	}
	
	private Properties loadProperties() throws SQLException {
		Properties dbProps = new Properties();
		try {
			FileInputStream fIn = new FileInputStream(dbConf);
			dbProps.load(fIn);
			fIn.close();
		}catch(Exception e) {
			throw new SQLException(e);
		}
		return dbProps;
	}
	
	private String getJsonSql(Connection con, String endpoint, String version, String accesstoken) throws Exception {
		String sql = null;
		PreparedStatement stmt = con.prepareStatement(configSelect);
		stmt.setString(1, endpoint);
		stmt.setString(2, version);
		stmt.setString(3, "'%\"" + accesstoken + "\"%'");

		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			sql = rs.getString(1);
		}

		return sql;
		
	}
	
	private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            PrintWriter writer = response.getWriter();
            writer.println("Error: Form must has enctype=multipart/form-data.");
            writer.flush();
            return;
        }
 
        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // constructs the directory path to store upload file
        // this path is relative to application's directory
        String uploadPath = getServletContext().getRealPath("")
                + File.separator + UPLOAD_DIRECTORY;
         
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
 
        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
 
                        // saves the file on disk
                        item.write(storeFile);
                        request.setAttribute("message",
                            "Upload has been done successfully!");
                    }
                }
            }
        } catch (Exception ex) {
            request.setAttribute("message",
                    "There was an error: " + ex.getMessage());
        }
        // redirects client to message page
        getServletContext().getRequestDispatcher("/message.jsp").forward(
                request, response);	
                */
    }

	private void downloadFile(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
	}
	
	private void logRequest(String config, Properties props, Connection con, String sql, Map<String, String> params, String returnJson) throws Exception {
		String jsonParams = gson.toJson(params);
		String ulogSql = logSql.replaceAll("'\\{" + timestampfunc + "\\}'", props.getProperty(config + ".jdbc." + timestampfunc));
		PreparedStatement pstmt = con.prepareStatement(ulogSql);
		if(props.getProperty(config + ".jdbc.driver").toLowerCase().indexOf("postgresql") > -1) {
			pstmt.setString(1, sql);
			pstmt.setString(2, jsonParams);
			pstmt.setString(3, returnJson);
			pstmt.executeUpdate();
		} else {
			Clob c = con.createClob();
			c.setString(1, sql);
			pstmt.setClob(1, c);
			c = con.createClob();
			c.setString(1, jsonParams);
			pstmt.setClob(2, c);
			c = con.createClob();
			c.setString(1, returnJson);
			pstmt.setClob(3, c);
			pstmt.executeUpdate();
		}
	}
	
	private void sendEmail(Map<String, String> params) throws Exception {
		Properties props = loadEmailProperties();
		String to = params.get("email");
		String cc = params.get("cc");
		String from = props.getProperty("email.from");
		String host = props.getProperty("email.host");
		String port = props.getProperty("email.port");
		String subject = params.get("subject");
		String htmlmessage = params.get("message");
		final String username = props.getProperty("email.user");
		final String password = props.getProperty("email.pass");
		Properties mailprops = new Properties();
		mailprops.put("mail.smtp.auth", "true");
		mailprops.put("mail.smtp.starttls.enable", "true");
		mailprops.put("mail.smtp.host", host);
		mailprops.put("mail.smtp.port", port);
		Session session = Session.getInstance(mailprops,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
		});
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setContent(htmlmessage, "text/html");
		
		if(props.getProperty("email.replyto") != null) {
			message.setReplyTo(new javax.mail.Address[]
					{
					    new javax.mail.internet.InternetAddress(props.getProperty("email.replyto"))
					});		
		}
		
		Transport.send(message);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OutputStream out = resp.getOutputStream();
		PrintWriter w = new PrintWriter(out);

		Connection con = null;
		Connection logCon = null;
		Connection restCon = null;
		
		try {
			Properties p = this.loadProperties();
			resp.setContentType("application/json; charset=UTF-8");
			Class.forName("com.vnetcon.jdbc.rest.RestDriver");
			Map<String, String> params = new HashMap<String, String>();
			String uri = req.getRequestURI();
			String config = null;
			String endpoint = null;
			String version = null;
			String accesstoken = null;
			String pathParts[] = null;
			boolean writeInArray = true;
			boolean writeInOneRow = false;
						
			pathParts = uri.split("/");
			version = pathParts[pathParts.length - 1];
			endpoint = pathParts[pathParts.length - 2];
			config = pathParts[pathParts.length - 3];
			
			params.put("endpoint", endpoint);
				
			if("upload".equals(endpoint)) {
				this.uploadFile(req, resp);
				return;
			}

			if("download".equals(endpoint)) {
				this.downloadFile(req, resp);
				return;
			}
			
			
			Enumeration<String> reqParams = req.getParameterNames();
			while(reqParams.hasMoreElements()) {
				String name = reqParams.nextElement();
				String value = req.getParameter(name);
				params.put(name, value);
				if(name.equals(inArrayParam)) {
					if("false".equals(value)) {
						writeInArray = false;
					}
				}
				if(name.equals(inOnerowParam)) {
					if("true".equals(value)) {
						writeInOneRow = true;
					}
				}
			}

			Enumeration<String> headers = req.getHeaderNames();
			while(headers.hasMoreElements()) {
				String name = headers.nextElement();
				String value = req.getHeader(name);
				params.put(name, value);
				if(tokenHeader.equals(name)) {
					accesstoken = value;
				}
			}
	
			if("email".equals(endpoint)) {
				this.sendEmail(params);
				w.write("{\"status\":\"ok\"}");
				w.flush();
				out.flush();
				out.close();
				return;
			}

			
			//System.out.println("config: " + config + " user: " + p.getProperty(config + ".jdbc.user"));
			con = DriverManager.getConnection("jdbc:vnetcon:rest://" + config, p.getProperty(config + ".jdbc.user"), p.getProperty(config + ".jdbc.pass"));
			
			if(p.getProperty(config + ".jdbc.logcon") != null) {
				String logconConfig = p.getProperty(config + ".jdbc.logcon");
				logCon = DriverManager.getConnection("jdbc:vnetcon:rest://" + logconConfig, p.getProperty(config + ".jdbc.user"), p.getProperty(config + ".jdbc.pass"));
			}
			
			if(accesstoken == null) {
				accesstoken = "ALL";
			}
			
			((RestConnection)con).setQueryParams(params);
			String sql = null;
			if(p.getProperty(config + ".jdbc.restcon") != null) {
				restCon = DriverManager.getConnection("jdbc:vnetcon:rest://" + p.getProperty(config + ".jdbc.restcon"), p.getProperty(config + ".jdbc.user"), p.getProperty(config + ".jdbc.pass"));
				sql = getJsonSql(restCon, endpoint, version, accesstoken);
				restCon.close();
			} else {
				sql = getJsonSql(con, endpoint, version, accesstoken);
			}

			if(sql != null) {
				StringWriter sw = new StringWriter();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				char[] buf = new char[1024];
				int read = 0;
				String delim = "";
				
				if(writeInArray && rs != null) {
					w.write("{ \"results\": [");
					sw.write("{ \"results\": [");
					w.flush();
				}
				
				while(rs != null && rs.next()) {
					Reader c = rs.getClob(1).getCharacterStream();
					if(writeInArray) {
						w.write(delim);
						sw.write(delim);
						w.flush();
					}
					while((read = c.read(buf)) > -1) {
						if(writeInOneRow) {
							String s = new String(buf, 0, read);
							s = s.replace("\r", "");
							s = s.replace("\n", "");
							w.write(s);
							sw.write(s);
							w.flush();
						} else {
							w.write(buf, 0, read);
							sw.write(buf, 0, read);
							w.flush();
						}
					}
					delim = ",";
					w.write("\n");
					sw.write("\n");
					w.flush();
					out.flush();
				}
				if(writeInArray && rs != null) {
					w.write("]}");
					sw.write("]}");
					w.flush();
				}
				
				stmt.close();
				sw.flush();
				if(logCon != null) {
					this.logRequest(config, p, logCon, sql, params, sw.toString());
					logCon.close();
				}
				sw.close();
				con.close();
				out.flush();
				out.close();
				return;
			} else {
				w.write("{\"status\":\"No endpooint\"}");
				w.flush();
				out.flush();
				out.close();
				return;
			}
			
		}catch(Exception e) {
			//throw new ServletException(e);
			
			try {
				con.close();
			} catch (Exception e1) {
//				e1.printStackTrace();
			}
			w.write("{\"status\":\"Error: See the log files for details.\"}");
			w.flush();
			out.flush();
			out.close();
			e.printStackTrace();
		}
	}

	
	
}
