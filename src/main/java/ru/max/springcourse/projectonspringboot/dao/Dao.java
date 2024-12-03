package ru.max.springcourse.projectonspringboot.dao;



import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {

    E save(E person);
    boolean delete(K id);
    boolean update(E person);
    List<E> findAll();
    Optional<E> findById(K id);


}
