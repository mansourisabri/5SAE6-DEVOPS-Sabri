FROM sabri321/projet-devops-5sae6:latest

# Définit le port exposé par le conteneur
EXPOSE 8089

# Copie le fichier JAR généré par Maven vers le conteneur
# Assure-toi que le chemin cible soit correct, ici par exemple "tpAchatProject-1.0.0.jar"
ADD target/tpAchatProject-1.0.0.jar tpAchatProject-1.0.0.jar

# Commande d'exécution pour lancer l'application Spring Boot
ENTRYPOINT ["java", "-jar", "tpAchatProject-1.0.0.jar"]
