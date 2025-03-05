package server;
import spark.Spark;
import spark.Request;
import spark.Response;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class experiment {

//    Spark.get(“/hello”, (req, res) -> “Hello BYU!”);
public static void main(String[] args) {
    try {
        // Create a URL object from the URL string
        URL url = new URL("http://localhost:8080/session");

        // Open a connection to the server
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("DELETE");

        // Set the request header to indicate we're sending JSON
        connection.setRequestProperty("Content-Type", "application/json");

        // Enable input/output streams
        connection.setDoOutput(true);

        // Create the JSON data to send in the request body

        String jsonInputString = "{\"authToken\": \"sfjdkslafj;dskfkds;aljfds\"}";
        //String jsonInputString = "{\"username\": \"daviddarley\", \"password\": \"goliathsux123\"}";
        //String jsonInputString = "{\"username\": \"davidsdarley\", \"password\": \"goliathsux123\", \"email\": \"dsd2001@byu.edu\"}";

        // Write the JSON string to the output stream
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Get the response code (optional)
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response (optional)
        // (this part is not necessary for just sending the request, but you could handle it)
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            System.out.println("Response: " + response.toString());
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private static Object handleHello(Request req, Response res) {
        return "Hello BYU!";}


}
