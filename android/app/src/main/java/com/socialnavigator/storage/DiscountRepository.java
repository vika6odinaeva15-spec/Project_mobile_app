package com.socialnavigator.storage;

import com.socialnavigator.models.Discount;

import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий скидок.
 * Хранит список в памяти. Позже (Этап 5) будет загружать данные с API.
 */
public class DiscountRepository {

    private static DiscountRepository instance;
    private final List<Discount> discounts = new ArrayList<>();

    // Приватный конструктор — синглтон
    private DiscountRepository() {
    }

    // Получить единственный экземпляр
    public static synchronized DiscountRepository getInstance() {
        if (instance == null) {
            instance = new DiscountRepository();
        }
        return instance;
    }

    // Загрузить тестовые данные (пока — хардкод)
    public void loadTestData() {
        discounts.clear();

        discounts.add(new Discount(
                1,
                "Москва",
                "Участники СВО",
                "Москвариум",
                "Океанариум",
                "Скидка 30% от стоимости билета в Аквариум.",
                "пн–пт (за исключением праздников)",
                "Проспект Мира, 119, стр. 23",
                "https://moskvarium.ru"
        ));

        discounts.add(new Discount(
                2,
                "Москва",
                "Участники СВО",
                "Государственный музей В. В. Маяковского",
                "Музей",
                "Цена билета на посещение площадок музея «Квартира на Большой Пресне».",
                "Вт, ср, пт, сб: 11:00 – 19:00; чт: 13:00 – 21:00",
                "Студенецкий пер., д. 6, кв. 20",
                "https://muzeimayakovskogo.ru"
        ));

        discounts.add(new Discount(
                3,
                "Москва",
                "Участники СВО",
                "Аквапарк «Фэнтази»",
                "Аквапарк",
                "Скидка на билет «Целый день» и «5 часов»: 15% в выходные.",
                "В любое рабочее время",
                "ул. Люблинская, 100, к. 2",
                "https://fpark.ru"
        ));

        discounts.add(new Discount(
                4,
                "Москва",
                "Участники СВО",
                "Третьяковская галерея",
                "Музей",
                "Бесплатное посещение для участников СВО.",
                "Вт, ср, вс: 10:00 – 18:00; чт, пт, сб: 10:00 – 21:00",
                "Лаврушинский пер., 10",
                "https://www.tretyakovgallery.ru"
        ));

        discounts.add(new Discount(
                5,
                "Москва",
                "Участники СВО",
                "Термоленд",
                "Аквапарк",
                "Скидка 20% на билеты в термальный комплекс.",
                "Ежедневно с 10:00 до 22:00",
                "ул. Автозаводская, 18",
                "https://termy-los.ru"
        ));

        discounts.add(new Discount(
                6,
                "Москва",
                "Участники СВО",
                "ВДНХ",
                "Выставка",
                "Бесплатный вход в музеи ВДНХ для участников СВО.",
                "Ежедневно с 10:00 до 22:00",
                "проспект Мира, 119",
                "https://vdnh.ru"
        ));
    }

    // Получить все скидки
    public List<Discount> getAll() {
        return discounts;
    }

    // Получить скидку по ID
    public Discount getById(int id) {
        for (Discount discount : discounts) {
            if (discount.getId() == id) {
                return discount;
            }
        }
        return null;
    }
}