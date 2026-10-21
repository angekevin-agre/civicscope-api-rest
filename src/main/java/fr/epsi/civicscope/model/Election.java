package fr.epsi.civicscope.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "election")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String code;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private TypeElection typeElection;
    @Enumerated(EnumType.STRING)
    private TourElection tourElection;
    private String codePays;

    public Election() {
    }

    public Election(String code, LocalDate date, TypeElection typeElection, TourElection tourElection, String codePays) {
        this.code = code;
        this.date = date;
        this.typeElection = typeElection;
        this.tourElection = tourElection;
        this.codePays = codePays;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TypeElection getTypeElection() {
        return typeElection;
    }

    public void setTypeElection(TypeElection typeElection) {
        this.typeElection = typeElection;
    }

    public TourElection getTourElection() {
        return tourElection;
    }

    public void setTourElection(TourElection tourElection) {
        this.tourElection = tourElection;
    }

    public String getCodePays() {
        return codePays;
    }

    public void setCodePays(String codePays) {
        this.codePays = codePays;
    }
}
