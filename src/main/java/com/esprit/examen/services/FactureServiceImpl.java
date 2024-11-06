package com.esprit.examen.services;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.esprit.examen.entities.DetailFacture;
import com.esprit.examen.entities.Facture;
import com.esprit.examen.entities.Fournisseur;
import com.esprit.examen.entities.Operateur;
import com.esprit.examen.entities.Produit;
import com.esprit.examen.repositories.DetailFactureRepository;
import com.esprit.examen.repositories.FactureRepository;
import com.esprit.examen.repositories.FournisseurRepository;
import com.esprit.examen.repositories.OperateurRepository;
import com.esprit.examen.repositories.ProduitRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@Slf4j
@Transactional
public class FactureServiceImpl implements IFactureService {

	private static final Logger logger = LogManager.getLogger(FactureServiceImpl.class);


	@Autowired
	FactureRepository factureRepository;
	@Autowired
	OperateurRepository operateurRepository;
	@Autowired
	DetailFactureRepository detailFactureRepository;
	@Autowired
	FournisseurRepository fournisseurRepository;
	@Autowired
	ProduitRepository produitRepository;
    @Autowired
    ReglementServiceImpl reglementService;
	
	@Override
	public List<Facture> retrieveAllFactures() {
		logger.info("Starting retrieveAllFactures method.");
		List<Facture> factures = (List<Facture>) factureRepository.findAll();
		logger.info("Total factures retrieved: {}", factures.size());
		for (Facture facture : factures) {
			logger.debug("Retrieved facture: {}", facture);
		}
		return factures;
	}

	
	public Facture addFacture(Facture f) {

		logger.info("Starting addFacture method with input: {}", f);
		Facture savedFacture = factureRepository.save(f);
		logger.info("Facture successfully added with ID: {}", savedFacture.getIdFacture());
		return savedFacture;
	}

	/*
	 * calculer les montants remise et le montant total d'un détail facture
	 * ainsi que les montants d'une facture
	 */
	private Facture addDetailsFacture(Facture f, Set<DetailFacture> detailsFacture) {
		logger.info("Starting addDetailsFacture method with Facture ID: {}", f.getIdFacture());
		float montantFacture = 0;
		float montantRemise = 0;
		for (DetailFacture detail : detailsFacture) {
			logger.debug("Processing detail with Product ID: {}", detail.getProduit().getIdProduit());
			//Récuperer le produit 
			Produit produit = produitRepository.findById(detail.getProduit().getIdProduit()).get();
			//Calculer le montant total pour chaque détail Facture
			float prixTotalDetail = detail.getQteCommandee() * produit.getPrix();
			//Calculer le montant remise pour chaque détail Facture
			float montantRemiseDetail = (prixTotalDetail * detail.getPourcentageRemise()) / 100;
			float prixTotalDetailRemise = prixTotalDetail - montantRemiseDetail;
			detail.setMontantRemise(montantRemiseDetail);
			detail.setPrixTotalDetail(prixTotalDetailRemise);
			//Calculer le montant total pour la facture
			montantFacture = montantFacture + prixTotalDetailRemise;
			//Calculer le montant remise pour la facture
			montantRemise = montantRemise + montantRemiseDetail;
			detailFactureRepository.save(detail);
			logger.debug("Detail processed and saved with final price after discount: {}", prixTotalDetailRemise);

		}
		f.setMontantFacture(montantFacture);
		f.setMontantRemise(montantRemise);
		logger.info("Finalized facture details with total amount: {} and total discount: {}", montantFacture, montantRemise);
		return f;
	}

	@Override
	public void cancelFacture(Long factureId) {
		logger.info("Starting cancelFacture method for Facture ID: {}", factureId);
		// Méthode 01
		//Facture facture = factureRepository.findById(factureId).get();
		Facture facture = factureRepository.findById(factureId).orElse(new Facture());
		facture.setArchivee(true);
		factureRepository.save(facture);
		//Méthode 02 (Avec JPQL)
		factureRepository.updateFacture(factureId);
		logger.warn("Canceled facture with ID: {}", factureId);
	}

	@Override
	public Facture retrieveFacture(Long factureId) {
		logger.info("Starting retrieveFacture method for Facture ID: {}", factureId);
		Facture facture = factureRepository.findById(factureId).orElse(null);
		if (facture != null) {
			logger.info("Retrieved facture: {}", facture);
		} else {
			logger.warn("Facture with ID: {} not found.", factureId);
		}
		return facture;
	}

	@Override
	public List<Facture> getFacturesByFournisseur(Long idFournisseur) {
		logger.info("Starting getFacturesByFournisseur method for Fournisseur ID: {}", idFournisseur);
		Fournisseur fournisseur = fournisseurRepository.findById(idFournisseur).orElse(null);
		if (fournisseur != null) {
			logger.info("Retrieved factures for Fournisseur ID {}: {}", idFournisseur, fournisseur.getFactures());
			return (List<Facture>) fournisseur.getFactures();
		} else {
			logger.warn("Fournisseur with ID {} not found.", idFournisseur);
			return null;
		}	}

	@Override
	public void assignOperateurToFacture(Long idOperateur, Long idFacture) {
		logger.info("Starting assignOperateurToFacture method for Operateur ID {} and Facture ID {}", idOperateur, idFacture);
		Facture facture = factureRepository.findById(idFacture).orElse(null);
		Operateur operateur = operateurRepository.findById(idOperateur).orElse(null);
		if (facture != null && operateur != null) {
			operateur.getFactures().add(facture);
			operateurRepository.save(operateur);
			logger.info("Successfully assigned Operateur ID {} to Facture ID {}", idOperateur, idFacture);
		} else {
			logger.warn("Could not assign Operateur ID {} to Facture ID {}. One or both were not found.", idOperateur, idFacture);
		}	}

	@Override
	public float pourcentageRecouvrement(Date startDate, Date endDate) {
		logger.info("Starting pourcentageRecouvrement method for dates {} - {}", startDate, endDate);
		float totalFacturesEntreDeuxDates = factureRepository.getTotalFacturesEntreDeuxDates(startDate,endDate);
		float totalRecouvrementEntreDeuxDates =reglementService.getChiffreAffaireEntreDeuxDate(startDate,endDate);
		float pourcentage=(totalRecouvrementEntreDeuxDates/totalFacturesEntreDeuxDates)*100;
		logger.info("Calculated recouvrement percentage: {}%", pourcentage);
		return pourcentage;
	}
	

}