package app.controller;

import app.domain.Car;
import app.repository.CarRepository;
import app.repository.CarRepositoryDB;
import app.repository.CarRepositoryMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public class CarServlet extends HttpServlet {

    // private CarRepository repository = new CarRepositoryMap();
    private CarRepository repository = new CarRepositoryDB();

    // GET http://10.2.3.4:8080/cars
    // GET http://10.2.3.4:8080/cars?id=5

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Получение параметра id из запроса
        String idParam = req.getParameter("id");

        // Если параметр id передан, пытаемся найти автомобиль по этому id
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Car car = repository.getById(id);

                if (car != null) {
                    resp.getWriter().write(car.toString());
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("Car with id " + id + " not found.");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid id format.");
            }
        } else {
            // Получение параметров из запроса
            String minPriceParam = req.getParameter("minprice");
            String maxPriceParam = req.getParameter("maxprice");
            String brandParam = req.getParameter("brand"); // параметр для фильтрации по марке
            String orderByParam = req.getParameter("orderby");
            String orderParam = req.getParameter("order");
            String limitParam = req.getParameter("limit");

            // Переменные для хранения значений параметров
            BigDecimal minPrice = null;
            BigDecimal maxPrice = null;
            String orderBy = "id"; // По умолчанию сортировка по идентификатору
            String order = "asc"; // По умолчанию сортировка по возрастанию
            int limit = 10; // По умолчанию ограничение на 10 записей

            // Попытка преобразования параметров в нужный тип
            try {
                if (minPriceParam != null) {
                    minPrice = new BigDecimal(minPriceParam);
                }
                if (maxPriceParam != null) {
                    maxPrice = new BigDecimal(maxPriceParam);
                }
                if (orderByParam != null) {
                    orderBy = orderByParam;
                }
                if (orderParam != null) {
                    order = orderParam;
                }
                if (limitParam != null) {
                    limit = Integer.parseInt(limitParam);
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid parameter format.");
                return;
            }

            // Получение списка автомобилей с учетом параметров
            List<Car> cars = repository.getCarsWithParameters(minPrice, maxPrice, brandParam, orderBy, order, limit);

            // Вывод списка автомобилей в ответе
            for (Car car : cars) {
                resp.getWriter().write(car.toString() + "\n");
            }
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Для сохранения нового автомобиля в БД

        ObjectMapper mapper = new ObjectMapper();
        Car car = mapper.readValue(req.getReader(), Car.class);
        repository.save(car);
        resp.getWriter().write("New car saved: " + car.toString());
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Чтение JSON из тела запроса
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(req.getReader());

        // Извлечение id и новой цены из JSON
        long id = jsonNode.get("id").asLong();
        BigDecimal newPrice = new BigDecimal(jsonNode.get("price").asText());

        // Поиск автомобиля в базе данных
        Car car = repository.getById(id);

        if (car == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Car with id " + id + " not found.");
            return;
        }

        // Обновление цены автомобиля
        repository.updatePrice(id, newPrice);

        // Отправка успешного ответа
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Car price updated to new price: " + newPrice);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Извлечение параметра id из запроса
        String idParam = req.getParameter("id");

        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing parameter: id");
            return;
        }

        try {
            long id = Long.parseLong(idParam);

            // Поиск и удаление автомобиля в базе данных
            Car car = repository.getById(id);

            if (car == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Car with id " + id + " not found.");
                return;
            }

            repository.deleteById(id);

            // Отправка успешного ответа
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Car with id " + id + " deleted.");

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid id format.");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }
}