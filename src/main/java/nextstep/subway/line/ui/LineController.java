package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> findAllLines() {
		return ResponseEntity.ok(lineService.findLines());
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
		return ResponseEntity.ok(lineService.findLineResponseById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineUpdateRequest) {
		return ResponseEntity.ok().body(lineService.updateLine(id, lineUpdateRequest));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{lineId}/sections")
	public ResponseEntity<LineResponse> addLineStation(@PathVariable Long lineId,
		@RequestBody @Valid SectionRequest request) {
		LineResponse line = lineService.addLineStation(lineId, request);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@DeleteMapping("/{lineId}/sections")
	public ResponseEntity<?> removeLineStation(@PathVariable Long lineId, @RequestParam Long stationId) {
		lineService.removeLineStation(lineId, stationId);
		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}
}
