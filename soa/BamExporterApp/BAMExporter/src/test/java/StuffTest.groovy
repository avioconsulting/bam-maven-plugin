import com.avioconsulting.bamexportrunner.Exporter
import groovy.test.GroovyAssert
import org.junit.Test

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.assertThat

class StuffTest {
    @Test
    void validateProjectName_Letters() {
        // act
        def result = Exporter.validateProjectName 'ABC'

        // assert
        assertThat result,
                   is(equalTo(true))
    }

    @Test
    void validateProjectName_LettersLcase() {
        // act
        def result = Exporter.validateProjectName 'abc'

        // assert
        assertThat result,
                   is(equalTo(true))
    }

    @Test
    void validateProjectName_LettersMixcase() {
        // act
        def result = Exporter.validateProjectName 'Abc'

        // assert
        assertThat result,
                   is(equalTo(true))
    }

    @Test
    void validateProjectName_LettersNumbers() {
        // act
        def result = Exporter.validateProjectName 'ABC123'

        // assert
        assertThat result,
                   is(equalTo(true))
    }

    @Test
    void validateProjectName_Spaces() {
        // act
        def exception = GroovyAssert.shouldFail {
            Exporter.validateProjectName 'ABC DEF'
        }

        // assert
        assertThat exception.message,
                   is(containsString("Project name 'ABC DEF' includes a space and that's not supported!"))
    }

    @Test
    void validateProjectName_Quotes() {
        // act
        def exception = GroovyAssert.shouldFail {
            Exporter.validateProjectName 'ABC"DEF'
        }

        // assert
        assertThat exception.message,
                   is(containsString("Project name 'ABC\"DEF' includes quotes and that's not supported!"))
    }

    @Test
    void validateProjectName_Semicolons() {
        // act
        def exception = GroovyAssert.shouldFail {
            Exporter.validateProjectName 'ABC;DEF'
        }

        // assert
        assertThat exception.message,
                   is(containsString("Project name 'ABC;DEF' includes a semicolon and that's not supported!"))
    }
}
