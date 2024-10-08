package com.colin.sprintbootassessment.repository;

import com.colin.sprintbootassessment.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Data Persistence: CrudRepository (Basic), JpaRepository (Advanced, extends from CrudRepository)
    // save()           -- save() method is also equivalent to performing an update
    // findOne()
    // findById()
    // findByEmail()
    // findAll()
    // count()
    // delete()
    // deleteById()

}
