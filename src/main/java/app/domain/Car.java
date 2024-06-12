package app.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Car {
    private long id;
    private String brand;
    private BigDecimal price;
    private int year;

    // Конструктор по умолчанию
    public Car() {
    }

    // Конструктор с параметрами
    @JsonCreator
    public Car(@JsonProperty("brand") String brand,
               @JsonProperty("price") BigDecimal price,
               @JsonProperty("year") int year) {
        this.brand = brand;
        this.price = price;
        this.year = year;
    }

    // Геттеры и сеттеры
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return String.format("Car: id - %d, brand - %s, price - %s, year - %d",
                id, brand, price, year);
    }
}
