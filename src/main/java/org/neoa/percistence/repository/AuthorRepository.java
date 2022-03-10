package org.neoa.percistence.repository;

import org.neoa.percistence.entity.Author;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {

    @Query("SELECT a FROM Author a JOIN FETCH a.books WHERE a.name = ?1")
    Author fetchByName(String name);
}
