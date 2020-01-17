package br.com.josef.desafioconcretegit.model.data;

import androidx.room.TypeConverter;

import com.facebook.stetho.inspector.protocol.module.Runtime;

import java.util.Date;
import java.util.Observable;

public class Converter {



    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    public Object fromString( value){
        return value == null? null : new Object(value);
    }


}
