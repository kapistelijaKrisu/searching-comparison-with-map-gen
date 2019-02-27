package search_algorithm;

import mock.MockAnalysisWriter;
import mock.WebMapMock;
import model.web.WebMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import search_algorithm.structure_type.QueueType;
import search_algorithm.structure_type.UniqueSetType;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BreathSearchTest {

    private static MockAnalysisWriter mockWriter = new MockAnalysisWriter();

    @Test
    public void nullIsIllegalArgumentTest() {
        Throwable exceptionNull = assertThrows(IllegalArgumentException.class, () -> new BreathSearch(mockWriter, null, UniqueSetType.PRE_MADE_HASH_SET));
        assertEquals("Arguments cannot be null", exceptionNull.getMessage());
        exceptionNull = assertThrows(IllegalArgumentException.class, () -> new BreathSearch(mockWriter, QueueType.CUSTOM_QUEUE, null));
        assertEquals("Arguments cannot be null", exceptionNull.getMessage());
        exceptionNull = assertThrows(IllegalArgumentException.class, () -> new BreathSearch(mockWriter, null, null));
        assertEquals("Arguments cannot be null", exceptionNull.getMessage());
    }

    //setting up tests for all non null constructors
    static class BreathSearchArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    new BreathSearch(mockWriter, QueueType.CUSTOM_QUEUE, UniqueSetType.CUSTOM_DYNAMIC_SIZE_HASH_SET),
                    new BreathSearch(mockWriter, QueueType.CUSTOM_QUEUE, UniqueSetType.PRE_MADE_HASH_SET),
                    new BreathSearch(mockWriter, QueueType.CUSTOM_QUEUE, UniqueSetType.CUSTOM_SET_SIZE_HASH_SET),
                    new BreathSearch(mockWriter, QueueType.PRE_MADE_QUEUE, UniqueSetType.CUSTOM_DYNAMIC_SIZE_HASH_SET),
                    new BreathSearch(mockWriter, QueueType.PRE_MADE_QUEUE, UniqueSetType.PRE_MADE_HASH_SET),
                    new BreathSearch(mockWriter, QueueType.PRE_MADE_QUEUE, UniqueSetType.CUSTOM_SET_SIZE_HASH_SET)
            ).map(Arguments::of);
        }
    }


    @ParameterizedTest
    @ArgumentsSource(BreathSearchArgumentsProvider.class)
    public void doesNotThrowErrorsWithValidMapTest(BreathSearch breathSearch) throws IOException {
        breathSearch.setMapClean(WebMapMock.getMinimumValidMap());
        breathSearch.runSearch();
        assertTrue(mockWriter.isValidatingReturnedTrue());
        assertEquals("/doc/reports/nameless map/Breath first", mockWriter.getReceivedPath());
    }

    @ParameterizedTest
    @ArgumentsSource(BreathSearchArgumentsProvider.class)
    public void doesThrowErrorsWithInvalidMapTest(BreathSearch breathSearch) {
        var invalidMap = WebMapMock.getMinimumValidMap();
        invalidMap.setTileTarget(-1, 22);
        breathSearch.setMapClean(invalidMap);

        Throwable exception = assertThrows(IllegalStateException.class, () -> breathSearch.runSearch());
        assertEquals("Requires valid map, and name for algorithm", exception.getMessage());
    }

    /**
     * tests all but hardware and jvm related untestable values
     *
     * @throws IOException shouldnt happen because writer is mocked
     */
    @ParameterizedTest
    @ArgumentsSource(BreathSearchArgumentsProvider.class)
    public void valuesSetCorectlyWhereStartIsTargetTest(BreathSearch breathSearch) throws IOException {
        WebMap map = WebMapMock.getMinimumValidMap();
        map.setTileStart(map.getTileTarget().x, map.getTileTarget().y);
        breathSearch.setMapClean(map);
        breathSearch.runSearch();
        assertTrue(mockWriter.isValidatingReturnedTrue());
        assertEquals("/doc/reports/nameless map/Breath first", mockWriter.getReceivedPath());
        assertEquals("Breath first with " + breathSearch.getUniqueSetType() + " to keep track of visited edges and " + breathSearch.getQueueType() + " as an implementation of queue.", mockWriter.receivedAlDoc());
        assertEquals("Breath first", mockWriter.receivedAlgorithm());
        assertEquals("| V |", mockWriter.receivedAlSpace());
        assertEquals("O( | V + E | )", mockWriter.receivedAlTime());
        assertEquals("Width: 2 Height: 2\r\nStart location: 1,1\r\nTarget location: 1,1", mockWriter.receivedMapInfo());
        assertEquals("1", mockWriter.receivedTestMaxSteps());
        assertEquals("Target was not found", mockWriter.receivedTestPathWeight());
        assertEquals("0", mockWriter.receivedTestUsedSteps());

        String expectedProcessedMap = ". @ \r\n@ O ";
        assertEquals(expectedProcessedMap, mockWriter.receivedProcessedMap());
    }

    @ParameterizedTest
    @ArgumentsSource(BreathSearchArgumentsProvider.class)
    public void valuesSetCorectlyWhereStartIsNotTargetTest(BreathSearch breathSearch) throws IOException {
        breathSearch.setMapClean(WebMapMock.getValid6x7Map());
        breathSearch.runSearch();
        assertTrue(mockWriter.isValidatingReturnedTrue());
        assertEquals("/doc/reports/nameless map/Breath first", mockWriter.getReceivedPath());
        assertEquals("Breath first with " + breathSearch.getUniqueSetType() + " to keep track of visited edges and " + breathSearch.getQueueType() + " as an implementation of queue.", mockWriter.receivedAlDoc());
        assertEquals("Breath first", mockWriter.receivedAlgorithm());
        assertEquals("| V |", mockWriter.receivedAlSpace());
        assertEquals("O( | V + E | )", mockWriter.receivedAlTime());
        assertEquals("Width: 7 Height: 6\r\nStart location: 0,1\r\nTarget location: 4,3", mockWriter.receivedMapInfo());
        assertEquals("34", mockWriter.receivedTestMaxSteps());
        assertEquals("16", mockWriter.receivedTestPathWeight());
        assertEquals("27", mockWriter.receivedTestUsedSteps());

        String expectedProcessedMap =
                "X X X @ ! ! . \r\n" +
                        "S @ X X X ! ! \r\n" +
                        "! @ ! @ X ! . \r\n" +
                        "! @ ! @ F . . \r\n" +
                        "! ! ! @ ! . . \r\n" +
                        "! ! ! ! ! ! . ";
        assertEquals(expectedProcessedMap, mockWriter.receivedProcessedMap());
    }
}
