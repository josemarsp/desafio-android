package br.com.josef.desafioconcretegit.model.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import br.com.josef.desafioconcretegit.model.pojo.repositories.Item;
import io.reactivex.Flowable;

@Dao
public interface CacheDao {

    @Query("Select * from item ")
    Flowable<List<Item>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Item> results);

    @Query("Delete from item")
    void deleteAll();
}
