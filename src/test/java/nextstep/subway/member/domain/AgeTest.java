package nextstep.subway.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.subway.common.constant.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("나이 관련 도메인 테스트")
public class AgeTest {

    @ParameterizedTest(name = "나이 생성 시, 0보다 작거나 같으면 에러가 발생한다(age: {0})")
    @ValueSource(ints = {0, -1, -2})
    void createAgeThrowErrorWhenAgeLessThenOrEqualToZero(int age) {
        // when & then
        assertThatThrownBy(() -> Age.from(age))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.나이는_0보다_작거나_같을_수_없음.getErrorMessage());
    }

    @DisplayName("나이를 생성하면 조회할 수 있다.")
    @Test
    void createDistance() {
        // given
        int actual = 10;

        // when
        Age age = Age.from(actual);

        // then
        assertThat(age.value()).isEqualTo(actual);
    }
}
