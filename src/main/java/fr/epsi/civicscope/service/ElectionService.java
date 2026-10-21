package fr.epsi.civicscope.service;

import fr.epsi.civicscope.model.Election;
import fr.epsi.civicscope.model.TypeElection;
import fr.epsi.civicscope.repository.ElectionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    public List<Election> findAll(Integer annee, TypeElection type, Integer limit) {
        if (limit != null && limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Specification<Election> filtres = (root, query, cb) -> {
            List<Predicate> predicats = new ArrayList<>();
            if (annee != null) {
                predicats.add(cb.between(root.get("date"), LocalDate.of(annee, 1, 1), LocalDate.of(annee, 12, 31)));
            }
            if (type != null) {
                predicats.add(cb.equal(root.get("typeElection"), type));
            }
            return cb.and(predicats.toArray(new Predicate[0]));
        };
        Sort tri = Sort.by(Sort.Direction.DESC, "date");
        if (limit == null) {
            return electionRepository.findAll(filtres, tri);
        }
        return electionRepository.findAll(filtres, PageRequest.of(0, limit, tri)).getContent();
    }

    public Optional<Election> findById(UUID id) {
        return electionRepository.findById(id);
    }

    public Election create(Election election) {
        if (election == null || estVide(election.getCode()) || estIncomplete(election)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (electionRepository.existsByCode(election.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (electionRepository.existsByTypeElectionAndTourElectionAndDateBetween(election.getTypeElection(),
                election.getTourElection(), debutAnnee(election), finAnnee(election))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        election.setId(null);
        return electionRepository.save(election);
    }

    public Election update(UUID id, Election election) {
        if (!electionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (election == null || estVide(election.getCode()) || estIncomplete(election)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (electionRepository.existsByTypeElectionAndTourElectionAndDateBetweenAndIdNot(election.getTypeElection(),
                election.getTourElection(), debutAnnee(election), finAnnee(election), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        election.setId(id);
        return electionRepository.save(election);
    }

    public void delete(UUID id) {
        if (!electionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        electionRepository.deleteById(id);
    }

    private LocalDate debutAnnee(Election election) {
        return LocalDate.of(election.getDate().getYear(), 1, 1);
    }

    private LocalDate finAnnee(Election election) {
        return LocalDate.of(election.getDate().getYear(), 12, 31);
    }

    private boolean estIncomplete(Election election) {
        return election.getDate() == null || election.getTypeElection() == null;
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
