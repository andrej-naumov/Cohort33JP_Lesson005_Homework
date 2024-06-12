package app.constants;

public interface Constants {

    // http://10.2.3.4:8080/cars?id=3
    // jdbc:postgresql://10.2.3.4:5432/g_33_cars?user=postgres&password=ppp77777

    String DB_DRIVER_PATH = "org.postgresql.Driver";
    String DB_ADDRESS = "jdbc:postgresql://localhost:5432/";
    String DB_NAME = "cars";
    String DB_USERNAME = "cars_admin";
    String DB_PASSWORD = "my_secure_password";
}
