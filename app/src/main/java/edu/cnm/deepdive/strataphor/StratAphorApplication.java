package edu.cnm.deepdive.strataphor;

import android.app.Application;
import edu.cnm.deepdive.strataphor.model.StratAphorDatabase;
import java.util.concurrent.Executors;

public class StratAphorApplication extends Application {

  private static StratAphorApplication instance = null;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    Executors.newSingleThreadExecutor().execute(() ->
        StratAphorDatabase.getInstance().getSourceDao().findFirstById(0));
  }

  public static StratAphorApplication getInstance() {
    return instance;
  }

}
