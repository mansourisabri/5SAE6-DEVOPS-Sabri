package com.esprit.examen.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.esprit.examen.entities.CategorieProduit;
import com.esprit.examen.repositories.CategorieProduitRepository;

public class CategorieProduitServiceImplTest {

    @Mock
    CategorieProduitRepository categorieProduitRepository;

    @InjectMocks
    CategorieProduitServiceImpl categorieProduitService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // Initialisation des mocks
    }

    @Test
     void testRetrieveAllCategorieProduits() {
        // Setup des données mockées
        CategorieProduit cp1 = new CategorieProduit(1L, "CODE1", "Catégorie 1", null);
        CategorieProduit cp2 = new CategorieProduit(2L, "CODE2", "Catégorie 2", null);
        when(categorieProduitRepository.findAll()).thenReturn(Arrays.asList(cp1, cp2));

        // Appel de la méthode
        List<CategorieProduit> result = categorieProduitService.retrieveAllCategorieProduits();

        // Vérifications
        assertEquals(2, result.size());
        verify(categorieProduitRepository, times(1)).findAll();
    }

    @Test
     void testAddCategorieProduit() {
        // Setup des données mockées
        CategorieProduit cp = new CategorieProduit(1L, "CODE1", "Catégorie 1", null);
        when(categorieProduitRepository.save(any(CategorieProduit.class))).thenReturn(cp);

        // Appel de la méthode
        CategorieProduit result = categorieProduitService.addCategorieProduit(cp);

        // Vérifications
        assertNotNull(result);
        assertEquals("Catégorie 1", result.getLibelleCategorie());
        verify(categorieProduitRepository, times(1)).save(cp);
    }

    @Test
     void testDeleteCategorieProduit() {
        // Appel de la méthode
        categorieProduitService.deleteCategorieProduit(1L);

        // Vérifications
        verify(categorieProduitRepository, times(1)).deleteById(1L);
    }

    @Test
     void testUpdateCategorieProduit() {
        // Setup des données mockées
        CategorieProduit cp = new CategorieProduit(1L, "CODE1", "Catégorie Mise à jour", null);
        when(categorieProduitRepository.save(any(CategorieProduit.class))).thenReturn(cp);

        // Appel de la méthode
        CategorieProduit result = categorieProduitService.updateCategorieProduit(cp);

        // Vérifications
        assertNotNull(result);
        assertEquals("Catégorie Mise à jour", result.getLibelleCategorie());
        verify(categorieProduitRepository, times(1)).save(cp);
    }

    @Test
     void testRetrieveCategorieProduit() {
        // Setup des données mockées
        CategorieProduit cp = new CategorieProduit(1L, "CODE1", "Catégorie 1", null);
        when(categorieProduitRepository.findById(1L)).thenReturn(Optional.of(cp));

        // Appel de la méthode
        CategorieProduit result = categorieProduitService.retrieveCategorieProduit(1L);

        // Vérifications
        assertNotNull(result);
        assertEquals("Catégorie 1", result.getLibelleCategorie());
        verify(categorieProduitRepository, times(1)).findById(1L);
    }

    @Test
     void testRetrieveCategorieProduitNotFound() {
        // Setup pour renvoyer un résultat vide
        when(categorieProduitRepository.findById(1L)).thenReturn(Optional.empty());

        // Appel de la méthode
        CategorieProduit result = categorieProduitService.retrieveCategorieProduit(1L);

        // Vérifications
        assertNull(result);
        verify(categorieProduitRepository, times(1)).findById(1L);
    }
}
