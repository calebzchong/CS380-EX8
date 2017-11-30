import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
	static final String httpOK = "HTTP/1.1 200 OK\n"
							+ "Content-type: text/html\n"
							+ "Content-length: 124\n\n";
	
	static final String httpNotFound = "HTTP/1.1 404 Not Found\n" 
								+	"Content-type: text/html\n" 
								+	"Content-length: 126\n\n";
	
	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(8080)) {
			while (true) {
				System.out.println("Waiting for connection...");
				Socket socket = serverSocket.accept();
				System.out.println("Connected to client.");
				PrintStream out = new PrintStream(socket.getOutputStream(), true, "UTF-8");
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String first = br.readLine();
				String[] things = first.split(" ");
				String requestedFilePath = "www\\" + things[1].substring(1);
				System.out.println("Requested file: " + requestedFilePath);
				while ( br.ready() ){
					br.readLine();
					//System.out.println(br.readLine());
				}
				File file = new File(requestedFilePath);
				if ( file.exists() ){
					System.out.println("file exists");
					BufferedReader fileBR = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					out.printf(httpOK);
					while ( fileBR.ready() ){
						out.print(fileBR.readLine());
					}
					fileBR.close();
				} else {
					out.printf(httpNotFound);
					System.out.println("file does not exist");
					BufferedReader fileBR = new BufferedReader(new InputStreamReader(new FileInputStream("www\\404.html")));
					while ( fileBR.ready() ){
						out.print(fileBR.readLine());
					}
					fileBR.close();
				}
			}
		} catch ( BindException e ){
			System.out.println( "A server is already running on this port, terminating...");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
