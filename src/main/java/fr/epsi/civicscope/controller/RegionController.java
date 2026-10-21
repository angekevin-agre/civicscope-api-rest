package fr.epsi.civicscope.controller;

import fr.epsi.civicscope.model.Region;
import fr.epsi.civicscope.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public List<Region> findAll() {
        return regionService.findAll();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Region> findByCode(@PathVariable String code) {
        return regionService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Region> create(@RequestBody Region region) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regionService.create(region));
    }

    @PutMapping("/{code}")
    public ResponseEntity<Region> update(@PathVariable String code, @RequestBody Region region) {
        return ResponseEntity.ok(regionService.update(code, region));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        regionService.delete(code);
        return ResponseEntity.noContent().build();
    }
}
