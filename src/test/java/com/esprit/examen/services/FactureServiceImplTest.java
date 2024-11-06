package com.esprit.examen.services;

import com.esprit.examen.entities.Facture;
import com.esprit.examen.repositories.FactureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FactureServiceImplTest {
    @Mock
    private FactureRepository factureRepository;

    @Mock
    private ReglementServiceImpl reglementService;

    @InjectMocks
    private FactureServiceImpl factureService;

    private Facture testFacture;

    @BeforeEach
    void setUp() {
        testFacture = new Facture();
        testFacture.setIdFacture(1L);
        testFacture.setMontantFacture(1000f);
        testFacture.setMontantRemise(50f);
        testFacture.setArchivee(false);
    }

    @Test
    void testRetrieveAllFactures() {
        // Arrange
        when(factureRepository.findAll()).thenReturn(Arrays.asList(testFacture));

        // Act
        List<Facture> factures = factureService.retrieveAllFactures();

        // Assert
        assertNotNull(factures);
        assertEquals(1, factures.size());
        assertEquals(testFacture, factures.get(0));
        verify(factureRepository, times(1)).findAll();
    }

    @Test
    void testAddFacture() {
        // Arrange
        when(factureRepository.save(any(Facture.class))).thenReturn(testFacture);

        // Act
        Facture savedFacture = factureService.addFacture(testFacture);

        // Assert
        assertNotNull(savedFacture);
        assertEquals(testFacture.getIdFacture(), savedFacture.getIdFacture());
        verify(factureRepository, times(1)).save(testFacture);
    }

    @Test
    void testCancelFacture() {
        // Arrange
        when(factureRepository.findById(1L)).thenReturn(java.util.Optional.of(testFacture));

        // Act
        factureService.cancelFacture(1L);

        // Assert
        assertTrue(testFacture.getArchivee());
        verify(factureRepository, times(1)).save(testFacture);
    }

    @Test
    void testRetrieveFacture() {
        // Arrange
        when(factureRepository.findById(1L)).thenReturn(java.util.Optional.of(testFacture));

        // Act
        Facture facture = factureService.retrieveFacture(1L);

        // Assert
        assertNotNull(facture);
        assertEquals(1L, facture.getIdFacture());
        verify(factureRepository, times(1)).findById(1L);
    }

    @Test
    void testPourcentageRecouvrement() {
        // Arrange
        Date startDate = new Date();
        Date endDate = new Date();
        when(factureRepository.getTotalFacturesEntreDeuxDates(startDate, endDate)).thenReturn(2000f);
        when(reglementService.getChiffreAffaireEntreDeuxDate(startDate, endDate)).thenReturn(1000f);

        // Act
        float pourcentage = factureService.pourcentageRecouvrement(startDate, endDate);

        // Assert
        assertEquals(50.0f, pourcentage);
        verify(factureRepository, times(1)).getTotalFacturesEntreDeuxDates(startDate, endDate);
        verify(reglementService, times(1)).getChiffreAffaireEntreDeuxDate(startDate, endDate);
    }

}
