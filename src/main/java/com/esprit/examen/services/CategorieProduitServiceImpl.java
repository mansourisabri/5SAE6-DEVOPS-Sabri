package com.esprit.examen.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.esprit.examen.entities.CategorieProduit;
import com.esprit.examen.repositories.CategorieProduitRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Service
public class CategorieProduitServiceImpl implements ICategorieProduitService {

	private static final Logger logger = LogManager.getLogger(CategorieProduitServiceImpl.class);

	@Autowired
	CategorieProduitRepository categorieProduitRepository;
	@Override
	public List<CategorieProduit> retrieveAllCategorieProduits() {

		logger.info("Récupération de toutes les catégories de produits");
		List<CategorieProduit> categories = categorieProduitRepository.findAll();
		logger.debug("Nombre de catégories de produits récupérées : " + categories.size());
		return categories;	}

	@Override
	public CategorieProduit addCategorieProduit(CategorieProduit cp) {
		logger.info("Ajout de la catégorie de produit : " + cp.getLibelleCategorie());
		categorieProduitRepository.save(cp);
		logger.debug("Catégorie de produit ajoutée avec ID : " + cp.getIdCategorieProduit());
		return cp;
	}

	@Override
	public void deleteCategorieProduit(Long id) {
		logger.warn("Suppression de la catégorie de produit avec ID : " + id);
		categorieProduitRepository.deleteById(id);
		logger.debug("Catégorie de produit supprimée");
		
	}

	@Override
	public CategorieProduit updateCategorieProduit(CategorieProduit cp) {
		logger.info("Mise à jour de la catégorie de produit avec ID : " + cp.getIdCategorieProduit());
		categorieProduitRepository.save(cp);
		logger.debug("Catégorie de produit mise à jour : " + cp.toString());
		return cp;
	}

	@Override
	public CategorieProduit retrieveCategorieProduit(Long id) {
		logger.info("Récupération de la catégorie de produit avec ID : " + id);
		CategorieProduit categorieProduit = categorieProduitRepository.findById(id).orElse(null);
		if (categorieProduit != null) {
			logger.debug("Catégorie de produit trouvée : " + categorieProduit.getLibelleCategorie());
		} else {
			logger.error("Catégorie de produit non trouvée pour l'ID : " + id);
		}
		return categorieProduit;
	}

}
