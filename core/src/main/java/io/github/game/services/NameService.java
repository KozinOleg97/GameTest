package io.github.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NameService {

    private final Map<String, List<String>> nameCache = new HashMap<>();
    private final Random random = new Random();

    @Inject
    public NameService() {
        // Предзагрузка всех файлов при создании сервиса
        preloadAllNameFiles();
    }

    /**
     * Возвращает случайное название для типа
     */
    public String getRandomName(String locationType) {
        List<String> names = nameCache.get(locationType);
        if (names == null || names.isEmpty()) {
            Gdx.app.error("NameService", "No names found for type: " + locationType);
            return getDefaultName(locationType);
        }
        return names.get(random.nextInt(names.size()));
    }

    /**
     * Возвращает все названия для типа
     */
    public List<String> getAllNames(String locationType) {
        return new ArrayList<>(nameCache.getOrDefault(locationType, getDefaultNames(locationType)));
    }

    /**
     * Предзагрузка всех файлов с названиями
     */
    private void preloadAllNameFiles() {
        String[] locationTypes = {
            "cities", "villages", "towns", "outposts",
            "ports", "fortresses", "monasteries", "mines",
            "watchtowers", "bridges"
        };

        for (String type : locationTypes) {
            loadNamesFromFile(type);
        }
    }

    /**
     * Загружает названия из файла
     */
    private void loadNamesFromFile(String locationType) {
        String filePath = "data/locations/" + locationType + ".txt";
        FileHandle file = Gdx.files.internal(filePath);

        if (!file.exists()) {
            Gdx.app.error("NameService", "File not found: " + filePath);
            nameCache.put(locationType, getDefaultNames(locationType));
            return;
        }

        try {
            String[] lines = file.readString().split("\\r?\\n");
            List<String> names = new ArrayList<>();

            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) { // Игнорируем комментарии
                    names.add(trimmed);
                }
            }

            if (names.isEmpty()) {
                Gdx.app.log("NameService", "No names found in: " + filePath);
                names = getDefaultNames(locationType);
            }

            nameCache.put(locationType, names);
            Gdx.app.log("NameService", "Loaded " + names.size() + " names for: " + locationType);

        } catch (GdxRuntimeException e) {
            Gdx.app.error("NameService", "Error reading file: " + filePath, e);
            nameCache.put(locationType, getDefaultNames(locationType));
        }
    }

    /**
     * Запасные названия на случай отсутствия файлов
     */
    private List<String> getDefaultNames(String locationType) {
        return switch (locationType) {
            case "cities" -> Arrays.asList(
                "Стольный Град", "Великий Город", "Столица", "Королевский Город"
            );
            case "villages" -> Arrays.asList(
                "Деревня", "Село", "Хутор", "Поселение"
            );
            case "towns" -> Arrays.asList(
                "Городок", "Местечко", "Слобода", "Посад"
            );
            case "outposts" -> Arrays.asList(
                "Застава", "Форт", "Аванпост", "Сторожевая"
            );
            case "ports" -> Arrays.asList(
                "Порт", "Гавань", "Пристань", "Причал"
            );
            case "fortresses" -> Arrays.asList(
                "Крепость", "Цитадель", "Бастион", "Укрепление"
            );
            case "monasteries" -> Arrays.asList(
                "Монастырь", "Обитель", "Скит", "Лавра"
            );
            case "mines" -> Arrays.asList(
                "Шахта", "Рудник", "Копь", "Прииск"
            );
            case "watchtowers" -> Arrays.asList(
                "Сторожевая Башня", "Дозорная", "Вахта", "Наблюдательный Пункт"
            );
            case "bridges" -> Arrays.asList(
                "Мост", "Переправа", "Переход", "Тракт"
            );
            default -> List.of("Неизвестное Место");
        };
    }

    private String getDefaultName(String locationType) {
        List<String> defaultNames = getDefaultNames(locationType);
        return defaultNames.get(random.nextInt(defaultNames.size()));
    }
}
