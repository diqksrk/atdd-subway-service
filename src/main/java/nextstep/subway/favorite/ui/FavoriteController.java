package nextstep.subway.favorite.ui;

import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.favorite.application.FavoriteService;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Transactional(readOnly = true)
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<FavoriteResponse> createFavorite(@AuthenticationPrincipal LoginMember loginMember, @RequestBody FavoriteRequest favoriteRequest) {
        FavoriteResponse response = favoriteService.saveFavorite(loginMember.getId(), favoriteRequest);
        return ResponseEntity.created(URI.create("/favorites/" + response.getId())).build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> findAllFavorites(@AuthenticationPrincipal LoginMember loginMember) {
        return ResponseEntity.ok(favoriteService.findFavorites(loginMember.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal LoginMember loginMember, @PathVariable Long id) {
        favoriteService.deleteFavorite(loginMember.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
