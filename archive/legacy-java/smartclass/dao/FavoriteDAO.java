package com.smartclass.dao;

import java.util.List;
import com.smartclass.model.Favorite;

public interface FavoriteDAO {
    boolean isFavorite(int userId, int classroomId);
    List<Favorite> findByUserId(int userId);
    Favorite create(int userId, int classroomId);
    boolean delete(int userId, int classroomId);
}