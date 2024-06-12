package app.repository;

import app.domain.Car;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static app.constants.Constants.*;

public class CarRepositoryDB implements CarRepository {

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT id, brand, price, year FROM car";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Car car = new Car();
                car.setId(resultSet.getLong("id"));
                car.setBrand(resultSet.getString("brand"));
                car.setPrice(resultSet.getBigDecimal("price"));
                car.setYear(resultSet.getInt("year"));
                cars.add(car);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cars;
    }

    @Override
    public List<Car> getCarsWithParameters(BigDecimal minPrice, BigDecimal maxPrice, String brand, String orderBy, String order, int limit) {
        List<Car> cars = new ArrayList<>();
        try (Connection connection = getConnection()) {
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM car WHERE 1=1");

            if (minPrice != null) {
                sqlBuilder.append(" AND price >= ?");
            }
            if (maxPrice != null) {
                sqlBuilder.append(" AND price <= ?");
            }
            if (brand != null) {
                sqlBuilder.append(" AND brand = ?");
            }

            sqlBuilder.append(" ORDER BY ");
            if ("price".equals(orderBy)) {
                sqlBuilder.append("price");
            }
            if ("brand".equals(orderBy)) {
                sqlBuilder.append("brand");
            } else {
                sqlBuilder.append("id"); // По умолчанию сортировка по идентификатору
            }
            sqlBuilder.append(" ").append(order);

            sqlBuilder.append(" LIMIT ?;");
            PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString());

            int paramIndex = 1;
            if (minPrice != null) {
                statement.setBigDecimal(paramIndex++, minPrice);
            }
            if (maxPrice != null) {
                statement.setBigDecimal(paramIndex++, maxPrice);
            }
            if (brand != null) {
                statement.setString(paramIndex++, brand);
            }
            statement.setInt(paramIndex, limit);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = new Car();
                car.setId(resultSet.getLong("id"));
                car.setBrand(resultSet.getString("brand"));
                car.setPrice(resultSet.getBigDecimal("price"));
                car.setYear(resultSet.getInt("year"));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cars;
    }


    @Override
    public Car getById(long id) {
        Car car = null;
        try (Connection connection = getConnection()) {
            String query = "SELECT id, brand, price, year FROM car WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                car = new Car();
                car.setId(resultSet.getLong("id"));
                car.setBrand(resultSet.getString("brand"));
                car.setPrice(resultSet.getBigDecimal("price"));
                car.setYear(resultSet.getInt("year"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return car;
    }

    @Override
    public Car save(Car car) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO car (brand, price, year) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, car.getBrand());
            statement.setBigDecimal(2, car.getPrice());
            statement.setInt(3, car.getYear());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                car.setId(id);
            }
            return car;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePrice(long id, BigDecimal newPrice) {
        try (Connection connection = getConnection()) {
            String query = "UPDATE car SET price = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBigDecimal(1, newPrice);
            statement.setLong(2, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection connection = getConnection()) {
            String query = "DELETE FROM car WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() {
        try {
            Class.forName(DB_DRIVER_PATH);
            String dbUrl = String.format("%s%s?user=%s&password=%s",
                    DB_ADDRESS, DB_NAME, DB_USERNAME, DB_PASSWORD);
            return DriverManager.getConnection(dbUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
