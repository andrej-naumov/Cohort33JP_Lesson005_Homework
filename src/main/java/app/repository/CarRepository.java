package app.repository;

import app.domain.Car;
import java.util.List;
import java.math.BigDecimal;

public interface CarRepository {
    List<Car> getAll();
    List<Car> getCarsWithParameters(BigDecimal minPrice, BigDecimal maxPrice, String brand, String orderBy, String order, int limit);
    Car save(Car car);
    Car getById(long id); // добавляем метод getById
    void updatePrice(long id, BigDecimal newPrice); // новый метод для обновления цены
    void deleteById(long id); // новый метод для удаления автомобиля по id
}
