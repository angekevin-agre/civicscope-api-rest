package fr.epsi.civicscope.service;

import fr.epsi.civicscope.model.Region;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RegionService {

    private final Map<String, Region> regions = new LinkedHashMap<>();

    public RegionService() {
        String[][] donnees = {
                {"01", "DAKAR"}, {"02", "ZIGUINCHOR"}, {"03", "DIOURBEL"}, {"04", "SAINTLOUIS"},
                {"05", "TAMBACOUNDA"}, {"06", "KAOLACK"}, {"07", "THIES"}, {"08", "LOUGA"},
                {"09", "FATICK"}, {"10", "KOLDA"}, {"11", "MATAM"}, {"12", "KAFFRINE"},
                {"13", "KEDOUGOU"}, {"14", "SEDHIOU"}
        };

        for (String[] ligne : donnees) {
            regions.put(ligne[0], new Region(ligne[0], ligne[1]));
        }
    }

    public List<Region> findAll() {
        return new ArrayList<>(regions.values());
    }

    public Optional<Region> findByCode(String code) {
        return Optional.ofNullable(regions.get(code));
    }

    public Region create(Region region) {
        if (region == null || estVide(region.getCode()) || estVide(region.getNom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (regions.containsKey(region.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        regions.put(region.getCode(), region);
        return region;
    }

    public Region update(String code, Region region) {
        if (!regions.containsKey(code)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (region == null || estVide(region.getNom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Region regionModifiee = new Region(code, region.getNom());
        regions.put(code, regionModifiee);
        return regionModifiee;
    }

    public void delete(String code) {
        if (!regions.containsKey(code)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        regions.remove(code);
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
