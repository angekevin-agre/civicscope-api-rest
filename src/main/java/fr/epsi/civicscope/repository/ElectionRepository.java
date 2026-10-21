package fr.epsi.civicscope.repository;

import fr.epsi.civicscope.model.Election;
import fr.epsi.civicscope.model.TourElection;
import fr.epsi.civicscope.model.TypeElection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.UUID;

public interface ElectionRepository extends JpaRepository<Election, UUID>, JpaSpecificationExecutor<Election> {

    boolean existsByCode(String code);

    boolean existsByTypeElectionAndTourElectionAndDateBetween(TypeElection typeElection, TourElection tourElection,
                                                              LocalDate debut, LocalDate fin);

    boolean existsByTypeElectionAndTourElectionAndDateBetweenAndIdNot(TypeElection typeElection,
                                                                      TourElection tourElection,
                                                                      LocalDate debut, LocalDate fin, UUID id);
}
