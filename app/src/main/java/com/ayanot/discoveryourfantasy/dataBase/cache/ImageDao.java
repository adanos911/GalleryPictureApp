package com.ayanot.discoveryourfantasy.dataBase.cache;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ayanot.discoveryourfantasy.entity.Image;

import java.util.List;

/**
 * <h3>Интерфейс, предоставляющий методы работы с БД {@link ImageDatabase}</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
@Dao
public interface ImageDao {

    @Query("select * from cache_images")
    List<Image> getAll();

    @Query("select * from cache_images where id in (:imageIds)")
    List<Image> loadAllByIds(long... imageIds);

    @Query("select * from cache_images where id = :imageId limit 1")
    Image findById(long imageId);

    @Query("select * from cache_images where name = :name limit 1")
    Image findByName(String name);

    @Query("select * from cache_images where name like :names")
    List<Image> loadAllByNames(String... names);

    @Query("select count(*) from cache_images")
    int getRowCount();

    @Query("delete from cache_images where id = (select id from cache_images order by id desc limit 8)")
    void deleteOldRows();

    @Query("delete from cache_images where id = :imageId")
    void deleteById(long imageId);

    @Insert
    void insertAll(Image... images);

    @Delete
    void delete(Image image);
}
