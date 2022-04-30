package com.bookshop.service.impl;

import com.bookshop.model.Author;
import com.bookshop.model.exceptions.AuthorNotFoundException;
import com.bookshop.repository.AuthorRepository;
import com.bookshop.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }


    @Override
    public List<Author> findAll() {
        return this.authorRepository.findAll();
    }

    @Override
    public List<Author> findAllByName(String name) {
        return this.authorRepository.findAllByName(name);
    }

    @Override
    public Author findById(Long id) {
        return this.authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(id));
    }

    @Override
    public Author save(Author author) {
        return this.authorRepository.save(author);
    }

    @Override
    public Author update(Long id, String name, String surname, String description) {
        Author author = this.findById(id);

        author.setName(name);
        author.setSurname(surname);
        author.setDescription(description);

        return this.authorRepository.save(author);
    }

    @Override
    public void deleteById(Long id) {
        this.authorRepository.deleteById(id);
    }
}
