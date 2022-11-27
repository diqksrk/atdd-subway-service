package nextstep.subway.favorite.acceptance;

import static nextstep.subway.auth.acceptance.AuthRestAssured.로그인_되어_있음;
import static nextstep.subway.favorite.acceptance.FavoriteRestAssured.즐겨찾기_등록_요청;
import static nextstep.subway.favorite.acceptance.FavoriteRestAssured.즐겨찾기_목록_조회_요청;
import static nextstep.subway.favorite.acceptance.FavoriteRestAssured.즐겨찾기_삭제_요청;
import static nextstep.subway.line.acceptance.LineRestAssured.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.LineSectionRestAssured.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.member.acceptance.MemberRestAssured.회원_생성되어_있음;
import static nextstep.subway.station.acceptance.StationRestAssured.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collection;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;

@DisplayName("즐겨찾기 관련 기능")
public class FavoriteAcceptanceTest extends AcceptanceTest {

    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final int AGE = 20;

    private LineResponse 이호선;
    private LineResponse 구호선;
    private StationResponse 종합운동장역;
    private StationResponse 삼성역;
    private StationResponse 선릉역;
    private StationResponse 강남역;
    private StationResponse 신논현역;
    private StationResponse 선정릉역;
    private String accessToken;

    @BeforeEach
    void setup() {
        종합운동장역 = 지하철역_등록되어_있음("종합운동장역").as(StationResponse.class);
        삼성역 = 지하철역_등록되어_있음("삼성역").as(StationResponse.class);
        선릉역 = 지하철역_등록되어_있음("선릉역").as(StationResponse.class);
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        신논현역 = 지하철역_등록되어_있음("신논현역").as(StationResponse.class);
        선정릉역 = 지하철역_등록되어_있음("선정릉역").as(StationResponse.class);
        LineRequest 이호선_요청 = new LineRequest("이호선", "bg-green-600", 강남역.getId(), 선릉역.getId(), 6);
        이호선 = 지하철_노선_등록되어_있음(이호선_요청).as(LineResponse.class);
        LineRequest 구호선_요청 = new LineRequest("구호선", "bg-gold-600", 신논현역.getId(), 선정릉역.getId(), 12);
        구호선 = 지하철_노선_등록되어_있음(구호선_요청).as(LineResponse.class);
        지하철_노선에_지하철역_등록되어_있음(이호선, 선릉역, 삼성역, 4);
        지하철_노선에_지하철역_등록되어_있음(이호선, 삼성역, 종합운동장역, 4);
        지하철_노선에_지하철역_등록되어_있음(구호선, 선정릉역, 종합운동장역, 15);
        회원_생성되어_있음(new MemberRequest(EMAIL, PASSWORD, AGE));
        ExtractableResponse<Response> loginResponse = 로그인_되어_있음(new TokenRequest(EMAIL, PASSWORD));
        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    /**
     * Feature: 즐겨찾기를 관리한다.
     *
     *   Background (@BeforeEach)
     *     Given 지하철역 등록되어 있음
     *     And 지하철 노선 등록되어 있음
     *     And 지하철 노선에 지하철역 등록되어 있음
     *     And 회원 등록되어 있음
     *     And 로그인 되어있음
     *
     *   Scenario: 즐겨찾기를 관리
     *     When 정상회원이 즐겨찾기 생성을 요청
     *     Then 즐겨찾기 생성됨
     *     When 정상회원이 즐겨찾기 목록 조회 요청
     *     Then 즐겨찾기 목록 조회됨
     *     When 정상회원이 즐겨찾기 삭제 요청
     *     Then 즐겨찾기 삭제됨
     *     When 유효하지 않은 토큰을 가진 비정상회원이 즐겨찾기 생성을 요청
     *     Then 즐겨찾기 생성 실패됨
     *     When 유효하지 않은 토큰을 가진 비정상회원이 즐겨찾기 목록 조회 요청
     *     Then 즐겨찾기 목록 조회 실패됨
     *     When 유효하지 않은 토큰을 가진 비정상회원이 즐겨찾기 삭제 요청
     *     Then 즐겨찾기 삭제 실패됨
     */
    @DisplayName("즐겨찾기 관리")
    @TestFactory
    Collection<DynamicTest> manageFavoriteSenario() {
        String wrongAccessToken = "wrongAccessToken";

        return Arrays.asList(
                DynamicTest.dynamicTest("정상회원은 즐겨찾기를 생성할 수 있다.", () -> {
                    // when
                    ExtractableResponse<Response> response1 = 즐겨찾기_등록_요청(accessToken,
                            new FavoriteRequest(종합운동장역.getId().toString(), 선릉역.getId().toString()));
                    ExtractableResponse<Response> response2 = 즐겨찾기_등록_요청(accessToken,
                            new FavoriteRequest(강남역.getId().toString(), 선정릉역.getId().toString()));

                    // then
                    즐겨찾기_등록됨(response1);
                    즐겨찾기_등록됨(response2);
                }),
                DynamicTest.dynamicTest("정상회원은 즐겨찾기 목록을 조회 할 수 있다.", () -> {
                    // when
                    ExtractableResponse<Response> response = 즐겨찾기_목록_조회_요청(accessToken);

                    // then
                    즐겨찾기_목록_조회됨(response);
                }),
                DynamicTest.dynamicTest("정상회원은 즐겨찾기를 삭제할 수 있다.", () -> {
                    // when
                    ExtractableResponse<Response> response = 즐겨찾기_삭제_요청(accessToken, 1L);

                    // then
                    즐겨찾기_삭제됨(response);
                }),
                DynamicTest.dynamicTest("유효하지 않은 토큰을 가진 비정상회원은 즐겨찾기를 생성할 수 없다.", () -> {
                    // when
                    ExtractableResponse<Response> response = 즐겨찾기_등록_요청(wrongAccessToken,
                            new FavoriteRequest(종합운동장역.getId().toString(), 선릉역.getId().toString()));

                    // then
                    즐겨찾기_등록_안됨(response);
                }),
                DynamicTest.dynamicTest("유효하지 않은 토큰을 가진 비정상회원은 즐겨찾기 목록을 조회 할 수 없다.", () -> {
                    // when
                    ExtractableResponse<Response> response = 즐겨찾기_목록_조회_요청(wrongAccessToken);

                    // then
                    즐겨찾기_목록_조회_안됨(response);
                }),
                DynamicTest.dynamicTest("유효하지 않은 토큰을 가진 비정상회원은 즐겨찾기를 삭제할 수 없다.", () -> {
                    // when
                    ExtractableResponse<Response> response = 즐겨찾기_삭제_요청(wrongAccessToken, 1L);

                    // then
                    즐겨찾기_삭제_안됨(response);
                })
        );
    }

    private static void 즐겨찾기_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private static void 즐겨찾기_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static void 즐겨찾기_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private static void 즐겨찾기_등록_안됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static void 즐겨찾기_목록_조회_안됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static void 즐겨찾기_삭제_안됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
