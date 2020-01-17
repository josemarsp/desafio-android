package br.com.josef.desafioconcretegit.model.data;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.josef.desafioconcretegit.model.pojo.repositories.Item;

@androidx.room.Database(entities = {Item.class}, version = 1, exportSchema = false)
@TypeConverters(Converter.class)
public abstract class DatabaseGit extends RoomDatabase {


    public abstract CacheDao cacheDao();

    private static volatile DatabaseGit INSTANCE;

    public static DatabaseGit getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseGit.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseGit.class, "git_db")
                            .build();

                }
            }

        }return INSTANCE;
    }
}
