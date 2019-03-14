package edu.cnm.deepdive.strataphor.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import edu.cnm.deepdive.strataphor.model.entity.Saying;
import edu.cnm.deepdive.strataphor.model.pojo.SayingWithSource;
import java.util.List;

@Dao
public interface SayingDao {

  @Insert
  long insert(Saying saying);

  @Insert
  List<Long> insert(Saying... sayings);

  @Insert
  List<Long> insert(List<Saying> sayings);

  @Query("SELECT * FROM Saying ORDER BY text ASC")
  List<Saying> findAll();

  @Query("SELECT * FROM Saying WHERE source_id = :sourceId ORDER BY text ASC")
  List<Saying> findAllBySourceId(long sourceId);

  @Query("SELECT * FROM Saying WHERE saying_id = :sayingId")
  Saying findFirstById(long sayingId);

  @Query("SELECT Saying.text as saying, Source.name as source FROM Saying INNER JOIN Source ON Source.source_id = Saying.source_id ORDER BY RANDOM() LIMIT 1")
  SayingWithSource findFirstRandom();

  @Update
  int update(Saying... sayings);

  @Update
  int update(List<Saying> sayings);

  @Delete
  int delete(Saying... sayings);

  @Delete
  int delete(List<Saying> sayings);

}
