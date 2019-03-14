package edu.cnm.deepdive.strataphor.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import edu.cnm.deepdive.strataphor.R;
import edu.cnm.deepdive.strataphor.StratAphorApplication;
import edu.cnm.deepdive.strataphor.model.dao.SayingDao;
import edu.cnm.deepdive.strataphor.model.dao.SourceDao;
import edu.cnm.deepdive.strataphor.model.entity.Saying;
import edu.cnm.deepdive.strataphor.model.entity.Source;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Database(
    entities = {Source.class, Saying.class},
    version = 1,
    exportSchema = true
)
public abstract class StratAphorDatabase extends RoomDatabase {

  private static final String DATABASE_NAME = "strataphor_db";

  public static StratAphorDatabase getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public abstract SourceDao getSourceDao();

  public abstract SayingDao getSayingDao();

  private static class InstanceHolder {

    private static final StratAphorDatabase INSTANCE =
        Room.databaseBuilder(StratAphorApplication.getInstance().getApplicationContext(),
            StratAphorDatabase.class, DATABASE_NAME)
            .addCallback(new Callback())
            .build();

  }

  private static class Callback extends RoomDatabase.Callback {

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
      super.onCreate(db);
      Executors.newSingleThreadExecutor().execute(new PreloadTask());
    }

  }

  private static class PreloadTask implements Runnable {

    @Override
    public void run() {
      Context context = StratAphorApplication.getInstance().getApplicationContext();
      StratAphorDatabase database = StratAphorDatabase.getInstance();
      try (
          InputStream input = context.getResources().openRawResource(R.raw.sources);
          Reader reader = new InputStreamReader(input);
          CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
      ) {
        List<Saying> sayings = new LinkedList<>();
        for (CSVRecord record : parser) {
          Source source = new Source();
          source.setName(record.get(0));
          String resourceName = record.get(1);
          long sourceId = database.getSourceDao().insert(source);
          sayings.addAll(loadSayings(sourceId, resourceName));
        }
        database.getSayingDao().insert(sayings);
      } catch (IOException e) {
        Log.e(getClass().getSimpleName(), e.toString(), e);
      }
    }

    private List<Saying> loadSayings(long sourceId, String resourceName) throws IOException {
      Context context = StratAphorApplication.getInstance().getApplicationContext();
      int resourceId =
          context.getResources().getIdentifier(resourceName, "raw", context.getPackageName());
      try (
          InputStream input = context.getResources().openRawResource(resourceId);
          Reader reader = new InputStreamReader(input);
          BufferedReader buffer = new BufferedReader(reader);
      ) {
        List<Saying> sayings = new LinkedList<>();
        String line;
        while ((line = buffer.readLine()) != null) {
          if (!(line = line.trim()).isEmpty()) {
            Saying saying = new Saying();
            saying.setSourceId(sourceId);
            saying.setText(line);
            sayings.add(saying);
          }
        }
        return sayings;
      }
    }

  }

}
