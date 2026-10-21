package fr.epsi.civicscope.controller;

import fr.epsi.civicscope.model.Election;
import fr.epsi.civicscope.model.TypeElection;
import fr.epsi.civicscope.service.ElectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/elections")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public List<Election> findAll(@RequestParam(required = false) Integer annee,
                                  @RequestParam(required = false) TypeElection type,
                                  @RequestParam(required = false) Integer limit) {
        return electionService.findAll(annee, type, limit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> findById(@PathVariable UUID id) {
        return electionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Election> create(@RequestBody Election election) {
        return ResponseEntity.status(HttpStatus.CREATED).body(electionService.create(election));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Election> update(@PathVariable UUID id, @RequestBody Election election) {
        return ResponseEntity.ok(electionService.update(id, election));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        electionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
