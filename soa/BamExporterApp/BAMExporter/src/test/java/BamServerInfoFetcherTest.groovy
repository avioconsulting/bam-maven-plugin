import com.avioconsulting.bamexportrunner.BamServerInfoFetcher
import com.avioconsulting.bamexportrunner.PasswordDecryptor
import groovy.mock.interceptor.StubFor
import groovy.test.GroovyAssert
import org.junit.Test

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.assertThat

class BamServerInfoFetcherTest {
    File listenHostDomainConfig = new File('src/test/resources/listenHostDomainConfig.xml')
    File standardNodeManagerConfig = new File('src/test/resources/standardNodeManagerConfig.xml')

    def getResult(File serverConfig, File nodeManager) {
        assert serverConfig.exists()
        assert nodeManager.exists()
        def mock = new StubFor(PasswordDecryptor)
        mock.demand.with {
            decrypt {
                String input -> "decrypt ${input}"
            }
        }
        def info = null
        mock.use {
            info = new BamServerInfoFetcher(serverConfig,
                                            nodeManager,
                                            new PasswordDecryptor('foo')).bamServerInfo
        }
        info
    }

    @Test
    void parse_bamServerExplicitHost() {
        // act
        def result = getResult listenHostDomainConfig, standardNodeManagerConfig

        // assert
        assertThat result.host,
                   is(equalTo('the_bam_listen_address'))
        assertThat result.port,
                   is(equalTo(10001))
        assertThat result.username,
                   is(equalTo('weblogic'))
        assertThat result.password,
                   is(equalTo('decrypt {AES}r4jhRwZAbxa66FXOWFeCDy3+3H0x7NOrJYHs4Bh7yqQ='))
    }

    @Test
    void parse_bamServerLocalhost() {
        // arrange
        def localhost = new File('src/test/resources/localHostDomainConfig.xml')

        // act
        def result = getResult localhost, standardNodeManagerConfig

        // assert
        assertThat result.host,
                   is(equalTo('localhost'))
        assertThat result.port,
                   is(equalTo(10001))
        assertThat result.username,
                   is(equalTo('weblogic'))
        assertThat result.password,
                   is(equalTo('decrypt {AES}r4jhRwZAbxa66FXOWFeCDy3+3H0x7NOrJYHs4Bh7yqQ='))
    }

    @Test
    void parse_noBamServer() {
        // arrange
        def missingBam = new File('src/test/resources/noBamServerDomainConfig.xml')

        // act
        def exception = GroovyAssert.shouldFail {
            getResult missingBam, standardNodeManagerConfig
        }

        // assert
        assertThat exception.message,
                   is(containsString('Was not able to find BAM server bam_server1!'))
    }

    @Test
    void parse_noNodeManagerConfig() {
        // arrange
        def missingusername = new File('src/test/resources/differentNodeManagerFile.xml')

        // act
        def exception = GroovyAssert.shouldFail {
            getResult listenHostDomainConfig, missingusername
        }

        // assert
        assertThat exception.message,
                   is(containsString('Was not able to find username/password!'))
    }
}
