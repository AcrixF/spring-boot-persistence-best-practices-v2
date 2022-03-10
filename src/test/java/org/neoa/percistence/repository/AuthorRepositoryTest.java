package org.neoa.percistence.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.neoa.percistence.entity.Author;
import org.neoa.percistence.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;


    @BeforeEach
    public void setup() {
        assertThat(authorRepository).isNotNull();
        insertAuthorsWithBook();
    }

    @AfterEach
    public void cleanup() {
        //authorRepository.deleteAll();
    }

    @Test
    @Transactional
    public void saveAuthorWithUnidirectionalAssociationLeadsASeparateJunctionTable() {
       Author author = authorRepository.fetchByName("Joana Nimar");
       assertThat(author).isNotNull();
       assertThat(author.getBooks()).isNotNull()
               .isNotEmpty()
               .hasSize(3);
    }

    @Test
    @Transactional
    public void saveAuthorWithUnidirectionalAssociationDeleteAllRecordsWhenNewBookIsInserted() {

        /*
            This time Hibernate doesn't delete the associate books to add them back from memory.
         */

        Author author = authorRepository.fetchByName("Joana Nimar");
        Book book = new Book();
        book.setTitle("History Details");
        author.addBook(book);

        author = authorRepository.save(author);

        assertThat(author).isNotNull();
        assertThat(author.getBooks()).isNotNull()
                .isNotEmpty()
                .hasSize(4)
                .extracting(Book::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L);

    }

    @Test
    @Transactional
    public void deletingLastBookWithUnidirectionalAssociationDeletesAllAssociatedBooksFromJunctionTableAndReInsertRemaining() {

        /*
            Looks like @OrderColumn brought some benefits in the case of removing the last book. Hibernate did not delete all the
            associate books to add the remaining from memory.
         */
        Author author = authorRepository.fetchByName("Joana Nimar");
        List<Book> books = author.getBooks();
        author.removeBook(books.get(books.size() - 1));

        Optional<Author> sameAuthor = authorRepository.findById(1L);

        assertThat(sameAuthor).isPresent();
        assertThat(sameAuthor.get().getBooks())
                .isNotNull()
                .isNotEmpty()
                .extracting(Book::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    @Transactional
    public void deleteFirstBookWithUnidirectionalAssociationDeletesAllAssociatedBooksFromJunctionTableAnReInsertRemaining() {
        Author author = authorRepository.fetchByName("Joana Nimar");
        List<Book> books = author.getBooks();

        author.removeBook(books.get(0));

        Optional<Author> sameAuthor = authorRepository.findById(1L);

        assertThat(sameAuthor).isPresent();
        assertThat(sameAuthor.get().getBooks())
                .isNotNull()
                .isNotEmpty()
                .extracting(Book::getId)
                .containsExactly(2L, 3L);
    }


    @Transactional
    public void insertAuthorsWithBook() {

        Author author = new Author();
        author.setName("Joana Nimar");
        author.setAge(34);
        author.setGenre("History");

        Book bookOne = new Book();
        bookOne.setIsbn("001-JN");
        bookOne.setTitle("A History of Ancient Prague");

        Book bookTwo = new Book();
        bookTwo.setIsbn("002-JN");
        bookTwo.setTitle("A People's History");

        Book bookThree = new Book();
        bookThree.setIsbn("003-JN");
        bookThree.setTitle("World History");

        author.addBook(bookOne);
        author.addBook(bookTwo);
        author.addBook(bookThree);

        authorRepository.save(author);
    }
}
