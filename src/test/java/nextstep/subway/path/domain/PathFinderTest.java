package nextstep.subway.path.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathFinderTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 정자역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private PathFinder pathFinder;

    /**
     * Feature: 지하철 경로 검색
     * Scenario: 두 역의 최단 거리 경로를 조회
     *   Given 지하철역이 등록되어있음
     *   And 지하철 노선이 등록되어있음
     *   And 지하철 노선에 지하철역이 등록되어있음
     *
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재역
     *                          |
     *                          정자역
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        정자역 = new Station("정자역");
        남부터미널역 = new Station("남부터미널역");

        신분당선 = new Line("신분당선", "bg-red-600");
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 정자역, 50);

        이호선 = new Line("2호선", "bg-green-600");
        이호선.addSection(교대역, 강남역, 10);

        삼호선 = new Line("3호선", "bg-orange-600");
        삼호선.addSection(교대역, 남부터미널역, 5);
        삼호선.addSection(남부터미널역, 양재역, 3);

        pathFinder = new PathFinder(new DijkstraShortestPathFinder());
    }

    @Test
    @DisplayName("최단 경로 조회")
    void findShortestPath() {
        // when
        Path path = pathFinder.findShortestPath(Arrays.asList(이호선, 삼호선, 신분당선), 교대역, 양재역);

        // then
        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getDistance()).isEqualTo(8);
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 경우")
    void findPathSameSourceAndTarget() {
        // when then
        assertThatThrownBy(()  -> pathFinder.findShortestPath(Arrays.asList(이호선, 삼호선, 신분당선), 교대역, 교대역))
            .isInstanceOf(PathException.class);
    }

    @Test
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    public void notConnectSourceAndTarget() {
        // given
        Station 서울역 = new Station("서울역");
        Station 삼각지역 = new Station("삼각지역");
        Line 사호선 = new Line("신분당선", "bg-blue-600");
        사호선.addSection(서울역, 삼각지역, 5);

        // when then
        assertThatThrownBy(()  -> pathFinder.findShortestPath(Arrays.asList(이호선, 삼호선, 신분당선, 사호선), 교대역, 삼각지역))
            .isInstanceOf(PathException.class);
    }

    @Test
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    public void findPathNoExistStation() {
        // given
        Station 존재하지않는역 = new Station("존재하지않는역");

        // when then
        assertThatThrownBy(()  -> pathFinder.findShortestPath(Arrays.asList(이호선, 삼호선, 신분당선), 교대역, 존재하지않는역))
            .isInstanceOf(PathException.class);
    }

    @Test
    @DisplayName("거리에 따른 운임비용: 기본운임 1,250원, 10km초과∼50km까지(5km마다 100원), 50km초과 시 (8km마다 100원)")
    public void getFareByDistanceBased() {
        // when
        Path basePath = pathFinder.findShortestPath(Arrays.asList(이호선, 삼호선, 신분당선), 강남역, 양재역);
        Path excess10KmPath = pathFinder.findShortestPath(Arrays.asList(이호선, 삼호선, 신분당선), 강남역, 남부터미널역);
        Path excess50KmPath = pathFinder.findShortestPath(Arrays.asList(이호선, 삼호선, 신분당선), 강남역, 정자역);

        // then
        assertThat(basePath.getFare()).isEqualTo(1_250);
        assertThat(excess10KmPath.getFare()).isEqualTo(1_350);
        assertThat(excess50KmPath.getFare()).isEqualTo(2_250);
    }

    /**
     * 동대문역 --- *1호선(300원)* --- 동묘앞역 --- *6호선(900원)* --- 창신역
     */
    @Test
    @DisplayName("노선 추가 요금: 추가 요금이 가장 비싼 노선 비용")
    void getFareByLineBased() {
        // given
        Station 동대문역 = new Station("동대문역");
        Station 동묘앞역 = new Station("동묘앞역");
        Station 창신역 = new Station("창신역");

        Line 일호선 = new Line("1호선", "bg-blue-600", 300);
        Line 육호선 = new Line("6호선", "bg-brown-600", 900);

        일호선.addSection(동대문역, 동묘앞역, 2);
        육호선.addSection(동묘앞역, 창신역, 2);

        // when
        Path excess10KmPath = pathFinder.findShortestPath(Arrays.asList(일호선, 육호선), 동대문역, 창신역);

        // then
        assertThat(excess10KmPath.getFare()).isEqualTo(2_150);
    }
}
