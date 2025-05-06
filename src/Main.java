public class main {

    public static void main(String[] args) {
        try {
            // Initialize the DatabaseManager with your Firebase Realtime Database base URL
            String baseUrl = "https://languageproject-67eb8-default-rtdb.europe-west1.firebasedatabase.app/";
            DatabaseManager dmTest = new DatabaseManager(baseUrl);

            // Write
            String payload = "{\"user\":\"G\",\"message\":\"Hello, world!\",\"timestamp\":\"2025-05-05T12:00\"}";
            int postCode = dmTest.writeJson("data.json", payload);
            System.out.println("WRITE HTTP code: " + postCode);

            // Read
            String json = dmTest.readJson("data.json");
            System.out.println("READ JSON payload: " + json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void insertVal(){
        try {
            URL url = new URL("https://languageproject-67eb8-default-rtdb.europe-west1.firebasedatabase.app/data.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Customize your data here
            String message = "ENGLISH: Wrench";

            String jsonData = "{\"user\":\"G\",\"message\":\"" + message + "\",\"timestamp\":\"2025-04-25T14:30\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Response Data : " + response.toString());
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
