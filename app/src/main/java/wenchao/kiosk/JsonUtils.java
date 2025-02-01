package wenchao.kiosk;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    private static final String NEWS_FILE_NAME = "news.json";

    public static void saveNewsList(Context context, List<NewsInfo> newsList) {
        Gson gson = new Gson();
        String json = gson.toJson(newsList);
        writeFile(context, NEWS_FILE_NAME, json);
    }

    public static List<NewsInfo> loadNewsList(Context context) {
        String json = readFile(context, NEWS_FILE_NAME);
        if (json != null) {
            Gson gson = new Gson();
            Type newsListType = new TypeToken<List<NewsInfo>>() {}.getType();
            return gson.fromJson(json, newsListType);
        }
        return null;
    }

    private static void writeFile(Context context, String fileName, String content) {
        File file = new File(context.getFilesDir(), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        try (FileReader reader = new FileReader(file)) {
            char[] buffer = new char[(int) file.length()];
            reader.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
