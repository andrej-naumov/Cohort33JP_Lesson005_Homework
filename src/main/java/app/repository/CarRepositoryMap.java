package app.repository;

import app.domain.Car;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarRepositoryMap implements CarRepository {

    private final Map<Long, Car> database = new HashMap<>();
    private long currentId;

    public CarRepositoryMap() {
        save(new Car("Volkswagen", new BigDecimal(10000), 2010));
        save(new Car("Mazda", new BigDecimal(30000), 2015));
        save(new Car("Honda", new BigDecimal(50000), 2020));
    }

    @Override
    public List<Car> getAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public List<Car> getCarsWithParameters(BigDecimal minPrice, BigDecimal maxPrice, String brand, String orderBy, String order, int limit) {
        // TODO: implement please
        return List.of();
    }


    @Override
    public Car save(Car car) {
        car.setId(++currentId);
        database.put(currentId, car);
        return car;
    }

    @Override
    public Car getById(long id) { // реализация метода getById
        return database.get(id);
    }

    @Override
    public void updatePrice(long id, BigDecimal newPrice) {
        Car car = database.get(id);
        if (car != null) {
            car.setPrice(newPrice);
        }
    }

    @Override
    public void deleteById(long id) {
        database.remove(id);
    }
}
