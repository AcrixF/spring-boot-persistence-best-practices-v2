package org.neoa.percistence.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.neoa.percistence.entity.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void setup() {
        assertThat(authorRepository).isNotNull();
    }

    @Test
    public void saveAuthor() {
        Author author = Author
                .builder()
                .name("Brenda Marlen Martinez Flores")
                .age(27)
                .genre("Female")
                .build();
        Author expected = authorRepository.save(author);
        assertThat(expected).isNotNull();
        assertThat(expected.getId()).isNotNull();

        boolean existsById = authorRepository.existsById(author.getId());

        assertThat(existsById).isTrue();


    }
}
