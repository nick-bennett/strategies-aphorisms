package edu.cnm.deepdive.strataphor;

import android.app.Application;
import com.facebook.stetho.Stetho;
import edu.cnm.deepdive.android.BaseFluentAsyncTask;
import edu.cnm.deepdive.strataphor.model.StratAphorDatabase;

public class StratAphorApplication extends Application {

  private static StratAphorApplication instance = null;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    Stetho.initializeWithDefaults(this); // Comment out this line to disable Stetho.
    new BaseFluentAsyncTask<Void, Void, Void, Void>()
        .setPerformer((ignore) -> {
          StratAphorDatabase.getInstance().getSourceDao().findFirstById(0);
          return null;
        })
        .execute();
  }

  public static StratAphorApplication getInstance() {
    return instance;
  }

}
