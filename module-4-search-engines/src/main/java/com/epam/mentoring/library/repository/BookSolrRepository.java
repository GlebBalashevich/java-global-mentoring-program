package com.epam.mentoring.library.repository;

import java.util.Optional;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import com.epam.mentoring.library.model.Book;

@Repository
public interface BookSolrRepository extends SolrCrudRepository<Book, String> {

    Optional<Book> findBookByTitle(String title);

}
