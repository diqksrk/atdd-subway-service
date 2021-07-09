package nextstep.subway.favorite.dto;

import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.member.domain.Member;
import nextstep.subway.station.domain.Station;

public class FavoriteRequest {

	private Long source;
	private Long target;

	protected FavoriteRequest() {

	}

	public FavoriteRequest(Long source, Long target) {
		this.source = source;
		this.target = target;
	}

	public Long getSource() {
		return source;
	}

	public Long getTarget() {
		return target;
	}

	public static Favorite toFavorite(Member member, Station source, Station target) {
		return new Favorite(member, source, target);
	}
}
